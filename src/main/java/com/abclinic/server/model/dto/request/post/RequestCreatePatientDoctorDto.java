package com.abclinic.server.model.dto.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/14/2020 9:37 PM
 */
public class RequestCreatePatientDoctorDto {
    @JsonProperty("doctor_id")
    private long doctorId;
    @JsonProperty("inquiry_id")
    private long inquiryId;

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(long inquiryId) {
        this.inquiryId = inquiryId;
    }
}
