package com.abclinic.server.model.dto.request.post;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/15/2020 9:23 AM
 */
public class RequestCreateSpecialtyDto {
    private String name;
    private String detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
