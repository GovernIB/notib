/**
 * 
 */
package es.caib.notib.core.service;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import es.caib.loginModule.util.Base64.InputStream;
import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PagadorCieFormatSobreDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.UsuariRepository;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.Llibre;
import es.caib.notib.plugin.registre.LlibreOficina;
import es.caib.notib.plugin.registre.Oficina;
import es.caib.notib.plugin.registre.Organisme;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RegistreSortida;
import es.caib.notib.plugin.registre.RespostaAnotacioRegistre;
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

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	protected PagadorPostalService pagadorPostalService;
	@Autowired
	protected PagadorCieService pagadorCieService;
	@Autowired
	protected PagadorCieFormatFullaService pagadorCieFormatFullaService;
	@Autowired
	protected PagadorCieFormatSobreService pagadorCieFormatSobreService;
	
	@Autowired
	private  UsuariRepository usuariRepository;
	
	@Autowired
	private  PluginHelper pluginHelper;
	
	private DadesUsuariPlugin dadesUsuariPluginMock;
	private GestioDocumentalPlugin gestioDocumentalPluginMock;
	private RegistrePlugin registrePluginMock;
	private IArxiuPlugin arxiuPluginMock;
	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPluginMock;


	@BeforeClass
	public static void beforeClass() {
		PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
	}

	@AfterClass
	public static void afterClass() {
	}
	
	@Transactional
	protected void autenticarUsuari(String usuariCodi) {
		logger.debug("Autenticant usuari " + usuariCodi + "...");
		UserDetails userDetails = userDetailsService.loadUserByUsername(usuariCodi);
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(),
				userDetails.getPassword(),
				userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        UsuariEntity usuariEntity = usuariRepository.findOne(usuariCodi);
		if (usuariEntity == null) {
			usuariRepository.save(
					UsuariEntity.getBuilder(
							usuariCodi,
							usuariCodi + "@mail.com",
							"CA")
					.nom(usuariCodi)
					.llinatges(usuariCodi)
					.build());
		}
		logger.debug("... usuari " + usuariCodi + " autenticat correctament");
	}

	protected void testCreantElements(
			TestAmbElementsCreats test,
			String descripcioTest,
			Object... elements) {
		String descripcio = (descripcioTest != null && !descripcioTest.isEmpty()) ? descripcioTest : "";
		logger.info("-------------------------------------------------------------------");
		logger.info("-- Executant test \"" + descripcio + "\" amb els elements creats...");
		logger.info("-------------------------------------------------------------------");
		List<Object> elementsCreats = new ArrayList<Object>();
		Long entitatId = null;
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
				logger.debug("Creant objecte de tipus " + element.getClass().getSimpleName() + "...");
				if (element instanceof EntitatDto) {
					autenticarUsuari("super");
					EntitatDto entitatCreada = entitatService.create((EntitatDto)element);
					entitatId = entitatCreada.getId();
					elementsCreats.add(entitatCreada);
					if (((EntitatDto)element).getPermisos() != null) {
						for (PermisDto permis: ((EntitatDto)element).getPermisos()) {
							entitatService.permisUpdate(
									entitatCreada.getId(),
									permis);
						}
					}
					id = entitatCreada.getId();
				} else if(element instanceof AplicacioDto) {
					autenticarUsuari("super");
					AplicacioDto entitatCreada = usuariAplicacioService.create((AplicacioDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof ProcedimentDto) {
					autenticarUsuari("admin");
					ProcedimentDto entitatCreada = procedimentService.create(
							entitatId,
							(ProcedimentDto)element);
					elementsCreats.add(entitatCreada);
					if (((ProcedimentDto)element).getPermisos() != null) {
						for (PermisDto permis: ((ProcedimentDto)element).getPermisos()) {
							procedimentService.permisUpdate(
									entitatId,
									entitatCreada.getId(),
									permis,
									false);
						}
					}
					id = entitatCreada.getId();
				} else if(element instanceof OrganGestorDto) {
					if (((OrganGestorDto)element).getPermisos() != null) {
						OrganGestorDto organGestor = organGestorService.findByCodi(
								entitatId, 
								((OrganGestorDto)element).getCodi());
						for (PermisDto permis: ((OrganGestorDto)element).getPermisos()) {
							organGestorService.permisUpdate(
									entitatId, 
									organGestor.getId(), 
									permis);
						}
					}
				} else if(element instanceof GrupDto) {
					autenticarUsuari("admin");
					GrupDto entitatCreada = grupService.create(
							entitatId,
							(GrupDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof PagadorPostalDto) {
					autenticarUsuari("admin");
					PagadorPostalDto entitatCreada = pagadorPostalService.create(
							entitatId,
							(PagadorPostalDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof PagadorCieDto) {
					autenticarUsuari("admin");
					PagadorCieDto entitatCreada = pagadorCieService.create(
							entitatId,
							(PagadorCieDto)element);
					pagadorCieId = entitatCreada.getId();
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else if(element instanceof PagadorCieFormatFullaDto) {
					autenticarUsuari("admin");
					PagadorCieFormatFullaDto entitatCreada = pagadorCieFormatFullaService.create(
							pagadorCieId,
							(PagadorCieFormatFullaDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				}else if(element instanceof OrganGestorDto) {
					autenticarUsuari("admin");
					OrganGestorDto entitatCreada = organGestorService.create(
							(OrganGestorDto)element);
						elementsCreats.add(entitatCreada);
						id=entitatCreada.getId();
					
				}else if(element instanceof PagadorCieFormatSobreDto) {
					autenticarUsuari("admin");
					PagadorCieFormatSobreDto entitatCreada = pagadorCieFormatSobreService.create(
							pagadorCieId,
							(PagadorCieFormatSobreDto)element);
					elementsCreats.add(entitatCreada);
					id = entitatCreada.getId();
				} else {
					fail("No s'ha trobat cap entitat per associar l'objecte de tipus " + element.getClass().getSimpleName());
				}
				logger.debug("...objecte de tipus " + element.getClass().getSimpleName() + "creat (id=" + id + ").");
			}
			logger.debug("Executant accions del test...");
			test.executar(elementsCreats);
			logger.debug("...accions del test executades.");
		} catch (Exception ex) {
			logger.error("L'execució del test ha produït una excepció", ex);
			fail("L'execució del test ha produït una excepció");
		} finally {
			Collections.reverse(elementsCreats);
			for (Object element: elementsCreats) {
				logger.debug("Esborrant objecte de tipus " + element.getClass().getSimpleName() + "...");
				if (element instanceof EntitatDto) {
					autenticarUsuari("super");
					entitatService.delete(
							((EntitatDto)element).getId());
					entitatId = null;
				} else if(element instanceof AplicacioDto) {
					autenticarUsuari("super");
					usuariAplicacioService.delete(
							((AplicacioDto)element).getId(), 
							entitatId);
				} else if(element instanceof ProcedimentDto) {
					autenticarUsuari("admin");
					procedimentService.delete(
							entitatId, 
							((ProcedimentDto)element).getId());
				} else if(element instanceof GrupDto) {
					autenticarUsuari("admin");
					grupService.delete(((GrupDto)element).getId());
				} else if(element instanceof PagadorPostalDto) {
					autenticarUsuari("admin");
					pagadorPostalService.delete(((PagadorPostalDto)element).getId());
				} else if(element instanceof PagadorCieDto) {
					autenticarUsuari("admin");
					try {
						pagadorCieService.delete(((PagadorCieDto)element).getId());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if(element instanceof PagadorCieFormatFullaDto) {
					autenticarUsuari("admin");
					pagadorCieFormatFullaService.delete(((PagadorCieFormatFullaDto)element).getId());
				} else if(element instanceof PagadorCieFormatSobreDto) {
					autenticarUsuari("admin");
					pagadorCieFormatSobreService.delete(((PagadorCieFormatSobreDto)element).getId());
				}else if(element instanceof OrganGestorDto) {
					autenticarUsuari("admin");
					organGestorService.delete(entitatId,
					((OrganGestorDto)element).getId());
					 
				}
				
				logger.debug("...objecte de tipus " + element.getClass().getSimpleName() + " esborrat correctament.");
			}
			logger.info("-------------------------------------------------------------------");
			logger.info("-- ...test \"" + descripcio + "\" executat.");
			logger.info("-------------------------------------------------------------------");
		}
	}

	protected void testCreantElements(
			final TestAmbElementsCreats test,
			Object... elements) {
		testCreantElements(test, null, elements);
	}

	abstract class TestAmbElementsCreats {
		public abstract void executar(
				List<Object> elementsCreats) throws Exception;
	}
	
	
	
	// PLUGINS
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	
	//	DadesUsuariPlugin 
	protected void configureMockDadesUsuariPlugin() throws SistemaExternException {
		dadesUsuariPluginMock = Mockito.mock(DadesUsuariPlugin.class);
		List<String> rols = new ArrayList<String>();
		rols.add("NOT_USER");
		DadesUsuari usuari = new DadesUsuari();
		usuari.setCodi("user");
		usuari.setNom("User");
		usuari.setLlinatges("Surname");
		usuari.setNif("00000000T");
		usuari.setEmail("user@user.es");
		List<DadesUsuari> usuaris = new ArrayList<DadesUsuari>();
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
				Object[] args = invocation.getArguments();
				FitxerDto fitxer = getFitxerPdfDeTest();
				byte[] contingut = fitxer.getContingut();
				IOUtils.copy(new ByteArrayInputStream(contingut), (OutputStream)args[2]);
				return null;
			}
		}).when(gestioDocumentalPluginMock).get(Mockito.anyString(), Mockito.anyString(), Mockito.any(OutputStream.class));
		pluginHelper.setGestioDocumentalPlugin(gestioDocumentalPluginMock);
	}
	
	//	RegistrePlugin
	protected void configureMockRegistrePlugin() throws RegistrePluginException, IOException {
		registrePluginMock = Mockito.mock(RegistrePlugin.class);
		FitxerDto fitxer = getFitxerPdfDeTest();
		
		// registrarSalida
		Mockito.doAnswer(new Answer<RespostaAnotacioRegistre>() {
			public RespostaAnotacioRegistre answer(InvocationOnMock invocation) {
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				Date data = new Date();
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(data);
				String num = Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
				resposta.setData(data);
				resposta.setNumero(num);
				resposta.setNumeroRegistroFormateado(num + "/" + calendar.get(Calendar.YEAR));
				return resposta;
			}
		}).when(registrePluginMock).registrarSalida(Mockito.any(RegistreSortida.class), Mockito.anyString());

		// salidaAsientoRegistral
		Mockito.doAnswer(new Answer<RespostaConsultaRegistre>() {
			public RespostaConsultaRegistre answer(InvocationOnMock invocation) {
				RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
				Date data = new Date();
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(data);
				String num = Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
				resposta.setRegistreData(data);
				resposta.setRegistreNumero(num);
				resposta.setRegistreNumeroFormatat(num + "/" + calendar.get(Calendar.YEAR));
				resposta.setEstat(NotificacioRegistreEstatEnumDto.VALID);
				return resposta;
			}
		}).when(registrePluginMock).salidaAsientoRegistral(Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong());
		
		// obtenerAsientoRegistral
		Mockito.doAnswer(new Answer<RespostaConsultaRegistre>() {
			public RespostaConsultaRegistre answer(InvocationOnMock invocation) {
				RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
				Date data = new Date();
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(data);
				String num = Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
				resposta.setRegistreData(data);
				resposta.setRegistreNumero(num);
				resposta.setRegistreNumeroFormatat(num + "/" + calendar.get(Calendar.YEAR));
				resposta.setEstat(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
				resposta.setSirRegistreDestiData(data);
				resposta.setEntitatCodi("A04003003");
				resposta.setEntitatDenominacio("CAIB");
				return resposta;
			}
		}).when(registrePluginMock).obtenerAsientoRegistral(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyBoolean());

		// obtenerJustificante
		RespostaJustificantRecepcio respostaJustificantRecepcio = new RespostaJustificantRecepcio();
		respostaJustificantRecepcio.setJustificant(fitxer.getContingut());
		respostaJustificantRecepcio.setErrorCodi("OK");
		Mockito.when(registrePluginMock.obtenerJustificante(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(respostaJustificantRecepcio);
		
		// obtenerOficioExterno
		Mockito.when(registrePluginMock.obtenerOficioExterno(Mockito.anyString(), Mockito.anyString())).thenReturn(respostaJustificantRecepcio);
		
		// llistarTipusAssumpte
		List<TipusAssumpte> tipusAssumptes = new ArrayList<TipusAssumpte>();
		TipusAssumpte tipusAssumpte1 = new TipusAssumpte();
		tipusAssumpte1.setCodi("TA01");
		tipusAssumpte1.setNom("Tipus Assumpte 01");
		tipusAssumptes.add(tipusAssumpte1);
		Mockito.when(registrePluginMock.llistarTipusAssumpte(Mockito.anyString())).thenReturn(tipusAssumptes);
		
		// llistarCodisAssumpte
		List<CodiAssumpte> codiAssumptes = new ArrayList<CodiAssumpte>();
		CodiAssumpte codiAssumpte1 = new CodiAssumpte();
		codiAssumpte1.setCodi("CA01");
		codiAssumpte1.setNom("Codi Assumpte 01");
		codiAssumpte1.setTipusAssumpte("TA01");
		codiAssumptes.add(codiAssumpte1);
		Mockito.when(registrePluginMock.llistarCodisAssumpte(Mockito.anyString(), Mockito.anyString())).thenReturn(codiAssumptes);

		// llistarOficinaVirtual
		Oficina oficina = new Oficina();
		oficina.setCodi("O00009390");
		oficina.setNom(("DGTIC"));
		Mockito.when(registrePluginMock.llistarOficinaVirtual(Mockito.anyString(), Mockito.anyLong())).thenReturn(oficina);
																																																																																				
		// llistarOficines
		List<Oficina> oficines = new ArrayList<Oficina>();
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
				IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/notib/core/arxiu.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}
	
	protected FitxerDto getFitxerPdfFirmatDeTest() throws IOException {
		FitxerDto dto = new FitxerDto();
		dto.setNom("firma.pdf");
		dto.setContentType("application/pdf");
		dto.setContingut(
				IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/notib/core/firma.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BaseServiceTest.class);
}
