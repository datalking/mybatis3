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
package org.apache.ibatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.mapping.FetchType;

/**
 * bean属性与数据库字段 一对一 的映射
 * 联合映射在注解API中是不支持的，因为Java注解的限制,不允许循环引用
 *
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface One {

    // 已映射语句(也就是映射器方法)的完全限定名
    String select() default "";

    FetchType fetchType() default FetchType.DEFAULT;

}
