package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.model.entity.payload.IPayload;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "patient")
public class Patient extends User implements IPayload {
    @JsonView(Views.Private.class)
    private String address;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Inquiry.class, mappedBy = "patient")
    @JsonView(Views.Private.class)
    @JsonSerialize(using = ViewSerializer.class)
    private List<Inquiry> inquiries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = ViewSerializer.class)
    private Practitioner practitioner;

    @ManyToMany(fetch = FetchType.LAZY)
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

    public List<Specialist> getSpecialists() {
        return subDoctors.stream()
                .filter(u -> u.getRole().equals(Role.SPECIALIST))
                .map(u -> (Specialist) u)
                .collect(Collectors.toList());
    }

    public List<Dietitian> getDietitians() {
        return subDoctors.stream()
                .filter(u -> u.getRole().equals(Role.DIETITIAN))
                .map(u -> (Dietitian) u)
                .collect(Collectors.toList());
    }

    public void addSubDoc(User doctor) {
        this.subDoctors.add(doctor);
    }

    public boolean removeSubDoc(User doctor) {
        return this.subDoctors.remove(doctor);
    }
}
