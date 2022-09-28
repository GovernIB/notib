package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDataDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.logic.test.data.EntitatItemTest;
import es.caib.notib.logic.test.data.NotificacioMassivaCreator;
import es.caib.notib.logic.test.data.ProcedimentItemTest;
import es.caib.notib.logic.utils.CSVReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
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
@ContextConfiguration(locations = {"/es/caib/notib/logic/application-context-test.xml"})
@Transactional
public class NotificacioMassivaServiceIT extends BaseServiceTestV2 {
    @Autowired
    private NotificacioMassivaRepository notificacioMassivaRepository;
    @Autowired
    private NotificacioRepository notificacioRepository;

    @Autowired
    private NotificacioMassivaService notificacioMassivaService;

    @Autowired
    private NotificacioMassivaCreator notificacioMassivaCreator;

    @Autowired
    private ProcedimentItemTest procedimentCreator;

    private ElementsCreats database;

    @Captor
    ArgumentCaptor<ByteArrayInputStream> fileContentCaptor = ArgumentCaptor.forClass(ByteArrayInputStream.class);

    @Before
    public void setUp() throws Exception {
        setDefaultConfigs();
        addConfig("es.caib.notib.metriques.generar", "false");
        addConfig("es.caib.notib.emprar.sir", "true");
        addConfig("es.caib.notib.plugin.registre.documents.enviar", "false");
        addConfig("es.caib.notib.tasca.registre.enviaments.periode", "60000");
        addConfig("es.caib.notib.notificacio.document.size", "10485760");
        addConfig("es.caib.notib.notificacio.document.total.size", "15728640");
        addConfig("es.caib.notib.document.metadades.por.defecto", "true");
        configureMockGestioDocumentalPlugin();
        configureMockUnitatsOrganitzativesPlugin();

        notificacioMassivaCreator.addObject("notMassiva_test1", NotificacioMassivaCreator.getTest1Instance());
        notificacioMassivaCreator.addObject("notMassiva_test2", NotificacioMassivaCreator.getTest2Instance());
        notificacioMassivaCreator.addObject("notMassiva_test3", NotificacioMassivaCreator.getTest3Instance());

        ProcSerDto procediment1 = procedimentCreator.getRandomInstance();
        procediment1.setCodi("101310");
        procedimentCreator.addObject("procediment", procediment1);

        database = createDatabase(EntitatItemTest.getRandomInstance(),
                procedimentCreator,
                notificacioMassivaCreator
                );

    }

    @After
    public final void tearDown() {
        destroyDatabase(database.getEntitat().getId(),
                notificacioMassivaCreator,
                procedimentCreator
        );
    }

    @Test
    public void whenCreate_thenValidateCSVContent() throws Exception {
        // Given
        String usuariCodi = "CODI_USER";
        NotificacioMassivaDto notificacioMassiva = NotificacioMassivaCreator.getTest2Instance();

        // When
        NotificacioMassivaDataDto massivaCreated = notificacioMassivaService.create(
                database.entitat.getId(), usuariCodi, notificacioMassiva);

        // Then
        // 4 vegades: 3 setup, 1 ara
        Mockito.verify(gestioDocumentalPluginMock, Mockito.times(4)).update(Mockito.anyString(), Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES),
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

        List<NotificacioEntity> notificacions = notificacioRepository.findByNotificacioMassivaEntityId(massivaCreated.getId());
        Assert.assertEquals(0, notificacions.size());

        notificacioMassivaRepository.deleteById(massivaCreated.getId());
    }

