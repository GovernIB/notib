package es.caib.notib.logic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.NotificacioListHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
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
import org.apache.xerces.xs.datatypes.ObjectList;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

@Slf4j
@Mapper(componentModel = "spring", uses = {CommonConversor.class}, builder = @org.mapstruct.Builder(disableBuilder = true))
public abstract class NotificacioTableMapper {

//    private static final String ICONA_ENVIANT = "<span class=\"fa fa-clock-o\"></span>";
//    private static final String ICONA_PENDENT = "<span class=\"fa fa-clock-o\"></span>";
//    private static final String ICONA_ENVIADA = "<span class=\"fa fa-send-o\"></span>";
//    private static final String ICONA_FINALITZADA = "<span class=\"fa fa-check\"></span>";
//    private static final String ICONA_REGISTRADA = "<span class=\"fa fa-file-o\"></span>";
//    private static final String ICONA_PROCESSADA = "<span class=\"fa fa-check-circle\"></span>";
//    private static final String ICONA_ANULADA = "<span class=\"fa fa-ban\"></span>";

    private static final String ICONA_ENVIANT = "fa fa-clock-o";
    private static final String ICONA_PENDENT = "fa fa-clock-o";
    private static final String ICONA_ENVIADA = "fa fa-send-o";
    private static final String ICONA_FINALITZADA = "fa fa-check";
    private static final String ICONA_REGISTRADA = "fa fa-file-o";
    private static final String ICONA_PROCESSADA = "fa fa-check-circle";
    private static final String ICONA_ANULADA = "fa fa-ban";

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
    @Autowired
    private NotificacioTableHelper notificacioTableHelper;

    ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(target = "registreEnviamentIntent", source = "not.registreEnviamentIntent", defaultValue = "0")
    @Mapping(target = "createdDate", source = "not.createdDate", qualifiedByName = "optionalDate")
    @Mapping(target = "createdByNom", source = "not.createdBy", qualifiedByName = "optionalUserName")
    @Mapping(target = "createdByCodi", source = "not.createdBy", qualifiedByName = "optionalUserCode")
    @Mapping(target = "permisProcessar", source = "params.permisProcessar")
    @Mapping(target = "organEstat", source = "params.organEstat")
    public abstract NotificacioTableItemDto toNotificacioTableItemDto(NotificacioTableEntity not, NotificacioTableItemConversioParams params);

