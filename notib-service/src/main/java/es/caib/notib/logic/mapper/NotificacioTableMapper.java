package es.caib.notib.logic.mapper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.NotificacioListHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

@Mapper(componentModel = "spring", uses = {CommonConversor.class}, builder = @org.mapstruct.Builder(disableBuilder = true))
public abstract class NotificacioTableMapper {

    private static final String ICONA_ENVIANT = "<span class=\"fa fa-clock-o\"></span>";
    private static final String ICONA_PENDENT = "<span class=\"fa fa-clock-o\"></span>";
    private static final String ICONA_ENVIADA = "<span class=\"fa fa-send-o\"></span>";
    private static final String ICONA_FINALITZADA = "<span class=\"fa fa-check\"></span>";
    private static final String ICONA_REGISTRADA = "<span class=\"fa fa-file-o\"></span>";
    private static final String ICONA_PROCESSADA = "<span class=\"fa fa-check-circle\"></span>";
    private static final String SENSE_ICONA = "";


    @Autowired
    NotificacioListHelper notificacioListHelper;
    @Autowired
    private MessageHelper messageHelper;

    @Autowired
    private NotificacioEventRepository eventRepository;
    @Autowired
    private NotificacioTableHelper notificacioTableHelper;


    @Mapping(target = "registreEnviamentIntent", source = "not.registreEnviamentIntent", defaultValue = "0")
    @Mapping(target = "createdDate", source = "not.createdDate", qualifiedByName = "optionalDate")
    @Mapping(target = "createdByNom", source = "not.createdBy", qualifiedByName = "optionalUserName")
    @Mapping(target = "createdByCodi", source = "not.createdBy", qualifiedByName = "optionalUserCode")
    @Mapping(target = "permisProcessar", source = "params.permisProcessar")
    @Mapping(target = "organEstat", source = "params.organEstat")
    public abstract NotificacioTableItemDto toNotificacioTableItemDto(NotificacioTableEntity not, NotificacioTableItemConversioParams params);

    public abstract List<NotificacioTableItemDto> toNotificacionsTableItemDto(
            List<NotificacioTableEntity> nots,
            @Context List<String> codis,
            @Context Map<String, OrganismeDto> organs);

    public NotificacioTableItemDto mapNotificacioTableItemDtoContext(
            NotificacioTableEntity not,
            @Context List<String> codis,
            @Context Map<String, OrganismeDto> organs) {
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

        // TODO: Modificar perActualitzar quan hi hagi un canvi
        if (not.isPerActualitzar()) {
            var enviaments = not.getEnviaments();

            if (dto.getDocumentId() == null) {
                dto.setDocumentId(not.getNotificacio().getDocument() != null ? not.getNotificacio().getDocument().getId() : null);
            }

            if (enviaments != null && !enviaments.isEmpty()) {
                for (var enviament: enviaments) {
                    if (enviament.getNotificaCertificacioData() != null) {
                        dto.setEnvCerData(enviament.getNotificaCertificacioData());
                        break;
                    }
                }
            }

            dto.setErrorLastCallback(not.getNotificacio().isErrorLastCallback());
            dto.setEstatString(getColumnaEstat(dto, enviaments));

            // TODO: Fer-ho amb un servei apart amb transaccionalitat independent
            // Actualitzam l'entitat
            try {
                not.setDocumentId(dto.getDocumentId());
                not.setEnvCerData(dto.getEnvCerData());
                not.setEstatString(dto.getEstatString());
                //            not.setHasEnviamentsPendentsRegistre(dto.isHasEnviamentsPendentsRegistre());
                not.setPerActualitzar(false);
                notificacioTableHelper.actualitzarCampsLlistat(not);
            } catch (Exception ex) {
                // TODO: Si no es pot actualitzar, no es fa res. Es calcularà en cada consulta com fins ara!
            }

        }
    }

    private String getColumnaEstat(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {

        // Estat
        String iconaEstat = getIconaEstat(dto);
        String nomEstat = getNomEstat(dto);
        // Errors
        String eventError = getEventError(dto, enviaments.size());
        String callbackError = getCallbackError(dto);
        String fiReintentsError = getFiReintentsError(dto);
        String notificaMovilError = getNotificaMovilError(dto, enviaments);
        // Data
        String dataEstat = getDataEstat(dto);
        // Estats enviaments
        String registreEstat = getRegistreEstat(dto, enviaments);
        String notificaEstats = getNotificaEstats(dto, enviaments);

        var columnaEstat = new StringBuilder("<div class=\"flex-column\">")
                .append("<div style=\"display:flex; justify-content:space-between\">")
                .append("<span>")
                .append(iconaEstat)
                .append(nomEstat)
                .append(eventError)
                .append(fiReintentsError)
                .append(callbackError)
                .append(notificaMovilError)
                .append("</span>")
                .append(registreEstat)
                .append("</div>")
                .append("</div>")
                .append(dataEstat)
                .append(notificaEstats);

        return columnaEstat.toString();
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
        return " " + messageHelper.getMessage("es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto." + (dto.isEnviant() ? NotificacioEstatEnumDto.ENVIANT.name() : dto.getEstat().name())) + "";
    }

    private String getEventError(NotificacioTableItemDto dto, int enviamentsSize) {
        var error = "";

        boolean isFinal = NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat());
        var eventError = !isFinal ? eventRepository.findLastErrorEventByNotificacioId(dto.getId()) : null;

        if (eventError != null && !Strings.isNullOrEmpty(eventError.getErrorDescripcio())) {
            error = " <span class=\"fa fa-warning text-danger\" title=\"" + (enviamentsSize == 1 ? htmlEscape(eventError.getErrorDescripcio()) : messageHelper.getMessage("error.notificacio.enviaments")) + " \"></span>";
        }
        if (TipusUsuariEnumDto.APLICACIO.equals(dto.getTipusUsuari()) && dto.isErrorLastCallback()) {
            error += " <span class=\"fa fa-exclamation-circle text-primary\" title=\"" + messageHelper.getMessage("notificacio.list.client.error") + "\"></span>";
        }
        return error;
    }

