package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.NotificacioTableViewEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NotificacioTableHelper {
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;

    @Autowired
    private NotificacioEventRepository notificacioEventRepository;

    public void crearRegistre(NotificacioEntity notificacio){
        NotificacioTableViewEntity tableViewItem = NotificacioTableViewEntity.builder()
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
                .isErrorLastEvent(false)
                .build();

        notificacioTableViewRepository.save(tableViewItem);
    }

    public void actualitzarRegistre(NotificacioEntity notificacio){
        NotificacioTableViewEntity tableViewItem = notificacioTableViewRepository.findOne(notificacio.getId());

        tableViewItem.setEntitat(notificacio.getEntitat());
        tableViewItem.setProcedimentCodiNotib(notificacio.getProcedimentCodiNotib());
        tableViewItem.setProcedimentOrgan(notificacio.getProcedimentOrgan());
        tableViewItem.setUsuariCodi(notificacio.getUsuariCodi());
        tableViewItem.setGrupCodi(notificacio.getGrupCodi());
        tableViewItem.setTipusUsuari(notificacio.getTipusUsuari());
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

        notificacioTableViewRepository.saveAndFlush(tableViewItem);
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

    private boolean ignoreNotificaError(NotificacioEntity notificacio) {
        boolean hasNotificaIntents = false;
        boolean hasSirConsultaIntents = false;
        for(NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
            if(enviament.getNotificaIntentNum() != 0 ){
                hasNotificaIntents = true;
            }
            if(enviament.getSirConsultaIntent() != 0 ){
                hasSirConsultaIntents = true;
            }
        }
        return notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIADA) || notificacio.getRegistreEnviamentIntent() == 0 ||
                (!hasNotificaIntents && !hasSirConsultaIntents);
    }
}
