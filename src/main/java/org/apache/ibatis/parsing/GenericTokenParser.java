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
package org.apache.ibatis.parsing;

/**
 * 通用的标记占位符解析器
 * 主要用于查找指定占位符标记默认值，以及解析动态sql
 *
 * @author Clinton Begin
 */
public class GenericTokenParser {

    // 占位符开始标记
    private final String openToken;
    // 占位符结束标记
    private final String closeToken;

    // 占位符标记解析处理
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    /**
     * 顺序查找 openToken 和 closeToken，解析得到占位符的字面值
     */
    public String parse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;

        // search open token 查找开始标记
        int start = text.indexOf(openToken, offset);
        if (start == -1) {
            return text;
        }

        // 记录解析后的字符串
        final StringBuilder builder = new StringBuilder();
        // 记录标记占位符字面量
        StringBuilder expression = null;
        while (start > -1) {

            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                // 遇到转义标记，则直接将前面的字符串以及开始标记追加到 builder 中
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token. 查找到开始标记，且未转义
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }

                // 将前面的字符串追加到 builder 中
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                // 从 offset 向后继续查找结束标记
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {

                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        // 处理转义的结束标记
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        // 将开始标记和结束标记之间的字符串追加到 expression 中保存
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }

                /// 未找到结束标记
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    /// 将占位符的字面值交给TokenHandler处理，并将处理结果追加到builder中，最终拼凑出解析后的完整内容
                    builder.append(handler.handleToken(expression.toString()));
                    offset = end + closeToken.length();
                }
            }

            // 移动 start
            start = text.indexOf(openToken, offset);
        }

        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }

        return builder.toString();
    }
}
