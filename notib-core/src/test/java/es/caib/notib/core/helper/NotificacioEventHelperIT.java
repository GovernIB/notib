package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.service.BaseServiceTestV2;
import es.caib.notib.core.test.data.EntitatItemTest;
import es.caib.notib.core.test.data.NotificacioItemTest;
import es.caib.notib.core.test.data.ProcedimentItemTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioEventHelperIT extends BaseServiceTestV2 {


    private static final String ENTITAT_DGTIC_DIR3CODI = "EA0004518";
    private static final String ENTITAT_DGTIC_KEY = "MjkwNTc3Mjk0MjkyNTU3OTkyNA==";

    private static final int NUM_DESTINATARIS = 2;

    @Autowired
    private NotificacioEventHelper notificacioEventHelper;
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private NotificacioService notificacioService;

    @Autowired
    private NotificacioRepository notificacioRepository;
    @Autowired
    private NotificacioEventRepository notificacioEventRepository;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
//    @Mock
//    NotificaHelper notificaHelper;

//    @InjectMocks
//    NotificacioService notificacioService;


    EntitatDto entitatCreate;

    @Autowired
    private ProcedimentItemTest procedimentCreate;
    @Autowired
    private NotificacioItemTest notificacioCreate;


    @Before
    public void setup() throws Exception {
        configureMockGestioDocumentalPlugin();

        entitatCreate = EntitatItemTest.getRandomInstance();

//		organGestorCreate.setItemIdentifier("organGestor");

        procedimentCreate.addObject("procediment", procedimentCreate.getRandomInstance());

        notificacioCreate.addObject("notificacio", notificacioCreate.getRandomInstance());
        notificacioCreate.addRelated("notificacio", "procediment", procedimentCreate);

        notificacioCreate.addObject("notificacioError", notificacioCreate.getRandomInstance());
        notificacioCreate.addRelated("notificacioError", "procediment", procedimentCreate);
    }


    @Test
    public void whenClearOldEventsNotificacioWithLastError_thenAllEventsRemoved() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(ElementsCreats elementsCreats) throws Exception {
                        EntitatDto entitatCreate = elementsCreats.getEntitat();
                        ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
                        NotificacioDatabaseDto notificacioCreate = (NotificacioDatabaseDto) elementsCreats.get("notificacio");

                        assertNotNull(procedimentCreate);
                        assertNotNull(entitatCreate);
                        assertNotNull(notificacioCreate);

                        // Given: Una notificació amb un error associat a notificaError
                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreate.getId());
                        NotificacioEnviamentEntity env = notificacioEnviamentRepository.findByNotificacio(notificacioEntity).get(0);
                        notificacioEventHelper.addNotificaConsultaErrorEvent(notificacioEntity,
                                env);

//                        assertNotNull(notificacioEntity.getNotificaErrorEvent());

                        // When: clear all useless events
                        notificacioEventHelper.clearOldUselessEvents(notificacioEntity);

                        // Then
                        assertNull(env.getNotificacioErrorEvent());
//                        assertNull(notificacioEntity.getNotificaErrorEvent());
                        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacioEntity,
                                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR,
                                true);
                        assertTrue(events == null || events.size()== 0);

                    }
                },
                "Netejar events no útils",
                entitatCreate,
                procedimentCreate,
                notificacioCreate);

    }

    @Test
    public void whenClearOldEventsEnviamentWithLastError_thenAllEventsRemoved() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(ElementsCreats elementsCreats) throws Exception {
                        EntitatDto entitatCreate = elementsCreats.getEntitat();
                        ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
                        NotificacioDatabaseDto notificacioCreate = (NotificacioDatabaseDto) elementsCreats.get("notificacio");

                        assertNotNull(procedimentCreate);
                        assertNotNull(entitatCreate);
                        assertNotNull(notificacioCreate);

                        // Given: Una notificació amb un error associat a notificaError
                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreate.getId());
                        NotificacioEnviamentEntity env = notificacioEnviamentRepository.findByNotificacio(notificacioEntity).get(0);
                        notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
                                env, "");

                        assertNotNull(env.getNotificacioErrorEvent());

                        // When: clear all useless events
                        notificacioEventHelper.clearOldUselessEvents(notificacioEntity);

                        // Then
                        assertNull(env.getNotificacioErrorEvent());
