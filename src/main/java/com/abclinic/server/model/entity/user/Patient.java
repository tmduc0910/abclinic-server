package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.payload.IPayload;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "patient")
public class Patient extends User implements IPayload {
    private static final long serialVersionUID = 1L;

    @JsonView(Views.Public.class)
    private String address;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Inquiry.class, mappedBy = "patient")
    @JsonView(Views.Private.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private List<Inquiry> inquiries;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Practitioner practitioner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_disease",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "disease_id")
    )
    @JsonView(Views.Public.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Set<Disease> diseases;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    @JsonView(Views.Confidential.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
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

    public Set<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(Set<Disease> diseases) {
        this.diseases = diseases;
    }

    public void addDisease(Disease disease) {
        this.diseases.add(disease);
    }

    public void deleteDisease(Disease disease) {
        this.diseases.remove(disease);
    }

    public List<User> getSubDoctors() {
        return subDoctors;
    }

    public void setSubDoctors(List<User> subDoctors) {
        this.subDoctors = subDoctors;
    }

    @JsonView(Views.Private.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    public List<User> getSpecialists() {
        if (subDoctors != null)
            return subDoctors.stream()
                    .filter(u -> u.getRole().equals(Role.SPECIALIST))
                    .collect(Collectors.toList());
        return null;
    }

    @JsonView(Views.Private.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    public List<User> getDietitians() {
        if (subDoctors != null)
            return subDoctors.stream()
                    .filter(u -> u.getRole().equals(Role.DIETITIAN))
                    .collect(Collectors.toList());
        return null;
    }

    public void addSubDoc(User doctor) {
        this.subDoctors.add(doctor);
    }

    public boolean removeSubDoc(User doctor) {
        return this.subDoctors.remove(doctor);
    }
}
