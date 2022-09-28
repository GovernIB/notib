package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.persist.repository.NotificacioEventRepository;
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
    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private CallbackServiceImpl callbackService;


    private Map<Long, NotificacioEventEntity> eventsMap;

    @Before
    public void setUp() throws Exception {
//        EntitatEntity entitatMock = Mockito.mock(EntitatEntity.class);
//        Mockito.when(entitatMock.getId()).thenReturn(2L);

        NotificacioEntity notificacioMock = Mockito.mock(NotificacioEntity.class);
//        Mockito.when(notificacioMock.getEntitat()).thenReturn(entitatMock);

        List<Long> eventsIds = Arrays.asList(1L, 2L, 3L);
//        notificacioEventRepository = Mockito.mock(NotificacioEventRepository.class);
        Mockito.when(
                notificacioEventRepository.findEventsAmbCallbackPendentIds(Mockito.any(Pageable.class))
        ).thenReturn(eventsIds);

        UsuariEntity mockUser = Mockito.mock(UsuariEntity.class);
//        Mockito.when(mockUser.getCodi()).thenReturn("CODI_USER");

        NotificacioEnviamentEntity enviamentMock = NotificacioEnviamentEntity
                .builder()
                .serveiTipus(ServeiTipusEnumDto.NORMAL)
                .notificacio(notificacioMock)
                .build();
        enviamentMock.setCreatedBy(mockUser);
        Mockito.when(configHelper.getAsBoolean(Mockito.eq("es.caib.notib.tasques.actives"))).thenReturn(true);
        Mockito.when(configHelper.getAsBoolean(Mockito.eq("es.caib.notib.tasca.callback.pendents.actiu"))).thenReturn(true);
        Mockito.when(configHelper.getAsInt(Mockito.eq("es.caib.notib.tasca.callback.pendents.processar.max"))).thenReturn(3);


//        eventsMap = new HashMap<>();
//        for (Long eventId : eventsIds) {
//            NotificacioEventEntity eventMock = Mockito.mock(NotificacioEventEntity.class);
//            Mockito.when(
//                    eventMock.getId()
//            ).thenReturn(eventId);
//            eventsMap.put(eventId, eventMock);
//            Mockito.when(
//                    notificacioEventRepository.findOne(Mockito.eq(eventId))
//            ).thenReturn(eventMock);
//            Mockito.when(
//                    callbackHelper.notifica(Mockito.eq(eventMock))
//            ).thenReturn(notificacioMock);
//        }


    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void whenNotificaRaiseExeption_thenCallMarcarEventNoProcessable() throws Exception {
        // Given
        Mockito.when(
                callbackHelper.notifica(Mockito.eq(1L))
        ).thenThrow(Exception.class);
        Mockito.when(
                callbackHelper.notifica(Mockito.eq(2L))
        ).thenThrow(Exception.class);

        // When
        callbackService.processarPendents();

        // Then
        Mockito.verify(callbackHelper, Mockito.times(2)).marcarEventNoProcessable(
                Mockito.any(Long.class),
                Mockito.nullable(String.class),
                Mockito.nullable(String.class)
        );

    }
}