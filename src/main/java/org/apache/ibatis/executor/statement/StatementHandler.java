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
package org.apache.ibatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.ResultHandler;

/**
 * sql语句执行器
 *
 * @author Clinton Begin
 */
public interface StatementHandler {

    // 从连接中获取一个 Statement
    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

    // 绑定 statement 执行时所需的实参
    void parameterize(Statement statement) throws SQLException;

    // 批量执行 SQL 语句
    void batch(Statement statement) throws SQLException;

    // 执行 update/insert/delete 语句
    int update(Statement statement) throws SQLException;

    // 执行 select 语句
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

    <E> Cursor<E> queryCursor(Statement statement) throws SQLException;

    // 获取动态sql处理后可直接执行的sql
    BoundSql getBoundSql();

    // 获取其中封装的 ParameterHandler
    ParameterHandler getParameterHandler();

}
