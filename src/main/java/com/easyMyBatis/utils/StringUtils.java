package com.easyMyBatis.utils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class StringUtils {
    //将单词首字母大写
    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    //将单词首字母小写
    public static String lowerCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        return field.substring(0, 1).toLowerCase() + field.substring(1);
    }
}
