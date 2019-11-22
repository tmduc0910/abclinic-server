package com.abclinic.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

public class FileStorageProperties {
    @Value("${file.upload-dir}")
    private String uploadDir;
    private static FileStorageProperties instance = null;

    public static FileStorageProperties getInstance() {
        if (instance != null)
            return instance;
        else return new FileStorageProperties();
    }

    public String getUploadDirectory() {
        return uploadDir;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDir = uploadDirectory;
    }
}
