package com.sunrise.network.studyapi.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * @description: 非阻塞单文件Http服务器
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/19 11:17 PM
 */
public class NonBlockingSingleFileHttp {
    private ByteBuffer contentByteBuff;
    private int port = 80;

    public NonBlockingSingleFileHttp() {
    }

    /**
     * http header 头
     * <p>
     * data 数据
     *
     * @param contentByteBuff
     * @param encoding
     * @param MIMEType
     * @param port
     */
    public NonBlockingSingleFileHttp(ByteBuffer contentByteBuff, String encoding, String MIMEType, int port) {
        this.port = port;

        String header = "HTTP1.0 200 OK\r\n"
                + "Server: NonBlockingSingleFileHttp\r\n"
                + "Content-length: " + contentByteBuff.limit() + "\r\n"
                + "Content-type: " + MIMEType + "\r\n\r\n";
        byte[] headerBytes = header.getBytes();

        //所需要的缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(contentByteBuff.limit() + headerBytes.length);
        //将数据存入缓冲区
        byteBuffer.put(headerBytes);
        byteBuffer.put(contentByteBuff);
        //切换为读模式
        byteBuffer.flip();
        this.contentByteBuff = contentByteBuff;
    }

    //服务器运行入口
    public void run() throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket socket = serverSocketChannel.socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(this.port);
        socket.bind(inetSocketAddress);
        //开启非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //选择器
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //阻塞在select这个系统调用
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //迭代出来后 这个key需要及时被删除
                iterator.remove();
                try {
                    if (selectionKey.isAcceptable()) {
                        //如果是新的请求
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel clientChannel = serverSocketChannel1.accept();
                        clientChannel.configureBlocking(false);
                        // 因为是http文件服务器，大多是客户端请求，然后服务器响应，所以在这里是 注册客户端通道什么时候可以读这个事件
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("接收到请求："+clientChannel.socket().getRemoteSocketAddress());
                    } else if (selectionKey.isWritable()) {
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer attachment = (ByteBuffer) selectionKey.attachment();
                        //因为是非阻塞的，所以只要有数据就写入，就会返回
                        if (attachment.hasRemaining()) {
                            clientChannel.write(attachment);
                        } else {
                            //没有数据了
                            clientChannel.close();
                        }
                    } else if (selectionKey.isReadable()) {
                        //如果是客户端的套接字是可读的
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
                        clientChannel.read(byteBuffer);
                        //将通道切换为只写模式
                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                        selectionKey.attach(byteBuffer.duplicate());
                    }
                } catch (Exception e) {
                    selectionKey.cancel();
                    try {
                        selectionKey.channel().close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }
    }

    public void runTest() {
        String contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor("test.txt");
        Path path = FileSystems.getDefault().getPath("test.txt");
        try {
            byte[] readAllBytes = Files.readAllBytes(path);
            ByteBuffer byteBuffer = ByteBuffer.wrap(readAllBytes);
            //设置端口
            int port;
            port = Integer.parseInt("8888");
            if (port < 1 || port > 65535) {
                port = 80;
            }
            String encoding = "UTF-8";

            NonBlockingSingleFileHttp nonBlockingSingleFileHttp = new NonBlockingSingleFileHttp(byteBuffer, encoding, contentTypeFor, port);
            nonBlockingSingleFileHttp.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NonBlockingSingleFileHttp nonBlockingSingleFileHttp = new NonBlockingSingleFileHttp();
        nonBlockingSingleFileHttp.runTest();
        //if (args.length == 0) {
        //    System.out.println("Usage: java XXXXX file port mimetype");
        //    return;
        //}
        ////获取文件类型
        //String contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor(args[0]);
        //Path path = FileSystems.getDefault().getPath(args[1]);
        //try {
        //    byte[] readAllBytes = Files.readAllBytes(path);
        //    ByteBuffer byteBuffer = ByteBuffer.wrap(readAllBytes);
        //    //设置端口
        //    int port;
        //    port = Integer.parseInt(args[2]);
        //    if (port < 1 || port > 65535) {
        //        port = 80;
        //    }
        //    String encoding = "UTF-8";
        //    if (args.length > 2) {
        //        encoding = args[2];
        //    }
        //    NonBlockingSingleFileHttp nonBlockingSingleFileHttp = new NonBlockingSingleFileHttp(byteBuffer, encoding, contentTypeFor, port);
        //    nonBlockingSingleFileHttp.run();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }
}
