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

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * sqlSession管理器
 * 支持创建sqlSession对象以及利用sqlSession操作数据库
 *
 * @author Larry Meadors
 */
public class SqlSessionManager implements SqlSessionFactory, SqlSession {

    private final SqlSessionFactory sqlSessionFactory;

    // 记录与当前线程绑定的SqlSession对象，可避免在同一线程中多次创建SqlSession对象
    private ThreadLocal<SqlSession> localSqlSession = new ThreadLocal<>();

    // localSqlSession对象的代理对象
    private final SqlSession sqlSessionProxy;

    private SqlSessionManager(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;

        // 基于jdk动态代理创建代理对象
        this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                // 代理拦截方法
                new SqlSessionInterceptor());
    }

    // 一般代替构造函数
    public static SqlSessionManager newInstance(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionManager(sqlSessionFactory);
    }

    public static SqlSessionManager newInstance(Reader reader) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(reader, null, null));
    }

    public static SqlSessionManager newInstance(Reader reader, String environment) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(reader, environment, null));
    }

    public static SqlSessionManager newInstance(Reader reader, Properties properties) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(reader, null, properties));
    }

    public static SqlSessionManager newInstance(InputStream inputStream) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(inputStream, null, null));
    }

    public static SqlSessionManager newInstance(InputStream inputStream, String environment) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(inputStream, environment, null));
    }

    public static SqlSessionManager newInstance(InputStream inputStream, Properties properties) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(inputStream, null, properties));
    }


    public void startManagedSession() {
        this.localSqlSession.set(openSession());
    }

    public void startManagedSession(boolean autoCommit) {
        this.localSqlSession.set(openSession(autoCommit));
    }

    public void startManagedSession(Connection connection) {
        this.localSqlSession.set(openSession(connection));
    }

    public void startManagedSession(TransactionIsolationLevel level) {
        this.localSqlSession.set(openSession(level));
    }

    public void startManagedSession(ExecutorType execType) {
        this.localSqlSession.set(openSession(execType));
    }

    public void startManagedSession(ExecutorType execType, boolean autoCommit) {
        this.localSqlSession.set(openSession(execType, autoCommit));
    }

    public void startManagedSession(ExecutorType execType, TransactionIsolationLevel level) {
        this.localSqlSession.set(openSession(execType, level));
    }

    public void startManagedSession(ExecutorType execType, Connection connection) {
        this.localSqlSession.set(openSession(execType, connection));
    }

    public boolean isManagedSessionStarted() {
        return this.localSqlSession.get() != null;
    }

    @Override
    public SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return sqlSessionFactory.openSession(connection);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return sqlSessionFactory.openSession(level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return sqlSessionFactory.openSession(execType);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return sqlSessionFactory.openSession(execType, autoCommit);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return sqlSessionFactory.openSession(execType, level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return sqlSessionFactory.openSession(execType, connection);
    }

    @Override
    public Configuration getConfiguration() {
        return sqlSessionFactory.getConfiguration();
    }

    @Override
    public <T> T selectOne(String statement) {
        return sqlSessionProxy.selectOne(statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return sqlSessionProxy.selectOne(statement, parameter);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return sqlSessionProxy.selectMap(statement, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return sqlSessionProxy.selectMap(statement, parameter, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return sqlSessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement) {
        return sqlSessionProxy.selectCursor(statement);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return sqlSessionProxy.selectCursor(statement, parameter);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        return sqlSessionProxy.selectCursor(statement, parameter, rowBounds);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return sqlSessionProxy.selectList(statement);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return sqlSessionProxy.selectList(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return sqlSessionProxy.selectList(statement, parameter, rowBounds);
    }

    @Override
    public void select(String statement, ResultHandler handler) {
        sqlSessionProxy.select(statement, handler);
    }

    @Override
    public void select(String statement, Object parameter, ResultHandler handler) {
        sqlSessionProxy.select(statement, parameter, handler);
    }

    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    @Override
    public int insert(String statement) {
        return sqlSessionProxy.insert(statement);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return sqlSessionProxy.insert(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return sqlSessionProxy.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return sqlSessionProxy.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return sqlSessionProxy.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return sqlSessionProxy.delete(statement, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    @Override
    public Connection getConnection() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot get connection.  No managed session is started.");
        }
        return sqlSession.getConnection();
    }

    @Override
    public void clearCache() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot clear the cache.  No managed session is started.");
        }
        sqlSession.clearCache();
    }

    @Override
    public void commit() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot commit.  No managed session is started.");
        }
        sqlSession.commit();
    }

    @Override
    public void commit(boolean force) {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot commit.  No managed session is started.");
        }
        sqlSession.commit(force);
    }

    @Override
    public void rollback() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot rollback.  No managed session is started.");
        }
        sqlSession.rollback();
    }

    @Override
    public void rollback(boolean force) {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot rollback.  No managed session is started.");
        }
        sqlSession.rollback(force);
    }

    @Override
    public List<BatchResult> flushStatements() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot rollback.  No managed session is started.");
        }
        return sqlSession.flushStatements();
    }

    @Override
    public void close() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot close.  No managed session is started.");
        }
        try {
            sqlSession.close();
        } finally {
            localSqlSession.set(null);
        }
    }

    /**
     * jdk动态代理的拦截方法
     */
    private class SqlSessionInterceptor implements InvocationHandler {

        public SqlSessionInterceptor() {
            // Prevent Synthetic Access
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            final SqlSession sqlSession = SqlSessionManager.this.localSqlSession.get();

            /// 若同一线程的sqlSession存在，则重用
            if (sqlSession != null) {
                try {
                    // 调用真正的 SqlSession 对象，完成数据库的相关操作
                    return method.invoke(sqlSession, args);
                } catch (Throwable t) {
                    throw ExceptionUtil.unwrapThrowable(t);
                }
            }
            /// 若不存在，则创建新的sqlSession
            else {

                final SqlSession autoSqlSession = openSession();
                try {
                    final Object result = method.invoke(autoSqlSession, args);
                    autoSqlSession.commit();
                    return result;
                } catch (Throwable t) {
                    autoSqlSession.rollback();
                    throw ExceptionUtil.unwrapThrowable(t);
                } finally {
                    // 立即关闭
                    autoSqlSession.close();
                }
            }
        }
    }

}