	@Transactional
	public List<NotificacioTableItemDto> toNotificacionsTableItemDto(List<NotificacioTableEntity> nots, @Context List<String> codis, @Context Map<String, OrganismeDto> organs) {

//    public NotificacioTableItemDto mapNotificacioTableItemDtoContext(NotificacioTableEntity not, @Context List<String> codis, @Context Map<String, OrganismeDto> organs) {
		List<NotificacioTableItemDto> notificacions = new ArrayList<>();
		for (var not : nots)
		{
			if (not == null) {
				return null;
			}

			var paramBuilder = NotificacioTableItemConversioParams.builder();

			if (not.getProcedimentCodi() != null && NotificacioEstatEnumDto.FINALITZADA.equals(not.getEstat())) {
				paramBuilder.permisProcessar(codis.contains(not.getProcedimentCodi()) || codis.contains(not.getId() + ""));
			}
			if (not.getOrganCodi() != null) {
				var organ = organs.get(not.getOrganCodi());
				paramBuilder.organEstat(organ != null ? organ.getEstat() : null);
			}
			notificacions.add(toNotificacioTableItemDto(not, paramBuilder.build()));

		}
		return notificacions;
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
	@Autowired EntityManager em;
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
        dto.setEstatString(getColumnaEstatJson(dto, enviaments));
//        dto.setEstatJson(getColumnaEstatJson(dto, enviaments));
        dto.setDeleted(not.isDeleted());
        // TODO: Fer-ho amb un servei apart amb transaccionalitat independent
        // Actualitzam l'entitat
        try {
            not.setDocumentId(dto.getDocumentId());
            not.setEnvCerData(dto.getEnvCerData());
            not.setEstatString(dto.getEstatString());
//            not.setEstatJson(dto.getEstatJson());
            var rNums = !registreNums.toString().isEmpty() ? registreNums.substring(0, registreNums.length()-2) : "";
            if (rNums.length() > 2000) {
                rNums = rNums.substring(0, 2000) + "...";
            }
            not.setRegistreNums(rNums);
            not.setPerActualitzar(false);
            not.setAnulable(notificacioTableHelper.isAnulable(not.getNotificacio()));
            var inici = System.currentTimeMillis();
            notificacioTableViewRepository.saveAndFlush(not);
            var fi = System.currentTimeMillis();
            NotibLogger.getInstance().info("Guardar a not_table -> " + (fi - inici), log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
            NotibLogger.getInstance().info("Actualitzar notificacio -> " + (fi - iniciActualitzar), log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        } catch (Exception ex) {
			log.error("[NotificacioTableMapper] Error actualitzant la notificacio " + not.getId(), ex);
            // TODO: Si no es pot actualitzar, no es fa res. Es calcularà en cada consulta com fins ara!
        }
    }

    private String getColumnaEstatJson(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        NotibLogger.getInstance().info("Actualitzant la columna estat de la remesa " + dto.getId(), log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        var columanEstatInici = System.currentTimeMillis();
//        objectMapper = new ObjectMapper();
        var root = objectMapper.createObjectNode();
        //Entrega postal
        root.set("entregaPostal", getEntregaPostal(enviaments));
        // Estat
        root.setAll(getIconaEstat(dto));
        root.setAll(getNomEstat(dto));
        // Errors
        var inici = System.currentTimeMillis();
//        String eventError = getEventError(dto, enviaments);
        root.set("eventError", getEventError(dto, enviaments));
        var fi = System.currentTimeMillis();
        var  duracio = fi - inici;
        NotibLogger.getInstance().info("getEventError -> " + duracio, log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        inici = System.currentTimeMillis();
        root.setAll(getCallbackError(dto));
        fi = System.currentTimeMillis();
        duracio = fi - inici;
        NotibLogger.getInstance().info("getCallbackError -> " + duracio,  log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        inici = System.currentTimeMillis();
        root.set("notificacioMovilError", getNotificacioMovilError(dto, enviaments));
        fi = System.currentTimeMillis();
        duracio = fi - inici;
        NotibLogger.getInstance().info("getNotificaMovilError -> " + duracio,  log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        // Data
        root.setAll(getDataEstat(dto));
        // Estats enviaments
        root.set("registreEstat", getRegistreEstat(dto, enviaments));
        root.set("notificaEstats", dto.isComunicacioSir() ? getSirEstats(dto, enviaments) : getNotificaEstats(dto, enviaments));
        root.setAll(getAnulat(enviaments));

        duracio = fi - columanEstatInici;
        NotibLogger.getInstance().info("getColumnaEstat -> " + duracio, log, LoggingTipus.EFICIENCIA_TAULA_REMESES);
        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception ex) {
            log.error("Errro convertint l'estatJson a String. Per la remesa " + dto.getId());
            return "";
        }
    }

    private ObjectNode getAnulat(Set<NotificacioEnviamentEntity> enviaments) {

        var anulat = 0;
        var motiu = "";
        for (var enviament : enviaments) {
            if (enviament.isAnulat()) {
                anulat++;
                motiu = enviament.getMotiuAnulacio();
            }
        }
        var title = anulat > 1 ? messageHelper.getMessage("notificacio.enviament.anulat") : anulat == 1 ? motiu : "";
//        return anulat > 0  ? " <span class=\"fa fa-ban\" title=\"" + title + "\"></span>" : "";
        return objectMapper.createObjectNode().put("anulat", title);
    }

    private ObjectNode getEntregaPostal(Set<NotificacioEnviamentEntity> enviaments) {

        try {
            var entregaPostal = false;
            var errorEntregaPostal = false;
            for (var enviament : enviaments) {

                if (enviament.getEntregaPostal() != null) {
                    entregaPostal = true;
                    errorEntregaPostal = errorEntregaPostal || enviament.getEntregaPostal().errorEntregaPostal();
                }

            }
            if (!entregaPostal) {
                return null;
            }

            var title = messageHelper.getMessage(errorEntregaPostal ? "entrega.postal.erronia.icona.tooltip" : "entrega.postal.icona.tooltip");
//        return "<span class=\"label " + (errorEntregaPostal ? "label-danger"  : "label-success") + "\" title=\"" + title +"\" style=\"float: right; position: relative; top: 0px;\"><span class=\"fa fa-envelope\"></span></span>";

            var entrega = objectMapper.createObjectNode();
            entrega.put("label", errorEntregaPostal ? "label-danger"  : "label-success");
            entrega.put("title", title);
            entrega.put("icon", "");
            return entrega;
        } catch(Exception ex) {
            log.error("Error generant el json de la columan estat per l'entrega postal", ex);
            return null;
        }
    }


    private ObjectNode getIconaEstat(NotificacioTableItemDto dto) {

//        if (dto.isEnviant()) return ICONA_ENVIANT;
//        if (NotificacioEstatEnumDto.PENDENT.equals(dto.getEstat())) return ICONA_PENDENT;
//        if (NotificacioEstatEnumDto.ENVIADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(dto.getEstat())) return ICONA_ENVIADA;
//        if (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())) return ICONA_FINALITZADA;
//        if (NotificacioEstatEnumDto.REGISTRADA.equals(dto.getEstat())) return ICONA_REGISTRADA;
//        if (NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat())) return ICONA_PROCESSADA;
//        if (NotificacioEstatEnumDto.ANULADA.equals(dto.getEstat())) return ICONA_ANULADA;
//        return SENSE_ICONA;
        var iconaEstat = dto.isEnviant() ? ICONA_ENVIANT
                : NotificacioEstatEnumDto.PENDENT.equals(dto.getEstat()) ? ICONA_PENDENT
                : NotificacioEstatEnumDto.ENVIADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(dto.getEstat()) ? ICONA_ENVIADA
                : NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat()) ? ICONA_FINALITZADA
                : NotificacioEstatEnumDto.REGISTRADA.equals(dto.getEstat()) ? ICONA_REGISTRADA
                : NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) ? ICONA_PROCESSADA
                : NotificacioEstatEnumDto.ANULADA.equals(dto.getEstat()) ? ICONA_ANULADA
                : SENSE_ICONA;
        return objectMapper.createObjectNode().put("iconaEstat", iconaEstat);
    }

    private ObjectNode getNomEstat(NotificacioTableItemDto dto) {

        var nomEstat = messageHelper.getMessage("es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto." + (dto.isEnviant() ? NotificacioEstatEnumDto.ENVIANT.name() : dto.getEstat().name()));
        return objectMapper.createObjectNode().put("nomEstat", nomEstat);
    }

    private ObjectNode getEventError(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        var error = objectMapper.createArrayNode();
//        boolean isFinal = NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat());
//        var eventError = !isFinal ? eventRepository.findLastErrorEventByNotificacioId(dto.getId()) : null;
//        var eventError = eventRepository.findLastErrorEventByNotificacioId(dto.getId());
        NotificacioEventEntity event = null;
        String msg = messageHelper.getMessage("notificacio.event.fi.reintents");
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
            if (error.size() == 0) {
//                error = " <span class=\"fa fa-warning text-danger\" title=\"" + (enviaments.size() == 1 ? htmlEscape(desc) : getMessage + "error.notificacio.enviaments" + fiGetMessage) + " \"></span>";
                var err = objectMapper.createObjectNode();
                err.put("title", (enviaments.size() == 1 ? htmlEscape(desc) : messageHelper.getMessage("error.notificacio.enviaments")));
                error.add(err);
            }
            if (Boolean.TRUE.equals(event.getFiReintents())) {
                var eventTipus = NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(event.getTipus()) && event.getEnviament().isSirFiPooling() ? NotificacioEventTipusEnumDto.SIR_FI_POOLING : event.getTipus();
                tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + eventTipus);
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
        ObjectNode errorCallback = null;
        if (TipusUsuariEnumDto.APLICACIO.equals(dto.getTipusUsuari()) && dto.isErrorLastCallback()) {
             errorCallback = objectMapper.createObjectNode();
//            error += " <span class=\"fa fa-exclamation-circle text-primary\" title=\"" +  getMessage + "notificacio.list.client.error" + fiGetMessage + "\"></span>";
            errorCallback.put("errorCallback",  messageHelper.getMessage("notificacio.list.client.error"));
        }
        var eventError = objectMapper.createObjectNode();
        eventError.set("error", error);
        eventError.put("errorCallback", errorCallback);
        eventError.put("errorFiReintents", fiReintentsError.toString());
//        error += fiReintentsError.length() > 0 ? " <span class=\"fa fa-warning text-warning\" title=\"" + fiReintentsError + "\"></span>" : "";
        return eventError;
    }

    private ObjectNode getCallbackError(NotificacioTableItemDto dto) {

        int callbackFiReintents = eventRepository.countEventCallbackAmbFiReintentsByNotificacioId(dto.getId());
//        return callbackFiReintents > 0 ? " <span class=\"fa fa-warning text-info\" title=\"" + getMessage + "callback.fi.reintents" + fiGetMessage + "\"></span>" : "";
        return objectMapper.createObjectNode().put("callbackFiReintents", callbackFiReintents > 0 ? messageHelper.getMessage("callback.fi.reintents"): "");
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

    private ArrayNode getNotificacioMovilError(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        StringBuilder notificacioMovilMsg = new StringBuilder();
        int multipleApiCarpetaError = 0;
        var arrayNode = objectMapper.createArrayNode();
        for (NotificacioEnviamentEntity env : enviaments) {
            if (env.isPerEmail() || env.getNotificaEstat() == null) {
                continue;
            }

            var eventCarpeta = eventRepository.findLastApiCarpetaByEnviamentId(env.getId());
            if (eventCarpeta != null && !eventCarpeta.isEmpty() && eventCarpeta.get(0).isError()) {
                multipleApiCarpetaError++;
                notificacioMovilMsg.append(" <span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"").append(eventCarpeta.get(0).getErrorDescripcio()).append("\"></span>\n");
                arrayNode.add(objectMapper.createObjectNode().put("eventCarpeta", eventCarpeta.get(0).getErrorDescripcio()));
            }
        }
        if (multipleApiCarpetaError > 1) {
            notificacioMovilMsg = new StringBuilder("<span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"" + getMessage + "api.carpeta.send.notificacio.movil.error" + fiGetMessage + "\"></span>\n");
            arrayNode.removeAll().add(objectMapper.createObjectNode().put("eventCarpeta", messageHelper.getMessage("api.carpeta.send.notificacio.movil.error")));
        }
//        return notificacioMovilMsg.length() > 0 ? notificacioMovilMsg.toString() : "";
        return arrayNode;
    }

    private ObjectNode getDataEstat(NotificacioTableItemDto dto) {

        var df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        var dataEstat = "\n";
//        if ((NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())) && dto.getEstatDate() != null) {
//            dataEstat += "<span class=\"horaProcessat\">" + df.format(dto.getEstatDate()) + "</span>\n";
//        } else if (NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) && dto.getEstatProcessatDate() != null) {
//            dataEstat += "<span class=\"horaProcessat\">" + df.format(dto.getEstatProcessatDate()) + "</span>\n";
//        }

        var dataEstat = (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())) && dto.getEstatDate() != null ?
                            dto.getEstatDate()
                        : NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) && dto.getEstatProcessatDate() != null ? dto.getEstatProcessatDate() : null;
        return objectMapper.createObjectNode().put("dataEstat", dataEstat != null ? df.format(dataEstat) : null);
    }

    private ArrayNode getRegistreEstat(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

//        StringBuilder registreEstat = new StringBuilder();
        if (!dto.isComunicacioSir()) {
            return null;
        }
        try {

            var registreEstats = objectMapper.createArrayNode();
            ObjectNode registreEstat;
            for (NotificacioEnviamentEntity env : enviaments) {
                var regEstat = env.getRegistreEstat();
                if (regEstat == null) {
                    continue;
//                    registreEstat.append("<span style=\"margin-right: 3px;\"><span style=\"padding-bottom:1px; background-color: " + regEstat.getColor() + ";\" title=\"" +
//                            getMessage +"es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + regEstat + fiGetMessage
//                            + "\" class=\"label label-primary\">" + regEstat.getBudget() + "</span></span>");
                }
                registreEstat = objectMapper.createObjectNode();
                registreEstat.put("backgroundColor", regEstat.getColor());
                registreEstat.put("title", messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + regEstat));
                registreEstat.put("label", regEstat.getBudget());
                registreEstats.add(registreEstat);
            }
            return registreEstats;
        } catch(Exception ex) {
            log.error("Error generant el json de la columan estat per l'entrega postal", ex);
            return null;
        }
    }

    private ArrayNode getSirEstats(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        StringBuilder notificacioEstat = new StringBuilder();
        for (NotificacioEnviamentEntity env : enviaments) {
            dto.updateEstatSirTipusCount(env.getRegistreEstat());
        }
        var arrayNode = objectMapper.createArrayNode();
        if (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || dto.getContadorEstat().size() > 1) {
            for (var entry : dto.getContadorEstatSir().entrySet()) {
//                notificacioEstat.append("<div style=\"font-size:11px; box-shadow: inset 3px 0px 0px ").append(entry.getKey().getColor()).append("; padding-left: 5px;").append("\">")
//                        .append(entry.getValue()).append(" ").append(getMessage + "es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + entry.getKey() + fiGetMessage)
//                        .append("</div>");
                var estat = objectMapper.createObjectNode();
                estat.put("color", entry.getKey().getColor());
                estat.put("value", entry.getValue());
                estat.put("message", messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + entry.getKey()));
                arrayNode.add(estat);
            }
        }
        return arrayNode;
    }

