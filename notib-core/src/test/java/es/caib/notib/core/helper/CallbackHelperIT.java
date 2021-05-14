package es.caib.notib.core.helper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.*;
import es.caib.notib.core.service.BaseServiceTestV2;
import es.caib.notib.core.test.data.EntitatItemTest;
import es.caib.notib.core.test.data.NotificacioItemTest;
import es.caib.notib.core.test.data.ProcedimentItemTest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
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
        aplicacioRepository.delete(aplicacio.getId());
        destroyDatabase(database.getEntitat().getId(),
                notificacioCreate
        );
    }

    @Test
    public void whenNotificaCorrecte_thenEstatIsNotifica() throws Exception {
        // Given
        NotificacioDatabaseDto notificacioDto = (NotificacioDatabaseDto) database.get("notificacio");
        NotificacioEnviamentDtoV2 enviamentDto = notificacioDto.getEnviaments().get(0);
        NotificacioEnviamentEntity enviament = enviamentRepository.getOne(enviamentDto.getId());
        NotificacioEntity notificacio = notificacioRepository.getOne(notificacioDto.getId());
        NotificacioEventEntity eventNotificar = NotificacioEventEntity.builder()
                .callbackIntents(0)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviament)
                .notificacio(notificacio)
                .build();
        eventRepository.saveAndFlush(eventNotificar);

        // Comprovam que l'enviament té un, i només un, event associat
        Assert.assertEquals(1, eventRepository.findByEnviamentIdOrderByIdAsc(enviament.getId()).size());

        // Comprovam que no hi ha cap event de callback client
        List<NotificacioEventEntity> eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacio,
                NotificacioEventTipusEnumDto.CALLBACK_CLIENT, false);
        Assert.assertEquals(0, eventsCallback.size());

        // When
        callbackHelper.notifica(eventNotificar);

        // Then
        eventNotificar = eventRepository.findOne(eventNotificar.getId());
        Assert.assertEquals(CallbackEstatEnumDto.NOTIFICAT, eventNotificar.getCallbackEstat());

        // Verificam que s'ha asociat un event i no és d'error
        eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacio,
                NotificacioEventTipusEnumDto.CALLBACK_CLIENT, false);
        Assert.assertEquals(1, eventsCallback.size());

    }

    @Test
    public void whenCallbackResponse404_thenAddCallbackErrorEvent() throws Exception {
        // Given
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/notificaCanvi"))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));
        NotificacioDatabaseDto notificacioDto = (NotificacioDatabaseDto) database.get("notificacio");
        NotificacioEnviamentDtoV2 enviamentDto = notificacioDto.getEnviaments().get(0);
        NotificacioEnviamentEntity enviament = enviamentRepository.getOne(enviamentDto.getId());
        NotificacioEntity notificacio = notificacioRepository.getOne(notificacioDto.getId());
        NotificacioEventEntity eventNotificar = NotificacioEventEntity.builder()
                .callbackIntents(0)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviament)
                .notificacio(notificacio)
                .build();
        NotificacioEventEntity eventNotificarDarrerIntent = NotificacioEventEntity.builder()
                .callbackIntents(Integer.parseInt(MAX_INTENTS_CALLBACK)-1)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviament)
                .notificacio(notificacio)
                .build();
        eventRepository.saveAndFlush(eventNotificar);
        eventRepository.saveAndFlush(eventNotificarDarrerIntent);

        // Comprovam que l'enviament té dos, i només dos, event associats (els que acaban de crear)
        Assert.assertEquals(2, eventRepository.findByEnviamentIdOrderByIdAsc(enviament.getId()).size());

        // Comprovam que no hi ha cap event de callback client
        List<NotificacioEventEntity> eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacio,
                NotificacioEventTipusEnumDto.CALLBACK_CLIENT, false);
        Assert.assertEquals(0, eventsCallback.size());

        // When
        callbackHelper.notifica(eventNotificar);
        callbackHelper.notifica(eventNotificarDarrerIntent);

        // Then
        eventNotificar = eventRepository.findOne(eventNotificar.getId());
        Assert.assertEquals(CallbackEstatEnumDto.PENDENT, eventNotificar.getCallbackEstat());

        eventNotificarDarrerIntent = eventRepository.findOne(eventNotificarDarrerIntent.getId());
        Assert.assertEquals(CallbackEstatEnumDto.ERROR, eventNotificarDarrerIntent.getCallbackEstat());

        // Verificam que s'ha asociat un event i que és d'error
        eventsCallback = eventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacio,
                NotificacioEventTipusEnumDto.CALLBACK_CLIENT, true);
        Assert.assertEquals(1, eventsCallback.size());

    }

}
