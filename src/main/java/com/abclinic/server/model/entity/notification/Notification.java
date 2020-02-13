package com.abclinic.server.model.entity.notification;

import com.abclinic.server.base.Views;
import com.abclinic.server.model.entity.user.User;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 1/11/2020 3:59 PM
 */
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Abridged.class)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    @JsonView(Views.Abridged.class)
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    @JsonView(Views.Private.class)
    private User receiver;

    @JsonView(Views.Private.class)
    private long payloadId;

    @JsonView(Views.Abridged.class)
    private String message;

    @JsonView(Views.Private.class)
    private int type;

    @JsonView(Views.Abridged.class)
    private boolean isRead;

    @CreationTimestamp
    @JsonView(Views.Abridged.class)
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(User sender, User receiver, String message, int type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public long getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(long payloadId) {
        this.payloadId = payloadId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
