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
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.missatges.Missatge;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @Autowired
    private EnviamentSmService envSmService;

    private static final  String NOTIFICACIONS_FILTRE = "notificacions_filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "NotificacioController.session.seleccio";
    private static final String NOT_INFO = "notificacioInfo";
    private static final String REDIRECT = "redirect:";
    private static final String REDIRECT_NOTIFICACIO = "redirect:../../notificacio";
    private static final String REDIRECT_2_PARENTS = "redirect:../..";
    private static final String IS_MASSIU = "IS_MASSIU";
    private static final String MARCAR_PROCESSAT = "notificacioMarcarProcessat";
    private static final String EVENT_TIPUS = "eventTipus";
    private static final String VALIDACIO_OK = "notificacio.massiva.ok.validacio";
    private static final String ERROR_MSG = "avis.nivell.enum.ERROR";
    private static final String REFRESCAR_ESTAT_OK = "notificacio.controller.refrescar.estat.ok";
    private static final String PESTANYA_ACTIVA = "pestanyaActiva";
    private static final String ACCIONS = "accions";
    private static final String NOTIFICACIO_ID = "notificacioId";
    private static final String EVENT_TIPUS_ENUM = "es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto.";
    private static final String SET_COOKIE = "Set-cookie";
    private static final String FILE_DOWNLOAD = "fileDownload=true; path=/";
    private static final String SELECCIO_BUIDA = "accio.massiva.seleccio.buida";
    private static final String PERMIS_DENGAT = "Permís denegat";


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

    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var organGestorActual = getOrganGestorActual(request);
        var notificacioFiltreCommand = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        model.addAttribute(notificacioFiltreCommand);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "notificacioList";
    }

    @GetMapping(value = "/filtrades/{referencia}")
    public String getFiltrades(HttpServletRequest request, @PathVariable String referencia, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var organGestorActual = getOrganGestorActual(request);
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        filtre.setReferencia(referencia);
        model.addAttribute(filtre);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "redirect:/notificacio";
    }

    @PostMapping(params = "netejar")
    public String postNeteja(HttpServletRequest request, Model model) {
        return post(request, new NotificacioFiltreCommand(), model);
    }

    @PostMapping
    public String post(HttpServletRequest request, NotificacioFiltreCommand command, Model model) {

        RequestSessionHelper.actualitzarObjecteSessio(request, NOTIFICACIONS_FILTRE, command);
        model.addAttribute("notificacioFiltreCommand", command);
        model.addAttribute("nomesAmbErrors", command.isNomesAmbErrors());
        return "notificacioList";
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE).asDto();
        var notificacions = new PaginaDto<NotificacioTableItemDto>();
        var isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
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
    @GetMapping(value = "/procedimentsOrgan")
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

    @GetMapping(value = "/serveisOrgan")
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
    @GetMapping(value = "/procedimentsOrgan/{organGestor}")
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

    @GetMapping(value = "/serveisOrgan/{organGestor}")
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

    @GetMapping(value = "/{notificacioId}/info")
    public String info(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"dades", model);
        return NOT_INFO;
    }

    @GetMapping(value = "/{notificacioId}/delete")
    public String eliminar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        try {
            notificacioService.delete(entitatActual.getId(), notificacioId);
            return getModalControllerReturnValueSuccess(request,REDIRECT + referer,"notificacio.controller.esborrar.ok");
        } catch (Exception ex) {
            log.error("Hi ha hagut un error esborrant la notificació", ex);
            return getModalControllerReturnValueError(request, REDIRECT + referer, "notificacio.controller.esborrar.ko", new Object[]{ex.getMessage()});
        }
    }

    @GetMapping(value = "/{notificacioId}/processar")
    public String processarGet(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var command = new MarcarProcessatCommand();
        model.addAttribute(command);
        model.addAttribute(IS_MASSIU, false);
        return MARCAR_PROCESSAT;
    }

    @GetMapping(value = "/{notificacioId}/updateEstatList")
    public String updateEstatList(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        notificacioService.updateEstatList(notificacioId);
        return REDIRECT_NOTIFICACIO;
    }

    @PostMapping(value = "/{notificacioId}/processar")
    public String processarPost(HttpServletRequest request, @PathVariable Long notificacioId, @Valid MarcarProcessatCommand command,
                                BindingResult bindingResult,Model model) throws MessagingException {

        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute(IS_MASSIU, false);
                return MARCAR_PROCESSAT;
            }
            var resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdminEntitat());
            if (resposta != null) {
                MissatgesHelper.warning(request, resposta);
            }
            return getModalControllerReturnValueSuccess(request, REDIRECT_NOTIFICACIO, REFRESCAR_ESTAT_OK);
        } catch (Exception ex) {
            log.error("Hi ha hagut un error processant la notificació", ex);
            return getModalControllerReturnValueError(request, REDIRECT_NOTIFICACIO,"notificacio.controller.processar.ko", new Object[]{ex.toString()});
        }

    }

    @GetMapping(value = "/{notificacioId}/event")
    public String eventList(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        model.addAttribute(NOTIFICACIO_ID, notificacioId);
        model.addAttribute(EVENT_TIPUS, EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,EVENT_TIPUS_ENUM));
        return "notificacioEvents";
    }

    @GetMapping(value = "/{notificacioId}/event/datatable")
    @ResponseBody
    public DatatablesResponse eventDatatable(HttpServletRequest request, @PathVariable Long notificacioId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.eventFindAmbNotificacio(entitatActual.getId(), notificacioId));
    }

    @GetMapping(value = "/{notificacioId}/historic/datatable")
    @ResponseBody
    public DatatablesResponse historicDatatable(HttpServletRequest request, @PathVariable Long notificacioId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var historic = notificacioService.historicFindAmbNotificacio(entitatActual.getId(), notificacioId);
        return DatatablesHelper.getDatatableResponse(request, historic);
    }

    @GetMapping(value = "/{notificacioId}/enviar")
    public String enviar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var enviada = notificacioService.enviarNotificacioANotifica(notificacioId, true);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        if (enviada) {
            return getAjaxControllerReturnValueSuccess(request, NOT_INFO, "notificacio.controller.enviament.ok");
        }
        return getAjaxControllerReturnValueError(request, NOT_INFO, "notificacio.controller.enviament.error");
    }

    @GetMapping(value = "/{notificacioId}/registrar")
    public String registrar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) throws RegistreNotificaException {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        RespostaAccio<String> resposta = notificacioService.enviarNotificacioARegistre(notificacioId, true);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        if (resposta.isEmpty() || !resposta.getErrors().isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.registrar.error"));
            return NOT_INFO;
        }
        resposta.getExecutades().forEach(e -> MissatgesHelper.success(request, "(" + e + ")" + getMessage(request,"notificacio.controller.registrar.ok")));
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        return NOT_INFO;
    }

    @GetMapping(value = "/{notificacioId}/reactivarconsulta")
    public String reactivarNotConsulta(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var reactivat = notificacioService.reactivarConsulta(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, NOT_INFO,"notificacio.controller.reactivar.consulta.ok");
        }
        return getAjaxControllerReturnValueError(request, NOT_INFO,"notificacio.controller.reactivar.consulta.error");
    }

    @GetMapping(value = "/{notificacioId}/reactivarsir")
    public String reactivarComSir(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var reactivat = notificacioService.reactivarSir(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, NOT_INFO,"notificacio.controller.reactivar.sir.ok");
        }
        return getAjaxControllerReturnValueError(request, NOT_INFO,"notificacio.controller.reactivar.sir.error");
    }

    @GetMapping(value = "/{notificacioId}/enviament")
    @ResponseBody
    public List<NotificacioEnviamentDatatableDto> enviamentList(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {
        return enviamentService.enviamentFindAmbNotificacio(notificacioId);
    }

    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}")
    public String enviamentInfo(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, @RequestParam(required = false) String pipellaActiva, Model model) {
        emplenarModelEnviamentInfo(notificacioId, enviamentId, pipellaActiva != null ? pipellaActiva : "dades", model);
        return "enviamentInfo";
    }

    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/event/datatable")
    @ResponseBody
    public DatatablesResponse enviamentEventsDatatable(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.eventFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
    }

    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/historic/datatable")
    @ResponseBody
    public DatatablesResponse enviamentHistoricDatatable(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        return DatatablesHelper.getDatatableResponse(request, notificacioService.historicFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
    }

    @GetMapping(value = "/{notificacioId}/state/machine/afegir")
    @ResponseBody
    public Missatge enviamentStateMachineAfegir(HttpServletRequest request, @PathVariable Long notificacioId) {

        if (!RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
            throw new SecurityException(PERMIS_DENGAT);
        }
        var ok = envSmService.afegirNotificacio(notificacioId);
        var msg = getMessage(request, ok ? VALIDACIO_OK : ERROR_MSG);
        return Missatge.builder().ok(ok).msg(msg).build();
    }

    @GetMapping(value = "/enviament/{enviamentId}/state/machine/set/estat/{estat}")
    @ResponseBody
    public Missatge enviamentStateMachineSetEstat(HttpServletRequest request, @PathVariable Long enviamentId, @PathVariable String estat) {

        if (!RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
            throw new SecurityException(PERMIS_DENGAT);
        }
        var ok = envSmService.canviarEstat(enviamentId, estat);
        var msg = getMessage(request, ok ? VALIDACIO_OK : ERROR_MSG);
        return Missatge.builder().ok(ok).msg(msg).build();
    }

    @GetMapping(value = "/enviament/{enviamentId}/state/machine/enviar/event/{event}")
    @ResponseBody
    public Missatge enviamentStateMachineSetEvent(HttpServletRequest request, @PathVariable Long enviamentId, @PathVariable String event) {

        if (!RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
            throw new SecurityException(PERMIS_DENGAT);
        }
        var ok = envSmService.enviarEvent(enviamentId, event);
        var msg = getMessage(request, ok ? VALIDACIO_OK : ERROR_MSG);
        return Missatge.builder().ok(ok).msg(msg).build();
    }

    @ResponseBody
    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatNotifica")
    public Missatge refrescarEstatNotifica(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var enviamentEstat = notificacioService.enviamentRefrescarEstat(entitatActual.getId(), enviamentId);
        var totbe = !enviamentEstat.isNotificaError();
        String msg = null;
        if (totbe) {
            msg = getMessage(request, REFRESCAR_ESTAT_OK);
            MissatgesHelper.success(request, msg);
        } else {
            msg = getMessage(request, "notificacio.controller.refrescar.estat.error");
            MissatgesHelper.error(request, msg);
        }
        return Missatge.builder().ok(totbe).msg(msg).build();
    }

    @ResponseBody
    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatSir")
    public Missatge refrescarEstatSir(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        getEntitatActualComprovantPermisos(request);
        var totbe = notificacioService.enviamentRefrescarEstatSir(enviamentId);
        String msg = null;
        if (totbe) {
            msg = getMessage(request, REFRESCAR_ESTAT_OK);
            MissatgesHelper.success(request, msg);
        } else {
            msg = getMessage(request, "notificacio.controller.refrescar.estat.error");
            MissatgesHelper.error(request, msg);
        }

        return Missatge.builder().ok(totbe).msg(msg).build();
    }

    @GetMapping(value = "/descarregar/diagrama/state/machine")
    @ResponseBody
    public void descarregarDiagramaStateMachine(HttpServletResponse response) throws IOException {

        response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        try {
            writeFileToResponse("diagramaStateMachine.png", notificacioService.getDiagramaMaquinaEstats(), response);
        } catch (Exception ex) {
            log.debug("Error al obtenir la plantilla de el model de dades CSV de càrrega massiva", ex);
        }
    }

    @GetMapping(value = "/{notificacioId}/documentDescarregar/{documentId}")
    @ResponseBody
    public void documentDescarregar(HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long documentId) throws IOException {

        var arxiu = notificacioService.getDocumentArxiu(notificacioId, documentId);
        response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
    }

    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/certificacioDescarregar")
    @ResponseBody
    public void certificacioDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long enviamentId) throws IOException {

        try {
            var arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
            response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
            writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
        } catch (Exception ex) {
            log.error("Error descarregant la certificacio", ex);
            var entitatActual = getEntitatActualComprovantPermisos(request);
            notificacioService.enviamentRefrescarEstat(entitatActual.getId(), enviamentId);
            var arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
            response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
            writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
        }
    }

    @GetMapping(value = "/{notificacioId}/enviament/certificacionsDescarregar")
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
            response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
            var nom = MessageHelper.getInstance().getMessage("notificacio.list.enviament.certificacio.zip.nom", null, locale);
            writeFileToResponse(nom + "_" + notificacioId + ".zip", baos.toByteArray(), response);
        } catch (Exception ex) {
            var msg = getMessage(request, "notificacio.list.enviament.descaregar.certificacio.error");
            log.error(msg, ex);
            MissatgesHelper.error(request, msg);
            throw new RuntimeException(msg);
        }
    }


    @GetMapping(value = "/{notificacioId}/reenviarErrors")
    public String reenviarErrors(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var enviada = notificacioService.reenviarNotificacioAmbErrors(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        if (enviada) {
            return getAjaxControllerReturnValueSuccess(request, NOT_INFO, "notificacio.controller.reenviar.errors.ok");
        }
        return getAjaxControllerReturnValueError(request, NOT_INFO, "notificacio.controller.reenviar.errors.error");
    }

    @GetMapping(value = "/{notificacioId}/reactivarErrors")
    public String reactivarErrors(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var reactivat = notificacioService.reactivarNotificacioAmbErrors(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, NOT_INFO, "notificacio.controller.reactivar.errors.ok");
        }
        return getAjaxControllerReturnValueError(request, NOT_INFO, "notificacio.controller.reactivar.errors.error");
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
    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/justificantDescarregar")
    @ResponseBody
    public void justificantRegistreDescarregar(HttpServletRequest request, HttpServletResponse response,
                                               @PathVariable Long notificacioId, @PathVariable Long enviamentId) throws IOException {
        var arxiu = new ArxiuDto();
        arxiu.setContingut(enviamentService.getDocumentJustificant(enviamentId));
        arxiu.setNom("justificant");
        var mimeType = ".pdf";
        if (arxiu.getContingut() == null) {
            response.setHeader(SET_COOKIE, "fileDownload=false; path=/");
            throw new RuntimeException("Hi ha hagut un error generant/descarregant el justificant");
        }
        response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        writeFileToResponse(arxiu.getNom() + mimeType, arxiu.getContingut(), response);
    }

    @GetMapping(value = "/{notificacioId}/justificant")
    public String justificantDescarregar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        model.addAttribute(NOTIFICACIO_ID, notificacioId);
        return "justificantDownloadForm";
    }

    @PostMapping(value = "/{notificacioId}/justificant")
    @ResponseBody
    public void justificantDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId) throws IOException {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var sequence = request.getParameter("sequence");
        var justificant = justificantService.generarJustificantEnviament(notificacioId, entitatActual.getId(), sequence);
        if (justificant == null) {
            throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
        }
        response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
    }

    @GetMapping(value = "/{notificacioId}/justificant/estat/{sequence}")
    @ResponseBody
    public ProgresDescarregaDto justificantEstat(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable String sequence) {
        return justificantService.consultaProgresGeneracioJustificant(sequence);
    }

    @GetMapping(value = "/{notificacioId}/justificant/sir")
    public String justificantComunicacioSIRDescarregar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        model.addAttribute(NOTIFICACIO_ID, notificacioId);
        return "justificantSIRDownloadForm";
    }

    @PostMapping(value = "/{enviamentId}/justificant/sir")
    @ResponseBody
    public void justificantComunicacioSIRDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long enviamentId) throws IOException {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var sequence = request.getParameter("sequence");
        var justificant = justificantService.generarJustificantComunicacioSIR(enviamentId, entitatActual.getId(), sequence);
        if (justificant == null) {
            throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
        }
        response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
    }

    @GetMapping(value = "/{notificacioId}/refrescarEstatClient")
    public String refrescarEstatClient(HttpServletResponse response, HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var notificat = callbackService.reintentarCallback(notificacioId);
        var msg = notificat ? "notificacio.controller.notificar.client.ok" : "notificacio.controller.notificar.client.error";
        MissatgesHelper.error(request, getMessage(request,msg));
        return NOT_INFO;
    }

    ////
    // Actualització enviaments expirats
    ////

    @GetMapping(value = "/refrescarEstatNotifica")
    public String refrescarEstatNotificaGet(HttpServletRequest request, Model model) {
        return "enviamentsExpiratsActualitzacioForm";
    }

    @PostMapping(value = "/refrescarEstatNotifica")
    @ResponseBody
    public void refrescarEstatNotifica() {

        try {
            notificacioService.refrescarEnviamentsExpirats();
        } catch (Exception ex) {
            log.error("S'ha produit un error consultant els enviaments", ex);
        }
    }

    @GetMapping(value = "/refrescarEstatNotifica/estat")
    @ResponseBody
    public ProgresActualitzacioCertificacioDto enviamentsRefrescarEstatProgres() {
        return notificacioService.actualitzacioEnviamentsEstat();
    }

    // ACCIONS MASSIVES PER NOTIFICACIONS
    ////

    @GetMapping(value = "/enviar/notificacio/movil")
    public String enviarNotificacioMovil(HttpServletRequest request, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        var seleccio = getIdsSeleccionats(request);
        var redirect = "redirect:../../..";
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,redirect,SELECCIO_BUIDA);
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

    @GetMapping(value = "/reactivar/registre")
    public String reactivarReintentar(HttpServletRequest request, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,REDIRECT_2_PARENTS,SELECCIO_BUIDA);
        }
        List<String> notificacionsError = new ArrayList<>();
        for (var notificacioId : seleccio) {
            try {
                notificacioService.reactivarRegistre(notificacioId);
                notificacioService.resetNotificacioARegistre(notificacioId);
            } catch (Exception e) {
                notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
            }
        }
        if (notificacionsError.isEmpty()) {
            return getModalControllerReturnValueSuccess(request,REDIRECT_2_PARENTS,"accio.massiva.creat.ok");
        }
        if (notificacionsError.size() == seleccio.size()) {
            return getModalControllerReturnValueError(request,REDIRECT_2_PARENTS,"accio.massiva.creat.ko");
        }
        StringBuilder desc = new StringBuilder();
        for (var err: notificacionsError) {
            desc.append(err).append(" \n");
        }
        return getModalControllerReturnValueErrorWithDescription(request,REDIRECT_2_PARENTS,"accio.massiva.creat.part", desc.toString());
    }

    @GetMapping(value = {"/processar/massiu", "{notificacioId}/notificacio/"})
    public String processarMassiuModal(HttpServletRequest request, Model model) {

        var command = new MarcarProcessatCommand();
        model.addAttribute(command);
        model.addAttribute(IS_MASSIU, true);
        return MARCAR_PROCESSAT;
    }

    @PostMapping(value = {"/processar/massiu", "{notificacioId}/notificacio/processar/massiu"})
    public String processarMassiuPost(HttpServletRequest request, @Valid MarcarProcessatCommand command, BindingResult bindingResult, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request, REDIRECT_2_PARENTS, SELECCIO_BUIDA);
        }
        if (bindingResult.hasErrors()) {
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
            model.addAttribute(IS_MASSIU, true);
            return MARCAR_PROCESSAT;
        }
        boolean allOK = true;
        String resposta;
        for (var notificacioId : seleccio) {
            try {
                resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdminEntitat());
                if (resposta != null) {
                    MissatgesHelper.warning(request, resposta);
                    continue;
                }
                MissatgesHelper.success(request, String.format("La notificació (Id=%d) s'ha marcat com a processada", notificacioId));
            } catch (Exception ex) {
                var error = "Hi ha hagut un error processant la notificació";
                log.error(error, ex);
                allOK = false;
                MissatgesHelper.error(request, String.format("%s (Id=%d): %s", error, notificacioId, ex.getMessage()));
            }
        }

        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
        return allOK ? getModalControllerReturnValueSuccess(request, REDIRECT_2_PARENTS, "notificacio.controller.processar.massiu.ok")
            : getModalControllerReturnValueError(request, REDIRECT_2_PARENTS, "notificacio.controller.processar.massiu.ko");
    }

    @GetMapping(value = {"/eliminar", "{notificacioId}/notificacio/eliminar/"})
    public String eliminarMassiu(HttpServletRequest request, Model model) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,REDIRECT + referer,SELECCIO_BUIDA);
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
                getModalControllerReturnValueSuccess(request,REDIRECT + referer,"notificacio.controller.esborrar.massiu.ok")
                : getModalControllerReturnValueError(request,REDIRECT + referer,"notificacio.controller.esborrar.massiu.ko");
    }

    private void emplenarModelNotificacioInfo(EntitatDto entitatActual, Long notificacioId, HttpServletRequest request, String pipellaActiva, Model model) {

        var notificacio = notificacioService.findNotificacioInfo(notificacioId, isAdminEntitat());
        if (notificacio == null) {
            return;
        }
        if (notificacio.getGrupCodi() != null) {
            var grup = grupService.findByCodi(notificacio.getGrupCodi(), entitatActual.getId());
            notificacio.setGrup(grup);
        }
        if (!Strings.isNullOrEmpty(notificacio.getNotificaErrorDescripcio())) {
            notificacio.setNotificaErrorDescripcio(notificacio.getNotificaErrorDescripcio());
        }
        model.addAttribute("pipellaActiva", pipellaActiva);
        model.addAttribute("notificacio", notificacio);
        model.addAttribute(EVENT_TIPUS, EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, EVENT_TIPUS_ENUM));
        var permisGestio = false;
        if (notificacio.getProcediment() != null && !notificacio.getProcedimentCodiNotib().isEmpty()) {
            permisGestio = permisosService.hasNotificacioPermis(notificacioId, entitatActual.getId(), getCodiUsuariActual(), PermisEnum.GESTIO);
            permisGestio = permisGestio || procedimentService.hasPermisProcediment(notificacio.getProcediment().getId(), PermisEnum.GESTIO);
        }

        if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
            var mostrarSmInfo = envSmService.mostrarAfegirStateMachine(notificacioId);
            model.addAttribute("mostrarSmInfo", mostrarSmInfo);
        }

        model.addAttribute("permisGestio", permisGestio);
        model.addAttribute("permisAdmin", request.isUserInRole("NOT_ADMIN"));
    }


    private void emplenarModelEnviamentInfo(Long notificacioId, Long enviamentId, String pipellaActiva, Model model) {

        model.addAttribute("notificacio", notificacioService.findAmbId(notificacioId, isAdminEntitat()));
        model.addAttribute("pipellaActiva", pipellaActiva);
        var enviament = enviamentService.enviamentFindAmbId(enviamentId);
        model.addAttribute("enviament", enviament);
        if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual()) || RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual())) {
            var info = envSmService.infoStateMachine(enviamentId);
            model.addAttribute("smInfo", info);
            model.addAttribute("smEstats", info.getEstats());
            model.addAttribute("smEvents", info.getEvents());
        }
        model.addAttribute(EVENT_TIPUS, EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, EVENT_TIPUS_ENUM));
    }

    private boolean isAdminEntitat() {
        return RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("SI", "NO", false));
    }

}