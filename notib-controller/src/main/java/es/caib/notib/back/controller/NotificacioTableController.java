package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.*;
import es.caib.notib.back.helper.*;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.dto.missatges.Missatge;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ejb.access.LocalSlsbInvokerInterceptor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private NotificacioBackHelper notificacioListHelper;
    @Autowired
    private PermisosService permisosService;
    @Autowired
    private EnviamentSmService envSmService;
    @Autowired
    private ColumnesService columnesService;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private AccioMassivaService accioMassivaService;
    @Autowired
    private AplicacioService aplicacioService;

    private static final  String NOTIFICACIONS_FILTRE = "notificacions_filtre";
    private static final  String NOTIFICACIONS_FILTRE_ESBORRADES = "notificacions_filtre_esborrades";
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
    private static final String SELECCIO_BUIDA = "accio.massiva.seleccio.buida";
    private static final String PERMIS_DENGAT = "Permís denegat";
    @Autowired
    private OrganGestorService organGestorService;

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
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        var usuari = aplicacioService.getUsuariActual();
        if (usuari.getOrganDefecte() != null) {
            filtre.setOrganGestor(usuari.getOrganDefecte() + "");
            filtre.setFiltreSimpleActiu(false);
        }
        if (usuari.getProcedimentDefecte() != null) {
            filtre.setProcedimentId(usuari.getProcedimentDefecte());
            filtre.setFiltreSimpleActiu(false);
        }
        filtre.setDeleted(false);
        model.addAttribute(filtre);
        var codiUsuari = getCodiUsuariActual();
        var columnes = columnesService.getColumnesRemeses(entitatActual.getId(), codiUsuari);
        model.addAttribute("mostrarFiltreAvancat", !filtre.isFiltreSimpleActiu());
        model.addAttribute("columnes", ColumnesRemesesCommand.asCommand(columnes));
        model.addAttribute("nomesFiReintents", filtre.isNomesFiReintents());
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "notificacioList";
    }

    @GetMapping(value = "/notificacionsEsborrades")
    public String getNotificacionsEsborrades(HttpServletRequest request, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var organGestorActual = getOrganGestorActual(request);
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        filtre.setDeleted(true);
        model.addAttribute(filtre);
        var codiUsuari = getCodiUsuariActual();
        var columnes = columnesService.getColumnesRemeses(entitatActual.getId(), codiUsuari);
        model.addAttribute("columnes", ColumnesRemesesCommand.asCommand(columnes));
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "notificacioEsborradaList";
    }

    @PostMapping(value = "/notificacionsEsborrades")
    public String getNotificacionsEsborradesPost(HttpServletRequest request, NotificacioFiltreCommand command, Model model) {
        RequestSessionHelper.actualitzarObjecteSessio(request, NOTIFICACIONS_FILTRE, command);
        if (!command.getErrors().isEmpty()) {
            MissatgesHelper.error(request, getErrorMsg(request, command.getErrors()));
        }
        return notificacionsEsborrades(request, model);
    }

    public String notificacionsEsborrades(HttpServletRequest request, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var organGestorActual = getOrganGestorActual(request);
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        filtre.setDeleted(true);
        model.addAttribute(filtre);
        var codiUsuari = getCodiUsuariActual();
        var columnes = columnesService.getColumnesRemeses(entitatActual.getId(), codiUsuari);
        model.addAttribute("columnes", ColumnesRemesesCommand.asCommand(columnes));
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "notificacioEsborradaList";
    }

    @GetMapping(value = "/filtrades/{referencia}")
    public String getFiltrades(HttpServletRequest request, @PathVariable String referencia, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var organGestorActual = getOrganGestorActual(request);
        var filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        filtre.setDataInici(null);
        filtre.setDataFi(null);
        filtre.setReferencia(referencia);
        filtre.setMassiu(false);
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

        var entitatActual = getEntitatActualComprovantPermisos(request);
        RequestSessionHelper.actualitzarObjecteSessio(request, NOTIFICACIONS_FILTRE, command);
        if (!command.getErrors().isEmpty()) {
            MissatgesHelper.error(request, getErrorMsg(request, command.getErrors()));
        }
        var codiUsuari = getCodiUsuariActual();
        var organGestorActual = getOrganGestorActual(request);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        var columnes = columnesService.getColumnesRemeses(entitatActual.getId(), codiUsuari);
        model.addAttribute("mostrarFiltreAvancat", !command.isFiltreSimpleActiu());
        model.addAttribute("columnes", ColumnesRemesesCommand.asCommand(columnes));
        model.addAttribute("notificacioFiltreCommand", command);
        model.addAttribute("nomesAmbErrors", command.isNomesAmbErrors());
        model.addAttribute("nomesAmbEntregaPostal", command.isNomesAmbEntregaPostal());
        model.addAttribute("nomesFiReintents", command.isNomesFiReintents());
        return "notificacioList";
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var notificacions = new PaginaDto<NotificacioTableItemDto>();
        var filtreCommand = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        if (!filtreCommand.getErrors().isEmpty()) {
            return DatatablesHelper.getDatatableResponse(request, notificacions, "id", SESSION_ATTRIBUTE_SELECCIO);
        }
        var filtre = filtreCommand.asDto();
        var isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());

        try {
            if (isUsuariEntitat && filtre != null) {
                filtre.setEntitatId(entitatActual.getId());
            }
            var organGestorCodi = filtre.getOrganGestor();
            if (isAdminOrgan && entitatActual != null && Strings.isNullOrEmpty(organGestorCodi)) {
                OrganGestorDto organGestorActual = getOrganGestorActual(request);
                organGestorCodi = organGestorActual.getCodi();
            }
//            filtre.setDeleted(false);
            notificacions = notificacioService.findAmbFiltrePaginat(entitatActual != null ? entitatActual.getId() : null,
                                                RolEnumDto.valueOf(sessionScopedContext.getRolActual()), organGestorCodi, getCodiUsuariActual(), filtre,
                                                DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
        }
        return DatatablesHelper.getDatatableResponse(request, notificacions, "id", SESSION_ATTRIBUTE_SELECCIO);
    }


    @GetMapping(value = "/visualitzar")
    public String visualitzar(HttpServletRequest request, Model model) {

        var entitat = sessionScopedContext.getEntitatActual();
        var columnes = columnesService.getColumnesRemeses(entitat.getId(), getCodiUsuariActual());
        model.addAttribute(columnes != null ? ColumnesRemesesCommand.asCommand(columnes) : new ColumnesCommand());
        return "remesesColumns";
    }

    @PostMapping(value = "/visualitzar/save")
    public String save(HttpServletRequest request, @Valid ColumnesRemesesCommand columnesCommand, BindingResult bindingResult, Model model) {

        var entitat = sessionScopedContext.getEntitatActual();
        if (bindingResult.hasErrors()) {
            return "remesesColumns";
        }
        var organGestorActual = getOrganGestorActual(request);

        model.addAttribute(new NotificacioFiltreCommand());
        notificacioListHelper.fillModel(entitat, organGestorActual, request, model);
        columnesService.updateColumnesRemeses(entitat.getId(), ColumnesRemesesCommand.asDto(columnesCommand));
        return getModalControllerReturnValueSuccess(request, "redirect:notificacio", "enviament.controller.modificat.ok");
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
        var organ = organGestorService.findById(entitatId, organGestor);
        return procedimentService.getProcedimentsOrgan(entitatId, organ.getCodi(), organGestor, rol, permis);
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

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        log.info("[WEBSOCKET] missatge rebut");
        return "foo";
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

    @GetMapping(value = "/{notificacioId}/restore")
    public String recuperar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        try {
            notificacioService.restore(entitatActual.getId(), notificacioId);
            return getModalControllerReturnValueSuccess(request,REDIRECT + referer,"notificacio.controller.recuperar.ok");
        } catch (Exception ex) {
            log.error("Hi ha hagut un error recuperant la notificació", ex);
            return getModalControllerReturnValueError(request, REDIRECT + referer, "notificacio.controller.recuperar.ko", new Object[]{ex.getMessage()});
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
    @ResponseBody
    public Missatge enviar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        var ok = notificacioService.enviarNotificacioANotifica(notificacioId, true);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        var msg = getMessage(request, ok ? "notificacio.controller.accio.enviada.ok" : "notificacio.controller.accio.enviada.error");
        return Missatge.builder().ok(ok).msg(msg).build();
    }

    @GetMapping(value = "/{notificacioId}/enviar/entrega/postal/{notificacioUuid}")
    @ResponseBody
    public Missatge enviarEntregaPostal(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable String notificacioUuid, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        boolean ok;
        try {
            ok = notificacioService.enviarEntregaPostal(notificacioUuid, false);
        } catch (Exception ex) {
            ok = false;
        }
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        model.addAttribute(PESTANYA_ACTIVA, ACCIONS);
        var msg = getMessage(request, ok ? "notificacio.controller.accio.enviada.ok" : "notificacio.controller.accio.enviada.error");
        return Missatge.builder().ok(ok).msg(msg).build();
    }

    @GetMapping(value = "/{notificacioId}/registrar")
    @ResponseBody
    public Missatge registrar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) throws RegistreNotificaException {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        RespostaAccio<String> resposta = notificacioService.enviarNotificacioARegistre(notificacioId, true);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,ACCIONS, model);
        var ok = !resposta.isEmpty() && resposta.getErrors().isEmpty();
        var msg = getMessage(request, ok ? "notificacio.controller.registrar.ok" : "notificacio.controller.registrar.error");
        return Missatge.builder().ok(ok).msg(msg).build();
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

        var rolActual = sessionScopedContext.getRolActual();
        if (!RolHelper.isUsuariActualAdministrador(rolActual) && !RolHelper.isUsuariActualAdministradorEntitat(rolActual)) {
            throw new SecurityException(PERMIS_DENGAT);
        }
        var ok = envSmService.canviarEstat(enviamentId, estat);
        var msg = getMessage(request, ok ? VALIDACIO_OK : ERROR_MSG);
        return Missatge.builder().ok(ok).msg(msg).build();
    }

    @GetMapping(value = "/enviament/{enviamentId}/state/machine/enviar/event/{event}")
    @ResponseBody
    public Missatge enviamentStateMachineSetEvent(HttpServletRequest request, @PathVariable Long enviamentId, @PathVariable String event) {

        var rolActual = sessionScopedContext.getRolActual();
        if (!RolHelper.isUsuariActualAdministrador(rolActual) && !RolHelper.isUsuariActualAdministradorEntitat(rolActual)) {
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
        if (enviamentEstat == null) {
            return Missatge.builder().ok(true).msg(getMessage(request, "notificacio.controller.refrescar.estat.no.refrescar")).build();
        }
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
        if (totbe == null) {
            return Missatge.builder().ok(true).msg(getMessage(request, "notificacio.controller.refrescar.estat.no.refrescar")).build();
        }
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
    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/cancelar/entrega/postal")
    public Missatge cancelarEntregaPostal(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        getEntitatActualComprovantPermisos(request);
        var ok = notificacioService.cancelarEntregaPostal(enviamentId);
        return Missatge.builder().ok(ok).msg(getMessage(request, ok ? "entrega.postal.cancelar.ok" : "entrega.postal.cancelar.error")).build();
    }

    @ResponseBody
    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/consultar/estat/entrega/postal")
    public Missatge infoEntregaPostal(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        getEntitatActualComprovantPermisos(request);
        var ok = notificacioService.consultarEstatEntregaPostal(enviamentId);
        return Missatge.builder().ok(ok).msg(getMessage(request, ok ? "entrega.postal.consultar.ok" : "entrega.postal.consultar.error")).build();
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
    public void certificacioDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long enviamentId) throws Exception {

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

    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/certificacioPostalDescarregar")
    @ResponseBody
    public void certificacioPostalDescarregar(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long enviamentId) throws Exception {

        try {
            var arxiu = enviamentService.getCertificacioPostalArxiu(enviamentId);
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

        var baos = new ByteArrayOutputStream();
        var locale = new Locale(sessionScopedContext.getIdiomaUsuari());
        try (var zos = new ZipOutputStream(baos);) {

            boolean contingut = false;
//            var baos = new ByteArrayOutputStream();
//            var zos = new ZipOutputStream(baos);
            var enviaments = enviamentService.enviamentFindAmbNotificacio(notificacioId);
            Map<String, Integer> interessats = new HashMap<>();
            ArxiuDto arxiu;
            int numInteressats = 0;
            for (var env : enviaments) {
                if (env.getNotificaCertificacioData() == null) {
                    continue;
                }
                try {
                    arxiu = notificacioService.enviamentGetCertificacioArxiu(env.getId());
                } catch (Exception ex) {
                    log.error("Error descarregant la certificacio de l'enviament " + env.getId(), ex);
                    continue;
                }
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
            response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        } catch (Exception ex) {
            var msg = getMessage(request, "notificacio.list.enviament.descaregar.certificacio.error");
            log.error(msg, ex);
            MissatgesHelper.error(request, msg);
            throw new RuntimeException(msg);
        } finally {
            baos.close();
        }
        var nom = MessageHelper.getInstance().getMessage("notificacio.list.enviament.certificacio.zip.nom", null, locale);
        writeFileToResponse(nom + "_" + notificacioId + ".zip", baos.toByteArray(), response);
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
        FitxerDto justificant;
        try {
            justificant = justificantService.generarJustificantEnviament(notificacioId, entitatActual.getId(), sequence);
            if (justificant == null) {
                throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
            }
        } catch (Exception ex) {
            throw new IOException("Error generant el justificant");
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
        FitxerDto justificant = null;
        try {
            justificant = justificantService.generarJustificantComunicacioSIR(enviamentId, entitatActual.getId(), sequence);
            if (justificant == null) {
                throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
            }
            response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
            writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
        } catch (Exception ex) {
            log.error("Error generant el justificant " + ex.getMessage());
        } finally {
            if (justificant == null) {
                writeFileToResponse("error_justificant.pdf", "Error generant el justificant".getBytes(StandardCharsets.UTF_8), response);
            }
        }

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

    @GetMapping(value = "/{notificacioId}/anular")
    public String anularGet(HttpServletResponse response, HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var anular = new AnularCommand();
        anular.setNotificacioId(notificacioId);
        model.addAttribute(anular);
        return "anularForm";
    }

    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/anular")
    public String anularOEGetEnviament(HttpServletResponse response, HttpServletRequest request, Model model, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        var anular = new AnularCommand();
        anular.setEnviamentId(enviamentId);
        model.addAttribute(anular);
        return "anularForm";
    }

    @PostMapping(value = "/anular")
    public String anularPost(HttpServletResponse response, HttpServletRequest request, Model model, AmpliacionPlazoCommand ampliacionPlazo) {

        try {
//            var ampliarPlazoOE = new AmpliarPlazoOE();
//            ampliarPlazoOE.setPlazo(ampliacionPlazo.getDies());
//            ampliarPlazoOE.setMotivo(ampliacionPlazo.getMotiu());
//            Long accioMassivaId;
//            if (ampliacionPlazo.isMassiu()) {
//                var entitatActual = sessionScopedContext.getEntitatActual();
//                var seleccio = ampliacionPlazo.getNotificacionsId() != null && !ampliacionPlazo.getNotificacionsId().isEmpty() ? ampliacionPlazo.getNotificacionsId() : ampliacionPlazo.getEnviamentsId();
//                var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
//                var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
//                var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.AMPLIAR_TERMINI).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
//                accioMassivaId = accioMassivaService.altaAccioMassiva(accio);
//                accio.setAccioId(accioMassivaId);
//                accio.setAmpliacionPlazo(ConversioTipusHelper.convertir(ampliacionPlazo, AmpliacionPlazoDto.class));
//                accioMassivaService.executarAccio(accio);
//            }
//            var resposta = notificacioService.ampliacionPlazoOE(ConversioTipusHelper.convertir(ampliacionPlazo, AmpliacionPlazoDto.class));
            var resposta = RespuestaAmpliarPlazoOE.builder().codigoRespuesta("000").build();
            return resposta != null && resposta.isOk() ? getModalControllerReturnValueSuccess(request, "redirect:/enviament", "ampliar.plazo.ok")
                    : getModalControllerReturnValueError(request, "redirect:/enviament", "ampliar.plazo.error", new Object[]{resposta.getDescripcions() != null ? resposta.getDescripcions() : resposta.getDescripcionRespuesta()});
        } catch (Exception ex) {
            log.error("Error ampliant el plazo", ex);
            return getModalControllerReturnValueError(request, "redirect:/enviament", "ampliar.plazo.error");
        }
    }


    @GetMapping(value = "/{notificacioId}/ampliacion/plazo")
    public String ampliarPlazoOEGet(HttpServletResponse response, HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var ampliacion = new AmpliacionPlazoCommand();
        ampliacion.setCaducitat(notificacioService.getCaducitat(notificacioId));
        ampliacion.setNotificacioId(notificacioId);
        model.addAttribute(ampliacion);
        return "ampliarPlazoForm";
    }

    @GetMapping(value = "/ampliacion/plazo/massiu")
    public String ampliarPlazoOEMassiu(HttpServletResponse response, HttpServletRequest request, Model model) {

        var seleccio = getIdsSeleccionats(request);
        var redirect = "redirect:../../..";
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,redirect,SELECCIO_BUIDA);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return getModalControllerReturnValueError(request, redirect,"accio.massiva.creat.ko");
        }
        var ampliacion = new AmpliacionPlazoCommand();
        ampliacion.setMassiu(true);
        ampliacion.setNotificacionsId(new ArrayList<>(seleccio));
        model.addAttribute(ampliacion);
        return "ampliarPlazoForm";
    }


    @GetMapping(value = "/{notificacioId}/enviament/{enviamentId}/ampliacion/plazo")
    public String ampliarPlazoOEGetEnviament(HttpServletResponse response, HttpServletRequest request, Model model, @PathVariable Long notificacioId, @PathVariable Long enviamentId) {

        var ampliacion = new AmpliacionPlazoCommand();
        ampliacion.setEnviamentId(enviamentId);
        model.addAttribute(ampliacion);
        return "ampliarPlazoForm";
    }


    @PostMapping(value = "/ampliacion/plazo")
    public String ampliarPlazoOEPost(HttpServletResponse response, HttpServletRequest request, Model model, AmpliacionPlazoCommand ampliacionPlazo) {

        try {
            var ampliarPlazoOE = new AmpliarPlazoOE();
            ampliarPlazoOE.setPlazo(ampliacionPlazo.getDies());
            ampliarPlazoOE.setMotivo(ampliacionPlazo.getMotiu());
            Long accioMassivaId;
            if (ampliacionPlazo.isMassiu()) {
                var entitatActual = sessionScopedContext.getEntitatActual();
                var seleccio = ampliacionPlazo.getNotificacionsId() != null && !ampliacionPlazo.getNotificacionsId().isEmpty() ? ampliacionPlazo.getNotificacionsId() : ampliacionPlazo.getEnviamentsId();
                var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
                var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
                var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.AMPLIAR_TERMINI).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
                accioMassivaId = accioMassivaService.altaAccioMassiva(accio);
                accio.setAccioId(accioMassivaId);
                accio.setAmpliacionPlazo(ConversioTipusHelper.convertir(ampliacionPlazo, AmpliacionPlazoDto.class));
                accioMassivaService.executarAccio(accio);
            }
            var resposta = notificacioService.ampliacionPlazoOE(ConversioTipusHelper.convertir(ampliacionPlazo, AmpliacionPlazoDto.class));
            return resposta != null && resposta.isOk() ? getModalControllerReturnValueSuccess(request, "redirect:/enviament", "ampliar.plazo.ok")
                    : getModalControllerReturnValueError(request, "redirect:/enviament", "ampliar.plazo.error", new Object[]{resposta.getDescripcions() != null ? resposta.getDescripcions() : resposta.getDescripcionRespuesta()});
        } catch (Exception ex) {
            log.error("Error ampliant el plazo", ex);
            return getModalControllerReturnValueError(request, "redirect:/enviament", "ampliar.plazo.error");
        }
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
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return getModalControllerReturnValueError(request, redirect,"accio.massiva.creat.ko");
        }
        List<String> notificacionsError = new ArrayList<>();
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.ENVIAR_NOT_MOVIL).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        try {
            accioMassivaService.executarAccio(accio);
        } catch (Exception ex) {
            return getModalControllerReturnValueError(request, redirect,"accio.massiva.creat.ko");
        }
        return getModalControllerReturnValueSuccess(request, redirect,"accio.massiva.creat.ok");
//        for (Long notificacioId : seleccio) {
//            try {
//                notificacioService.reenviarNotificaionsMovil(notificacioId);
//            } catch (Exception e) {
//                notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
//            }
//        }
//        if (notificacionsError.isEmpty()) {
//            return getModalControllerReturnValueSuccess(request, redirect,"accio.massiva.creat.ok");
//        }
//        if (notificacionsError.size() == seleccio.size()) {
//            return getModalControllerReturnValueError(request, redirect,"accio.massiva.creat.ko");
//        }
//        var desc = new StringBuilder();
//        for (var err: notificacionsError) {
//            desc.append(err).append(" \n");
//        }
//        return getModalControllerReturnValueErrorWithDescription(request,redirect,"accio.massiva.creat.part", desc.toString());
    }

    @GetMapping(value = "/reactivar/registre")
    public String reactivarReintentar(HttpServletRequest request, Model model) {

        // identificadors de les notificacions, no dels enviaments.
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,REDIRECT_2_PARENTS,SELECCIO_BUIDA);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return getModalControllerReturnValueError(request,REDIRECT_2_PARENTS,"accio.massiva.creat.ko");
        }
        List<String> notificacionsError = new ArrayList<>();
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.REACTIVAR_REGISTRE).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        accioMassivaService.executarAccio(accio);
//        for (var notificacioId : seleccio) {
//            try {
//                notificacioService.reactivarRegistre(notificacioId);
//                notificacioService.resetNotificacioARegistre(notificacioId);
//            } catch (Exception e) {
//                notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
//            }
//        }
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
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
           return MARCAR_PROCESSAT;
        }
        if (bindingResult.hasErrors()) {
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
            model.addAttribute(IS_MASSIU, true);
            return MARCAR_PROCESSAT;
        }
