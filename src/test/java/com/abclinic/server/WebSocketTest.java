package com.abclinic.server;

import com.abclinic.server.constant.MessageType;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.InquiryRepository;
import com.abclinic.server.repository.UserRepository;
import com.abclinic.server.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author tmduc
 * @package testing
 * @created 2/26/2020 2:02 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketTest {
    private static final String URL = "ws://localhost:8109/api/ws";
    private Logger logger = LoggerFactory.getLogger(WebSocketTest.class);

    @Autowired
    NotificationService service;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Test
    public void testNoti() throws InterruptedException, ExecutionException, TimeoutException {
        User user = userRepository.findById(2).get();
        try {
            WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                    Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            StompSession session = stompClient
                    .connect(URL, new StompSessionHandlerAdapter() {
                    })
                    .get(1, SECONDS);
            session.subscribe("/topic/users/5", new CustomStompSessionHandler());
        } finally {
            service.makeNotification(userRepository.findById(5).get(), new NotificationMessage(MessageType.INQUIRE, user, inquiryRepository.findById(1).get()));
            new Scanner(System.in).nextLine();
        }
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
