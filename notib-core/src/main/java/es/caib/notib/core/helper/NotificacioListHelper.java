package es.caib.notib.core.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.rest.consulta.Notificacio;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.PermisosService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.NotificacioTableEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.helper.FiltreHelper.FiltreField;
import es.caib.notib.core.helper.FiltreHelper.StringField;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.repository.ServeiRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Set;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

@Slf4j
@Component
public class NotificacioListHelper {

    @Autowired
    private PermisosService permisosService;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private MessageHelper messageHelper;

    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private ServeiRepository serveiRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private NotificacioEventRepository eventRepository;

    public Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {
        Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
        mapeigPropietatsOrdenacio.put("procediment.organGestor", new String[] {"pro.organGestor.codi"});
        mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organCodi"});
        mapeigPropietatsOrdenacio.put("procediment.nom", new String[] {"procedimentNom"});
        mapeigPropietatsOrdenacio.put("procedimentDesc", new String[] {"procedimentCodi"});
        mapeigPropietatsOrdenacio.put("createdByComplet", new String[] {"createdBy"});
        mapeigPropietatsOrdenacio.put("estatString", new String[] {"estat"});
        return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
    }

    public PaginaDto<NotificacioTableItemDto> complementaNotificacions(EntitatEntity entitatEntity, String usuariCodi, Page<NotificacioTableEntity> notificacions) {

        if (notificacions == null) {
            return paginacioHelper.getPaginaDtoBuida(NotificacioTableItemDto.class);
        }

        List<String> codis = new ArrayList<>();
        List<CodiValorOrganGestorComuDto> procSersAmbPermis = permisosService.getProcSersAmbPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
        List<CodiValorDto> organs =  permisosService.getOrgansAmbPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
        if (procSersAmbPermis != null) {
            for (CodiValorOrganGestorComuDto procedimentOrgan : procSersAmbPermis) {
                codis.add(procedimentOrgan.getCodi());
            }
        }
        if (organs != null && !organs.isEmpty()) {
            for (CodiValorDto organ : organs) {
                codis.add(organ.getCodi());
            }
        }

        boolean permisProcessar;
        List<NotificacioTableItemDto> notificacionsDto = new ArrayList<>();
        long inici = System.currentTimeMillis();
        long iniciPreparaEstat;
        long fiPreparaEstat = 0;
        long iniciCrearTableItem;
        long fiCrearTableItem = 0;
        NotificacioTableItemDto notificacio;
        Set<NotificacioEnviamentEntity> envs;
        NotificacioEnviamentEntity env;
        Date cerData;
        Long id;
        boolean hasEnviamentsPendents;
        List<NotificacioTableEntity> nots = notificacions.getContent();
        int size = nots.size();
        NotificacioTableEntity not;
        for (int foo = 0; foo<size;foo++) {
//        for (NotificacioTableEntity not : notificacions.getContent()) {
            not = nots.get(foo);
            iniciCrearTableItem = System.currentTimeMillis();
            notificacio = crearTableItem(not);
            fiCrearTableItem += System.currentTimeMillis() - iniciCrearTableItem;
            permisProcessar = false;
            if (notificacio.getProcedimentCodi() != null && NotificacioEstatEnumDto.FINALITZADA.equals(notificacio.getEstat())) {
                permisProcessar = codis.contains(notificacio.getProcedimentCodi()) || codis.contains(notificacio.getOrganCodi());
            }

            notificacio.setPermisProcessar(permisProcessar);
            iniciPreparaEstat = System.currentTimeMillis();
            envs = not.getEnviaments();
            prepararColumnaEstat(notificacio, envs);
            fiPreparaEstat += System.currentTimeMillis() - iniciPreparaEstat;

            if (envs != null && !envs.isEmpty()) {
                env = envs.iterator().next();
                notificacio.setEnvCerData(env.getNotificaCertificacioData());
            }
            id = not != null && not.getNotificacio().getDocument() != null ? not.getNotificacio().getDocument().getId() : null;
            notificacio.setDocumentId(id);
            notificacio.setOrganEstat(not.getNotificacio().getOrganGestor() != null ? not.getNotificacio().getOrganGestor().getEstat() : null);
            notificacio.setErrorLastCallback(not.getNotificacio().isErrorLastCallback());
            notificacionsDto.add(notificacio);
        }
        long fi = System.currentTimeMillis() - inici;
        log.info("Total crear table item: " + fiCrearTableItem);
        log.info("Total preparar estat: " + fiPreparaEstat);
        log.info("For remeses: " + fi);
        return  paginacioHelper.toPaginaDto(notificacionsDto, notificacions);
    }

