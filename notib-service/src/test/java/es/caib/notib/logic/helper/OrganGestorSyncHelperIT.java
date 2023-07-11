package es.caib.notib.logic.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.service.BaseServiceTestV2;
import es.caib.notib.logic.test.AuthenticationTest;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/logic/application-context-synctest.xml"})
@Transactional
public class OrganGestorSyncHelperIT {

    @Autowired
    @InjectMocks
    private OrganGestorService organGestorService;

    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private ProcSerSyncHelper procSerSyncHelper;
    @Mock
    private IntegracioHelper integracioHelper;

    private static final String UNITATS_JSON = "[" +
            "{\"codigo\": \"A002\", \"denominacion\": \"B002\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A003\", \"denominacion\": \"A003\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A101\", \"A102\"]}," +
            "{\"codigo\": \"A004\", \"denominacion\": \"A004\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A103\"]}," +
            "{\"codigo\": \"A005\", \"denominacion\": \"A005\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A103\"]}," +
            "{\"codigo\": \"A006\", \"denominacion\": \"A006\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A104\"]}," +
            "{\"codigo\": \"A007\", \"denominacion\": \"A007\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A008\", \"denominacion\": \"A008\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A106\", \"A107\", \"A108\"]}," +
            "{\"codigo\": \"A009\", \"denominacion\": \"A009\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A111\"]}," +
            "{\"codigo\": \"A010\", \"denominacion\": \"A010\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A111\"]}," +
            "{\"codigo\": \"A011\", \"denominacion\": \"A011\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A112\", \"A113\"]}," +
            "{\"codigo\": \"A012\", \"denominacion\": \"A012\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A117\", \"A118\"]}," +
            "{\"codigo\": \"A013\", \"denominacion\": \"A013\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A014\", \"denominacion\": \"A014\", \"descripcionEstado\":\"V\", \"superior\": \"A013\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": [\"A119\"]}," +
            "{\"codigo\": \"A014\", \"denominacion\": \"A014\", \"descripcionEstado\":\"V\", \"superior\": \"A119\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A015\", \"denominacion\": \"A015\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A016\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A015\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": [\"A120\"]}," +
            "{\"codigo\": \"A016\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A121\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A017\", \"denominacion\": \"A017\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A017\", \"A122\"]}," +
            "{\"codigo\": \"A018\", \"denominacion\": \"A018\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A018\"]}," +
            "{\"codigo\": \"A019\", \"denominacion\": \"A019\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A018\"]}," +
            "{\"codigo\": \"A101\", \"denominacion\": \"A101\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A102\", \"denominacion\": \"A102\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A103\", \"denominacion\": \"A103\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A104\", \"denominacion\": \"A104\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A105\", \"denominacion\": \"A105\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A106\", \"denominacion\": \"A106\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A109\"]}," +
            "{\"codigo\": \"A107\", \"denominacion\": \"A107\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A110\"]}," +
            "{\"codigo\": \"A108\", \"denominacion\": \"A108\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A110\"]}," +
            "{\"codigo\": \"A109\", \"denominacion\": \"A109\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A110\", \"denominacion\": \"A110\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A111\", \"denominacion\": \"A111\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A114\"]}," +
            "{\"codigo\": \"A112\", \"denominacion\": \"A112\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A114\"]}," +
            "{\"codigo\": \"A113\", \"denominacion\": \"A113\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": [\"A115\", \"A116\"]}," +
            "{\"codigo\": \"A114\", \"denominacion\": \"A114\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A115\", \"denominacion\": \"A115\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A116\", \"denominacion\": \"A116\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A117\", \"denominacion\": \"A117\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A118\", \"denominacion\": \"A118\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}," +
            "{\"codigo\": \"A119\", \"denominacion\": \"A119\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A014\", \"denominacion\": \"A014\", \"descripcionEstado\":\"V\", \"superior\": \"A119\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": []}," +
            "{\"codigo\": \"A120\", \"denominacion\": \"A120\", \"descripcionEstado\":\"E\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A016\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A120\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": [\"A121\"]}," +
            "{\"codigo\": \"A121\", \"denominacion\": \"A121\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [{\"codigo\": \"A014\", \"denominacion\": \"A016\", \"descripcionEstado\":\"V\", \"superior\": \"A121\", \"hijos\": [], \"historicosUO\": []}], \"historicosUO\": []}," +
            "{\"codigo\": \"A122\", \"denominacion\": \"A122\", \"descripcionEstado\":\"V\", \"superior\": \"A000\", \"hijos\": [], \"historicosUO\": []}" +
            "]";


    @BeforeClass
    public static void beforeClass() {
//        JBossPropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
        BaseServiceTestV2.loadProperties("classpath:es/caib/notib/core/test.properties");
    }

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        organGestorService.setServicesForSynctest(procSerSyncHelper, pluginHelper, integracioHelper);
        organGestorHelper.setServicesForSynctest(pluginHelper);
        cacheHelper.setPluginHelper(pluginHelper);
        ObjectMapper mapper = new ObjectMapper();

        Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.anyString(), Mockito.anyString(), Mockito.nullable(Date.class), Mockito.nullable(Date.class))).thenReturn(mapper.readValue(UNITATS_JSON, new TypeReference<List<NodeDir3>>(){}));
        Mockito.doNothing().when(procSerSyncHelper).actualitzaProcediments(Mockito.any(EntitatDto.class));
        Mockito.doNothing().when(procSerSyncHelper).actualitzaServeis(Mockito.any(EntitatDto.class));
        Mockito.doNothing().when(integracioHelper).addAccioOk(Mockito.any(IntegracioInfo.class));
        Mockito.doNothing().when(integracioHelper).addAccioOk(Mockito.any(IntegracioInfo.class), Mockito.anyBoolean());
        Mockito.when(pluginHelper.dadesUsuariConsultarAmbCodi(Mockito.anyString())).thenReturn(null);
        Mockito.when(pluginHelper.llistarLlibreOrganisme(Mockito.anyString(), Mockito.anyString())).thenReturn(new LlibreDto());

    }

