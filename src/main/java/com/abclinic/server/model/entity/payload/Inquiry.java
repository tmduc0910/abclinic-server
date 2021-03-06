package com.abclinic.server.model.entity.payload;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.common.constant.PayloadStatus;
import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.abclinic.server.serializer.PrivateViewSerializer;
import com.abclinic.server.serializer.PublicViewSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inquiry")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Inquiry extends IPayloadIpml {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Patient patient;

    @JsonView(Views.Private.class)
    private String albumId;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = Chain.class, mappedBy = "inquiry")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = PublicViewSerializer.class)
    private Chain chain;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Reply.class, mappedBy = "inquiry")
    @JsonView(Views.Private.class)
    private List<Reply> replies;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = MedicalRecord.class, mappedBy = "inquiry")
    @JsonView(Views.Private.class)
    @JsonSerialize(using = PrivateViewSerializer.class)
    @Where(clause = "status = " + PayloadStatus.PROCESSED)
    private List<MedicalRecord> medicalRecords;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = DietRecord.class, mappedBy = "inquiry")
    @JsonView(Views.Private.class)
    @JsonSerialize(using = PrivateViewSerializer.class)
    @Where(clause = "status = " + PayloadStatus.PROCESSED)
    private List<DietRecord> dietRecords;

    @JsonView(Views.Abridged.class)
    private String content;

    @JsonView(Views.Abridged.class)
    private int type;

    @JsonView(Views.Private.class)
    private int status;

    @JsonView(Views.Public.class)
//    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private LocalDateTime date;

    @JsonView(Views.Abridged.class)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonView(Views.Abridged.class)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Inquiry() {
    }

    public Inquiry(Patient patient, String albumId, String content, int type, LocalDateTime date) {
        this.patient = patient;
        this.albumId = albumId;
        this.content = content;
        this.type = type;
        this.date = date;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<DietRecord> getDietRecords() {
        return dietRecords;
    }

    public void setDietRecords(List<DietRecord> dietRecords) {
        this.dietRecords = dietRecords;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public Chain getChain() {
        return chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }

    public boolean of(User user) {
        try {
            switch (user.getRole()) {
                case DIETITIAN:
                    return patient.getDietitians().contains(user);
                case SPECIALIST:
                    return patient.getSpecialists().contains(user);
                case PRACTITIONER:
                    return patient.getPractitioner().equals(user);
                case PATIENT:
                    return patient.equals(user);
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
