package com.sunrise.network.projects.bio;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/22 8:53 PM
 */
public class ConsoleUtils {
    private static String inputHead = "******";

    /**
     * 美化console字符串
     * @param rawStr
     * @return
     */
    public static String prettifyInput(String rawStr) {
        return inputHead+" "+rawStr+" "+inputHead;
    }
}
