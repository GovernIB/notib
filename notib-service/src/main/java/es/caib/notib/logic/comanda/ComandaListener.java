package es.caib.notib.logic.comanda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.caib.comanda.ms.broker.model.Avis;
import es.caib.comanda.ms.broker.model.AvisTipus;
import es.caib.comanda.ms.broker.model.Tasca;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.ProcSerHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import joptsimple.internal.Strings;
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
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class ComandaListener {

    private final ConfigHelper configHelper;
    private final IntegracioHelper integracioHelper;
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
    public void enviarAvisComanda(@Payload AvisDto avis, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var info = new IntegracioInfo(IntegracioCodi.COMANDA, "Enviament de tasca a comanda", IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Avis", avis.toString()));
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = configHelper.getConfig("es.caib.notib.plugin.comanda.url");
            if (url == null) {
                throw new Exception("La propietat \"es.caib.notib.plugin.comanda.url.base\" no pot ser null");
            }
            url += (url.charAt(url.length()-1) != '/' ? "/" : "") + "api/jms/avisos";
            var httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            var mapper = new ObjectMapper();

            var requestBody = mapper.writeValueAsString(getAvisComanda(avis));
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            NotibLogger.getInstance().info("[enviarAvisCommanda] Resposta: " + response.getBody(), log, LoggingTipus.COMANDA);
            integracioHelper.addAccioOk(info);
        } catch (Exception ex) {
            var msg = "Error al enviar l'avis a Commanda";
            integracioHelper.addAccioError(info, msg, ex);
            log.error("[enviarAvisComanda] " + msg, ex);
        }
    }

    private Avis getAvisComanda(AvisDto avis) {

        return Avis.builder()
                .appCodi("")
                .identificador(avis.getId() + "")
                .tipus(AvisTipus.INFO)
                .nom(avis.getAssumpte())
                .descripcio(avis.getMissatge())
                .dataInici(avis.getDataInici())
                .dataFi(avis.getDataFinal())
                .build();
    }

    private void enviarTascaComanda(Tasca tasca) throws Exception  {

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        var requestBody = mapper.writeValueAsString(tasca);
        NotibLogger.getInstance().info("[ComandaListener] Enviant tasca a la cua de tasques de Comanda " + tasca, log, LoggingTipus.COMANDA);
        jmsTemplate.convertAndSend(SmConstants.CUA_COMANDA_TASQUES, requestBody,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 0);
                    return m;
                });
    }


    @Transactional
    @JmsListener(destination = SmConstants.CUA_COMANDA_TASQUES, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void enviarTascaComanda(@Payload String tasca, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var info = new IntegracioInfo(IntegracioCodi.COMANDA, "Enviament de tasca a comanda", IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Tasca", tasca));
        String url;
        try {
            url = configHelper.getConfig("es.caib.notib.plugin.comanda.url");
            if (url == null) {
                throw new Exception("La propietat es.caib.notib.plugin.comanda.url.base no pot ser null");
            }
            url += (url.charAt(url.length()-1) != '/' ? "/" : "") + "api/jms/tasques";
            info.addParam("url", url);
        } catch (Exception ex) {
            var msg = "Error al enviar la tasca a Commanda";
            integracioHelper.addAccioError(info, msg, ex);
            log.error("[enviarTascaCommanda] " + msg, ex);
            return;
        }
        try {
            var httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            var username = configHelper.getConfig("es.caib.notib.plugin.comanda.usuari");
            var password = configHelper.getConfig("es.caib.notib.plugin.comanda.password");
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            httpHeaders.set("Authorization", authHeader);
            HttpEntity<String> requestEntity = new HttpEntity<>(tasca, httpHeaders);
            NotibLogger.getInstance().info("[enviarTascaCommanda] Enviant tasca a Comanda url " + url, log, LoggingTipus.COMANDA);
            var restTemplate = new RestTemplate();
             ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            NotibLogger.getInstance().info("[enviarTascaCommanda] Resposta: " + response.getBody(), log, LoggingTipus.COMANDA);
            integracioHelper.addAccioOk(info);
        } catch (Exception ex) {
            var msg = "Error al enviar la tasca a Commanda";
            integracioHelper.addAccioError(info, msg, ex);
            log.error("[enviarTascaCommanda] " + msg, ex);
        }
    }

    public void enviarTasca(NotificacioEnviamentEntity enviament) {

        try {
            var comandaActiva = configHelper.getConfigAsBoolean("es.caib.notib.plugin.comanda.actiu", false);
            if (!comandaActiva) {
                NotibLogger.getInstance().info("[ComandaListener] L'enviament a Comanda no esta actiu", log, LoggingTipus.COMANDA);
                return;
            }
            var entornCodi = configHelper.getConfig("es.caib.notib.plugin.comanda.entorn.codi");
            if (Strings.isNullOrEmpty(entornCodi)) {
                NotibLogger.getInstance().info("[ComandaListener] No s'ha definit un codi d'entorn", log, LoggingTipus.COMANDA);
                throw new Exception("La propietat \"es.caib.notib.plugin.comanda.entorn.codi\" no pot ser null");
            }
            var notificacio = enviament.getNotificacio();
            var permisos = procSerHelper.findUsuarisAndRolsAmbPermis(notificacio);
            var descripcio = notificacio.getDescripcio();
            var appBaseUrl = configHelper.getConfig("es.caib.notib.app.base.url");
            var redireccio = appBaseUrl + "/notificacio/"+notificacio.getId()+"/enviament/"+ enviament.getId();
            var estatDesc = EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus()) ? "Estat SIR: " + enviament.getRegistreEstat().name()
                            : "Estat Notifica: " + enviament.getNotificaEstat().name();
            estatDesc += enviament.getEntregaPostal() != null ? ", Estat CIE: " + (enviament.getEntregaPostal().getCieEstat() != null ? enviament.getEntregaPostal().getCieEstat() : "Pendent d'enviar al centre CIE" ) : "";
            var dataInici = notificacio.getCreatedDate().isPresent() ? Date.from(notificacio.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()) : null;
            var tasca =  Tasca.builder()
                .appCodi("NOT")
                .entornCodi(entornCodi)
                .identificador(enviament.getId() + "")
                .tipus(notificacio.getEnviamentTipus().name())
                .nom(notificacio.getConcepte())
                .descripcio(descripcio)
                .dataInici(dataInici)
                .dataFi(notificacio.getEstatProcessatDate())
                .dataCaducitat(notificacio.getCaducitat())
                .estat(enviament.getEstatPerComanda())
                .estatDescripcio(estatDesc)
                .numeroExpedient(notificacio.getNumExpedient())
                .responsable(notificacio.getUsuariCodi())
                .usuarisAmbPermis(permisos.getUsuarisAmbPermis())
                .grupsAmbPermis(permisos.getRolsAmbPermis())
                .redireccio(new URL(redireccio))
                .grup(notificacio.getGrupCodi())
                .build();
            enviarTascaComanda(tasca);
        } catch (Exception ex) {
            log.error("[ComandaListener] Error generant la tasca " );
        }
    }
}
