package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaDataDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaFiltreDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaInfoDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaTableItemDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.InvalidCSVFileNotificacioMassivaException;
import es.caib.notib.core.api.exception.MaxLinesExceededException;
import es.caib.notib.core.api.exception.NotificacioMassivaException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.GestioDocumentalService;
import es.caib.notib.core.api.service.NotificacioMassivaService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.OperadorPostalService;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.command.NotificacioMassivaCommand;
import es.caib.notib.war.command.NotificacioMassivaFiltreCommand;
import es.caib.notib.war.helper.CaducitatHelper;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.ExceptionHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.NotificacioListHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador per a la consulta i gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/notificacio/massiva")
public class NotificacioMassivaController extends TableAccionsMassivesController {

    private final static String TABLE_FILTRE = "not_massiva_filtre";
    private final static String TABLE_NOTIFICACIONS_FILTRE = "not_massiva_nots_filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "NotificacioController.session.seleccio";

    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private NotificacioMassivaService notificacioMassivaService;
    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private OperadorPostalService operadorPostalService;
    @Autowired
    private NotificacioListHelper notificacioListHelper;
    @Autowired
    private GestioDocumentalService gestioDocumentalService;

    public NotificacioMassivaController() {
        super.sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(HttpServletRequest request, Model model) {

        NotificacioMassivaFiltreCommand filtre = getFiltreCommand(request);
        model.addAttribute("notificacioMassivaFiltreCommand", filtre);
        model.addAttribute("notificacioMassivaEstats", EnumHelper.getOptionsForEnum(NotificacioMassivaEstatDto.class,
                                                "es.caib.notib.core.api.dto.notificacio.NotificacioMassivaEstatDto."));
        return "notificacioMassivaList";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(HttpServletRequest request, NotificacioMassivaFiltreCommand command, Model model) throws ParseException {

        RequestSessionHelper.actualitzarObjecteSessio(request, TABLE_FILTRE, command);
        return "notificacioMassivaList";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        NotificacioMassivaFiltreDto filtre = getFiltreCommand(request).asDto();
        PaginaDto<NotificacioMassivaTableItemDto> notificacions = new PaginaDto<>();
        try {

            notificacions = notificacioMassivaService.findAmbFiltrePaginat(
                    entitatActual != null ? entitatActual.getId() : null,
                    filtre,
                    RolEnumDto.valueOf(RolHelper.getRolActual(request)),
                    DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
        }
        return DatatablesHelper.getDatatableResponse(request, notificacions);
    }

    @RequestMapping(value = "/{id}/resum", method = RequestMethod.GET)
    public String summary(HttpServletRequest request, Model model, @PathVariable Long id) {

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        NotificacioMassivaInfoDto info = notificacioMassivaService.getNotificacioMassivaInfo(entitatActual.getId(), id);
        model.addAttribute("info", info);
        return "notificacioMassivaInfo";
    }

    @RequestMapping(value = "/{id}/csv/download", method = RequestMethod.GET)
    @ResponseBody
    public void csvDownload(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws IOException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getCSVFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }

    @RequestMapping(value = "/{id}/zip/download", method = RequestMethod.GET)
    public void zipDownload(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws IOException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getZipFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }

    @RequestMapping(value = "/{id}/resum/download", method = RequestMethod.GET)
    public void summaryDownload(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws IOException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getResumFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }

    @RequestMapping(value = "/{id}/errors/download", method = RequestMethod.GET)
    public void errorsDownload(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws IOException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getErrorsFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }

    @RequestMapping(value = "/{id}/posposar", method = RequestMethod.GET)
    public String posposar(HttpServletRequest request, @PathVariable Long id) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        try {
            notificacioMassivaService.posposar(entitatActual.getId(), id);
        } catch (Exception e) {
            log.error("Hi ha hagut un error posposant la notificació massiva", e);
            return getModalControllerReturnValueError(request, "redirect:..", "notificacio.massiva.controller.posposar.ko", new Object[]{e.getMessage()});
        }
        return getModalControllerReturnValueSuccess(request,"redirect:..","notificacio.massiva.controller.posposar.ok");
    }

    @RequestMapping(value = "/{id}/reactivar", method = RequestMethod.GET)
    public String reactivar(HttpServletRequest request, @PathVariable Long id) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        try {
            notificacioMassivaService.reactivar(entitatActual.getId(), id);
        } catch (Exception e) {
            log.error("Hi ha hagut un error reactivant la notificació massiva", e);
            return getModalControllerReturnValueError(request,"redirect:..", "notificacio.massiva.controller.reactivar.ko", new Object[]{e.getMessage()});
        }
        return getModalControllerReturnValueSuccess(request, "redirect:..", "notificacio.massiva.controller.reactivar.ok");
    }

    @RequestMapping(value = "/{id}/remeses", method = RequestMethod.GET)
    public String consultarRemeses(HttpServletRequest request, Model model, @PathVariable Long id) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        NotificacioFiltreCommand notificacioFiltreCommand = notificacioListHelper.getFiltreCommand(request, TABLE_NOTIFICACIONS_FILTRE);
        model.addAttribute(notificacioFiltreCommand);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        model.addAttribute("notificacioMassivaId", id);
        NotificacioMassivaDataDto notMassivaData = notificacioMassivaService.findById(entitatActual.getId(), id);
        SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
        String[] txt = new String[] {data.format(notMassivaData.getCreatedDate()), notMassivaData.getCsvFilename(), notMassivaData.getCreatedBy().getCodi()};
        model.addAttribute("subtitle", getMessage(request, "notificacio.massiva.notificacions.list.titol.sub", txt));
        return "notificacioMassivaNotificacionsList";
    }

