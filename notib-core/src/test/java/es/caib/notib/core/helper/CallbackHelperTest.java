package es.caib.notib.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.Statuses;
import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.AplicacioRepository;
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

    @InjectMocks
    private CallbackHelper callbackHelper;

    private AplicacioEntity aplicacio;

    private NotificacioEntity notificacioMock;
    private EntitatEntity entitatMock;
    private NotificacioEnviamentEntity enviamentMock;

    private final String MAX_INTENTS_CALLBACK = "10";

    @Before
    public void setUp() throws Exception {
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

        enviamentMock = NotificacioEnviamentEntity.getBuilder("",
                ServeiTipusEnumDto.NORMAL, notificacioMock).build();
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
    public void whenNotificaACallbackInactiu_ThenCallBackEstatIsPROCESSAT() {
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
    public void whenNotificaCorrecte_ThenCallBackEstatIsNOTIFICAT() {
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
    public void whenNotificaTipusEventIncorrecte_ThenCallBackEstatIsERROR() {
        // Given
        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .callbackIntents(0)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO)
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
    public void whenNotificaRaiseExceptionAndIntentsPendents_ThenCallBackEstatIsPENDENT() throws JsonProcessingException {
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
    public void whenNotificaRaiseExceptionAndIsLastIntent_ThenCallBackEstatIsERROR() throws JsonProcessingException {
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

}