//                        assertNull(notificacioEntity.getNotificaErrorEvent());
                        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacioEntity,
                                NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO,
                                true);
                        assertTrue(events == null || events.size()== 0);

                    }
                },
                "Netejar events no útils",
                entitatCreate,
                procedimentCreate,
                notificacioCreate);

    }

//    @Test
//    public void whenClearOldUselessEventsTest_thenAllEventsRemoved() {
//        testCreantElements(
//                new TestAmbElementsCreats() {
//                    @Override
//                    public void executar(ElementsCreats elementsCreats) throws Exception {
//                        List<NotificacioEventEntity> events;
//                        NotificacioEventTipusEnumDto tipus;
//                        autenticarUsuari("admin");
//
//                        // 1. Crear notificacio
//                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
//                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
//                        NotificacioDatabaseDto notificacioCreada = (NotificacioDatabaseDto)elementsCreats.get(3);
//                        assertNotNull(procedimentCreat);
//                        assertNotNull(entitatCreada);
//                        assertNotNull(notificacioCreada);
//
//                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());
//
//                        ////
//                        // 2. Crear events en la notificació
//                        ////
//                        notificacioEventHelper.addEnviamentRegistreOKEvent(notificacioEntity, "666", new Date(),
//                                NotificacioRegistreEstatEnumDto.OFICI_EXTERN, notificacioEntity.getEnviaments(), true);
//
//                        Map<NotificacioEnviamentEntity, String> identificadorsResultatsEnviaments = new HashMap<>();
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            identificadorsResultatsEnviaments.put(enviament, "identificador");
//                        }
//                        notificacioEventHelper.addEnviamentNotificaOKEvent(notificacioEntity, identificadorsResultatsEnviaments);
//
//                        // event notifica informa finalitzat
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addNotificaCallbackEvent(notificacioEntity, enviament,
//                                    NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
//                                    "datat resultado darrer");
//                        }
//
//                        // Events que s'haurien d'esborrar
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId());
//                        }
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addNotificaCallbackEvent(notificacioEntity, enviament,
//                                    NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
//                                    "Notifica callback datat");
//                        }
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addNotificaCallbackEvent(notificacioEntity, enviament,
//                                    NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
//                                    "anulada",
//                                    "Descripció de l'error",
//                                    true
//                            );
//                        }
//                        ////
//                        // 3. Comprovar que els events estan desats a la base de dades
//                        ////
//
//                        // events enviament registre ok
//                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(1, events.size());
//
//                        // events enviament notifica ok
//                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(1, events.size());
//
//                        // events notifica informa finalitzat
//                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(notificacioEntity.getEnviaments().size(), events.size());
//
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        assertEquals(notificacioEntity.getEnviaments().size(), events.size());
//
//                        ////
//                        // 4. Executar el mètode
//                        ////
//                        notificacioEventHelper.clearOldUselessEvents(notificacioEntity);
//
//                        ////
//                        // 5. Comprovar que els events que s'havien de borrar s'han borrat,
//                        // i que els que no s'havien de borrar encara hi son
//                        ////
//
//
//                        // events enviament registre ok
//                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(1, events.size());
//
//                        // events enviament notifica ok
//                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(1, events.size());
//
//                        // events notifica informa finalitzat
//                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(notificacioEntity.getEnviaments().size(), events.size());
//
//                        // events a esborrar
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        assertEquals(0, events.size());
//
//                        events = notificacioEventRepository.findByNotificacio(notificacioEntity);
//                        assertEquals(events.size(), 4);
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            enviament.setNotificaEstat(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT);
//                        }
//                    }
//                },
//                "Netejar events no útils",
//                entitat,
//                organGestor,
//                procediment,
//                notificacio);
//    }
//
////    @Test
//    public void addRegistreCallBackEstatEventTest() {
//        testCreantElements(
//                new TestAmbElementsCreats() {
//                    @Override
//                    public void executar(ElementsCreats elementsCreats) throws Exception {
//                        List<NotificacioEventEntity> events;
//                        NotificacioEventTipusEnumDto tipus;
//                        autenticarUsuari("admin");
//
//                        // 1. Crear notificacio
//                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
//                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
//                        NotificacioDatabaseDto notificacioCreada = (NotificacioDatabaseDto)elementsCreats.get(3);
//                        assertNotNull(procedimentCreat);
//                        assertNotNull(entitatCreada);
//                        assertNotNull(notificacioCreada);
//
//                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());
//
//                        ////
//                        // 2. Proves events amb errors
//                        ////
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId(), true);
//                        }
//                        NotificacioEventEntity primerError;
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        primerError = events.get(0);
//                        assertEquals(2, events.size());
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId(), true);
//                        }
//
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        assertEquals(2, events.size());
//                        assertEquals(primerError.getId(), events.get(0).getId());
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId(), true);
//                        }
//
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        assertEquals(2, events.size());
//                        assertEquals(primerError.getId(), events.get(0).getId());
//
//                        ////
//                        // 2. Proves events sense errors
//                        ////
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId(), false);
//                        }
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(1, events.size());
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId(), false);
//                        }
//
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(1, events.size());
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId(), false);
//                        }
//
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                false);
//                        assertEquals(1, events.size());
//                    }
//                },
//                "Únic event de consulta",
//                entitat,
//                organGestor,
//                procediment,
//                notificacio);
//    }
//
////    @Test
//    public void addRegistreConsultaInfoErrorEventTest() {
//        testCreantElements(
//                new TestAmbElementsCreats() {
//                    @Override
//                    public void executar(ElementsCreats elementsCreats) throws Exception {
//                        List<NotificacioEventEntity> events;
//                        NotificacioEventTipusEnumDto tipus;
//                        autenticarUsuari("admin");
//
//                        // 1. Crear notificacio
//                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
//                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
//                        NotificacioDtoV2 notificacioCreada = (NotificacioDtoV2)elementsCreats.get(3);
//                        assertNotNull(procedimentCreat);
//                        assertNotNull(entitatCreada);
//                        assertNotNull(notificacioCreada);
//
//                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());
//
//                        ////
//                        // 2. Crear events en la notificació
//                        ////
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId());
//                        }
//                        NotificacioEventEntity primerError;
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        primerError = events.get(0);
//                        assertEquals(2, events.size());
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId());
//                        }
//
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        assertEquals(2, events.size());
//                        assertEquals(primerError.getId(), events.get(0).getId());
//
//                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
//                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
//                                    enviament, "registre_consulta_info_" + enviament.getId());
//                        }
//
//                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
//                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
//                                notificacioEntity,
//                                tipus,
//                                true);
//                        assertEquals(2, events.size());
//                        assertEquals(primerError.getId(), events.get(0).getId());
//                    }
//                },
//                "Deixar primer i darrer error",
//                entitat,
//                organGestor,
//                procediment,
//                notificacio);
//    }

//    @Test
//    public void addErrorEventTest() {
//
//    }
//
//    @Test
//    public void addCallbackEventTest() {
//
//    }
//
//    @Test
//    public void addNotificaConsultaSirErrorEventTest() {
//
//    }
//
//    @Test
//    public void addEnviamentRegistreOKEventTest() {
//
//    }
//
//    @Test
//    public void addEnviamentNotificaOKEventTest() {
//
//    }
//
//    @Test
//    public void addNotificaCallbackEventTest() {
//
//    }
//
//    @Test
//    public void addNotificaConsultaInfoEventTest() {
//
//    }
//
//    @Test
//    public void addNotificaConsultaErrorEventTest() {
//
//    }
}