//        boolean allOK = true;
//        String resposta;
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.MARCAR_PROCESSADES).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        accioMassivaService.executarAccio(accio);
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
        return getModalControllerReturnValueSuccess(request, REDIRECT_2_PARENTS, "accio.massiva.creat.ok");

//        for (var notificacioId : seleccio) {
//            try {
//                resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdminEntitat());
//                if (resposta != null) {
//                    MissatgesHelper.warning(request, resposta);
//                    continue;
//                }
//                MissatgesHelper.success(request, String.format("La notificació (Id=%d) s'ha marcat com a processada", notificacioId));
//            } catch (Exception ex) {
//                var error = "Hi ha hagut un error processant la notificació";
//                log.error(error, ex);
//                allOK = false;
//                MissatgesHelper.error(request, String.format("%s (Id=%d): %s", error, notificacioId, ex.getMessage()));
//            }
//        }
//        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<>());
//        return allOK ? getModalControllerReturnValueSuccess(request, REDIRECT_2_PARENTS, "notificacio.controller.processar.massiu.ok")
//            : getModalControllerReturnValueError(request, REDIRECT_2_PARENTS, "notificacio.controller.processar.massiu.ko");
    }

    @GetMapping(value = {"/eliminar", "{notificacioId}/notificacio/eliminar/"})
    public String eliminarMassiu(HttpServletRequest request, Model model) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,REDIRECT + referer,SELECCIO_BUIDA);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return getModalControllerReturnValueError(request,REDIRECT + referer,"notificacio.controller.esborrar.massiu.ko");
        }
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.ESBORRAR).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        var notificacionsNoEsborrades = accioMassivaService.esborrarNotificacions(accio);
        Set<Long> noEsborrades = new HashSet<>();
        if (!notificacionsNoEsborrades.isEmpty()) {
            for (var not : notificacionsNoEsborrades) {
                MissatgesHelper.error(request, not.getErrorDesc());
                noEsborrades.add(not.getId());
            }
        }
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, noEsborrades);
        return notificacionsNoEsborrades.isEmpty() ?
                getModalControllerReturnValueSuccess(request,REDIRECT + referer,"notificacio.controller.esborrar.massiu.ok")
                : getModalControllerReturnValueError(request,REDIRECT + referer,"notificacio.controller.esborrar.massiu.ko");
    }

    @GetMapping(value = {"/recuperar", "{notificacioId}/recuperar/"})
    public String recuperarMassiu(HttpServletRequest request, Model model) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request,REDIRECT + referer,SELECCIO_BUIDA);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return getModalControllerReturnValueError(request,REDIRECT + referer,"notificacio.controller.esborrar.massiu.ko");
        }
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.RECUPERAR_ESBORRADES).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        var notificacionsNoRecuperades = accioMassivaService.recuperarNotificacionsEsborrades(accio);
        Set<Long> noRecuperades = new HashSet<>();
        if (!notificacionsNoRecuperades.isEmpty()) {
            for (var not : notificacionsNoRecuperades) {
                MissatgesHelper.error(request, not.getErrorDesc());
                noRecuperades.add(not.getId());
            }
        }
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, notificacionsNoRecuperades);
        return notificacionsNoRecuperades.isEmpty() ?
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