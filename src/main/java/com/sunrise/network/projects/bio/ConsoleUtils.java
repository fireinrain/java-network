package com.sunrise.network.projects.bio;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
    private static List<String> chatRuleList = Arrays.asList("@ people-name/id your-message",
                                                            "# your-message",
                                                            "$ all",
                                                            "$ quit");

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
        System.out.println(prettifyServerLog(s) + date);
    }

    /**
     * 发送聊天规则给客户端
     */
    public static String sendClientChatRuleTable() {
        String rule = "********************************* Chat Rules *********************************";
        String rule2 = "*******************| message format   |   means          |********************";
        String rule3 = "*******************| # your-message   |   send public message|****************";
        String rule4 = "******| @ people-name/id your-message |   send private message|***************";
        String rule5 = "***************| $ all                |   get all auth people list|************";
        String rule6 = "***************| $ quit               |   get all auth people list|************";
        return rule + "\r\n" + rule2 + "\r\n" + rule3 + "\r\n" + rule4 + "\r\n" + rule5 + "\r\n" + rule6;
    }

    public static List<String> getChatRuleList() {
        return chatRuleList;
    }
}
