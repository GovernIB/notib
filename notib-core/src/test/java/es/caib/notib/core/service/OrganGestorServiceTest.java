package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.cacheable.ProcSerCacheable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.*;
import es.caib.notib.core.test.data.ConfigTest;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class OrganGestorServiceTest {
    @Mock
    private ProcedimentRepository procedimentRepository;
    @Mock
    private OrganGestorRepository organGestorRepository;
    @Mock
    private NotificacioTableViewRepository notificacioTableViewRepository;
    @Mock
    private EnviamentTableRepository enviamentTableRepository;
    @Mock
    private ConversioTipusHelper conversioTipusHelper;
    @Mock
    private EntityComprovarHelper entityComprovarHelper;
    @Mock
    private PaginacioHelper paginacioHelper;
    @Mock
    private PermisosHelper permisosHelper;
    @Mock
    private CacheHelper cacheHelper;
    @Mock
    private OrganigramaHelper organigramaHelper;
    @Mock
    private MetricsHelper metricsHelper;
    @Mock
    private GrupRepository grupReposity;
    @Mock
    private PagadorPostalRepository pagadorPostalReposity;
    @Mock
    private PagadorCieRepository pagadorCieReposity;
    @Mock
    private OrganGestorHelper organGestorHelper;
    @Mock
    private OrganGestorCachable organGestorCachable;
    @Mock
    private PermisosCacheable permisosCacheable;
    @Mock
    private ProcSerCacheable procedimentsCacheable;
    @Mock
    private ConfigHelper configHelper;
    @Mock
    private EntregaCieRepository entregaCieRepository;
    @InjectMocks
    private OrganGestorServiceImpl organGestorService;

    private static final long ENTITAT_ID = 2L;
    private static final String ENTITAT_CODI = ConfigTest.ORGAN_DIR3;

    private static final String ORGAN_CODI = ConfigTest.DEFAULT_ORGAN_DIR3;

    @Before
    public void setUp() throws Exception {
        EntitatEntity entitatEntity = new EntitatEntity();
        ReflectionTestUtils.setField(entitatEntity, "id", ENTITAT_ID);
        ReflectionTestUtils.setField(entitatEntity, "apiKey", "APIKEY");
        ReflectionTestUtils.setField(entitatEntity, "dir3Codi", ENTITAT_CODI);

        Mockito.when(entityComprovarHelper.comprovarEntitat(Mockito.eq(2L))).thenReturn(entitatEntity);

        OrganGestorEntity organGestor = OrganGestorEntity.builder(
                ORGAN_CODI,
                "nom vell",
                entitatEntity,
                "llibre",
                "llibre nom",
                "oficina",
                "oficina nom",
                OrganGestorEstatEnum.ALTRES)
                .build();
        Mockito.when(organGestorRepository.findByCodi(Mockito.eq(ORGAN_CODI))).thenReturn(organGestor);
    }

    @Test
    public void givenOrganGestorInexistentAlRegistre_whenUpdateOne_thenUpdateAllfieldsExceptLlibre() throws RegistrePluginException, SistemaExternException {
        // Given
        Mockito.when(cacheHelper.getLlibreOrganGestor(
                Mockito.eq(ENTITAT_CODI),
                Mockito.eq(ORGAN_CODI)
        )).thenReturn(null);

        Mockito.when(cacheHelper.findDenominacioOrganisme(Mockito.eq(ORGAN_CODI))).thenReturn("Nom nou");

        Map<String, NodeDir3> arbreUnitats = new HashMap<>();
        NodeDir3 nodeOrganDefault = new NodeDir3();
        nodeOrganDefault.setCodi(ORGAN_CODI);
        nodeOrganDefault.setDenominacio("Òrgan default");
        nodeOrganDefault.setEstat("Vigent");
        arbreUnitats.put(ORGAN_CODI, nodeOrganDefault);
        Mockito.when(cacheHelper.findOrganigramaNodeByEntitat(Mockito.eq(ENTITAT_CODI))).thenReturn(arbreUnitats);

        List<OficinaDto> oficinesSIR = new ArrayList<>();
        oficinesSIR.add(new OficinaDto("CODI2", "OFICINA NOVA"));
        Mockito.when(cacheHelper.getOficinesSIRUnitat(
                Mockito.<Map<String, NodeDir3>>any(), Mockito.eq(ORGAN_CODI))
        ).thenReturn(oficinesSIR);
        Mockito.when(organGestorHelper.getEstatOrgan(Mockito.eq(nodeOrganDefault))).thenCallRealMethod();


        OrganGestorEntity organGestor = organGestorRepository.findByCodi(ORGAN_CODI);
        Assert.assertEquals(ORGAN_CODI, organGestor.getCodi());
        Assert.assertEquals("nom vell", organGestor.getNom());
        Assert.assertEquals(ENTITAT_ID, (long) organGestor.getEntitat().getId());
        Assert.assertEquals("llibre", organGestor.getLlibre());
        Assert.assertEquals("llibre nom", organGestor.getLlibreNom());
        Assert.assertEquals("oficina", organGestor.getOficina());
        Assert.assertEquals("oficina nom", organGestor.getOficinaNom());
        Assert.assertEquals(OrganGestorEstatEnum.ALTRES, organGestor.getEstat());

        // When
        organGestorService.updateOne(ENTITAT_ID, ORGAN_CODI);

        // Then
        organGestor = organGestorRepository.findByCodi(ORGAN_CODI);
        Assert.assertEquals(ORGAN_CODI, organGestor.getCodi());
        Assert.assertEquals("Nom nou", organGestor.getNom());
        Assert.assertEquals(ENTITAT_ID, (long) organGestor.getEntitat().getId());
        Assert.assertEquals("llibre", organGestor.getLlibre());
        Assert.assertEquals("llibre nom", organGestor.getLlibreNom());
        Assert.assertEquals("CODI2", organGestor.getOficina());
        Assert.assertEquals("OFICINA NOVA", organGestor.getOficinaNom());
        Assert.assertEquals(OrganGestorEstatEnum.VIGENT, organGestor.getEstat());
    }

}