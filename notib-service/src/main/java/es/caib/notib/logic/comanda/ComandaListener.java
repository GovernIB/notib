package es.caib.notib.logic.comanda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.caib.comanda.ms.broker.model.Avis;
import es.caib.comanda.ms.broker.model.AvisTipus;
import es.caib.comanda.ms.broker.model.Tasca;
import es.caib.comanda.ms.broker.model.TascaEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ProcSerHelper;
import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import joptsimple.internal.Strings;
import liquibase.pro.packaged.S;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Component
public class ComandaListener {

    private final ConfigHelper configHelper;
    private final ProcSerHelper procSerHelper;
    private final JmsTemplate jmsTemplate;

    public void enviarAvisCommanda(AvisDto avis) {

        jmsTemplate.convertAndSend(SmConstants.CUA_COMANDA_AVISOS, avis,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 0);
                    return m;
                });
    }

    @Transactional
    @JmsListener(destination = SmConstants.CUA_COMANDA_AVISOS, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void enviarAvisCommanda(@Payload AvisDto avis, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = configHelper.getConfig("es.caib.notib.plugin.comanda.url");
            if (url == null) {
                throw new Exception("La propietat \"es.caib.notib.plugin.comanda.url.base\" no pot ser null");
            }
            url += (url.charAt(url.length()-1) != '/' ? "/" : "") + "api/cues/avisos";
            var httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            var mapper = new ObjectMapper();

            var requestBody = mapper.writeValueAsString(getAvisComanda(avis));
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            NotibLogger.getInstance().info("[enviarAvisCommanda] Resposta: " + response.getBody(), log, LoggingTipus.COMANDA);
        } catch (Exception ex) {
            log.error("Error al enviar l'avis a Commanda ", ex);
        }
    }

    private Avis getAvisComanda(AvisDto avis) {

        var dataInici = avis.getDataInici().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        var dataFi = avis.getDataFinal().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Avis.builder()
                .appCodi("")
                .identificador(avis.getId() + "")
                .tipus(AvisTipus.INFO)
                .nom(avis.getAssumpte())
                .descripcio(avis.getMissatge())
                .dataInici(dataInici)
                .dataFi(dataFi)
                .build();
    }

    private void enviarTascaCommanda(Tasca tasca) throws Exception  {

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        var requestBody = mapper.writeValueAsString(tasca);
        jmsTemplate.convertAndSend(SmConstants.CUA_COMANDA_TASQUES, requestBody,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 0);
                    return m;
                });
    }


    @Transactional
    @JmsListener(destination = SmConstants.CUA_COMANDA_TASQUES, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void enviarTascaCommanda(@Payload String tasca, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        try {
            String url = configHelper.getConfig("es.caib.notib.plugin.comanda.url");
            if (url == null) {
                throw new Exception("La propietat \"es.caib.notib.plugin.comanda.url.base\" no pot ser null");
            }
            url += (url.charAt(url.length()-1) != '/' ? "/" : "") + "api/cues/tasques";
            var httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            var username = configHelper.getConfig("es.caib.notib.plugin.comanda.usuari");
            var password = configHelper.getConfig("es.caib.notib.plugin.comanda.password");
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            httpHeaders.set("Authorization", authHeader);
            HttpEntity<String> requestEntity = new HttpEntity<>(tasca, httpHeaders);
            var restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            NotibLogger.getInstance().info("[enviarTascaCommanda] Resposta: " + response.getBody(), log, LoggingTipus.COMANDA);
        } catch (Exception ex) {
            log.error("[ComandaListener] Error al enviar la tasca a Commanda ", ex);
        }
    }

    public void enviarTasca(NotificacioEnviamentEntity enviament) {

        try {
            var comandaActiva = configHelper.getConfigAsBoolean("es.caib.notib.plugin.comanda.actiu", false);
            if (!comandaActiva) {
                return;
            }
            var entornCodi = configHelper.getConfig("es.caib.notib.plugin.comanda.entorn.codi");
            if (Strings.isNullOrEmpty(entornCodi)) {
                throw new Exception("La propietat \"es.caib.notib.plugin.comanda.entorn.codi\" no pot ser null");
            }
            var notificacio = enviament.getNotificacio();
            var permisos = procSerHelper.findUsuarisAndRolsAmbPermis(notificacio);
            var data = notificacio.getCaducitat();
            var caducitat = data != null ? new java.util.Date(data.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
            data = notificacio.getEstatProcessatDate();
//            var dataProcesada = data != null ? new java.util.Date(data.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
            var dataProcesada = data != null ? LocalDateTime.ofInstant(data.toInstant(), ZoneId.systemDefault()) : null;
            var descripcio = notificacio.getDescripcio();
            var appBaseUrl = configHelper.getConfig("es.caib.notib.app.base.url");
            var redireccio = appBaseUrl + "/notificacio/"+notificacio.getId()+"/enviament/"+ enviament.getId();
            var estatDesc = EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus()) ? "Estat SIR: " + enviament.getRegistreEstat().name()
                            : "Estat Notifica: " + enviament.getNotificaEstat().name();
            estatDesc += enviament.getEntregaPostal() != null ? ", Estat CIE: " + (enviament.getEntregaPostal().getCieEstat() != null ? enviament.getEntregaPostal().getCieEstat() : "Pendent d'enviar al centre CIE" ) : "";
            var tasca =  Tasca.builder()
                .appCodi("not")
                .entornCodi(entornCodi)
                .identificador(enviament.getId() + "")
                .tipus(notificacio.getEnviamentTipus().name())
                .nom(notificacio.getConcepte())
                .descripcio(descripcio)
                .dataInici(notificacio.getCreatedDate().get())
                .dataFi(dataProcesada)
                .dataCaducitat(caducitat)
                .estat(enviament.getEstatPerComanda())
                .estatDescripcio(estatDesc)
                .numeroExpedient(notificacio.getNumExpedient())
                .responsable(notificacio.getUsuariCodi())
                .usuarisAmbPermis(permisos.getUsuarisAmbPermis())
                .grupsAmbPermis(permisos.getRolsAmbPermis())
                .redireccio(new URL(redireccio))
                .grup(notificacio.getGrupCodi())
                .build();
            enviarTascaCommanda(tasca);
        } catch (Exception ex) {
            log.error("[ComandaListener] Error generant la tasca " );
        }
    }
}
