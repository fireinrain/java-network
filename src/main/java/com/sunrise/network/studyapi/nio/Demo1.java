package com.sunrise.network.studyapi.nio;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.file.*;
import java.util.List;

/**
 * @description: FileWatcher 文件监视
 * @date: 2019/10/28
 * @author: lzhaoyang
 */
public class Demo1 {
    public static void main(String[] args) {
        //testMyFileWatcher();
        testFileWatcher();
    }

    private static void testMyFileWatcher() {
        Path path = Paths.get("E:\\.");
        System.out.println("File watcher start at: " + path.getFileName());

        try {
            WatchService watchService = path.getFileSystem().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            WatchKey watchKey = watchService.take();
            while (true) {
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                for (WatchEvent<?> event : watchEvents) {
                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        System.out.println("file: " + event.context().toString() + " create here");
                        continue;
                    }
                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        System.out.println("file: " + event.context().toString() + " delete here");
                    }

                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testFileWatcher() {
        WatchService watchService = null;
        try {
            //获取文件监视服务
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("E:\\.");
            // 为指定的文件路径注册文件监视服务
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            //当事件被触发时
            while ((key = watchService.take()) != null) {
                // key.pollEvents() 会把所有触发的事件返回回来
                for (WatchEvent<?> event : key.pollEvents()) {
                    // event.kind()  返回的就是前面注册的事件
                    // event.context() 返回是那个文件触发的
                    System.out.println(
                            "Event kind:" + event.kind()
                                    + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

}

