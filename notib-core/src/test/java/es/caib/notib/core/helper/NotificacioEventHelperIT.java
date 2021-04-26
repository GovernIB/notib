package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.service.BaseServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioEventHelperIT extends BaseServiceTest {


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

//        configureMockRegistrePlugin();
//        configureMockUnitatsOrganitzativesPlugin();
//        configureMockDadesUsuariPlugin();

        notificacio = generarNotificacio(
                new Long(System.currentTimeMillis()).toString(),
                procediment,
                entitat,
                ENTITAT_DGTIC_DIR3CODI,
                NUM_DESTINATARIS,
                false);

        System.setProperty("es.caib.notib.plugin.gesdoc.filesystem.base.dir", "/home/bgalmes/dades/notib-fs/");
    }


    @Test
    public void whenClearOldEventsNotificacioWithLastError_thenAllEventsRemoved() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(List<Object> elementsCreats) throws Exception {
                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
                        NotificacioDtoV2 notificacioCreada = (NotificacioDtoV2)elementsCreats.get(3);
                        assertNotNull(procedimentCreat);
                        assertNotNull(entitatCreada);
                        assertNotNull(notificacioCreada);

                        // Given: Una notificació amb un error associat a notificaError
                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());
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
                entitat,
                organGestor,
                procediment,
                notificacio);

    }

    @Test
    public void whenClearOldEventsEnviamentWithLastError_thenAllEventsRemoved() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(List<Object> elementsCreats) throws Exception {
                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
                        NotificacioDtoV2 notificacioCreada = (NotificacioDtoV2)elementsCreats.get(3);
                        assertNotNull(procedimentCreat);
                        assertNotNull(entitatCreada);
                        assertNotNull(notificacioCreada);

                        // Given: Una notificació amb un error associat a notificaError
                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());
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
                entitat,
                organGestor,
                procediment,
                notificacio);

    }

//    @Test
    public void whenClearOldUselessEventsTest_thenAllEventsRemoved() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(List<Object> elementsCreats) throws Exception {
                        List<NotificacioEventEntity> events;
                        NotificacioEventTipusEnumDto tipus;
                        autenticarUsuari("admin");

                        // 1. Crear notificacio
                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
                        NotificacioDtoV2 notificacioCreada = (NotificacioDtoV2)elementsCreats.get(3);
                        assertNotNull(procedimentCreat);
                        assertNotNull(entitatCreada);
                        assertNotNull(notificacioCreada);

                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());

                        ////
                        // 2. Crear events en la notificació
                        ////
                        notificacioEventHelper.addEnviamentRegistreOKEvent(notificacioEntity, "666", new Date(),
                                NotificacioRegistreEstatEnumDto.OFICI_EXTERN, notificacioEntity.getEnviaments(), true);

                        Map<NotificacioEnviamentEntity, String> identificadorsResultatsEnviaments = new HashMap<>();
                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            identificadorsResultatsEnviaments.put(enviament, "identificador");
                        }
                        notificacioEventHelper.addEnviamentNotificaOKEvent(notificacioEntity, identificadorsResultatsEnviaments);

                        // event notifica informa finalitzat
                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addNotificaCallbackEvent(notificacioEntity, enviament,
                                    NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
                                    "datat resultado darrer");
                        }

                        // Events que s'haurien d'esborrar
                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId());
                        }

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addNotificaCallbackEvent(notificacioEntity, enviament,
                                    NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
                                    "Notifica callback datat");
                        }

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addNotificaCallbackEvent(notificacioEntity, enviament,
                                    NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
                                    "anulada",
                                    "Descripció de l'error",
                                    true
                            );
                        }
                        ////
                        // 3. Comprovar que els events estan desats a la base de dades
                        ////

                        // events enviament registre ok
                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(1, events.size());

                        // events enviament notifica ok
                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(1, events.size());

                        // events notifica informa finalitzat
                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(notificacioEntity.getEnviaments().size(), events.size());

                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        assertEquals(notificacioEntity.getEnviaments().size(), events.size());

                        ////
                        // 4. Executar el mètode
                        ////
                        notificacioEventHelper.clearOldUselessEvents(notificacioEntity);

                        ////
                        // 5. Comprovar que els events que s'havien de borrar s'han borrat,
                        // i que els que no s'havien de borrar encara hi son
                        ////


                        // events enviament registre ok
                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(1, events.size());

                        // events enviament notifica ok
                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(1, events.size());

                        // events notifica informa finalitzat
                        tipus = NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(notificacioEntity.getEnviaments().size(), events.size());

                        // events a esborrar
                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        assertEquals(0, events.size());

                        events = notificacioEventRepository.findByNotificacio(notificacioEntity);
                        assertEquals(events.size(), 4);

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            enviament.setNotificaEstat(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT);
                        }
                    }
                },
                "Netejar events no útils",
                entitat,
                organGestor,
                procediment,
                notificacio);
    }

