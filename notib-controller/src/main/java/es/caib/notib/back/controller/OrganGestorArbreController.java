package es.caib.notib.back.controller;

import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.back.command.OrganGestorCommand;
import es.caib.notib.back.command.OrganGestorFiltreCommand;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.service.PermisosService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    private final static String ORGANS_FILTRE = "organs_filtre";

    @Autowired
    private EntitatService entitatService;
    @Autowired
    private OrganGestorController controller;
    @Autowired
    private OrganGestorService organService;
    @Autowired
    private OperadorPostalService operadorPostalService;
    @Autowired
    private PagadorCieService cieService;
    @Autowired
    private PermisosService permisosService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        try {
            var ti = System.currentTimeMillis();
            var entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
            var filtres = controller.getFiltreCommand(request);
            model.addAttribute("organGestorFiltreCommand", filtres);
            model.addAttribute("organGestorEstats", EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class, "es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum."));
            var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
            var organ = getOrganGestorActual(request);
//            var tf = System.currentTimeMillis();
//            System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T1: " + (tf - ti) + "ms");
//            ti = tf;
            var arbre = organService.generarArbreOrgans(entitat, filtres.asDto(), isAdminOrgan, organ);
            model.addAttribute("arbreOrgans", arbre);
            model.addAttribute("filtresEmpty", filtres.isEmpty());
            model.addAttribute("isFiltre", "true".equals(filtres.getIsFiltre()));
//            tf = System.currentTimeMillis();
//            System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2: " + (tf - ti) + "ms");
//            ti = tf;
            omplirModel(model, entitat, null);
//            tf = System.currentTimeMillis();
//            System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T3: " + (tf - ti) + "ms");
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

    @RequestMapping(method = RequestMethod.POST)
    public String post(HttpServletRequest request, OrganGestorFiltreCommand command, Model model) {

        RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, command);
        return "redirect:organgestorArbre";
    }

    @RequestMapping(method = RequestMethod.POST, value="/guardar")
    public String guardarOrgan(HttpServletRequest request, @Valid OrganGestorCommand command, BindingResult bindingResult, Model model) {

        var entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
        var organ = OrganGestorCommand.asDto(command);
        var msg = "";
        if (bindingResult.hasErrors()) {
            omplirModel(model, entitat, organ);
            msg = "organgestor.arbre.error.guardar";
            return getAjaxControllerReturnValueError(request,"redirect:./", msg);
        }
        organ.setLlibreNom(command.getLlibre() != null ? organService.getLlibreOrganisme(entitat.getId(), organ.getCodi()).getNomLlarg() : null);
        if (command.getOficina() != null) {
            var oficines = organService.getOficinesSIR(entitat.getId(), organ.getCodi(),true);
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
            organService.update(organ);
        }

        return getAjaxControllerReturnValueSuccess(request, "redirect:./", msg);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/organgestor/{codiSia}")
    public String getOrgan(HttpServletRequest request, @PathVariable("codi") String codi, Model model) {

        try {
            model.addAttribute("desactivarAvisos", true);
            var entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
            var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
            var operadorPostalList = operadorPostalService.findNoCaducatsByEntitatAndOrgan(entitat, codi, isAdminOrgan);
            model.addAttribute("operadorPostalList", operadorPostalList);
            var cieList = cieService.findNoCaducatsByEntitatAndOrgan(entitat, codi, isAdminOrgan);
            model.addAttribute("cieList", cieList);
            var o = organService.findByCodi(entitat.getId(), codi);
            var usr = SecurityContextHolder.getContext().getAuthentication().getName();
            //o = o == null ? organService.getOrganNou(codiSia) : o;
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

//        var ti = System.currentTimeMillis();
        var command = organ != null ? OrganGestorCommand.asCommand(organ) : new OrganGestorCommand();
        command.setEntitatId(entitat.getId());
//        var tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.1: " + (tf - ti) + "ms");
//        ti = tf;
        model.addAttribute("organsEntitat", organService.getOrgansAsList());
        model.addAttribute("id", organ != null && organ.getId() != null ? organ.getId() : 0);
        model.addAttribute("organGestorCommand", command);
        model.addAttribute("entitat", entitat);
        model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
        model.addAttribute("setOficina", !entitat.isOficinaEntitat());
        model.addAttribute("isModificacio", organ != null && organ.getId() != null);
//        tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.2: " + (tf - ti) + "ms");
//        ti = tf;
        if (!entitat.isOficinaEntitat()) {
            var oficinesEntitat = organService.getOficinesSIR(entitat.getId(), entitat.getDir3Codi(),true);    // <-- TODO: El problema està aquí
            model.addAttribute("oficinesEntitat", oficinesEntitat);
        }
        if (organ == null) {
            return;
        }
//        tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.3: " + (tf - ti) + "ms");
//        ti = tf;
        List<LlibreDto> llibres = new ArrayList<>();
        llibres.add(organService.getLlibreOrganisme(entitat.getId(), organ.getCodi()));
        model.addAttribute("llibres", llibres);
//        tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.4: " + (tf - ti) + "ms");
//        ti = tf;
        var oficines = organService.getOficinesSIR(entitat.getId(), organ.getCodi(),false);
        model.addAttribute("oficines", oficines);
//        tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.5: " + (tf - ti) + "ms");
//        ti = tf;
        for(var oficina: oficines) {
            if (oficina.getCodi() != null && oficina.getCodi().equals(entitat.getOficina())) {
                command.setOficinaNom(oficina.getCodi() + " - " + oficina.getNom());
                break;
            }
        }
//        tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.6: " + (tf - ti) + "ms");
    }
}
