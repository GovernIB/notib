package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.helper.FiltreHelper.FiltreField;
import es.caib.notib.logic.helper.FiltreHelper.StringField;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.objectes.FiltreNotificacio;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.ServeiRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
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
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private ProcSerHelper procedimentHelper;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private OrganigramaHelper organigramaHelper;

    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private ServeiRepository serveiRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private NotificacioEventRepository eventRepository;


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

    public PaginaDto<NotificacioTableItemDto> complementaNotificacions(EntitatEntity entitatEntity, String usuariCodi, Page<NotificacioTableEntity> notificacions) {

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
        List<NotificacioTableItemDto> notificacionsDto = new ArrayList<>();
        var inici = System.currentTimeMillis();
        long iniciPreparaEstat;
        var fiPreparaEstat = 0;
        long iniciCrearTableItem;
        var fiCrearTableItem = 0;
        NotificacioTableItemDto notificacio;
        Set<NotificacioEnviamentEntity> envs;
        NotificacioEnviamentEntity env;
        Long id;
        var nots = notificacions.getContent();
        var size = nots.size();
        NotificacioTableEntity not;
        for (var foo = 0; foo < size;foo++) {
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
                .contadorEstat(new HashMap<>())
                .tipusUsuari(n.getTipusUsuari())
                .notificaErrorData(n.getNotificaErrorData())
                .notificaErrorDescripcio(n.getNotificaErrorDescripcio())
                .enviamentTipus(n.getEnviamentTipus())
                .numExpedient(n.getNumExpedient())
                .concepte(n.getConcepte())
                .estatDate(n.getEstatDate())
                .estat(n.getEstat())
                .createdByNom(n.getCreatedBy().isPresent() ? n.getCreatedBy().orElseThrow().getNom() : null)
                .createdByCodi(n.getCreatedBy().isPresent() ? n.getCreatedBy().orElseThrow().getCodi()  : null)
                .createdDate(Date.from(n.getCreatedDate().orElseThrow().atZone(ZoneId.systemDefault()).toInstant()))
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

        StringBuilder estat = new StringBuilder(item.isEnviant() ? "<span class=\"fa fa-clock-o\"></span>" :
                NotificacioEstatEnumDto.PENDENT.equals(item.getEstat()) ? "<span class=\"fa fa-clock-o\"></span>" :
                        NotificacioEstatEnumDto.ENVIADA.equals(item.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-send-o\"></span>" :
                                NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-check\"></span>" :
                                        NotificacioEstatEnumDto.REGISTRADA.equals(item.getEstat()) ? "<span class=\"fa fa-file-o\"></span>" :
                                                NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) ? "<span class=\"fa fa-check-circle\"></span>" : "");

        var nomEstat = " " + messageHelper.getMessage("es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto." + (item.isEnviant() ? NotificacioEstatEnumDto.ENVIANT.name() : item.getEstat().name())) + "";
        boolean isFinal = NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat());
        var e = !isFinal ? eventRepository.findLastErrorEventByNotificacioId(item.getId()) : null;
//        String error = item.isNotificaError() ? " <span class=\"fa fa-warning text-danger\" title=\"" + htmlEscape(item.getNotificaErrorDescripcio()) + " \"></span>"
//                        : e != null ? " <span class=\"fa fa-warning text-danger\" title=\"" + htmlEscape(e.getErrorDescripcio()) + " \"></span>" : "";
        var error = e != null && !Strings.isNullOrEmpty(e.getErrorDescripcio()) ? " <span class=\"fa fa-warning text-danger\" title=\""
                + (enviaments.size() == 1 ? htmlEscape(e.getErrorDescripcio()) : messageHelper.getMessage("error.notificacio.enviaments")) + " \"></span>" : "";
        error += TipusUsuariEnumDto.APLICACIO.equals(item.getTipusUsuari()) && item.isErrorLastCallback() ?
                " <span class=\"fa fa-exclamation-circle text-primary\" title=\"<spring:message code=\"notificacio.list.client.error/>\"></span>" : "";

        List<NotificacioEventEntity> lastErrorEvent = eventRepository.findEventsAmbFiReintentsByNotificacioId(item.getId());
        StringBuilder m = new StringBuilder();
        if (lastErrorEvent != null && !lastErrorEvent.isEmpty()) {
            String msg;
            String tipus;
            var env = 1;
            for (var event : lastErrorEvent) {
                msg = messageHelper.getMessage("notificacio.event.fi.reintents");
                tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + event.getTipus());
                m.append("Env ").append(env).append(": ").append(msg).append(" -> ").append(tipus).append("\n");
                env++;
            }
        }
        int callbackFiReintents = eventRepository.countEventCallbackAmbFiReintentsByNotificacioId(item.getId());
        var callbackMsg = callbackFiReintents > 0 ? "<span class=\"fa fa-warning text-info\" title=\"" + messageHelper.getMessage("callback.fi.reintents") + "\"></span>" : "";
        var fiReintents = !Strings.isNullOrEmpty(m.toString()) ? "<span class=\"fa fa-warning text-warning\" title=\"" + m + "\"></span>" : "";

        var data = "\n";
        if ((NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat())) && item.getEstatDate() != null) {
            var df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            var d = df.format(item.getEstatDate());
            data += "<span class=\"horaProcessat\">" + d + "</span>\n";
        } else if (NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) && item.getEstatProcessatDate() != null) {
            var df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String d = df.format(item.getEstatProcessatDate());
            data += "<span class=\"horaProcessat\">" + d + "</span>\n";
        }
        var notificaEstat = "";
        StringBuilder registreEstat = new StringBuilder();
        Map<String, Integer>  registres = new HashMap<>();
        var hasEnviamentsPendents = false;
        StringBuilder notificacioMovilMsg = new StringBuilder();
        int multipleApiCarpetaError = 0;
        for (NotificacioEnviamentEntity env : enviaments) {
            item.updateEstatTipusCount(env.getNotificaEstat());
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
                registreEstat.append(env.getRegistreEstat() != null ? "<div><span style=\"padding-bottom:1px; background-color: " + r.getColor() + ";\" title=\"" +
                        messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + r)
                        + "\" class=\"label label-primary\">" + r.getBudget() + "</span></div>" : "");
            }
            if (!env.isNotificaEstatFinal() && EnviamentEstat.NOTIB_PENDENT.equals(env.getNotificaEstat())) {
                hasEnviamentsPendents = true;
            }
            if (env.isPerEmail() || env.getNotificaEstat() == null) {
                continue;
            }
            var eventCarpeta = eventRepository.findLastApiCarpetaByEnviamentId(env.getId());
            if (eventCarpeta != null && eventCarpeta.isError()) {
                multipleApiCarpetaError++;
                notificacioMovilMsg.append("<span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"").append(eventCarpeta.getErrorDescripcio()).append("\"></span>\n");
            }
        }
        if (multipleApiCarpetaError > 1) {
            notificacioMovilMsg = new StringBuilder("<span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"" + messageHelper.getMessage("api.carpeta.send.notificacio.movil.error") + "\"></span>\n");
        }
        item.setHasEnviamentsPendentsRegistre(hasEnviamentsPendents);
        notificaEstat = notificaEstat.length() > 0 ? notificaEstat.substring(0, notificaEstat.length()-2) : "";
        estat = new StringBuilder("<span>" + estat + nomEstat + error + "  " + fiReintents + callbackMsg + notificacioMovilMsg + "</span>");
        estat = new StringBuilder("<div class=\"flex-column\"><div style=\"display:flex; justify-content:space-between\">" + estat + (registreEstat.length() > 0 ? registreEstat.toString() : "")
                + "</div></div>" + data + notificaEstat);
        var padding = "; padding-left: 5px;";
        var boxShadow = "box-shadow: inset 3px 0px 0px ";
        if (NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) || notificaEstat.length() > 0 || item.getContadorEstat().size() > 1) {

            for (Map.Entry<EnviamentEstat, Integer> entry : item.getContadorEstat().entrySet()) {
                estat.append("<div style=\"font-size:11px;").append(boxShadow).append(entry.getKey().getColor()).append(padding).append("\">").append(entry.getValue())
                        .append(" ").append(messageHelper.getMessage("es.caib.notib.client.domini.EnviamentEstat." + entry.getKey())).append("</div>");
            }
        }
        item.setEstatString(estat.toString());
    }

    public FiltreNotificacio getFiltre(NotificacioFiltreDto f, Long entitatId, RolEnumDto rol, String usuariCodi, List<String> rols) {

        OrganGestorEntity organGestor = null;
        if (f.getOrganGestor() != null && !f.getOrganGestor().isEmpty()) {
            organGestor = organGestorRepository.findById(Long.parseLong(f.getOrganGestor())).orElse(null);
        }
        ProcSerEntity procediment = null;
        if (f.getProcedimentId() != null) {
            procediment = procedimentRepository.findById(f.getProcedimentId()).orElse(null);
        } else if (f.getServeiId() != null) {
            procediment = serveiRepository.findById(f.getServeiId()).orElse(null);
        }
        var isUsuari = RolEnumDto.tothom.equals(rol);
        var isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
        var isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
        var isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
        var entitatActual = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat,false);
        var nomesSenseErrors = false;
        var nomesAmbErrors = f.isNomesAmbErrors();
        List<String> codisProcedimentsDisponibles = new ArrayList<>();
        List<String> codisOrgansGestorsDisponibles = new ArrayList<>();
        List<String> codisProcedimentsOrgans = new ArrayList<>();
        if (isUsuari && entitatActual != null) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
            // Procediments accessibles per qualsevol òrgan gestor
            codisProcedimentsDisponibles = procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatActual, PermisEnum.CONSULTA);
            // Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
            codisOrgansGestorsDisponibles = organGestorHelper.findCodiOrgansGestorsWithPermis(auth, entitatActual, PermisEnum.CONSULTA);
            // Procediments comuns que es poden consultar per a òrgans gestors concrets
            codisProcedimentsOrgans = permisosService.getProcedimentsOrgansAmbPermis(entitatActual.getId(), auth.getName(), PermisEnum.CONSULTA);
        } else if (isAdminOrgan && entitatActual != null) {
            codisProcedimentsDisponibles = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestor.getCodi());
        }

        var esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
        var esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
        var esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());
        var organs = isAdminOrgan && organGestor != null ? organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestor.getCodi()) : null;
        var entitatsActives = isSuperAdmin ? entitatRepository.findByActiva(true) : null;
        var entitatFiltre = isUsuariEntitat || isUsuari ? entitatId : f.getEntitatId();
        return FiltreNotificacio.builder()
                .entitatIdNull(entitatFiltre == null)
                .entitatId(entitatFiltre)
                .entitat(entitatActual)
                .enviamentTipusNull(f.getEnviamentTipus() == null)
                .enviamentTipus(f.getEnviamentTipus())
                .concepteNull(Strings.isNullOrEmpty(f.getConcepte()))
                .concepte(f.getConcepte())
                .estatNull(f.getEstat() == null)
                .estatMask(f.getEstat() == null ? 0 : f.getEstat().getMask())
                .dataIniciNull(f.getDataInici() == null)
                .dataInici(f.getDataInici())
                .dataFiNull(f.getDataFi() == null)
                .dataFi(f.getDataFi())
                .titularNull(Strings.isNullOrEmpty(f.getTitular()))
                .titular(f.getTitular())
                .organCodiNull(organGestor == null)
                .organCodi(organGestor.getCodi())
                .procedimentNull(procediment == null)
                .procedimentCodi(procediment.getCodi())
                .tipusUsuariNull(f.getTipusUsuari() == null)
                .tipusUsuari(f.getTipusUsuari())
                .numExpedientNull(Strings.isNullOrEmpty(f.getNumExpedient()))
                .numExpedient(f.getNumExpedient())
                .creadaPerNull(Strings.isNullOrEmpty(f.getCreadaPer()))
                .creadaPer(f.getCreadaPer())
                .identificadorNull(Strings.isNullOrEmpty(f.getIdentificador()))
                .identificador(f.getIdentificador())
                .nomesAmbErrors(nomesAmbErrors)
                .nomesSenseErrors(nomesSenseErrors)
                .referenciaNull(Strings.isNullOrEmpty(f.getReferencia()))
                .referencia(f.getReferencia())
                .isUsuari(isUsuari)
                .procedimentsCodisNotibNull(esProcedimentsCodisNotibNull)
                .procedimentsCodisNotib(esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles)
                .grupsProcedimentCodisNotib(rols)
                .organsGestorsCodisNotibNull(esOrgansGestorsCodisNotibNull)
                .organsGestorsCodisNotib(esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles)
                .procedimentOrgansIdsNotibNull(esProcedimentOrgansAmbPermisNull)
                .procedimentOrgansIdsNotib(esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans)
                .usuariCodi(usuariCodi)
                .isSuperAdmin(isSuperAdmin)
                .entitatsActives(entitatsActives)
                .isAdminOrgan(isAdminOrgan)
                .organs(organs)
                .notMassivaIdNull(f.getNotMassivaId() == null)
                .notMassivaId(f.getNotMassivaId()).build();
