package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.PermisosUsuarisFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.NotificacioBackHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
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

    private static final  String PERMISOS_USUARIS_FILTRE = "permisos_usuaris_filtre";
    private static final  String PERMISOS_USUARIS = "permisos_usuaris";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "PermisosController.session.seleccio";
    @Autowired
    private UsuariService usuariService;


    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        getEntitatActualComprovantPermisos(request);
        getOrganGestorActual(request);
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

        getEntitatActualComprovantPermisos(request);
        RequestSessionHelper.actualitzarObjecteSessio(request, PERMISOS_USUARIS_FILTRE, command);
        model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
        model.addAttribute("permisosUsuarisFiltreCommand", command);
        return "permisosUsuari";
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

        var notificacions = new PaginaDto<UsuariDto>();
        var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());

        try {
            var entitatActual = getEntitatActualComprovantPermisos(request);
            var filtre = getFiltreCommand(request).asDto();
            var organGestorCodi = filtre.getOrganGestor();
            if (isAdminOrgan && entitatActual != null && Strings.isNullOrEmpty(organGestorCodi)) {
                var organGestorActual = getOrganGestorActual(request);
                organGestorCodi = organGestorActual.getCodi();
            }
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
            var organAdmin = getOrganGestorActual(request);
            return usuariService.getPermisosUsuari(entitat, usuariCodi, organAdmin);
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
