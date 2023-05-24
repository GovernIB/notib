package es.caib.notib.logic.helper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import es.caib.notib.logic.service.BaseServiceTestV2;
import es.caib.notib.logic.test.data.EntitatItemTest;
import es.caib.notib.logic.test.data.NotificacioItemTest;
import es.caib.notib.logic.test.data.ProcedimentItemTest;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/logic/application-context-test.xml"})
@Transactional
public class CallbackHelperIT extends BaseServiceTestV2 {
    @Autowired
    NotificacioRepository notificacioRepository;
    @Autowired
    NotificacioEnviamentRepository enviamentRepository;
    @Autowired
    NotificacioEventRepository eventRepository;
    @Autowired
    EntitatRepository entitatRepository;
    @Autowired
    private AplicacioRepository aplicacioRepository;

    @Autowired
    private CallbackHelper callbackHelper;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().port(8080),
            false);

    @Autowired
    private ProcedimentItemTest procedimentCreate;
    @Autowired
    private NotificacioItemTest notificacioCreate;

    private ElementsCreats database;
    private AplicacioEntity aplicacio;

    private final String MAX_INTENTS_CALLBACK = "10";
    private final static String CALLBACK_URL = "http://localhost:8080/";

    @Before
    public void setUp() throws Exception {
        setDefaultConfigs();
        configureMockGestioDocumentalPlugin();

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/notificaCanvi"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));


        procedimentCreate.addObject("procediment", procedimentCreate.getRandomInstance());

        notificacioCreate.addObject("notificacio", NotificacioItemTest.getRandomInstance());
        notificacioCreate.addRelated("notificacio", "procediment", procedimentCreate);

        notificacioCreate.addObject("notificacioError", NotificacioItemTest.getRandomInstance());
        notificacioCreate.addRelated("notificacioError", "procediment", procedimentCreate);

        database = createDatabase(EntitatItemTest.getRandomInstance(),
                notificacioCreate
                );

        aplicacio = AplicacioEntity.builder()
                .usuariCodi("admin")
                .callbackUrl(CALLBACK_URL)
                .activa(true)
                .entitat(entitatRepository.getOne(database.getEntitat().getId()))
                .build();
        aplicacioRepository.saveAndFlush(aplicacio);

        System.setProperty("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max", MAX_INTENTS_CALLBACK);
    }

    @After
    public final void tearDown() {
        removeAllConfigs();
        aplicacioRepository.deleteById(aplicacio.getId());
        destroyDatabase(database.getEntitat().getId(),
                notificacioCreate
        );
    }

    // TODO:

//    @Test
//    public void whenNotificaCorrecte_thenEstatIsNotifica() throws Exception {
//        // Given
//        NotificacioDatabaseDto notificacioDto = (NotificacioDatabaseDto) database.get("notificacio");
//        NotEnviamentDatabaseDto enviamentDto = notificacioDto.getEnviaments().get(0);
//        NotificacioEnviamentEntity enviament = enviamentRepository.getOne(enviamentDto.getId());
//        NotificacioEntity notificacio = notificacioRepository.getOne(notificacioDto.getId());
//        NotificacioEventEntity eventNotificar = NotificacioEventEntity.builder()
//                .callbackIntents(0)
//                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
//                .enviament(enviament)
//                .notificacio(notificacio)
//                .build();
//        eventRepository.saveAndFlush(eventNotificar);
//
//        // Comprovam que l'enviament té un, i només un, event associat
//        Assert.assertEquals(1, eventRepository.findByEnviamentIdOrderByIdAsc(enviament.getId()).size());
//
//        // Comprovam que no hi ha cap event de callback client
//        List<NotificacioEventEntity> eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(notificacio,
//                NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT, false);
//        Assert.assertEquals(0, eventsCallback.size());
//
//        // When
//        callbackHelper.notifica(enviament);
//
//        // Then
//        eventNotificar = eventRepository.findOne(eventNotificar.getId());
//        Assert.assertEquals(CallbackEstatEnumDto.NOTIFICAT, eventNotificar.getCallbackEstat());
//
//        // Verificam que s'ha asociat un event i no és d'error
//        eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(notificacio,
//                NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT, false);
//        Assert.assertEquals(1, eventsCallback.size());
//
//    }

//    @Test
//    public void whenCallbackResponse404_thenAddCallbackErrorEvent() throws Exception {
//        // Given
//        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/notificaCanvi"))
//                .willReturn(WireMock.aResponse()
//                        .withStatus(404)
//                        .withHeader("Content-Type", "text/xml")
//                        .withBody("<response>Some content</response>")));
//        NotificacioDatabaseDto notificacioDto = (NotificacioDatabaseDto) database.get("notificacio");
//        NotEnviamentDatabaseDto enviamentDto = notificacioDto.getEnviaments().get(0);
//        NotificacioEnviamentEntity enviament = enviamentRepository.getOne(enviamentDto.getId());
//        NotificacioEntity notificacio = notificacioRepository.getOne(notificacioDto.getId());
//        NotificacioEventEntity eventNotificar = NotificacioEventEntity.builder()
//                .callbackIntents(0)
//                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
//                .enviament(enviament)
//                .notificacio(notificacio)
//                .build();
//        NotificacioEventEntity eventNotificarDarrerIntent = NotificacioEventEntity.builder()
//                .callbackIntents(Integer.parseInt(MAX_INTENTS_CALLBACK)-1)
//                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
//                .enviament(enviament)
//                .notificacio(notificacio)
//                .build();
//        eventRepository.saveAndFlush(eventNotificar);
//        eventRepository.saveAndFlush(eventNotificarDarrerIntent);
//
//        // Comprovam que l'enviament té dos, i només dos, event associats (els que acaban de crear)
//        Assert.assertEquals(2, eventRepository.findByEnviamentIdOrderByIdAsc(enviament.getId()).size());
//
//        // Comprovam que no hi ha cap event de callback client
//        List<NotificacioEventEntity> eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(notificacio,
//                NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT, false);
//        Assert.assertEquals(0, eventsCallback.size());
//
//        // When
//        callbackHelper.notifica(enviament);
//
//        // Then
//        eventNotificar = eventRepository.findOne(eventNotificar.getId());
//        Assert.assertEquals(CallbackEstatEnumDto.PENDENT, eventNotificar.getCallbackEstat());
//
//        eventNotificarDarrerIntent = eventRepository.findOne(eventNotificarDarrerIntent.getId());
//        Assert.assertEquals(CallbackEstatEnumDto.ERROR, eventNotificarDarrerIntent.getCallbackEstat());
//
//        // Verificam que s'ha asociat un event i que és d'error
//        eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(notificacio,
//                NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT, true);
//        Assert.assertEquals(1, eventsCallback.size());
//
//    }

}
