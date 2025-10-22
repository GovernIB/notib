package es.caib.notib.logic.mapper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.helper.EnviamentHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.NotificacioListHelper;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

@Slf4j
@Mapper(componentModel = "spring", uses = {CommonConversor.class}, builder = @org.mapstruct.Builder(disableBuilder = true))
public abstract class NotificacioTableMapper {

    private static final String ICONA_ENVIANT = "<span class=\"fa fa-clock-o\"></span>";
    private static final String ICONA_PENDENT = "<span class=\"fa fa-clock-o\"></span>";
    private static final String ICONA_ENVIADA = "<span class=\"fa fa-send-o\"></span>";
    private static final String ICONA_FINALITZADA = "<span class=\"fa fa-check\"></span>";
    private static final String ICONA_REGISTRADA = "<span class=\"fa fa-file-o\"></span>";
    private static final String ICONA_PROCESSADA = "<span class=\"fa fa-check-circle\"></span>";
    private static final String SENSE_ICONA = "";

    private static final String getMessage = "<getMessage>";
    private static final String fiGetMessage = "</getMessage>";


    @Autowired
    NotificacioListHelper notificacioListHelper;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private NotificacioEventRepository eventRepository;
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;


    @Mapping(target = "registreEnviamentIntent", source = "not.registreEnviamentIntent", defaultValue = "0")
    @Mapping(target = "createdDate", source = "not.createdDate", qualifiedByName = "optionalDate")
    @Mapping(target = "createdByNom", source = "not.createdBy", qualifiedByName = "optionalUserName")
    @Mapping(target = "createdByCodi", source = "not.createdBy", qualifiedByName = "optionalUserCode")
    @Mapping(target = "permisProcessar", source = "params.permisProcessar")
    @Mapping(target = "organEstat", source = "params.organEstat")
    @Mapping(target = "anulable", expression = "java(isAnulable(not))")
    public abstract NotificacioTableItemDto toNotificacioTableItemDto(NotificacioTableEntity not, NotificacioTableItemConversioParams params);

    public abstract List<NotificacioTableItemDto> toNotificacionsTableItemDto(List<NotificacioTableEntity> nots, @Context List<String> codis, @Context Map<String, OrganismeDto> organs);

    protected boolean isAnulable(NotificacioTableEntity notificacio) {

        for (var enviament : notificacio.getEnviaments()) {
            if (!(!enviament.isAnulat() && !Strings.isNullOrEmpty(enviament.getNotificaIdentificador()) && !enviament.isNotificaEstatFinal() && !enviament.isCieEstatFinal()
                    && (enviament.getEntregaPostal() == null || CieEstat.ENVIADO_CI.equals(enviament.getEntregaPostal().getCieEstat())
                    && enviament.getNotificacio().getOrganGestor().getEntregaCie().getCie().isCieExtern()))) {
                return false;
            }
        }
        return true;
    }

    public NotificacioTableItemDto mapNotificacioTableItemDtoContext(NotificacioTableEntity not, @Context List<String> codis, @Context Map<String, OrganismeDto> organs) {

        if (not == null) {
            return null;
        }

        var paramBuilder = NotificacioTableItemConversioParams.builder();

        if (not.getProcedimentCodi() != null && NotificacioEstatEnumDto.FINALITZADA.equals(not.getEstat())) {
            paramBuilder.permisProcessar(codis.contains(not.getProcedimentCodi()) || codis.contains(not.getOrganCodi()));
        }
        if (not.getOrganCodi() != null) {
            var organ = organs.get(not.getOrganCodi());
            paramBuilder.organEstat(organ != null ? organ.getEstat() : null);
        }

        return toNotificacioTableItemDto(not, paramBuilder.build());
    }

