package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.NotificacioTableEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NotificacioTableHelper {
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;

    @Autowired
    private NotificacioEventRepository notificacioEventRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void crearRegistre(NotificacioEntity notificacio){
        log.info(String.format("[NOTIF-TABLE] Cream el registre de la notificacio [Id: %d]", notificacio.getId()));
        NotificacioTableEntity tableViewItem = NotificacioTableEntity.builder()
                .notificacio(notificacio)
                .entitat(notificacio.getEntitat())
                .procedimentCodiNotib(notificacio.getProcedimentCodiNotib())
                .procedimentOrgan(notificacio.getProcedimentOrgan())
                .usuariCodi(notificacio.getUsuariCodi())
                .grupCodi(notificacio.getGrupCodi())
//				.notificaErrorEvent(notificacio.getNotificaErrorEvent())
                .tipusUsuari(notificacio.getTipusUsuari())
                .notificaErrorData(null)
                .notificaErrorDescripcio(null)
                .enviamentTipus(notificacio.getEnviamentTipus())
                .numExpedient(notificacio.getNumExpedient())
                .concepte(notificacio.getConcepte())
                .estat(notificacio.getEstat())
                .estatDate(notificacio.getEstatDate())
                .entitatNom(notificacio.getEntitat().getNom())
                .procedimentCodi(notificacio.getProcediment() != null ? notificacio.getProcediment().getCodi() : null)
                .procedimentNom(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : null)
                .procedimentIsComu(notificacio.getProcediment() != null && notificacio.getProcediment().isComu())
                .organCodi(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null)
                .organNom(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getNom() : null)
                .organEstat(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getEstat() : null)
                .isErrorLastEvent(false)
                .notificacioMassiva(notificacio.getNotificacioMassivaEntity())
                .build();

        notificacioTableViewRepository.save(tableViewItem);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void actualitzarRegistre(NotificacioEntity notificacio){
        log.info(String.format("[NOTIF-TABLE] Actualitzam el registre de la notificacio [Id: %d]", notificacio.getId()));
        NotificacioTableEntity tableViewItem = notificacioTableViewRepository.findOne(notificacio.getId());
        if (tableViewItem == null) {
            this.crearRegistre(notificacio);
            return;
        }

        tableViewItem.setEntitat(notificacio.getEntitat());
        tableViewItem.setProcedimentCodiNotib(notificacio.getProcedimentCodiNotib());
        tableViewItem.setProcedimentOrgan(notificacio.getProcedimentOrgan());
        tableViewItem.setUsuariCodi(notificacio.getUsuariCodi());
        tableViewItem.setGrupCodi(notificacio.getGrupCodi());
        tableViewItem.setTipusUsuari(notificacio.getTipusUsuari());
        tableViewItem.setNotificacioMassiva(notificacio.getNotificacioMassivaEntity());

        if (ignoreNotificaError(notificacio)) {
            tableViewItem.setNotificaErrorData(null);
            tableViewItem.setNotificaErrorDescripcio(null);

        } else {
            NotificacioEventEntity lastEvent = notificacioEventRepository.findLastErrorEventByNotificacioId(notificacio.getId());
            tableViewItem.setNotificaErrorData(lastEvent != null ? lastEvent.getData() : null);
            tableViewItem.setNotificaErrorDescripcio(lastEvent != null ? lastEvent.getErrorDescripcio() : null);
            tableViewItem.setErrorLastEvent(isErrorLastEvent(notificacio, lastEvent));
        }

        tableViewItem.setEnviamentTipus(notificacio.getEnviamentTipus());
        tableViewItem.setNumExpedient(notificacio.getNumExpedient());
        tableViewItem.setConcepte(notificacio.getConcepte());
        tableViewItem.setEstat(notificacio.getEstat());
        tableViewItem.setEstatDate(notificacio.getEstatDate());
        tableViewItem.setEntitatNom(notificacio.getEntitat().getNom());
        tableViewItem.setProcedimentCodi(notificacio.getProcediment() != null ? notificacio.getProcediment().getCodi() : null);
        tableViewItem.setProcedimentNom(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : null);
        tableViewItem.setProcedimentIsComu(notificacio.getProcediment() != null && notificacio.getProcediment().isComu());
        tableViewItem.setOrganCodi(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null);
        tableViewItem.setOrganNom(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getNom() : null);
        tableViewItem.setOrganEstat(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getEstat() : null);
        tableViewItem.setRegistreEnviamentIntent(notificacio.getRegistreEnviamentIntent());
        
        notificacioTableViewRepository.saveAndFlush(tableViewItem);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void eliminarRegistre(NotificacioEntity notificacio){
        log.info(String.format("[NOTIF-TABLE] Eliminam el registre de la notificacio [Id: %d]", notificacio.getId()));
        notificacioTableViewRepository.delete(notificacio.getId());
    }

    /////
    // PRIVATE METHODS
    ////

    private boolean isErrorLastEvent(NotificacioEntity notificacio, NotificacioEventEntity event) {
        List<NotificacioEventTipusEnumDto> errorsTipus = Arrays.asList(
                NotificacioEventTipusEnumDto.CALLBACK_CLIENT,
                NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
                NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
                NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
                NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR,
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR
        );
        return event != null && notificacio.isTipusUsuariAplicacio() && event.isError() && errorsTipus.contains(event.getTipus());
    }

    /**
     * Ignoram els esdeveniments d'error de la notificació indicada
     * * quan ja esta enviada
     * * quan encara no s'ha intentat enviar ni al registre
     * * Quan ja ha estat registrada però encara no s'ha intentat enviar a notifica
     *
     * @param notificacio
     *
     * @return boleà indicant si s'ha d'ignorar l'error de la notificació
     */
    private boolean ignoreNotificaError(NotificacioEntity notificacio) {
        boolean hasNotificaIntents = notificacio.getNotificaEnviamentIntent() != 0;
//        boolean hasSirConsultaIntents = false;
        boolean hasRegistreIntents = notificacio.getRegistreEnviamentIntent() != 0;
//        for(NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
//            if(!hasNotificaIntents && enviament.getNotificaIntentNum() != 0 ){
//                hasNotificaIntents = true;
//            }
//            if(enviament.getSirConsultaIntent() != 0 ){
//                hasSirConsultaIntents = true;
//            }
//        }
        NotificacioEstatEnumDto notificacioEstat = notificacio.getEstat();
        return notificacioEstat.equals(NotificacioEstatEnumDto.ENVIADA) ||
                (notificacioEstat.equals(NotificacioEstatEnumDto.PENDENT) && !hasRegistreIntents) ||
                (notificacioEstat.equals(NotificacioEstatEnumDto.REGISTRADA) && !hasNotificaIntents);
    }
}
