package com.abclinic.server.model.dto.request.post;

import com.google.gson.Gson;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request
 * @created 5/14/2020 8:19 PM
 */
public class RequestLoginDto {
    private String account;
    private String password;

    public RequestLoginDto() {
    }

    public RequestLoginDto(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public RequestLoginDto(String json) {
        Gson gson = new Gson();
        RequestLoginDto r = gson.fromJson(json, RequestLoginDto.class);
        this.account = r.getAccount();
        this.password = r.getPassword();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
