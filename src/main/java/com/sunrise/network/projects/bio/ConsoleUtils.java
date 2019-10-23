package com.sunrise.network.projects.bio;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/22 8:53 PM
 */
public class ConsoleUtils {
    private static String inputHead = "****** # ******";
    private static String serverLogHead = "|---------- # ----------|";

    /**
     * 美化console字符串
     *
     * @param rawStr
     * @return
     */
    public static String prettifyInput(String rawStr) {
        return inputHead.replace("#", rawStr).trim();
    }

    /**
     * 美化console字符串，提供模板
     *
     * @param formatTpl
     * @param rawStr
     * @return
     */
    public static String prettifyInput(String formatTpl, String rawStr) {
        return formatTpl.replace("#", rawStr).trim();
    }

    /**
     * 美化服务端的console输出
     *
     * @param rawStr
     * @return
     */
    public static String prettifyServerLog(String rawStr) {
        return serverLogHead.replace("#", rawStr).trim();

    }

    /**
     * 美化服务端的console输出,提供模板
     *
     * @param formatTpl
     * @param rawStr
     * @return
     */
    public static String prettifyServerLog(String formatTpl, String rawStr) {
        return formatTpl.replace("#", rawStr).trim();

    }

    /**
     * 服务端日志输出
     *
     * @param s
     */
    public static void log(String s) {
        String date = LocalDateTime.now().toString();
        System.out.println(prettifyServerLog(s)+date);
    }
}
