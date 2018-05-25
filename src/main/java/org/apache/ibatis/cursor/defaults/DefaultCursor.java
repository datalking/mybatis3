/**
 * Copyright ${license.git.copyrightYears} the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.cursor.defaults;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cursor默认实现类
 * This is the default implementation of a MyBatis Cursor.
 * This implementation is not thread safe.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public class DefaultCursor<T> implements Cursor<T> {

    private final DefaultResultSetHandler resultSetHandler;
    private final ResultMap resultMap;
    private final ResultSetWrapper rsw;

    // 指定对结果集进行映射的起止位置
    private final RowBounds rowBounds;
    // 用于暂存映射的结果对象
    private final ObjectWrapperResultHandler<T> objectWrapperResultHandler = new ObjectWrapperResultHandler<>();

    // 通过该迭代器获取映射得到的结果对象
    private final CursorIterator cursorIterator = new CursorIterator();

    // 标识是否正在迭代结果集
    private boolean iteratorRetrieved;
    // 记录已经完成映射的行数
    private int indexWithRowBound = -1;

    private CursorStatus status = CursorStatus.CREATED;

    private enum CursorStatus {

        /**
         * A freshly created cursor, database ResultSet consuming has not started
         */
        CREATED,
        /**
         * A cursor currently in use, database ResultSet consuming has started
         */
        OPEN,
        /**
         * A closed cursor, not fully consumed
         */
        CLOSED,
        /**
         * A fully consumed cursor, a consumed cursor is always closed
         */
        CONSUMED
    }

    public DefaultCursor(DefaultResultSetHandler resultSetHandler, ResultMap resultMap, ResultSetWrapper rsw, RowBounds rowBounds) {
        this.resultSetHandler = resultSetHandler;
        this.resultMap = resultMap;
        this.rsw = rsw;
        this.rowBounds = rowBounds;
    }

    @Override
    public boolean isOpen() {
        return status == CursorStatus.OPEN;
    }

    @Override
    public boolean isConsumed() {
        return status == CursorStatus.CONSUMED;
    }

    @Override
    public int getCurrentIndex() {
        return rowBounds.getOffset() + cursorIterator.iteratorIndex;
    }

    @Override
    public Iterator<T> iterator() {
        if (iteratorRetrieved) {
            throw new IllegalStateException("Cannot open more than one iterator on a Cursor");
        }
        iteratorRetrieved = true;
        return cursorIterator;
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        ResultSet rs = rsw.getResultSet();
        try {
            if (rs != null) {
                Statement statement = rs.getStatement();

                rs.close();
                if (statement != null) {
                    statement.close();
                }
            }
            status = CursorStatus.CLOSED;
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * 将记录行映射成结果对象
     */
    protected T fetchNextUsingRowBound() {

        // 映射一行数据库记录，得到结果对象
        T result = fetchNextObjectFromDatabase();

        // 从结果集开始一条条记录映射，但是将RowBounds.offset之前的映射结果全部忽略
        while (result != null && indexWithRowBound < rowBounds.getOffset()) {
            result = fetchNextObjectFromDatabase();
        }

        return result;
    }

    protected T fetchNextObjectFromDatabase() {
        if (isClosed()) {
            return null;
        }
        try {
            // 更新游标状态
            status = CursorStatus.OPEN;
            // 将映射得到的结果对象保存到 ObjectWrapperResultHandler.result 字段中
            resultSetHandler.handleRowValues(rsw, resultMap, objectWrapperResultHandler, RowBounds.DEFAULT, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 获取结果对象
        T next = objectWrapperResultHandler.result;
        if (next != null) {
            // 统计返回的结果对象数量
            indexWithRowBound++;
        }

        // No more object or limit reached
        // 检测是否还存在需映射的记录，如果没有，则关闭游标并修改状态
        if (next == null || (getReadItemsCount() == rowBounds.getOffset() + rowBounds.getLimit())) {
            // 关闭结果集以及对应的Statement对象
            close();
            status = CursorStatus.CONSUMED;
        }
        objectWrapperResultHandler.result = null;

        return next;
    }

    private boolean isClosed() {
        return status == CursorStatus.CLOSED || status == CursorStatus.CONSUMED;
    }

    private int getReadItemsCount() {
        return indexWithRowBound + 1;
    }

    private static class ObjectWrapperResultHandler<T> implements ResultHandler<T> {

        private T result;

        @Override
        public void handleResult(ResultContext<? extends T> context) {
            this.result = context.getResultObject();
            context.stop();
        }
    }

    private class CursorIterator implements Iterator<T> {

        /**
         * Holder for the next object to be returned
         */
        T object;

        /**
         * Index of objects returned using next(), and as such, visible to users.
         */
        int iteratorIndex = -1;

        @Override
        public boolean hasNext() {
            if (object == null) {
                object = fetchNextUsingRowBound();
            }
            return object != null;
        }

        @Override
        public T next() {
            // Fill next with object fetched from hasNext()
            T next = object;

            if (next == null) {
                next = fetchNextUsingRowBound();
            }

            if (next != null) {
                object = null;
                iteratorIndex++;
                return next;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove element from Cursor");
        }
    }
}
