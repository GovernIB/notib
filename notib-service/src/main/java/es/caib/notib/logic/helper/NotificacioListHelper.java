package es.caib.notib.logic.helper;

import es.caib.notib.logic.helper.FiltreHelper.FiltreField;
import es.caib.notib.logic.helper.FiltreHelper.StringField;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.ServeiRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

@Component
public class NotificacioListHelper {

    @Autowired
    private PermisosService permisosService;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private ServeiRepository serveiRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private NotificacioRepository notificacioRepository;
    @Autowired
    private NotificacioEnviamentRepository enviamentRepository;

    public Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {

        Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
        mapeigPropietatsOrdenacio.put("procediment.organGestor", new String[] {"pro.organGestor.codi"});
        mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organCodi"});
        mapeigPropietatsOrdenacio.put("procediment.nom", new String[] {"procedimentNom"});
        mapeigPropietatsOrdenacio.put("procedimentDesc", new String[] {"procedimentCodi"});
        mapeigPropietatsOrdenacio.put("createdByComplet", new String[] {"createdBy"});
        mapeigPropietatsOrdenacio.put("estatString", new String[] {"estat"});
        return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
    }
    public PaginaDto<NotificacioTableItemDto> complementaNotificacions(EntitatEntity entitatEntity, String usuariCodi, Page<NotificacioTableEntity> notificacions, String ... locale) {

        if (notificacions == null) {
            return paginacioHelper.getPaginaDtoBuida(NotificacioTableItemDto.class);
        }

        List<String> codis = new ArrayList<>();
        var procSersAmbPermis = permisosService.getProcSersAmbPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
        var organs =  permisosService.getOrgansAmbPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
        if (procSersAmbPermis != null) {
            for (var procedimentOrgan : procSersAmbPermis) {
                codis.add(procedimentOrgan.getCodi());
            }
        }
        if (organs != null && !organs.isEmpty()) {
            for (var organ : organs) {
                codis.add(organ.getCodi());
            }
        }

        boolean permisProcessar;
        var page = paginacioHelper.toPaginaDto(notificacions, NotificacioTableItemDto.class);
        Optional<NotificacioEntity> not;
        NotificacioEntity e;
        for (var notificacio : page.getContingut()) {
            permisProcessar = false;
            if (notificacio.getProcedimentCodi() != null && NotificacioEstatEnumDto.FINALITZADA.equals(notificacio.getEstat())) {
                permisProcessar = codis.contains(notificacio.getProcedimentCodi()) || codis.contains(notificacio.getOrganCodi());
            }

            notificacio.setPermisProcessar(permisProcessar);
//            enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
//            if (enviamentsPendents != null && !enviamentsPendents.isEmpty()) {
//                notificacio.setHasEnviamentsPendentsRegistre(true);
//            }
            notificacio.setHasEnviamentsPendentsRegistre(notificacioEnviamentRepository.hasEnviamentsPendentsByNotificacioId(notificacio.getId()));
            notificacio.setDocumentId(notificacioRepository.findDOcumentId(notificacio.getId()));
            not = notificacioRepository.findById(notificacio.getId());
            if (not == null || not.isEmpty()) {
                continue;
            }
            e = not.get();
            var envs = enviamentRepository.findByNotificacio(e);
            prepararColumnaEstat(notificacio, envs);

            var cerData = envs != null && !envs.isEmpty() && envs.get(0) != null ? envs.get(0).getNotificaCertificacioData() : null;
            var id = e != null && e.getDocument() != null ? e.getDocument().getId() : null;
            notificacio.setDocumentId(id);
            notificacio.setEnvCerData(cerData);
            notificacio.setOrganEstat(e != null && e.getOrganGestor() != null ? e.getOrganGestor().getEstat() : null);
        }
        return page;
    }

