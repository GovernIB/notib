package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.PersonaEntity;
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
	private IntegracioHelper integracioHelper;
	@Mock
	private NotificacioEventHelper notificacioEventHelper;
	@Mock
	private ConfigHelper configHelper;
	@Mock
	private AuditHelper auditHelper;
	@Mock
	private EnviamentTableHelper enviamentTableHelper;
	@Mock
	private NotificacioTableHelper notificacioTableHelper;

	@InjectMocks
	private RegistreNotificaHelper registreNotificaHelper;
	
	@Before
	public void setUp() throws RegistrePluginException {
		Mockito.when(pluginHelper.notificacioToAsientoRegistralBean(Mockito.any(NotificacioEntity.class), Mockito.<NotificacioEnviamentEntity>any(), Mockito.anyBoolean(), Mockito.anyBoolean()))
				.thenReturn(new AsientoRegistralBeanDto());
		Mockito.when(pluginHelper.notificacioEnviamentsToAsientoRegistralBean(Mockito.any(NotificacioEntity.class), Mockito.<NotificacioEnviamentEntity>anySet(), Mockito.anyBoolean()))
				.thenReturn(new AsientoRegistralBeanDto());
		Mockito.when(pluginHelper.crearAsientoRegistral(Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.nullable(long.class), Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(new RespostaConsultaRegistre());
		Mockito.when(configHelper.getConfigAsBoolean(Mockito.eq("es.caib.notib.emprar.sir"))).thenReturn(true);
//		Mockito.when(conversioTipusHelper.convertir(Mockito.any(EntitatEntity.class), Mockito.any(Class.class))).thenReturn(new EntitatDto());

	}

	@Test
	public void whenRealitzarProcesRegistrar_GivenComunicacioSIRAAdministracio_ThenInclouDocumentsAndGeneraJustificant() throws RegistreNotificaException, RegistrePluginException {

		// Given
		var entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(initPersonaAdministracio(InteressatTipus.ADMINISTRACIO));
		enviaments.add(enviament);
		var notificacio = initNotificacio(entidad, EnviamentTipus.COMUNICACIO, enviaments);
		Mockito.doNothing().when(auditHelper).auditaNotificacio(Mockito.any(NotificacioEntity.class), Mockito.<AuditService.TipusOperacio>any(), Mockito.anyString());
		Mockito.doNothing().when(notificacioTableHelper).actualitzarRegistre(Mockito.any(NotificacioEntity.class));

		// When
		registreNotificaHelper.realitzarProcesRegistrar(notificacio);

		// Then
		// S'inclou el document
		Mockito.verify(pluginHelper, Mockito.times(1))
				.notificacioToAsientoRegistralBean(Mockito.eq(notificacio), Mockito.eq(enviament), Mockito.eq(true), Mockito.eq(true));

		// Es genera el justificant
		Mockito.verify(pluginHelper, Mockito.times(1))
				.crearAsientoRegistral(Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.eq(true)
		);
	}

	@Test
	public void whenRealitzarProcesRegistrar_GivenNotificacio_ThenNoInclouDocumentsAndNoGeneraJustificant() throws RegistreNotificaException, RegistrePluginException {

		// Given
		var entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		var enviament = initEnviament(initPersonaAdministracio(InteressatTipus.ADMINISTRACIO));
		enviaments.add(enviament);
		NotificacioEntity notificacio = initNotificacio(entidad, EnviamentTipus.NOTIFICACIO, enviaments);
		Mockito.doNothing().when(auditHelper).auditaEnviament(Mockito.any(NotificacioEnviamentEntity.class), Mockito.<AuditService.TipusOperacio>any(), Mockito.anyString());
		Mockito.doNothing().when(enviamentTableHelper).actualitzarRegistre(Mockito.any(NotificacioEnviamentEntity.class));

		// When
		registreNotificaHelper.realitzarProcesRegistrar(notificacio);

		// Then
		// S'inclou el document
		Mockito.verify(pluginHelper, Mockito.times(1))
				.notificacioEnviamentsToAsientoRegistralBean(Mockito.eq(notificacio), Mockito.<NotificacioEnviamentEntity>anySet(), Mockito.eq(false));

		// No es genera el justificant
		Mockito.verify(pluginHelper, Mockito.times(1))
				.crearAsientoRegistral(Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.nullable(long.class), Mockito.anyLong(), Mockito.anyString(), Mockito.eq(false));
	}

	private NotificacioEntity initNotificacio(EntitatEntity entitat, EnviamentTipus enviamentTipus, HashSet<NotificacioEnviamentEntity> enviaments) {

		var notificacio =  Mockito.mock(NotificacioEntity.class);
//		Mockito.when(notificacio.getEmisorDir3Codi()).thenReturn(ConfigTest.ENTITAT_DGTIC_DIR3CODI);
		Mockito.when(notificacio.getId()).thenReturn(1L);
		Mockito.when(notificacio.getEnviamentTipus()).thenReturn(enviamentTipus);
		Mockito.when(notificacio.getEntitat()).thenReturn(entitat);
		Mockito.when(notificacio.getEnviaments()).thenReturn(enviaments);
		for (var enviament: enviaments) {
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
//				false,
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
	private PersonaEntity initPersonaAdministracio(InteressatTipus interessatTipus) {
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
		Mockito.reset(integracioHelper);
		Mockito.reset(notificacioEventHelper);
	}

}
