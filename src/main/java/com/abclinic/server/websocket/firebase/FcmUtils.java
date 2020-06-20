package com.abclinic.server.websocket.firebase;

import com.abclinic.server.model.dto.NotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket.firebase
 * @created 5/31/2020 3:52 PM
 */
public class FcmUtils {
    public static final String TOPIC = "users-";

    public static void pushNoti(long userId, NotificationDto notificationDto, long payloadId, int type, String image) {
        try {
            String json = new ObjectMapper().writeValueAsString(notificationDto);
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle("Tư vấn phòng khám")
                            .setBody(notificationDto.getNotification())
                            .setImage(image)
                            .build())
                    .putData("content", json)
//                    .putData("payloadId", String.valueOf(payloadId))
//                    .putData("type", String.valueOf(type))
                    .setTopic(TOPIC + userId)
                    .build();
            // Send a message to the devices subscribed to the provided topic.
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
