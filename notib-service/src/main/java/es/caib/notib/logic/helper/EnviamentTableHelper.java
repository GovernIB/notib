package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EnviamentTableEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import joptsimple.internal.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EnviamentTableHelper {

    @Autowired
    private EnviamentTableRepository enviamentTableRepository;
    @Autowired
    private NotificacioTableHelper notificacioTableHelper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void crearRegistre(NotificacioEnviamentEntity enviament) {

        var notificacio = enviament.getNotificacio();
        var titular = enviament.getTitular();
        var document = notificacio.getDocument();
        var csv = document != null && !Strings.isNullOrEmpty(document.getCsv()) ? document.getCsv() : "";
        var uuid = document != null && !Strings.isNullOrEmpty(document.getUuid()) ? document.getUuid() : "";

        EnviamentTableEntity tableViewItem = EnviamentTableEntity.builder()
                .enviament(enviament)
                .notificacio(notificacio)
                .notificacioId(notificacio.getId())
                .entitat(notificacio.getEntitat())
                .destinataris(getEnviamentDestinataris(enviament))
                .tipusEnviament(notificacio.getEnviamentTipus())
                .titularNif(titular != null ? titular.getNif() : null)
                .titularNom(titular != null ? titular.getNom() : null)
                .titularEmail(titular != null ? titular.getEmail() : null)
                .titularLlinatge1(titular != null ? titular.getLlinatge1() : null)
                .titularLlinatge2(titular != null ? titular.getLlinatge2() : null)
                .titularRaoSocial(titular != null ? titular.getRaoSocial() : null)
                .enviamentDataProgramada(notificacio.getEnviamentDataProgramada())
                .procedimentCodiNotib(notificacio.getProcedimentCodiNotib())
                .procedimentNom(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : null)
                .referenciaNotificacio(notificacio.getReferencia())
                .grupCodi(notificacio.getGrupCodi())
                .emisorDir3Codi(notificacio.getEmisorDir3Codi())
                .usuariCodi(notificacio.getUsuariCodi())
                .organCodi(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null)
                .organNom(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getNom() : null)
                .organEstat(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getEstat() : null)
                .concepte(notificacio.getConcepte())
                .descripcio(notificacio.getDescripcio())
                .registreLlibreNom(notificacio.getRegistreLlibreNom())
                .estat(notificacio.getEstat())
                .csv_uuid(csv + " " + uuid)
                .hasErrors(false)
                .procedimentIsComu(notificacio.getProcediment() != null && notificacio.getProcediment().isComu())
                .procedimentOrganId(notificacio.getProcedimentOrgan() != null ? notificacio.getProcedimentOrgan().getId() : null)
                .procedimentRequirePermission(notificacio.getProcediment() != null && notificacio.getProcediment().isRequireDirectPermission())
                .procedimentTipus(notificacio.getProcediment() != null ? notificacio.getProcediment().getTipus() : null)
                .registreNumero(notificacio.getRegistreNumero())
                .registreData(enviament.getRegistreData())
                .registreEnviamentIntent(0)
                .notificaDataCaducitat(enviament.getNotificaDataCaducitat())
                .notificaIdentificador(enviament.getNotificaIdentificador())
                .notificaCertificacioNumSeguiment(enviament.getNotificaCertificacioNumSeguiment())
                .notificaEstat(enviament.getNotificaEstat())
                .notificaReferencia(enviament.getNotificaReferencia())
                .errorLastCallback(false)
                .entregaPostal(enviament.getEntregaPostal() != null)
                .anulable(isAnulable(enviament))
                .build();

        enviamentTableRepository.save(tableViewItem);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void actualitzarRegistre(NotificacioEnviamentEntity enviament) {

        EnviamentTableEntity tableViewItem = enviamentTableRepository.findById(enviament.getId()).orElse(null);
        if (tableViewItem == null) {
            this.crearRegistre(enviament);
            return;
        }

        PersonaEntity titular = enviament.getTitular();
        NotificacioEntity notificacio = enviament.getNotificacio();
        DocumentEntity document = notificacio.getDocument();

        tableViewItem.setEnviament(enviament);
        tableViewItem.setNotificacio(notificacio);
        tableViewItem.setNotificacioId(notificacio.getId());
        tableViewItem.setEnviadaDate(enviament.getUltimEvent() != null ? enviament.getUltimEvent().getData() : null);
        tableViewItem.setEntitat(notificacio.getEntitat());
        tableViewItem.setDestinataris(getEnviamentDestinataris(enviament));
        tableViewItem.setTipusEnviament(notificacio.getEnviamentTipus());

        tableViewItem.setTitularNif(titular != null ? titular.getNif() : null);
        tableViewItem.setTitularNom(titular != null ? titular.getNom() : null);
        tableViewItem.setTitularEmail(titular != null ? titular.getEmail() : null);
        tableViewItem.setTitularLlinatge1(titular != null ? titular.getLlinatge1() : null);
        tableViewItem.setTitularLlinatge2(titular != null ? titular.getLlinatge2() : null);
        tableViewItem.setTitularRaoSocial(titular != null ? titular.getRaoSocial() : null);

        tableViewItem.setEnviamentDataProgramada(notificacio.getEnviamentDataProgramada());
        tableViewItem.setProcedimentCodiNotib(notificacio.getProcedimentCodiNotib());
        tableViewItem.setProcedimentNom(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : null);
        tableViewItem.setReferenciaNotificacio(notificacio.getReferencia());
        tableViewItem.setGrupCodi(notificacio.getGrupCodi());
        tableViewItem.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
        tableViewItem.setUsuariCodi(notificacio.getUsuariCodi());
        tableViewItem.setOrganCodi(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null);
        tableViewItem.setOrganNom(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getNom() : null);
        tableViewItem.setOrganEstat(notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getEstat() : null);
        tableViewItem.setConcepte(notificacio.getConcepte());
        tableViewItem.setDescripcio(notificacio.getDescripcio());
        tableViewItem.setRegistreLlibreNom(notificacio.getRegistreLlibreNom());
        tableViewItem.setEstat(notificacio.getEstat());
        var csv = document != null && !Strings.isNullOrEmpty(document.getCsv()) ? document.getCsv() : "";
        var uuid = document != null && !Strings.isNullOrEmpty(document.getUuid()) ? document.getUuid() : "";
        tableViewItem.setCsv_uuid(csv + " " + uuid);
        tableViewItem.setHasErrors(false);

        tableViewItem.setProcedimentIsComu(notificacio.getProcediment() != null && notificacio.getProcediment().isComu());
        tableViewItem.setProcedimentOrganId(notificacio.getProcedimentOrgan() != null ? notificacio.getProcedimentOrgan().getId() : null);
        tableViewItem.setProcedimentRequirePermission(notificacio.getProcediment() != null && notificacio.getProcediment().isRequireDirectPermission());
        tableViewItem.setProcedimentTipus(notificacio.getProcediment() != null ? notificacio.getProcediment().getTipus() : null);

        tableViewItem.setRegistreNumero(notificacio.getRegistreNumero());
        tableViewItem.setRegistreData(enviament.getRegistreData());
        tableViewItem.setRegistreEnviamentIntent(notificacio.getRegistreEnviamentIntent());

        tableViewItem.setNotificaDataCaducitat(enviament.getNotificaDataCaducitat());
        tableViewItem.setNotificaIdentificador(enviament.getNotificaIdentificador());
        tableViewItem.setNotificaCertificacioNumSeguiment(enviament.getNotificaCertificacioNumSeguiment());
        tableViewItem.setNotificaEstat(enviament.getNotificaEstat());
        tableViewItem.setNotificaReferencia(enviament.getNotificaReferencia());
        tableViewItem.setEntitat(notificacio.getEntitat());
        tableViewItem.setAnulable(isAnulable(enviament));
        tableViewItem.setAnulat(enviament.isAnulat());
        tableViewItem.setMotiuAnulacio(enviament.getMotiuAnulacio());
        tableViewItem.setEntregaPostal(enviament.getEntregaPostal() != null);
        tableViewItem.setErrorLastCallback(enviament.isErrorLastCallback());

        enviamentTableRepository.saveAndFlush(tableViewItem);
        notificacioTableHelper.actualitzarRegistre(notificacio);
    }

    public boolean isAnulable(NotificacioEnviamentEntity enviament) {
        return enviament != null && !enviament.isAnulat() && !com.google.common.base.Strings.isNullOrEmpty(enviament.getNotificaIdentificador()) && !enviament.isNotificaEstatFinal() && !enviament.isCieEstatFinal()
                && (enviament.getEntregaPostal() == null || enviament.getEntregaPostal().getCieEstat() == null
                    || CieEstat.ENVIADO_CI.equals(enviament.getEntregaPostal().getCieEstat()) || CieEstat.ERROR.equals(enviament.getEntregaPostal().getCieEstat())
                && enviament.getNotificacio().getOrganGestor().getEntregaCie().getCie().isCieExtern());
    }

    /////
    // PRIVATE METHODS
    ////

    private String getEnviamentDestinataris(NotificacioEnviamentEntity enviament) {

        var destinataris = enviament.getDestinataris();
        var destinatarisNomLlinatges = new StringBuilder();
        for(var destinatari: destinataris) {
            destinatarisNomLlinatges.append(destinatari.asDto().getNomFormatted()).append("<br>");
        }
        if (destinatarisNomLlinatges.length() > 0) {
            destinatarisNomLlinatges.delete(destinatarisNomLlinatges.length() - 4, destinatarisNomLlinatges.length());
        }
        return destinatarisNomLlinatges.toString();
    }
}
