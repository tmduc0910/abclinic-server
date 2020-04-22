package com.abclinic.server.model.entity.notification;

import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.model.entity.payload.Payload;
import com.abclinic.server.model.entity.user.User;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 2/10/2020 2:35 PM
 */
public class NotificationMessage {
    private final MessageType messageType;
    private User targetUser;
    private Payload payload;

    public NotificationMessage(MessageType messageType, Payload payload) {
        this.messageType = messageType;
        this.payload = payload;
    }

    public NotificationMessage(MessageType messageType, User targetUser, Payload payload) {
        this.messageType = messageType;
        this.targetUser = targetUser;
        this.payload = payload;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
