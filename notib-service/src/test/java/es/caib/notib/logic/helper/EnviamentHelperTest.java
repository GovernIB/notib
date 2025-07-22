package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class EnviamentHelperTest {
	
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private NotificaHelper notificaHelper;
	@Mock
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Mock
	private IntegracioHelper integracioHelper;
	
	@InjectMocks
	private EnviamentHelper enviamentHelper;
	
	@Before
	public void setUp() {
		System.setProperty("es.caib.notib.procediment.alta.auto.retard", "10");
		System.setProperty("es.caib.notib.procediment.alta.auto.caducitat", "15");
	}
	
	@Test
	public void whenRefrescarEnviamentsExpirats_thenCallEnviamentRefrescarEstatForEachEnviament() throws Exception {
		// Given
		List<Long> enviamentsIds = Arrays.asList(1L, 2L, 10L);
		Mockito .when(notificacioEnviamentRepository.findIdExpiradesAndNotificaCertificacioDataNull()).thenReturn(enviamentsIds);

		// When
		enviamentHelper.refrescarEnviamentsExpirats();

		// Then
		Mockito.verify(notificaHelper, Mockito.times(enviamentsIds.size())).enviamentRefrescarEstat(Mockito.any(ConsultaNotificaRequest.class), Mockito.anyBoolean()
		);
	}

	@Test
	public void GivenNoEnviamentsExpirats_whenRefrescarEnviamentsExpirats_thenNotCallEnviamentRefrescar() throws Exception {

		// Given
		List<Long> enviamentsIds = new ArrayList<>();
		Mockito .when(notificacioEnviamentRepository.findIdExpiradesAndNotificaCertificacioDataNull()).thenReturn(enviamentsIds);
		// When
		enviamentHelper.refrescarEnviamentsExpirats();
		// Then
		Mockito.verify(notificaHelper, Mockito.times(0)).enviamentRefrescarEstat(Mockito.any(ConsultaNotificaRequest.class), Mockito.anyBoolean()
		);
	}

	@After
	public void tearDown() {
		Mockito.reset(messageHelper);
		Mockito.reset(notificaHelper);
		Mockito.reset(notificacioEnviamentRepository);
		Mockito.reset(integracioHelper);
	}

}
