package com.abclinic.server.common.constant;

/**
 * @author tmduc
 * @package com.abclinic.server.common.constant
 * @created 4/23/2020 8:06 PM
 */
public enum FilterConstant {
    NAME("name"),
    AGE("age"),
    GENDER("gender"),
    STATUS("status"),
    DESCRIPTION("description"),
    PRACTITIONER("practitioner.id"),
    SPECIALIST("subDoctors"),
    DIETITIAN("subDoctors"),
    SPECIALTY("specialty"),
    EXPERIENCE("experience"),
    ROLE("role"),
    SCHEDULE_PAT_ID("patient.id"),
    SCHEDULE_PAT_NAME("patient.name"),
    VAL_PAT_ID("schedule.patient.id"),
    VAL_PAT_NAME("schedule.patient.name"),
    VAL_INDEX_ID("schedule.index.id"),
    VAL_SCHEDULE_ID("schedule.id"),
    VAL_INDEX_NAME("schedule.index.name"),
    VAL_DOC_ID("schedule.doctor.id"),
    VAL_PRAC_ID("schedule.patient.practitioner.id"),
    TYPE("type"),
    INQUIRY_ID("inquiry.id"),
    MED_INQUIRY_PAT("inquiry.patient.id"),
    DIET_INQUIRY_PAT("inquiry.patient.id"),
    INQUIRY_PAT_PRAC_ID("inquiry.patient.practitioner.id"),
    CHAIN_INQUIRY_ID("inquiry.id"),
    CHAIN_PATIENT_ID("inquiry.patient.id"),
    CHAIN_PRACTITIONER_ID("inquiry.patient.practitioner.id"),
    CHAIN_SUBDOCTOR("inquiry.patient.subDoctors");

    private final String value;

    FilterConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
