package com.sunrise.network.projects.bio;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

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
     * 支持的功能
     * 1. 创建聊天群
     * 2. 支持他人申请进入聊天群
     * 3  支持群主主动踢人
     * 4  支持主动退群
     * 5  支持在线搜索群
     * 6  支持在线搜索人
     * 7  支持发送点对点私密消息
     */
    private static List<Pattern> chatRuleList = Arrays.asList(
            Pattern.compile("^@ (\\w+|\\d+) (.+)$"),
            Pattern.compile("^@ all (.+)$"),
            Pattern.compile("^# all$"),
            Pattern.compile("^# quit$"));

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
        String rule3 = "*******************| @ your-message   |   send public message|****************";
        String rule4 = "******| @ people-name/id your-message |   send private message|***************";
        String rule5 = "***************| # all                |   get all auth people list|************";
        String rule6 = "***************| # quit               |   get all auth people list|************";
        return rule + "\r\n" + rule2 + "\r\n" + rule3 + "\r\n" + rule4 + "\r\n" + rule5 + "\r\n" + rule6;
    }

    /**
     * 获取聊天规则列表
     *
     * @return
     */
    public static List<Pattern> getChatRuleList() {
        return chatRuleList;
    }

    /**
     * 获取所有在线客户，并组成字符串
     *
     * @param hashMap
     * @param myKey
     * @return
     */
    public static String getAllAuthClient(Enumeration<String> hashMapKeys, String myKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("------(id  ////  nickName)------");
        stringBuilder.append("\r\n");

        ArrayList<String[]> idAndNameList = new ArrayList<>();
        while (hashMapKeys.hasMoreElements()) {
            String[] idAndName = hashMapKeys.nextElement().split("&");
            idAndNameList.add(idAndName);

        }
        idAndNameList.sort(Comparator.comparingInt(o -> Integer.parseInt(o[0])));
        for (String[] key : idAndNameList) {
            stringBuilder.append("|   ");
            stringBuilder.append(key[0]);
            stringBuilder.append("  ");
            stringBuilder.append("////  ");
            stringBuilder.append(key[1]);
            if (key[1].equals(myKey.split("&")[1].trim())) {
                stringBuilder.append(" (****)");
            }
            stringBuilder.append("    |");
            stringBuilder.append("\r\n");
        }
        stringBuilder.append("---------------------");
        return stringBuilder.toString();
    }
}