    @AfterMapping
    protected void addColumnaEstat(NotificacioTableEntity not, @MappingTarget NotificacioTableItemDto dto) {

        if (not == null) {
            return;
        }
        if (not.isPerActualitzar()) {
            actualitzar(not, dto);
        }

        var estat = not.getEstatString();
        var codis = StringUtils.substringsBetween(estat, getMessage, fiGetMessage);
        List<String> traduccions = new ArrayList<>();
        if (codis == null) {
            return;
        }
        for (var codi : codis) {
            traduccions.add(messageHelper.getMessage(codi));
        }
        for (var traduccio : traduccions) {
            estat = estat.replaceFirst(getMessage + ".*?" + fiGetMessage, traduccio);
        }
        dto.setEstatString(estat);
    }

    private void actualitzar(NotificacioTableEntity not, NotificacioTableItemDto dto) {

        var iniciActualitzar = System.currentTimeMillis();
        var enviaments = not.getEnviaments();
        if (dto.getDocumentId() == null) {
            dto.setDocumentId(not.getNotificacio().getDocument() != null ? not.getNotificacio().getDocument().getId() : null);
        }
        var registreNums = new StringBuilder();
        if (enviaments != null && !enviaments.isEmpty()) {
            var certificacio = false;
            for (var env : enviaments) {
                if (env.getNotificaCertificacioData() != null && !certificacio) {
                    dto.setEnvCerData(env.getNotificaCertificacioData());
                    certificacio = true;
                }
                if (!Strings.isNullOrEmpty(env.getRegistreNumeroFormatat())) {
                    registreNums.append(env.getRegistreNumeroFormatat()).append(", ");
                }
            }
        }
//        var fi = System.currentTimeMillis();
//        log.info("actualitzar part1 -> " + (fi - inici));
        dto.setErrorLastCallback(not.getNotificacio().isErrorLastCallback());
        dto.setEstatString(getColumnaEstat(dto, enviaments));
        dto.setDeleted(not.isDeleted());
        // TODO: Fer-ho amb un servei apart amb transaccionalitat independent
        // Actualitzam l'entitat
        try {
            not.setDocumentId(dto.getDocumentId());
            not.setEnvCerData(dto.getEnvCerData());
            not.setEstatString(dto.getEstatString());
            var rNums = registreNums.substring(0, registreNums.length()-2);
            if (rNums.length() > 2000) {
                rNums = rNums.substring(0, 2000) + "...";
            }
            not.setRegistreNums(rNums);
            not.setPerActualitzar(false);
            var inici = System.currentTimeMillis();
            notificacioTableViewRepository.saveAndFlush(not);
            var fi = System.currentTimeMillis();
            NotibLogger.getInstance().info("Guardar a not_table -> " + (fi - inici), log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
            NotibLogger.getInstance().info("Actualitzar notificacio -> " + (fi - iniciActualitzar), log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        } catch (Exception ex) {
            // TODO: Si no es pot actualitzar, no es fa res. Es calcularà en cada consulta com fins ara!
        }
    }

    private String getColumnaEstat(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        NotibLogger.getInstance().info("Actualitzant la columna estat de la remesa " + dto.getId(), log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        var columanEstatInici = System.currentTimeMillis();
        //Entrega postal
        String entregaPostal = getEntregaPostal(enviaments);
        // Estat
        String iconaEstat = getIconaEstat(dto);
        String nomEstat = getNomEstat(dto);
        // Errors
        var inici = System.currentTimeMillis();
        String eventError = getEventError(dto, enviaments);
        var fi = System.currentTimeMillis();
        var  duracio = fi - inici;
        NotibLogger.getInstance().info("getEventError -> " + duracio, log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        inici = System.currentTimeMillis();
        String callbackError = getCallbackError(dto);
        fi = System.currentTimeMillis();
        duracio = fi - inici;
        NotibLogger.getInstance().info("getCallbackError -> " + duracio,  log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        inici = System.currentTimeMillis();
        String notificaMovilError = getNotificaMovilError(dto, enviaments);
        fi = System.currentTimeMillis();
        duracio = fi - inici;
        NotibLogger.getInstance().info("getNotificaMovilError -> " + duracio,  log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        // Data
        String dataEstat = getDataEstat(dto);
        // Estats enviaments
        String registreEstat = getRegistreEstat(dto, enviaments);
        String notificaEstats = dto.isComunicacioSir() ? getSirEstats(dto, enviaments) : getNotificaEstats(dto, enviaments);
        var anulat = getAnulat(enviaments);

        var columnaEstat = new StringBuilder(entregaPostal + "<div class=\"flex-column\">")
                .append("<div style=\"display:flex; justify-content:space-between\">")
                .append("<span>")
                .append(registreEstat)
                .append(iconaEstat)
                .append(nomEstat)
                .append(anulat)
                .append(eventError)
                .append(callbackError)
                .append(notificaMovilError)
                .append("</span>")
                .append("</div>")
                .append("</div>")
                .append(dataEstat)
                .append(notificaEstats);

        fi = System.currentTimeMillis();
        duracio = fi - columanEstatInici;
        NotibLogger.getInstance().info("getColumnaEstat -> " + duracio, log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        return columnaEstat.toString();
    }

    private String getAnulat(Set<NotificacioEnviamentEntity> enviaments) {

        var anulat = false;
        for (var enviament : enviaments) {
            if (enviament.isAnulat()) {
                anulat = true;
            }
        }
        var title = messageHelper.getMessage("notificacio.enviament.anulat");
        return anulat ?  " <span class=\"fa fa-ban\" title=\"" + title + "\"></span>" : "";
    }

    private String getEntregaPostal(Set<NotificacioEnviamentEntity> enviaments) {

        var entregaPostal = false;
        var errorEntregaPostal = false;
        for (var enviament : enviaments) {

            if (enviament.getEntregaPostal() != null) {
                entregaPostal = true;
                errorEntregaPostal = errorEntregaPostal || enviament.getEntregaPostal().errorEntregaPostal();
            }

        }
        if (!entregaPostal) {
            return "";
        }
        var title = messageHelper.getMessage(errorEntregaPostal ? "entrega.postal.erronia.icona.tooltip" : "entrega.postal.icona.tooltip");
        return "<span class=\"label " + (errorEntregaPostal ? "label-danger"  : "label-success") + "\" title=\"" + title +"\" style=\"float: right; position: relative; top: 0px;\"><span class=\"fa fa-envelope\"></span></span>";
    }


    private String getIconaEstat(NotificacioTableItemDto dto) {
        if (dto.isEnviant()) return ICONA_ENVIANT;
        if (NotificacioEstatEnumDto.PENDENT.equals(dto.getEstat())) return ICONA_PENDENT;
        if (NotificacioEstatEnumDto.ENVIADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(dto.getEstat())) return ICONA_ENVIADA;
        if (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())) return ICONA_FINALITZADA;
        if (NotificacioEstatEnumDto.REGISTRADA.equals(dto.getEstat())) return ICONA_REGISTRADA;
        if (NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat())) return ICONA_PROCESSADA;
        return SENSE_ICONA;
    }

    private String getNomEstat(NotificacioTableItemDto dto) {
        return " " + getMessage + "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto." + (dto.isEnviant() ? NotificacioEstatEnumDto.ENVIANT.name() : dto.getEstat().name()) + fiGetMessage;
    }

    private String getEventError(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        var error = "";
//        boolean isFinal = NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat());
//        var eventError = !isFinal ? eventRepository.findLastErrorEventByNotificacioId(dto.getId()) : null;
//        var eventError = eventRepository.findLastErrorEventByNotificacioId(dto.getId());
        NotificacioEventEntity event = null;
        String msg = getMessage + "notificacio.event.fi.reintents" + fiGetMessage;
        String tipus;
        int numEnv = 1;
        StringBuilder fiReintentsError = new StringBuilder();
        for (var env : enviaments) {
            event = env.getUltimEvent();
            if (event == null || Strings.isNullOrEmpty(event.getErrorDescripcio())) {
                continue;
            }
            var desc = event.getErrorDescripcio();
            if (desc.length() > 500) {
                desc = desc.substring(0, 500);
            }
            if (error.isEmpty()) {
                error = " <span class=\"fa fa-warning text-danger\" title=\"" + (enviaments.size() == 1 ? htmlEscape(desc) : getMessage + "error.notificacio.enviaments" + fiGetMessage) + " \"></span>";
            }
            if (Boolean.TRUE.equals(event.getFiReintents())) {
                var et = NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(event.getTipus()) && event.getEnviament().isSirFiPooling() ? NotificacioEventTipusEnumDto.SIR_FI_POOLING : event.getTipus();
                tipus = getMessage + "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + et + fiGetMessage;
                fiReintentsError.append("Env ").append(numEnv++).append(": ").append(msg).append(" -> ").append(tipus).append("\n");
            }
        }

//        if (eventError != null && !Strings.isNullOrEmpty(eventError.getErrorDescripcio())) {
//            var desc = eventError.getErrorDescripcio();
//            if (desc.length() > 500) {
//                desc = desc.substring(0, 500);
//            }
//            error = " <span class=\"fa fa-warning text-danger\" title=\"" + (enviaments.size() == 1 ? htmlEscape(desc) : getMessage + "error.notificacio.enviaments" + fiGetMessage)+ " \"></span>";
//        }
        if (TipusUsuariEnumDto.APLICACIO.equals(dto.getTipusUsuari()) && dto.isErrorLastCallback()) {
            error += " <span class=\"fa fa-exclamation-circle text-primary\" title=\"" +  getMessage + "notificacio.list.client.error" + fiGetMessage + "\"></span>";
        }

        error += fiReintentsError.length() > 0 ? " <span class=\"fa fa-warning text-warning\" title=\"" + fiReintentsError + "\"></span>" : "";
        return error;
    }

    private String getCallbackError(NotificacioTableItemDto dto) {

        int callbackFiReintents = eventRepository.countEventCallbackAmbFiReintentsByNotificacioId(dto.getId());
        return callbackFiReintents > 0 ? " <span class=\"fa fa-warning text-info\" title=\"" + getMessage + "callback.fi.reintents" + fiGetMessage + "\"></span>" : "";
    }

//    private String getFiReintentsError(NotificacioTableItemDto dto) {
//
//        List<NotificacioEventEntity> lastErrorEvent = eventRepository.findEventsAmbFiReintentsByNotificacioId(dto.getId());
//        StringBuilder fiReintentsError = new StringBuilder();
//        if (lastErrorEvent != null && !lastErrorEvent.isEmpty()) {
//            String msg = getMessage + "notificacio.event.fi.reintents" + fiGetMessage;
//            String tipus;
//            int env = 1;
//            for (var event : lastErrorEvent) {
//                var et = NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(event.getTipus()) && event.getEnviament().isSirFiPooling() ? NotificacioEventTipusEnumDto.SIR_FI_POOLING : event.getTipus();
//                tipus = getMessage + "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + et + fiGetMessage;
//                fiReintentsError.append("Env ").append(env++).append(": ").append(msg).append(" -> ").append(tipus).append("\n");
//            }
//        }
//        return fiReintentsError.length() > 0 ? " <span class=\"fa fa-warning text-warning\" title=\"" + fiReintentsError + "\"></span>" : "";
//    }

    private String getNotificaMovilError(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {
        StringBuilder notificacioMovilMsg = new StringBuilder();
        int multipleApiCarpetaError = 0;
        for (NotificacioEnviamentEntity env : enviaments) {
            if (env.isPerEmail() || env.getNotificaEstat() == null) {
                continue;
            }

            var eventCarpeta = eventRepository.findLastApiCarpetaByEnviamentId(env.getId());
            if (eventCarpeta != null && !eventCarpeta.isEmpty() && eventCarpeta.get(0).isError()) {
                multipleApiCarpetaError++;
                notificacioMovilMsg.append(" <span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"").append(eventCarpeta.get(0).getErrorDescripcio()).append("\"></span>\n");
            }
        }
        if (multipleApiCarpetaError > 1) {
            notificacioMovilMsg = new StringBuilder("<span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"" + getMessage + "api.carpeta.send.notificacio.movil.error" + fiGetMessage + "\"></span>\n");
        }
        return notificacioMovilMsg.length() > 0 ? notificacioMovilMsg.toString() : "";
    }

    private String getDataEstat(NotificacioTableItemDto dto) {
        var df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        var dataEstat = "\n";
        if ((NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())) && dto.getEstatDate() != null) {
            dataEstat += "<span class=\"horaProcessat\">" + df.format(dto.getEstatDate()) + "</span>\n";
        } else if (NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) && dto.getEstatProcessatDate() != null) {
            dataEstat += "<span class=\"horaProcessat\">" + df.format(dto.getEstatProcessatDate()) + "</span>\n";
        }
        return dataEstat;
    }

    private String getRegistreEstat(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {
        StringBuilder registreEstat = new StringBuilder();
        if (dto.isComunicacioSir()) {
            for (NotificacioEnviamentEntity env : enviaments) {
                NotificacioRegistreEstatEnumDto regEstat = env.getRegistreEstat();
                if (regEstat != null) {
                    registreEstat.append("<span style=\"margin-right: 3px;\"><span style=\"padding-bottom:1px; background-color: " + regEstat.getColor() + ";\" title=\"" +
                            getMessage +"es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + regEstat + fiGetMessage
                            + "\" class=\"label label-primary\">" + regEstat.getBudget() + "</span></span>");
                }
            }
        }
        return registreEstat.length() > 0 ? registreEstat.toString() : "";
    }

    private String getSirEstats(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        StringBuilder notificacioEstat = new StringBuilder();
        for (NotificacioEnviamentEntity env : enviaments) {
            dto.updateEstatSirTipusCount(env.getRegistreEstat());
        }
        if (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || dto.getContadorEstat().size() > 1) {

            for (var entry : dto.getContadorEstatSir().entrySet()) {
                notificacioEstat.append("<div style=\"font-size:11px; box-shadow: inset 3px 0px 0px ").append(entry.getKey().getColor()).append("; padding-left: 5px;").append("\">")
                        .append(entry.getValue()).append(" ").append(getMessage + "es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + entry.getKey() + fiGetMessage)
                        .append("</div>");
            }
        }
        return notificacioEstat.length() > 0 ? notificacioEstat.toString() : "";
    }

    private String getNotificaEstats(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        StringBuilder notificacioEstat = new StringBuilder();
        for (NotificacioEnviamentEntity env : enviaments) {
            if (dto.isComunicacioSir()) {

            }
            var estat = env.getEntregaPostal() != null && CieEstat.NOTIFICADA.equals(env.getEntregaPostal().getCieEstat()) ? EnviamentEstat.NOTIFICADA : env.getNotificaEstat();
//            dto.updateEstatTipusCount(env.getNotificaEstat());
            dto.updateEstatTipusCount(estat);
        }
        if (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || dto.getContadorEstat().size() > 1) {

            for (Map.Entry<EnviamentEstat, Integer> entry : dto.getContadorEstat().entrySet()) {
                notificacioEstat.append("<div style=\"font-size:11px; box-shadow: inset 3px 0px 0px ").append(entry.getKey().getColor()).append("; padding-left: 5px;").append("\">")
                        .append(entry.getValue()).append(" ").append(getMessage + "es.caib.notib.client.domini.EnviamentEstat." + entry.getKey() + fiGetMessage)
                        .append("</div>");
            }
        }
        return notificacioEstat.length() > 0 ? notificacioEstat.toString() : "";
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    protected static class NotificacioTableItemConversioParams {
        boolean permisProcessar;
        OrganGestorEstatEnum organEstat;
    }
}
