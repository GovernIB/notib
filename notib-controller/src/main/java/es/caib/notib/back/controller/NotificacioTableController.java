package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.MarcarProcessatCommand;
import es.caib.notib.back.command.NotificacioFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EntitatHelper;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.NotificacioBackHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.back.helper.SessioHelper;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.missatges.Missatge;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

/**
 * Controlador per a la consulta i gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/notificacio")
public class NotificacioTableController extends TableAccionsMassivesController {

    private final static String NOTIFICACIONS_FILTRE = "notificacions_filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "NotificacioController.session.seleccio";

    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private ProcedimentService procedimentService;
    @Autowired
    private ServeiService serveiService;
    @Autowired
    private EnviamentService enviamentService;
    @Autowired
    private GrupService grupService;
    @Autowired
    private JustificantService justificantService;
    @Autowired
    private NotificacioBackHelper notificacioBackHelper;
    @Autowired
    private PermisosService permisService;

    public NotificacioTableController() {
        super.sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO;
    }

    protected List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        String organGestorCodi = null;
        if (RolHelper.isUsuariActualUsuariAdministradorOrgan(request) && entitatActual != null) {
            OrganGestorDto organGestorActual = getOrganGestorActual(request);
            organGestorCodi = organGestorActual.getCodi();

        }
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        NotificacioFiltreDto filtre = notificacioBackHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE).asDto();
        assert entitatActual != null;
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return notificacioService.findIdsAmbFiltre(entitatActual.getId(), rol, organGestorCodi, usuariActual.getCodi(), filtre);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        NotificacioFiltreCommand notificacioFiltreCommand = notificacioBackHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        model.addAttribute(notificacioFiltreCommand);
        notificacioBackHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "notificacioList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/filtrades/{referencia}")
    public String getFiltrades(HttpServletRequest request, @PathVariable String referencia, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        NotificacioFiltreCommand filtre = notificacioBackHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        filtre.setReferencia(referencia);
        model.addAttribute(filtre);
        notificacioBackHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "redirect:/notificacio";
    }

    @RequestMapping(method = RequestMethod.POST, params = "netejar")
    public String postNeteja(HttpServletRequest request, Model model) {
        return post(request, new NotificacioFiltreCommand(), model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(HttpServletRequest request, NotificacioFiltreCommand command, Model model) {

        RequestSessionHelper.actualitzarObjecteSessio(request, NOTIFICACIONS_FILTRE, command);
        model.addAttribute("notificacioFiltreCommand", command);
        model.addAttribute("nomesAmbErrors", command.isNomesAmbErrors());
        return "notificacioList";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        NotificacioFiltreDto filtre = notificacioBackHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE).asDto();
        PaginaDto<NotificacioTableItemDto> notificacions = new PaginaDto<>();
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
        boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
        boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
        boolean isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
        String organGestorCodi = null;

        try {
            if (isUsuariEntitat && filtre != null) {
                filtre.setEntitatId(entitatActual.getId());
            }
            if (isAdminOrgan && entitatActual != null) {
                OrganGestorDto organGestorActual = getOrganGestorActual(request);
                organGestorCodi = organGestorActual.getCodi();
            }
            RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
            Long id = entitatActual != null ? entitatActual.getId() : null;
            PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
            notificacions = notificacioService.findAmbFiltrePaginat(id, rol, organGestorCodi, usuariActual.getCodi(), filtre, params);
            prepararColumnaEstat(request, notificacions.getContingut());
        } catch (SecurityException e) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
        }
        return DatatablesHelper.getDatatableResponse(request, notificacions, "id", SESSION_ATTRIBUTE_SELECCIO);
    }

    private void prepararColumnaEstat(HttpServletRequest request, List<NotificacioTableItemDto> items) {

        for (NotificacioTableItemDto item : items) {
            List<NotificacioEnviamentDatatableDto> enviaments = enviamentService.enviamentFindAmbNotificacio(item.getId());
            String estat = item.isEnviant() ? "<span class=\"fa fa-clock-o\"></span>" :
                    NotificacioEstatEnumDto.PENDENT.equals(item.getEstat()) ? "<span class=\"fa fa-clock-o\"></span>" :
                    NotificacioEstatEnumDto.ENVIADA.equals(item.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-send-o\"></span>" :
                    NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat()) ? "<span class=\"fa fa-check\"></span>" :
                    NotificacioEstatEnumDto.REGISTRADA.equals(item.getEstat()) ? "<span class=\"fa fa-file-o\"></span>" :
                    NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) ? "<span class=\"fa fa-check-circle\"></span>" : "";
            String nomEstat = " " + getMessage(request, "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto." + item.getEstat().name()) + "";
            String error = item.isNotificaError() ? " <span class=\"fa fa-warning text-danger\" title=\"" + htmlEscape(item.getNotificaErrorDescripcio()) + " \"></span>" : "";
            error += TipusUsuariEnumDto.APLICACIO.equals(item.getTipusUsuari()) && item.isErrorLastCallback() ?
                    " <span class=\"fa fa-exclamation-circle text-primary\" title=\"<spring:message code=\"notificacio.list.client.error/>\"></span>" : "";
            estat = "<span>" + estat + nomEstat + error + "</span>";
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
            for (NotificacioEnviamentDatatableDto env : enviaments) {
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
                            getMessage(request, "es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto." + r)
                            + "\" class=\"label label-primary\">" + r.getBudget() + "</span></div>" : "";
                }
            }
            notificaEstat = notificaEstat.length() > 0 ? notificaEstat.substring(0, notificaEstat.length()-2) : "";
            estat = "<div class=\"flex-column\"><div style=\"display:flex; justify-content:space-between\">" + estat + (registreEstat.length() > 0 ? registreEstat : "")
                    + "</div></div>" + data + notificaEstat;
            String padding = "; padding-left: 5px;";
            String boxShadow = "box-shadow: inset 3px 0px 0px ";

            if (NotificacioEstatEnumDto.FINALITZADA.equals(item.getEstat()) || NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(item.getEstat())
                    || NotificacioEstatEnumDto.PROCESSADA.equals(item.getEstat()) || notificaEstat.length() > 0 || item.getContadorEstat().size() > 1) {

                for (Map.Entry<EnviamentEstat, Integer> entry : item.getContadorEstat().entrySet()) {
                    estat += "<div style=\"font-size:11px;" + boxShadow + entry.getKey().getColor() + padding + "\">" +
                            entry.getValue() + " " + getMessage(request, "es.caib.notib.logic.intf.dto.EnviamentEstat." + entry.getKey())
                            + "</div>";
                }
            }

//            if (item.getNTramitacio() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.TRAMITACIO.getColor() + padding + "\">" +
//                        item.getNTramitacio() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.TRAMITACIO)
//                        + "</div>";
//            }
//            if (item.getNCompareixenca() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor() + padding + "\">" +
//                        item.getNCompareixenca() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.PENDENT_COMPAREIXENCA)
//                        + "</div>";
//            }
//            if (item.getNLlegida() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.LLEGIDA.getColor() + padding + "\">" +
//                        item.getNLlegida() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.LLEGIDA)
//                        + "</div>";
//            }
//            if (item.getNRebutjada() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.REBUTJADA.getColor() + padding + "\">" +
//                        item.getNRebutjada() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.REBUTJADA)
//                        + "</div>";;
//            }
//            if (item.getNExpirada() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.EXPIRADA.getColor() + padding + "\">" +
//                        item.getNExpirada() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.EXPIRADA)
//                        + "</div>";
//            }
//            if (item.getNAnulada() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.ANULADA.getColor() + padding + "\">" +
//                        item.getNAnulada() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.ANULADA)
//                        + "</div>";
//            }
//            if (item.getNError() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.ERROR.getColor() + padding + "\">" +
//                        item.getNError() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.ERROR)
//                        + "</div>";
//            }
//            if (item.getNFinalitzada() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.ESTAT_FICTICI.getColor() + padding + "\">" +
//                        item.getNFinalitzada() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.FINALITZADA)
//                        + "</div>";
//            }
//            if (item.getNProcessada() > 0 ){
//                estat += "<div style=\"" + boxShadow + EnviamentEstatGrup.ESTAT_FICTICI.getColor() + padding + "\">" +
//                        item.getNProcessada() + " " + getMessage(request, "enviament.grup." + EnviamentEstatGrup.PROCESSADA)
//                        + "</div>";
//            }

            item.setEstatString(estat);
        }
    }


    /**
     * Obté el llistat de procediments que es pot consultar les seves notificacions.
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/procedimentsOrgan", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getProcediments(HttpServletRequest request, Model model) {

        Long entitatId = EntitatHelper.getEntitatActual(request).getId();
        String organCodi = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organGestor = getOrganGestorActual(request);
        if (organGestor != null) {
            organCodi = organGestor.getCodi();
        }
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return procedimentService.getProcedimentsOrgan(entitatId, organCodi, null, rol, permis);
    }

    @RequestMapping(value = "/serveisOrgan", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getServeis(HttpServletRequest request, Model model) {

        Long entitatId = EntitatHelper.getEntitatActual(request).getId();
        String organCodi = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organGestor = getOrganGestorActual(request);
        if (organGestor != null) {
            organCodi = organGestor.getCodi();
        }
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return serveiService.getServeisOrgan(entitatId, organCodi,null, rol, permis);
    }

    /**
     * Obté el llistat de procediments de l'òrgan gestor indicat que es pot consultar les seves notificacions.
     *
     * @param request
     * @param organGestor
     * @param model
     * @return
     */
    @RequestMapping(value = "/procedimentsOrgan/{organGestor}", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getProcedimentByOrganGestor(HttpServletRequest request, @PathVariable Long organGestor, Model model) {

        Long entitatId = EntitatHelper.getEntitatActual(request).getId();
        String organCodi = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organActual = getOrganGestorActual(request);
        if (organActual != null) {
            organCodi = organActual.getCodi();
        }
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return procedimentService.getProcedimentsOrgan(entitatId, organCodi, organGestor, rol, permis);
    }

    @RequestMapping(value = "/serveisOrgan/{organGestor}", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getServeiByOrganGestor(HttpServletRequest request, @PathVariable Long organGestor, Model model) {

        Long entitatId = EntitatHelper.getEntitatActual(request).getId();
        String organCodi = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organActual = getOrganGestorActual(request);
        if (organActual != null) {
            organCodi = organActual.getCodi();
        }
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return serveiService.getServeisOrgan(entitatId, organCodi, organGestor, rol, permis);
    }

    @RequestMapping(value = "/{notificacioId}/info", method = RequestMethod.GET)
    public String info(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"dades", model);
        return "notificacioInfo";
    }

    @RequestMapping(value = "/{notificacioId}/delete", method = RequestMethod.GET)
    public String eliminar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        String referer = request.getHeader("Referer");
        String url = "redirect:" + referer;
        try {
            notificacioService.delete(entitatActual.getId(), notificacioId);
            return getModalControllerReturnValueSuccess(request, url,"notificacio.controller.esborrar.ok");
        } catch (Exception ex) {
            log.error("Hi ha hagut un error esborrant la notificació", ex);
            return getModalControllerReturnValueError(request, url, "notificacio.controller.esborrar.ko", new Object[]{ex.getMessage()});
        }
    }

    @RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.GET)
    public String processarGet(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        MarcarProcessatCommand command = new MarcarProcessatCommand();
        model.addAttribute(command);
        model.addAttribute("isMassiu", false);
        return "notificacioMarcarProcessat";
    }

    @RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.POST)
    public String processarPost(HttpServletRequest request, @PathVariable Long notificacioId, @Valid MarcarProcessatCommand command,
                                BindingResult bindingResult,Model model) throws MessagingException {

        String url = "redirect:../../notificacio";
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("isMassiu", false);
                return "notificacioMarcarProcessat";
            }
            String resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdministrador(request));
            if (resposta != null) {
                MissatgesHelper.warning(request, resposta);
            }
            return getModalControllerReturnValueSuccess(request, url,"notificacio.controller.refrescar.estat.ok");
        } catch (Exception ex) {
            log.error("Hi ha hagut un error processant la notificació", ex);
            return getModalControllerReturnValueError(request, url,"notificacio.controller.processar.ko", new Object[]{ex.toString()});
        }
    }

    @RequestMapping(value = "/{notificacioId}/event", method = RequestMethod.GET)
    public String eventList(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        model.addAttribute("notificacioId", notificacioId);
        String prefix = "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto.";
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, prefix));
        return "notificacioEvents";
    }

    @RequestMapping(value = "/{notificacioId}/event/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse eventDatatable(HttpServletRequest request, @PathVariable Long notificacioId) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.eventFindAmbNotificacio(entitatActual.getId(), notificacioId));
    }

    @RequestMapping(value = "/{notificacioId}/historic/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse historicDatatable(HttpServletRequest request, @PathVariable Long notificacioId) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        List<NotificacioAuditDto> historic = notificacioService.historicFindAmbNotificacio(entitatActual.getId(), notificacioId);
        return DatatablesHelper.getDatatableResponse(request, historic);
    }

    @RequestMapping(value = "/{notificacioId}/enviar", method = RequestMethod.GET)
    public String enviar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean enviada = notificacioService.enviar(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        String url = "notificacioInfo";
        String msg = enviada ? "notificacio.controller.enviament.ok" : "notificacio.controller.enviament.error";
        return  enviada ? getAjaxControllerReturnValueSuccess(request, url, msg) : getAjaxControllerReturnValueError(request, url, msg);
    }

    @RequestMapping(value = "/{notificacioId}/registrar", method = RequestMethod.GET)
    public String registrar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) throws RegistreNotificaException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        List<RegistreIdDto> registresIdDto = notificacioService.registrarNotificar(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        String msgError = "notificacio.controller.registrar.error";
        if (registresIdDto == null || registresIdDto.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, msgError));
            return "notificacioInfo";
        }
        for (RegistreIdDto registreIdDto : registresIdDto) {
            if (registreIdDto.getNumero() != null) {
                MissatgesHelper.success(request, "(" + registreIdDto.getNumeroRegistreFormat() + ")" + getMessage(request,"notificacio.controller.registrar.ok"));
                continue;
            }
            MissatgesHelper.error(request, getMessage(request, msgError));
        }
        model.addAttribute("pestanyaActiva", "accions");
        return "notificacioInfo";
    }

    @RequestMapping(value = "/{notificacioId}/reactivarconsulta", method = RequestMethod.GET)
    public String reactivarconsulta(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean reactivat = notificacioService.reactivarConsulta(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        String url = "notificacioInfo";
        String msg = reactivat ? "notificacio.controller.reactivar.consulta.ok" : "notificacio.controller.reactivar.consulta.error";
        return reactivat ? getAjaxControllerReturnValueSuccess(request, url,msg ) : getAjaxControllerReturnValueError(request, url, msg);
    }

    @RequestMapping(value = "/{notificacioId}/reactivarsir", method = RequestMethod.GET)
    public String reactivarsir(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean reactivat = notificacioService.reactivarSir(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        String url = "notificacioInfo";
        String msg = reactivat ? "notificacio.controller.reactivar.sir.ok" : "notificacio.controller.reactivar.sir.error";
        return  reactivat ? getAjaxControllerReturnValueSuccess(request, url, msg) : getAjaxControllerReturnValueError(request, url,msg);
    }

    @RequestMapping(value = "/{notificacioId}/enviament", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificacioEnviamentDatatableDto> enviamentList(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {
        return enviamentService.enviamentFindAmbNotificacio(notificacioId);
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}", method = RequestMethod.GET)
    public String enviamentInfo(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        emplenarModelEnviamentInfo(notificacioId, enviamentId,"dades", model, request);
        return "enviamentInfo";
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/event/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse enviamentEventsDatatable(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.eventFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/historic/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse enviamentHistoricDatatable(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.historicFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
    }

    @ResponseBody
    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatNotifica", method = RequestMethod.GET)
    public Missatge refrescarEstatNotifica(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var enviamentEstat = notificacioService.enviamentRefrescarEstat(entitatActual.getId(), enviamentId);
        var totbe = !enviamentEstat.isNotificaError();
        var msg = totbe ? "notificacio.controller.refrescar.estat.ok" : "notificacio.controller.refrescar.estat.error";
        emplenarModelEnviamentInfo(notificacioId, enviamentId, "estatNotifica", model, request);
        return Missatge.builder().ok(totbe).msg(getMessage(request, msg)).build();
    }

    @RequestMapping(value = "/{notificacioId}/documentDescarregar/{documentId}", method = RequestMethod.GET)
    @ResponseBody
    public void documentDescarregar(HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long documentId) throws IOException {

        ArxiuDto arxiu = notificacioService.getDocumentArxiu(notificacioId, documentId);
//        String mimeType = "";
//        if (arxiu.getContentType() == "application_pdf" || arxiu.getContentType() == "application/pdf" || arxiu.getContentType() == "PDF" && !arxiu.getNom().contains(".pdf")) {
//            mimeType = ".pdf";
//        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/certificacioDescarregar", method = RequestMethod.GET)
    @ResponseBody
    public void certificacioDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long enviamentId) throws IOException {

        try {
            var arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
            response.setHeader("Set-cookie", "fileDownload=true; path=/");
            writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
        } catch (Exception ex) {
            log.error("Error descarregant la certificacio", ex);
            var entitatActual = getEntitatActualComprovantPermisos(request);
            var enviamentEstat = notificacioService.enviamentRefrescarEstat(entitatActual.getId(), enviamentId);
            var arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
            response.setHeader("Set-cookie", "fileDownload=true; path=/");
            writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
        }
    }

    @RequestMapping(value = "/{notificacioId}/enviament/certificacionsDescarregar", method = RequestMethod.GET)
    @ResponseBody
    public void certificacionsDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId) throws IOException {

        try {
            Locale locale = new Locale(SessioHelper.getIdioma(aplicacioService));
            boolean contingut = false;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            List<NotificacioEnviamentDatatableDto> enviaments = enviamentService.enviamentFindAmbNotificacio(notificacioId);
            Map<String, Integer> interessats = new HashMap<>();
            int numInteressats = 0;
            for (NotificacioEnviamentDatatableDto env : enviaments) {
                if (env.getNotificaCertificacioData() == null) {
                    continue;
                }
                ArxiuDto arxiu = notificacioService.enviamentGetCertificacioArxiu(env.getId());
                arxiu.setNom(env.getTitular().getNif() + "_" + arxiu.getNom());
                if (interessats.get(env.getTitular().getNif()) == null) {
                    numInteressats++;
                    interessats.put(env.getTitular().getNif(), numInteressats);
                    arxiu.setNom(numInteressats + "_" + arxiu.getNom());
                }
                ZipEntry entry = new ZipEntry(arxiu.getNom());
                entry.setSize(arxiu.getTamany());
                zos.putNextEntry(entry);
                zos.write(arxiu.getContingut());
                contingut = true;
            }

            if (!contingut) {
                MissatgesHelper.error(request, MessageHelper.getInstance().getMessage("notificacio.list.enviament.descarregar.sensecertificacio", null, locale));
                return;
            }
            zos.closeEntry();
            zos.close();
            response.setHeader("Set-cookie", "fileDownload=true; path=/");
            String nom = MessageHelper.getInstance().getMessage("notificacio.list.enviament.certificacio.zip.nom", null, locale);
            writeFileToResponse(nom + "_" + notificacioId + ".zip", baos.toByteArray(), response);
        } catch (Exception ex) {
            String msg = getMessage(request, "notificacio.list.enviament.descaregar.certificacio.error");
            log.error(msg, ex);
            MissatgesHelper.error(request, msg);
            throw new RuntimeException(msg);
        }
    }


    @RequestMapping(value = "/{notificacioId}/reenviarErrors", method = RequestMethod.GET)
    public String reenviarErrors(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean enviada = notificacioService.reenviarNotificacioAmbErrors(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        String url = "notificacioInfo";
        String msg = enviada ? "notificacio.controller.reenviar.errors.ok" : "notificacio.controller.reenviar.errors.error";
        return enviada ? getAjaxControllerReturnValueSuccess(request, url, msg) : getAjaxControllerReturnValueError(request, url, msg);
    }

    @RequestMapping(value = "/{notificacioId}/reactivarErrors", method = RequestMethod.GET)
    public String reactivarErrors(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean reactivat = notificacioService.reactivarNotificacioAmbErrors(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        String url = "notificacioInfo";
        String msg =  reactivat ?  "notificacio.controller.reactivar.errors.ok" : "notificacio.controller.reactivar.errors.error";
        return reactivat ? getAjaxControllerReturnValueSuccess(request, url, msg) : getAjaxControllerReturnValueError(request, url, msg);
    }

	/////
    /// CONTROLADORS DELS JUSTIFICANTS
    /////

    /**
     * Controlador per a descarregar el justificant del registre.
     *
     * @param request
     * @param response
     * @param notificacioId
     * @param enviamentId
     * @throws IOException
     */
    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/justificantDescarregar", method = RequestMethod.GET)
    @ResponseBody
    public void justificantRegistreDescarregar(HttpServletRequest request, HttpServletResponse response,
                                               @PathVariable Long notificacioId, @PathVariable Long enviamentId) throws IOException {
        ArxiuDto arxiu = new ArxiuDto();
        arxiu.setContingut(enviamentService.getDocumentJustificant(enviamentId));
        arxiu.setNom("justificant");
        String mimeType = ".pdf";
        if (arxiu.getContingut() == null) {
            response.setHeader("Set-cookie", "fileDownload=false; path=/");
            throw new RuntimeException("Hi ha hagut un error generant/descarregant el justificant");
        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(arxiu.getNom() + mimeType, arxiu.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/justificant", method = RequestMethod.GET)
    public String justificantDescarregar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) throws IOException {

        model.addAttribute("notificacioId", notificacioId);
        return "justificantDownloadForm";
    }

    @RequestMapping(value = "/{notificacioId}/justificant", method = RequestMethod.POST)
    @ResponseBody
    public void justificantDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId) throws IOException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        String sequence = request.getParameter("sequence");
        FitxerDto justificant = justificantService.generarJustificantEnviament(notificacioId, entitatActual.getId(), sequence);
        if (justificant == null) {
            throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/justificant/estat/{sequence}", method = RequestMethod.GET)
    @ResponseBody
    public ProgresDescarregaDto justificantEstat(HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable Long notificacioId, @PathVariable String sequence) throws IOException {

        return justificantService.consultaProgresGeneracioJustificant(sequence);
    }

    @RequestMapping(value = "/{notificacioId}/justificant/sir", method = RequestMethod.GET)
    public String justificantComunicacioSIRDescarregar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) throws IOException {

        model.addAttribute("notificacioId", notificacioId);
        return "justificantSIRDownloadForm";
    }

    @RequestMapping(value = "/{enviamentId}/justificant/sir", method = RequestMethod.POST)
    @ResponseBody
    public void justificantComunicacioSIRDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long enviamentId) throws IOException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        String sequence = request.getParameter("sequence");
        FitxerDto justificant = justificantService.generarJustificantComunicacioSIR(enviamentId, entitatActual.getId(), sequence);
        if (justificant == null) {
            throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/refrescarEstatClient", method = RequestMethod.GET)
    public String refrescarEstatClient(HttpServletResponse response, HttpServletRequest request, Model model, @PathVariable Long notificacioId) throws IOException {

        List<NotificacioEventDto> events = enviamentService.eventFindAmbNotificacio(notificacioId);
        boolean notificat = false;
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"dades", model);
        if (events != null && events.size() > 0) {
            NotificacioEventDto lastEvent = events.get(events.size() - 1);
            NotificacioEventTipusEnumDto tipus = lastEvent.getTipus();
            if (lastEvent.isError() &&
                    (tipus.equals(NotificacioEventTipusEnumDto.CALLBACK_CLIENT) || tipus.equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT) ||
                    tipus.equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) || tipus.equals(NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT) ||
                    tipus.equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR) || tipus.equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR) ||
                    tipus.equals(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE) || tipus.equals(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT))) {

                log.info("Preparant per notificar canvi del event : " + lastEvent.getId() + " de tipus " + lastEvent.getTipus().name());
                notificat = enviamentService.reintentarCallback(lastEvent.getId());
            }
        }
        String msg = notificat ? "notificacio.controller.notificar.client.ok" : "notificacio.controller.notificar.client.error";
        MissatgesHelper.error(request, getMessage(request,msg));
        return "notificacioInfo";
    }

    ////
    // Actualització enviaments expirats
    ////

    @RequestMapping(value = "/refrescarEstatNotifica", method = RequestMethod.GET)
    public String refrescarEstatNotificaGet(HttpServletRequest request, Model model) {
        return "enviamentsExpiratsActualitzacioForm";
    }

    @RequestMapping(value = "/refrescarEstatNotifica", method = RequestMethod.POST)
    @ResponseBody
    public void refrescarEstatNotifica() {

        try {
            notificacioService.refrescarEnviamentsExpirats();
        } catch (Exception ex) {
            log.error("S'ha produit un error consultant els enviaments", ex);
        }
    }

    @RequestMapping(value = "/refrescarEstatNotifica/estat", method = RequestMethod.GET)
    @ResponseBody
    public ProgresActualitzacioCertificacioDto enviamentsRefrescarEstatProgres() throws IOException {
        return notificacioService.actualitzacioEnviamentsEstat();
    }

    ////
    // ACCIONS MASSIVES PER NOTIFICACIONS
    ////
    @RequestMapping(value = "/reintentar/registre", method = RequestMethod.GET)
    public String registreReintentar(HttpServletRequest request, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        Set<Long> seleccio = getIdsSeleccionats(request);

        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,"redirect:../..","accio.massiva.seleccio.buida");
        }
        List<String> notificacionsError = new ArrayList<String>();
        for (Long notificacioId : seleccio) {
            try {
                notificacioService.reactivarRegistre(notificacioId);
            } catch (Exception e) {
                notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
            }
        }

        String url = "redirect:../..";
        if (!notificacionsError.isEmpty()) {
            if (notificacionsError.size() == seleccio.size()) {
                getModalControllerReturnValueError(request, url,"accio.massiva.creat.ko");
            } else {
                String desc = "";
                for (String err: notificacionsError) {
                    desc = desc + err + " \n";
                }
                return getModalControllerReturnValueErrorWithDescription(request,url,"accio.massiva.creat.part", desc);
            }
        }
        return getModalControllerReturnValueSuccess(request,url,"accio.massiva.creat.ok");
    }

    @RequestMapping(value = {"/processar/massiu", "{notificacioId}/notificacio/"}, method = RequestMethod.GET)
    public String processarMassiuModal(HttpServletRequest request, Model model) {

        MarcarProcessatCommand command = new MarcarProcessatCommand();
        model.addAttribute(command);
        model.addAttribute("isMassiu", true);
        return "notificacioMarcarProcessat";
    }

    @RequestMapping(value = {"/processar/massiu", "{notificacioId}/notificacio/processar/massiu"}, method = RequestMethod.POST)
    public String processarMassiuPost(HttpServletRequest request, @Valid MarcarProcessatCommand command, BindingResult bindingResult, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        Set<Long> seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request, "redirect:../..", "accio.massiva.seleccio.buida");
        }
        if (bindingResult.hasErrors()) {
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
            model.addAttribute("isMassiu", true);
            return "notificacioMarcarProcessat";
        }
        boolean allOK = true;
        for (Long notificacioId : seleccio) {
            try {
                String resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdministrador(request));

                if (resposta != null) {
                    MissatgesHelper.warning(request, resposta);
                    continue;
                }
                MissatgesHelper.success(request, String.format("La notificació (Id=%d) s'ha marcat com a processada", notificacioId));
            } catch (Exception ex) {
                String error = "Hi ha hagut un error processant la notificació";
                log.error(error, ex);
                allOK = false;
                MissatgesHelper.error(request, String.format(error + " (Id=%d): %s", notificacioId, ex.getMessage()));
            }
        }
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
        String url = "redirect:../..";
        String msg = allOK ? "notificacio.controller.processar.massiu.ok" : "notificacio.controller.processar.massiu.ko";
        return allOK ? getModalControllerReturnValueSuccess(request, url, msg) : getModalControllerReturnValueError(request, url, msg);
    }

    @RequestMapping(value = {"/eliminar", "{notificacioId}/notificacio/eliminar/"} , method = RequestMethod.GET)
    public String eliminarMassiu(HttpServletRequest request, Model model) {

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        String referer = request.getHeader("Referer");
        Set<Long> seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,"redirect:" + referer,"accio.massiva.seleccio.buida");
        }
        Set<Long> notificacionsNoEsborrades = new HashSet<>();
        for (Long notificacioId : seleccio) {
            try {
                notificacioService.delete(entitatActual.getId(), notificacioId);
            } catch (Exception ex) {
                notificacionsNoEsborrades.add(notificacioId);
                log.error("Hi ha hagut un error esborrant la notificació", ex);
                MissatgesHelper.error(request, String.format("Hi ha hagut un error esborrant la notificació (Id: %s): %s", notificacioId, ex.getMessage()));
            }
        }
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, notificacionsNoEsborrades);
        String url = "redirect:" + referer;
        String msg = notificacionsNoEsborrades.isEmpty() ? "notificacio.controller.esborrar.massiu.ok" : "notificacio.controller.esborrar.massiu.ko";
        return notificacionsNoEsborrades.isEmpty()  ? getModalControllerReturnValueSuccess(request, url, msg) : getModalControllerReturnValueError(request, url,msg);
    }

    private void emplenarModelNotificacioInfo(EntitatDto entitatActual, Long notificacioId, HttpServletRequest request, String pipellaActiva, Model model) {

        NotificacioInfoDto notificacio = notificacioService.findNotificacioInfo(notificacioId, isAdministrador(request));
        if (notificacio != null && notificacio.getGrupCodi() != null) {
            GrupDto grup = grupService.findByCodi(notificacio.getGrupCodi(), entitatActual.getId());
            notificacio.setGrup(grup);
        }
        if (!Strings.isNullOrEmpty(notificacio.getNotificaErrorDescripcio())) {
            notificacio.setNotificaErrorDescripcio(htmlEscape(notificacio.getNotificaErrorDescripcio()));
        }
        model.addAttribute("pipellaActiva", pipellaActiva);
        model.addAttribute("notificacio", notificacio);
        String text = "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto.";
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, text));
        model.addAttribute("permisGestio", null);
        boolean permisGestio = false;
        if (notificacio != null && notificacio.getProcediment() != null && !notificacio.getProcedimentCodiNotib().isEmpty()) {
            permisGestio = permisService.hasNotificacioPermis(notificacioId, entitatActual.getId(), notificacio.getUsuariCodi(), PermisEnum.GESTIO);
            permisGestio = permisGestio || procedimentService.hasPermisProcediment(notificacio.getProcediment().getId(), PermisEnum.GESTIO);
        }
        model.addAttribute("permisGestio", permisGestio);
        model.addAttribute("permisAdmin", request.isUserInRole("NOT_ADMIN"));
    }


    private void emplenarModelEnviamentInfo(Long notificacioId, Long enviamentId, String pipellaActiva, Model model, HttpServletRequest request) {

        model.addAttribute("notificacio", notificacioService.findAmbId(notificacioId, isAdministrador(request)));
        model.addAttribute("pipellaActiva", pipellaActiva);
        NotificacioEnviamentDto enviament = enviamentService.enviamentFindAmbId(enviamentId);
        model.addAttribute("enviament", enviament);
        String text = "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto.";
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, text));
    }

    private boolean isAdministrador(HttpServletRequest request) {
        return RolHelper.isUsuariActualAdministradorEntitat(request);
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("SI", "NO", false));
    }
}