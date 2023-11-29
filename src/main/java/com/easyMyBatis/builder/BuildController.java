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

public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_CONTROLLER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + "Controller";
        File serviceFile = new File(folder, className + ".java");
        OutputStream out = null;
        OutputStreamWriter outWriter = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(serviceFile);
            outWriter = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outWriter);
            String serviceName = tableInfo.getBeanName() + "Service";
            String serviceNameLowercase = StringUtils.lowerCaseFirstLetter(serviceName);
            bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".ResponseVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            BuildComment.createClassComment(bw, tableInfo.getComment() + " 业务实现");
            bw.write("@RestController");
            bw.newLine();
            bw.write("@RequestMapping(\"" + StringUtils.lowerCaseFirstLetter(tableInfo.getBeanName()) + "\")");
            bw.newLine();
            bw.write("public class " + className + " extends ABaseController" + " {");

            bw.newLine();
            bw.newLine();
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + serviceName + " " + serviceNameLowercase + ";");
            bw.newLine();
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "根据条件分页查询");
            bw.write("\t@RequestMapping(\"loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(" + serviceNameLowercase + ".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "新增");
            bw.write("\t@RequestMapping(\"add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\t" + serviceNameLowercase + ".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\t@RequestMapping(\"addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\t" + serviceNameLowercase + ".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "批量新增/修改");
            bw.write("\t@RequestMapping(\"addOrUpdateBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tproductInfoService.addOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyIndexInfoList = entry.getValue();
                Integer index = 0;
                StringBuffer methodName = new StringBuffer();
                StringBuffer methodParams = new StringBuffer();
                StringBuffer methodParamsWithoutType = new StringBuffer();
                for (FieldInfo fieldInfo : keyIndexInfoList) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyIndexInfoList.size()) {
                        methodName.append("And");
                    }
                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    methodParamsWithoutType.append(fieldInfo.getPropertyName());
                    if (index < keyIndexInfoList.size()) {
                        methodParams.append(", ");
                        methodParamsWithoutType.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询对象");
                bw.write("\t@RequestMapping(\"get" + tableInfo.getBeanName() + "By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO " + "get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(" + serviceNameLowercase + ".get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParamsWithoutType + "));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\t@RequestMapping(\"delete" + tableInfo.getBeanName() + "By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\t" + serviceNameLowercase + ".delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParamsWithoutType + "); ");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "修改");
                bw.write("\t@RequestMapping(\"update" + tableInfo.getBeanName() + "By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
                bw.newLine();
                bw.write("\t\t" + serviceNameLowercase + ".update" + tableInfo.getBeanName() + "By" + methodName + "(bean, " + methodParamsWithoutType + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
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