    private ArrayNode getNotificaEstats(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        StringBuilder notificacioEstat = new StringBuilder();
        for (NotificacioEnviamentEntity env : enviaments) {
            var estat = env.getEntregaPostal() != null && CieEstat.NOTIFICADA.equals(env.getEntregaPostal().getCieEstat()) ? EnviamentEstat.NOTIFICADA : env.getNotificaEstat();
//            dto.updateEstatTipusCount(env.getNotificaEstat());
            dto.updateEstatTipusCount(estat);
        }
        var arrayNode = objectMapper.createArrayNode();
        if (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || dto.getContadorEstat().size() > 1) {

            for (Map.Entry<EnviamentEstat, Integer> entry : dto.getContadorEstat().entrySet()) {
                notificacioEstat.append("<div style=\"font-size:11px; box-shadow: inset 3px 0px 0px ").append(entry.getKey().getColor()).append("; padding-left: 5px;").append("\">")
                        .append(entry.getValue()).append(" ").append(getMessage + "es.caib.notib.client.domini.EnviamentEstat." + entry.getKey() + fiGetMessage)
                        .append("</div>");
                var estat = objectMapper.createObjectNode();
                estat.put("color", entry.getKey().getColor());
                estat.put("value", entry.getValue());
                estat.put("message", messageHelper.getMessage("es.caib.notib.client.domini.EnviamentEstat." + entry.getKey()));
                arrayNode.add(estat);
            }
        }
        return arrayNode;
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
