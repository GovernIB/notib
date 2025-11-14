package es.caib.notib.back.controller;

import es.caib.notib.back.command.AccioMassivaFiltreCommand;
import es.caib.notib.back.command.PermisosUsuarisFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.NotificacioBackHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDetall;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElementEstat;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuari;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.UsuariService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/permisos")
public class PermisosController extends BaseUserController {

    @Autowired
    private NotificacioBackHelper notificacioListHelper;

    private static final  String PERMISOS_USUARIS_FILTRE = "permisos_usuaris_filtre";
    private static final  String PERMISOS_USUARIS = "permisos_usuaris";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "PermisosController.session.seleccio";
    @Autowired
    private UsuariService usuariService;
    @Autowired
    private PermisosService permisosService;

    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
        model.addAttribute("mantenirPaginacio", mantenirPaginacio != null && mantenirPaginacio);
        model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
        model.addAttribute("tipusPermisos", EnumHelper.getOptionsForEnum(AccioMassivaTipus.class, "es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus."));
        model.addAttribute("elementEstats", EnumHelper.getOptionsForEnum(AccioMassivaElementEstat.class, "es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElementEstat."));
        model.addAttribute(getFiltreCommand(request));
        return "permisosUsuari";
    }

    @PostMapping
    public String post(HttpServletRequest request, PermisosUsuarisFiltreCommand command, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);

//        var callbackId = (Long)RequestSessionHelper.obtenirObjecteSessio(request, ACCIO_MASSIVA_ID);
//        if (callbackId == null || !callbackId.equals(command.getId())) {
//            RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
//            RequestSessionHelper.actualitzarObjecteSessio(request, ACCIO_MASSIVA_ID, command.getId());
//        }
        RequestSessionHelper.actualitzarObjecteSessio(request, PERMISOS_USUARIS_FILTRE, command);
        model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
//        model.addAttribute("tipusAccions", EnumHelper.getOptionsForEnum(AccioMassivaTipus.class, "es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus."));
//        model.addAttribute("elementEstats", EnumHelper.getOptionsForEnum(AccioMassivaElementEstat.class, "es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElementEstat."));
        model.addAttribute("permisosUsuarisFiltreCommand", command);
        return "permisosUsuari";
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

        var notificacions = new PaginaDto<UsuariDto>();
//        var filtreCommand = getFiltreCommand(request, PERMISOS_USUARIS);
//        if (!filtreCommand.getErrors().isEmpty()) {
//            return DatatablesHelper.getDatatableResponse(request, notificacions, "id", SESSION_ATTRIBUTE_SELECCIO);
//        }
//        var filtre = filtreCommand.asDto();
//        var isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
//        var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());

        try {
            var entitatActual = getEntitatActualComprovantPermisos(request);
//            if (isUsuariEntitat && filtre != null) {
//                filtre.setEntitatId(entitatActual.getId());
//            }
//            var organGestorCodi = filtre.getOrganGestor();
//            if (isAdminOrgan && entitatActual != null && Strings.isNullOrEmpty(organGestorCodi)) {
//                OrganGestorDto organGestorActual = getOrganGestorActual(request);
//                organGestorCodi = organGestorActual.getCodi();
//            }
//            filtre.setDeleted(false);
            var filtre = getFiltreCommand(request).asDto();
            notificacions = usuariService.findAmbFiltre(filtre, DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(request, e.getMessage());
        }
        return DatatablesHelper.getDatatableResponse(request, notificacions, "codi", SESSION_ATTRIBUTE_SELECCIO);
    }

    @GetMapping(value = "/usuari/{usuariCodi}")
    @ResponseBody
    public PermisosUsuari get(HttpServletRequest request, Model model, @PathVariable String usuariCodi) {

        try {
            var entitat = getEntitatActualComprovantPermisos(request);
            return usuariService.getPermisosUsuari(entitat, usuariCodi);
//        model.addAttribute("tipusAccions", EnumHelper.getOptionsForEnum(AccioMassivaTipus.class, "es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus."));
//        model.addAttribute("elementEstats", EnumHelper.getOptionsForEnum(AccioMassivaElementEstat.class, "es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElementEstat."));
        } catch (Exception ex) {
            log.error("Error obtinguent els permisos assigants a l'usuari " + usuariCodi);
            return new PermisosUsuari();
        }
    }

    private PermisosUsuarisFiltreCommand getFiltreCommand(HttpServletRequest request) {

        var filtreCommand = (PermisosUsuarisFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, PERMISOS_USUARIS_FILTRE);
        if (filtreCommand != null) {
            return filtreCommand;
        }
        filtreCommand = new PermisosUsuarisFiltreCommand();
        RequestSessionHelper.actualitzarObjecteSessio(request, PERMISOS_USUARIS_FILTRE, filtreCommand);
        return filtreCommand;
    }
}