    private String getCallbackError(NotificacioTableItemDto dto) {
        int callbackFiReintents = eventRepository.countEventCallbackAmbFiReintentsByNotificacioId(dto.getId());
        return callbackFiReintents > 0 ? " <span class=\"fa fa-warning text-info\" title=\"" + messageHelper.getMessage("callback.fi.reintents") + "\"></span>" : "";
    }

    private String getFiReintentsError(NotificacioTableItemDto dto) {
        List<NotificacioEventEntity> lastErrorEvent = eventRepository.findEventsAmbFiReintentsByNotificacioId(dto.getId());
        StringBuilder fiReintentsError = new StringBuilder();
        if (lastErrorEvent != null && !lastErrorEvent.isEmpty()) {
            String msg = messageHelper.getMessage("notificacio.event.fi.reintents");
            String tipus;
            int env = 1;
            for (var event : lastErrorEvent) {
                tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + event.getTipus());
                fiReintentsError.append("Env ").append(env++).append(": ").append(msg).append(" -> ").append(tipus).append("\n");
            }
        }
        return fiReintentsError.length() > 0 ? " <span class=\"fa fa-warning text-warning\" title=\"" + fiReintentsError + "\"></span>" : "";
    }

    private String getNotificaMovilError(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {
        StringBuilder notificacioMovilMsg = new StringBuilder();
        int multipleApiCarpetaError = 0;
        for (NotificacioEnviamentEntity env : enviaments) {
            if (env.isPerEmail() || env.getNotificaEstat() == null) {
                continue;
            }
            var eventCarpeta = eventRepository.findLastApiCarpetaByEnviamentId(env.getId());
            if (eventCarpeta != null && eventCarpeta.isError()) {
                multipleApiCarpetaError++;
                notificacioMovilMsg.append(" <span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"").append(eventCarpeta.getErrorDescripcio()).append("\"></span>\n");
            }
        }
        if (multipleApiCarpetaError > 1) {
            notificacioMovilMsg = new StringBuilder("<span style=\"color:#8a6d3b;\" class=\"fa fa-mobile fa-lg\" title=\"" + messageHelper.getMessage("api.carpeta.send.notificacio.movil.error") + "\"></span>\n");
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
                    registreEstat.append("<div><span style=\"padding-bottom:1px; background-color: " + regEstat.getColor() + ";\" title=\"" +
                            messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + regEstat)
                            + "\" class=\"label label-primary\">" + regEstat.getBudget() + "</span></div>");
                }
            }
        }
        return registreEstat.length() > 0 ? registreEstat.toString() : "";
    }

    private String getNotificaEstats(NotificacioTableItemDto dto, Set<NotificacioEnviamentEntity> enviaments) {
        StringBuilder notificacioEstat = new StringBuilder();
//        var hasEnviamentsPendents = false;
        for (NotificacioEnviamentEntity env : enviaments) {
            dto.updateEstatTipusCount(env.getNotificaEstat());
//            if (!env.isNotificaEstatFinal() && EnviamentEstat.NOTIB_PENDENT.equals(env.getNotificaEstat())) {
//                hasEnviamentsPendents = true;
//            }
        }
//        dto.setHasEnviamentsPendentsRegistre(hasEnviamentsPendents);

        var padding = "; padding-left: 5px;";
        var boxShadow = "box-shadow: inset 3px 0px 0px ";
        if (NotificacioEstatEnumDto.FINALITZADA.equals(dto.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(dto.getEstat())
                || NotificacioEstatEnumDto.PROCESSADA.equals(dto.getEstat()) || dto.getContadorEstat().size() > 1) {

            for (Map.Entry<EnviamentEstat, Integer> entry : dto.getContadorEstat().entrySet()) {
                notificacioEstat.append("<div style=\"font-size:11px; box-shadow: inset 3px 0px 0px ").append(entry.getKey().getColor()).append("; padding-left: 5px;").append("\">")
                        .append(entry.getValue()).append(" ").append(messageHelper.getMessage("es.caib.notib.client.domini.EnviamentEstat." + entry.getKey()))
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
