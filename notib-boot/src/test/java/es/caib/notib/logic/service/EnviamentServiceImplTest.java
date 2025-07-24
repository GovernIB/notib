package es.caib.notib.logic.service;

import es.caib.notib.NotibApp;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.EntitatTipusEnumDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusDocumentDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.test.data.ConfigTest;
import es.caib.notib.logic.test.data.NotificacioItemTest;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.registre.RegistrePluginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = NotibApp.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@Transactional
public class EnviamentServiceImplTest extends BaseServiceTest {

    private static final String ENTITAT_DGTIC_DIR3CODI = "EA0004518";
    private static final String ENTITAT_DGTIC_KEY = "MjkwNTc3Mjk0MjkyNTU3OTkyNA==";

    private static final int NUM_DESTINATARIS = 2;

    @Autowired
    PermisosHelper permisosHelper;
    @Autowired
    EnviamentService enviamentService;
    @Autowired
    NotificacioEnviamentRepository enviamentRepository;
    @Autowired
    NotificacioItemTest notificacioCreate;

    EntitatDto entitatCreate;
    ProcSerDto procedimentCreate;
    OrganGestorDto organGestorCreate;



    @Before
    public void setUp() throws SistemaExternException, IOException, DecoderException, RegistrePluginException {

        configHelper.reloadDbProperties();
//        setDefaultConfigs();
        List<PermisDto> permisosEntitat = new ArrayList<PermisDto>();
        entitatCreate = new EntitatDto();
        entitatCreate.setCodi("LIMIT");
        entitatCreate.setNom("Limit Tecnologies");
        entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
        entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
        entitatCreate.setDir3Codi("CAIB");
        entitatCreate.setApiKey(ENTITAT_DGTIC_KEY);
        entitatCreate.setAmbEntregaDeh(true);
//        entitatCreate.setAmbEntregaCie(true);
        TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
        tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
        entitatCreate.setTipusDocDefault(tipusDocDefault);

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

        List<PermisDto> permisosOrgan = new ArrayList<PermisDto>();
        organGestorCreate = new OrganGestorDto();
        organGestorCreate.setCodi(ConfigTest.ORGAN_DIR3);
        organGestorCreate.setNom("Òrgan prova");
        PermisDto permisOrgan = new PermisDto();
        permisOrgan.setAdministrador(true);
        permisOrgan.setTipus(TipusEnumDto.USUARI);
        permisOrgan.setPrincipal("admin");
        permisosOrgan.add(permisOrgan);
        organGestorCreate.setPermisos(permisosOrgan);

        List<PermisDto> permisosProcediment = new ArrayList<PermisDto>();
        procedimentCreate = new ProcSerDto();
        procedimentCreate.setCodi("216076");
        procedimentCreate.setNom("Procedimiento 1");
        procedimentCreate.setOrganGestor(ConfigTest.ORGAN_DIR3);
        PermisDto permisNotificacio = new PermisDto();
        permisNotificacio.setNotificacio(true);
        permisNotificacio.setTipus(TipusEnumDto.USUARI);
        permisNotificacio.setPrincipal("admin");
        permisosProcediment.add(permisNotificacio);

        procedimentCreate.setPermisos(permisosProcediment);
        ConfigHelper.setEntitatCodi("test");
//        System.setProperty("es.caib.notib.plugin.gesdoc.filesystem.base.dir", "/home/bgalmes/dades/notib-fs/");
    }

    @Test
    public void actualitzarEstat() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(List<Object> elementsCreats) throws Exception {
                        configureMockRegistrePlugin();
                        configureMockUnitatsOrganitzativesPlugin();
                        configureMockDadesUsuariPlugin();
                        configureMockGestioDocumentalPlugin();
                        autenticarUsuari("admin");

                        var entitatCreate = (EntitatDto)elementsCreats.get(0);
                        var procedimentCreate = (ProcSerDto)elementsCreats.get(2);
                        assertNotNull(procedimentCreate);
                        assertNotNull(procedimentCreate.getId());
                        assertNotNull(entitatCreate);
                        assertNotNull(entitatCreate.getId());
                        String notificacioId = new Long(System.currentTimeMillis()).toString();
                        var notificacio = notificacioCreate.getRandomInstance();

                        var notificacioCreated = notificacioService.create(entitatCreate.getId(), notificacio);
                        assertNotNull(notificacioCreated);

                        // Given: Un enviament qualsevol
                        var enviament = notificacioCreated.getEnviaments().get(0);

                        // When: Actualitzam l'estat de l'enviament
                        try {
                            enviamentService.actualitzarEstat(enviament.getId(), null);

                            // Then: Comptadors d'intents a 0
                            var envEntity = enviamentRepository.findById(enviament.getId()).orElseThrow();
                            assertEquals(0, envEntity.getNotificaIntentNum());
                            assertEquals(0, envEntity.getSirConsultaIntent());

                        } finally {
                            notificacioService.delete(entitatCreate.getId(), notificacioCreated.getId());
                        }
                    }
                },
                "Create Notificació",
                entitatCreate,
                organGestorCreate,
                procedimentCreate);
    }
}