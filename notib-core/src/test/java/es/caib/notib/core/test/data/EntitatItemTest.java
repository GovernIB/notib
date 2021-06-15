package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.test.AuthenticationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntitatItemTest extends DatabaseItemTest<EntitatDto> {
    @Autowired
    protected EntitatService entitatService;
    @Autowired
    protected AuthenticationTest authenticationTest;


    public EntitatDto create(EntitatDto element) throws Exception{
        authenticationTest.autenticarUsuari("super");
        EntitatDto entitatCreada = entitatService.create(element);
        if (element.getPermisos() != null) {
            for (PermisDto permis: element.getPermisos()) {
                entitatService.permisUpdate(
                        entitatCreada.getId(),
                        permis);
            }
        }
        return entitatCreada;
    }

    public void delete(Long entitatId) {
        authenticationTest.autenticarUsuari("super");
        entitatService.delete(entitatId);
    }

    public static EntitatDto getRandomInstance() {
        EntitatDto entitatCreate = new EntitatDto();
        entitatCreate.setCodi("LIMIT");
        entitatCreate.setNom("Limit Tecnologies");
        entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
        entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
        entitatCreate.setDir3Codi(ConfigTest.ENTITAT_DGTIC_DIR3CODI);
        entitatCreate.setApiKey(ConfigTest.ENTITAT_DGTIC_KEY);
        entitatCreate.setAmbEntregaDeh(true);
        entitatCreate.setAmbEntregaCie(true);
        TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
        tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
        entitatCreate.setTipusDocDefault(tipusDocDefault);

        List<PermisDto> permisosEntitat = new ArrayList<PermisDto>();
        PermisDto permisUsuari = new PermisDto();
        PermisDto permisAdminEntitat = new PermisDto();

        permisUsuari.setUsuari(true);
        permisUsuari.setTipus(TipusEnumDto.USUARI);
        permisUsuari.setPrincipal("admin");
        permisosEntitat.add(permisUsuari);

        permisAdminEntitat.setAdministradorEntitat(true);
        permisAdminEntitat.setTipus(TipusEnumDto.USUARI);
        permisAdminEntitat.setPrincipal("admin");
        permisosEntitat.add(permisAdminEntitat);
        entitatCreate.setPermisos(permisosEntitat);
        return entitatCreate;
    }

    @Override
    public EntitatDto create(EntitatDto element, Long entitatId) throws Exception {
        return create(element);
    }

    @Override
    public void delete(Long entitatId, EntitatDto object) throws Exception {
        delete(entitatId);
    }
}
