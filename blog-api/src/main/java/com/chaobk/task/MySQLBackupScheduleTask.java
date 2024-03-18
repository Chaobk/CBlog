package com.chaobk.task;

import com.chaobk.util.FileUtils;
import com.chaobk.util.upload.UploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MySQLBackupScheduleTask {

    private final DataSourceProperties dataSourceProperties;
    private final UploadUtils uploadUtils;

    /**
     * 备份文件的存放目录
     */
    @Value("${mysql.data.backup-path}")
    private String backupDir;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

    /**
     * 定时任务，每周一凌晨四点，备份MySQL的数据
     * 备份逻辑：
     * 1.mysql数据备份到文件
     * 2.备份文件压缩
     * 3.压缩文件上传到OSS
     * 4.残留文件清理
     * 5.备份结果的邮件通知 //TODO
     * 6.适应化改造，改成类似NBlog中的定时任务 //TODO
     */
    @Scheduled(cron = "0 0 4 * * 1")
    public void backUpMySQLData() {
        checkDir(backupDir);
        String dateFormat = simpleDateFormat.format(new Date());
        String fileName = String.format("cblog-%s.sql", dateFormat);
        String compressedFileName = fileName + ".zip";
        String dataPath = backupDir + File.separator + fileName;
        String compressedFilePath = backupDir + File.separator + compressedFileName;
        try {
            log.debug("mysql备份开始");
            // 1.mysql数据备份
            backupData(dataPath);
            // 2.文件压缩
            FileUtils.compressFile(dataPath, compressedFilePath);
            // 3.上传到OSS
            String uploadLink = UploadUtils.upload(compressedFilePath);
            log.info("备份文件({})已上传至OSS({})", compressedFilePath, uploadLink);
            // 4.清除残留文件
            FileUtils.delFileByPath(dataPath, compressedFilePath);
        } catch (IOException e) {
            log.error("mysql数据备份失败");
            log.error(e.getMessage());
        }
    }

    /**
     * MySQL数据备份
     *
     * @param dataPath 备份文件的保存路径
     * @throws IOException
     */
    private void backupData(String dataPath) throws IOException {
        long start = System.currentTimeMillis();
        String cmd = String.format("docker exec mysql mysqldump -u%s -p%s cblog > %s", dataSourceProperties.getUsername(), dataSourceProperties.getPassword(), dataPath);
        String[] cmds = {"sh", "-c", cmd};
        log.debug("欲执行命令：{}", cmd);
        try (InputStream inputStream = Runtime.getRuntime().exec(cmds).getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
            String line = bufferedReader.readLine();
            while (line != null) {
                log.debug(line);
                line = bufferedReader.readLine();
            }
        }
        long end = System.currentTimeMillis();
        log.info("mysql备份命令执行成功,耗时：{}ms", end - start);
    }

    private void checkDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
