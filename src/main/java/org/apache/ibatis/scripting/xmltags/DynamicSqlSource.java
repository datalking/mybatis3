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
package org.apache.ibatis.scripting.xmltags;

import java.util.Map;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * 动态sql解析
 * 实际执行SQL语句之前，DynamicSqlSource中封装的SQL语句还需要进行一系列解析，才会最终形成数据库可执行的SQL语句
 *
 * @author Clinton Begin
 */
public class DynamicSqlSource implements SqlSource {

    private Configuration configuration;

    // 记录待解析的SqlNode树的根节点
    private SqlNode rootSqlNode;

    public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        this.configuration = configuration;
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {

        // 创建 DynamicContext 对象 ， parameterObject 是用户传入的实参
        DynamicContext context = new DynamicContext(configuration, parameterObject);

        // 法调用整个树形结构中全部 SqlNode.apply()
        rootSqlNode.apply(context);

        // 创建 SqlSourceBuilder ，解析参数属性并将SQL语句中的 #{} 占位符替换成 ? 占位符
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());

        // 创建 BoundSql 对象，并将 DynamicContext.bindings 中的参数信息复制到 additionalParameters 集合中保存
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);

        for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
            boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }

        return boundSql;
    }

}
