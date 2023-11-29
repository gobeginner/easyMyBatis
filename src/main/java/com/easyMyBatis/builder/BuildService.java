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

public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    //生成bean
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + "Service";
        File serviceFile = new File(folder, className + ".java");
        OutputStream out = null;
        OutputStreamWriter outWriter = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(serviceFile);
            outWriter = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outWriter);
            bw.write("package " + Constants.PACKAGE_SERVICE + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            BuildComment.createClassComment(bw, tableInfo.getComment() + " 业务接口");
            bw.write("public interface " + className + " {");

            bw.newLine();
            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("\tList<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "根据条件查询个数");
            bw.write("\tInteger findCountByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("\tPaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "新增");
            bw.write("\tInteger add(" + tableInfo.getBeanName() + " bean);");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\tInteger addBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "批量新增/修改");
            bw.write("\tInteger addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
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
                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    if (index < keyIndexInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询对象");
                bw.write("\t" + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "修改");
                bw.write("\tInteger update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ");");
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            logger.error("创建service失败！", e);
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
