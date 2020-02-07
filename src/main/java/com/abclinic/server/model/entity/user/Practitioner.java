package com.abclinic.server.model.entity.user;

import com.abclinic.server.base.Views;
import com.abclinic.server.constant.RoleValue;
import com.abclinic.server.model.entity.Specialty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Practitioner")
public class Practitioner extends Doctor {

    @ManyToMany
    @JoinTable(
            name = "practitioner_specialty",
            joinColumns = @JoinColumn(name = "practitioner_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    @JsonView(Views.Public.class)
    private Set<Specialty> specialties;

    @OneToMany(targetEntity = Patient.class, mappedBy = "practitioner")
    @JsonView(Views.Private.class)
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
}
