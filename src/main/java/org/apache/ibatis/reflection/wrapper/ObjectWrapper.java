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
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 对象属性信息操作 接口
 * 对对象的包装，抽象了对象的属性信息
 *
 * @author Clinton Begin
 */
public interface ObjectWrapper {

    Object get(PropertyTokenizer prop);

    void set(PropertyTokenizer prop, Object value);

    // 查找属性表达式指定的属性
    String findProperty(String name, boolean useCamelCaseMapping);

    // 查找可读属性的名称集合
    String[] getGetterNames();

    String[] getSetterNames();

    Class<?> getSetterType(String name);

    Class<?> getGetterType(String name);

    // 判断属性表达式指定属性是否有 getter/setter 方法
    boolean hasSetter(String name);

    boolean hasGetter(String name);

    // 为属性表达式指定的属 ，l生创建相应的 MetaObject 对象
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    // 检查对象是否为Collection类型
    boolean isCollection();

    // 调用 Collection 对象的 add()
    void add(Object element);

    // 调用 Collection 对象的 addAll()
    <E> void addAll(List<E> element);

}
