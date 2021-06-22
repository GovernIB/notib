package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaDataDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.core.api.service.NotificacioMassivaService;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.NotificacioMassivaRepository;
import es.caib.notib.core.test.NotificacioMassivaTests;
import es.caib.notib.core.test.data.EntitatItemTest;
import es.caib.notib.core.test.data.ProcedimentItemTest;
import es.caib.notib.core.utils.CSVReader;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioMassivaServiceIT extends BaseServiceTestV2 {
    @Autowired
    private NotificacioMassivaRepository notificacioMassivaRepository;


    @Autowired
    private NotificacioMassivaService notificacioMassivaService;

    @Autowired
    private ProcedimentItemTest procedimentCreate;

    private ElementsCreats database;

    @Captor
    ArgumentCaptor<ByteArrayInputStream> fileContentCaptor = ArgumentCaptor.forClass(ByteArrayInputStream.class);

    @Before
    public void setUp() throws Exception {
        configureMockGestioDocumentalPlugin();
        configureMockUnitatsOrganitzativesPlugin();


        ProcedimentDto procediment1 = procedimentCreate.getRandomInstance();
        procediment1.setCodi("101310");
        procedimentCreate.addObject("procediment", procediment1);
//
//        notificacioCreate.addObject("notificacio", NotificacioItemTest.getRandomInstance());
//        notificacioCreate.addRelated("notificacio", "procediment", procedimentCreate);
//
//        notificacioCreate.addObject("notificacioError", NotificacioItemTest.getRandomInstance());
//        notificacioCreate.addRelated("notificacioError", "procediment", procedimentCreate);

        database = createDatabase(EntitatItemTest.getRandomInstance(),
                procedimentCreate
                );


        Map<String, NodeDir3> organigramaEntitat = new HashMap<>();
        organigramaEntitat.put("E04975701", new NodeDir3());
        Mockito.when(unitatsOrganitzativesPluginMock.organigramaPerEntitat(Mockito.anyString()))
                .thenReturn(organigramaEntitat);
    }

    @After
    public final void tearDown() {
        destroyDatabase(database.getEntitat().getId(),
                procedimentCreate
        );
    }

    @Test
    public void whenCreate_thenValidateCSVContent() throws Exception {
        // Given
        String usuariCodi = "CODI_USER";
        NotificacioMassivaTests.TestMassiusFiles test1Data = NotificacioMassivaTests.getTest2Files();
        NotificacioMassivaDto notificacioMassiva = NotificacioMassivaDto.builder()
                .ficheroCsvNom("csv_test.csv")
                .ficheroZipNom("zip_test.zip")
                .ficheroCsvBytes(test1Data.getCsvContent())
                .ficheroZipBytes(test1Data.getZipContent())
                .caducitat(new Date())
                .email("test@email.com")
                .build();

        // When
        NotificacioMassivaDataDto massivaCreated = notificacioMassivaService.create(
                database.entitat.getId(), usuariCodi, notificacioMassiva);

        // Then
        Mockito.verify(gestioDocumentalPluginMock).update(Mockito.anyString(), Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES),
                fileContentCaptor.capture());
        ByteArrayInputStream fileContent = fileContentCaptor.getValue();
        List<String[]> linies = CSVReader.readFile(IOUtils.toByteArray(fileContent));

        List<String> errorsEsperats = Arrays.asList(
//                "1000", "1001", "1010", "1011",  -- Aquests errors no es poden provar, són de l'entitat
                "1020", "1020", "1330", "1021",
                "1030",
                "1031",
                "1050",
                "1032",
                "1060",
//                "1072",
//                "1070", "1071", -- provar a part, valor comú en totes les notificacions
//                "1100", -- mai es null
//                "1101",
//                "1110",
//                "1111",
                "1112",
                "1113",
//                "1114",
                "1115",
                "1023"
        );

        int i = 0;
        for (String [] fila : linies) {
            Assert.assertNotEquals(
                    "No s'ha trobat l'error de la fila " + (i+2) + " s'esperava: " + errorsEsperats.get(i),
                    fila[22], "OK");
            String errorCode = fila[22].substring(1, 5);
            Assert.assertEquals(errorsEsperats.get(i), errorCode);
            System.out.println(errorCode);
            i++;
        }

        notificacioMassivaRepository.delete(massivaCreated.getId());
    }

}
