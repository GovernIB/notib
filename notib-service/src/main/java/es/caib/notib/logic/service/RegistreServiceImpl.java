package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.RegistreSmHelper;
import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.RegistreService;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaSirDto;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistreServiceImpl implements RegistreService {

    private final EnviamentSmService enviamentSmService;
    private final NotificacioService notificacioService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final RegistreSmHelper registreSmHelper;
    private final NotificacioTableHelper notificacioTableHelper;
    private final AuditHelper auditHelper;


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
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> registrant ");
            // Registrar enviament
            boolean registreSuccess = registreSmHelper.registrarEnviament(enviament, numIntent);
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> registrat ");

            // Actualitzar notificació
            if (notificacioEnviamentRepository.areEnviamentsRegistrats(notificacio.getId()) == 1) {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> actualitzant notificacio");
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
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> actualitzant registre");
            notificacioTableHelper.actualitzarRegistre(notificacio);
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> audita notificacio");
            auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "RegistreSmHelper.registrarEnviament");

    //            TEST
    //            var registreSuccess = new Random().nextBoolean();
    //            if (registreSuccess) {
    //                enviament.setRegistreData(new Date());
    //                notificacioEnviamentRepository.save(enviament);
    //            }
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> is success " + registreSuccess);
            return registreSuccess;
        } catch (Exception ex) {
            if (enviamentUuid != null) {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> error ", ex);
                enviamentSmService.registreFailed(enviamentUuid);
            }
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
            enviamentSmService.sirFailed(enviament.getUuid());
            return false;
        }
    }
}
