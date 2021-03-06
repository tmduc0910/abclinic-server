package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.payload.IPayloadIpml;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.abclinic.server.serializer.PublicViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = PublicViewSerializer.class)
    private User doctor;

    @Column(name = "record_type")
    @JsonView(Views.Abridged.class)
    private int type;

    @JsonView(Views.Abridged.class)
    private int status;

    @JsonView(Views.Abridged.class)
    private String note;

    @JsonView(Views.Abridged.class)
    private String prescription;

    @CreationTimestamp
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime updatedAt;

    public Record() {

    }

    public Record(Inquiry inquiry, User doctor, int type, String note, String prescription) {
        this.inquiry = inquiry;
        this.doctor = doctor;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
