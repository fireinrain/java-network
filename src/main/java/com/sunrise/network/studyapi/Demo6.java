package com.sunrise.network.studyapi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/17 10:55 PM
 */
public class Demo6 {
    public static void main(String[] args) {
        //testUrlConnection();
        testMessageCode();
    }


    public static void testUrlConnection() {
        URL url = null;
        URLConnection urlConnection = null;
        try {
            url = new URL("http://www.baidu.com");
            urlConnection = url.openConnection();

            System.out.println("-----print all init request header------");
            Map<String, List<String>> requestProperties = urlConnection.getRequestProperties();
            for (Map.Entry<String,List<String>> e :requestProperties.entrySet() ) {
                System.out.println(e.getKey()+": "+e.getValue());
            }

            //打印http协议响应头
            System.out.println(urlConnection.getContentLength());
            System.out.println(urlConnection.getContentType());
            System.out.println(urlConnection.getContentEncoding());
            System.out.println(urlConnection.getHeaderField("Set-Cookie"));
            System.out.println("-----print all respond header-------");
            for (int i = 1; ; i++) {
                String headerFieldKey = urlConnection.getHeaderFieldKey(i);
                if (null==headerFieldKey){
                    break;
                }
                System.out.println(headerFieldKey+": "+urlConnection.getHeaderField(headerFieldKey));
            }


            try (InputStream inputStream = urlConnection.getInputStream()) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream);
                int c;
                while ((c = inputStreamReader.read()) != -1) {
                    System.out.print((char) c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void testMessageCode(){
        try {
            URL url = new URL("http://www.baidu.com");
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            System.out.println(urlConnection.getResponseCode());
            System.out.println(urlConnection.getResponseMessage());
            System.out.println(urlConnection.getHeaderField(0));
            System.out.println(urlConnection.getContentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
