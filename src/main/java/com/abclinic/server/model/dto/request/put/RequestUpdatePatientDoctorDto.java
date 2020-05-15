package com.abclinic.server.model.dto.request.put;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.put
 * @created 5/14/2020 9:38 PM
 */
public class RequestUpdatePatientDoctorDto {
    @JsonProperty("notification_id")
    private long notificationId;
    @JsonProperty("is_accepted")
    private boolean isAccepted;

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