    @RequestMapping(value = "/{id}/remeses", method = RequestMethod.POST)
    public String consultarRemesesUpdateFiltre(HttpServletRequest request, NotificacioFiltreCommand command, Model model) {

        RequestSessionHelper.actualitzarObjecteSessio(request, TABLE_NOTIFICACIONS_FILTRE, command);
        return "notificacioMassivaNotificacionsList";
    }

    @RequestMapping(value = "/{id}/remeses/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesHelper.DatatablesResponse consultarRemesesDatatable(HttpServletRequest request, @PathVariable Long id) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        NotificacioFiltreDto filtre = notificacioListHelper.getFiltreCommand(request, TABLE_NOTIFICACIONS_FILTRE).asDto();
        PaginaDto<NotificacioTableItemDto> notificacions = new PaginaDto<>();
        try {
            notificacions = notificacioMassivaService.findNotificacions(entitatActual.getId(), id, filtre, DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
        }
        return DatatablesHelper.getDatatableResponse(request, notificacions, "id", SESSION_ATTRIBUTE_SELECCIO);
    }

    @RequestMapping(value = "/new")
    public String get(HttpServletRequest request, Model model) {

        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
        NotificacioMassivaCommand notificacioMassiuCommand = new NotificacioMassivaCommand();
        notificacioMassiuCommand.setCaducitat(CaducitatHelper.sumarDiesNaturals(10));
        model.addAttribute("notificacioMassivaCommand", notificacioMassiuCommand);
        model.addAttribute("emailSize", notificacioMassiuCommand.getEmailDefaultSize());
        return getNotificacioMassivaForm(entitat, request, model);
    }

    private String getNotificacioMassivaForm(EntitatDto entitat, HttpServletRequest request, Model model) {

        OrganGestorDto organGestorActual = getOrganGestorActual(request);
//        if (organGestorActual != null) {
//            model.addAttribute("pagadorsPostal", operadorPostalService.findByEntitatAndOrganGestor(entitat, organGestorActual));
//        } else {
            model.addAttribute("pagadorsPostal", operadorPostalService.findByEntitat(entitat.getId()));
//        }
//        model.addAttribute("mostrarPagadorPostal", entitat.isAmbEntregaCie());
        return "notificacioMassivaForm";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String post(HttpServletRequest request, @Valid NotificacioMassivaCommand notificacioMassivaCommand, BindingResult bindingResult, Model model) throws IOException {

        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. ");
        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        if (bindingResult.hasErrors()) {
            log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Errors de validació formulari. ");
            model.addAttribute("errors", bindingResult.getAllErrors());
            for (ObjectError error: bindingResult.getAllErrors()) {
                log.debug("[NOT-CONTROLLER] POST notificació massiu desde interfície web. Error formulari: " + error.toString());
            }
            model.addAttribute("emailSize", notificacioMassivaCommand.getEmailDefaultSize());
            MultipartFile csvMultipartFile = notificacioMassivaCommand.getFicheroCsv();
            if (csvMultipartFile != null && !csvMultipartFile.isEmpty()) {
                String contingutBase64 = Base64.encodeBase64String(csvMultipartFile.getBytes());
                String csvGestdocId = gestioDocumentalService.guardarArxiuTemporal(contingutBase64);
                notificacioMassivaCommand.setFitxerCSVNom(csvMultipartFile.getOriginalFilename());
                notificacioMassivaCommand.setFitxerCSVGestdocId(csvGestdocId);
            }
            MultipartFile zipMultipartFile = notificacioMassivaCommand.getFicheroZip();
            if (zipMultipartFile != null && !zipMultipartFile.isEmpty()) {
                String contingutBase64 = Base64.encodeBase64String(zipMultipartFile.getBytes());
                String csvGestdocId = gestioDocumentalService.guardarArxiuTemporal(contingutBase64);
                notificacioMassivaCommand.setFitxerZIPNom(zipMultipartFile.getOriginalFilename());
                notificacioMassivaCommand.setFitxerZIPGestdocId(csvGestdocId);
            }
            return getNotificacioMassivaForm(entitat, request, model);
        }

        try {
            log.debug("[NOT-CONTROLLER] POST notificació massiu desde interfície web. Processant dades del formulari. ");
            notificacioMassivaService.create(entitat.getId(), usuariActual.getCodi(),notificacioMassivaCommand.asDto(gestioDocumentalService));
        } catch (Exception ex) {
            log.error("[NOT-CONTROLLER] POST notificació massiu desde interfície web. Excepció al processar les dades del formulari", ex);
            log.error(ExceptionUtils.getFullStackTrace(ex));
            if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, MaxLinesExceededException.class)) {
                MissatgesHelper.error(request, getMessage(request, "notificacio.massiva.csv.error"));
            } else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, InvalidCSVFileNotificacioMassivaException.class)) {
                MissatgesHelper.error(request, getMessage(request, "notificacio.massiva.csv.error.format"));
            } else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, NotificacioMassivaException.class)) {
                NotificacioMassivaException notificacioMassivaException = (NotificacioMassivaException) ExceptionHelper.findThrowableInstance(ex, NotificacioMassivaException.class);
                MissatgesHelper.error(request,
                        getMessage(request, "notificacio.massiva.error.fila", new Object[] {notificacioMassivaException.getFila(), notificacioMassivaException.getColumna()}) +
                                "<br/>" + notificacioMassivaException.getMessage() +
                                (notificacioMassivaException.getCause().getMessage() != null ? "<br/>" + notificacioMassivaException.getCause().getMessage() : "") +
                                "<button class=\"btn btn-default btn-xs pull-right\" data-toggle=\"collapse\" data-target=\"#collapseError\" aria-expanded=\"false\" aria-controls=\"collapseError\">\n" +
                                "\t\t\t\t<span class=\"fa fa-bars\"></span>\n" +
                                "\t\t\t</button>\n" +
                                "\t\t\t<div id=\"collapseError\" class=\"collapse\">\n" +
                                "\t\t\t\t<br/>\n" +
                                "\t\t\t\t<textarea rows=\"10\" style=\"width:100%\">" + ExceptionUtils.getStackTrace(notificacioMassivaException) +"</textarea>\n" +
                                "\t\t\t</div>");
            } else {
                MissatgesHelper.error(request, getMessage(request, "notificacio.massiva.error") + "<br/>" + ex.getMessage());
            }
            return getNotificacioMassivaForm(entitat, request, model);
        }
        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Formulari processat satisfactoriament. ");
        return "redirect:/notificacio/massiva";
    }

    @RequestMapping(value = "/getModelDadesCarregaMassiuCSV", method = RequestMethod.GET)
    @ResponseBody
    public void getModelDadesCarregaMassiuCSV(HttpServletResponse response) throws IOException {

        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        try {
            writeFileToResponse("modelo_datos_carga_masiva.csv", notificacioMassivaService.getModelDadesCarregaMassiuCSV(), response);
        } catch (Exception ex) {
            log.debug("Error al obtenir la plantilla de el model de dades CSV de càrrega massiva", ex);
        }
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
    }

    private NotificacioMassivaFiltreCommand getFiltreCommand(HttpServletRequest request) {

        NotificacioMassivaFiltreCommand notificacioMassivaFiltreCommand = (NotificacioMassivaFiltreCommand) request.getSession().getAttribute(TABLE_FILTRE);
        if (notificacioMassivaFiltreCommand == null) {
            notificacioMassivaFiltreCommand = new NotificacioMassivaFiltreCommand();
            RequestSessionHelper.actualitzarObjecteSessio(request, TABLE_FILTRE, notificacioMassivaFiltreCommand);
        }
        return notificacioMassivaFiltreCommand;
    }

    @Override
    protected List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        String organGestorCodi = null;
        if (RolHelper.isUsuariActualUsuariAdministradorOrgan(request) && entitatActual != null) {
            OrganGestorDto organGestorActual = getOrganGestorActual(request);
            organGestorCodi = organGestorActual.getCodi();

        }
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        NotificacioFiltreDto filtre = notificacioListHelper.getFiltreCommand(request, TABLE_FILTRE).asDto();
        assert entitatActual != null;
        return notificacioService.findIdsAmbFiltre(entitatActual.getId(),
                RolEnumDto.valueOf(RolHelper.getRolActual(request)), organGestorCodi, usuariActual.getCodi(), filtre);
    }
}