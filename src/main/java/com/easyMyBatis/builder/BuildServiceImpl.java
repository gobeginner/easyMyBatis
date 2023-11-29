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

public class BuildServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String interfaceName = tableInfo.getBeanName() + "Service";
        String className = tableInfo.getBeanName() + "ServiceImpl";
        File serviceFile = new File(folder, className + ".java");
        OutputStream out = null;
        OutputStreamWriter outWriter = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(serviceFile);
            outWriter = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outWriter);
            String mapperName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
            String mapperNameLowercase = StringUtils.lowerCaseFirstLetter(mapperName);
            bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_ENUM + ".PageSize;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_MAPPERS + "." + mapperName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + interfaceName + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            BuildComment.createClassComment(bw, tableInfo.getComment() + " 业务接口实现");
            bw.write("@Service(\"" + StringUtils.lowerCaseFirstLetter(interfaceName) + "\")");
            bw.newLine();
            bw.write("public class " + className + " implements " + interfaceName + " {");

            bw.newLine();
            bw.newLine();
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + mapperName + "<" + tableInfo.getBeanName() + ", " + tableInfo.getBeanParamName() + "> " + StringUtils.lowerCaseFirstLetter(mapperName) + ";");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperNameLowercase + ".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "根据条件查询个数");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer findCountByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperNameLowercase + ".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic PaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\tInteger count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tint pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(), count, pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<" + tableInfo.getBeanName() + "> list = this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\tPaginationResultVO<" + tableInfo.getBeanName() + "> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);");
            bw.newLine();
            bw.write("\t\treturn result;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "新增");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperNameLowercase + ".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif (listBean == null || listBean.isEmpty()) {\n" +
                    "            return 0;\n" +
                    "        }");
            bw.newLine();
            bw.write("\t\treturn this." + mapperNameLowercase + ".insertBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createFieldComment(bw, "批量新增/修改");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif (listBean == null || listBean.isEmpty()) {\n" +
                    "            return 0;\n" +
                    "        }");
            bw.newLine();
            bw.write("\t\treturn this." + mapperNameLowercase + ".insertOrUpdateBatch(listBean);");
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
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic " + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperNameLowercase + ".selectBy" + methodName + "(" + methodParamsWithoutType + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperNameLowercase + ".deleteBy" + methodName + "(" + methodParamsWithoutType + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "修改");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperNameLowercase + ".updateBy" + methodName + "(bean, " + methodParamsWithoutType + ");");
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
