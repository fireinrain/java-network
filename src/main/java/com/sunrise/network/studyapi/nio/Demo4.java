package com.sunrise.network.studyapi.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @description: 异步channel
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/20 11:00 PM
 */
public class Demo4 {
    static class LineHandle implements CompletionHandler<Integer,ByteBuffer>{


        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            attachment.flip();
            WritableByteChannel writableByteChannel = Channels.newChannel(System.out);
            try {
                writableByteChannel.write(attachment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println(exc.getStackTrace());
        }
    }
    public static void main(String[] args) {
        testAsyncServerSocketChannel();
        testAsyncSocketChannel();
        testAsyncSocketChannel2();
    }

    //处理方式2
    private static void testAsyncSocketChannel2() {
        try (AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open()) {
            asynchronousSocketChannel.connect(new InetSocketAddress("'127.0.0.1", 8888));
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            asynchronousSocketChannel.read(byteBuffer,byteBuffer,new LineHandle());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //处理方式1
    private static void testAsyncSocketChannel() {
        try (AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open()) {
            Future<Void> connect = asynchronousSocketChannel.connect(new InetSocketAddress("'127.0.0.1", 8888));
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //等待连接完成(这会阻塞当前线程)
            connect.get();

            Future<Integer> future = asynchronousSocketChannel.read(byteBuffer);

            //可以做其它事情

            //等待读取完成
            future.get();

            byteBuffer.flip();
            WritableByteChannel writableByteChannel = Channels.newChannel(System.out);
            writableByteChannel.write(byteBuffer);
        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void testAsyncServerSocketChannel() {

    }
}

