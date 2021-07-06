package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.MaxLinesExceededException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.NotificacioMassivaService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.command.NotificacioMassiuCommand;
import es.caib.notib.war.command.NotificacioMassivaFiltreCommand;
import es.caib.notib.war.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controlador per a la consulta i gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/notificacio/massiva")
public class NotificacioMassivaController extends BaseUserController {

    private final static String TABLE_FILTRE = "not_massiva_filtre";
    private final static String TABLE_NOTIFICACIONS_FILTRE = "not_massiva_nots_filtre";

    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private NotificacioMassivaService notificacioMassivaService;
    @Autowired
    private PagadorPostalService pagadorPostalService;
    @Autowired
    private NotificacioListHelper notificacioListHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(
            HttpServletRequest request,
            Model model) {

        NotificacioMassivaFiltreCommand filtre = getFiltreCommand(request);
        model.addAttribute("notificacioMassivaFiltreCommand", filtre);
        model.addAttribute("notificacioMassivaEstats",
                EnumHelper.getOptionsForEnum(NotificacioMassivaEstatDto.class,
                        "es.caib.notib.core.api.dto.notificacio.NotificacioMassivaEstatDto."));

        return "notificacioMassivaList";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(
            HttpServletRequest request,
            NotificacioMassivaFiltreCommand command,
            Model model) throws ParseException {
        RequestSessionHelper.actualitzarObjecteSessio(
                request,
                TABLE_FILTRE,
                command);

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
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "notificacio.controller.entitat.cap.assignada"));
        }

        return DatatablesHelper.getDatatableResponse(request, notificacions);
    }

    @RequestMapping(value = "/{id}/resum", method = RequestMethod.GET)
    public String summary(
            HttpServletRequest request,
            Model model,
            @PathVariable Long id) {
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        NotificacioMassivaInfoDto info = notificacioMassivaService.getNotificacioMassivaInfo(entitatActual.getId(), id);

        model.addAttribute("info", info);
        return "notificacioMassivaInfo";
    }

    @RequestMapping(value = "/{id}/csv/download", method = RequestMethod.GET)
    @ResponseBody
    public void csvDownload(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long id) throws IOException {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getCSVFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }
    @RequestMapping(value = "/{id}/zip/download", method = RequestMethod.GET)
    public void zipDownload(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long id) throws IOException {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getZipFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }

    @RequestMapping(value = "/{id}/resum/download", method = RequestMethod.GET)
    public void summaryDownload(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long id) throws IOException {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getResumFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }

    @RequestMapping(value = "/{id}/errors/download", method = RequestMethod.GET)
    public void errorsDownload(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long id) throws IOException {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        FitxerDto file = notificacioMassivaService.getErrorsFile(entitatActual.getId(), id);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(file.getNom(), file.getContingut(), response);
    }

    @RequestMapping(value = "/{id}/posposar", method = RequestMethod.GET)
    public String posposar(
            HttpServletRequest request,
            @PathVariable Long id) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        try {
            notificacioMassivaService.posposar(entitatActual.getId(), id);
        } catch (Exception e) {
            log.error("Hi ha hagut un error posposant la notificació massiva", e);
            return getModalControllerReturnValueError(
                    request,
                    "redirect:..",
                    "notificacio.massiva.controller.posposar.ko",
                    new Object[]{e.getMessage()});
        }
        return getModalControllerReturnValueSuccess(
                request,
                "redirect:..",
                "notificacio.massiva.controller.posposar.ok");
    }

    @RequestMapping(value = "/{id}/reactivar", method = RequestMethod.GET)
    public String reactivar(
            HttpServletRequest request,
            @PathVariable Long id) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        try {
            notificacioMassivaService.reactivar(entitatActual.getId(), id);
        } catch (Exception e) {
            log.error("Hi ha hagut un error reactivant la notificació massiva", e);
            return getModalControllerReturnValueError(
                    request,
                    "redirect:..",
                    "notificacio.massiva.controller.reactivar.ko",
                    new Object[]{e.getMessage()});
        }
        return getModalControllerReturnValueSuccess(
                request,
                "redirect:..",
                "notificacio.massiva.controller.reactivar.ok");
    }

    @RequestMapping(value = "/{id}/remeses", method = RequestMethod.GET)
    public String consultarRemeses(
            HttpServletRequest request,
            Model model,
            @PathVariable Long id) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        NotificacioFiltreCommand notificacioFiltreCommand = notificacioListHelper.getFiltreCommand(request,
                TABLE_NOTIFICACIONS_FILTRE);

        model.addAttribute(notificacioFiltreCommand);
        notificacioListHelper.fillModel(entitatActual, organGestorActual, request, model);
        model.addAttribute("notificacioMassivaId", id);
        NotificacioMassivaDataDto notMassivaData = notificacioMassivaService.findById(entitatActual.getId(), id);
        model.addAttribute("subtitle", getMessage(
                request,
                "notificacio.massiva.notificacions.list.titol.sub",
                new String[] {
                        new SimpleDateFormat("dd/MM/yyyy").format(notMassivaData.getCreatedDate()),
                        notMassivaData.getCsvFilename(),
                        notMassivaData.getCreatedBy().getCodi()}));

        return "notificacioMassivaNotificacionsList";
    }
    @RequestMapping(value = "/{id}/remeses", method = RequestMethod.POST)
    public String consultarRemesesUpdateFiltre(
            HttpServletRequest request,
            NotificacioFiltreCommand command,
            Model model) {
        RequestSessionHelper.actualitzarObjecteSessio(
                request,
                TABLE_NOTIFICACIONS_FILTRE,
                command);
        return "notificacioMassivaNotificacionsList";
    }
    @RequestMapping(value = "/{id}/remeses/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesHelper.DatatablesResponse consultarRemesesDatatable(
            HttpServletRequest request, @PathVariable Long id) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        NotificacioFiltreDto filtre = notificacioListHelper.getFiltreCommand(request, TABLE_NOTIFICACIONS_FILTRE).asDto();
        PaginaDto<NotificacioTableItemDto> notificacions = new PaginaDto<>();
        try {
            notificacions = notificacioMassivaService.findNotificacions(
                    entitatActual.getId(),
                    id,
                    filtre,
                    DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "notificacio.controller.entitat.cap.assignada"));
        }

        return DatatablesHelper.getDatatableResponse(request, notificacions);
    }

    @RequestMapping(value = "/new")
    public String get(
            HttpServletRequest request,
            Model model) {
        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
        NotificacioMassiuCommand notificacioMassiuCommand = new NotificacioMassiuCommand();
        notificacioMassiuCommand.setCaducitat(CaducitatHelper.sumarDiesLaborals(10));

        model.addAttribute("notificacioMassiuCommand", notificacioMassiuCommand);
        model.addAttribute("emailSize", notificacioMassiuCommand.getEmailDefaultSize());

        return getNotificacioMassivaForm(entitat, request, model);
    }

    private String getNotificacioMassivaForm(EntitatDto entitat,
                                             HttpServletRequest request,
                                             Model model) {
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        if (organGestorActual != null) {
            model.addAttribute("pagadorsPostal", pagadorPostalService.findByEntitatAndOrganGestor(entitat, organGestorActual));
        } else {
            model.addAttribute("pagadorsPostal", pagadorPostalService.findByEntitat(entitat.getId()));
        }
        model.addAttribute("mostrarPagadorPostal", entitat.isAmbEntregaCie());
        return "notificacioMassivaForm";
    }
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String post(
            HttpServletRequest request,
            @Valid NotificacioMassiuCommand notificacioMassiuCommand,
            BindingResult bindingResult,
            Model model) throws IOException {
        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. ");
        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        
        if (bindingResult.hasErrors()) {
            log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Errors de validació formulari. ");
            
            model.addAttribute("errors", bindingResult.getAllErrors());
 
            for (ObjectError error: bindingResult.getAllErrors()) {
                log.debug("[NOT-CONTROLLER] POST notificació massiu desde interfície web. Error formulari: " + error.toString());
            }

            model.addAttribute("emailSize", notificacioMassiuCommand.getEmailDefaultSize());
            return getNotificacioMassivaForm(entitat, request, model);
        }

        try {
            log.debug("[NOT-CONTROLLER] POST notificació massiu desde interfície web. Processant dades del formulari. ");

            notificacioMassivaService.create(entitat.getId(), usuariActual.getCodi(), notificacioMassiuCommand.asDto());
      
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("[NOT-CONTROLLER] POST notificació massiu desde interfície web. Excepció al processar les dades del formulari", ex);
            log.error(ExceptionUtils.getFullStackTrace(ex));
            if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, MaxLinesExceededException.class))
                MissatgesHelper.error(request, getMessage(request, "notificacio.massiva.csv.error"));
            else 
            	MissatgesHelper.error(request, ex.getMessage());

            return getNotificacioMassivaForm(entitat, request, model);
        }
        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Formulari processat satisfactoriament. ");

        return "redirect:/notificacio/massiva";
    }

    @RequestMapping(value = "/getModelDadesCarregaMassiuCSV", method = RequestMethod.GET)
    @ResponseBody
    public void getModelDadesCarregaMassiuCSV(
            HttpServletResponse response) throws IOException {

        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        try {
            writeFileToResponse(
                    "modelo_datos_carga_masiva.csv",
                    notificacioMassivaService.getModelDadesCarregaMassiuCSV(),
                    response);
        } catch (Exception ex) {
            log.debug("Error al obtenir la plantilla de el model de dades CSV de càrrega massiva", ex);
        }
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(
                Date.class,
                new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
    }

    private NotificacioMassivaFiltreCommand getFiltreCommand(
            HttpServletRequest request) {
        NotificacioMassivaFiltreCommand notificacioMassivaFiltreCommand = (NotificacioMassivaFiltreCommand) request.getSession()
                .getAttribute(TABLE_FILTRE);

        if (notificacioMassivaFiltreCommand == null) {
            notificacioMassivaFiltreCommand = new NotificacioMassivaFiltreCommand();
            RequestSessionHelper.actualitzarObjecteSessio(
                    request,
                    TABLE_FILTRE,
                    notificacioMassivaFiltreCommand);
        }
        return notificacioMassivaFiltreCommand;
    }

}