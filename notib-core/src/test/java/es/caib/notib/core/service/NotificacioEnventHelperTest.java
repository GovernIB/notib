package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.helper.NotificacioEventHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.NotificacioRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioEnventHelperTest extends BaseServiceTest {

    private static final String ENTITAT_DGTIC_DIR3CODI = "EA0004518";
    private static final String ENTITAT_DGTIC_KEY = "MjkwNTc3Mjk0MjkyNTU3OTkyNA==";

    private static final int NUM_DESTINATARIS = 2;

    @Autowired
    NotificacioEventHelper notificacioEventHelper;
    @Autowired
    PermisosHelper permisosHelper;
    @Autowired
    NotificacioService notificacioService;

    @Autowired
    NotificacioRepository notificacioRepository;

//    @Mock
//    NotificaHelper notificaHelper;

//    @InjectMocks
//    NotificacioService notificacioService;

    EntitatDto entitat;
    ProcedimentDto procediment;
    OrganGestorDto organGestor;
    NotificacioDtoV2 notificacio;

    @Before
    public void setup() throws Exception {

        List<PermisDto> permisosEntitat = new ArrayList<PermisDto>();
        entitat = new EntitatDto();
        entitat.setCodi("LIMIT");
        entitat.setNom("Limit Tecnologies");
        entitat.setDescripcio("Descripció de Limit Tecnologies");
        entitat.setTipus(EntitatTipusEnumDto.GOVERN);
        entitat.setDir3Codi(ENTITAT_DGTIC_DIR3CODI);
        entitat.setApiKey(ENTITAT_DGTIC_KEY);
        entitat.setAmbEntregaDeh(true);
        entitat.setAmbEntregaCie(true);
        TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
        tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
        entitat.setTipusDocDefault(tipusDocDefault);

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
        entitat.setPermisos(permisosEntitat);

        List<PermisDto> permisosOrgan = new ArrayList<PermisDto>();
        organGestor = new OrganGestorDto();
        organGestor.setCodi("A00000000");
        organGestor.setNom("Òrgan prova");
        PermisDto permisOrgan = new PermisDto();
        permisOrgan.setAdministrador(true);
        permisOrgan.setTipus(TipusEnumDto.USUARI);
        permisOrgan.setPrincipal("admin");
        permisosOrgan.add(permisOrgan);
        organGestor.setPermisos(permisosOrgan);

        List<PermisDto> permisosProcediment = new ArrayList<PermisDto>();
        procediment = new ProcedimentDto();
        procediment.setCodi("216076");
        procediment.setNom("Procedimiento 1");
        procediment.setOrganGestor("A00000000");
        PermisDto permisNotificacio = new PermisDto();
        permisNotificacio.setNotificacio(true);
        permisNotificacio.setTipus(TipusEnumDto.USUARI);
        permisNotificacio.setPrincipal("admin");
        permisosProcediment.add(permisNotificacio);

        procediment.setPermisos(permisosProcediment);

        configureMockRegistrePlugin();
        configureMockUnitatsOrganitzativesPlugin();
        configureMockDadesUsuariPlugin();

        notificacio = generarNotificacio(
                new Long(System.currentTimeMillis()).toString(),
                procediment,
                entitat,
                ENTITAT_DGTIC_DIR3CODI,
                NUM_DESTINATARIS,
                false);

//        notificaHelper = mock(NotificaHelper.class);
//        when(notificaHelper.notificacioEnviar(anyLong())).thenThrow(new SistemaExternException());
    }

//    @Test
    void clearOldUselessEventsTest() {
        testCreantElements(
            new TestAmbElementsCreats() {
                @Override
                public void executar(List<Object> elementsCreats) throws Exception {
                    autenticarUsuari("admin");

                    // 1. Crear notificacio
                    EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
                    ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
                    NotificacioDtoV2 notificacioCreada = (NotificacioDtoV2)elementsCreats.get(3);
                    assertNotNull(procedimentCreat);
                    assertNotNull(entitatCreada);
                    assertNotNull(notificacioCreada);

                    NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());

                    // 2. Crear events en la notificació

                    // 3. Comprovar que els events estan desats a la base de dades

                    // 4. Executar el mètode
                    notificacioEventHelper.clearOldUselessEvents(notificacioEntity);

                    // 5. Comprovar que els events que s'havien de borrar s'han borrat,
                    // i que els que no s'havien de borrar encara hi son

                }
            },
            "Netejar events no útils",
            entitat,
            organGestor,
            procediment,
            notificacio);
    }


}