//    @Test
    public void addRegistreCallBackEstatEventTest() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(List<Object> elementsCreats) throws Exception {
                        List<NotificacioEventEntity> events;
                        NotificacioEventTipusEnumDto tipus;
                        autenticarUsuari("admin");

                        // 1. Crear notificacio
                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
                        NotificacioDtoV2 notificacioCreada = (NotificacioDtoV2)elementsCreats.get(3);
                        assertNotNull(procedimentCreat);
                        assertNotNull(entitatCreada);
                        assertNotNull(notificacioCreada);

                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());

                        ////
                        // 2. Proves events amb errors
                        ////

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId(), true);
                        }
                        NotificacioEventEntity primerError;
                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        primerError = events.get(0);
                        assertEquals(2, events.size());

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId(), true);
                        }

                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        assertEquals(2, events.size());
                        assertEquals(primerError.getId(), events.get(0).getId());

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId(), true);
                        }

                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        assertEquals(2, events.size());
                        assertEquals(primerError.getId(), events.get(0).getId());

                        ////
                        // 2. Proves events sense errors
                        ////
                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId(), false);
                        }
                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(1, events.size());

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId(), false);
                        }

                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(1, events.size());

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreCallBackEstatEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId(), false);
                        }

                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                false);
                        assertEquals(1, events.size());
                    }
                },
                "Únic event de consulta",
                entitat,
                organGestor,
                procediment,
                notificacio);
    }

//    @Test
    public void addRegistreConsultaInfoErrorEventTest() {
        testCreantElements(
                new TestAmbElementsCreats() {
                    @Override
                    public void executar(List<Object> elementsCreats) throws Exception {
                        List<NotificacioEventEntity> events;
                        NotificacioEventTipusEnumDto tipus;
                        autenticarUsuari("admin");

                        // 1. Crear notificacio
                        EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
                        ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(2);
                        NotificacioDtoV2 notificacioCreada = (NotificacioDtoV2)elementsCreats.get(3);
                        assertNotNull(procedimentCreat);
                        assertNotNull(entitatCreada);
                        assertNotNull(notificacioCreada);

                        NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreada.getId());

                        ////
                        // 2. Crear events en la notificació
                        ////

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId());
                        }
                        NotificacioEventEntity primerError;
                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        primerError = events.get(0);
                        assertEquals(2, events.size());

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId());
                        }

                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        assertEquals(2, events.size());
                        assertEquals(primerError.getId(), events.get(0).getId());

                        for (NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
                            notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacioEntity,
                                    enviament, "registre_consulta_info_" + enviament.getId());
                        }

                        tipus = NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO;
                        events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(
                                notificacioEntity,
                                tipus,
                                true);
                        assertEquals(2, events.size());
                        assertEquals(primerError.getId(), events.get(0).getId());
                    }
                },
                "Deixar primer i darrer error",
                entitat,
                organGestor,
                procediment,
                notificacio);
    }

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