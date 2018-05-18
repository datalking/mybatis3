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
package org.apache.ibatis.session;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;

/**
 * 数据库会话 接口
 * 定义了常用的数据库操作以及事务的相关操作
 * <p>
 * The primary Java interface for working with MyBatis.
 * Through this interface you can execute commands, get mappers and manage transactions.
 *
 * @author Clinton Begin
 */
public interface SqlSession extends Closeable {

    /**
     * 输入sql语句，返回查询结果对象
     * Retrieve a single row mapped from the statement key
     *
     * @param <T>       the returned object type
     * @param statement
     * @return Mapped object
     */
    <T> T selectOne(String statement);

    /**
     * Retrieve a single row mapped from the statement key and parameter.
     *
     * @param <T>       the returned object type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement. sql语句需要绑定的实参来源
     * @return Mapped object
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * Retrieve a list of mapped objects from the statement key and parameter.
     *
     * @param <E>       the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @return List of mapped object 查询结果会封装成list
     */
    <E> List<E> selectList(String statement);

    /**
     * Retrieve a list of mapped objects from the statement key and parameter.
     *
     * @param <E>       the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement, Object parameter);

    /**
     * Retrieve a list of mapped objects from the statement key and parameter,
     * within the specified row bounds.
     *
     * @param <E>       the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param rowBounds Bounds to limit object retrieval 限制解析结果集的范围
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

    /**
     * The selectMap is a special case in that it is designed to convert a list
     * of results into a Map based on one of the properties in the resulting
     * objects.
     * Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
     *
     * @param <K>       the returned Map keys type
     * @param <V>       the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param mapKey    The property to use as key for each value in the list.
     * @return Map containing key pair data. 查询结果会封装成map
     */
    <K, V> Map<K, V> selectMap(String statement, String mapKey);

    /**
     * The selectMap is a special case in that it is designed to convert a list
     * of results into a Map based on one of the properties in the resulting
     * objects.
     *
     * @param <K>       the returned Map keys type
     * @param <V>       the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param mapKey    The property to use as key for each value in the list.
     * @return Map containing key pair data.
     */
    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

    /**
     * The selectMap is a special case in that it is designed to convert a list
     * of results into a Map based on one of the properties in the resulting
     * objects.
     *
     * @param <K>       the returned Map keys type
     * @param <V>       the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param mapKey    The property to use as key for each value in the list.
     * @param rowBounds Bounds to limit object retrieval
     * @return Map containing key pair data.
     */
    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);

    /**
     * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
     *
     * @param <T>       the returned cursor element type.
     * @param statement Unique identifier matching the statement to use.
     * @return Cursor of mapped objects  查询结果返回游标对象
     */
    <T> Cursor<T> selectCursor(String statement);

    /**
     * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
     *
     * @param <T>       the returned cursor element type.
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return Cursor of mapped objects
     */
    <T> Cursor<T> selectCursor(String statement, Object parameter);

    /**
     * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
     *
     * @param <T>       the returned cursor element type.
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param rowBounds Bounds to limit object retrieval
     * @return Cursor of mapped objects
     */
    <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);

    /**
     * Retrieve a single row mapped from the statement key and parameter
     * using a {@code ResultHandler}.
     *
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param handler   ResultHandler that will handle each retrieved row 指定查询结果处理器handler
     */
    void select(String statement, Object parameter, ResultHandler handler);

    /**
     * Retrieve a single row mapped from the statement
     * using a {@code ResultHandler}.
     *
     * @param statement Unique identifier matching the statement to use.
     * @param handler   ResultHandler that will handle each retrieved row
     */
    void select(String statement, ResultHandler handler);

    /**
     * Retrieve a single row mapped from the statement key and parameter
     * using a {@code ResultHandler} and {@code RowBounds}
     *
     * @param statement Unique identifier matching the statement to use.
     * @param rowBounds RowBound instance to limit the query results
     * @param handler   ResultHandler that will handle each retrieved row
     */
    void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler);

    /**
     * 执行insert语句
     * Execute an insert statement.
     *
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the insert.
     */
    int insert(String statement);

    /**
     * Execute an insert statement with the given parameter object. Any generated
     * autoincrement values or selectKey entries will modify the given parameter
     * object properties. Only the number of rows affected will be returned.
     *
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the insert.
     */
    int insert(String statement, Object parameter);

    /**
     * 执行update语句
     * Execute an update statement. The number of rows affected will be returned.
     *
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the update.
     */
    int update(String statement);

    /**
     * Execute an update statement. The number of rows affected will be returned.
     *
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the update.
     */
    int update(String statement, Object parameter);

    /**
     * 执行delete语句
     * Execute a delete statement. The number of rows affected will be returned.
     *
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the delete.
     */
    int delete(String statement);

    /**
     * Execute a delete statement. The number of rows affected will be returned.
     *
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the delete.
     */
    int delete(String statement, Object parameter);

    /**
     * 提交事务
     * Flushes batch statements and commits database connection.
     * Note that database connection will not be committed if no updates/deletes/inserts were called.
     * To force the commit call {@link SqlSession#commit(boolean)}
     */
    void commit();

    /**
     * Flushes batch statements and commits database connection.
     *
     * @param force forces connection commit
     */
    void commit(boolean force);

    /**
     * 回滚事务
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     * To force the rollback call {@link SqlSession#rollback(boolean)}
     */
    void rollback();

    /**
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     *
     * @param force forces connection rollback
     */
    void rollback(boolean force);

    /**
     * 批量执行sql
     * Flushes batch statements.
     *
     * @return BatchResult list of updated records
     * @since 3.0.6
     */
    List<BatchResult> flushStatements();

    /**
     * 关闭当前session
     * Closes the session
     */
    @Override
    void close();

    /**
     * 清空缓存
     * Clears local session cache
     */
    void clearCache();

    /**
     * Retrieves current configuration
     *
     * @return Configuration
     */
    Configuration getConfiguration();

    /**
     * 获取type对应的mapper对象
     * Retrieves a mapper.
     *
     * @param <T>  the mapper type
     * @param type Mapper interface class
     * @return a mapper bound to this SqlSession
     */
    <T> T getMapper(Class<T> type);

    /**
     * 获取当前SqlSession对应的数据库连接
     * Retrieves inner database connection
     *
     * @return Connection
     */
    Connection getConnection();
}
