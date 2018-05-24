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
package org.apache.ibatis.executor;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Array;
import java.util.List;

/**
 * 从result list中提取对象的入口
 * 用于将延迟加载得到的结果对象转换成targetType类型的对象
 *
 * @author Andrew Gustafson
 */
public class ResultExtractor {

    private final Configuration configuration;
    private final ObjectFactory objectFactory;

    public ResultExtractor(Configuration configuration, ObjectFactory objectFactory) {
        this.configuration = configuration;
        this.objectFactory = objectFactory;
    }

    /**
     * 从结果集中获取第一个对象
     */
    public Object extractObjectFromList(List<Object> list, Class<?> targetType) {
        Object value = null;

        /// 如果目标对象类型为 List ，则无须转换
        if (targetType != null && targetType.isAssignableFrom(list.getClass())) {
            value = list;
        }
        /// 如果目标对象类型是 Collection 子类
        else if (targetType != null && objectFactory.isCollection(targetType)) {
            value = objectFactory.create(targetType);
            MetaObject metaObject = configuration.newMetaObject(value);
            metaObject.addAll(list);
        }
        /// 如果目标对象类型是数组类型(其中项可以是基本类型，也可以是 对象类型)，则创建targetType类型的集合对象，并复制List<Object>中的项
        else if (targetType != null && targetType.isArray()) {
            Class<?> arrayComponentType = targetType.getComponentType();
            Object array = Array.newInstance(arrayComponentType, list.size());
            if (arrayComponentType.isPrimitive()) {
                for (int i = 0; i < list.size(); i++) {
                    Array.set(array, i, list.get(i));
                }
                value = array;
            } else {
                value = list.toArray((Object[]) array);
            }
        }
        /// 如果目标对象是普通Java对象且延迟加载得到的List大小为 1，则将其中唯一的项作为转换后的对象返回
        else {

            if (list != null && list.size() > 1) {
                throw new ExecutorException("Statement returned more than one row, where no more than one was expected.");
            } else if (list != null && list.size() == 1) {
                value = list.get(0);
            }
        }

        return value;
    }
}
