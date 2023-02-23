package es.caib.notib.core.helper;

import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotTableUpdate;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.NotificacioMassivaEntity;
import es.caib.notib.core.entity.NotificacioTableEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioMassivaRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class NotificacioTableHelper {
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;
    @Autowired
    private NotificacioEventRepository notificacioEventRepository;
    @Autowired
    private NotificacioMassivaRepository notificacioMassivaRepository;
    @Resource
    private MessageHelper messageHelper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void crearRegistre(NotificacioEntity notificacio){

        log.info(String.format("[NOTIF-TABLE] Cream el registre de la notificacio [Id: %d]", notificacio.getId()));
        try {

            // Camps calcaulats a partir de valors dels enviaments
            String titular = "";
            String notificaIds = "";
            Integer estatMask = 0;

            if (notificacio.getEnviaments() != null) {
                for (NotificacioEnviamentEntity e : notificacio.getEnviaments()) {
                    if (e.getTitular() != null) {
                        titular += e.getTitular().getNomFormatted() + ", ";
                    }
                }
                if (titular.length() > 2)
                    titular = titular.substring(0, titular.length() - 2);
            }
            estatMask = NotificacioEstatEnumDto.ENVIANT.getMask();

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
                    .estatProcessatDate(notificacio.getEstatProcessatDate())
                    .entitatNom(notificacio.getEntitat().getNom())
                    .procedimentCodi(notificacio.getProcediment() != null ? notificacio.getProcediment().getCodi() : null)
                    .procedimentNom(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : null)
                    .procedimentIsComu(notificacio.getProcediment() != null && notificacio.getProcediment().isComu())
                    .procedimentRequirePermission(notificacio.getProcediment() != null && notificacio.getProcediment().isRequireDirectPermission())
                    .procedimentTipus(notificacio.getProcediment() != null ? notificacio.getProcediment().getTipus() : null)
                    .organCodi(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null)
                    .organNom(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getNom() : null)
                    .organEstat(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getEstat() : null)
                    .isErrorLastEvent(false)
                    .notificacioMassiva(notificacio.getNotificacioMassivaEntity())
                    .enviadaDate(getEnviadaDate(notificacio))
                    .referencia(notificacio.getReferencia())
                    .titular(titular)
                    .notificaIds(notificaIds)
                    .estatMask(estatMask)
                    .build();
            notificacioTableViewRepository.save(tableViewItem);
        } catch (Exception ex) {
            log.error("No ha estat possible crear la informació de la notificació " + notificacio.getId(), ex);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void actualitzar(NotTableUpdate not) {

        try {
            NotificacioTableEntity item = notificacioTableViewRepository.findOne(not.getId());
            if (item == null) {
                return;
            }
            if (not.getEstatProcessatDate() != null) {
                item.setEstatProcessatDate(not.getEstatProcessatDate());
            }
            if (not.getEstatDate() != null) {
                item.setEstatDate(not.getEstatDate());
            }
            if (not.getReintentsRegistre() != null) {
                item.setRegistreEnviamentIntent(not.getReintentsRegistre());
            }

            if (not.getEstat() != null) {
                // Estat de la notificacio
                item.setEstatMask(item.getEstatMask() - item.getEstat().getMask() + not.getEstat().getMask());
                if (NotificacioEstatEnumDto.PENDENT.equals(item.getEstat()) && item.getRegistreEnviamentIntent() > 0) {
                    item.setEstatMask(NotificacioEstatEnumDto.PENDENT.getMask());
                }
                // Estats dels enviaments
                if (NotificacioEstatEnumDto.FINALITZADA.equals(not.getEstat()) ||
                        NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(not.getEstat()) ||
                        NotificacioEstatEnumDto.PROCESSADA.equals(not.getEstat())) {
                    Integer estatMask = item.getEstatMask();
                    for (NotificacioEnviamentEntity enviament : item.getEnviaments()) {
                        if (EnumUtils.isValidEnum(NotificacioEstatEnumDto.class, enviament.getNotificaEstat().name())) {
                            NotificacioEstatEnumDto eventEstat = NotificacioEstatEnumDto.valueOf(enviament.getNotificaEstat().name());
                            if ((estatMask & eventEstat.getMask()) == 0) {
                                estatMask += eventEstat.getMask();
                            }
                        }
                    }
                    item.setEstatMask(estatMask);
                }
                item.setEstat(not.getEstat());
            }
            notificacioTableViewRepository.saveAndFlush(item);
        } catch (Exception ex) {
            log.error("Error acutalitzant la informació de la notificació " + not.getId(), ex);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void actualitzarRegistre(NotificacioEntity notificacio){
        log.info(String.format("[NOTIF-TABLE] Actualitzam el registre de la notificacio [Id: %d]", notificacio.getId()));
        try {
            NotificacioTableEntity tableViewItem = notificacioTableViewRepository.findOne(notificacio.getId());
            if (tableViewItem == null) {
                this.crearRegistre(notificacio);
                return;
            }

            // Si pertany a una notificació massiva necessitam l'estat actual i error
            NotificacioEstatEnumDto estatActual = tableViewItem.getEstat();
            boolean hasErrorActual = tableViewItem.getNotificaErrorData() != null;

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
                if (tableViewItem.getNotificaErrorDescripcio() == null) {
                    // TODO MISSATGE MULTIDIOMA
//                String desc = notificacio.hasEnviamentsPerEmail() ? messageHelper.getMessage("error.notificacio.enviaments")
                    String desc = notificacio.hasEnviamentsPerEmail() ?
                            "S'ha produït algun error en els enviaments. Els errors es poden consultar en cada un dels enviaments."
                            : (lastEvent != null ? lastEvent.getErrorDescripcio() : null);
                    tableViewItem.setNotificaErrorDescripcio(desc);
                }
                tableViewItem.setErrorLastEvent(isErrorLastEvent(notificacio, lastEvent));
            }

            // Camps calcaulats a partir de valors dels enviaments
            String titular = "";
            String notificaIds = "";
            Integer estatMask = 0;

            if (notificacio.getEnviaments() != null) {
                for (NotificacioEnviamentEntity e : notificacio.getEnviaments()) {
                    if (e.getTitular() != null) {
                        titular += e.getTitular().getNomFormatted() + ", ";
                    }
                    if (e.getNotificaIdentificador() != null) {
                        notificaIds += e.getNotificaIdentificador() + ", ";
                    }
                    // Estat de la notificacio
                    if ((estatMask & e.getNotificacio().getEstat().getMask()) == 0) {
                        estatMask += e.getNotificacio().getEstat().getMask();
                    }
                    if (EnumUtils.isValidEnum(NotificacioEstatEnumDto.class, e.getNotificaEstat().name())) {
                        NotificacioEstatEnumDto eventEstat = NotificacioEstatEnumDto.valueOf(e.getNotificaEstat().name());
                        if ((estatMask & eventEstat.getMask()) == 0) {
                            estatMask += eventEstat.getMask();
                        }
                    }
                }
                if (titular.length() > 2)
                    titular = titular.substring(0, titular.length() - 2);
                if (notificaIds.length() > 2)
                    notificaIds = notificaIds.substring(0, notificaIds.length() - 2);
            }

            tableViewItem.setEnviamentTipus(notificacio.getEnviamentTipus());
            tableViewItem.setNumExpedient(notificacio.getNumExpedient());
            tableViewItem.setConcepte(notificacio.getConcepte());
            tableViewItem.setEstat(notificacio.getEstat());
            tableViewItem.setEstatDate(notificacio.getEstatDate());
            tableViewItem.setEstatProcessatDate(notificacio.getEstatProcessatDate());
            tableViewItem.setEntitatNom(notificacio.getEntitat().getNom());
            tableViewItem.setProcedimentCodi(notificacio.getProcediment() != null ? notificacio.getProcediment().getCodi() : null);
            tableViewItem.setProcedimentNom(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : null);
            tableViewItem.setProcedimentIsComu(notificacio.getProcediment() != null && notificacio.getProcediment().isComu());
            tableViewItem.setProcedimentRequirePermission(notificacio.getProcediment() != null && notificacio.getProcediment().isRequireDirectPermission());
            tableViewItem.setProcedimentTipus(notificacio.getProcediment() != null ? notificacio.getProcediment().getTipus() : null);
            tableViewItem.setOrganCodi(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null);
            tableViewItem.setOrganNom(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getNom() : null);
            tableViewItem.setOrganEstat(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getEstat() : null);
            tableViewItem.setRegistreEnviamentIntent(notificacio.getRegistreEnviamentIntent());
            tableViewItem.setEnviadaDate(getEnviadaDate(notificacio));
            tableViewItem.setTitular(titular);
            tableViewItem.setNotificaIds(notificaIds);
            tableViewItem.setEstatMask(estatMask);

            notificacioTableViewRepository.saveAndFlush(tableViewItem);

            if (notificacio.getNotificacioMassivaEntity() != null) {
                updateMassiva(notificacio.getNotificacioMassivaEntity(), estatActual, hasErrorActual,
                            tableViewItem.getEstat(),tableViewItem.getNotificaErrorData() != null);
                notificacioMassivaRepository.saveAndFlush(notificacio.getNotificacioMassivaEntity());
            }
        } catch (Exception ex) {
            log.error("No ha estat possible actualitzar la informació de la notificació " + notificacio.getId(), ex);
        }
    }

    public void updateMassiva(
            NotificacioMassivaEntity notificacioMassiva,
            NotificacioEstatEnumDto originalEstat,
            boolean originalHasError,
            NotificacioEstatEnumDto destiEstat,
            boolean destiHasError) {

        log.info("[PROCES MASSIU] updateMassiva");

        // Canvi d'estat
        if (!destiEstat.equals(originalEstat)) {
            // Estat inicial --> Estat processat
            if (isEstatInicial(originalEstat) && !isEstatInicial(destiEstat)) {

                if (originalHasError) {
                    // Passar de processada amb error a processada
                    if (!destiHasError) {
                        notificacioMassiva.updateErrorToProcessada();
                    }
                } else {
                    // Actualitzam com a processada
                    notificacioMassiva.updateToProcessada();
                }

                // Estat processat --> Estat processat
            } else if (!isEstatInicial(originalEstat)) {
                // Passar de processada amb error a processada
                if(originalHasError && !destiHasError) {
                    notificacioMassiva.updateErrorToProcessada();
                    // Passar de processada a processada amb error
                } else if (!originalHasError && destiHasError) {
                    notificacioMassiva.updateProcessadaToError();
                }
                // Estat inicial --> Estat inicial
            } else {
                // Actialitzar com a processada amb error
                if (!originalHasError && destiHasError) {
                    notificacioMassiva.updateToError();
                }
            }

            // No canvia l'estat
        } else if (!originalHasError && destiHasError) {
            // Actialitzar com a processada amb error
            if (isEstatInicial(destiEstat)) {
                notificacioMassiva.updateToError();
                // Passar de processada a processada amb error
            } else {
                notificacioMassiva.updateProcessadaToError();
            }
        }
    }

    private boolean isEstatInicial(NotificacioEstatEnumDto estat) {
        return NotificacioEstatEnumDto.PENDENT.equals(estat) ||
                NotificacioEstatEnumDto.ENVIANT.equals(estat);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void eliminarRegistre(NotificacioEntity notificacio){
        log.info(String.format("[NOTIF-TABLE] Eliminam el registre de la notificacio [Id: %d]", notificacio.getId()));
        notificacioTableViewRepository.delete(notificacio.getId());
    }

    /////
    // PRIVATE METHODS
    ////

    private Date getEnviadaDate(NotificacioEntity notificacio) {

        try {
            if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
                return null;
            }
            NotificacioEnviamentEntity env = notificacio.getEnviaments().iterator().next();

            if (env.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)
                && (!notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)
                    || !notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIANT))) {
                return env.getRegistreData();
            }

            if (!env.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)
                    && (!notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)
                        || !notificacio.getEstat().equals(NotificacioEstatEnumDto.REGISTRADA)
                        || !notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIANT))) {
                return notificacio.getNotificaEnviamentNotificaData();
            }
        } catch (Exception ex) {
            log.error("Error actualitzant la data d'enviament a la taula del llistat", ex);
        }
        return null;
    }

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
        return !notificacioEstat.equals(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS) &&
                (notificacioEstat.equals(NotificacioEstatEnumDto.ENVIADA) ||
                (notificacioEstat.equals(NotificacioEstatEnumDto.PENDENT) && !hasRegistreIntents) ||
                (notificacioEstat.equals(NotificacioEstatEnumDto.REGISTRADA) && !hasNotificaIntents));
    }

}
