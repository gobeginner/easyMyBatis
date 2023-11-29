package com.easyMyBatis.builder;

import com.easyMyBatis.bean.Constants;
import com.easyMyBatis.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

//创建bean注解
public class BuildComment {
    public static void createClassComment(BufferedWriter bw, String classComment) throws IOException {
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description:" + classComment);
        bw.newLine();
        bw.write(" * @author:" + Constants.AUTHOR_COMMENT);
        bw.newLine();
        bw.write(" * @date:" + DateUtils.format(new Date(), DateUtils._YYYYMMDD));
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }

    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws IOException {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * ");
        bw.write(fieldComment == null ? "" : fieldComment);
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }

    public static void createMethodComment(BufferedWriter bw) {

    }
}
