package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.payload.IPayloadIpml;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:21 PM
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Record extends IPayloadIpml {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inquiry_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = ViewSerializer.class)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = ViewSerializer.class)
    private User doctor;

    @JsonView(Views.Abridged.class)
    private int recordType;

    @JsonView(Views.Abridged.class)
    private int status;

    @JsonView(Views.Abridged.class)
    private String note;

    @JsonView(Views.Abridged.class)
    private String prescription;

    @CreationTimestamp
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = ViewSerializer.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime updatedAt;

    public Record() {

    }

    public Record(Inquiry inquiry, User doctor, int recordType, String note, String prescription) {
        this.inquiry = inquiry;
        this.doctor = doctor;
        this.recordType = recordType;
        this.note = note;
        this.prescription = prescription;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
