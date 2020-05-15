package com.abclinic.server.model.dto.request.post;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/14/2020 9:24 PM
 */
public class RequestCreateImageDto {
    private MultipartFile[] files;

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}
