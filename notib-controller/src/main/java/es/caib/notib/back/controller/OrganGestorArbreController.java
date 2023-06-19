package es.caib.notib.back.controller;

import es.caib.notib.back.command.OrganGestorCommand;
import es.caib.notib.back.command.OrganGestorFiltreCommand;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.PermisosService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per el mantinemnt organs gestors format arbre.
 *
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Slf4j
@Controller
@RequestMapping("/organgestorArbre")
public class OrganGestorArbreController extends BaseUserController {

    @Autowired
    private EntitatService entitatService;
    @Autowired
    private OrganGestorService organGestorService;
    @Autowired
    private OperadorPostalService operadorPostalService;
    @Autowired
    private PagadorCieService pagadorCieService;
    @Autowired
    private PermisosService permisosService;

    private static final String ORGANS_FILTRE = "organs_filtre";


    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        try {
            var entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
            var filtres = OrganGestorController.getFiltreCommand(request);
            model.addAttribute("organGestorFiltreCommand", filtres);
            model.addAttribute("organGestorEstats", EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class, "es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum."));
            var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
            var organ = getOrganGestorActual(request);
            var arbre = organGestorService.generarArbreOrgans(entitat, filtres.asDto(), isAdminOrgan, organ);
            model.addAttribute("arbreOrgans", arbre);
            model.addAttribute("filtresEmpty", filtres.isEmpty());
            model.addAttribute("isFiltre", "true".equals(filtres.getIsFiltre()));
            omplirModel(model, entitat, null);
        } catch (Exception ex) {
            log.error("Error generant l'arbre d'òrgans", ex);
            var msg = getMessage(request, "organgestor.list.datatable.error", new Object[] {
                    "<button class=\"btn btn-default btn-xs pull-right\" data-toggle=\"collapse\" data-target=\"#collapseError\" aria-expanded=\"false\" aria-controls=\"collapseError\">\n" +
                            "\t\t\t\t<span class=\"fa fa-bars\"></span>\n" +
                            "\t\t\t</button>\n" +
                            "\t\t\t<div id=\"collapseError\" class=\"collapse\">\n" +
                            "\t\t\t\t<br/>\n" +
                            "\t\t\t\t<textarea rows=\"10\" style=\"width:100%\">" + ExceptionUtils.getStackTrace(ex) +"</textarea>\n" +
                            "\t\t\t</div>"});
            MissatgesHelper.error(request, msg);
        }
        return "organGestorArbre";
    }

    @PostMapping
    public String post(HttpServletRequest request, OrganGestorFiltreCommand command, Model model) {

        RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, command);
        return "redirect:organgestorArbre";
    }

    @PostMapping(value="/guardar")
    public String guardarOrgan(HttpServletRequest request, @Valid OrganGestorCommand command, BindingResult bindingResult, Model model) {

        var entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
        var organ = OrganGestorCommand.asDto(command);
        var msg = "";
        if (bindingResult.hasErrors()) {
            omplirModel(model, entitat, organ);
            msg = "organgestor.arbre.error.guardar";
            return getAjaxControllerReturnValueError(request,"redirect:./", msg);
        }
        organ.setLlibreNom(command.getLlibre() != null ? organGestorService.getLlibreOrganisme(entitat.getId(), organ.getCodi()).getNomLlarg() : null);
        if (command.getOficina() != null) {
            var oficines = organGestorService.getOficinesSIR(entitat.getId(), organ.getCodi(),true);
            String oficinaNom = null;
            for (var oficina : oficines) {
                if (oficina.getCodi().equals(organ.getCodi())) {
                    oficinaNom = oficina.getNom();
                }
            }
            organ.setOficinaNom(oficinaNom);
        }

        if (command.getId() != null) {
            msg = "organgestor.controller.update.nom.ok";
            organGestorService.update(organ);
        }
        return getAjaxControllerReturnValueSuccess(request, "redirect:./", msg);
    }

    @GetMapping(value = "/organgestor/{codi}")
    public String getOrgan(HttpServletRequest request, @PathVariable("codi") String codi, Model model) {

        try {
            model.addAttribute("desactivarAvisos", true);
            var entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
            var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
            var operadorPostalList = operadorPostalService.findNoCaducatsByEntitatAndOrgan(entitat, codi, isAdminOrgan);
            model.addAttribute("operadorPostalList", operadorPostalList);
            var cieList = pagadorCieService.findNoCaducatsByEntitatAndOrgan(entitat, codi, isAdminOrgan);
            model.addAttribute("cieList", cieList);
            var o = organGestorService.findByCodi(entitat.getId(), codi);
            var usr = SecurityContextHolder.getContext().getAuthentication().getName();
            if (o == null || (isAdminOrgan && !permisosService.hasUsrPermisOrgan(entitat.getId(), usr, codi, PermisEnum.ADMIN))) {
                throw new NotFoundException(codi, OrganGestorDto.class);
            }
            o.setEstatTraduccio(MessageHelper.getInstance().getMessage("es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum." + o.getEstat()));
            omplirModel(model, entitat, o);
        } catch (Exception ex) {
            var msg = getMessage(request, "organgestor.detall.error", new Object[] {
                    "<button class=\"btn btn-default btn-xs pull-right\" data-toggle=\"collapse\" data-target=\"#collapseError\" aria-expanded=\"false\" aria-controls=\"collapseError\">\n" +
                            "\t\t\t\t<span class=\"fa fa-bars\"></span>\n" +
                            "\t\t\t</button>\n" +
                            "\t\t\t<div id=\"collapseError\" class=\"collapse\">\n" +
                            "\t\t\t\t<br/>\n" +
                            "\t\t\t\t<textarea rows=\"10\" style=\"width:100%\">" + ExceptionUtils.getStackTrace(ex) +"</textarea>\n" +
                            "\t\t\t</div>"});
            MissatgesHelper.error(request, msg);
        }
        return "organGestorArbreDetall";
    }

    private void omplirModel(Model model, EntitatDto entitat, OrganGestorDto organ) {

        var command = organ != null ? OrganGestorCommand.asCommand(organ) : new OrganGestorCommand();
        command.setEntitatId(entitat.getId());
        model.addAttribute("organsEntitat", organGestorService.getOrgansAsList());
        model.addAttribute("id", organ != null && organ.getId() != null ? organ.getId() : 0);
        model.addAttribute("organGestorCommand", command);
        model.addAttribute("entitat", entitat);
        model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
        model.addAttribute("setOficina", !entitat.isOficinaEntitat());
        model.addAttribute("isModificacio", organ != null && organ.getId() != null);
        if (!entitat.isOficinaEntitat()) {
            var oficinesEntitat = organGestorService.getOficinesSIR(entitat.getId(), entitat.getDir3Codi(),true);
            model.addAttribute("oficinesEntitat", oficinesEntitat);
        }
        if (organ == null) {
            return;
        }
        List<LlibreDto> llibres = new ArrayList<>();
        llibres.add(organGestorService.getLlibreOrganisme(entitat.getId(), organ.getCodi()));
        model.addAttribute("llibres", llibres);
        var oficines = organGestorService.getOficinesSIR(entitat.getId(), organ.getCodi(),false);
        model.addAttribute("oficines", oficines);
        for (var oficina: oficines) {
            if (oficina.getCodi() != null && oficina.getCodi().equals(entitat.getOficina())) {
                command.setOficinaNom(oficina.getCodi() + " - " + oficina.getNom());
                break;
            }
        }
    }
}
