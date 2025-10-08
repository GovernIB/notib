package es.caib.notib.back.config;

import es.caib.notib.back.websocket.WebSocketUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/notibws");
        config.setApplicationDestinationPrefixes("/notibback");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS(); //TODO VARIABLE PER ELS DIFERENTS ENTORNS
//        registry.addEndpoint("/websocket").setAllowedOrigins("/notibback").withSockJS().setWebSocketEnabled(false);
        registry.addEndpoint("/websocket")
//                .setAllowedOrigins("/notibback")
                .setAllowedOriginPatterns("/notibback")
//                .setHandshakeHandler(new WebSocketHandshakeHandler())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebSocketUserInterceptor());
    }
}

