package com.abclinic.server.model.entity.user;


import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Abridged.class)
    private long id;

    @JsonIgnore
    private String uid;

    @JsonView(Views.Abridged.class)
    private int role;

    @JsonView(Views.Abridged.class)
    private String name;

    @JsonView(Views.Public.class)
    private String email;

    @JsonView(Views.Public.class)
    private int gender;

    @Column(name = "dob")
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @JsonView(Views.Public.class)
    private LocalDate dateOfBirth;

    @JsonView(Views.Abridged.class)
    private int age;

    @JsonView(Views.Confidential.class)
    private String password;

    @JsonView(Views.Public.class)
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonView(Views.Abridged.class)
    private String avatar;

    @JsonView(Views.Abridged.class)
    private int status;

    @CreationTimestamp
    @JsonView(Views.Private.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Confidential.class)
    private LocalDateTime updatedAt;

    public User() {};

    public User(int role, String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        this.role = role;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.age = DateTimeUtils.getDistanceByYear(dateOfBirth);
        this.status = UserStatus.NEW.getValue();
    }

    public User(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.age = DateTimeUtils.getDistanceByYear(dateOfBirth);
        this.status = UserStatus.NEW.getValue();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Role getRole() {
        return Role.valueOf(role).get();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.age = DateTimeUtils.getDistanceByYear(dateOfBirth);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return this.getId() == ((User) obj).getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) this.getId();
    }
}
