package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.test.AuthenticationTest;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProcedimentItemTest extends DatabaseItemTest<ProcSerDto>{
    @Autowired
    protected ProcedimentService procedimentService;
    @Autowired
    protected OrganGestorService organGestorService;
    @Autowired
    protected AuthenticationTest authenticationTest;

    @Override
    public ProcSerDto create(Object element, Long entitatId) throws Exception{
        authenticationTest.autenticarUsuari("admin");
        ProcSerDto entitatCreada = procedimentService.create(
                entitatId,
                (ProcSerDto) element);
        OrganGestorDto organ = organGestorService.findByCodi(entitatId,
                ConfigTest.DEFAULT_ORGAN_DIR3);
        if (((ProcSerDto)element).getPermisos() != null) {
            for (PermisDto permis: ((ProcSerDto)element).getPermisos()) {
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
    public void delete(Long entitatId, ProcSerDto object) {
        authenticationTest.autenticarUsuari("admin");
        procedimentService.delete(
                entitatId,
                object.getId(),
                true);
    }

//    @Override
    public ProcSerDto getRandomInstance() {
        ProcSerDto procedimentCreate = new ProcSerDto();
        procedimentCreate.setCodi(RandomStringUtils.randomNumeric(6));
        procedimentCreate.setNom(RandomStringUtils.randomAlphanumeric(10));
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
    public static ProcSerDto getRandomProcedimentSensePermis() {
        ProcSerDto procedimentCreate = new ProcSerDto();
        procedimentCreate.setCodi(RandomStringUtils.randomNumeric(6));
        procedimentCreate.setNom(RandomStringUtils.randomAlphanumeric(10));
        procedimentCreate.setOrganGestor(ConfigTest.DEFAULT_ORGAN_DIR3);
        return procedimentCreate;
    }

    public ProcSerDto getRandomInstanceAmbEntregaCie(Long cieId, Long operadorPostalId) {
        ProcSerDto procedimentCreate = getRandomInstance();
        procedimentCreate.setEntregaCieActiva(true);
        procedimentCreate.setCieId(cieId);
        procedimentCreate.setOperadorPostalId(operadorPostalId);
        return procedimentCreate;
    }

}
