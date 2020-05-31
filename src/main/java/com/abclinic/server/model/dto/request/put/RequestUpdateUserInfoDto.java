package com.abclinic.server.model.dto.request.put;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.put
 * @created 5/15/2020 9:37 AM
 */
public class RequestUpdateUserInfoDto {
    private String email;
    private String phone;
    private String address = null;
    private String description = null;

    public RequestUpdateUserInfoDto() {
    }

    public RequestUpdateUserInfoDto(String email, String phone, String address, String description) {
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
