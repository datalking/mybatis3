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

/**
 * Reflector类反射工厂 接口
 */
public interface ReflectorFactory {

    // 获取是否开启缓存Reflector 对象，默认开启
    boolean isClassCacheEnabled();

    // 设置是否开启缓存Reflector 对象
    void setClassCacheEnabled(boolean classCacheEnabled);

    // 从缓存中查找class元信息，若没有则创建
    Reflector findForClass(Class<?> type);
}
