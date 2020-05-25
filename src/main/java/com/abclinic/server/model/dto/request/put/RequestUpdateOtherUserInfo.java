package com.abclinic.server.model.dto.request.put;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.put
 * @created 5/25/2020 9:51 AM
 */
public class RequestUpdateOtherUserInfo {
    private String name;
    private String dob;
    private int gender;
    private String email;
    private String phone;
    private String address = null;
    private String description = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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
