package es.caib.notib.logic.websocket;

import es.caib.notib.logic.intf.dto.missatges.MissatgeWs;
import es.caib.notib.logic.intf.websocket.WebSocketConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketJms {

    private final JmsTemplate jmsTemplate;

    public void enviarMissatge(MissatgeWs missatge) {

        jmsTemplate.convertAndSend(WebSocketConstants.CUA_WEBSOCKET, missatge);
    }
}
