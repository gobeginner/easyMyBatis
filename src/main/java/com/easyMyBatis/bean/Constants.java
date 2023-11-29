package com.easyMyBatis.bean;

import com.easyMyBatis.utils.PropertiesUtils;

/**
 * 抽取配置文件中的常量
 */
public class Constants {
    public static String AUTHOR_COMMENT;
    public static Boolean IGNORE_TABLE_PREFIX;
    public static String SUFFIX_BEAN_QUERY;
    //参数模糊搜索后缀
    public static String SUFFIX_BEAN_QUERY_FUZZY;
    //Mappers后缀
    public static String SUFFIX_MAPPERS;
    //参数日期起始
    public static String SUFFIX_BEAN_QUERY_TIME_START;
    public static String SUFFIX_BEAN_QUERY_TIME_END;
    //需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FIELD;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;
    //时间格式序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    //时间格式反序列化
    public static String BEAN_DATE_UNFORMAT_EXPRESSION;
    public static String BEAN_DATE_UNFORMAT_CLASS;

    public static String PATH_JAVA = "java";
    public static String PATH_RESOURCE = "resources";
    public static String PATH_BASE;
    public static String PATH_PO;
    public static String PATH_UTILS;
    public static String PATH_ENUM;
    public static String PATH_QUERY;
    public static String PATH_MAPPERS_XMLS;
    public static String PATH_MAPPERS;
    public static String PATH_SERVICE;
    public static String PATH_SERVICE_IMPL;
    public static String PATH_VO;
    public static String PATH_EXCEPTION;
    public static String PATH_CONTROLLER;

    public static String PACKAGE_BASE;
    public static String PACKAGE_PO;
    public static String PACKAGE_QUERY;
    public static String PACKAGE_UTILS;
    public static String PACKAGE_ENUM;
    public static String PACKAGE_MAPPERS;
    public static String PACKAGE_SERVICE;
    public static String PACKAGE_SERVICE_IMPL;
    public static String PACKAGE_VO;
    public static String PACKAGE_EXCEPTION;
    public static String PACKAGE_CONTROLLER;


    static {
        //需要忽略的属性
        IGNORE_BEAN_TOJSON_FIELD = PropertiesUtils.getString("ignore.bean.tojson.field");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");
        //时间格式序列化
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        //时间格式反序列化
        BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.unformat.expression");
        BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.unformat.class");
        //读取是否忽略前缀
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        //参数bean后缀
        SUFFIX_BEAN_QUERY = PropertiesUtils.getString("suffix.bean.query");
        //参数模糊搜索后缀
        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");
        //Mappers后缀
        SUFFIX_MAPPERS = PropertiesUtils.getString("suffix.mappers");
        //参数日期起始
        SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getString("suffix.bean.query.time.end");
        //文件输出路径
        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        //utils路径
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        //po包名
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        //enum包名
        PACKAGE_ENUM = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enum");
        //query包名
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.query");
        //vo包名
        PACKAGE_VO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.vo");
        //mappers包名
        PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.mappers");
        //service包名
        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service");
        //serviceImpl包名
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service.impl");
        //exception包名
        PACKAGE_EXCEPTION = PACKAGE_BASE + "." + PropertiesUtils.getString("package.exception");
        //controller包名
        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertiesUtils.getString("package.controller");

        //基础路径
        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA + "/" + PACKAGE_BASE.replace(".", "/");
        //po路径
        PATH_PO = PATH_BASE + "/" + PropertiesUtils.getString("package.po").replace(".", "/");
        //utils路径
        PATH_UTILS = PATH_BASE + "/" + PropertiesUtils.getString("package.utils").replace(".", "/");
        //enum路径
        PATH_ENUM = PATH_BASE + "/" + PropertiesUtils.getString("package.enum").replace(".", "/");
        //query路径
        PATH_QUERY = PATH_BASE + "/" + PropertiesUtils.getString("package.query").replace(".", "/");
        //vo路径
        PATH_VO = PATH_BASE + "/" + PropertiesUtils.getString("package.vo").replace(".", "/");
        //mappers路径
        PATH_MAPPERS = PATH_BASE + "/" + PropertiesUtils.getString("package.mappers").replace(".", "/");
        //xml路径
        PATH_MAPPERS_XMLS = PropertiesUtils.getString("path.base") + PATH_RESOURCE + "/" + PACKAGE_MAPPERS.replace(".", "/");
        //service路径
        PATH_SERVICE = PATH_BASE + "/" + PropertiesUtils.getString("package.service").replace(".", "/");
        //serviceImpl路径
        PATH_SERVICE_IMPL = PATH_BASE + "/" + PropertiesUtils.getString("package.service.impl").replace(".", "/");
        //exception路径
        PATH_EXCEPTION = PATH_BASE + "/" + PropertiesUtils.getString("package.exception").replace(".", "/");
        //controller路径
        PATH_CONTROLLER = PATH_BASE + "/" + PropertiesUtils.getString("package.controller").replace(".", "/");
        //作者名字
        AUTHOR_COMMENT = PropertiesUtils.getString("author.comment");

    }

    //sql的类型对应的java的类型
    public final static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public final static String[] SQL_DATE_TYPES = new String[]{"date"};
    public final static String[] SQL_DECIMAL_TYPE = new String[]{"decimal", "double", "float"};
    public final static String[] SQL_STRING_TYPE = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};
    public final static String[] SQL_INTEGER_TYPE = new String[]{"int", "tinyint"};
    public final static String[] SQL_LONG_TYPE = new String[]{"bigint"};

    public static void main(String[] args) {
        System.out.println(Constants.PACKAGE_CONTROLLER);
        System.out.println(Constants.PATH_CONTROLLER);
    }

}
