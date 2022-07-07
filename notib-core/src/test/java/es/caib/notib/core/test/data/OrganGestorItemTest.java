package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.test.AuthenticationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrganGestorItemTest extends DatabaseItemTest<OrganGestorDto>{
    @Autowired
    protected OrganGestorService organGestorService;
    @Autowired
    protected AuthenticationTest authenticationTest;



    @Override
    public OrganGestorDto create(Object element, Long entitatId) throws Exception{
//        authenticationTest.autenticarUsuari("admin");
//        ((OrganGestorDto)element).setEntitatId(entitatId);
//        OrganGestorDto entitatCreada = organGestorService.create((OrganGestorDto)element);
////        organGestorId = entitatCreada.getId();
////        elementsCreats.add(entitatCreada);
//        if (((OrganGestorDto)element).getPermisos() != null) {
//            for (PermisDto permis: ((OrganGestorDto)element).getPermisos()) {
//                organGestorService.permisUpdate(
//                        entitatId,
//                        entitatCreada.getId(),
//                        false,
//                        permis);
//            }
//        }
//        return entitatCreada;
        return null;
    }

    @Override
    public void delete(Long entitatId, OrganGestorDto object) {
//        authenticationTest.autenticarUsuari("admin");
//        organGestorService.delete(
//                entitatId,
//                object.getId());
    }

    public static OrganGestorDto getRandomInstance() {
        List<PermisDto> permisosOrgan = new ArrayList<PermisDto>();
        OrganGestorDto organGestorCreate = new OrganGestorDto();
        organGestorCreate.setCodi(ConfigTest.DEFAULT_ORGAN_DIR3);
        organGestorCreate.setNom("Òrgan prova");
        PermisDto permisOrgan = new PermisDto();
        permisOrgan.setAdministrador(true);
        permisOrgan.setTipus(TipusEnumDto.USUARI);
        permisOrgan.setPrincipal("admin");
        permisosOrgan.add(permisOrgan);
        organGestorCreate.setPermisos(permisosOrgan);
        return organGestorCreate;
    }

//    public void setDefaultInstance() {
//        List<PermisDto> permisosOrgan = new ArrayList<PermisDto>();
//        OrganGestorDto organGestorCreate = new OrganGestorDto();
//        organGestorCreate.setCodi(DEFAULT_CODE);
//        organGestorCreate.setNom("Òrgan default");
//        organGestorCreate.setOficina(new OficinaDto("CodiOf", "Oficina"));
//        PermisDto permisOrgan = new PermisDto();
//        permisOrgan.setAdministrador(true);
//        permisOrgan.setTipus(TipusEnumDto.USUARI);
//        permisOrgan.setPrincipal("admin");
//        permisosOrgan.add(permisOrgan);
//        organGestorCreate.setPermisos(permisosOrgan);
//        this.objectInstance = organGestorCreate;
//    }

}
