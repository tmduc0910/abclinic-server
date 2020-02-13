package com.abclinic.server.model.entity.payload.record;

import com.abclinic.server.base.Views;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.payload.Payload;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:21 PM
 */
@Entity
@Table(name = "record")
@Inheritance(strategy = InheritanceType.JOINED)
public class Record extends Payload {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inquiry_id")
    @JsonView(Views.Abridged.class)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "practitioner_id")
    @JsonView(Views.Abridged.class)
    private Practitioner practitioner;

    @JsonView(Views.Confidential.class)
    private int recordType;

    @JsonView(Views.Confidential.class)
    private int status;

    @CreationTimestamp
    @JsonView(Views.Abridged.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonView(Views.Public.class)
    private LocalDateTime updatedAt;

    public Record() {

    }

    public Record(Inquiry inquiry, Practitioner practitioner, int recordType) {
        this.inquiry = inquiry;
        this.practitioner = practitioner;
        this.recordType = recordType;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
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