//        return NotificacioFiltre.builder()
//                .entitatId(new FiltreField<>(filtreDto.getEntitatId()))
//                .comunicacioTipus(new FiltreField<>(filtreDto.getComunicacioTipus()))
//                .enviamentTipus(new FiltreField<>(filtreDto.getEnviamentTipus()))
//                .estat(new FiltreField<>(estat, isEstatNull))
//                .concepte(new StringField(filtreDto.getConcepte()))
//                .dataInici(new FiltreField<>(FiltreHelper.toIniciDia(filtreDto.getDataInici())))
//                .dataFi(new FiltreField<>(FiltreHelper.toFiDia(filtreDto.getDataFi())))
//                .titular(new StringField(filtreDto.getTitular()))
//                .organGestor(new FiltreField<>(organGestor))
//                .procediment(new FiltreField<>(procediment))
//                .tipusUsuari(new FiltreField<>(filtreDto.getTipusUsuari()))
//                .numExpedient(new StringField(filtreDto.getNumExpedient()))
//                .creadaPer(new StringField(filtreDto.getCreadaPer()))
//                .identificador(new StringField(filtreDto.getIdentificador()))
//                .nomesAmbErrors(new FiltreField<>(nomesAmbErrors))
//                .nomesSenseErrors(new FiltreField<>(nomesSenseErrors))
//                .referencia(new StringField(filtreDto.getReferencia()))
//                .build();
    }

//    @Builder
//    @Getter
//    @Setter
//    public static class NotificacioFiltre implements Serializable {
//
//        private FiltreField<Long> entitatId;
//        private FiltreField<NotificacioComunicacioTipusEnumDto> comunicacioTipus;
//        private FiltreField<EnviamentTipus> enviamentTipus;
//        private FiltreField<NotificacioEstatEnumDto> estat;
//        private StringField concepte;
//        private FiltreField<Date> dataInici;
//        private FiltreField<Date> dataFi;
//        private StringField titular;
//        private FiltreField<OrganGestorEntity> organGestor;
//        private FiltreField<ProcSerEntity> procediment;
//        private FiltreField<TipusUsuariEnumDto> tipusUsuari;
//        private StringField numExpedient;
//        private StringField creadaPer;
//        private StringField identificador;
//        private StringField referencia;
//        private FiltreField<Boolean> nomesAmbErrors;
//        private FiltreField<Boolean> nomesSenseErrors;
//        private FiltreField<Boolean> hasZeronotificaEnviamentIntent;
//    }

}
