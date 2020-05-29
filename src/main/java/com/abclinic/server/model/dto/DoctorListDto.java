package com.abclinic.server.model.dto;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.user.Dietitian;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.Specialist;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/5/2020 2:18 PM
 */
public class DoctorListDto {
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Patient patient;

    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Practitioner practitioner;

    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private List<Specialist> specialists;

    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private List<Dietitian> dietitians;

    public DoctorListDto(Patient patient, List<Specialist> specialists, List<Dietitian> dietitians) {
        this.patient = patient;
        this.practitioner = patient.getPractitioner();
        this.specialists = specialists;
        this.dietitians = dietitians;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    public List<Specialist> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(List<Specialist> specialists) {
        this.specialists = specialists;
    }

    public List<Dietitian> getDietitians() {
        return dietitians;
    }

    public void setDietitians(List<Dietitian> dietitians) {
        this.dietitians = dietitians;
    }
}
