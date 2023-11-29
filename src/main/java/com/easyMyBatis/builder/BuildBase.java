package com.easyMyBatis.builder;

import com.easyMyBatis.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildBase {
    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);

    public static void execute() {
        ArrayList<String> headerInfoList = new ArrayList<>();
        //生成date枚举
        headerInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUM);
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_UTILS);
        build(headerInfoList, "DateUtil", Constants.PATH_UTILS);
        headerInfoList.clear();
        //生成baseMapper
        headerInfoList.add("package " + Constants.PACKAGE_MAPPERS);
        build(headerInfoList, "BaseMapper", Constants.PATH_MAPPERS);
        headerInfoList.clear();
        //生成pageSize枚举
        headerInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(headerInfoList, "PageSize", Constants.PATH_ENUM);
        headerInfoList.clear();
        //生成simplePage
        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".PageSize");
        build(headerInfoList, "SimplePage", Constants.PATH_QUERY);
        headerInfoList.clear();
        //生成baseQuery
        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        build(headerInfoList, "BaseQuery", Constants.PATH_QUERY);
        headerInfoList.clear();
        //生成PaginationResultVO
        headerInfoList.add("package " + Constants.PACKAGE_VO);
        build(headerInfoList, "PaginationResultVO", Constants.PATH_VO);
        headerInfoList.clear();
        //生成ResponseCodeEnum
        headerInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(headerInfoList, "ResponseCodeEnum", Constants.PATH_ENUM);
        headerInfoList.clear();
        //生成BusinessException
        headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum;");
        build(headerInfoList, "BusinessException", Constants.PATH_EXCEPTION);
        headerInfoList.clear();
        //生成ResponseVO
        headerInfoList.add("package " + Constants.PACKAGE_VO);
        build(headerInfoList, "ResponseVO", Constants.PATH_VO);
        headerInfoList.clear();
        //生成ABaseController
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum;");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO;");
        build(headerInfoList, "ABaseController", Constants.PATH_CONTROLLER);
        headerInfoList.clear();
        //生成AGlobalExceptionHandlerController
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum;");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO;");
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException;");
        build(headerInfoList, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);
        headerInfoList.clear();

    }

    private static void build(List<String> headerInfoList, String fileName, String outPutPath) {
        File folder = new File(outPutPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File javaFile = new File(outPutPath, fileName + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;
        try {
            out = new FileOutputStream(javaFile);
            outw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(outw);

            String templatePath = BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt").getPath();
            in = new FileInputStream(templatePath);
            inr = new InputStreamReader(in, "UTF-8");
            br = new BufferedReader(inr);

            for (String s : headerInfoList) {
                bw.write(s + ";");
                bw.newLine();
                if (s.contains("package")) {
                    bw.newLine();
                }
            }

            String lineInfo = null;
            while ((lineInfo = br.readLine()) != null) {
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            logger.error("生成基础类{}，失败！", fileName, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inr != null) {
                try {
                    inr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
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
            if (outw != null) {
                try {
                    outw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
