package com.yuxin.springbootinit.utils;

import java.util.regex.Pattern;

/**
 * packageName com.yuxin.springbootinit.utils
 * 去除AI生成多余内容
 */
public class RemoveUtils {
        public static void main(String[] args) {
            String text = "这里是文本内容，可能包含由AI生成的[多余内容]。";

            // 定义正则表达式来匹配AI生成的标记
            String patternString = "\\[.*?\\]"; // 匹配括号内的内容

            // 编译正则表达式
            Pattern pattern = Pattern.compile(patternString);

            // 使用正则表达式进行替换，去除括号及其内容
            String result = pattern.matcher(text).replaceAll("");

            // 打印结果
            System.out.println(result);
        }
}
