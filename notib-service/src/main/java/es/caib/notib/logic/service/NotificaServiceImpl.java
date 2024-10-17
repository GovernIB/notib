package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.dto.missatges.MissatgeWs;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificaService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaNotificaDto;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import es.caib.notib.logic.websocket.WebSocketJms;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Service
public class NotificaServiceImpl implements NotificaService {

    private final EnviamentSmService enviamentSmService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final NotificacioService notificacioService;
    private final WebSocketJms webSocketJms;
    private final MessageHelper messageHelper;

    private static Map<Long, Integer> notificacionsExecutades = new HashMap<>();
    public static synchronized boolean haDeExecutar(Long notificacioId, Integer intent) {

        var ultimIntent = notificacionsExecutades.get(notificacioId);
        if (ultimIntent == null || ultimIntent < intent) {
            notificacionsExecutades.put(notificacioId, intent);
            return true;
        }
        return false;
    }

    public static void netejaExecucio(Long notificacioId) {
        notificacionsExecutades.remove(notificacioId);
    }

    @Transactional
    @Override
    public void enviarNotifica(String enviamentUuid, EnviamentNotificaRequest enviamentNotificaRequest) {

        NotificacioEntity notificacio = null;
        try {
            var enviamentEntity = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            notificacio = enviamentEntity.getNotificacio();
            var numIntent = enviamentNotificaRequest.getNumIntent();

            // Al notificar es processen tots els enviaments a l'hora. --> Notificacio
            // Per tant controlam que només s'executi el primer enviament de la notificació
            if (!haDeExecutar(notificacio.getId(), numIntent)) {
                return;
            }
            notificacioService.notificacioEnviar(notificacio.getId());
        } catch (Exception ex) {
            if (notificacio != null) {
                log.error("Error enviant a notifica la notificacio " + notificacio.getId());
            }

        }
    }

    @Transactional
    @Override
    public void enviarEvents(String enviamentUuid, String codiUsuari) {

        try {
//            var enviamentEntity = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            var env = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
//            var notificacio = enviamentEntity.getNotificacio();
            var notificacio = env.getNotificacio();
//            for (var env : notificacio.getEnviaments()) {
//                var enviamentSuccess = enviamentEntity.getNotificaEstatData() != null;
                var enviamentSuccess = env.getNotificaEstatData() != null;
                var event = enviamentSuccess ? EnviamentSmEvent.NT_SUCCESS : EnviamentSmEvent.NT_ERROR;

                var ok = true;
                var msg = "";
                switch (event) {
                    case NT_SUCCESS:
                        if (env.isPerEmail()) {
                            enviamentSmService.notificaFi(env.getNotificaReferencia());
                        } else {
                            enviamentSmService.notificaSuccess(env.getNotificaReferencia());
                        }
                        msg = "enviament.notifica.ok";
                        break;
                    case NT_ERROR:
                        enviamentSmService.notificaFailed(env.getNotificaReferencia());
                        msg = "enviament.notifica.error";
                        ok = false;
                        break;
                }
                msg = messageHelper.getMessage(msg);
                webSocketJms.enviarMissatge(MissatgeWs.builder().ok(ok).msg(msg).updateInfo(true).codiUsuari(codiUsuari).build());
//            }

            if (NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat()) ||
                    NotificacioEstatEnumDto.FINALITZADA.equals(notificacio.getEstat()) ||
                    NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(notificacio.getEstat())) {
                netejaExecucio(notificacio.getId());
            }
        } catch (Exception ex) {
            log.error("Error al enviar events un cop enviada la notificacio a Notifica");
        }
    }

    @Transactional
    @Override
    public boolean consultaEstatEnviament(ConsultaNotificaDto enviament) {

        try {
        // Consultar enviament a notifica
            notificacioService.enviamentRefrescarEstat(enviament.getId());
            var enviamentEntity = notificacioEnviamentRepository.findByUuid(enviament.getUuid()).orElseThrow();
            return enviamentEntity.getNotificaIntentNum() == 0;
        } catch (Exception ex) {
            log.error("Error en la consulta de l'estat de l'enviament " + enviament.getUuid());
            return false;
        }
    }
}
