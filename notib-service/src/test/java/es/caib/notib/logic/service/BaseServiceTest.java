/**
 * 
 */
package es.caib.notib.logic.service;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.EntitatDataDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieFormatFullaService;
import es.caib.notib.logic.intf.service.PagadorCieFormatSobreService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.entity.config.ConfigEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntregaCieRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.persist.repository.config.ConfigRepository;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.Llibre;
import es.caib.notib.plugin.registre.LlibreOficina;
import es.caib.notib.plugin.registre.Oficina;
import es.caib.notib.plugin.registre.Organisme;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.registre.TipusAssumpte;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.fail;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class BaseServiceTest {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	protected UsuariAplicacioService usuariAplicacioService;
	@Autowired
	protected EntitatService entitatService;
	@Autowired
	protected ProcedimentService procedimentService;
	@Autowired
	protected OrganGestorService organGestorService;
	@Autowired
	protected GrupService grupService;
	@Autowired
	protected OperadorPostalService operadorPostalService;
	@Autowired
	protected PagadorCieService pagadorCieService;
	@Autowired
	protected PagadorCieFormatFullaService pagadorCieFormatFullaService;
	@Autowired
	protected PagadorCieFormatSobreService pagadorCieFormatSobreService;
	@Autowired
	protected NotificacioService notificacioService;
	
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private EntregaCieRepository entregaCieRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private CacheHelper cacheHelper;
	
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	protected ConfigRepository configRepository;
	
	private DadesUsuariPlugin dadesUsuariPluginMock;
	private GestioDocumentalPlugin gestioDocumentalPluginMock;
	private RegistrePlugin registrePluginMock;
	private IArxiuPlugin arxiuPluginMock;
	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPluginMock;


	@BeforeClass
	public static void beforeClass() {
//		PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
	}

	@AfterClass
	public static void afterClass() {
	}
	
	@Transactional
	protected void autenticarUsuari(String usuariCodi) {

		log.debug("Autenticant usuari " + usuariCodi + "...");
		var userDetails = userDetailsService.loadUserByUsername(usuariCodi);
		var authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        var usuariEntity = usuariRepository.findById(usuariCodi).orElse(null);
		if (usuariEntity == null) {
			usuariRepository.save(UsuariEntity.builder().codi(usuariCodi).email(usuariCodi + "@mail.com").idioma("CA").nom(usuariCodi).llinatges(usuariCodi).build());
		}
		log.debug("... usuari " + usuariCodi + " autenticat correctament");
	}

	protected void testCreantElements(TestAmbElementsCreats test, String descripcioTest, Object... elements) {

		var descripcio = (descripcioTest != null && !descripcioTest.isEmpty()) ? descripcioTest : "";
		log.info("-------------------------------------------------------------------");
		log.info("-- Executant test \"" + descripcio + "\" amb els elements creats...");
		log.info("-------------------------------------------------------------------");
		List<Object> elementsCreats = new ArrayList<>();
		Long entitatId = null;
		Long organGestorId = null;
		Long pagadorCieId = null;
		try {
			
			// Entitats que es poden crear:
			//
			// - Entitat
			// - Aplicacio
			// - Procediment
			// - Organ gestor -> permisos
			// - Grup
			// - Pagador postal
			// - Pagador cie
			// - Format fulla
			// - Format sobre
			
			for (Object element: elements) {
				Long id = null;
				log.debug("Creant objecte de tipus " + element.getClass().getSimpleName() + "...");
				if (element instanceof EntitatDto) {
					autenticarUsuari("super");
					var entitatCreada = entitatService.create((EntitatDataDto) element);
					entitatId = entitatCreada.getId();
					elementsCreats.add(entitatCreada);
					if (((EntitatDto)element).getPermisos() != null) {
						for (PermisDto permis: ((EntitatDto)element).getPermisos()) {
							entitatService.permisUpdate(entitatCreada.getId(), permis);
						}
					}
					id = entitatCreada.getId();
				} else if(element instanceof AplicacioDto) {
					autenticarUsuari("super");
					var entitatCreada = usuariAplicacioService.create((AplicacioDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof OrganGestorDto) {
					autenticarUsuari("admin");
					((OrganGestorDto)element).setEntitatId(entitatId);
					var entitatCreada = organGestorCreate((OrganGestorDto)element);
					organGestorId = entitatCreada.getId();
					elementsCreats.add(entitatCreada);
					if (((OrganGestorDto)element).getPermisos() != null) {
						for (PermisDto permis: ((OrganGestorDto)element).getPermisos()) {
							organGestorService.permisUpdate(entitatId, entitatCreada.getId(), false, permis);
						}
					}
					id = entitatCreada.getId();
				} else if(element instanceof ProcSerDto) {
					autenticarUsuari("admin");
					var entitatCreada = procedimentService.create(entitatId, (ProcSerDto)element);
					elementsCreats.add(entitatCreada);
					if (((ProcSerDto)element).getPermisos() != null) {
						for (PermisDto permis: ((ProcSerDto)element).getPermisos()) {
							procedimentService.permisUpdate(entitatId, organGestorId, entitatCreada.getId(), permis);
						}
					}
					id = entitatCreada.getId();
				} else if(element instanceof GrupDto) {
					autenticarUsuari("admin");
					var entitatCreada = grupService.create(entitatId, (GrupDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof OperadorPostalDto) {
					autenticarUsuari("admin");
					var entitatCreada = operadorPostalService.upsert(entitatId, (OperadorPostalDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof CieDto) {
					autenticarUsuari("admin");
					var entitatCreada = pagadorCieService.upsert(entitatId, (CieDto)element);
					pagadorCieId = entitatCreada.getId();
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof CieFormatFullaDto) {
					autenticarUsuari("admin");
					var entitatCreada = pagadorCieFormatFullaService.create(pagadorCieId, (CieFormatFullaDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				}else if(element instanceof CieFormatSobreDto) {
					autenticarUsuari("admin");
					var entitatCreada = pagadorCieFormatSobreService.create(pagadorCieId, (CieFormatSobreDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof Notificacio) {
					autenticarUsuari("admin");
					var entitatCreada = notificacioService.create(entitatId, (Notificacio) element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else {
					fail("No s'ha trobat cap entitat per associar l'objecte de tipus " + element.getClass().getSimpleName());
				}
				log.debug("...objecte de tipus " + element.getClass().getSimpleName() + "creat (id=" + id + ").");
			}
			log.debug("Executant accions del test...");
			test.executar(elementsCreats);
			log.debug("...accions del test executades.");
		} catch (Exception ex) {
			log.error("L'execució del test ha produït una excepció", ex);
			fail("L'execució del test ha produït una excepció");
		} finally {
			Collections.reverse(elementsCreats);
			for (Object element: elementsCreats) {
				log.debug("Esborrant objecte de tipus " + element.getClass().getSimpleName() + "...");
				if (element instanceof EntitatDto) {
					autenticarUsuari("admin");
					var entitadId = ((EntitatDto)element).getId();
					var organsGestors = organGestorService.findByEntitat(entitadId);
					for(var organGestorDto: organsGestors) {
						organGestorDelete(entitatId, organGestorDto.getId());
					}
					autenticarUsuari("super");
					entitatService.delete(entitadId);
					entitatId = null;
				} else if(element instanceof AplicacioDto) {
					autenticarUsuari("super");
					usuariAplicacioService.delete(((AplicacioDto)element).getId(), entitatId);
				} else if(element instanceof OrganGestorDto) {
					autenticarUsuari("admin");
					organGestorDelete(entitatId, ((OrganGestorDto)element).getId());
				} else if(element instanceof ProcSerDto) {
					
					autenticarUsuari("admin");
					var procedimentId = ((ProcSerDto)element).getId();
					var notificacionsByProcediment = notificacioRepository.findByProcedimentId(procedimentId);
					for(var notificacioEntity: notificacionsByProcediment) {
						notificacioService.delete(entitatId, notificacioEntity.getId());
					}
					procedimentService.delete(entitatId, ((ProcSerDto)element).getId(), true);
				} else if(element instanceof GrupDto) {
					autenticarUsuari("admin");
					grupService.delete(((GrupDto)element).getId());
				} else if(element instanceof OperadorPostalDto) {
					autenticarUsuari("admin");
					operadorPostalService.delete(((OperadorPostalDto)element).getId());
				} else if(element instanceof CieDto) {
					autenticarUsuari("admin");
					pagadorCieService.delete(((CieDto)element).getId());
				} else if(element instanceof CieFormatFullaDto) {
					autenticarUsuari("admin");
					pagadorCieFormatFullaService.delete(((CieFormatFullaDto)element).getId());
				} else if(element instanceof CieFormatSobreDto) {
					autenticarUsuari("admin");
					pagadorCieFormatSobreService.delete(((CieFormatSobreDto)element).getId());
				} else if(element instanceof NotificacioDtoV2) {
					autenticarUsuari("admin");
					notificacioService.delete(entitatId, ((NotificacioDtoV2)element).getId());
				}
				log.debug("...objecte de tipus " + element.getClass().getSimpleName() + " esborrat correctament.");
			}
			removeAllConfigs();
			log.info("-------------------------------------------------------------------");
			log.info("-- ...test \"" + descripcio + "\" executat.");
			log.info("-------------------------------------------------------------------");
		}
	}

	protected void testCreantElements(final TestAmbElementsCreats test, Object... elements) {
		testCreantElements(test, null, elements);
	}

	protected abstract class TestAmbElementsCreats {
		public abstract void executar(List<Object> elementsCreats) throws Exception;
	}

	protected void addConfig(String key, String value) {
		ConfigEntity configEntity = new ConfigEntity(key, value);
		configRepository.save(configEntity);
	}

	protected void setDefaultConfigs() throws IOException {

		var props = new Properties(System.getProperties());
		props.load(getClass().getClassLoader().getResourceAsStream("es/caib/notib/logic/test.properties"));
//		Properties props = PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties").findAll();
		for (var entry : props.entrySet() ) {
			addConfig(entry.getKey().toString(), entry.getValue().toString());
		}
	}
	protected void removeAllConfigs() {
		configRepository.deleteAll();
	}
	
	// PLUGINS
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	
	//	DadesUsuariPlugin 
	protected void configureMockDadesUsuariPlugin() throws SistemaExternException {

		dadesUsuariPluginMock = Mockito.mock(DadesUsuariPlugin.class);
		List<String> rols = new ArrayList<>();
		rols.add("tothom");
		DadesUsuari usuari = new DadesUsuari();
		usuari.setCodi("user");
		usuari.setNom("User");
		usuari.setLlinatges("Surname");
		usuari.setNif("00000000T");
		usuari.setEmail("user@user.es");
		List<DadesUsuari> usuaris = new ArrayList<>();
		usuaris.add(usuari);
		Mockito.when(dadesUsuariPluginMock.consultarRolsAmbCodi(Mockito.anyString())).thenReturn(rols);
		Mockito.when(dadesUsuariPluginMock.consultarAmbCodi(Mockito.anyString())).thenReturn(usuari);
		Mockito.when(dadesUsuariPluginMock.consultarAmbGrup(Mockito.anyString())).thenReturn(usuaris);
		pluginHelper.setDadesUsuariPlugin(dadesUsuariPluginMock);
	}
	
	//	GestioDocumentalPlugin
	protected void configureMockGestioDocumentalPlugin() throws SistemaExternException, IOException {

		gestioDocumentalPluginMock = Mockito.mock(GestioDocumentalPlugin.class);
		Mockito.when(gestioDocumentalPluginMock.create(Mockito.anyString(), Mockito.any(InputStream.class))).thenReturn(Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
		Mockito.doNothing().when(gestioDocumentalPluginMock).update(Mockito.anyString(), Mockito.anyString(), Mockito.any(InputStream.class));
		Mockito.doNothing().when(gestioDocumentalPluginMock).delete(Mockito.anyString(), Mockito.anyString());
		Mockito.doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) throws IOException {
				var args = invocation.getArguments();
				var fitxer = getFitxerPdfDeTest();
				var contingut = fitxer.getContingut();
				IOUtils.copy(new ByteArrayInputStream(contingut), (OutputStream)args[2]);
				return null;
			}
		}).when(gestioDocumentalPluginMock).get(Mockito.anyString(), Mockito.anyString(), Mockito.any(OutputStream.class), Mockito.eq(false));
		pluginHelper.setGestioDocumentalPlugin(gestioDocumentalPluginMock);
	}
	
	//	RegistrePlugin
	protected void configureMockRegistrePlugin() throws RegistrePluginException, IOException {

		registrePluginMock = Mockito.mock(RegistrePlugin.class);
		var fitxer = getFitxerPdfDeTest();
		
		// registrarSalida
//		Mockito.doAnswer(new Answer<RespostaAnotacioRegistre>() {
//			public RespostaAnotacioRegistre answer(InvocationOnMock invocation) {
//				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
//				Date data = new Date();
//				Calendar calendar = new GregorianCalendar();
//				calendar.setTime(data);
//				String num = Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
//				resposta.setData(data);
//				resposta.setNumero(num);
//				resposta.setNumeroRegistroFormateado(num + "/" + calendar.get(Calendar.YEAR));
//				return resposta;
//			}
//		}).when(registrePluginMock).registrarSalida(Mockito.any(RegistreSortida.class), Mockito.anyString());

		// salidaAsientoRegistral
		Mockito.doAnswer((Answer<RespostaConsultaRegistre>) invocation -> {

			var resposta = new RespostaConsultaRegistre();
			var data = new Date();
			var calendar = new GregorianCalendar();
			calendar.setTime(data);
			var num = Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
			resposta.setRegistreData(data);
			resposta.setRegistreNumero(num);
			resposta.setRegistreNumeroFormatat(num + "/" + calendar.get(Calendar.YEAR));
			resposta.setEstat(NotificacioRegistreEstatEnumDto.VALID);
			return resposta;
		}).when(registrePluginMock).salidaAsientoRegistral(Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong(), Mockito.anyBoolean());
		
		// obtenerAsientoRegistral
		Mockito.doAnswer((Answer<RespostaConsultaRegistre>) invocation -> {
			var resposta = new RespostaConsultaRegistre();
			var data = new Date();
			var calendar = new GregorianCalendar();
			calendar.setTime(data);
			var num = Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
			resposta.setRegistreData(data);
			resposta.setRegistreNumero(num);
			resposta.setRegistreNumeroFormatat(num + "/" + calendar.get(Calendar.YEAR));
			resposta.setEstat(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
			resposta.setSirRegistreDestiData(data);
			resposta.setEntitatCodi("A04003003");
			resposta.setEntitatDenominacio("CAIB");
			return resposta;
		}).when(registrePluginMock).obtenerAsientoRegistral(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyBoolean());

		// obtenerJustificante
		var respostaJustificantRecepcio = new RespostaJustificantRecepcio();
		respostaJustificantRecepcio.setJustificant(fitxer.getContingut());
		respostaJustificantRecepcio.setErrorCodi("OK");
		Mockito.when(registrePluginMock.obtenerJustificante(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(respostaJustificantRecepcio);
		
		// obtenerOficioExterno
		Mockito.when(registrePluginMock.obtenerOficioExterno(Mockito.anyString(), Mockito.anyString())).thenReturn(respostaJustificantRecepcio);
		
		// llistarTipusAssumpte
		List<TipusAssumpte> tipusAssumptes = new ArrayList<>();
		TipusAssumpte tipusAssumpte1 = new TipusAssumpte();
		tipusAssumpte1.setCodi("TA01");
		tipusAssumpte1.setNom("Tipus Assumpte 01");
		tipusAssumptes.add(tipusAssumpte1);
		Mockito.when(registrePluginMock.llistarTipusAssumpte(Mockito.anyString())).thenReturn(tipusAssumptes);
		
		// llistarCodisAssumpte
		List<CodiAssumpte> codiAssumptes = new ArrayList<>();
		var codiAssumpte1 = new CodiAssumpte();
		codiAssumpte1.setCodi("CA01");
		codiAssumpte1.setNom("Codi Assumpte 01");
		codiAssumpte1.setTipusAssumpte("TA01");
		codiAssumptes.add(codiAssumpte1);
		Mockito.when(registrePluginMock.llistarCodisAssumpte(Mockito.anyString(), Mockito.anyString())).thenReturn(codiAssumptes);

		// llistarOficinaVirtual
		var oficina = new Oficina();
		oficina.setCodi("O00009390");
		oficina.setNom(("DGTIC"));
		Mockito.when(registrePluginMock.llistarOficinaVirtual(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(oficina);
																																																																																				
		// llistarOficines
		List<Oficina> oficines = new ArrayList<>();
		oficines.add(oficina);
		Mockito.when(registrePluginMock.llistarOficines(Mockito.anyString(), Mockito.anyLong())).thenReturn(oficines);

		// llistarLlibres
		List<Llibre> llibres = new ArrayList<Llibre>();
		Llibre llibre = new Llibre();
		llibre.setCodi("L99");
		llibre.setNomCurt("Llibre prova");
		llibres.add(llibre);
		Mockito.when(registrePluginMock.llistarLlibres(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(llibres);

		// llistarLlibreOrganisme
		Mockito.when(registrePluginMock.llistarLlibreOrganisme(Mockito.anyString(), Mockito.anyString())).thenReturn(llibre);

		// llistarLlibresOficines
		List<LlibreOficina> llibresOficina = new ArrayList<LlibreOficina>();
		LlibreOficina llibreOficina = new LlibreOficina();
		llibreOficina.setLlibre(llibre);
		llibreOficina.setOficina(oficina);
		llibresOficina.add(llibreOficina);
		Mockito.when(registrePluginMock.llistarLlibresOficines(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(llibresOficina);
			
		// llistarOrganismes
		List<Organisme> organismes = new ArrayList<Organisme>();
		Organisme organisme = new Organisme();
		organisme.setCodi("A04003003");
		organisme.setNom("Govern de les Illes Balears");
		organismes.add(organisme);
		Mockito.when(registrePluginMock.llistarOrganismes(Mockito.anyString())).thenReturn(organismes);

		
		pluginHelper.setRegistrePlugin(registrePluginMock);
	}

	//	IArxiuPlugin
	protected void configureMockArxiuPlugin() throws IOException {
		arxiuPluginMock = Mockito.mock(IArxiuPlugin.class);
		Expedient expedientArxiu = new Expedient();
		expedientArxiu.setIdentificador(UUID.randomUUID().toString());
		expedientArxiu.setNom("nom");
		expedientArxiu.setVersio("1");
		Document documentArxiu = new Document();
		documentArxiu.setIdentificador(UUID.randomUUID().toString());
		documentArxiu.setNom("nom");
		documentArxiu.setVersio("1");
		Document documentArxiuAmbContingut = new Document();
		documentArxiuAmbContingut.setIdentificador(UUID.randomUUID().toString());
		documentArxiuAmbContingut.setNom("nom");
		documentArxiuAmbContingut.setVersio("1");
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setArxiuNom("arxiu.pdf");
		documentContingut.setTipusMime("application/pdf");
		FitxerDto fitxer = getFitxerPdfDeTest();
		documentContingut.setArxiuNom(fitxer.getNom());
		documentContingut.setTipusMime(fitxer.getContentType());
		documentContingut.setContingut(fitxer.getContingut());
		documentContingut.setTamany(fitxer.getTamany());
		documentArxiuAmbContingut.setContingut(documentContingut);
		Mockito.when(arxiuPluginMock.expedientCrear(Mockito.any(Expedient.class))).thenReturn(expedientArxiu);
		Mockito.when(arxiuPluginMock.expedientCrear(null)).thenThrow(NullPointerException.class);
		Mockito.when(arxiuPluginMock.expedientDetalls(Mockito.anyString(), Mockito.nullable(String.class))).thenReturn(expedientArxiu);
		Mockito.when(arxiuPluginMock.documentCrear(Mockito.any(Document.class), Mockito.anyString())).thenReturn(documentArxiu);
		Mockito.when(arxiuPluginMock.documentCrear(null, null)).thenThrow(NullPointerException.class);
		Mockito.when(arxiuPluginMock.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
		Mockito.when(arxiuPluginMock.documentImprimible(Mockito.anyString())).thenReturn(documentContingut);
		pluginHelper.setArxiuPlugin(arxiuPluginMock);
	}
	
	//	UnitatsOrganitzativesPlugin
	protected void configureMockUnitatsOrganitzativesPlugin() throws SistemaExternException {
		unitatsOrganitzativesPluginMock = Mockito.mock(UnitatsOrganitzativesPlugin.class);
		
		List<ObjetoDirectorio> unitats = new ArrayList<ObjetoDirectorio>();
//		unitats.add(new ObjetoDirectorio("A04003003", "Gobierno de las Islas Baleares"));
		unitats.add(new ObjetoDirectorio("A04019898", "Presidencia Gobierno Islas Baleares"));
		unitats.add(new ObjetoDirectorio("A04026906", "Consejería de Administraciones Públicas y Modernización"));
		unitats.add(new ObjetoDirectorio("A04026911", "Consejería de Hacienda y Relaciones Exteriores"));
		unitats.add(new ObjetoDirectorio("A04026919", "Consejería de Salud y Consumo"));
		List<CodiValorPais> paisos = new ArrayList<CodiValorPais>();
		CodiValorPais espanya = new CodiValorPais();
		espanya.setAlfa2Pais("ES");
		espanya.setAlfa3Pais("ESP");
		espanya.setCodiPais(70L);
		espanya.setDescripcioPais("España");
		paisos.add(espanya);
		CodiValorPais andorra = new CodiValorPais();
		andorra.setAlfa2Pais("AN");
		andorra.setAlfa3Pais("AND");
		andorra.setCodiPais(20L);
		andorra.setDescripcioPais("Andorra");
		paisos.add(andorra);
		List<CodiValor> provincies = new ArrayList<CodiValor>();
		provincies.add(new CodiValor("7", "Illes Balears"));
		provincies.add(new CodiValor("8", "Barcelona"));
		provincies.add(new CodiValor("28", "Madrid"));
		List<CodiValor> localitats = new ArrayList<CodiValor>();
		localitats.add(new CodiValor("337", "Manacor"));
		localitats.add(new CodiValor("407", "Palma"));
		localitats.add(new CodiValor("276", "Inca"));
		
		Mockito.when(unitatsOrganitzativesPluginMock.unitatsPerEntitat(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(unitats);
		Mockito.when(unitatsOrganitzativesPluginMock.unitatsPerEntitat(Mockito.eq((String)null), Mockito.anyBoolean())).thenThrow(NullPointerException.class);
		Mockito.when(unitatsOrganitzativesPluginMock.unitatDenominacio(Mockito.anyString())).thenReturn("Gobierno de las Islas Baleares");
		Mockito.when(unitatsOrganitzativesPluginMock.unitatDenominacio(null)).thenThrow(NullPointerException.class);
		Mockito.when(unitatsOrganitzativesPluginMock.paisos()).thenReturn(paisos);
		Mockito.when(unitatsOrganitzativesPluginMock.provincies()).thenReturn(provincies);
		Mockito.when(unitatsOrganitzativesPluginMock.localitats(Mockito.anyString())).thenReturn(localitats);
		pluginHelper.setUnitatsOrganitzativesPlugin(unitatsOrganitzativesPluginMock);
	}
	
	protected FitxerDto getFitxerPdfDeTest() throws IOException {
		FitxerDto dto = new FitxerDto();
		dto.setNom("arxiu.pdf");
		dto.setContentType("application/pdf");
		dto.setContingut(
				IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/notib/logic/arxiu.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}
	
	protected FitxerDto getFitxerPdfFirmatDeTest() throws IOException {
		FitxerDto dto = new FitxerDto();
		dto.setNom("firma.pdf");
		dto.setContentType("application/pdf");
		dto.setContingut(
				IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/notib/logic/firma.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}

	// TODO: Millorar per a poder utilitzar amb més casuístiques
	protected NotificacioDtoV2 generarNotificacio(String notificacioId, ProcSerDto procediment, EntitatDto entitat, String organEmisor, int numDestinataris, boolean ambEnviamentPostal) throws IOException, DecoderException {

		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		NotificacioDtoV2 notificacio = new NotificacioDtoV2();
		notificacio.setUsuariCodi("admin");
		notificacio.setEmisorDir3Codi(organEmisor);
		notificacio.setEnviamentTipus(EnviamentTipus.NOTIFICACIO);
		notificacio.setConcepte("concepte_" + notificacioId);
		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
		notificacio.setRetard(5);
		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
		var document = new es.caib.notib.logic.intf.dto.notificacio.Document();
		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
		document.setHash(Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		document.setNormalitzat(false);
		document.setGenerarCsv(false);
		notificacio.setDocument(document);
		notificacio.setProcediment(procediment);
		notificacio.setOrganGestor("A00000000");
		notificacio.setEntitat(entitat);
		List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<>();
		for (int i = 0; i < numDestinataris; i++) {
			var enviament = new NotificacioEnviamentDtoV2();
			var titular = PersonaDto.builder()
					.interessatTipus(InteressatTipus.FISICA)
					.nom("titularNom" + i)
					.llinatge1("titLlinatge1_" + i)
					.llinatge2("titLlinatge2_" + i)
					.nif("00000000T")
					.telefon("666010101")
					.email("titular@gmail.com").build();
			enviament.setTitular(titular);
			List<PersonaDto> destinataris = new ArrayList<>();
			var destinatari = PersonaDto.builder()
					.interessatTipus(InteressatTipus.FISICA)
					.nom("destinatariNom" + i)
					.llinatge1("destLlinatge1_" + i)
					.llinatge2("destLlinatge2_" + i)
					.nif("12345678Z")
					.telefon("666020202")
					.email("destinatari@gmail.com").build();
			destinataris.add(destinatari);
			enviament.setDestinataris(destinataris);
			enviament.setServeiTipus(ServeiTipus.URGENT);
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
		return notificacio;
	}

	private java.io.InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream("/es/caib/notib/logic/notificacio_adjunt.pdf");
	}

	private OrganGestorDto organGestorCreate(OrganGestorDto dto) {

			var entitat = entitatRepository.getOne(dto.getEntitatId());
			var estat = dto.getEstat() != null ? dto.getEstat() : OrganGestorEstatEnum.V;
			var arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			var node = arbreUnitats.get(dto.getCodi());
			var codiPare = node != null ? node.getPare() : null;
			var organGestorBuilder = OrganGestorEntity.builder()
					.codi(dto.getCodi())
					.nom(dto.getNom())
					.codiPare(codiPare)
					.entitat(entitat)
					.llibre(dto.getLlibre())
					.llibreNom(dto.getLlibreNom())
					.oficina(dto.getOficina() != null ? dto.getOficina().getCodi() : null)
					.oficinaNom(dto.getOficina() != null ? dto.getOficina().getNom() : null)
					.estat(estat.name())
					.sir(dto.getSir());
			if (dto.isEntregaCieActiva()) {
				var entregaCie = new EntregaCieEntity(dto.getCieId(), dto.getOperadorPostalId());
				organGestorBuilder.entregaCie(entregaCieRepository.save(entregaCie));
			}
			return conversioTipusHelper.convertir(organGestorRepository.save(organGestorBuilder.build()), OrganGestorDto.class);
	}

		public OrganGestorDto organGestorDelete(Long entitatId, Long organId) {

			var entitat = entitatRepository.getOne(entitatId);
			var organGestorEntity = organGestorRepository.findByEntitatAndIds(entitat, Arrays.asList(new Long[]{organId})).get(0);
			// Eliminar permisos de l'òrgan
			permisosHelper.deleteAcl(organId, OrganGestorEntity.class);
			// Eliminar organ
			organGestorRepository.delete(organGestorEntity);
			return conversioTipusHelper.convertir(organGestorEntity, OrganGestorDto.class);
	}


	// Contrucción PaginacioParamsDto
	// ///////////////////////////////////////////////////////////////////////////////////////////////

	protected static PaginacioParamsDto getPaginacioDtoFromRequest(Map<String, String[]> mapeigFiltres, Map<String, String[]> mapeigOrdenacions) {

		var params = new DatatablesParams();
		log.debug("Informació de la pàgina obtingudes de datatables (draw=" + params.getDraw() + ", start=" + params.getStart() + ", length=" + params.getLength() + ")");
		var paginacio = new PaginacioParamsDto();
		int paginaNum = params.getStart() / params.getLength();
		paginacio.setPaginaNum(paginaNum);
		if (params.getLength() != null && params.getLength().intValue() == -1) {
			paginacio.setPaginaTamany(Integer.MAX_VALUE);
		} else {
			paginacio.setPaginaTamany(params.getLength());
		}
		paginacio.setFiltre(params.getSearchValue());
		for (int i = 0; i < params.getColumnsSearchValue().size(); i++) {
			String columna = params.getColumnsData().get(i);
			String[] columnes = new String[] {columna};
			if (mapeigFiltres != null && mapeigFiltres.get(columna) != null) {
				columnes = mapeigFiltres.get(columna);
			}
			for (String col: columnes) {
				if (!"<null>".equals(col)) {
					paginacio.afegirFiltre(
							col,
							params.getColumnsSearchValue().get(i));
					log.debug("Afegit filtre a la paginació (" +
							"columna=" + col + ", " +
							"valor=" + params.getColumnsSearchValue().get(i) + ")");
				}
			}
		}
		for (int i = 0; i < params.getOrderColumn().size(); i++) {
			int columnIndex = params.getOrderColumn().get(i);
			var columna = params.getColumnsData().get(columnIndex);
			var direccio = "asc".equals(params.getOrderDir().get(i)) ? OrdreDireccioDto.ASCENDENT : OrdreDireccioDto.DESCENDENT;
			String[] columnes = new String[] {columna};
			if (mapeigOrdenacions != null && mapeigOrdenacions.get(columna) != null) {
				columnes = mapeigOrdenacions.get(columna);
			}
			for (String col: columnes) {
				paginacio.afegirOrdre(col, direccio);
				log.debug("Afegida ordenació a la paginació (columna=" + columna + ", direccio=" + direccio + ")");
			}
		}
		log.debug("Informació de la pàgina sol·licitada (paginaNum=" + paginacio.getPaginaNum() + ", paginaTamany=" + paginacio.getPaginaTamany() + ")");
		return paginacio;
	}
	
	// Listado de Procediments
	@Getter @Setter
	protected static class DatatablesParams {
		private Integer draw;
		private Integer start;
		private Integer length;
		private String searchValue;
		private Boolean searchRegex;
		private List<Integer> orderColumn = new ArrayList<Integer>();
		private List<String> orderDir = new ArrayList<String>();
		private List<String> columnsData = new ArrayList<String>();
		private List<String> columnsName = new ArrayList<String>();
		private List<Boolean> columnsSearchable = new ArrayList<Boolean>();
		private List<Boolean> columnsOrderable = new ArrayList<Boolean>();
		private List<String> columnsSearchValue = new ArrayList<String>();
		private List<Boolean> columnsSearchRegex = new ArrayList<Boolean>();
		protected DatatablesParams() {
			draw = 1;
			start = 0;
			length = 10;
			searchValue = "";
			searchRegex = null;
			orderColumn.add(3);
			orderDir.add("desc");
			columnsData = Arrays.asList("id", "codi", "nom", "organGestorDesc", "pagadorpostal", "pagadorcie", "comu", "agrupar", "grupsCount", "permisosCount", "id");
			columnsName = Arrays.asList(null, null, null, null, null, null, null, null, null, null, null);
			columnsSearchable = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false);
			columnsOrderable = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false);
			columnsSearchValue = Arrays.asList(null, null, null, null, null, null, null, null, null, null, null);
			columnsSearchRegex = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false);
		}
	}
}
