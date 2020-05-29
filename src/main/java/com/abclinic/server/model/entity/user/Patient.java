package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.model.entity.payload.IPayload;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "patient")
public class Patient extends User implements IPayload {
    private static final long serialVersionUID = 1L;

    @JsonView(Views.Public.class)
    private String address;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Inquiry.class, mappedBy = "patient")
    @JsonView(Views.Private.class)
    @JsonSerialize(using = ViewSerializer.class)
    private List<Inquiry> inquiries;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = ViewSerializer.class)
    private Practitioner practitioner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    @JsonView(Views.Confidential.class)
    @JsonSerialize(using = ViewSerializer.class)
    private List<User> subDoctors;

    public Patient() {
    }

    public Patient(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.PATIENT, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Patient(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String address) {
        super(RoleValue.PATIENT, name, email, gender, dateOfBirth, password, phoneNumber);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Inquiry> getInquiries() {
        return inquiries;
    }

    public void setInquiries(List<Inquiry> inquiries) {
        this.inquiries = inquiries;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public List<User> getSubDoctors() {
        return subDoctors;
    }

    public void setSubDoctors(List<User> subDoctors) {
        this.subDoctors = subDoctors;
    }

    public List<User> getSpecialists() {
        return subDoctors.stream()
                .filter(u -> u.getRole().equals(Role.SPECIALIST))
                .collect(Collectors.toList());
    }

    public List<User> getDietitians() {
        return subDoctors.stream()
                .filter(u -> u.getRole().equals(Role.DIETITIAN))
                .collect(Collectors.toList());
    }

    public void addSubDoc(User doctor) {
        this.subDoctors.add(doctor);
    }

    public boolean removeSubDoc(User doctor) {
        return this.subDoctors.remove(doctor);
    }
}
