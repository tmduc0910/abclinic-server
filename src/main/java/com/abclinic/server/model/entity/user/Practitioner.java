package com.abclinic.server.model.entity.user;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.RoleValue;
import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Practitioner")
public class Practitioner extends Doctor {
    private static final long serialVersionUID = 1L;

    @ManyToMany
    @JoinTable(
            name = "practitioner_specialty",
            joinColumns = @JoinColumn(name = "practitioner_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    @JsonView(Views.Public.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Set<Specialty> specialties;

    @OneToMany(targetEntity = Patient.class, mappedBy = "practitioner")
    @JsonView(Views.Private.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private List<Patient> patients;

    public Practitioner() {
    }

    public Practitioner(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber) {
        super(RoleValue.PRACTITIONER, name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Practitioner(String name, String email, int gender, LocalDate dateOfBirth, String password, String phoneNumber, String description) {
        super(RoleValue.PRACTITIONER, name, email, gender, dateOfBirth, password, phoneNumber, description);
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public void addPatients(Patient patient) {
        this.patients.add(patient);
    }

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

    public void addSpecialties(Specialty... specialties) {
        this.specialties.addAll(Arrays.asList(specialties));
    }

    @Override
    public String toString() {
        return "Đa khoa " + getId();
    }
}