    private NotificacioTableItemDto crearTableItem(NotificacioTableEntity n) {
            return NotificacioTableItemDto.builder().id(n.getId())
                .contadorEstat(new HashMap<EnviamentEstat, Integer>())
                .tipusUsuari(n.getTipusUsuari())
                .notificaErrorData(n.getNotificaErrorData())
                .notificaErrorDescripcio(n.getNotificaErrorDescripcio())
                .enviamentTipus(n.getEnviamentTipus())
                .numExpedient(n.getNumExpedient())
                .concepte(n.getConcepte())
                .estatDate(n.getEstatDate())
                .estat(n.getEstat())
                .createdByNom(n.getCreatedBy() != null ? n.getCreatedBy().getNom() : null)
                .createdByCodi(n.getCreatedBy() != null ? n.getCreatedBy().getCodi()  : null)
                .createdDate(n.getCreatedDate().toDate())
                .permisProcessar(n.isPermisProcessar())
                .comunicacioSir(n.isComunicacioSir())
                .entitatNom(n.getEntitatNom())
                .procedimentCodi(n.getProcedimentCodi())
                .procedimentNom(n.getProcedimentNom())
                .procedimentTipus(n.getProcedimentTipus())
                .organCodi(n.getOrganCodi())
                .organNom(n.getOrganNom())
                .estatProcessatDate(n.getEstatProcessatDate())
                .enviadaDate(n.getEnviadaDate())
                .registreEnviamentIntent(n.getRegistreEnviamentIntent())
                .referencia(n.getReferencia())
                .build();
    }

    private void prepararColumnaEstat(NotificacioTableItemDto item, Set<NotificacioEnviamentEntity> enviaments) {

//        List<NotificacioEnviamentDatatableDto> enviaments = enviamentService.enviamentFindAmbNotificacio(item.getId());
        String estat = item.isEnviant() ? "<span class=\"fa fa-clock-o\"></span>" :
                NotificacioEstatEnumDto.PENDENT.equals(item.getEstat()) ? "<span class=\"fa fa-clock-o\"></span>" :
                        NotificacioEstatEnumDto.ENVIADA.equals(item.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-send-o\"></span>" :
                                NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-check\"></span>" :
                                        NotificacioEstatEnumDto.REGISTRADA.equals(item.getEstat()) ? "<span class=\"fa fa-file-o\"></span>" :
                                                NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) ? "<span class=\"fa fa-check-circle\"></span>" : "";
        String nomEstat = " " + messageHelper.getMessage("es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto." + (item.isEnviant() ? NotificacioEstatEnumDto.ENVIANT.name() : item.getEstat().name())) + "";
        boolean isFinal = NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat());
        NotificacioEventEntity e = !isFinal ? eventRepository.findLastErrorEventByNotificacioId(item.getId()) : null;
//        String error = item.isNotificaError() ? " <span class=\"fa fa-warning text-danger\" title=\"" + htmlEscape(item.getNotificaErrorDescripcio()) + " \"></span>"
//                        : e != null ? " <span class=\"fa fa-warning text-danger\" title=\"" + htmlEscape(e.getErrorDescripcio()) + " \"></span>" : "";
        String error = e != null && !Strings.isNullOrEmpty(e.getErrorDescripcio()) ? " <span class=\"fa fa-warning text-danger\" title=\"" + htmlEscape(e.getErrorDescripcio()) + " \"></span>" : "";
        error += TipusUsuariEnumDto.APLICACIO.equals(item.getTipusUsuari()) && item.isErrorLastCallback() ?
                " <span class=\"fa fa-exclamation-circle text-primary\" title=\"<spring:message code=\"notificacio.list.client.error/>\"></span>" : "";

