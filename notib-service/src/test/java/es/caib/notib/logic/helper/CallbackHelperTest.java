package es.caib.notib.logic.helper;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.Statuses;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.notib.logic.intf.ws.callback.NotificacioCanviClient;
import es.caib.notib.persist.entity.*;
import es.caib.notib.persist.repository.AplicacioRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallbackHelperTest {
    @Mock
    private AplicacioRepository aplicacioRepository;
    @Mock
    private NotificaHelper notificaHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private AuditNotificacioHelper auditNotificacioHelper;
    @Mock
    private NotificacioEventHelper notificacioEventHelper;
    @Mock
    private RequestsHelper requestsHelper;
    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private CallbackHelper callbackHelper;

    private AplicacioEntity aplicacio;

    private NotificacioEntity notificacioMock;
    private EntitatEntity entitatMock;
    private NotificacioEnviamentEntity enviamentMock;

    private final String MAX_INTENTS_CALLBACK = "10";

    @Before
    public void setUp() throws Exception {
        Mockito.when(configHelper.getConfigAsInteger(Mockito.eq("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max"))).thenReturn(3);

        entitatMock = Mockito.mock(EntitatEntity.class);
        Mockito.when(entitatMock.getId()).thenReturn(2L);

        aplicacio = AplicacioEntity.builder()
                .usuariCodi("")
                .callbackUrl("")
                .activa(true)
                .entitat(entitatMock)
                .build();

        notificacioMock =  Mockito.mock(NotificacioEntity.class);
        Mockito.when(notificacioMock.getEntitat()).thenReturn(entitatMock);

        UsuariEntity mockUser = Mockito.mock(UsuariEntity.class);
        Mockito.when(mockUser.getCodi()).thenReturn("CODI_USER");

        enviamentMock = NotificacioEnviamentEntity.builder()
                .serveiTipus(ServeiTipusEnumDto.NORMAL)
                .notificacio(notificacioMock)
                .build();
        enviamentMock.setCreatedBy(mockUser);
        Mockito.when(aplicacioRepository.findByUsuariCodiAndEntitatId(Mockito.anyString(),
                Mockito.anyLong())).thenReturn(aplicacio);

        ClientResponse responseMock = Mockito.mock(ClientResponse.class);
        Mockito.when(responseMock.getStatusInfo()).thenReturn(Statuses.from(200, "OK"));

        Mockito.when(requestsHelper.callbackAplicacioNotificaCanvi(
                Mockito.anyString(), Mockito.any(NotificacioCanviClient.class))
        ).thenReturn(responseMock);

        System.setProperty("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max", MAX_INTENTS_CALLBACK);
    }

    @Test
    public void whenNotificaACallbackInactiu_ThenCallBackEstatIsPROCESSAT() throws Exception {
        // Given
        aplicacio.updateActiva(false);

        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .callbackIntents(0)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviamentMock)
                .notificacio(notificacioMock)
                .build();

        // When
        callbackHelper.notifica(event);

        // Then
        Assert.assertEquals(CallbackEstatEnumDto.PROCESSAT, event.getCallbackEstat());

        // Verificam que no s'ha asociat cap event
        Mockito.verify(notificacioEventHelper, Mockito.times(0)).addCallbackEvent(
                Mockito.any(NotificacioEntity.class),
                Mockito.eq(event),
                Mockito.anyBoolean()
                );
    }

    @Test
    public void whenNotificaCorrecte_ThenCallBackEstatIsNOTIFICAT() throws Exception {
        // Given
        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .callbackIntents(0)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviamentMock)
                .notificacio(notificacioMock)
                .build();

        // When
        callbackHelper.notifica(event);

        // Then
        Assert.assertEquals(CallbackEstatEnumDto.NOTIFICAT, event.getCallbackEstat());

        // Verificam que s'ha asociat un event i no és d'error
        Mockito.verify(notificacioEventHelper).addCallbackEvent(
                Mockito.any(NotificacioEntity.class),
                Mockito.eq(event),
                Mockito.eq(false)
        );

    }

    @Test
    public void whenNotificaTipusEventIncorrecte_ThenCallBackEstatIsERROR() throws Exception {
        // Given
        var event = NotificacioEventEntity.builder().callbackIntents(0).tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO)
                    .enviament(enviamentMock).notificacio(notificacioMock).build();
        // When
        callbackHelper.notifica(event);
        // Then
        Assert.assertEquals(CallbackEstatEnumDto.ERROR, event.getCallbackEstat());
        // Verificam que s'ha asociat un event i que és d'error
        Mockito.verify(notificacioEventHelper).addCallbackEvent(Mockito.any(NotificacioEntity.class), Mockito.eq(event), Mockito.eq(true));
    }

    @Test
    public void whenNotificaRaiseExceptionAndIntentsPendents_ThenCallBackEstatIsPENDENT() throws Exception {
        // Given
        ClientResponse responseMock = Mockito.mock(ClientResponse.class);
        Mockito.when(responseMock.getStatusInfo()).thenReturn(Statuses.from(404, "Not found"));
        Mockito.when(requestsHelper.callbackAplicacioNotificaCanvi(
                Mockito.anyString(), Mockito.any(NotificacioCanviClient.class))
        ).thenReturn(responseMock);

        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .callbackIntents(0)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviamentMock)
                .notificacio(notificacioMock)
                .build();

        // When
        callbackHelper.notifica(event);

        // Then
        Assert.assertEquals(CallbackEstatEnumDto.PENDENT, event.getCallbackEstat());

        // Verificam que s'ha asociat un event i que és d'error
        Mockito.verify(notificacioEventHelper).addCallbackEvent(
                Mockito.any(NotificacioEntity.class),
                Mockito.eq(event),
                Mockito.eq(true)
        );
    }

    @Test
    public void whenNotificaRaiseExceptionAndIsLastIntent_ThenCallBackEstatIsERROR() throws Exception {
        // Given
        ClientResponse responseMock = Mockito.mock(ClientResponse.class);
        Mockito.when(responseMock.getStatusInfo()).thenReturn(Statuses.from(404, "Not found"));
        Mockito.when(requestsHelper.callbackAplicacioNotificaCanvi(
                Mockito.anyString(), Mockito.any(NotificacioCanviClient.class))
        ).thenReturn(responseMock);

        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .callbackIntents(Integer.parseInt(MAX_INTENTS_CALLBACK)-1)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviamentMock)
                .notificacio(notificacioMock)
                .build();

        // When
        callbackHelper.notifica(event);

        // Then
        Assert.assertEquals(CallbackEstatEnumDto.ERROR, event.getCallbackEstat());

        // Verificam que s'ha asociat un event i que és d'error
        Mockito.verify(notificacioEventHelper).addCallbackEvent(
                Mockito.any(NotificacioEntity.class),
                Mockito.eq(event),
                Mockito.eq(true)
        );
    }

    @Test
    public void whenAplicacioNull_thenAddErrorIntegracioAndRaiseException() throws Exception {
        // Given
        Mockito.when(aplicacioRepository.findByUsuariCodiAndEntitatId(Mockito.anyString(),
                Mockito.anyLong())).thenReturn(null);

        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .callbackIntents(Integer.parseInt(MAX_INTENTS_CALLBACK)-1)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviamentMock)
                .notificacio(notificacioMock)
                .build();

        // When
        try {
            callbackHelper.notifica(event);
        }catch (Exception e) {
            Assert.assertTrue(true);
        }
        // Then
        Assert.assertEquals(CallbackEstatEnumDto.ERROR, event.getCallbackEstat());

        // Verificam que s'ha asociat un event i que és d'error
        Mockito.verify(notificacioEventHelper).addCallbackEvent(
                Mockito.any(NotificacioEntity.class),
                Mockito.eq(event),
                Mockito.eq(true)
        );

        Mockito.verify(integracioHelper).addAccioError(
                Mockito.any(IntegracioInfo.class),
                Mockito.anyString()
        );
    }
    @Test
    public void whenAplicacioCallbackUrlNull_thenAddErrorIntegracioAndRaiseException() throws Exception {
        // Given
       aplicacio.update("", null);

        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .callbackIntents(Integer.parseInt(MAX_INTENTS_CALLBACK)-1)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .enviament(enviamentMock)
                .notificacio(notificacioMock)
                .build();

        // When
        try {
            callbackHelper.notifica(event);
        }catch (Exception e) {
            Assert.assertTrue(true);
        }
        // Then
        Assert.assertEquals(CallbackEstatEnumDto.ERROR, event.getCallbackEstat());

        // Verificam que s'ha asociat un event i que és d'error
        Mockito.verify(notificacioEventHelper).addCallbackEvent(
                Mockito.any(NotificacioEntity.class),
                Mockito.eq(event),
                Mockito.eq(true)
        );

        Mockito.verify(integracioHelper).addAccioError(
                Mockito.any(IntegracioInfo.class),
                Mockito.anyString()
        );
    }
}