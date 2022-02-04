package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.Arbre;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.organisme.OrganDetall;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganOrganigrama;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.war.command.OrganGestorCommand;
import es.caib.notib.war.helper.EnumHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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

        try {
            EntitatDto entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
            model.addAttribute("organGestorFiltreCommand", controller.getFiltreCommand(request));
            model.addAttribute("organGestorEstats", EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class, "es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum."));
            model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
            model.addAttribute("setOficina", !entitat.isOficinaEntitat());
            model.addAttribute("oficinesEntitat", organService.getOficinesSIR(entitat.getId(), entitat.getDir3Codi(), true));
            Arbre<OrganGestorDto> arbre = organService.generarArbreOrgans(entitat.getDir3Codi());
            model.addAttribute("organs", organService.getOrgansAsList());
            model.addAttribute("arbreOrgans", arbre);
            omplirModel(model, entitat.getId());
        } catch (Exception ex) {
            System.out.println(ex); // TODO FALTA PASSAR MISSATGE AL FRONT
        }
        return "organGestorArbre";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/organgestor/{codiSia}")
    @ResponseBody
    public OrganOrganigrama getOrgan(HttpServletRequest request, @PathVariable("codiSia") String codiSia, Model model) {

        try {
            EntitatDto entitat = entitatService.findById(controller.getEntitatActualComprovantPermisos(request).getId());
            OrganGestorDto o = organService.findByCodi(entitat.getId(), codiSia);
            if (o == null) {
                return null;
            }
            OrganOrganigrama organ = new OrganOrganigrama();
            organ.setOrgan(OrganDetall.builder().nom(o.getNom()).codi(o.getCodi()).oficinaEntitat(entitat.getOficina())
                    .estat(o.getEstat().name()).cie(o.isEntregaCieActiva()).build());
            organ.setPermisos(organService.permisFind(entitat.getId(), o.getId()));
            omplirModel(model, entitat.getId());
            return organ;
        } catch (Exception ex) {
            System.out.println(ex); // TODO FALTA PASSAR MISSATGE AL FRONT
            throw ex;
        }
    }

    private void omplirModel(Model model, Long entitatId) {

        OrganGestorCommand command = new OrganGestorCommand();
        command.setEntitatId(entitatId);
        model.addAttribute("organGestorCommand", command);
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
