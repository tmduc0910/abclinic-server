package com.abclinic.server.model.dto;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 1/10/2020 2:14 PM
 */
public class AccountDto {
    private long id;
    private String uid;
    private String email;
    private String phone;
    private String password;
    private String name;

    public AccountDto(long id, String uid, String email, String phone, String password, String name) {
        this.id = id;
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
