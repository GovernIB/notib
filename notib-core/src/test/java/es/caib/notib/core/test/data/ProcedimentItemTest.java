package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.test.AuthenticationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProcedimentItemTest extends DatabaseItemTest<ProcedimentDto>{
    @Autowired
    protected ProcedimentService procedimentService;
    @Autowired
    protected OrganGestorService organGestorService;
    @Autowired
    protected AuthenticationTest authenticationTest;

    @Override
    public ProcedimentDto create(ProcedimentDto element, Long entitatId) throws Exception{
        authenticationTest.autenticarUsuari("admin");
        ProcedimentDto entitatCreada = procedimentService.create(
                entitatId,
                element);
        OrganGestorDto organ = organGestorService.findByCodi(entitatId,
                ConfigTest.DEFAULT_ORGAN_DIR3);
        if (((ProcedimentDto)element).getPermisos() != null) {
            for (PermisDto permis: element.getPermisos()) {
                procedimentService.permisUpdate(
                        entitatId,
                        organ.getId(),
                        entitatCreada.getId(),
                        permis);
            }
        }
        return entitatCreada;
    }

    @Override
    public void delete(Long entitatId, ProcedimentDto object) {
        authenticationTest.autenticarUsuari("admin");
        procedimentService.delete(
                entitatId,
                object.getId(),
                true);
    }

//    @Override
    public ProcedimentDto getRandomInstance() {
        ProcedimentDto procedimentCreate = new ProcedimentDto();
        procedimentCreate.setCodi("216076");
        procedimentCreate.setNom("Procedimiento 1");
        procedimentCreate.setOrganGestor(ConfigTest.DEFAULT_ORGAN_DIR3);

        List<PermisDto> permisosProcediment = new ArrayList<PermisDto>();
        PermisDto permisNotificacio = new PermisDto();
        permisNotificacio.setNotificacio(true);
        permisNotificacio.setTipus(TipusEnumDto.USUARI);
        permisNotificacio.setPrincipal("admin");

        permisosProcediment.add(permisNotificacio);

        procedimentCreate.setPermisos(permisosProcediment);
        return procedimentCreate;
    }

}
