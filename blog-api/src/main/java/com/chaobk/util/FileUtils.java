package com.chaobk.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtils {
    /**
     * 压缩文件到指定路径
     * @param oriFilePath 要压缩的原文件路径
     * @param compressedFilePath 压缩后的文件存放路径
     * @throws IOException IO异常，不在catch模块捕捉，交给调用方自行处理
     */
    public static void compressFile(String oriFilePath, String compressedFilePath) throws IOException {
        File file = new File(oriFilePath);
        File zipFile = new File(compressedFilePath);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))){
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            int temp = 0;
            while ((temp = fileInputStream.read()) != -1) {
                zipOutputStream.write(temp);
            }
        }
        log.info("文件压缩完成");
    }

    public static void delFileByPath(String... paths) {
        if (paths == null) {
            return;
        }
        for (String path : paths) {
            File file = new File(path);
            del(file);
        }
    }

    private static void del(File file) {
        String filePath = file.getAbsolutePath();
        file.delete();
        log.info("{}文件已清除", filePath);
    }
}