    @Test
    public void whenCreate_thenCreateValidIgnoreOthers() throws Exception {
        // Given
        String usuariCodi = "CODI_USER";
        NotificacioMassivaDto notificacioMassiva = NotificacioMassivaCreator.getTest3Instance();

        // When
        NotificacioMassivaDataDto massivaCreated = notificacioMassivaService.create(
                database.entitat.getId(), usuariCodi, notificacioMassiva);

        // Then
        // 4 vegades: 4 setup, 1 ara
        Mockito.verify(gestioDocumentalPluginMock, Mockito.times(4)).update(Mockito.anyString(), Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES),
                fileContentCaptor.capture());
        ByteArrayInputStream fileContent = fileContentCaptor.getValue();
        List<String[]> linies = CSVReader.readFile(IOUtils.toByteArray(fileContent));

        int correctCount = 0;
        int errorCount = 0;
        for (String [] fila : linies) {
            System.out.println(fila[22]);
            if (fila[22].trim().substring(0, 2).equals("OK")) {
                correctCount++;
            } else {
                errorCount ++;
            }
        }
        Assert.assertEquals(2, correctCount);
        Assert.assertEquals(2, errorCount);

        List<NotificacioEntity> notificacions = notificacioRepository.findByNotificacioMassivaEntityId(massivaCreated.getId());
        Assert.assertEquals(2, notificacions.size());

        for (NotificacioEntity notificacio: notificacions) {
            notificacioRepository.deleteById(notificacio.getId());
        }
        notificacioMassivaRepository.deleteById(massivaCreated.getId());
    }

    @Test
    public void whenPosposar_thenIncreaseAllNotMassivaNotificacionsRegistreData() throws Exception {
        // Given
        NotificacioMassivaDataDto nMassivaDto = (NotificacioMassivaDataDto) database.get("notMassiva_test3");
        List<NotificacioEntity> notificacionsPre = notificacioRepository.findByNotificacioMassivaEntityId(nMassivaDto.getId());
        Assert.assertEquals(2, notificacionsPre.size());
        Map<Long, Date> previousDates = new HashMap<>();
        for (NotificacioEntity n : notificacionsPre)
            previousDates.put(n.getId(), n.getNotificaEnviamentData());

        // When
        notificacioMassivaService.posposar(database.entitat.getId(), nMassivaDto.getId());

        // Then
        List<NotificacioEntity> notificacionsPost = notificacioRepository.findByNotificacioMassivaEntityId(nMassivaDto.getId());
        Assert.assertEquals(2, notificacionsPost.size());


        for (NotificacioEntity n : notificacionsPost) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(previousDates.get(n.getId()));
            cal.add(Calendar.SECOND, 8*60*60);
            Date expected = cal.getTime();
            Assert.assertEquals(DateUtils.truncate(expected, Calendar.MINUTE), DateUtils.truncate(n.getNotificaEnviamentData(), Calendar.MINUTE));
        }
    }

    @Test
    public void whenReactivar_thenResetAllNotMassivaNotificacionsRegistreData() throws Exception {
        // Given
        NotificacioMassivaDataDto nMassivaDto = (NotificacioMassivaDataDto) database.get("notMassiva_test3");
        List<NotificacioEntity> notificacionsPre = notificacioRepository.findByNotificacioMassivaEntityId(nMassivaDto.getId());
        Assert.assertEquals(2, notificacionsPre.size());
        Map<Long, Date> previousDates = new HashMap<>();
        for (NotificacioEntity n : notificacionsPre)
            previousDates.put(n.getId(), n.getNotificaEnviamentData());

        // When
        notificacioMassivaService.reactivar(database.entitat.getId(), nMassivaDto.getId());

        // Then
        List<NotificacioEntity> notificacionsPost = notificacioRepository.findByNotificacioMassivaEntityId(nMassivaDto.getId());
        Assert.assertEquals(2, notificacionsPost.size());


        for (NotificacioEntity n : notificacionsPost) {
            Calendar cal = GregorianCalendar.getInstance();
            Date expected = cal.getTime();
            Assert.assertEquals(DateUtils.truncate(expected, Calendar.MINUTE), DateUtils.truncate(n.getNotificaEnviamentData(), Calendar.MINUTE));
        }
    }
}
