package com.abclinic.server.model.dto.request.post;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/14/2020 9:26 PM
 */
public class RequestCreateAvatarDto {
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
