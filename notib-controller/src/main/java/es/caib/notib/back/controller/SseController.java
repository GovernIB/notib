package es.caib.notib.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.logic.intf.dto.missatges.MissatgeWs;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.websocket.WebSocketConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@RestController
public class SseController extends BaseController {

    @Autowired
    AplicacioService aplicacioService;

    private final Map<Long, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();
    @Autowired
    private SessionScopedContext sessionScopedContext;

    @GetMapping("/{notificacioId}/sse-endpoint")
    public SseEmitter streamEvents(@PathVariable Long notificacioId) {

        var em = emitters.getOrDefault(notificacioId, new HashMap<>());
        var emitter = new SseEmitter(0L);
        var usuariCodi = sessionScopedContext.getUsuariActualCodi();
        em.remove(usuariCodi);
        em.put(usuariCodi, emitter);
        emitters.put(notificacioId, em);

//        emitter.onCompletion(() -> emitters.get(notificacioId).remove(usuariCodi));
//        emitter.onTimeout(() -> emitters.get(notificacioId).remove(usuariCodi));
//        emitter.onError(e -> emitters.get(notificacioId).remove(usuariCodi));

        return emitter;
    }

//    @JmsListener(destination = WebSocketConstants.CUA_WEBSOCKET, containerFactory = WebSocketConstants.JMS_FACTORY_ACK)
    public void sendEventToClient(@Payload MissatgeWs missatge, @Headers MessageHeaders headers, Message message) throws JMSException {

        message.acknowledge();
        var emits = emitters.get(missatge.getNotificacioId());
        if (emits == null || emits.isEmpty()) {
            return;
        }
        try {
            for (var emitter : emits.entrySet()) {
                emitter.getValue().send(SseEmitter.event().data(missatge));
            }
        } catch (IOException ex) {
            log.error("Emitter error al send ", ex);
            emitters.remove(missatge.getNotificacioId());
        }
    }
}