package es.caib.notib.plugin.registre;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.plugin.utils.PropertiesHelper;


/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginMockImpl implements RegistrePlugin{
	
	@Override
	public RespostaAnotacioRegistre registrarSalida(
			RegistreSortida registreSortida,
			String aplicacion) throws RegistrePluginException {
		
		RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
		
		Date data = new Date();
		Integer[] registre = readRegistreFile(data, true);
		
        resposta.setData(data);
        resposta.setNumero(String.valueOf(registre[1]));
        resposta.setNumeroRegistroFormateado(registre[1] + "/" + registre[0]);
//        resposta.setErrorCodi("OK");
        return resposta;
	}
	
	@Override
	public RespostaConsultaRegistre salidaAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio) {
		
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		
		Date data = new Date();
		Integer[] registre = readRegistreFile(data, true);
		
        resposta.setRegistreData(data);
        resposta.setRegistreNumero(String.valueOf(registre[1]));
        resposta.setRegistreNumeroFormatat(registre[1] + "/" + registre[0]);
        resposta.setEstat(NotificacioRegistreEstatEnumDto.VALID);
//        resposta.setErrorCodi("OK");
        return resposta;
	}
	
	@Override
	public RespostaConsultaRegistre obtenerAsientoRegistral(
			String codiDir3Entitat, 
			String numeroRegistreFormatat,
			Long tipusOperacio,
			boolean ambAnnexos) {
		boolean respostaAmbError = false;
		RespostaConsultaRegistre respostaConsultaRegistre = new RespostaConsultaRegistre();
		Date data = new Date();
		Integer[] registre = readRegistreFile(data, true);
		respostaConsultaRegistre.setRegistreNumeroFormatat(registre[1] + "/" + registre[0]);
		respostaConsultaRegistre.setRegistreNumero(String.valueOf(registre[1]));
		respostaConsultaRegistre.setRegistreData(data);
		respostaConsultaRegistre.setEstat(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
		
		respostaConsultaRegistre.setEntitatCodi("A04003003");
		respostaConsultaRegistre.setEntitatDenominacio("CAIB");
		if (respostaAmbError) {
			respostaConsultaRegistre.setCodiError("500");
			respostaConsultaRegistre.setDescripcioError("Simular error");
		}
		
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
		
		List<TipusAssumpte> tipusAssumptes = new ArrayList<TipusAssumpte>();
		
//		TipusAssumpte tipusAssumpte1 = new TipusAssumpte();
//		tipusAssumpte1.setCodi("");
//		tipusAssumpte1.setNom("");
//		tipusAssumptes.add(tipusAssumpte1);
		
		return tipusAssumptes;
	}
	
	@Override
	public List<CodiAssumpte> llistarCodisAssumpte(
			String entitat,
			String tipusAssumpte) throws RegistrePluginException {
		
		List<CodiAssumpte> codiAssumptes = new ArrayList<CodiAssumpte>();
		
//		CodiAssumpte codiAssumpte1 = new CodiAssumpte();
//		codiAssumpte1.setCodi("");
//		codiAssumpte1.setNom("");
//		codiAssumpte1.setTipusAssumpte(tipusAssumpte);
//		codiAssumptes.add(codiAssumpte1);
		
		return codiAssumptes;
	}
	
	@Override
	public Oficina llistarOficinaVirtual(
			String entitatCodi, 
			Long autoritzacioValor) throws RegistrePluginException {
		Oficina oficina = new Oficina();
		oficina.setCodi("O00009390");
		oficina.setNom(("DGTIC"));
		return oficina;
	}
	
	@Override
	public List<Oficina> llistarOficines(
			String entitat,
			Long autoritzacio) throws RegistrePluginException {
		
		List<Oficina> oficines = new ArrayList<Oficina>();
		
//		Oficina oficina1 = new Oficina();
//		oficina1.setCodi("O00001496");
//		oficina1.setNom("Fogaiba Servicios Centrales");
//		oficines.add(oficina1);
//		
//		Oficina oficina2 = new Oficina();
//		oficina2.setCodi("O00001497");
//		oficina2.setNom("Fogaiba Palma");
//		oficines.add(oficina2);
//		
//		Oficina oficina3 = new Oficina();
//		oficina3.setCodi("O00001502");
//		oficina3.setNom("Fogaiba SA Pobla");
//		oficines.add(oficina3);
//		
//		Oficina oficina4 = new Oficina();
//		oficina4.setCodi("O00001500");
//		oficina4.setNom("Fogaiba Inca");
//		oficines.add(oficina4);
//		
//		Oficina oficina5 = new Oficina();
//		oficina5.setCodi("O00001501");
//		oficina5.setNom("Fogaiba Manacor");
//		oficines.add(oficina5);
//		
//		Oficina oficina6 = new Oficina();
//		oficina6.setCodi("O00009385");
//		oficina6.setNom("Fogaiba Maó");
//		oficines.add(oficina6);
//		
//		Oficina oficina7 = new Oficina();
//		oficina7.setCodi("O00009386");
//		oficina7.setNom("Fogaiba Ciutadella");
//		oficines.add(oficina7);
//		
//		Oficina oficina8 = new Oficina();
//		oficina8.setCodi("O00001498");
//		oficina8.setNom("Fogaiba Campos");
//		oficines.add(oficina8);
//		
//		Oficina oficina9 = new Oficina();
//		oficina9.setCodi("O00009384");
//		oficina9.setNom("Fogaiba Eivissa");
//		oficines.add(oficina9);
//		
//		Oficina oficina10 = new Oficina();
//		oficina10.setCodi("O00001499");
//		oficina10.setNom("Fogaiba Felanitx");
//		oficines.add(oficina10);
//		
//		Oficina oficina11 = new Oficina();
//		oficina11.setCodi("O00009436");
//		oficina11.setNom("Oficina Conveni Consell D'Eivissa");
//		oficines.add(oficina11);
//		
//		Oficina oficina12 = new Oficina();
//		oficina12.setCodi("O00009437");
//		oficina12.setNom("Oficina Conveni Consell de Menorca");
//		oficines.add(oficina12);
//		
//		Oficina oficina13 = new Oficina();
//		oficina13.setCodi("O00010444");
//		oficina13.setNom("Oficina Conveni Consell D'Eivissa");
//		oficines.add(oficina13);
		
		return oficines;
	}
	
	@Override
	public List<Llibre> llistarLlibres(
			String entitat,
			String oficina,
			Long autoritzacio) throws RegistrePluginException {
		
		List<Llibre> llibres = new ArrayList<Llibre>();
	
//		Llibre llibre1 = new Llibre();
//		llibre1.setCodi("L95");
//		llibre1.setOrganisme("A04003003");
//		llibre1.setNomCurt("L95");
//		llibre1.setNomLlarg("FOGAIBA");
//		llibres.add(llibre1);
//		
//		Llibre llibre2 = new Llibre();
//		llibre2.setCodi("L2");
//		llibre2.setOrganisme("A04003003");
//		llibre2.setNomCurt("L2");
//		llibre2.setNomLlarg("OF. CONVENI CONSELL MENORCA");
//		llibres.add(llibre2);
//		
//		Llibre llibre3 = new Llibre();
//		llibre3.setCodi("L3");
//		llibre3.setOrganisme("A04003003");
//		llibre3.setNomCurt("L3");
//		llibre3.setNomLlarg("OF. CONVENI CONSELL EIVISSA");
//		llibres.add(llibre3);
		
		return llibres;
	}
	
	@Override
	public List<LlibreOficina> llistarLlibresOficines(
			String entitatCodi, 
			String usuariCodi,
			Long tipusRegistre){
		return null;
	}
	
	@Override
	public Llibre llistarLlibreOrganisme(
			String entitatCodi, 
			String organismeCodi) throws RegistrePluginException {
		Llibre llibre = new Llibre();
		llibre.setCodi("L99");
		llibre.setNomCurt("Llibre prova");
		return llibre;
	}
	
	@Override
	public List<Organisme> llistarOrganismes(
			String entitat) throws RegistrePluginException {
		
		List<Organisme> organismes = new ArrayList<Organisme>(); 
		
		Organisme organisme = new Organisme();
		organisme.setCodi("A04003003");
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

			scanner.close();
			
		} catch (IOException e) {
			e.printStackTrace();
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
				
				fs.println(anualitat.toString());
				fs.print(numero.toString());
				fs.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new Integer[]{anualitat, numero};
	}
	
	private byte[] getJustificant() {
		
		byte[] fileContent = null;
		
//		ClassLoader classLoader = getClass().getClassLoader();
//		File file = new File(classLoader.getResource("es/caib/notib/plugin/caib/registre/justificant.pdf").getFile());
		File file = new File(getJustificantPath());
		
		try {
		
			fileContent = Files.readAllBytes(file.toPath());
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileContent;
	}
	
	public static String getSequenciaPath() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.regweb.mock.sequencia");
	}
	
	public static String getJustificantPath() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.regweb.mock.justificant");
	}
	
}
