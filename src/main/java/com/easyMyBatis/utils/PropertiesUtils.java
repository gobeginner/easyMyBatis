package com.easyMyBatis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置文件工具，用于获取配置文件中的值
 */
public class PropertiesUtils {
    private static Properties props = new Properties();
    private static Map<String, String> PROPS_MAP = new ConcurrentHashMap();

    //获取application.properties的内容，存储到PROPS_MAP中。
    static {
        InputStream is = null;
        try {
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            props.load(new InputStreamReader(is, "utf8"));
            Iterator<Object> iterator = props.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                PROPS_MAP.put(key, props.getProperty(key));
            }
        } catch (Exception e) {

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据key获取map中对应的值
    public static String getString(String key) {
        return PROPS_MAP.get(key);
    }

}
