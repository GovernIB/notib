package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.dto.procediment.ProcedimentDataDto;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.cacheable.ProcSerCacheable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.repository.GrupProcSerRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.test.data.ConfigTest;
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
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ProcedimentHelperTest {
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private PermisosHelper permisosHelper;
    @Mock
    private ProcedimentUpdateHelper procedimentUpdateHelper;
    @Mock
    private GrupProcSerRepository grupProcedimentRepository;
    @Mock
    private ProcedimentRepository procedimentRepository;
    @Mock
    private OrganGestorRepository organGestorRepository;
    @Mock
    private OrganGestorService organGestorService;
    @Mock
    private OrganGestorHelper organGestorHelper;
    @Mock
    private MessageHelper messageHelper;
    @Mock
    private ProcSerCacheable procedimentsCacheable;
    @InjectMocks
    private ProcSerHelper procedimentHelper;

    private EntitatEntity entitatEntity;


    private static final long ENTITAT_ID = 2L;
    private static final String ENTITAT_CODI = ConfigTest.ORGAN_DIR3;

    private static final String ORGAN_CODI = ConfigTest.DEFAULT_ORGAN_DIR3;

    @Before
    public void setUp() throws Exception {
        entitatEntity = new EntitatEntity();
        ReflectionTestUtils.setField(entitatEntity, "id", ENTITAT_ID);
        ReflectionTestUtils.setField(entitatEntity, "apiKey", "APIKEY");
        ReflectionTestUtils.setField(entitatEntity, "dir3Codi", ENTITAT_CODI);

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
    public void givenProcedimentSenseOrgan_whenActualitzarProcedimentFromGda_ThenReturn() throws Exception {
        // Given
        ProcedimentDataDto procedimentGda = ProcedimentDataDto.builder()
                .organGestor(ORGAN_CODI)
                .codi("codiSIA")
                .nom("nom vell")
                .build();
        ProcedimentEntity procedimentDB = ProcedimentEntity.builder()
                .organGestor(null)
                .codi("codiSIA")
                .nom("nom nou")
                .entitat(entitatEntity)
                .build();
        Mockito.when(procedimentRepository.findByCodiAndEntitat(
                Mockito.eq("codiSIA"),
                Mockito.eq(entitatEntity)
        )).thenReturn(procedimentDB);

        Mockito.when(procedimentUpdateHelper.updateProcediment(
                Mockito.<ProcedimentDataDto>any(),
                Mockito.<ProcedimentEntity>any(),
                Mockito.<OrganGestorEntity>any()
        )).thenReturn(procedimentDB);
//        Mockito.when(procedimentRepository.save(
//                Mockito.<ProcedimentEntity>any()
//        )).thenReturn(procedimentDB);
        Map<String, OrganismeDto> organigramaEntitat = new HashMap<>();
        organigramaEntitat.put(ORGAN_CODI, null);

        // When
        ProgresActualitzacioDto progres = new ProgresActualitzacioDto();
        progres.setNumProcediments(1);
        procedimentHelper.actualitzarProcedimentFromGda(
                progres,
                procedimentGda,
                entitatEntity,
                organigramaEntitat,
                true,
                new ArrayList<OrganGestorEntity>()
        );

        // Then
    }

}