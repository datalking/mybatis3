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
package org.apache.ibatis.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.invoker.MethodInvoker;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 类的元信息
 *
 * @author Clinton Begin
 */
public class MetaClass {

    // 创建类元信息的factory
    private ReflectorFactory reflectorFactory;
    // 记录类元信息的reflector
    private Reflector reflector;

    private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
        this.reflector = reflectorFactory.findForClass(type);
    }

    // 一般使用此方法代替构造方法
    public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
        return new MetaClass(type, reflectorFactory);
    }

    // 为该属性创建对应的 MetaClass 对象
    public MetaClass metaClassForProperty(String name) {
        Class<?> propType = reflector.getGetterType(name);
        return MetaClass.forClass(propType, reflectorFactory);
    }

    // 获取属性元信息，属性只能使用.号导航
    public String findProperty(String name) {

        StringBuilder prop = buildProperty(name, new StringBuilder());

        return prop.length() > 0 ? prop.toString() : null;
    }

    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping) {
            name = name.replace("_", "");
        }
        return findProperty(name);
    }

    public String[] getGetterNames() {
        return reflector.getGetablePropertyNames();
    }

    public String[] getSetterNames() {
        return reflector.getSetablePropertyNames();
    }

    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaClass metaProp = metaClassForProperty(prop.getName());
            return metaProp.getSetterType(prop.getChildren());
        } else {
            return reflector.getSetterType(prop.getName());
        }
    }

    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);

        if (prop.hasNext()) {
            MetaClass metaProp = metaClassForProperty(prop);

            // 递归
            return metaProp.getGetterType(prop.getChildren());
        }
        // issue #506. Resolve the type inside a Collection Object
        return getGetterType(prop);
    }

    private MetaClass metaClassForProperty(PropertyTokenizer prop) {
        // 获取表达式所表示的属性的类型
        Class<?> propType = getGetterType(prop);
        return MetaClass.forClass(propType, reflectorFactory);
    }

    private Class<?>    getGetterType(PropertyTokenizer prop) {
        // 获取属性类型
        Class<?> type = reflector.getGetterType(prop.getName());

        // 该表达式中是否使用 [] 指定了下标，且是 Collection 子类
        if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
            Type returnType = getGenericGetterType(prop.getName());

            if (returnType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();

                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    returnType = actualTypeArguments[0];

                    if (returnType instanceof Class) {
                        type = (Class<?>) returnType;
                    } else if (returnType instanceof ParameterizedType) {
                        type = (Class<?>) ((ParameterizedType) returnType).getRawType();
                    }
                }
            }
        }
        return type;
    }

    private Type getGenericGetterType(String propertyName) {
        try {
            Invoker invoker = reflector.getGetInvoker(propertyName);
            if (invoker instanceof MethodInvoker) {
                Field _method = MethodInvoker.class.getDeclaredField("method");
                _method.setAccessible(true);
                Method method = (Method) _method.get(invoker);
                return TypeParameterResolver.resolveReturnType(method, reflector.getType());
            } else if (invoker instanceof GetFieldInvoker) {
                Field _field = GetFieldInvoker.class.getDeclaredField("field");
                _field.setAccessible(true);
                Field field = (Field) _field.get(invoker);
                return TypeParameterResolver.resolveFieldType(field, reflector.getType());
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }

    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (reflector.hasSetter(prop.getName())) {
                MetaClass metaProp = metaClassForProperty(prop.getName());
                return metaProp.hasSetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasSetter(prop.getName());
        }
    }

    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (reflector.hasGetter(prop.getName())) {
                MetaClass metaProp = metaClassForProperty(prop);
                return metaProp.hasGetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasGetter(prop.getName());
        }
    }

    public Invoker getGetInvoker(String name) {
        return reflector.getGetInvoker(name);
    }

    public Invoker getSetInvoker(String name) {
        return reflector.getSetInvoker(name);
    }

    // 解析属性表达式
    private StringBuilder buildProperty(String name, StringBuilder builder) {
        PropertyTokenizer prop = new PropertyTokenizer(name);

        if (prop.hasNext()) {

            String propertyName = reflector.findPropertyName(prop.getName());

            if (propertyName != null) {
                builder.append(propertyName);
                builder.append(".");
                MetaClass metaProp = metaClassForProperty(propertyName);

                // 递归解析children字段
                metaProp.buildProperty(prop.getChildren(), builder);
            }

        } else {
            String propertyName = reflector.findPropertyName(name);
            if (propertyName != null) {
                builder.append(propertyName);
            }
        }
        return builder;
    }

    public boolean hasDefaultConstructor() {
        return reflector.hasDefaultConstructor();
    }

}
