package com.easyMyBatis.builder;

import com.easyMyBatis.bean.Constants;
import com.easyMyBatis.bean.FieldInfo;
import com.easyMyBatis.bean.TableInfo;
import com.easyMyBatis.utils.PropertiesUtils;
import com.easyMyBatis.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildTable {
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static Connection conn = null;

    private static String SQL_SHOW_TABLE_STATUS = "show TABLE STATUS";
    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";
    private static String SQL_SHOW_TABLE_INDEX = "show index from %s";

    //连接数据库
    static {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            logger.error("数据库连接失败！", e);
        }
    }

    //获取表信息
    public static List<TableInfo> getTables() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            rs = ps.executeQuery();
            while (rs.next()) {
                String tableName = rs.getString("Name");
                String comment = rs.getString("Comment");
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(beanName.indexOf("_") + 1);
                }
                beanName = processField(beanName, true);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_QUERY);
                tableInfo.setHaveDate(false);
                tableInfo.setHaveDateTime(false);
                tableInfo.setHaveBigDecimal(false);
                readFieldInfo(tableInfo);
                readKeyIndexInfo(tableInfo);
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("读取表失败！", e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return tableInfoList;
    }

    //获取属性信息
    public static void readFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> fieldExtendList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String field = fieldResult.getString("Field");
                String type = fieldResult.getString("Type");
                String extra = fieldResult.getString("Extra");
                String comment = fieldResult.getString("Comment");
                if (type.indexOf("(") > 0) {
                    type = type.substring(0, type.indexOf("("));
                }
                String propertyName = processField(field, false);
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(field);
                fieldInfo.setSqlType(type);
                fieldInfo.setComment(comment);
                fieldInfo.setIsAutoIncrement("auto_increment".equals(extra) ? true : false);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(type));
                fieldInfoList.add(fieldInfo);
                tableInfo.setFieldList(fieldInfoList);
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
                    tableInfo.setHaveDate(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
                    tableInfo.setHaveDateTime(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE, type)) {
                    tableInfo.setHaveBigDecimal(true);
                }

                //String类型的属性进行模糊查询存放在tableInfoList中
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, type)) {
                    FieldInfo tempFieldInfo = new FieldInfo();
                    tempFieldInfo.setJavaType(fieldInfo.getJavaType());
                    tempFieldInfo.setSqlType(type);
                    tempFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    tempFieldInfo.setFieldName(fieldInfo.getFieldName());
                    fieldExtendList.add(tempFieldInfo);
                }

                //Date类型的属性进行时间段查询存放在tableInfoList中
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
                    FieldInfo tempStartFieldInfo = new FieldInfo();
                    tempStartFieldInfo.setJavaType("String");
                    tempStartFieldInfo.setSqlType(type);
                    tempStartFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    tempStartFieldInfo.setFieldName(fieldInfo.getFieldName());
                    fieldExtendList.add(tempStartFieldInfo);
                    FieldInfo tempEndFieldInfo = new FieldInfo();
                    tempEndFieldInfo.setJavaType("String");
                    tempEndFieldInfo.setSqlType(type);
                    tempEndFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    tempEndFieldInfo.setFieldName(fieldInfo.getFieldName());
                    fieldExtendList.add(tempEndFieldInfo);
                }

            }
            tableInfo.setFieldExtendList(fieldExtendList);
            tableInfo.setFieldList(fieldInfoList);
        } catch (Exception e) {
            logger.error("读取表失败！", e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fieldResult != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取index信息
    public static void readKeyIndexInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            HashMap<String, FieldInfo> tempMap = new HashMap<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                tempMap.put(fieldInfo.getFieldName(), fieldInfo);
            }
            while (fieldResult.next()) {
                String keyName = fieldResult.getString("Key_name");
                Integer nonUnique = fieldResult.getInt("non_unique");
                String columnName = fieldResult.getString("column_name");
                if (nonUnique == 1) {
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if (keyFieldList == null) {
                    keyFieldList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName, keyFieldList);
                }
                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("读取索引失败！", e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fieldResult != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //属性名修改大写
    private static String processField(String field, Boolean upperCaseFirstLetter) {
        StringBuilder sb = new StringBuilder();
        String[] fields = field.split("_");
        sb.append(upperCaseFirstLetter ? StringUtils.upperCaseFirstLetter(fields[0]) : fields[0]);
        for (int i = 1; i < fields.length; i++) {
            sb.append(StringUtils.upperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    //将sql的类型转换成java的类型
    public static String processJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPE, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPE, type)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, type)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE, type)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别的类型" + type);
        }
    }

}
