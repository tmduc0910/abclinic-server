package com.abclinic.server.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/20/2020 2:52 PM
 */
public class NotificationDto {
    @JsonProperty("notification_id")
    private long notificationId;
    @JsonProperty("user_id")
    private long userId;
    private String notification;

    public NotificationDto(long notificationId, long userId, String notification) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.notification = notification;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
