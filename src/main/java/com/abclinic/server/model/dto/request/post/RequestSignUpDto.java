package com.abclinic.server.model.dto.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request
 * @created 5/14/2020 8:36 PM
 */
public class RequestSignUpDto {
    private int role;
    private String email;
    private String password;
    private String name;
    private int gender;
    @JsonProperty("dob")
    private String dateOfBirth;
    private String phone;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public RequestSignUpDto() {
    }


    public RequestSignUpDto(int role, String email, String password, String name, int gender, String dateOfBirth, String phone) {
        this.role = role;
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
    }
}
