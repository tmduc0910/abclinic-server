package com.abclinic.server.websocket;

import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket
 * @created 2/24/2020 2:50 PM
 */

@Service
public class WebSocketService {
    @Autowired
    private SimpMessagingTemplate template;

    public String getTopicUrl(User user) {
        return "/topic/user/" +
                user.getId();
    }

    public void broadcast(String destination, Notification notification) {
        template.convertAndSend(destination, notification);
    }

    public void broadcast(User sender, Notification notification) {
        broadcast(getTopicUrl(sender), notification);
    }
}
