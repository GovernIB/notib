/**
 * 
 */
package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.entity.config.ConfigEntity;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.config.ConfigRepository;
import es.caib.notib.core.test.AuthenticationTest;
import es.caib.notib.core.test.data.DatabaseItemTest;
import es.caib.notib.core.test.data.EntitatItemTest;
import es.caib.notib.core.test.data.OrganGestorItemTest;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.*;
import es.caib.notib.plugin.unitat.*;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.notib.plugin.utils.PropertiesHelper;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.fail;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class BaseServiceTestV2 {

	@Autowired
	protected EntitatItemTest entitatItemTest;

	@Autowired
	protected AuthenticationTest authenticationTest;

	@Autowired
	protected  PluginHelper pluginHelper;
	@Autowired
	protected ConfigRepository configRepository;

	private DadesUsuariPlugin dadesUsuariPluginMock;
	@Mock
	protected GestioDocumentalPlugin gestioDocumentalPluginMock;

	private RegistrePlugin registrePluginMock;
	private IArxiuPlugin arxiuPluginMock;
	protected UnitatsOrganitzativesPlugin unitatsOrganitzativesPluginMock;

	@Autowired
	OrganGestorItemTest organGestorCreate;

	@BeforeClass
	public static void beforeClass() {
		PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
	}

	@AfterClass
	public static void afterClass() {
	}

	protected void testCreantElements(
			TestAmbElementsCreats test,
			String descripcioTest,
			EntitatDto entitatDto,
			DatabaseItemTest... elements) {
		String descripcio = (descripcioTest != null && !descripcioTest.isEmpty()) ? descripcioTest : "";
		log.info("-------------------------------------------------------------------");
		log.info("-- Executant test \"" + descripcio + "\" amb els elements creats...");
		log.info("-------------------------------------------------------------------");
		Long entitatId = null;
		try {
			ElementsCreats database = createDatabase(entitatDto, elements);
			entitatId = database.entitat.getId();

			log.debug("Executant accions del test...");
			test.executar(database);
			log.debug("...accions del test executades.");
		} catch (Exception ex) {
			log.error("L'execució del test ha produït una excepció", ex);
			fail("L'execució del test ha produït una excepció");
		} finally {
			destroyDatabase(entitatId, elements);

			log.info("-------------------------------------------------------------------");
			log.info("-- ...test \"" + descripcio + "\" executat.");
			log.info("-------------------------------------------------------------------");
		}
	}

	protected ElementsCreats createDatabase (EntitatDto entitatDto,
											 DatabaseItemTest... elements) throws Exception {
		EntitatDto entitatCreada = entitatItemTest.create(entitatDto);

		organGestorCreate.addObject("organ_default", organGestorCreate.getRandomInstance());
		organGestorCreate.createAll(entitatCreada.getId());
		OrganGestorDto organ = organGestorCreate.getObject("organ_default");

		Map<String, Object> elementsCreats = new HashMap<>();
		for (DatabaseItemTest<?> element: elements) {
			log.debug("Creant objecte de tipus " + element.getClass().getSimpleName() + "...");
			element.relateElements();
			element.createAll(entitatCreada.getId());
			for(String key : element.getObjects().keySet()) {
				elementsCreats.put(key, element.getObject(key));
				log.debug("...objecte amb la clau: '" + key + "' creat correctament.");
			}

		}
		return new ElementsCreats(entitatCreada, organ, elementsCreats);
	}
	protected void destroyDatabase (Long entitatId,
									DatabaseItemTest... elements) {
		for(int counter=elements.length-1; counter >= 0;counter--){
			DatabaseItemTest<?> element = elements[counter];
			log.debug("Esborrant objecte de tipus " + element.getClass().getSimpleName() + "...");
			try {
				element.deleteAll(entitatId);
				System.out.println("...objecte de tipus " + element.getClass().getSimpleName() + " esborrat correctament.");
			} catch (Exception e) {
				System.out.println("...error esborrant objecte de tipus " + element.getClass().getSimpleName() + ".");
				System.out.println(e);
			}

		}

		try {
			organGestorCreate.delete(entitatId, "organ_default");
			entitatItemTest.delete(entitatId);
			removeAllConfigs();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void testCreantElements(
			final TestAmbElementsCreats test,
			Object... elements) {
		testCreantElements(test, null, elements);
	}

	protected abstract class TestAmbElementsCreats {
		public abstract void executar(
				ElementsCreats elementsCreats) throws Exception;
	}

	@Getter
	@AllArgsConstructor
	protected static class ElementsCreats {
		EntitatDto entitat;
		OrganGestorDto organ;
		Map<String, Object> elementsCreats;

		public Object get(String key) {
			return this.elementsCreats.get(key);
		}
	}

	protected void addConfig(String key, String value) {
		ConfigEntity configEntity = new ConfigEntity(key, value);
		configRepository.save(configEntity);
	}
	protected void setDefaultConfigs() {
		Properties props = PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties").findAll();
		for (Map.Entry<Object, Object> entry : props.entrySet() ) {
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
		List<String> rols = new ArrayList<String>();
		rols.add("tothom");
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
	protected void configureMockGestioDocumentalPlugin() throws SistemaExternException {
		// TODO: Amb el mock activat quan s'executen tots els tests falla
		gestioDocumentalPluginMock = Mockito.mock(GestioDocumentalPlugin.class);
		Mockito.when(gestioDocumentalPluginMock.create(Mockito.anyString(), Mockito.any(InputStream.class))).thenReturn(Integer.toString(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
//		Mockito.doNothing().when(gestioDocumentalPluginMock).update(Mockito.anyString(), Mockito.anyString(), Mockito.any(InputStream.class));
//		Mockito.doNothing().when(gestioDocumentalPluginMock).delete(Mockito.anyString(), Mockito.anyString());
//		Mockito.doAnswer(new Answer<Void>() {
//			public Void answer(InvocationOnMock invocation) throws IOException {
//				Object[] args = invocation.getArguments();
//				FitxerDto fitxer = getFitxerPdfDeTest();
//				byte[] contingut = fitxer.getContingut();
//				IOUtils.copy(new ByteArrayInputStream(contingut), (OutputStream)args[2]);
//				return null;
//			}
//		}).when(gestioDocumentalPluginMock).get(Mockito.anyString(), Mockito.anyString(), Mockito.any(OutputStream.class));
		pluginHelper.setGestioDocumentalPlugin(gestioDocumentalPluginMock);
	}
	
	//	RegistrePlugin
	protected void configureMockRegistrePlugin() throws RegistrePluginException, IOException {
		registrePluginMock = Mockito.mock(RegistrePlugin.class);
		FitxerDto fitxer = getFitxerPdfDeTest();
		
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
		}).when(registrePluginMock).salidaAsientoRegistral(
				Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong(), Mockito.anyBoolean()
		);
		
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
		Mockito.when(registrePluginMock.llistarOficinaVirtual(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(oficina);
																																																																																				
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
		Map<String, NodeDir3> organigramaEntitat = new HashMap<String, NodeDir3>();
		organigramaEntitat.put("E04975701", new NodeDir3());
		Mockito.when(unitatsOrganitzativesPluginMock.organigramaPerEntitat(Mockito.anyString())).thenReturn(organigramaEntitat);
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

	private java.io.InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/notificacio_adjunt.pdf");
	}

}
