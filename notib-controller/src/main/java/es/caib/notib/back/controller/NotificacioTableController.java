package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.MarcarProcessatCommand;
import es.caib.notib.back.command.NotificacioFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.NotificacioBackHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.missatges.Missatge;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.CallbackService;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    private NotificacioBackHelper notificacioListHelper;
    @Autowired
    private PermisosService permisosService;
    @Autowired
    private CallbackService callbackService;

    public NotificacioTableController() {
        super.sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO;
    }

    protected List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        String organGestorCodi = null;
        if (RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual()) && entitatActual != null) {
            var organGestorActual = getOrganGestorActual(request);
            organGestorCodi = organGestorActual.getCodi();
        }
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE).asDto();
        assert entitatActual != null;
        return notificacioService.findIdsAmbFiltre(entitatActual.getId(), RolEnumDto.valueOf(sessionScopedContext.getRolActual()),
                                                    organGestorCodi, getCodiUsuariActual(), filtre);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var organGestorActual = getOrganGestorActual(request);
        var notificacioFiltreCommand = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        model.addAttribute(notificacioFiltreCommand);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "notificacioList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/filtrades/{referencia}")
    public String getFiltrades(HttpServletRequest request, @PathVariable String referencia, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var organGestorActual = getOrganGestorActual(request);
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        filtre.setReferencia(referencia);
        model.addAttribute(filtre);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "redirect:/notificacio";
    }

    @RequestMapping(method = RequestMethod.POST, params = "netejar")
    public String postNeteja(HttpServletRequest request, Model model) {
        return post(request, new NotificacioFiltreCommand(), model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(HttpServletRequest request, NotificacioFiltreCommand command, Model model) {

        RequestSessionHelper.actualitzarObjecteSessio(request, NOTIFICACIONS_FILTRE, command);
//        notificacioListHelper.ompleProcediments(request, model);
        model.addAttribute("notificacioFiltreCommand", command);
        model.addAttribute("nomesAmbErrors", command.isNomesAmbErrors());
//        deselect(request, null);
        return "notificacioList";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE).asDto();
        var notificacions = new PaginaDto<NotificacioTableItemDto>();
        var isUsuari = RolHelper.isUsuariActualUsuari(sessionScopedContext.getRolActual());
        var isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var isAdministrador = RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
        var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
        String organGestorCodi = null;
        try {
            if (isUsuariEntitat && filtre != null) {
                filtre.setEntitatId(entitatActual.getId());
            }
            if (isAdminOrgan && entitatActual != null) {
                OrganGestorDto organGestorActual = getOrganGestorActual(request);
                organGestorCodi = organGestorActual.getCodi();
            }
            notificacions = notificacioService.findAmbFiltrePaginat(entitatActual != null ? entitatActual.getId() : null,
                                                RolEnumDto.valueOf(sessionScopedContext.getRolActual()), organGestorCodi, getCodiUsuariActual(), filtre,
                                                DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
        }
        return DatatablesHelper.getDatatableResponse(request, notificacions, "id", SESSION_ATTRIBUTE_SELECCIO);
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

        var entitatId = sessionScopedContext.getEntitatActualId();
        String organCodi = null;
        var permis = PermisEnum.CONSULTA;
        var organGestor = getOrganGestorActual(request);
        if (organGestor != null) {
            organCodi = organGestor.getCodi();
        }
        var rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
        return procedimentService.getProcedimentsOrgan(entitatId, organCodi, null, rol, permis);
    }

    @RequestMapping(value = "/serveisOrgan", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getServeis(HttpServletRequest request, Model model) {

        var entitatId = sessionScopedContext.getEntitatActualId();
        String organCodi = null;
        var permis = PermisEnum.CONSULTA;
        var organGestor = getOrganGestorActual(request);
        if (organGestor != null) {
            organCodi = organGestor.getCodi();
        }
        var rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
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

        var entitatId = sessionScopedContext.getEntitatActualId();
        String organCodi = null;
        var permis = PermisEnum.CONSULTA;
        var organActual = getOrganGestorActual(request);
        if (organActual != null) {
            organCodi = organActual.getCodi();
        }
        var rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
        return procedimentService.getProcedimentsOrgan(entitatId, organCodi, organGestor, rol, permis);
    }

    @RequestMapping(value = "/serveisOrgan/{organGestor}", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getServeiByOrganGestor(HttpServletRequest request, @PathVariable Long organGestor, Model model) {

        Long entitatId = sessionScopedContext.getEntitatActualId();
        String organCodi = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organActual = getOrganGestorActual(request);
        if (organActual != null) {
            organCodi = organActual.getCodi();
        }
        RolEnumDto rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
        return serveiService.getServeisOrgan(entitatId, organCodi, organGestor, rol, permis);
    }

    @RequestMapping(value = "/{notificacioId}/info", method = RequestMethod.GET)
    public String info(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"dades", model);
        return "notificacioInfo";
    }

    @RequestMapping(value = "/{notificacioId}/delete", method = RequestMethod.GET)
    public String eliminar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        try {
            notificacioService.delete(entitatActual.getId(), notificacioId);
            return getModalControllerReturnValueSuccess(request,"redirect:" + referer,"notificacio.controller.esborrar.ok");
        } catch (Exception ex) {
            log.error("Hi ha hagut un error esborrant la notificació", ex);
            return getModalControllerReturnValueError(request, "redirect:" + referer, "notificacio.controller.esborrar.ko", new Object[]{ex.getMessage()});
        }
    }

    @RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.GET)
    public String processarGet(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var command = new MarcarProcessatCommand();
        model.addAttribute(command);
        model.addAttribute("isMassiu", false);
        return "notificacioMarcarProcessat";
    }

    @RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.POST)
    public String processarPost(HttpServletRequest request, @PathVariable Long notificacioId, @Valid MarcarProcessatCommand command,
                                BindingResult bindingResult,Model model) throws MessagingException {

        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("isMassiu", false);
                return "notificacioMarcarProcessat";
            }
            var resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdministrador(request));
            if (resposta != null) {
                MissatgesHelper.warning(request, resposta);
            }
            return getModalControllerReturnValueSuccess(request,"redirect:../../notificacio","notificacio.controller.refrescar.estat.ok");
        } catch (Exception ex) {
            log.error("Hi ha hagut un error processant la notificació", ex);
            return getModalControllerReturnValueError(request, "redirect:../../notificacio","notificacio.controller.processar.ko", new Object[]{ex.toString()}); //ex.getMessage()});
        }

    }

    @RequestMapping(value = "/{notificacioId}/event", method = RequestMethod.GET)
    public String eventList(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        model.addAttribute("notificacioId", notificacioId);
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,"es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto."));
        return "notificacioEvents";
    }

    @RequestMapping(value = "/{notificacioId}/event/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse eventDatatable(HttpServletRequest request, @PathVariable Long notificacioId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.eventFindAmbNotificacio(entitatActual.getId(), notificacioId));
    }

    @RequestMapping(value = "/{notificacioId}/historic/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse historicDatatable(HttpServletRequest request, @PathVariable Long notificacioId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var historic = notificacioService.historicFindAmbNotificacio(entitatActual.getId(), notificacioId);
        return DatatablesHelper.getDatatableResponse(request, historic);
    }

    @RequestMapping(value = "/{notificacioId}/enviar", method = RequestMethod.GET)
    public String enviar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var enviada = notificacioService.enviar(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        if (enviada) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo", "notificacio.controller.enviament.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo", "notificacio.controller.enviament.error");
    }

    @RequestMapping(value = "/{notificacioId}/registrar", method = RequestMethod.GET)
    public String registrar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) throws RegistreNotificaException {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var registresIdDto = notificacioService.registrarNotificar(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        if (registresIdDto == null || registresIdDto.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.registrar.error"));
            return "notificacioInfo";
        }
        for (var registreIdDto : registresIdDto) {
            if (registreIdDto.getNumero() != null || registreIdDto.getNumeroRegistreFormat() != null) {
                MissatgesHelper.success(request, "(" + registreIdDto.getNumeroRegistreFormat() + ")" + getMessage(request,"notificacio.controller.registrar.ok"));
                continue;
            }
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.registrar.error"));
        }
        model.addAttribute("pestanyaActiva", "accions");
        return "notificacioInfo";
    }

    @RequestMapping(value = "/{notificacioId}/reactivarconsulta", method = RequestMethod.GET)
    public String reactivarconsulta(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var reactivat = notificacioService.reactivarConsulta(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo","notificacio.controller.reactivar.consulta.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo","notificacio.controller.reactivar.consulta.error");
    }

    @RequestMapping(value = "/{notificacioId}/reactivarsir", method = RequestMethod.GET)
    public String reactivarsir(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var reactivat = notificacioService.reactivarSir(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo","notificacio.controller.reactivar.sir.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo","notificacio.controller.reactivar.sir.error");
    }

    @RequestMapping(value = "/{notificacioId}/enviament", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificacioEnviamentDatatableDto> enviamentList(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {
        return enviamentService.enviamentFindAmbNotificacio(notificacioId);
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}", method = RequestMethod.GET)
    public String enviamentInfo(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, @RequestParam(required = false) String pipellaActiva, Model model) {
        emplenarModelEnviamentInfo(notificacioId, enviamentId, pipellaActiva != null ? pipellaActiva : "dades", model, request);
        return "enviamentInfo";
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/event/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse enviamentEventsDatatable(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.eventFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/historic/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse enviamentHistoricDatatable(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.historicFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
    }

    @ResponseBody
    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatNotifica", method = RequestMethod.GET)
    public Missatge refrescarEstatNotifica(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var enviamentEstat = notificacioService.enviamentRefrescarEstat(entitatActual.getId(), enviamentId);
        var totbe = !enviamentEstat.isNotificaError();
        String msg = null;
        if (totbe) {
            msg = getMessage(request, "notificacio.controller.refrescar.estat.ok");
            MissatgesHelper.success(request, msg);
        } else {
            msg = getMessage(request, "notificacio.controller.refrescar.estat.error");
            MissatgesHelper.error(request, msg);
        }
        return Missatge.builder().ok(totbe).msg(msg).build();
    }

    @ResponseBody
    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatSir", method = RequestMethod.GET)
    public Missatge refrescarEstatSir(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var totbe = notificacioService.enviamentRefrescarEstatSir(enviamentId);
        String msg = null;
        if (totbe) {
            msg = getMessage(request, "notificacio.controller.refrescar.estat.ok");
            MissatgesHelper.success(request, msg);
        } else {
            msg = getMessage(request, "notificacio.controller.refrescar.estat.error");
            MissatgesHelper.error(request, msg);
        }

        return Missatge.builder().ok(totbe).msg(msg).build();
    }

    @RequestMapping(value = "/{notificacioId}/documentDescarregar/{documentId}", method = RequestMethod.GET)
    @ResponseBody
    public void documentDescarregar(HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long documentId) throws IOException {

        var arxiu = notificacioService.getDocumentArxiu(notificacioId, documentId);
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
            var locale = new Locale(sessionScopedContext.getIdiomaUsuari());
            boolean contingut = false;
            var baos = new ByteArrayOutputStream();
            var zos = new ZipOutputStream(baos);
            var enviaments = enviamentService.enviamentFindAmbNotificacio(notificacioId);
            Map<String, Integer> interessats = new HashMap<>();
            int numInteressats = 0;
            for (var env : enviaments) {
                if (env.getNotificaCertificacioData() == null) {
                    continue;
                }
                var arxiu = notificacioService.enviamentGetCertificacioArxiu(env.getId());
                arxiu.setNom(env.getTitular().getNif() + "_" + arxiu.getNom());
                if (interessats.get(env.getTitular().getNif()) == null) {
                    numInteressats++;
                    interessats.put(env.getTitular().getNif(), numInteressats);
                    arxiu.setNom(numInteressats + "_" + arxiu.getNom());
                }
                var entry = new ZipEntry(arxiu.getNom());
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
            var nom = MessageHelper.getInstance().getMessage("notificacio.list.enviament.certificacio.zip.nom", null, locale);
            writeFileToResponse(nom + "_" + notificacioId + ".zip", baos.toByteArray(), response);
        } catch (Exception ex) {
            var msg = getMessage(request, "notificacio.list.enviament.descaregar.certificacio.error");
            log.error(msg, ex);
            MissatgesHelper.error(request, msg);
            throw new RuntimeException(msg);
        }
    }


    @RequestMapping(value = "/{notificacioId}/reenviarErrors", method = RequestMethod.GET)
    public String reenviarErrors(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var enviada = notificacioService.reenviarNotificacioAmbErrors(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        if (enviada) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo", "notificacio.controller.reenviar.errors.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo", "notificacio.controller.reenviar.errors.error");
    }

    @RequestMapping(value = "/{notificacioId}/reactivarErrors", method = RequestMethod.GET)
    public String reactivarErrors(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var reactivat = notificacioService.reactivarNotificacioAmbErrors(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo", "notificacio.controller.reactivar.errors.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo", "notificacio.controller.reactivar.errors.error");
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
        var arxiu = new ArxiuDto();
        arxiu.setContingut(enviamentService.getDocumentJustificant(enviamentId));
        arxiu.setNom("justificant");
        var mimeType = ".pdf";
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

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var sequence = request.getParameter("sequence");
        var justificant = justificantService.generarJustificantEnviament(notificacioId, entitatActual.getId(), sequence);
        if (justificant == null) {
            throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/justificant/estat/{sequence}", method = RequestMethod.GET)
    @ResponseBody
    public ProgresDescarregaDto justificantEstat(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable String sequence) throws IOException {
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

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var sequence = request.getParameter("sequence");
        var justificant = justificantService.generarJustificantComunicacioSIR(enviamentId, entitatActual.getId(), sequence);
        if (justificant == null) {
            throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/refrescarEstatClient", method = RequestMethod.GET)
    public String refrescarEstatClient(HttpServletResponse response, HttpServletRequest request, Model model, @PathVariable Long notificacioId) throws IOException {

        var notificat = callbackService.reintentarCallback(notificacioId);
        var msg = notificat ? "notificacio.controller.notificar.client.ok" : "notificacio.controller.notificar.client.error";
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

    // ACCIONS MASSIVES PER NOTIFICACIONS
    ////

    @RequestMapping(value = "/enviar/notificacio/movil", method = RequestMethod.GET)
    public String enviarNotificacioMovil(HttpServletRequest request, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        var seleccio = getIdsSeleccionats(request);
        var redirect = "redirect:../../..";
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,redirect,"accio.massiva.seleccio.buida");
        }
        List<String> notificacionsError = new ArrayList<>();
        for (Long notificacioId : seleccio) {
            try {
                notificacioService.reenviarNotificaionsMovil(notificacioId);
            } catch (Exception e) {
                notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
            }
        }
        if (notificacionsError.isEmpty()) {
            return getModalControllerReturnValueSuccess(request, redirect,"accio.massiva.creat.ok");
        }
        if (notificacionsError.size() == seleccio.size()) {
            return getModalControllerReturnValueError(request, redirect,"accio.massiva.creat.ko");
        }
        var desc = new StringBuilder();
        for (var err: notificacionsError) {
            desc.append(err).append(" \n");
        }
        return getModalControllerReturnValueErrorWithDescription(request,redirect,"accio.massiva.creat.part", desc.toString());
    }

    @RequestMapping(value = "/reactivar/registre", method = RequestMethod.GET)
    public String reactivarReintentar(HttpServletRequest request, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,"redirect:../..","accio.massiva.seleccio.buida");
        }
        List<String> notificacionsError = new ArrayList<>();
        for (var notificacioId : seleccio) {
            try {
                notificacioService.reactivarRegistre(notificacioId);
            } catch (Exception e) {
                notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
            }
        }
        if (notificacionsError.isEmpty()) {
            return getModalControllerReturnValueSuccess(request,"redirect:../..","accio.massiva.creat.ok");
        }
        if (notificacionsError.size() == seleccio.size()) {
            return getModalControllerReturnValueError(request,"redirect:../..","accio.massiva.creat.ko");
        }
        var desc = "";
        for (var err: notificacionsError) {
            desc = desc + err + " \n";
        }
        return getModalControllerReturnValueErrorWithDescription(request,"redirect:../..","accio.massiva.creat.part", desc);
    }

    @RequestMapping(value = {"/processar/massiu", "{notificacioId}/notificacio/"}, method = RequestMethod.GET)
    public String processarMassiuModal(HttpServletRequest request, Model model) {

        var command = new MarcarProcessatCommand();
        model.addAttribute(command);
        model.addAttribute("isMassiu", true);
        return "notificacioMarcarProcessat";
    }

    @RequestMapping(value = {"/processar/massiu", "{notificacioId}/notificacio/processar/massiu"}, method = RequestMethod.POST)
    public String processarMassiuPost(HttpServletRequest request, @Valid MarcarProcessatCommand command, BindingResult bindingResult, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request, "redirect:../..", "accio.massiva.seleccio.buida");
        }
        if (bindingResult.hasErrors()) {
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
            model.addAttribute("isMassiu", true);
            return "notificacioMarcarProcessat";
        }
        boolean allOK = true;
        String resposta;
        for (var notificacioId : seleccio) {
            try {
                resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdministrador(request));
                if (resposta != null) {
                    MissatgesHelper.warning(request, resposta);
                    continue;
                }
                MissatgesHelper.success(request, String.format("La notificació (Id=%d) s'ha marcat com a processada", notificacioId));
            } catch (Exception ex) {
                var error = "Hi ha hagut un error processant la notificació";
                log.error(error, ex);
                allOK = false;
                MissatgesHelper.error(request, String.format(error + " (Id=%d): %s", notificacioId, ex.getMessage()));
            }
        }

        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
        return allOK ? getModalControllerReturnValueSuccess(request, "redirect:../..", "notificacio.controller.processar.massiu.ok")
            : getModalControllerReturnValueError(request, "redirect:../..", "notificacio.controller.processar.massiu.ko");
    }

    @RequestMapping(value = {"/eliminar", "{notificacioId}/notificacio/eliminar/"} , method = RequestMethod.GET)
    public String eliminarMassiu(HttpServletRequest request, Model model) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,"redirect:" + referer,"accio.massiva.seleccio.buida");
        }

        Set<Long> notificacionsNoEsborrades = new HashSet<>();
        for (var notificacioId : seleccio) {
            try {
                notificacioService.delete(entitatActual.getId(), notificacioId);
            } catch (Exception ex) {
                notificacionsNoEsborrades.add(notificacioId);
                log.error("Hi ha hagut un error esborrant la notificació", ex);
                MissatgesHelper.error(request, String.format("Hi ha hagut un error esborrant la notificació (Id: %s): %s", notificacioId, ex.getMessage()));
            }
        }
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, notificacionsNoEsborrades);
        return notificacionsNoEsborrades.isEmpty() ?
                getModalControllerReturnValueSuccess(request,"redirect:" + referer,"notificacio.controller.esborrar.massiu.ok")
                : getModalControllerReturnValueError(request,"redirect:" + referer,"notificacio.controller.esborrar.massiu.ko");
    }

    private void emplenarModelNotificacioInfo(EntitatDto entitatActual, Long notificacioId, HttpServletRequest request, String pipellaActiva, Model model) {

        var notificacio = notificacioService.findNotificacioInfo(notificacioId, isAdministrador(request));
        if (notificacio == null) {
            return;
        }
        if (notificacio != null && notificacio.getGrupCodi() != null) {
            var grup = grupService.findByCodi(notificacio.getGrupCodi(), entitatActual.getId());
            notificacio.setGrup(grup);
        }
        if (!Strings.isNullOrEmpty(notificacio.getNotificaErrorDescripcio())) {
            notificacio.setNotificaErrorDescripcio(notificacio.getNotificaErrorDescripcio());
        }
        model.addAttribute("pipellaActiva", pipellaActiva);
        model.addAttribute("notificacio", notificacio);
        var text = "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto.";
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, text));
        var permisGestio = false;
        if (notificacio != null && notificacio.getProcediment() != null && !notificacio.getProcedimentCodiNotib().isEmpty()) {
            permisGestio = permisosService.hasNotificacioPermis(notificacioId, entitatActual.getId(), getCodiUsuariActual(), PermisEnum.GESTIO);
            permisGestio = permisGestio || procedimentService.hasPermisProcediment(notificacio.getProcediment().getId(), PermisEnum.GESTIO);
        }
        model.addAttribute("permisGestio", permisGestio);
        model.addAttribute("permisAdmin", request.isUserInRole("NOT_ADMIN"));
    }


    private void emplenarModelEnviamentInfo(Long notificacioId, Long enviamentId, String pipellaActiva, Model model, HttpServletRequest request) {

        model.addAttribute("notificacio", notificacioService.findAmbId(notificacioId, isAdministrador(request)));
        model.addAttribute("pipellaActiva", pipellaActiva);
        var enviament = enviamentService.enviamentFindAmbId(enviamentId);
        model.addAttribute("enviament", enviament);
        var text = "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto.";
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, text));
    }

    private boolean isAdministrador(HttpServletRequest request) {
        return RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("SI", "NO", false));
    }

}