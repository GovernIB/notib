package es.caib.notib.plugin.registre;

import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.DatosInteresadoWsDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;


/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class RegistrePluginMockImpl implements RegistrePlugin{

	private final Properties properties;
	private final Random rand = new Random();
	private static final String ENTITAT_DIR3_CODI = "A04003003";


	public RegistrePluginMockImpl(Properties properties) {
		this.properties = properties;
	}

	@Override
	public RespostaConsultaRegistre salidaAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio, boolean generarJustificant) {

		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		log.info(arb.toString());
//		System.out.println(">>> DETALL REGISTRE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arb);
//			System.out.println(json);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		System.out.println(">>> FIIIIIIII  DETALL REGISTRE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		DatosInteresadoWsDto interesado = arb.getInteresados().get(0).getInteresado();
 		if (arb.getResumen().toUpperCase().contains("ERROR") || interesado.getApellido1() != null && interesado.getApellido1().equals("error")) {
			resposta.setErrorCodi("3");
			resposta.setErrorDescripcio("Error de registre MOCK (" + System.currentTimeMillis() + ")");
			return resposta;
		}
		Date data = new Date();
		Integer[] registre = readRegistreFile(data, true);

		resposta.setRegistreData(data);
		resposta.setRegistreNumero(String.valueOf(registre[1]));
		resposta.setRegistreNumeroFormatat(registre[1] + "/" + registre[0]);
		resposta.setEstat(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
//		resposta.setEstat(NotificacioRegistreEstatEnumDto.PENDENT);

		if (resposta.getEstat().equals(NotificacioRegistreEstatEnumDto.OFICI_SIR)) {
			resposta.setSirRecepecioData(data);
		}
		if (resposta.getEstat().equals(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT)) {
			resposta.setSirRegistreDestiData(data);
		}
//        resposta.setErrorCodi("OK");
		return resposta;
	}
	
	@Override
	public RespostaConsultaRegistre obtenerAsientoRegistral(String codiDir3Entitat, String numeroRegistreFormatat, Long tipusOperacio, boolean ambAnnexos) {

		boolean respostaAmbError = false;
		RespostaConsultaRegistre respostaConsultaRegistre = new RespostaConsultaRegistre();
		Date data = new Date();
		Integer[] registre = readRegistreFile(data, true);
		respostaConsultaRegistre.setRegistreNumeroFormatat(registre[1] + "/" + registre[0]);
		respostaConsultaRegistre.setRegistreNumero(String.valueOf(registre[1]));
		respostaConsultaRegistre.setRegistreData(data);
		respostaConsultaRegistre.setEstat(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
//		respostaConsultaRegistre.setEstat(NotificacioRegistreEstatEnumDto.PENDENT);

		 if (respostaConsultaRegistre.getEstat().equals(NotificacioRegistreEstatEnumDto.OFICI_SIR))
			 respostaConsultaRegistre.setSirRecepecioData(data);
		 if (respostaConsultaRegistre.getEstat().equals(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT) ||
				 respostaConsultaRegistre.getEstat().equals(NotificacioRegistreEstatEnumDto.REBUTJAT))
			 respostaConsultaRegistre.setSirRegistreDestiData(data);

		respostaConsultaRegistre.setEntitatCodi(ENTITAT_DIR3_CODI);
		respostaConsultaRegistre.setEntitatDenominacio("CAIB");
		if (respostaAmbError) {
			respostaConsultaRegistre.setErrorCodi("500");
			respostaConsultaRegistre.setErrorDescripcio("Simular error");
		}
		respostaConsultaRegistre.setNumeroRegistroDestino("NUMERO REG. DESTINO");
		respostaConsultaRegistre.setMotivo("motiu per el qual s’ha reenviat o rebutjat l’assentament en destí");
		respostaConsultaRegistre.setCodigoEntidadRegistralProcesado("A04026906"); // Codi oficina que ha acceptat/rebutjat
		respostaConsultaRegistre.setDecodificacionEntidadRegistralProcesado("Nom oficina que ha acceptat/rebutjat ");
		return respostaConsultaRegistre;
	}
	
	@Override
	public RespostaJustificantRecepcio obtenerJustificante(
			String codiDir3Entitat, 
			String numeroRegistreFormatat,
			long tipusRegistre) {

		RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
		
		resposta.setJustificant(getJustificant());
		resposta.setErrorCodi("OK");
		return resposta;
		
	}
	
	@Override
	public RespostaJustificantRecepcio obtenerOficioExterno(
			String codiDir3Entitat, 
			String numeroRegistreFormatat) {
		return null;
	}
	
	
	@Override
	public List<TipusAssumpte> llistarTipusAssumpte(String entitat) throws RegistrePluginException {
		
		List<TipusAssumpte> tipusAssumptes = new ArrayList<>();
		
//		TipusAssumpte tipusAssumpte1 = new TipusAssumpte();
//		tipusAssumpte1.setCodi("");
//		tipusAssumpte1.setNom("");
//		tipusAssumptes.add(tipusAssumpte1);
		
		return tipusAssumptes;
	}
	
	@Override
	public List<CodiAssumpte> llistarCodisAssumpte(String entitat, String tipusAssumpte) {

		List<CodiAssumpte> codiAssumptes = new ArrayList<>();
//		CodiAssumpte codiAssumpte1 = new CodiAssumpte();
//		codiAssumpte1.setCodi("");
//		codiAssumpte1.setNom("");
//		codiAssumpte1.setTipusAssumpte(tipusAssumpte);
//		codiAssumptes.add(codiAssumpte1);

		return codiAssumptes;
	}
	
	@Override
	public Oficina llistarOficinaVirtual(String entitatCodi, String nomOficinaVirtualEntitat, Long autoritzacioValor) {

		Oficina oficina = new Oficina();
		oficina.setCodi("O00009390");
		oficina.setNom(("DGTIC"));
		return oficina;
	}
	
	@Override
	public List<Oficina> llistarOficines(String entitat, Long autoritzacio) {
		
		List<Oficina> oficines = new ArrayList<>();
		
		Oficina oficina1 = new Oficina();
		oficina1.setCodi("O00001496");
		oficina1.setNom("Fogaiba Servicios Centrales");
		oficines.add(oficina1);
		
		Oficina oficina2 = new Oficina();
		oficina2.setCodi("O00001497");
		oficina2.setNom("Fogaiba Palma");
		oficines.add(oficina2);
		
		Oficina oficina3 = new Oficina();
		oficina3.setCodi("O00001502");
		oficina3.setNom("Fogaiba SA Pobla");
		oficines.add(oficina3);
		
		Oficina oficina4 = new Oficina();
		oficina4.setCodi("O00001500");
		oficina4.setNom("Fogaiba Inca");
		oficines.add(oficina4);
		
		Oficina oficina5 = new Oficina();
		oficina5.setCodi("O00001501");
		oficina5.setNom("Fogaiba Manacor");
		oficines.add(oficina5);
		
		Oficina oficina6 = new Oficina();
		oficina6.setCodi("O00009385");
		oficina6.setNom("Fogaiba Maó");
		oficines.add(oficina6);
		
		Oficina oficina7 = new Oficina();
		oficina7.setCodi("O00009386");
		oficina7.setNom("Fogaiba Ciutadella");
		oficines.add(oficina7);
		
		Oficina oficina8 = new Oficina();
		oficina8.setCodi("O00001498");
		oficina8.setNom("Fogaiba Campos");
		oficines.add(oficina8);
		
		Oficina oficina9 = new Oficina();
		oficina9.setCodi("O00009384");
		oficina9.setNom("Fogaiba Eivissa");
		oficines.add(oficina9);
		
		Oficina oficina10 = new Oficina();
		oficina10.setCodi("O00001499");
		oficina10.setNom("Fogaiba Felanitx");
		oficines.add(oficina10);
		
		Oficina oficina11 = new Oficina();
		oficina11.setCodi("O00009436");
		oficina11.setNom("Oficina Conveni Consell D'Eivissa");
		oficines.add(oficina11);
		
		Oficina oficina12 = new Oficina();
		oficina12.setCodi("O00009437");
		oficina12.setNom("Oficina Conveni Consell de Menorca");
		oficines.add(oficina12);
		
		Oficina oficina13 = new Oficina();
		oficina13.setCodi("O00010444");
		oficina13.setNom("Oficina Conveni Consell D'Eivissa");
		oficines.add(oficina13);
		
		return oficines;
	}
	
	@Override
	public List<Llibre> llistarLlibres(
			String entitat,
			String oficina,
			Long autoritzacio) throws RegistrePluginException {
		
		List<Llibre> llibres = new ArrayList<>();
	
		Llibre llibre1 = new Llibre();
		llibre1.setCodi("L95");
		llibre1.setOrganisme(ENTITAT_DIR3_CODI);
		llibre1.setNomCurt("L95");
		llibre1.setNomLlarg("FOGAIBA");
		llibres.add(llibre1);
		
		Llibre llibre2 = new Llibre();
		llibre2.setCodi("L2");
		llibre2.setOrganisme(ENTITAT_DIR3_CODI);
		llibre2.setNomCurt("L2");
		llibre2.setNomLlarg("OF. CONVENI CONSELL MENORCA");
		llibres.add(llibre2);
		
		Llibre llibre3 = new Llibre();
		llibre3.setCodi("L3");
		llibre3.setOrganisme(ENTITAT_DIR3_CODI);
		llibre3.setNomCurt("L3");
		llibre3.setNomLlarg("OF. CONVENI CONSELL EIVISSA");
		llibres.add(llibre3);
		
		return llibres;
	}
	
	@Override
	public List<LlibreOficina> llistarLlibresOficines(String entitatCodi, String usuariCodi, Long tipusRegistre){
		return new ArrayList<>();
	}
	
	@Override
	public Llibre llistarLlibreOrganisme(String entitatCodi, String organismeCodi) {


		Integer num = rand.nextInt(100);
		Llibre llibre = new Llibre();
		llibre.setCodi("L" + num);
		llibre.setNomCurt("Llibre " + num + " de prova");
		llibre.setNomLlarg("Llibre " + num + " de prova llarg");
		return llibre;
	}
	
	@Override
	public List<Organisme> llistarOrganismes(
			String entitat) throws RegistrePluginException {
		
		List<Organisme> organismes = new ArrayList<>();
		
		Organisme organisme = new Organisme();
		organisme.setCodi(ENTITAT_DIR3_CODI);
		organisme.setNom("FOGAIBA");
		organismes.add(organisme);
			
		return organismes;
	}
	
	
	private Integer[] readRegistreFile(Date data, boolean update) {
		
		Integer anualitat =  null;
		Integer numero = null;

//		ClassLoader classLoader = getClass().getClassLoader();
//		File file = new File(classLoader.getResource("es/caib/notib/plugin/caib/registre/registre.txt").getFile());
		File file = new File(getSequenciaPath());

		try (Scanner scanner = new Scanner(file)) {

			anualitat = Integer.parseInt(scanner.nextLine());
			numero = Integer.parseInt(scanner.nextLine());

		} catch (IOException e) {
			log.error("Scanner error ", e);
		}
		
		if (update) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			Integer anualitatActual = cal.get(Calendar.YEAR);
			if (anualitatActual > anualitat) {
				anualitat = anualitatActual;
				numero = 1;
			} else {
				numero++;
			}
			
			try (PrintStream fs = new PrintStream(file)) {
				fs.println(anualitat);
				fs.print(numero);
			} catch (IOException e) {
				log.error("PrintStream error ", e);
			}
		}
		
		return new Integer[]{anualitat, numero};
	}
	
	private byte[] getJustificant() {
		
		byte[] fileContent = null;
		var justificantPath = getJustificantPath();
		var file = new File(justificantPath);
		try {
			fileContent = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			log.error("Error llegint el justificant ", e);
		}
		return fileContent;
	}
	
	public String getSequenciaPath() {
		return properties.getProperty("es.caib.notib.plugin.regweb.mock.sequencia");
	}
	
	public String getJustificantPath() {
		return properties.getProperty("es.caib.notib.plugin.regweb.mock.justificant");
	}

	@Override
	public String toString() {
	    return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}
	
	
}
