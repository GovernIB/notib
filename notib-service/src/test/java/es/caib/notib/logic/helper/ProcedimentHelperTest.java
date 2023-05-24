package es.caib.notib.logic.helper;

import es.caib.notib.logic.cacheable.ProcSerCacheable;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProgresActualitzacioProcSer;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.test.data.ConfigTest;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
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
public class ProcedimentHelperTest {
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private PermisosHelper permisosHelper;
    @Mock
    private ProcSerUpdateHelper procedimentUpdateHelper;
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

        OrganGestorEntity organGestor = OrganGestorEntity.builder()
                .codi(ORGAN_CODI)
                .nom("nom vell")
                .entitat(entitatEntity)
                .llibre("llibre")
                .llibreNom("llibre nom")
                .oficina("oficina")
                .oficinaNom("oficina nom")
                .estat("E")
                .build();
        Mockito.when(organGestorRepository.findByCodi(Mockito.eq(ORGAN_CODI))).thenReturn(organGestor);
    }

    @Test
    public void givenProcedimentSenseOrgan_whenActualitzarProcedimentFromGda_ThenReturn() throws Exception {
        // Given
        ProcSerDataDto procedimentGda = ProcSerDataDto.builder()
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
                Mockito.<ProcSerDataDto>any(),
                Mockito.<ProcedimentEntity>any(),
                Mockito.<OrganGestorEntity>any()
        )).thenReturn(procedimentDB);
//        Mockito.when(procedimentRepository.save(
//                Mockito.<ProcedimentEntity>any()
//        )).thenReturn(procedimentDB);
//        Map<String, OrganismeDto> organigramaEntitat = new HashMap<>();
//        organigramaEntitat.put(ORGAN_CODI, null);
        List<String> codiOrgansGda = new ArrayList<>();
        codiOrgansGda.add(ORGAN_CODI);

        // When
        ProgresActualitzacioProcSer progres = new ProgresActualitzacioProcSer();
        progres.setNumOperacions(1);
        Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();
        procedimentHelper.actualitzarProcedimentFromGda(
                progres,
                procedimentGda,
                entitatEntity,
                codiOrgansGda,
                true,
                new ArrayList<OrganGestorEntity>(),
                avisosProcedimentsOrgans);

        // Then
    }

}