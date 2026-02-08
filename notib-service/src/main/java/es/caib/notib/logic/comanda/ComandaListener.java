package es.caib.notib.logic.comanda;

import es.caib.comanda.api.client.v1.ComandaClient;
import es.caib.comanda.model.v1.avis.Avis;
import es.caib.comanda.model.v1.avis.AvisTipus;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.ProcSerHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.missatges.MissatgeComanda;
import es.caib.notib.logic.intf.util.DatesUtils;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
    private final NotificacioEnviamentRepository enviamentRepository;

    private static final String APP_CODI = "NOT";

    private boolean isComandaActiva() {

        var comandaActiva = configHelper.getConfigAsBoolean("es.caib.notib.plugin.comanda.actiu", false);
        if (!comandaActiva) {
            NotibLogger.getInstance().info("[ComandaListener] L'enviament a Comanda no esta actiu", log, LoggingTipus.COMANDA);
            return false;
        }
        return true;
    }

    private String getEntornCodi() throws Exception{

        var entornCodi = configHelper.getConfig("es.caib.notib.plugin.comanda.entorn.codi");
        if (Strings.isNullOrEmpty(entornCodi)) {
            NotibLogger.getInstance().info("[ComandaListener] No s'ha definit un codi d'entorn", log, LoggingTipus.COMANDA);
            throw new Exception("La propietat \"es.caib.notib.plugin.comanda.entorn.codi\" no pot ser null");
        }
        return entornCodi;
    }

    public void enviarAvis(NotificacioEnviamentEntity enviament, AvisTipus tipus) {

        try {
            if (!isComandaActiva()) {
                return;
            }
            enviarMissatgeComanda(MissatgeComanda.builder()
                    .id(enviament.getId())
                    .tipus(tipus)
                    .build());
        } catch (Exception ex) {
            log.error("[ComandaListener.avisEnviament] Error enviant l'avis", ex);
        }
    }

    private void enviarMissatgeComanda(MissatgeComanda missatge) throws Exception {

        NotibLogger.getInstance().info("[ComandaListener] Enviant missatge a la cua de tasques de Comanda " + missatge, log, LoggingTipus.COMANDA);
        jmsTemplate.convertAndSend(SmConstants.CUA_COMANDA_AVISOS, missatge);
    }

    @Transactional
    @JmsListener(destination = SmConstants.CUA_COMANDA_AVISOS, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void enviarAvisComanda(@Payload MissatgeComanda missatgeComanda, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        Avis avis = null;
        try {
            var enviament = enviamentRepository.findById(missatgeComanda.getId()).orElseThrow();
            var tipus = missatgeComanda.getTipus();
            var entornCodi = getEntornCodi();
            var notificacio = enviament.getNotificacio();
            var dataInici = enviament.getCreatedDate().isPresent() ? Date.from(enviament.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()) : null;
            var tipusEnviament = notificacio.getEnviamentTipus();
            Date dataFi = null;
            var isSir = !EnviamentTipus.SIR.equals(tipusEnviament);
            if (isSir && enviament.isNotificaEstatFinal()) {
                dataFi = enviament.getNotificaEstatData();
            } else if(isSir) {
                dataFi = enviament.getSirRegDestiData();
            }
            var desc = " Tipus: " + tipusEnviament + "."
                    + " Estat " + (!EnviamentTipus.SIR.equals(tipusEnviament) ? enviament.getNotificaEstat() : enviament.getRegistreEstat());
            var permisos = procSerHelper.findUsuarisAndRolsAmbPermis(notificacio);
            var appBaseUrl = configHelper.getConfig("es.caib.notib.app.base.url");
            var redireccio = appBaseUrl + "/notificacio/" + notificacio.getId() + "/enviament/" + enviament.getId();

            avis = Avis.builder()
                    .appCodi(APP_CODI)
                    .entornCodi(entornCodi)
                    .identificador(enviament.getNotificaReferencia())
                    .nom(notificacio.getConcepte())
                    .dataInici(dataInici != null ? DatesUtils.toOffsetDateTime(dataInici) : null)
                    .dataFi(dataFi != null ? DatesUtils.toOffsetDateTime(dataFi) : null)
                    .descripcio(desc)
                    .tipus(tipus)
                    .responsable(notificacio.getUsuariCodi())
                    .usuarisAmbPermis(permisos.getUsuarisAmbPermis())
                    .grupsAmbPermis(permisos.getRolsAmbPermis())
                    .redireccio(new URL(redireccio))
                    .grup(notificacio.getGrupCodi())
                    .build();
        } catch (Exception ex) {
            log.error("[enviarAvisComanda] Error reconstruint l'avis de Comanda", ex);
            return;
        }

        var info = new IntegracioInfo(IntegracioCodi.COMANDA, "Enviament d'avis a comanda", IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Avis", avis.toString()));
        String url;
        try {
            url = configHelper.getConfig("es.caib.notib.plugin.comanda.url");
            if (Strings.isNullOrEmpty(url )) {
                throw new Exception("La propietat es.caib.notib.plugin.comanda.url.base no pot ser null");
            }
            url = url.endsWith("/") ? url.substring(0,url.length()-1) : url;
        } catch (Exception ex) {
            var msg = "Error al obtenir la url per enviar l'avis a Commanda";
            integracioHelper.addAccioError(info, msg, ex);
            log.error("[enviarAvisComanda] " + msg, ex);
            return;
        }

        try {
            var username = configHelper.getConfig("es.caib.notib.plugin.comanda.usuari");
            var password = configHelper.getConfig("es.caib.notib.plugin.comanda.password");
            NotibLogger.getInstance().info("[enviarAvisComanda] Enviant avis a Comanda url " + url, log, LoggingTipus.COMANDA);
            var comandaClient = new ComandaClient(url, username, password);
            var resposta = comandaClient.crearAvis(avis);
            NotibLogger.getInstance().info("[enviarAvisCommanda] Resposta: " + resposta, log, LoggingTipus.COMANDA);
            integracioHelper.addAccioOk(info);
        } catch (Exception ex) {
            var msg = "Error al enviar l'avis a Commanda";
            integracioHelper.addAccioError(info, msg, ex);
            log.error("[enviarAvisComanda] " + msg, ex);
        }
    }

    public boolean diagnosticar() throws Exception {

        var url = configHelper.getConfig("es.caib.notib.plugin.comanda.url");
        if (url == null) {
            throw new Exception("La propietat es.caib.notib.plugin.comanda.url.base no pot ser null");
        }
        url += (url.charAt(url.length()-1) != '/' ? "/" : "") + "api";
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        var username = configHelper.getConfig("es.caib.notib.plugin.comanda.usuari");
        var password = configHelper.getConfig("es.caib.notib.plugin.comanda.password");
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);
        var restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);
        return HttpStatus.ACCEPTED.equals(response.getStatusCode());
    }
}