    private void prepararColumnaEstat(NotificacioTableItemDto item, List<NotificacioEnviamentEntity> enviaments) {

        var estat = item.isEnviant() ? "<span class=\"fa fa-clock-o\"></span>" :
                NotificacioEstatEnumDto.PENDENT.equals(item.getEstat()) ? "<span class=\"fa fa-clock-o\"></span>" :
                        NotificacioEstatEnumDto.ENVIADA.equals(item.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-send-o\"></span>" :
                                NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-check\"></span>" :
                                        NotificacioEstatEnumDto.REGISTRADA.equals(item.getEstat()) ? "<span class=\"fa fa-file-o\"></span>" :
                                                NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) ? "<span class=\"fa fa-check-circle\"></span>" : "";
        var nomEstat = " " + messageHelper.getMessage("es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto." + item.getEstat().name()) + "";
        var error = item.isNotificaError() ? " <span class=\"fa fa-warning text-danger\" title=\"" + htmlEscape(item.getNotificaErrorDescripcio()) + " \"></span>" : "";
        error += TipusUsuariEnumDto.APLICACIO.equals(item.getTipusUsuari()) && item.isErrorLastCallback() ?
                " <span class=\"fa fa-exclamation-circle text-primary\" title=\"<spring:message code=\"notificacio.list.client.error/>\"></span>" : "";
        estat = "<span>" + estat + nomEstat + error + "</span>";
        var data = "\n";
        if ((NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat())) && item.getEstatDate() != null) {
            var df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            var d = df.format(item.getEstatDate());
            data += "<span class=\"horaProcessat\">" + d + "</span>\n";
        } else if (NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) && item.getEstatProcessatDate() != null) {
            var df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            var d = df.format(item.getEstatProcessatDate());
            data += "<span class=\"horaProcessat\">" + d + "</span>\n";
        }

        var notificaEstat = "";
        var registreEstat = "";
        Map<String, Integer>  registres = new HashMap<>();
        for (var env : enviaments) {
            item.updateEstatTipusCount(env.getNotificaEstat());
//                if (NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat()) || NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat())) {
//                    notificaEstat += getMessage(request, "es.caib.notib.client.domini.EnviamentEstat." + env.getNotificaEstat()) + ", ";
//                }
            if (env.getRegistreEstat() != null) {
                if (registres.containsKey(env.getRegistreEstat().name())) {
                    var count = registres.get(env.getRegistreEstat().name());
                    count = count + 1;
                    registres.put(env.getRegistreEstat().name(), count);
                } else {
                    registres.put(env.getRegistreEstat().name(), 1);
                }
            }
            if (item.isComunicacioSir()) {
                var r = env.getRegistreEstat();
                registreEstat += env.getRegistreEstat() != null ?  "<div><span style=\"padding-bottom:1px; background-color: " + r.getColor() + ";\" title=\"" +
                        messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + r)
                        + "\" class=\"label label-primary\">" + r.getBudget() + "</span></div>" : "";
            }
        }
        notificaEstat = notificaEstat.length() > 0 ? notificaEstat.substring(0, notificaEstat.length()-2) : "";
        estat = "<div class=\"flex-column\"><div style=\"display:flex; justify-content:space-between\">" + estat + (registreEstat.length() > 0 ? registreEstat : "")
                + "</div></div>" + data + notificaEstat;
        var padding = "; padding-left: 5px;";
        var boxShadow = "box-shadow: inset 3px 0px 0px ";
        if (NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) || notificaEstat.length() > 0 || item.getContadorEstat().size() > 1) {

            for (var entry : item.getContadorEstat().entrySet()) {
                estat += "<div style=\"font-size:11px;" + boxShadow + entry.getKey().getColor() + padding + "\">" +
                        entry.getValue() + " " + messageHelper.getMessage("es.caib.notib.client.domini.EnviamentEstat." + entry.getKey())
                        + "</div>";
            }
        }
        item.setEstatString(estat);
    }

    public NotificacioFiltre getFiltre(NotificacioFiltreDto filtreDto) {

        Optional<OrganGestorEntity> organGestor = null;
        if (filtreDto.getOrganGestor() != null && !filtreDto.getOrganGestor().isEmpty()) {
            organGestor = organGestorRepository.findById(Long.parseLong(filtreDto.getOrganGestor()));
        }
        ProcSerEntity procediment = null;
        if (filtreDto.getProcedimentId() != null) {
            procediment = procedimentRepository.findProcSer(filtreDto.getProcedimentId());
        } else if (filtreDto.getServeiId() != null) {
            procediment = serveiRepository.findProcSer(filtreDto.getServeiId());
        }
        var estat = filtreDto.getEstat();
        Boolean hasZeronotificaEnviamentIntent = null;
        var isEstatNull = estat == null;
        var nomesSenseErrors = false;
        var nomesAmbErrors = filtreDto.isNomesAmbErrors();
//        if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.ENVIANT)) {
//            estat = NotificacioEstatEnumDto.PENDENT;
//            hasZeronotificaEnviamentIntent = true;
//            nomesSenseErrors = true;
//
//        } else if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.PENDENT)) {
//            hasZeronotificaEnviamentIntent = false;
////					nomesAmbErrors = true;
//        }
        return NotificacioFiltre.builder()
                .entitatId(new FiltreField<>(filtreDto.getEntitatId()))
                .comunicacioTipus(new FiltreField<>(filtreDto.getComunicacioTipus()))
                .enviamentTipus(new FiltreField<>(filtreDto.getEnviamentTipus()))
                .estat(new FiltreField<>(estat, isEstatNull))
                .concepte(new StringField(filtreDto.getConcepte()))
                .dataInici(new FiltreField<>(FiltreHelper.toIniciDia(filtreDto.getDataInici())))
                .dataFi(new FiltreField<>(FiltreHelper.toFiDia(filtreDto.getDataFi())))
                .titular(new StringField(filtreDto.getTitular()))
                .organGestor(new FiltreField<>(organGestor.get()))
                .procediment(new FiltreField<>(procediment))
                .tipusUsuari(new FiltreField<>(filtreDto.getTipusUsuari()))
                .numExpedient(new StringField(filtreDto.getNumExpedient()))
                .creadaPer(new StringField(filtreDto.getCreadaPer()))
                .identificador(new StringField(filtreDto.getIdentificador()))
                .nomesAmbErrors(new FiltreField<>(nomesAmbErrors))
                .nomesSenseErrors(new FiltreField<>(nomesSenseErrors))
                .hasZeronotificaEnviamentIntent(new FiltreField<>(hasZeronotificaEnviamentIntent))
                .referencia(new StringField(filtreDto.getReferencia()))
                .build();
    }

    @Builder
    @Getter
    @Setter
    public static class NotificacioFiltre implements Serializable {
        private FiltreField<Long> entitatId;
        private FiltreField<NotificacioComunicacioTipusEnumDto> comunicacioTipus;
        private FiltreField<NotificaEnviamentTipusEnumDto> enviamentTipus;
        private FiltreField<NotificacioEstatEnumDto> estat;
        private StringField concepte;
        private FiltreField<Date> dataInici;
        private FiltreField<Date> dataFi;
        private StringField titular;
        private FiltreField<OrganGestorEntity> organGestor;
        private FiltreField<ProcSerEntity> procediment;
        private FiltreField<TipusUsuariEnumDto> tipusUsuari;
        private StringField numExpedient;
        private StringField creadaPer;
        private StringField identificador;
        private StringField referencia;
        private FiltreField<Boolean> nomesAmbErrors;
        private FiltreField<Boolean> nomesSenseErrors;
        private FiltreField<Boolean> hasZeronotificaEnviamentIntent;
    }

}