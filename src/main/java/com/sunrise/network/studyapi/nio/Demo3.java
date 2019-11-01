package com.sunrise.network.studyapi.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.InflaterInputStream;

/**
 * @description:
 * nio 包下有：
 *          channels 包
 *          charset 包
 *          file 包
 *          。。。
 *          余下的都是类基本都是Buffer相关
 *
 *
 * @date: 2019/10/31
 * @author: lzhaoyang
 */
public class Demo3 {
    public static void main(String[] args) throws IOException {
        // Path用于来表示文件路径和文件
        Path path = Paths.get(".gitignore");
        System.out.println(path.getFileSystem());

        Path path1 = FileSystems.getDefault().getPath(".gitignore");

        //Files 和文件相关的各种操作
        //谷歌： java channel 在系统层面到底是什么
        // 资料 http://dawell.cc/2019/08/31/netty%E4%B8%8Enio%E6%8F%AD%E7%A7%98/
        System.out.println(Files.isReadable(Paths.get(".gitignore")));
        FileChannel fileChannel = null;
        try {
            fileChannel = FileChannel.open(path);
            ByteBuffer allocate = ByteBuffer.allocate(10);
            allocate.put("ac".getBytes());
            fileChannel.write(allocate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
