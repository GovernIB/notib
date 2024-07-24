package es.caib.notib.back.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class WebSocketUserInterceptor implements ChannelInterceptor {

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {

            var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
                return message;
            }
            var raw = message.getHeaders().get(SimpMessageHeaderAccessor.USER_HEADER);
            if (raw instanceof PreAuthenticatedAuthenticationToken) {
                var usuari = ((PreAuthenticatedAuthenticationToken) raw).getName();
                accessor.setUser(new WebSocketUser(usuari));
            }
            return message;
        }
}
