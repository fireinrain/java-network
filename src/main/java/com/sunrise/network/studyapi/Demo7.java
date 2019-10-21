package com.sunrise.network.studyapi;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/19 10:13 PM
 */
public class Demo7 {
    public static void main(String[] args) {
        //testSocket();
        //testSocketOutput();

        testCloseHalfSocket();
    }

    //测试关闭socket的输入输出流
    //socket是双工的
    private static void testCloseHalfSocket() {
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            socket = new Socket("www.baidu.com", 80);
            socket.setSoTimeout(15 * 1000);

            outputStream = socket.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "8859_1"));
            bufferedWriter.write("GET / HTTP/1.1\n" +
                    "Host: www.baidu.com\n\n");
            bufferedWriter.flush();
            //及时关闭写数据
            socket.shutdownOutput();

            //读去返回的数据
            inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
            int c;
            StringBuilder stringBuilder = new StringBuilder();
            while ((c = bufferedReader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            System.out.println("server send data: " + stringBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void testSocketOutput() {
        String url = "dict.org";
        int port = 2628;
        int timeout = 15 * 1000;
        List<String> arrayList = Arrays.asList("age", "name", "people");

        Socket socket = null;
        try {
            socket = new Socket(url, port);
            socket.setSoTimeout(timeout);
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            //写入
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            //读取
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            for (String word : arrayList) {
                processWord(word, bufferedWriter, bufferedReader);
            }

            bufferedWriter.write("quit\r\n");
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 和socket 交互
    private static void processWord(String word, Writer writer, BufferedReader bufferedReader) throws IOException {
        writer.write("DEFINE eng-lat" + word + "\r\n");
        writer.flush();
        String c;
        while ((c = bufferedReader.readLine()) != null) {
            System.out.println(c);

            if (c.startsWith("5")) {
                return;
            }
        }
    }

    public static void testSocket() {
        try (Socket socket = new Socket("time.nist.gov", 13)) {
            socket.setSoTimeout(15 * 1000);

            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, "ASCII");
            int c;
            StringBuilder stringBuilder = new StringBuilder();
            while ((c = inputStreamReader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            System.out.println(stringBuilder.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