        String fiReintents = "";
        List<NotificacioEventEntity> lastErrorEvent = eventRepository.findEventsAmbFiReintentsByNotificacioId(item.getId());
        String m = "";
        if (lastErrorEvent != null && !lastErrorEvent.isEmpty()) {
            String msg = "";
            String tipus = "";
            for (NotificacioEventEntity event : lastErrorEvent) {
                msg = messageHelper.getMessage("notificacio.event.fi.reintents");
                tipus = messageHelper.getMessage("es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto." + event.getTipus());
                m += msg + " -> " + tipus + "\n";
            }
        }
        int callbackFiReintents = eventRepository.countEventCallbackAmbFiReintentsByNotificacioId(item.getId());
        String callbackMsg = callbackFiReintents > 0 ? "<span class=\"fa fa-warning text-info\" title=\"" + messageHelper.getMessage("callback.fi.reintents") + "\"></span>" : "";
        fiReintents = !Strings.isNullOrEmpty(m) ? "<span class=\"fa fa-warning text-warning\" title=\"" + m + "\"></span>" : "";
        estat = "<span>" + estat + nomEstat + error + "  " + fiReintents + callbackMsg + "</span>";
        String data = "\n";
        if ((NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat())) && item.getEstatDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            String d = df.format(item.getEstatDate());
            data += "<span class=\"horaProcessat\">" + d + "</span>\n";
        } else if (NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) && item.getEstatProcessatDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            String d = df.format(item.getEstatProcessatDate());
            data += "<span class=\"horaProcessat\">" + d + "</span>\n";
        }

        String notificaEstat = "";
        String registreEstat = "";
        Map<String, Integer>  registres = new HashMap<>();
        boolean hasEnviamentsPendents = false;
        boolean isError = false;
        for (NotificacioEnviamentEntity env : enviaments) {
            item.updateEstatTipusCount(env.getNotificaEstat());
//                if (NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat()) || NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat())) {
//                    notificaEstat += getMessage(request, "es.caib.notib.client.domini.EnviamentEstat." + env.getNotificaEstat()) + ", ";
//                }
            if (env.getRegistreEstat() != null) {
                if (registres.containsKey(env.getRegistreEstat().name())) {
                    Integer count = registres.get(env.getRegistreEstat().name());
                    count = count + 1;
                    registres.put(env.getRegistreEstat().name(), count);
                } else {
                    registres.put(env.getRegistreEstat().name(), 1);
                }
            }
            if (item.isComunicacioSir()) {
                NotificacioRegistreEstatEnumDto r = env.getRegistreEstat();
                registreEstat += env.getRegistreEstat() != null ?  "<div><span style=\"padding-bottom:1px; background-color: " + r.getColor() + ";\" title=\"" +
                        messageHelper.getMessage("es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto." + r)
                        + "\" class=\"label label-primary\">" + r.getBudget() + "</span></div>" : "";
            }
            if (!env.isNotificaEstatFinal() && EnviamentEstat.NOTIB_PENDENT.equals(env.getNotificaEstat())) {
                hasEnviamentsPendents = true;
            }
        }
        item.setHasEnviamentsPendentsRegistre(hasEnviamentsPendents);
        notificaEstat = notificaEstat.length() > 0 ? notificaEstat.substring(0, notificaEstat.length()-2) : "";
        estat = "<div class=\"flex-column\"><div style=\"display:flex; justify-content:space-between\">" + estat + (registreEstat.length() > 0 ? registreEstat : "")
                + "</div></div>" + data + notificaEstat;
        String padding = "; padding-left: 5px;";
        String boxShadow = "box-shadow: inset 3px 0px 0px ";

        if (NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) || notificaEstat.length() > 0 || item.getContadorEstat().size() > 1) {

            for (Map.Entry<EnviamentEstat, Integer> entry : item.getContadorEstat().entrySet()) {
                estat += "<div style=\"font-size:11px;" + boxShadow + entry.getKey().getColor() + padding + "\">" +
                        entry.getValue() + " " + messageHelper.getMessage("es.caib.notib.client.domini.EnviamentEstat." + entry.getKey())
                        + "</div>";
            }
        }

        item.setEstatString(estat);
    }

    public NotificacioFiltre getFiltre(NotificacioFiltreDto filtreDto) {

        OrganGestorEntity organGestor = null;
        if (filtreDto.getOrganGestor() != null && !filtreDto.getOrganGestor().isEmpty()) {
            organGestor = organGestorRepository.findOne(Long.parseLong(filtreDto.getOrganGestor()));
        }
        ProcSerEntity procediment = null;
        if (filtreDto.getProcedimentId() != null) {
            procediment = procedimentRepository.findById(filtreDto.getProcedimentId());
        } else if (filtreDto.getServeiId() != null) {
            procediment = serveiRepository.findById(filtreDto.getServeiId());
        }
        NotificacioEstatEnumDto estat = filtreDto.getEstat();
//        Boolean hasZeronotificaEnviamentIntent = null;
        boolean isEstatNull = estat == null;
        boolean nomesSenseErrors = false;
        boolean nomesAmbErrors = filtreDto.isNomesAmbErrors();
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
                .organGestor(new FiltreField<>(organGestor))
                .procediment(new FiltreField<>(procediment))
                .tipusUsuari(new FiltreField<>(filtreDto.getTipusUsuari()))
                .numExpedient(new StringField(filtreDto.getNumExpedient()))
                .creadaPer(new StringField(filtreDto.getCreadaPer()))
                .identificador(new StringField(filtreDto.getIdentificador()))
                .nomesAmbErrors(new FiltreField<>(nomesAmbErrors))
                .nomesSenseErrors(new FiltreField<>(nomesSenseErrors))
//                .hasZeronotificaEnviamentIntent(new FiltreField<>(hasZeronotificaEnviamentIntent))
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