    @After
    public final void tearDown() {

    }


    @Test
    public void whenOrganGestorSync() throws Exception {
        authenticationTest.autenticarUsuari("admin");
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setCodi("ENTITAT_TESTS");
        entitatDto.setDir3Codi("EA0004518");
        entitatDto.setId(1L);
        List<OrganGestorEntity>[] result = (List<OrganGestorEntity>[])organGestorService.syncDir3OrgansGestors(entitatDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.length);

//        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(1L);
        List<OrganGestorEntity> organsVigents = organGestorRepository.findByEntitatIdAndEstat(1L, OrganGestorEstatEnum.V);
        List<OrganGestorEntity> obsoleteUnitats = result[0];
        List<OrganGestorEntity> organsDividits = result[1];
        List<OrganGestorEntity> organsFusionats = result[2];
        List<OrganGestorEntity> organsSubstituits = result[3];

        // ÒRGANS
        // Vigents
        Assert.assertEquals(22, organsVigents.size());
        Assert.assertTrue(conteOrgan(organsVigents, "EA0004518"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "EA0004518").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A000"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A000").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A001"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A001").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A002"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A002").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A101"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A101").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A102"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A102").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A103"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A103").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A104"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A104").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A105"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A105").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A109"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A109").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A110"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A110").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A114"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A114").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A115"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A115").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A116"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A116").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A118"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A118").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A119"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A119").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A014"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A014").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A121"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A121").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A016"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A016").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A017"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A017").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A018"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A018").getEstat()));
        Assert.assertTrue(conteOrgan(organsVigents, "A122"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsVigents, "A122").getEstat()));

        // Obsolets
        Assert.assertEquals(22, obsoleteUnitats.size());

        // Extingides
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(obsoleteUnitats, "A007").getEstat()));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(obsoleteUnitats, "A117").getEstat()));

        // Dividits
        Assert.assertEquals(6, organsDividits.size());
        Assert.assertTrue(conteOrgan(organsDividits, "A003"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsDividits, "A003").getEstat()));
        Assert.assertTrue(conteOrgan(organsDividits, "A008"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsDividits, "A008").getEstat()));
        Assert.assertTrue(conteOrgan(organsDividits, "A011"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsDividits, "A011").getEstat()));
        Assert.assertTrue(conteOrgan(organsDividits, "A113"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsDividits, "A113").getEstat()));
        Assert.assertTrue(conteOrgan(organsDividits, "A012"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsDividits, "A012").getEstat()));
        Assert.assertTrue(conteOrgan(organsDividits, "A017"));
        Assert.assertTrue(OrganGestorEstatEnum.V.equals(getOrgan(organsDividits, "A017").getEstat()));

        // Fusionats
        Assert.assertEquals(9, organsFusionats.size());
        Assert.assertTrue(conteOrgan(organsFusionats, "A004"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A004").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A005"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A005").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A107"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A107").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A108"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A108").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A009"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A009").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A010"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A010").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A111"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A111").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A112"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A112").getEstat()));
        Assert.assertTrue(conteOrgan(organsFusionats, "A019"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsFusionats, "A019").getEstat()));

        // Substituits
        Assert.assertEquals(5, organsSubstituits.size());
        Assert.assertTrue(conteOrgan(organsSubstituits, "A006"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsSubstituits, "A006").getEstat()));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A106"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsSubstituits, "A106").getEstat()));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A013"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsSubstituits, "A013").getEstat()));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A015"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsSubstituits, "A015").getEstat()));
        Assert.assertTrue(conteOrgan(organsSubstituits, "A120"));
        Assert.assertTrue(OrganGestorEstatEnum.E.equals(getOrgan(organsSubstituits, "A120").getEstat()));


        // Permisos
        for (OrganGestorEntity organ: organsVigents) {
            List<PermisDto> permisos = permisosHelper.findPermisos(organ.getId(), OrganGestorEntity.class);
            PermisDto permisAdmin = null;
            PermisDto permisRole = null;
            for (PermisDto permis: permisos) {
                if ("admin".equals(permis.getPrincipal())) {
                    permisAdmin = permis;
                } else if ("ROLE".equals(permis.getPrincipal())) {
                    permisRole = permis;
                }
            }
            switch (organ.getCodi()) {
                case "EA0004518":
                    break;
                case "A000":
                    Assert.assertEquals(0, permisos.size());
                    break;
                case "A001":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isRead());
                    break;
                case "A002":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isRead());
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisRole.isAdministrador());
                    break;
                case "A101":
                case "A102":
                    Assert.assertEquals(0, permisos.size());
                    Assert.assertNull(permisRole);
                    break;
                case "A103":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isAdministrador());
                    Assert.assertTrue(permisAdmin.isNotificacio());
                    Assert.assertTrue(permisAdmin.isComuns());
                    Assert.assertTrue(permisAdmin.isComunicacioSir());
                    Assert.assertTrue(permisRole.isRead());
                    Assert.assertTrue(permisRole.isAdministrador());
                    Assert.assertTrue(permisRole.isComuns());
                    break;
                case "A104":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isAdministration());
                    Assert.assertTrue(permisRole.isProcessar());
                    break;
                case "A105":
                    Assert.assertEquals(0, permisos.size());
                    break;
                case "A109":
                case "A110":
                    Assert.assertEquals(0, permisos.size());
                    Assert.assertNull(permisAdmin);
                    Assert.assertNull(permisRole);
                    break;
                case "A114":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isRead());
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisAdmin.isNotificacio());
                    Assert.assertTrue(permisRole.isProcessar());
                    Assert.assertTrue(permisRole.isComuns());
                    Assert.assertTrue(permisRole.isComunicacioSir());
                    break;
                case "A115":
                case "A116":
                    Assert.assertEquals(0, permisos.size());
                    Assert.assertNull(permisAdmin);
                    Assert.assertNull(permisRole);
                    break;
                case "A118":
                    Assert.assertEquals(0, permisos.size());
                    Assert.assertNull(permisAdmin);
                    break;
                case "A119":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isRead());
                    Assert.assertTrue(permisRole.isNotificacio());
                    break;
                case "A014":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isRead());
                    break;
                case "A121":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isAdministration());
                    Assert.assertTrue(permisAdmin.isAdministrador());
                    break;
                case "A016":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isAdministration());
                    break;
                case "A017":
                    Assert.assertEquals(2, permisos.size());
                    Assert.assertNotNull(permisAdmin);
                    Assert.assertTrue(permisAdmin.isAdministrador());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isProcessar());
                    break;
                case "A122":
                    Assert.assertEquals(0, permisos.size());
                    Assert.assertNull(permisAdmin);
                    Assert.assertNull(permisRole);
                    break;
                case "A018":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisRole.isAdministration());
                    Assert.assertTrue(permisRole.isComuns());
                    break;
                case "A019":
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertEquals(1, permisos.size());
                    Assert.assertNotNull(permisRole);
                    Assert.assertTrue(permisAdmin.isAdministration());
                    break;
                default:
                    Assert.fail("L'òrgan " + organ.getCodi() + " no hauria d'estar vigent!");
            }
        }

    }

    private boolean conteOrgan(List<OrganGestorEntity> llista, String codi) {
        for (OrganGestorEntity organ: llista) {
            if (organ.getCodi().equals(codi))
                return true;
        }
        return false;
    }

    private OrganGestorEntity getOrgan(List<OrganGestorEntity> llista, String codi) {
        for (OrganGestorEntity organ: llista) {
            if (organ.getCodi().equals(codi)) {
                return organ;
            }
        }
        return null;
    }
}
