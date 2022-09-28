package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.logic.service.BaseServiceTestV2;
import es.caib.notib.logic.test.data.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/logic/application-context-test.xml"})
@Transactional
public class EntityComprovarHelperIT extends BaseServiceTestV2 {
    @Autowired
    private OrganGestorRepository organGestorRepository;


    @Autowired
    private EntityComprovarHelper entityComprovarHelper;

    @Autowired
    private ProcedimentItemTest procedimentCreator;
    @Autowired
    private NotificacioItemTest notificacioCreator;

    private ElementsCreats database;
    @Autowired
    private OrganGestorItemTest organGestorCreator;

    @Before
    public void setUp() throws Exception {
        setDefaultConfigs();
        configureMockGestioDocumentalPlugin();

        procedimentCreator.addObject("procediment", procedimentCreator.getRandomInstance());
        procedimentCreator.addObject("procedimentSensePermis", ProcedimentItemTest.getRandomProcedimentSensePermis());

        notificacioCreator.addObject("notificacio", NotificacioItemTest.getRandomInstance());
        notificacioCreator.addRelated("notificacio", "procediment", procedimentCreator);

        NotificacioDatabaseDto notificacioSensePermisos = NotificacioItemTest.getRandomInstance(1);
        notificacioSensePermisos.setOrganGestorCodi(ConfigTest.ORGAN_DIR3_SENSE_PERMISOS);
        notificacioCreator.addObject("notificacioSensePermisos", notificacioSensePermisos);
        notificacioCreator.addRelated("notificacioSensePermisos", "procedimentSensePermis", procedimentCreator);

        OrganGestorDto organGestor = new OrganGestorDto();
        organGestor.setCodi("DIR3-1");
        organGestor.setNom("Procedimiento 1");
        organGestor.setEstat(OrganGestorEstatEnum.V);
        organGestorCreator.addObject("organGestorProves", organGestor);

        database = createDatabase(EntitatItemTest.getRandomInstance(),
                organGestorCreator,
                procedimentCreator,
                notificacioCreator
        );
    }

    @After
    public final void tearDown() {
        destroyDatabase(database.getEntitat().getId(),
                notificacioCreator,
                procedimentCreator,
                organGestorCreator
        );
    }


    @Test
    public void whenHasOrganGestor() throws Exception {

        OrganGestorDto organGestor = (OrganGestorDto) database.get("organGestorProves");
        OrganGestorEntity organGestorEntity = organGestorRepository.findById(organGestor.getId()).orElseThrow();
        Assert.assertFalse(entityComprovarHelper.hasPermisOrganGestor(organGestorEntity, PermisEnum.PROCESSAR));

        PermisDto permis = new PermisDto();
        permis.setProcessar(true);
        permis.setTipus(TipusEnumDto.USUARI);
        permis.setPrincipal("user");
        organGestorService.permisUpdate(
                database.getEntitat().getId(),
                organGestorEntity.getId(),
                false,
                permis);

        //When
        authenticationTest.autenticarUsuari("user");
        organGestorEntity = organGestorRepository.findById(organGestor.getId()).orElseThrow();
        Assert.assertTrue(entityComprovarHelper.hasPermisOrganGestor(organGestorEntity, PermisEnum.PROCESSAR));

    }
}
