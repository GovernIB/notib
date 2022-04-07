package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.*;
import es.caib.notib.war.command.MarcarProcessatCommand;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.helper.*;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
    private NotificacioListHelper notificacioListHelper;

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
        NotificacioFiltreDto filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE).asDto();
        assert entitatActual != null;
        return notificacioService.findIdsAmbFiltre(entitatActual.getId(), RolEnumDto.valueOf(RolHelper.getRolActual(request)),
                                                    organGestorCodi, usuariActual.getCodi(), filtre);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        NotificacioFiltreCommand notificacioFiltreCommand = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
        model.addAttribute(notificacioFiltreCommand);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        return "notificacioList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/filtrades/{referencia}")
    public String getFiltrades(HttpServletRequest request, @PathVariable String referencia, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        NotificacioFiltreCommand filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE);
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
        return "notificacioList";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        NotificacioFiltreDto filtre = notificacioListHelper.getFiltreCommand(request, NOTIFICACIONS_FILTRE).asDto();
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
            notificacions = notificacioService.findAmbFiltrePaginat(entitatActual != null ? entitatActual.getId() : null,
                                                    RolEnumDto.valueOf(RolHelper.getRolActual(request)), organGestorCodi, usuariActual.getCodi(), filtre,
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
    public List<CodiValorComuDto> getProcediments(HttpServletRequest request, Model model) {

        Long entitatId = EntitatHelper.getEntitatActual(request).getId();
        String organCodi = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organGestor = getOrganGestorActual(request);
        if (organGestor != null) {
            organCodi = organGestor.getCodi();
        }
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return procedimentService.getProcedimentsOrgan(entitatId, organCodi,null, rol, permis);
    }

    @RequestMapping(value = "/serveisOrgan", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorComuDto> getServeis(HttpServletRequest request, Model model) {

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
    public List<CodiValorComuDto> getProcedimentByOrganGestor(HttpServletRequest request, @PathVariable Long organGestor, Model model) {

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
    public List<CodiValorComuDto> getServeiByOrganGestor(HttpServletRequest request, @PathVariable Long organGestor, Model model) {

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

        MarcarProcessatCommand command = new MarcarProcessatCommand();
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
            String resposta = notificacioService.marcarComProcessada(notificacioId, command.getMotiu(), isAdministrador(request));
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
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
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
        if (enviada) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo", "notificacio.controller.enviament.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo", "notificacio.controller.enviament.error");
    }

    @RequestMapping(value = "/{notificacioId}/registrar", method = RequestMethod.GET)
    public String registrar(HttpServletRequest request, @PathVariable Long notificacioId, Model model) throws RegistreNotificaException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        List<RegistreIdDto> registresIdDto = notificacioService.registrarNotificar(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        if (registresIdDto == null || registresIdDto.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.registrar.error"));
            return "notificacioInfo";
        }
        for (RegistreIdDto registreIdDto : registresIdDto) {
            if (registreIdDto.getNumero() != null) {
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

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean reactivat = notificacioService.reactivarConsulta(notificacioId);
        emplenarModelNotificacioInfo(entitatActual, notificacioId, request,"accions", model);
        model.addAttribute("pestanyaActiva", "accions");
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo","notificacio.controller.reactivar.consulta.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo","notificacio.controller.reactivar.consulta.error");
    }

    @RequestMapping(value = "/{notificacioId}/reactivarsir", method = RequestMethod.GET)
    public String reactivarsir(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean reactivat = notificacioService.reactivarSir(notificacioId);
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

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatNotifica", method = RequestMethod.GET)
    public String refrescarEstatNotifica(HttpServletRequest request, @PathVariable Long notificacioId, @PathVariable Long enviamentId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        NotificacioEnviamenEstatDto enviamentEstat = notificacioService.enviamentRefrescarEstat(entitatActual.getId(), enviamentId);
        boolean totbe = !enviamentEstat.isNotificaError();
        String msg = totbe ? "notificacio.controller.refrescar.estat.ok" : "notificacio.controller.refrescar.estat.error";
        MissatgesHelper.error(request, getMessage(request, msg));
        emplenarModelEnviamentInfo(notificacioId, enviamentId, "estatNotifica", model, request);
        return "enviamentInfo";
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
    public void certificacioDescarregar(HttpServletResponse response, @PathVariable Long notificacioId, @PathVariable Long enviamentId) throws IOException {

        ArxiuDto arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
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
        if (enviada) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo", "notificacio.controller.reenviar.errors.ok");
        }
        return getAjaxControllerReturnValueError(request, "notificacioInfo", "notificacio.controller.reenviar.errors.error");
    }

    @RequestMapping(value = "/{notificacioId}/reactivarErrors", method = RequestMethod.GET)
    public String reactivarErrors(HttpServletRequest request, @PathVariable Long notificacioId, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        boolean reactivat = notificacioService.reactivarNotificacioAmbErrors(notificacioId);
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

        if (!notificacionsError.isEmpty()) {
            if (notificacionsError.size() == seleccio.size()) {
                getModalControllerReturnValueError(request,"redirect:../..","accio.massiva.creat.ko");
            } else {
                String desc = "";
                for (String err: notificacionsError) {
                    desc = desc + err + " \n";
                }
                return getModalControllerReturnValueErrorWithDescription(request,"redirect:../..","accio.massiva.creat.part", desc);
            }
        }
        return getModalControllerReturnValueSuccess(request,"redirect:../..","accio.massiva.creat.ok");
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
        if (allOK) {
            return getModalControllerReturnValueSuccess(request, "redirect:../..", "notificacio.controller.processar.massiu.ok");
        }
        return getModalControllerReturnValueError(request, "redirect:../..", "notificacio.controller.processar.massiu.ko");
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

        if (notificacionsNoEsborrades.isEmpty()){
            return getModalControllerReturnValueSuccess(request,"redirect:" + referer,"notificacio.controller.esborrar.massiu.ok");
        }
        return getModalControllerReturnValueError(request,"redirect:" + referer,"notificacio.controller.esborrar.massiu.ko");
    }

    private void emplenarModelNotificacioInfo(EntitatDto entitatActual, Long notificacioId, HttpServletRequest request, String pipellaActiva, Model model) {

        NotificacioInfoDto notificacio = notificacioService.findNotificacioInfo(notificacioId, isAdministrador(request));
        if (notificacio != null && notificacio.getGrupCodi() != null) {
            GrupDto grup = grupService.findByCodi(notificacio.getGrupCodi(), entitatActual.getId());
            notificacio.setGrup(grup);
        }
        model.addAttribute("pipellaActiva", pipellaActiva);
        model.addAttribute("notificacio", notificacio);
        String text = "es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.";
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class, text));
        model.addAttribute("permisGestio", null);
        if (notificacio != null && notificacio.getProcediment() != null && !notificacio.getProcedimentCodiNotib().isEmpty()) {
            model.addAttribute("permisGestio", procedimentService.hasPermisProcediment(notificacio.getProcediment().getId(), PermisEnum.GESTIO));
        }
        model.addAttribute("permisAdmin", request.isUserInRole("NOT_ADMIN"));
    }


    private void emplenarModelEnviamentInfo(Long notificacioId, Long enviamentId, String pipellaActiva, Model model, HttpServletRequest request) {

        model.addAttribute("notificacio", notificacioService.findAmbId(notificacioId, isAdministrador(request)));
        model.addAttribute("pipellaActiva", pipellaActiva);
        NotificacioEnviamentDto enviament = enviamentService.enviamentFindAmbId(enviamentId);
        model.addAttribute("enviament", enviament);
        String text = "es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.";
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