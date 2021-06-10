package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;

@RunWith(MockitoJUnitRunner.class)
public class RegistreNotificaHelperTest {
	
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private RegistreHelper registreHelper;
	@Mock
	private AuditNotificacioHelper auditNotificacioHelper;
	@Mock
	private IntegracioHelper integracioHelper;
	@Mock
	private NotificacioEventHelper notificacioEventHelper;

	@InjectMocks
	private RegistreNotificaHelper registreNotificaHelper;
	
	@Before
	public void setUp() throws RegistrePluginException {
		Mockito.when(
				pluginHelper.notificacioToAsientoRegistralBean(Mockito.any(NotificacioEntity.class), Mockito.<NotificacioEnviamentEntity>any(), Mockito.anyBoolean())
		).thenReturn(new AsientoRegistralBeanDto());
		Mockito.when(
				pluginHelper.notificacioEnviamentsToAsientoRegistralBean(Mockito.any(NotificacioEntity.class), Mockito.<NotificacioEnviamentEntity>anySet(), Mockito.anyBoolean())
		).thenReturn(new AsientoRegistralBeanDto());
		Mockito.when(
				pluginHelper.crearAsientoRegistral(
						Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()
				)
		).thenReturn(new RespostaConsultaRegistre());

	}

	@Test
	public void whenRealitzarProcesRegistrar_GivenComunicacioSIRAAdministracio_ThenInclouDocumentsAndGeneraJustificant() throws RegistreNotificaException, RegistrePluginException {
		// Given
		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
		NotificacioEntity notificacio = initNotificacio(entidad, NotificaEnviamentTipusEnumDto.COMUNICACIO, enviaments);

		// When
		registreNotificaHelper.realitzarProcesRegistrar(notificacio);

		// Then
		// S'inclou el document
		Mockito.verify(pluginHelper, Mockito.times(1)).notificacioToAsientoRegistralBean(
				Mockito.eq(notificacio),
				Mockito.eq(enviament),
				Mockito.eq(true)
		);

		// Es genera el justificant
		Mockito.verify(pluginHelper, Mockito.times(1)).crearAsientoRegistral(
				Mockito.anyString(),
				Mockito.any(AsientoRegistralBeanDto.class),
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyString(),
				Mockito.eq(true)
		);
	}

	@Test
	public void whenRealitzarProcesRegistrar_GivenNotificacio_ThenNoInclouDocumentsAndNoGeneraJustificant()
			throws RegistreNotificaException, RegistrePluginException {
		// Given
		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
		NotificacioEntity notificacio = initNotificacio(entidad, NotificaEnviamentTipusEnumDto.NOTIFICACIO, enviaments);

		// When
		registreNotificaHelper.realitzarProcesRegistrar(notificacio);

		// Then
		// S'inclou el document
		Mockito.verify(pluginHelper, Mockito.times(1)).notificacioEnviamentsToAsientoRegistralBean(
				Mockito.eq(notificacio),
				Mockito.<NotificacioEnviamentEntity>anySet(),
				Mockito.eq(false)
		);


		// No es genera el justificant
		Mockito.verify(pluginHelper, Mockito.times(1)).crearAsientoRegistral(
				Mockito.anyString(),
				Mockito.any(AsientoRegistralBeanDto.class),
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyString(),
				Mockito.eq(false)
		);
	}

	private NotificacioEntity initNotificacio(EntitatEntity entitat,
											  NotificaEnviamentTipusEnumDto enviamentTipus,
											  HashSet<NotificacioEnviamentEntity> enviaments) {

		NotificacioEntity notificacio =  Mockito.mock(NotificacioEntity.class);
//		Mockito.when(notificacio.getEmisorDir3Codi()).thenReturn(ConfigTest.ENTITAT_DGTIC_DIR3CODI);
		Mockito.when(notificacio.getId()).thenReturn(1L);
		Mockito.when(notificacio.getEnviamentTipus()).thenReturn(enviamentTipus);
		Mockito.when(notificacio.getEntitat()).thenReturn(entitat);
		Mockito.when(notificacio.getEnviaments()).thenReturn(enviaments);

		for (NotificacioEnviamentEntity enviament: enviaments) {
			enviament.setNotificacio(notificacio);
		}

		return notificacio;
	}

	private NotificacioEnviamentEntity initEnviament(PersonaEntity titular) {
		NotificacioEnviamentEntity enviament = Mockito.mock(NotificacioEnviamentEntity.class);
		Mockito.when(enviament.getId()).thenReturn(1L);
		Mockito.when(enviament.getTitular()).thenReturn(titular);

		return enviament;
	}
	private EntitatEntity initEntitat() {
		 return EntitatEntity.getBuilder("codi",
				"nom",
				null,
				"dir3Codi",
				"dir3CodiReg",
				"apiKey",
				false,
				false,
				null,
				null,
				"colorFons",
				"colorLletra",
				null,
				"oficina",
				"nomOficinaVirtual",
				false,
				"llibre",
				"llibreNom",
				false)
				.build();
	}
	private PersonaEntity initPersonaAdministracio(InteressatTipusEnumDto interessatTipus) {
		return PersonaEntity.builder()
				.interessatTipus(interessatTipus)
				.email("sandreu@limit.es")
				.llinatge1("Andreu")
				.llinatge2("Nadal")
				.nif("00000000T")
				.nom("Siòn")
				.telefon("666010101").build();
	}

	@After
	public void tearDown() {
		Mockito.reset(pluginHelper);
		Mockito.reset(registreHelper);
		Mockito.reset(auditNotificacioHelper);
		Mockito.reset(integracioHelper);
		Mockito.reset(notificacioEventHelper);
	}

}
