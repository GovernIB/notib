package es.caib.notib.api.interna.controller;

import es.caib.notib.api.interna.openapi.interficies.ProcedimentApiRestIntf;
import es.caib.notib.client.domini.Procediment;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.procediment.ProcedimentConsultaFiltre;
import es.caib.notib.logic.intf.service.ProcedimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/procediment/")
public class ProcedimentApiController extends NotificacioApiRestBaseController implements ProcedimentApiRestIntf {

    @Autowired
    private ProcedimentService procedimentService;

    @GetMapping(value = "/entitat/{codiEntitat}")
    public PaginaDto<Procediment> getProcedimentsByEntitat(HttpServletRequest request,
                                                           @PathVariable String codiEntitat,
                                                           @RequestParam(value = "codi", required = false) String codi,
                                                           @RequestParam(value = "nom", required = false) String nom,
                                                           @RequestParam(value = "organGestor", required = false) String organGestor,
                                                           @RequestParam(value = "actiu", required = false) Boolean actiu,
                                                           @RequestParam(value = "comu", required = false) Boolean comu,
                                                           @RequestParam(value = "manual", required = false) Boolean manual,
                                                           @RequestParam(value = "entregaCieActiva", required = false) Boolean entregaCieActiva,
                                                           @RequestParam(value = "requereixPermisDirecte", required = false) Boolean requereixPermisDirecte,
                                                           @RequestParam(value = "pagina", required = false) Integer pagina,
                                                           @RequestParam(value = "mida", required = false) Integer mida) {

            var filtre = ProcedimentConsultaFiltre.builder()
                    .codi(codi)
                    .nom(nom)
                    .organGestor(organGestor)
                    .actiu(actiu)
                    .comu(comu)
                    .manual(manual)
                    .entregaCieActiva(entregaCieActiva)
                    .requireDirectPermission(requereixPermisDirecte)
                    .mida(mida)
                    .pagina(pagina)
                    .build();
        return procedimentService.findByEntitat(codiEntitat, filtre);
    }

    @GetMapping(value = "/entitat/{codiEntitat}/cie/actiu")
    public List<Procediment> getProcedimentsCieByEntitat(HttpServletRequest request,
                                                         @PathVariable String codiEntitat,
                                                         @RequestParam(value = "codi", required = false) String codi,
                                                         @RequestParam(value = "nom", required = false) String nom,
                                                         @RequestParam(value = "organGestor", required = false) String organGestor,
                                                         @RequestParam(value = "actiu", required = false) Boolean actiu,
                                                         @RequestParam(value = "comu", required = false) Boolean comu,
                                                         @RequestParam(value = "manual", required = false) Boolean manual,
                                                         @RequestParam(value = "entregaCieActiva", required = false) Boolean entregaCieActiva,
                                                         @RequestParam(value = "requereixPermisDirecte", required = false) Boolean requereixPermisDirecte,
                                                         @RequestParam(value = "pagina", required = false) Integer pagina,
                                                         @RequestParam(value = "mida", required = false) Integer mida) {

        var filtre = ProcedimentConsultaFiltre.builder()
                            .codi(codi)
                            .nom(nom)
                            .organGestor(organGestor)
                            .actiu(actiu)
                            .comu(comu)
                            .manual(manual)
                            .entregaCieActiva(entregaCieActiva)
                            .requireDirectPermission(requereixPermisDirecte)
                            .mida(mida)
                            .pagina(pagina)
                            .build();
        return procedimentService.getProcedimentsCieByEntitat(codiEntitat, filtre);
    }

    @GetMapping(value = "{codiProcediment}/entitat/{codiEntitat}/organ/{codiOrgan}/cie/actiu")
    public Boolean getProcedimentsCieByEntitatAndOrganAndCodi(HttpServletRequest request,
                                                      @PathVariable String codiProcediment,
                                                      @PathVariable String codiEntitat,
                                                      @PathVariable String codiOrgan) {

        return procedimentService.isProcedimentEntregaCieActiva(codiEntitat, codiOrgan, codiProcediment);
    }
}
