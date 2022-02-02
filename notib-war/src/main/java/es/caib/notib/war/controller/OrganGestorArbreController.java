package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.Arbre;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.service.OrganGestorServiceImpl;
import es.caib.notib.war.command.OrganGestorFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controlador per el mantinemnt organs gestors format arbre.
 *
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/organgestorArbre")
public class OrganGestorArbreController extends BaseUserController {

    @Autowired
    private EntitatService entitatService;
    @Autowired
    private OrganGestorController controller;
    @Autowired
    private OrganGestorService organService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        EntitatDto entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
        model.addAttribute("organGestorFiltreCommand", controller.getFiltreCommand(request));
        model.addAttribute("organGestorEstats", EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class, "es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum."));
        model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
        model.addAttribute("setOficina", !entitat.isOficinaEntitat());
        model.addAttribute("oficinesEntitat", organService.getOficinesSIR(entitat.getId(), entitat.getDir3Codi(), true));
        List<OrganGestorDto> organs = organService.findByEntitat(entitat.getId());
        model.addAttribute("organs", organService.getOrgansAsList());
        Arbre<OrganGestorDto> arbre = organService.generarArbreOrgans(entitat.getDir3Codi());
        model.addAttribute("arbreOrgans", arbre);
        return "organGestorArbre";
    }

//    OrganGestorFiltreCommand organGestorFiltreCommand = getFiltreCommand(request);
//    PaginaDto<OrganGestorDto> organs = new PaginaDto<OrganGestorDto>();
//
//		try {
//        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//
//        OrganGestorDto organGestorActual = getOrganGestorActual(request);
//        String organActualCodiDir3=null;
//        if (organGestorActual!=null) organActualCodiDir3 = organGestorActual.getCodi();
//
//        organs = organGestorService.findAmbFiltrePaginat(
//                entitat.getId(),
//                organActualCodiDir3,
//                organGestorFiltreCommand.asDto(),
//                DatatablesHelper.getPaginacioDtoFromRequest(request));
//    }catch(SecurityException e) {
//        MissatgesHelper.error(
//                request,
//                getMessage(
//                        request,
//                        "notificacio.controller.entitat.cap.assignada"));
//    }
//		return DatatablesHelper.getDatatableResponse(
//    request,
//    organs,
//            "codi");
}
