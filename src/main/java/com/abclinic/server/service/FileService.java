package com.abclinic.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 11/23/2019 4:20 PM
 */
@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDirectory;
    private Logger logger = LoggerFactory.getLogger(FileService.class);
//    private static FileService instance = null;
//
//    public static FileService getInstance() {
//        if (instance != null)
//            return instance;
//        return new FileService();
//    }
//
//    public String getUploadDirectory() {
//        return uploadDirectory;
//    }

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void deleteFilesScheduledTask() throws IOException {
        findFiles(uploadDirectory);
    }

    public void findFiles(String filePath) throws IOException {
        boolean canDelete = false;
        List<File> files = Files.list(Paths.get(filePath))
                .map(Path::toFile)
                .collect(Collectors.toList());
        for (File file: files) {
            if (file.isDirectory()) {
                findFiles(file.getAbsolutePath());
            } else if (isFileOld(file)){
                deleteFile(file);
                canDelete = true;
            }
        }
        if (canDelete)
            logger.info("Wiped out old upload data from ." + uploadDirectory);
    }

    public void deleteFile(File file) {
        file.delete();
    }

    public boolean isFileOld(File file) {
        LocalDate fileDate = Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate oldDate = LocalDate.now().minusDays(1);
        return fileDate.isBefore(oldDate);
    }
}