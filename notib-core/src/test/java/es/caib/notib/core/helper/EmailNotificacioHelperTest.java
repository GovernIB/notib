package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.GrupProcSerRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.UsuariRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertNull;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class EmailNotificacioHelperTest {

	// Dades per el Test
//	private static final String EMAIL_USER = "xxxx@gmail.com";
	private static final String EMAIL_PASS = "xxxxxx";
	private static final String EMAIL_USER = "proves_limit@limit.es";
	private static final String EMAIL_DEST = "";
	private static final String FILE_PATH = "";

	@Mock
	private GrupRepository grupRepository;
	@Mock
	private ProcSerHelper procedimentHelper;
	@Mock
	protected CacheHelper cacheHelper;
	@Mock
	protected UsuariRepository usuariRepository;
	@Mock
	protected GrupProcSerRepository grupProcedimentRepository;
	@Mock
	private ConfigHelper configHelper;
	@Mock
	private DocumentHelper documentHelper;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	protected ProcSerHelper procSerHelper;
	@Spy
	private JavaMailSender mailSender = getMailSender();

	@InjectMocks
	private EmailNotificacioHelper emailNotificacioHelper;

	@InjectMocks
	private EmailNotificacioSenseNifHelper emailNotificacioSenseNifHelper;

	private NotificacioEntity notificacioMock;
	private NotificacioEnviamentEntity enviamentMock;


	private JavaMailSender getMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost("correu.limit.es");
		javaMailSender.setPort(25);
//		javaMailSender.setHost("smtp.gmail.com");
//		javaMailSender.setPort(465);
		javaMailSender.setUsername(EMAIL_USER);
		javaMailSender.setPassword(EMAIL_PASS);

		Properties props = javaMailSender.getJavaMailProperties();
//		props.put("mail.smtp.host", "smtp.gmail.com");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.socketFactory.fallback", "false");
//		props.put("mail.smtp.port", "465");
//		props.put("mail.smtp.socketFactory.port", "465");

		props.put("mail.smtp.host", "correu.limit.es");
		props.put("mail.smtp.auth", "false");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.port", "25");
		props.put("mail.smtp.socketFactory.port", "25");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(EMAIL_USER, EMAIL_PASS);
					}
				});
		javaMailSender.setSession(session);

		return javaMailSender;
	}
	
	@Before
	public void setUp() {
		Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.email.remitent"))).thenReturn("email_test@limit.es");
		Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.email.footer"))).thenReturn(" Notib - Govern de les Illes Balears");
		Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.app.base.url"))).thenReturn("http://localhost:8080/notib");
		Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.justificant.capsalera.logo"))).thenReturn(null);
		Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.justificant.peu.logo"))).thenReturn(null);

		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.titol"))).thenReturn("Notificació notib");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.titol"))).thenReturn("Canvi estat a la notificació: ");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.notificacio"))).thenReturn("ID notificació");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.notificacio.concepte"))).thenReturn("Concepte");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.procediment"))).thenReturn("Procediment");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.entitat"))).thenReturn("Entitat");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.estat.nou"))).thenReturn("Nou estat");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.estat.enum.ENVIADA"))).thenReturn("ENVIADA");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.estat.motiu"))).thenReturn("Motiu");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.notificacio.info"))).thenReturn("Detall");
		Mockito.when(messageHelper.getMessage(Mockito.eq("notificacio.email.notificacio.detall"))).thenReturn("Detall notificació");

	}
	
	@Test
	public void whenSendEmailNotificacio_thenReturn() throws Exception {

		// Given
		EntitatEntity entidad = new EntitatEntity();
		Set<String> usuaris = new HashSet<>(Arrays.asList("user"));
		DadesUsuari dadesUsuari = DadesUsuari.builder().email(EMAIL_DEST).build();
		UsuariEntity usuari = UsuariEntity.hiddenBuilder().codi("user").rebreEmailsNotificacio(false).rebreEmailsNotificacioCreats(true).build();
		ProcedimentEntity procediment = ProcedimentEntity.builder().nom("Nom del procediment").agrupar(false).createdBy(usuari).build();
		OrganGestorEntity organGestor = OrganGestorEntity.builder().entitat(entidad).build();
		GrupEntity grupNotificacio = GrupEntity.getBuilder(null, null, entidad, organGestor).build();

		notificacioMock =  Mockito.mock(NotificacioEntity.class);
		Mockito.when(notificacioMock.getId()).thenReturn(666L);
		Mockito.when(notificacioMock.getProcediment()).thenReturn(procediment);
		Mockito.when(notificacioMock.getGrupCodi()).thenReturn(null);
		Mockito.when(notificacioMock.getEntitat()).thenReturn(entidad);
		Mockito.when(notificacioMock.getConcepte()).thenReturn("Concepte de la notificació");
		Mockito.when(notificacioMock.getEstat()).thenReturn(NotificacioEstatEnumDto.ENVIADA);
		Mockito.when(notificacioMock.getMotiu()).thenReturn("Motiu de la notificació");
		Mockito.when(notificacioMock.getEmisorDir3Codi()).thenReturn("A03001234");

//		Document documentArxiuUuid = initDocument(document2.getUuid());
//		Document documentArxiuCsv = initDocument(document3.getCsv());
//
//		DocumentEntity documentEntity = initDocumentEntity(document, documentGesdocId);
//		DocumentEntity documentEntity2 = initDocumentEntity(document2, documentGesdocId);
//		DocumentEntity documentEntity3 = initDocumentEntity(document3, documentGesdocId);
		
		// Mocks
		Mockito.when(grupRepository.findByCodiAndEntitat(Mockito.anyString(), Mockito.any(EntitatEntity.class))).thenReturn(grupNotificacio);
		Mockito.when(grupProcedimentRepository.findByProcSer(Mockito.any(ProcSerEntity.class))).thenReturn(new ArrayList<GrupProcSerEntity>());
		Mockito.when(procedimentHelper.findUsuarisAmbPermisReadPerGrupNotificacio(Mockito.any(GrupEntity.class), Mockito.any(ProcSerEntity.class))).thenReturn(usuaris);
		Mockito.when(procedimentHelper.findUsuarisAmbPermisReadPerProcediment(Mockito.any(NotificacioEntity.class))).thenReturn(usuaris);
		Mockito.when(cacheHelper.findUsuariAmbCodi(Mockito.anyString())).thenReturn(dadesUsuari);
		Mockito.when(usuariRepository.findOne(Mockito.anyString())).thenReturn(usuari);
		Mockito.when(procSerHelper.findUsuaris(Mockito.any(NotificacioEntity.class))).thenReturn(usuaris);

		// When	
		String resposta = emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacioMock);
		
		// Then
		Mockito.verify(grupRepository, Mockito.times(0)).findByCodiAndEntitat(Mockito.anyString(), Mockito.any(EntitatEntity.class));
		Mockito.verify(procedimentHelper, Mockito.times(0)).findUsuarisAmbPermisReadPerGrupNotificacio(Mockito.any(GrupEntity.class), Mockito.any(ProcSerEntity.class));
		Mockito.verify(procedimentHelper, Mockito.times(0)).findUsuarisAmbPermisReadPerGrup(Mockito.any(ProcSerEntity.class));
		Mockito.verify(procedimentHelper, Mockito.times(1)).findUsuarisAmbPermisReadPerProcediment(Mockito.any(NotificacioEntity.class));
		assertNull(resposta);
	}

	@Test
	public void whenSendEmailNotificacioSenseNif_thenReturn() throws Exception {

		// Given
		EntitatEntity entidad = EntitatEntity.hiddenBuilder().nom("Govern de les Illes Balears").build(); // TODO: Afegir logos
		OrganGestorEntity organGestor = OrganGestorEntity.builder().entitat(entidad).nom("Direcció General de Modernització o Administració Digital").build();
		ProcedimentEntity procediment = ProcedimentEntity.builder().nom("Nom del procediment").build();
		PersonaEntity persona = PersonaEntity.builder().nom("Nom").llinatge1("Llinatge1").llinatge2("Llinatge2").email(EMAIL_DEST).build();

		notificacioMock =  Mockito.mock(NotificacioEntity.class);
		Mockito.when(notificacioMock.getEnviamentTipus()).thenReturn(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
		Mockito.when(notificacioMock.getEntitat()).thenReturn(entidad);
		Mockito.when(notificacioMock.getOrganGestor()).thenReturn(organGestor);
		Mockito.when(notificacioMock.getProcediment()).thenReturn(procediment);
		Mockito.when(notificacioMock.getConcepte()).thenReturn("Concepte de la notificació");
		Mockito.when(notificacioMock.getDescripcio()).thenReturn("Descripció de la notificació. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		//Mockito.when(notificacioMock.getIdioma()).thenReturn(IdiomaEnumDto.CA);

		enviamentMock = Mockito.mock(NotificacioEnviamentEntity.class);
		Mockito.when(enviamentMock.getId()).thenReturn(666L);
		Mockito.when(enviamentMock.getNotificacio()).thenReturn(notificacioMock);
		Mockito.when(enviamentMock.getTitular()).thenReturn(persona);
		Mockito.when(enviamentMock.getNotificaReferencia()).thenReturn("48bd0894-0a40-48e1-8ffb-8f2c6c11f0d0");

		// When
		String resposta = emailNotificacioSenseNifHelper.sendEmailInfoEnviamentSenseNif(enviamentMock);

		// Then
		assertNull(resposta);
	}

	@Test
	public void whenSendEmailComunicacioSenseNif_thenReturn() throws Exception {

		// Given
		EntitatEntity entidad = EntitatEntity.hiddenBuilder().nom("Govern de les Illes Balears").build(); // TODO: Afegir logos
		OrganGestorEntity organGestor = OrganGestorEntity.builder().entitat(entidad).nom("Direcció General de Modernització o Administració Digital").build();
		ProcedimentEntity procediment = ProcedimentEntity.builder().nom("Nom del procediment").build();
		PersonaEntity persona = PersonaEntity.builder().nom("Nom").llinatge1("Llinatge1").llinatge2("Llinatge2").email(EMAIL_DEST).build();
		DocumentEntity document = DocumentEntity.getBuilderV2(null, null, "buit.pdf", null, false, null, null, null, null, null, null, null, null).build();
		ArxiuDto arxiu = ArxiuDto.builder().nom("buit.pdf").contingut(Files.readAllBytes(Paths.get(FILE_PATH))).build();

		notificacioMock =  Mockito.mock(NotificacioEntity.class);
		Mockito.when(notificacioMock.getEnviamentTipus()).thenReturn(NotificaEnviamentTipusEnumDto.COMUNICACIO);
		Mockito.when(notificacioMock.getEntitat()).thenReturn(entidad);
		Mockito.when(notificacioMock.getOrganGestor()).thenReturn(organGestor);
		Mockito.when(notificacioMock.getProcediment()).thenReturn(procediment);
		Mockito.when(notificacioMock.getConcepte()).thenReturn("Concepte de la notificació");
		Mockito.when(notificacioMock.getDescripcio()).thenReturn("Descripció de la notificació. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		Mockito.when(notificacioMock.getDocument()).thenReturn(document);

		enviamentMock = Mockito.mock(NotificacioEnviamentEntity.class);
		Mockito.when(enviamentMock.getId()).thenReturn(666L);
		Mockito.when(enviamentMock.getNotificacio()).thenReturn(notificacioMock);
		Mockito.when(enviamentMock.getTitular()).thenReturn(persona);
		Mockito.when(enviamentMock.getNotificaReferencia()).thenReturn("48bd0894-0a40-48e1-8ffb-8f2c6c11f0d0");

		// Mocks
		Mockito.when(documentHelper.documentToArxiuDto(Mockito.anyString(), Mockito.any(DocumentEntity.class))).thenReturn(arxiu);

		// When
		String resposta = emailNotificacioSenseNifHelper.sendEmailInfoEnviamentSenseNif(enviamentMock);

		// Then
		Mockito.verify(documentHelper, Mockito.times(1)).documentToArxiuDto(Mockito.anyString(), Mockito.any(DocumentEntity.class));
		assertNull(resposta);
	}
	
	@After
	public void tearDown() {
		Mockito.reset(notificacioMock);
		Mockito.reset(grupRepository);
		Mockito.reset(procedimentHelper);
		Mockito.reset(cacheHelper);
		Mockito.reset(usuariRepository);
		Mockito.reset(grupProcedimentRepository);
		Mockito.reset(configHelper);
	}

}
