package es.caib.notib.back.controller;

import es.caib.notib.back.command.OrganGestorCommand;
import es.caib.notib.back.command.OrganGestorFiltreCommand;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.Arbre;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.PermisosService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private AplicacioService aplicacioService;
    @Autowired
    private PermisosService permisosService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        try {
            Long ti = System.currentTimeMillis();

            EntitatDto entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
            OrganGestorFiltreCommand filtres = controller.getFiltreCommand(request);
            model.addAttribute("organGestorFiltreCommand", filtres);
            model.addAttribute("organGestorEstats", EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class, "es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum."));
            boolean isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
            OrganGestorDto organ = getOrganGestorActual(request);

//            Long tf = System.currentTimeMillis();
//            System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T1: " + (tf - ti) + "ms");
//            ti = tf;

            Arbre<OrganGestorDto> arbre = organService.generarArbreOrgans(entitat, filtres.asDto(), isAdminOrgan, organ);
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
            String msg = getMessage(request, "organgestor.list.datatable.error", new Object[] {
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

        EntitatDto entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());

        OrganGestorDto organ = OrganGestorCommand.asDto(command);
        String msg = "";
        if (bindingResult.hasErrors()) {
            omplirModel(model, entitat, organ);
            msg = "organgestor.arbre.error.guardar";
            return getAjaxControllerReturnValueError(request,"redirect:./", msg);
        }

        organ.setLlibreNom(command.getLlibre() != null ? organService.getLlibreOrganisme(entitat.getId(), organ.getCodi()).getNomLlarg() : null);
        if (command.getOficina() != null) {
            List<OficinaDto> oficines = organService.getOficinesSIR(entitat.getId(), organ.getCodi(),true);
            String oficinaNom = null;
            for (OficinaDto oficina : oficines) {
                if (oficina.getCodi().equals(organ.getCodi())) {
                    oficinaNom = oficina.getNom();
                }
            }
            organ.setOficinaNom(oficinaNom);
        }

        if (command.getId() != null) {
            msg = "organgestor.controller.update.nom.ok";
            organService.update(organ);
        } else {
//            msg = "organgestor.controller.creat.ok";
//            organService.create(organ);
        }
        return getAjaxControllerReturnValueSuccess(request, "redirect:./", msg);
    }

//    @ResponseBody
//    @RequestMapping(value = "/{organGestorCodi}/delete", method = RequestMethod.GET)
//    public Resposta delete(HttpServletRequest request, @PathVariable String organGestorCodi) {
//
//        Locale locale = new Locale(SessioHelper.getIdioma(aplicacioService));
//        try {
//            EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//            OrganGestorDto organ = organService.findByCodi(entitat.getId(), organGestorCodi);
//            if (organ == null) {
//                return Resposta.builder().msg(MessageHelper.getInstance().getMessage("organgestor.controller.esborrat.ko", null, locale)).error(true).build();
//            }
//            if (organService.organGestorEnUs(organ.getId())) {
//                return Resposta.builder().msg(MessageHelper.getInstance().getMessage("organgestor.controller.esborrat.us", null, locale)).error(true).build();
//            }
//            organService.delete(entitat.getId(), organ.getId());
//            return Resposta.builder().msg(MessageHelper.getInstance().getMessage("organgestor.controller.esborrat.ok", null, locale)).build();
//        } catch (Exception e) {
//            logger.error(String.format("Excepció intentant esborrar l'òrgan gestor %s:", organGestorCodi), e);
//            return Resposta.builder().msg(MessageHelper.getInstance().getMessage("organgestor.controller.esborrat.ko ", null, locale)).error(true).build();
//        }
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/organgestor/{codi}")
    public String getOrgan(HttpServletRequest request, @PathVariable("codi") String codi, Model model) {

        try {
            model.addAttribute("desactivarAvisos", true);
            EntitatDto entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
            boolean isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
            List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findNoCaducatsByEntitatAndOrgan(entitat, codi, isAdminOrgan);
            model.addAttribute("operadorPostalList", operadorPostalList);
            List<IdentificadorTextDto> cieList = cieService.findNoCaducatsByEntitatAndOrgan(entitat, codi, isAdminOrgan);
            model.addAttribute("cieList", cieList);
            OrganGestorDto o = organService.findByCodi(entitat.getId(), codi);
            String usr = SecurityContextHolder.getContext().getAuthentication().getName();
            //o = o == null ? organService.getOrganNou(codiSia) : o;
            if (o == null || (isAdminOrgan && !permisosService.hasUsrPermisOrgan(entitat.getId(), usr, codi, PermisEnum.ADMIN))) {
                throw new NotFoundException(codi, OrganGestorDto.class);
            }
            o.setEstatTraduccio(MessageHelper.getInstance().getMessage("es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum." + o.getEstat()));
            omplirModel(model, entitat, o);
        } catch (Exception ex) {
            String msg = getMessage(request, "organgestor.detall.error", new Object[] {
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

        Long ti = System.currentTimeMillis();

        OrganGestorCommand command = organ != null ? OrganGestorCommand.asCommand(organ) : new OrganGestorCommand();
        command.setEntitatId(entitat.getId());

//        Long tf = System.currentTimeMillis();
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
            List<OficinaDto> oficinesEntitat = organService.getOficinesSIR(entitat.getId(), entitat.getDir3Codi(),true);    // <-- TODO: El problema està aquí
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

        List<OficinaDto> oficines = organService.getOficinesSIR(entitat.getId(), organ.getCodi(),false);
        model.addAttribute("oficines", oficines);

//        tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.5: " + (tf - ti) + "ms");
//        ti = tf;

        for(OficinaDto oficina: oficines) {
            if (oficina.getCodi() != null && oficina.getCodi().equals(entitat.getOficina())) {
                command.setOficinaNom(oficina.getCodi() + " - " + oficina.getNom());
                break;
            }
        }

//        tf = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>> ARBRE >>> T2.6: " + (tf - ti) + "ms");
    }

    private static final Logger logger = LoggerFactory.getLogger(OrganGestorArbreController.class);
}
