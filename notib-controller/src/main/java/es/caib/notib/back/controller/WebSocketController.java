package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.missatges.MissatgeWs;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.websocket.WebSocketConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
@Controller
public class WebSocketController extends BaseController {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @Autowired
    private NotificacioService notificacioService;

    @Autowired
    public WebSocketController(SimpMessagingTemplate template, SimpUserRegistry simpUserRegistry) {
        this.template = template;
        this.simpUserRegistry = simpUserRegistry;
    }

//    @MessageMapping("/send")
//    @SendTo("/topic/messages")
//    public String send(final String message) throws Exception {
//
//        log.info("message" + message);
//        return "foo";
//    }

//    @GetMapping("/alta/websocket")
//    @ResponseBody
//    @SendTo("/topic/messages")
//    public String altaWebsocket(HttpServletRequest request) throws Exception {
//        return "hola websocket";
//    }

    @JmsListener(destination = WebSocketConstants.CUA_WEBSOCKET, containerFactory = WebSocketConstants.JMS_FACTORY_ACK)
    public void enviarMissatge(@Payload MissatgeWs missatge, @Headers MessageHeaders headers, Message message) throws JMSException {

        if (Strings.isNullOrEmpty(missatge.getCodiUsuari()) || simpUserRegistry.getUser(missatge.getCodiUsuari()) == null) {
            message.acknowledge();
            return;
        }
        template.convertAndSendToUser(missatge.getCodiUsuari(), "/notibws/missatge", missatge);
        message.acknowledge();
    }
//
//    @JmsListener(destination = WebSocketConstants.CUA_WEBSOCKET, containerFactory = WebSocketConstants.JMS_FACTORY_ACK)
//    public void fireGreetings(SimpMessageHeaderAccessor sha, @Payload Missatge missatge, @Headers MessageHeaders headers, Message message) throws JMSException {
//
//        log.info("fire greeetings");
//        template.convertAndSend("/notibws/notificacio/info", missatge);
//        message.acknowledge();
//    }


}
