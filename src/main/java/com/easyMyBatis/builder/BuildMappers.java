package com.easyMyBatis.builder;

import com.easyMyBatis.bean.Constants;
import com.easyMyBatis.bean.FieldInfo;
import com.easyMyBatis.bean.TableInfo;
import com.easyMyBatis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildMappers {
    private static final Logger logger = LoggerFactory.getLogger(BuildMappers.class);

    //生成bean
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File poFile = new File(folder, tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS + ".java");
        OutputStream out = null;
        OutputStreamWriter outWriter = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outWriter = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outWriter);
            bw.write("package " + Constants.PACKAGE_MAPPERS + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            //类注解
            BuildComment.createClassComment(bw, tableInfo.getComment() + "Mappers");
            bw.write("public interface " + tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS + "<T, P> extends BaseMapper" + " {");
            bw.newLine();
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyIndexInfoList = entry.getValue();
                Integer index = 0;
                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParams = new StringBuffer();
                for (FieldInfo fieldInfo : keyIndexInfoList) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyIndexInfoList.size()) {
                        methodName.append("And");
                    }
                    methodParams.append("@Param(\"" + fieldInfo.getPropertyName() + "\") " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    if (index < keyIndexInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(" + "@Param(\"bean\") T t, " + methodParams + ");");
                bw.newLine();
                bw.newLine();
            }
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建mapper失败！", e);
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
