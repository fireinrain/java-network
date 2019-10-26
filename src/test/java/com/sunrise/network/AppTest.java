package com.sunrise.network;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @date: 2019/10/22
 * @author: lzhaoyang
 */
public class AppTest {

    @Test
    public void testBrowserGoogle() {
        Pattern pattern = Pattern.compile("^@ (\\w+|\\d+) (.+)");
        Matcher matcher = pattern.matcher("# xxxx hello");
        if (matcher.matches()){
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }
}