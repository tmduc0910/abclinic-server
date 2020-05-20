package com.abclinic.server.websocket;

import com.abclinic.server.model.dto.NotificationDto;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket
 * @created 5/20/2020 3:52 PM
 */

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/sendNoti")
    public void sendMessage(@Payload NotificationDto notificationDto) {
        Gson gson = new Gson();
        template.convertAndSend("/topic/users/" + notificationDto.getUserId(), gson.toJson(notificationDto));
    }
}
