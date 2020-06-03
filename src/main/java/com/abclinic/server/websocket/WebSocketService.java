package com.abclinic.server.websocket;

import com.abclinic.server.model.dto.NotificationDto;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.websocket.firebase.FcmUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;

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
    private Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    public String getTopicUrl(User user) {
        return "/topic/users/" +
                user.getId();
    }

    public void broadcast(String destination, NotificationDto notification) {
        try {
            Gson gson = new Gson();
//            publisher.getSession().send("/api/app/sendNoti", gson.toJson(notification).getBytes());
            publisher.getSession().send(destination, gson.toJson(notification).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Gson gson = new Gson();
//        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
//                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
////        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        try {
//            StompSession session = stompClient
//                    .connect(publisher.getUri(), new StompSessionHandlerAdapter() {
//                    })
//                    .get(1, SECONDS);
//            session.subscribe("/topic/users/6", new CustomStompSessionHandler());
//            session.send(destination, gson.toJson(notification));
//            session.send("/app/sendNoti", gson.toJson(notification));
//        } catch (Exception e) {
//
//        }
    }

    public void broadcast(User receiver, Notification notification, String message) {
        NotificationDto n = new NotificationDto(notification.getId(), notification.getReceiver().getId(), message, notification.getType());
        broadcast(getTopicUrl(receiver), n);
        FcmUtils.pushNoti(receiver.getId(), n, notification.getPayloadId(), notification.getType());
    }

    private class CustomStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            logger.info("CAUGHT");
            logger.info((new String((byte[]) o)));
        }
    }
}
