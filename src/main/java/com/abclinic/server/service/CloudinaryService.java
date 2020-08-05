package com.abclinic.server.service;

import com.abclinic.server.common.constant.CloudinaryConstant;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.common.utils.FileUtils;
import com.abclinic.server.model.entity.Image;
import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.http44.ApiStrategy;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 5/13/2020 8:10 PM
 */
@Service
public class CloudinaryService {
    @Value("${file.upload-dir}")
    private String uploadDirectory;
    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                CloudinaryConstant.KEY_CLOUD_NAME, CloudinaryConstant.CLOUD_NAME,
                CloudinaryConstant.KEY_API_KEY, CloudinaryConstant.API_KEY,
                CloudinaryConstant.KEY_API_SEC, CloudinaryConstant.API_SEC));
    }

    private Map upload(File file, String tag, boolean isAvatar) throws IOException {
        Map params = ObjectUtils.asMap(
                CloudinaryConstant.KEY_USE_FILENAME, CloudinaryConstant.USE_FILENAME,
                CloudinaryConstant.KEY_FOLDER, isAvatar ? CloudinaryConstant.AVA_FOLDER : CloudinaryConstant.IMG_FOLDER,
                CloudinaryConstant.KEY_TAGS, tag
        );
        return cloudinary.uploader().upload(file, params);
    }

    public Image uploadAvatar(MultipartFile file) throws IOException {
        Map res = upload(getTempFile(file), getTag(true), true);
        String fileName = getFileName(file);
        String fileType = getFileType(file);
        return new Image((String) res.get(CloudinaryConstant.KEY_URL), fileName, fileType);
    }

    public List<Image> uploadImages(MultipartFile[] files, String tag) throws IOException {
        List<Image> images = new ArrayList<>();
        Map res;
        for (MultipartFile f : files) {
            File file = getTempFile(f);
            res = upload(file, tag, false);
            images.add(new Image(
                    (String) res.get(CloudinaryConstant.KEY_URL),
                    getFileName(f),
                    getFileType(f)));
        }
        return images;
    }

    public String getTag(boolean isAvatar) {
        return isAvatar ? CloudinaryConstant.AVA_TAG : CloudinaryConstant.IMG_TAG
                + DateTimeUtils.getCurrent().toString()
                + Math.random() * 100000;
    }

    public File getTempFile(MultipartFile file) throws IOException {
        String fileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf('.'));
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        return FileUtils.toFile(fileName, fileType, file.getBytes(), uploadDirectory);
    }

    public String getFileName(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf('.'));
    }

    public String getFileType(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.') + 1);
    }

    public List<String> getImages(String tag) throws Exception {
        Api api = cloudinary.api();
        Map result = api.resourcesByTag(tag, ObjectUtils.emptyMap());
        return ((List<Map>) result.get(CloudinaryConstant.KEY_RESOURCES)).stream()
                .map(r -> (String) r.get(CloudinaryConstant.KEY_URL))
                .collect(Collectors.toList());
    }
}
