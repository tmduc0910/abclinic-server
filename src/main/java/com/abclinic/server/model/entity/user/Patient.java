package com.abclinic.server.model.entity.user;

import com.abclinic.server.constant.Role;
import com.abclinic.server.constant.RoleValue;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "patient")
public class Patient extends User {
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dietitian_id")
    private Dietitian dietitian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    private Practitioner practitioner;

    public Patient() {
    }

    public Patient(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.PATIENT, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Patient(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber, String address) {
        super(RoleValue.PATIENT, name, email, gender, dateOfBirth, password, phoneNumber);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Dietitian getDietitian() {
        return dietitian;
    }

    public void setDietitian(Dietitian dietitian) {
        this.dietitian = dietitian;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }
}
