package com.abclinic.server.model.entity.payload;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity.payload
 * @created 7/23/2020 10:39 AM
 */
@Entity
@Table(name = "inquiry_chain")
public class Chain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Confidential.class)
    private long id;

    @JsonView(Views.Abridged.class)
    private long chainId;

    @JsonView(Views.Abridged.class)
    private int type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Inquiry inquiry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Inquiry prevInquiry = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private Inquiry nextInquiry = null;

    public Chain() {
    }

    public Chain(Inquiry inquiry) {
        this.chainId = 1;
        this.type = inquiry.getType();
        this.inquiry = inquiry;
        this.patient = inquiry.getPatient();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public Inquiry getPrevInquiry() {
        return prevInquiry;
    }

    public void setPrevInquiry(Inquiry prevInquiry) {
        this.prevInquiry = prevInquiry;
    }

    public Inquiry getNextInquiry() {
        return nextInquiry;
    }

    public void setNextInquiry(Inquiry nextInquiry) {
        this.nextInquiry = nextInquiry;
    }

    public Chain clone() {
        Chain c = new Chain();
        c.setChainId(chainId);
        c.setPatient(patient);
        c.setType(type);
        return c;
    }
}
