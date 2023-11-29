package com.easyMyBatis.builder;

import com.easyMyBatis.bean.Constants;
import com.easyMyBatis.bean.FieldInfo;
import com.easyMyBatis.bean.TableInfo;
import com.easyMyBatis.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class BuildQuery {
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    //生成bean
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_QUERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
        File poFile = new File(folder, className + ".java");
        OutputStream out = null;
        OutputStreamWriter outWriter = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outWriter = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outWriter);
            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            bw.newLine();
            //类注解
            BuildComment.createClassComment(bw, tableInfo.getComment());
            bw.newLine();
            bw.write("public class " + className +" extends BaseQuery"+ " {");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                //属性注解
                BuildComment.createFieldComment(bw, fieldInfo.getComment() + "查询对象");
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();

                //String类型的属性进行模糊查询
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPE, fieldInfo.getSqlType())) {
                    bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY + ";");
                    bw.newLine();
                    bw.newLine();
                }

                //Date类型的属性进行时间段查询
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\tprivate String " + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String " + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ";");
                    bw.newLine();
                    bw.newLine();
                }
            }
            //写get set方法
            buildGetSet(bw, tableInfo.getFieldList());
            buildGetSet(bw, tableInfo.getFieldExtendList());
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建po失败！", e);
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

    private static void buildGetSet(BufferedWriter bw, List<FieldInfo> fieldInfoList) throws IOException {
        for (FieldInfo fieldInfo : fieldInfoList) {
            String tempField = StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName());
            bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + tempField + "() {");
            bw.newLine();
            bw.write("\t\treturn " + fieldInfo.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic void " + "set" + tempField + "(" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ") {");
            bw.newLine();
            bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
        }
    }

}
