package com.abclinic.server.websocket;

import com.abclinic.server.model.dto.NotificationDto;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import sun.plugin2.message.Message;

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
    @SendTo("/topic/public")
    public String sendMessage(@Payload String s) {
//        Gson gson = new Gson();
//        template.convertAndSend("/topic/users/" + notificationDto.getUserId(), gson.toJson(notificationDto));
        return s;
    }

    @SuppressWarnings("unused")
    @SubscribeMapping({ "/", "/chat", "/topic/users", "/messages", "/*" })
    public void listen(Message message, MessageHeaders headers, MessageHeaderAccessor accessor) throws Exception {
        int i = 0;
        System.out.println("subscribed");
    }
}
