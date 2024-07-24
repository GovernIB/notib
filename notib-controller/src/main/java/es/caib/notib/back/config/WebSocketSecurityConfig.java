package es.caib.notib.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;

//TODO implementar seguretat en els WebSockets
//@Configuration
public class WebSocketSecurityConfig {
//
//    @Bean
//    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//        messages.nullDestMatcher().authenticated()
//                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
//                .simpDestMatchers("/app/**").hasRole("USER")
//                .simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER")
//                .simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll()
//                .anyMessage().denyAll();
//
//        return messages.build();
//    }
}
