package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.helper.CallbackHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.repository.NotificacioEventRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class CallbackServiceImplTest {

    @Mock
    private NotificacioEventRepository notificacioEventRepository;
    @Mock
    private CallbackHelper callbackHelper;
    @Mock
    private MetricsHelper metricsHelper;

    @InjectMocks
    private CallbackServiceImpl callbackService;


    private Map<Long, NotificacioEventEntity> eventsMap;

    @Before
    public void setUp() throws Exception {
        EntitatEntity entitatMock = Mockito.mock(EntitatEntity.class);
        Mockito.when(entitatMock.getId()).thenReturn(2L);

        NotificacioEntity notificacioMock = Mockito.mock(NotificacioEntity.class);
        Mockito.when(notificacioMock.getEntitat()).thenReturn(entitatMock);

        List<Long> eventsIds = Arrays.asList(1L, 2L, 3L);
//        notificacioEventRepository = Mockito.mock(NotificacioEventRepository.class);
        Mockito.when(
                notificacioEventRepository.findEventsAmbCallbackPendentIds(Mockito.any(Pageable.class))
        ).thenReturn(eventsIds);

        UsuariEntity mockUser = Mockito.mock(UsuariEntity.class);
        Mockito.when(mockUser.getCodi()).thenReturn("CODI_USER");

        NotificacioEnviamentEntity enviamentMock = NotificacioEnviamentEntity.getBuilder("",
                ServeiTipusEnumDto.NORMAL, notificacioMock).build();
        enviamentMock.setCreatedBy(mockUser);

        eventsMap = new HashMap<>();
        for (Long eventId : eventsIds) {
            NotificacioEventEntity eventMock = Mockito.mock(NotificacioEventEntity.class);
            Mockito.when(
                    eventMock.getId()
            ).thenReturn(eventId);
            eventsMap.put(eventId, eventMock);
            Mockito.when(
                    notificacioEventRepository.findOne(Mockito.eq(eventId))
            ).thenReturn(eventMock);
            Mockito.when(
                    callbackHelper.notifica(Mockito.eq(eventMock))
            ).thenReturn(notificacioMock);
        }


    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void whenNotificaRaiseExeption_thenCallMarcarEventNoProcessable() throws Exception {
        // Given
        Mockito.when(
                callbackHelper.notifica(Mockito.eq(eventsMap.get(1L)))
        ).thenThrow(Exception.class);
        Mockito.when(
                callbackHelper.notifica(Mockito.eq(eventsMap.get(2L)))
        ).thenThrow(Exception.class);

        // When
        callbackService.processarPendents();

        // Then
        Mockito.verify(callbackHelper, Mockito.times(2)).marcarEventNoProcessable(
                Mockito.any(NotificacioEventEntity.class),
                Mockito.nullable(String.class),
                Mockito.anyString()
        );

    }
}