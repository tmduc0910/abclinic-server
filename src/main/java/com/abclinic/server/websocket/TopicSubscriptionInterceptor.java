package com.abclinic.server.websocket;

import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.entity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket
 * @created 6/17/2020 2:14 PM
 */
@Component
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    @Autowired
    private UserService userService;
    private Map<Long, String> map = new TreeMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String uid = accessor.getNativeHeader("Authorization").get(0);
                User user = userService.findByUID(uid).orElse(null);
                map.put(user.getId(), accessor.getSessionId());
            } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                if (!validate(accessor.getSessionId(), accessor.getDestination()))
                    throw new IllegalArgumentException();
            }
        } catch (NullPointerException ignored) {
        }
        return message;
    }

    private boolean validate(String session, String topic) {
        long id = Long.parseLong(topic.substring(topic.lastIndexOf("/") + 1));
        return map.entrySet().stream().filter(e -> e.getKey().equals(id)).findFirst().orElseThrow(NullPointerException::new)
                .getValue().equalsIgnoreCase(session);
    }
}
