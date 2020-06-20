package com.abclinic.server.websocket.security;

import com.abclinic.server.websocket.TopicSubscriptionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.inject.Inject;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket.security
 * @created 6/2/2020 10:09 AM
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {
    @Inject
    private AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

    @Autowired
    private TopicSubscriptionInterceptor topicSubscriptionInterceptor;

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // Endpoints are already registered on WebSocketConfig, no need to add more.
    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
//        registration.interceptors(authChannelInterceptorAdapter);
        registration.interceptors(topicSubscriptionInterceptor);
    }
}