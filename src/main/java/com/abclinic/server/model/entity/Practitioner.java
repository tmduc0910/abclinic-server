package com.abclinic.server.model.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Practitioner")
public class Practitioner extends Doctor {

    @OneToMany(targetEntity = Patient.class, mappedBy = "practitioner")
    private List<Patient> patients;

    @OneToMany(targetEntity = Specialist.class, mappedBy = "practitioner")
    private List<Specialist> specialists;

    @ManyToMany
    @JoinTable(
            name = "practitioner_specialty",
            joinColumns = @JoinColumn(name = "practitioner_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private List<Specialty> specialties;

    public Practitioner() {
    }

    public Practitioner(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber) {
        super(name, email, gender, dateOfBirth, password, phoneNumber);
    }

    public Practitioner(String name, String email, int gender, Date dateOfBirth, String password, String phoneNumber, String description, int experience) {
        super(name, email, gender, dateOfBirth, password, phoneNumber, description, experience);
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

    public List<Specialist> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(List<Specialist> specialists) {
        this.specialists = specialists;
    }

    public void addSpecialist(Specialist specialist) {
        this.specialists.add(specialist);
    }

    public List<Specialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<Specialty> specialties) {
        this.specialties = specialties;
    }
}
