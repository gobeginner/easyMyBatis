package com.easyMyBatis.builder;

import com.easyMyBatis.bean.Constants;
import com.easyMyBatis.bean.FieldInfo;
import com.easyMyBatis.bean.TableInfo;
import com.easyMyBatis.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);

    private static final String BASE_COLUMN_LIST = "base_column_list";
    private static final String BASE_QUERY_CONDITION = "base_query_condition";
    private static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";
    private static final String QUERY_CONDITION = "query_condition";

    //生成bean
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS_XMLS);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;  //ProductInfoMapper
        File poFile = new File(folder, className + ".xml");
        OutputStream out = null;
        OutputStreamWriter outWriter = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outWriter = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outWriter);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
            bw.newLine();
            bw.write("\t\t\"https://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPERS + "." + className + "\">");
            bw.newLine();
            //实体映射
            bw.write("\t<!-- 实体映射 -->");
            bw.newLine();
            String poClass = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"base_result_map\" type=\"" + poClass + "\">");
            bw.newLine();
            FieldInfo idField = null;
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                if ("PRIMARY".equals(entry.getKey())) {
                    List<FieldInfo> fieldInfoList = entry.getValue();
                    if (fieldInfoList.size() == 1) {
                        idField = fieldInfoList.get(0);
                        break;
                    }
                }
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t<!-- " + fieldInfo.getComment() + " -->");
                bw.newLine();
                String key = null;
                if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
                    key = "id";
                } else {
                    key = "result";
                }
                bw.write("\t\t");
                bw.write("<result property=\"" + fieldInfo.getPropertyName() + "\"" + " column=\"" + fieldInfo.getFieldName() + "\"></result>");
                bw.newLine();
            }
            bw.write("\t</resultMap>");
            bw.newLine();

            //通用查询列
            bw.newLine();
            bw.write("\t<!-- 通用查询结果列 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
            bw.newLine();
            StringBuilder columnBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                columnBuilder.append(fieldInfo.getFieldName() + ",");
            }
            String columnBuilderStr = columnBuilder.substring(0, columnBuilder.lastIndexOf(","));
            bw.write("\t\t" + columnBuilderStr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //基础查询条件
            bw.newLine();
            bw.write("\t<!-- 基础查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String stringQuery = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, fieldInfo.getSqlType())) {
                    stringQuery = " and query." + fieldInfo.getPropertyName() + "!=''";
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + "!= null" + stringQuery + "\">");
                bw.newLine();
                bw.write("\t\t\tand " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //扩展的查询条件
            bw.newLine();
            bw.write("\t<!-- 扩展的查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldExtendList()) {
                String andWhere = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, fieldInfo.getSqlType())) {
                    andWhere = "and " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)) {
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d') ]]>";
                    } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)) {
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " < date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d'),interval -1 day) ]]>";
                    }
                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + "!= null and query." + fieldInfo.getPropertyName() + "!=''" + "\">");
                bw.newLine();
                bw.write("\t\t\t" + andWhere);
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //通用查询条件
            bw.newLine();
            bw.write("\t<!-- 通用查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"></include>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"></include>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //查询列表
            bw.newLine();
            bw.write("\t<!-- 查询列表 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectList\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write("\t\tSELECT");
            bw.newLine();
            bw.write("\t\t<include refid=\"base_column_list\"/>");
            bw.newLine();
            bw.write("\t\tFROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy!=null\">\n" +
                    "            order by ${query.orderBy}\n" +
                    "        </if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage!=null\">\n" +
                    "            limit #{query.simplePage.start},#{query.simplePage.end}\n" +
                    "        </if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

            //查询数量
            bw.newLine();
            bw.write("\t<!-- 查询数量 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

            //插入 （匹配有值的字段）
            bw.newLine();
            bw.write("\t<!-- 插入 （匹配有值的字段 -->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            FieldInfo autoFieldInfo = null;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement() != null && fieldInfo.getIsAutoIncrement()) {
                    autoFieldInfo = fieldInfo;
                    break;
                }
            }
            if (autoFieldInfo != null) {
                bw.write("\t\t<selectKey keyProperty=\"bean." + autoFieldInfo.getPropertyName() + "\" resultType=\"" + autoFieldInfo.getJavaType() + "\" order=\"AFTER\">\n" +
                        "\t\t\tSELECT LAST_INSERT_ID()\n" +
                        "\t\t</selectKey>");
            }
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">\n" +
                        "                " + fieldInfo.getFieldName() + ",\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + "!=null\">\n" +
                        "                #{bean." + fieldInfo.getPropertyName() + "},\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //插入或者更新 （匹配有值的字段）
            bw.newLine();
            bw.write("\t<!-- 插入或者更新 （匹配有值的字段） -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">\n" +
                        "                " + fieldInfo.getFieldName() + ",\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + "!=null\">\n" +
                        "                #{bean." + fieldInfo.getPropertyName() + "},\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            HashMap<String, String> keyTempMap = new HashMap<>();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo fieldInfo : fieldInfoList) {
                    keyTempMap.put(fieldInfo.getFieldName(), fieldInfo.getFieldName());
                }
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (keyTempMap.get(fieldInfo.getFieldName()) != null) {
                    continue;
                }
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + "!=null\">\n" +
                        "                " + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),\n" +
                        "            </if>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //添加 （批量插入）
            bw.newLine();
            bw.write("\t<!-- 添加 （批量插入） -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            StringBuffer insertField = new StringBuffer();
            StringBuffer insertFieldValue = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getIsAutoIncrement()) {
                    continue;
                }
                insertField.append("\t\t" + fieldInfo.getFieldName() + ",\n");
                insertFieldValue.append("\t\t\t" + "#{item." + fieldInfo.getPropertyName() + "},\n");
            }
            String insertStr = insertField.substring(0, insertField.lastIndexOf(",")).toString();
            String insertStrValue = insertFieldValue.substring(0, insertFieldValue.lastIndexOf(",")).toString();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(");
            bw.newLine();
            bw.write(insertStr);
            bw.newLine();
            bw.write("\t\t)values");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
            bw.newLine();
            bw.write("\t\t\t(");
            bw.newLine();
            bw.write(insertStrValue);
            bw.newLine();
            bw.write("\t\t\t)");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //批量新增修改 （批量插入）
            bw.newLine();
            bw.write("\t<!-- 批量新增修改 （批量插入） -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(");
            bw.newLine();
            bw.write(insertStr);
            bw.newLine();
            bw.write("\t\t)values");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();
            bw.write("\t\t\t(");
            bw.newLine();
            bw.write(insertStrValue);
            bw.newLine();
            bw.write("\t\t\t)");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            StringBuilder insertBatchUpdateBuffer = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                insertBatchUpdateBuffer.append("\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),\n");
            }
            String insertBatchUpdateBufferStr = insertBatchUpdateBuffer.substring(0, insertBatchUpdateBuffer.lastIndexOf(","));
            bw.write(insertBatchUpdateBufferStr);
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //根据主键更新、删除
            bw.newLine();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyIndexInfoList = entry.getValue();
                Integer index = 0;
                StringBuffer methodName = new StringBuffer();
                StringBuffer paramsName = new StringBuffer();
                for (FieldInfo fieldInfo : keyIndexInfoList) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    paramsName.append(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "}");
                    if (index < keyIndexInfoList.size()) {
                        methodName.append("And");
                        paramsName.append(" and ");
                    }
                }
                bw.write("\t<!-- 根据" + methodName + "获取对象-->");
                bw.newLine();
                bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\"base_result_map\">");
                bw.newLine();
                bw.write("\t\tselect\n" +
                        "        <include refid=\"" + BASE_COLUMN_LIST + "\"/>\n" +
                        "        from " + tableInfo.getTableName() + " where " + paramsName);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();
                bw.newLine();

                bw.write("\t<!-- 根据" + methodName + "修改对象-->");
                bw.newLine();
                bw.write("\t<update id=\"updateBy" + methodName + "\" parameterType=\"" + poClass + "\">");
                bw.newLine();
                bw.write("\t\tUPDATE " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.newLine();
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t\twhere " + paramsName);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();
                bw.newLine();

                bw.write("\t<!-- 根据" + methodName + "删除对象-->");
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tdelete\n" +
                        "        from " + tableInfo.getTableName() + "\n" +
                        "        where " + paramsName);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();
                bw.newLine();

//                bw.write("\tT deleteBy" + methodName + "(" + "@Param(\"bean\") T t, " + methodParams + ");");
//                bw.newLine();
//                bw.newLine();
//                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
//                bw.write("\tT updateBy" + methodName + "(" + methodParams + ");");
//                bw.newLine();
//                bw.newLine();
            }

            bw.newLine();
            bw.write("</mapper>");
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            logger.error("创建mapperXML失败！", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outWriter != null) {
                try {
                    outWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}