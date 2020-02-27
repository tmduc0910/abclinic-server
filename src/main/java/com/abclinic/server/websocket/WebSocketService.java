package com.abclinic.server.websocket;

import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket
 * @created 2/24/2020 2:50 PM
 */

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private WebSocketPublisher publisher;

    public String getTopicUrl(User user) {
        return "/topic/users/" +
                user.getId();
    }

    public void broadcast(String destination, String notification) {
//        template.convertAndSend(destination, notification.getBytes());

        try {
            publisher.getSession().send(destination, notification.getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(User sender, String notification) {
        broadcast(getTopicUrl(sender), notification);
    }
}
