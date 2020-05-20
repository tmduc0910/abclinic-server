package com.abclinic.server.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket
 * @created 2/27/2020 3:43 PM
 */
@Component
public class WebSocketPublisher {

    @Value("${server.port}")
    private String port;

    private WebSocketStompClient stompClient;

    @PostConstruct
    public void init() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    public StompSession getSession(String url) throws InterruptedException, ExecutionException {
        return stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();
    }

    public StompSession getSession() throws ExecutionException, InterruptedException {
        return getSession(getUri());
    }

    public String getUri() {
        String hostName = InetAddress.getLoopbackAddress().getHostName();
        return "http://" + hostName + ":" + port + "/api/ws";
    }
}
