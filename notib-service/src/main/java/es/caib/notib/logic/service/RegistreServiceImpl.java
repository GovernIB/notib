package es.caib.notib.logic.service;

import es.caib.notib.logic.email.EmailConstants;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.EnviamentTableHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.RegistreHelper;
import es.caib.notib.logic.helper.RegistreSmHelper;
import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.adviser.sir.RespostaSirAdviser;
import es.caib.notib.logic.intf.dto.adviser.sir.SirAdviser;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.RegistreService;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaSirDto;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistreServiceImpl implements RegistreService {

    private final NotificacioService notificacioService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final RegistreSmHelper registreSmHelper;
    private final NotificacioTableHelper notificacioTableHelper;
    private final AuditHelper auditHelper;
    private final RegistreHelper registreHelper;
    private final JmsTemplate jmsTemplate;
    private final NotificacioEventHelper notificacioEventHelper;
    private final CallbackHelper callbackHelper;
    private final EnviamentTableHelper enviamentTableHelper;


    @Override
    public void registrarSortida(RegistreAnotacioDto registreAnotacio) {
        // not implemented
    }

    @Transactional
    @Override
    public boolean enviarRegistre(EnviamentRegistreRequest enviamentRegistreRequest) {

        var enviamentUuid = enviamentRegistreRequest.getEnviamentUuid();
        try {
            var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            var notificacio = enviament.getNotificacio();
            var numIntent = enviamentRegistreRequest.getNumIntent();
            notificacio.setRegistreEnviamentIntent(numIntent);
            NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> registrant ", log, LoggingTipus.REGISTRE);
            // Registrar enviament
            boolean registreSuccess = registreSmHelper.registrarEnviament(enviament, numIntent);
            NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> registrat ", log, LoggingTipus.REGISTRE);

            // Actualitzar notificació
            if (notificacioEnviamentRepository.areEnviamentsRegistrats(notificacio.getId()) == 1) {
                NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> actualitzant notificacio", log, LoggingTipus.REGISTRE);
                var isSir = notificacio.isComunicacioSir();
                notificacio.updateEstat(isSir ? NotificacioEstatEnumDto.ENVIAT_SIR : NotificacioEstatEnumDto.REGISTRADA);

                // És possible que el registre ja retorni estats finals al registrar SIR?
                if (isSir && notificacio.getEnviaments().stream().allMatch(e -> e.isRegistreEstatFinal())) {
                    var nouEstat = NotificacioEstatEnumDto.FINALITZADA;
                    //Marcar com a processada si la notificació s'ha fet des de una aplicació
                    if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
                        nouEstat = NotificacioEstatEnumDto.PROCESSADA;
                    }
                    notificacio.updateEstat(nouEstat);
                    notificacio.updateMotiu(enviament.getRegistreEstat().name());
                    notificacio.updateEstatDate(new Date());
                }
            }
            NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> actualitzant registre", log, LoggingTipus.REGISTRE);
            notificacioTableHelper.actualitzarRegistre(notificacio);
            NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> audita notificacio", log, LoggingTipus.REGISTRE);
            auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "RegistreSmHelper.registrarEnviament");

    //            TEST
    //            var registreSuccess = new Random().nextBoolean();
    //            if (registreSuccess) {
    //                enviament.setRegistreData(new Date());
    //                notificacioEnviamentRepository.save(enviament);
    //            }
            NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> is success " + registreSuccess, log, LoggingTipus.REGISTRE);
            return registreSuccess;
        } catch (Exception ex) {
            NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> error ", ex, log, LoggingTipus.REGISTRE);
            return false;
        }
    }

    @Transactional
    @Override
    public boolean consultaSir(ConsultaSirDto enviament) {

        try {
            // Consultar enviament a SIR
            notificacioService.enviamentRefrescarEstatRegistre(enviament.getId());
            var enviamentEntity = notificacioEnviamentRepository.findByUuid(enviament.getUuid()).orElseThrow();
            return enviamentEntity.getSirConsultaIntent() == 0;
        } catch (Exception ex) {
            log.error("Error a la consulta SIR per l'enviament " + enviament.getUuid());
            return false;
        }
    }

    private String validarAdviserSir(SirAdviser adviser) {

        var error = new StringJoiner(". ");
        if (Strings.isNullOrEmpty(adviser.getRegistreNumero())) {
            error.add("El número de registre no pot ser null");
        }
        if (adviser.getEstat() == null) {
            error.add("L'estat no pot ser null");
        }
        if (adviser.getRegistreData() == null) {
            error.add("La data de registre not pot ser null");
        }
        if (adviser.getSirRecepcioData() == null) {
            error.add("La data de rececpió SIR no pot ser null");
        }
        if (adviser.getSirRegistreDestiData() == null) {
            error.add("La data de registre SIR destí no pot ser null");
        }
        return error.toString();
    }

    @Transactional
    @Override
    public RespostaSirAdviser sincronitzarEnviamentSir(SirAdviser adviser) {

        var errorsValidacio = validarAdviserSir(adviser);
        if (!Strings.isNullOrEmpty(errorsValidacio)) {
            return RespostaSirAdviser.builder().ok(false).errorDescripcio(errorsValidacio).build();
        }
        var enviament = notificacioEnviamentRepository.findByRegistreNumeroFormatat(adviser.getRegistreNumero()).orElse(null);
        if (enviament == null ) {
            return RespostaSirAdviser.builder().ok(false).errorDescripcio("No existeix l'enviament amb el número de registre " + adviser.getRegistreNumero()).build();
        }
        var notificacio = enviament.getNotificacio();
        var canviEstat = false;
        var errorDescripcio = "";
        var error = false;
        try {
            canviEstat = !enviament.getRegistreEstat().equals(adviser.getEstat());
            log.info("[SIR ADVISER] Actualitzar estat comunicació SIR [Id: " + enviament.getId() + " - estat enviament:" + enviament.getRegistreEstat()
                    + " - estat adviser " + adviser.getEstat() +"]: ");
            registreHelper.enviamentUpdateDatat(adviser.getEstat(), adviser.getRegistreData(), adviser.getSirRecepcioData(), adviser.getSirRegistreDestiData(), adviser.getRegistreNumero(), enviament);
            if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA && canviEstat) {
                try {
                    log.info("[SIR ADVISER] Preparar enviament mail notificació [Id: " + enviament.getId() + "]");
                    jmsTemplate.convertAndSend(EmailConstants.CUA_EMAIL_NOTIFICACIO, notificacio.getId());
                } catch (JmsException ex) {
                    log.error("[SIR ADVISER] Hi ha hagut un error al intentar enviar el correu electrònic de la notificació amb id: ." + notificacio.getId(), ex);
                }
            }
        } catch (Exception ex) {
            log.error("[SIR ADVISER] Error al sincronitzar enviament SIR", ex);
            error = true;
            errorDescripcio = "[SIR ADVISER] Error sincronitzant enviament SIR: " + ex.getMessage();
            notificacioEventHelper.addSirAdviserEvent(enviament, true, errorDescripcio, false);
        }

        notificacioEventHelper.addSirAdviserEvent(enviament, false, null, false);
        if (canviEstat) {
            callbackHelper.updateCallback(enviament, false, null);
        }
        notificacioTableHelper.actualitzarRegistre(notificacio);
        enviamentTableHelper.actualitzarRegistre(enviament);
        auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "RegistreService.sincronitzarEnviamentSir");
        return RespostaSirAdviser.builder().ok(!error).errorDescripcio(errorDescripcio).build();
    }
}
