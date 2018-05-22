/**
 * 
 */
package es.caib.notib.plugin.seu;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de proves per al plugin d'integració amb l'arxiu digital.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeuPluginTest {

	private String organCodi;
	private String unitatAdministrativaSistra;
	private String identificadorProcedimiento;
	private String expedientIdentificador;
	private String idioma;
	private String descripcio;
	

	private SeuPlugin plugin;

	@Before
	public void setUp() throws Exception {
		System.setProperty(
				"es.caib.notib.plugin.seu.sistra.base.url",
				"https://proves.caib.es/sistraback");
		System.setProperty(
				"es.caib.notib.plugin.seu.sistra.username",
				"e43110511r");
		System.setProperty(
				"es.caib.notib.plugin.seu.sistra.password",
				"limit5678");
		plugin = new SeuPluginSistra();
		organCodi = "A04003003";
		unitatAdministrativaSistra = "1";
		//unitatAdministrativaSistra = "1002000";
		identificadorProcedimiento = "000000";
		long expedientId = 43114311;
		int anyExpedient = Calendar.getInstance().get(Calendar.YEAR);
		expedientIdentificador = "ES_" + organCodi + "_" + anyExpedient + "_EXP_NOTIB" + String.format("%025d", expedientId);
		idioma = "ca";
		descripcio = "Prova NOTIB";
	}

	//@Test
	public void republicarExpedient() throws es.caib.notib.plugin.SistemaExternException {
		System.out.println("TEST: PUBLICAR EXPEDIENT");
		SeuPersona destinatari = new SeuPersona();
		destinatari.setNif("43110511R");
		destinatari.setNom("Límit");
		destinatari.setLlinatge1("Tecnologies");
		destinatari.setLlinatge2(null);
		SeuPersona representat = null;
		String avisEmail = "josepg@limit.es";
		String avisMobil = null;
		boolean creat1 = plugin.comprovarExpedientCreat(
				expedientIdentificador,
				unitatAdministrativaSistra,
				identificadorProcedimiento,
				idioma,
				descripcio,
				destinatari,
				representat,
				null, // bantelNumeroEntrada
				true,
				avisEmail,
				avisMobil);
		System.out.println("   creat1: " + creat1);
		boolean creat2 = plugin.comprovarExpedientCreat(
				expedientIdentificador,
				unitatAdministrativaSistra,
				identificadorProcedimiento,
				idioma,
				descripcio,
				destinatari,
				representat,
				null, // bantelNumeroEntrada
				true,
				avisEmail,
				avisMobil);
		System.out.println("   creat2 (s'espera false): " + creat2);
		assertTrue(!creat2);
	}

	//@Test
	public void crearAvis() throws es.caib.notib.plugin.SistemaExternException, IOException {
		System.out.println("TEST: PUBLICAR AVIS A EXPEDIENT");
		SeuPersona destinatari = new SeuPersona();
		destinatari.setNif("43110511R");
		destinatari.setNom("Límit");
		destinatari.setLlinatge1("Tecnologies");
		destinatari.setLlinatge2(null);
		SeuPersona representat = null;
		String avisEmail = "josepg@limit.es";
		String avisMobil = null;
		int anyExpedient = Calendar.getInstance().get(Calendar.YEAR);
		long expedientId = 43114311;
		String ntiIdentificador = "ES_" + organCodi + "_" + anyExpedient + "_EXP_NOTIB" + String.format("%025d", expedientId);
		plugin.comprovarExpedientCreat(
				ntiIdentificador,
				unitatAdministrativaSistra,
				identificadorProcedimiento,
				idioma,
				descripcio,
				destinatari,
				representat,
				null, // bantelNumeroEntrada
				true,
				avisEmail,
				avisMobil);
		List<SeuDocument> annexos = new ArrayList<SeuDocument>();
		SeuDocument annex = new SeuDocument();
		annex.setTitol("Document annex amb l'avís");
		annex.setArxiuNom("annex.pdf");
		annex.setArxiuContingut(IOUtils.toByteArray(getAvisAnnexPdf()));
		annexos.add(annex);
		plugin.avisCrear(
				expedientIdentificador,
				unitatAdministrativaSistra,
				"Avís de prova NOTIB 09/05/2017 (0)",
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.",
				"Text de l'avís per mòbils",
				annexos);
	}

	@Test
	public void crearNotificacio() throws es.caib.notib.plugin.SistemaExternException, IOException {
		System.out.println("TEST: PUBLICAR NOTIFICACIO A EXPEDIENT");
		SeuPersona destinatari = new SeuPersona();
		destinatari.setNif("43110511R");
		destinatari.setNom("Límit");
		destinatari.setLlinatge1("Tecnologies");
		destinatari.setLlinatge2("Tecnologies");
		SeuPersona representat = null;
		String avisEmail = "josepg@limit.es";
		String avisMobil = null;
		int anyExpedient = Calendar.getInstance().get(Calendar.YEAR);
		long expedientId = 43114311;
		String ntiIdentificador = "ES_" + organCodi + "_" + anyExpedient + "_EXP_NOTIB" + String.format("%025d", expedientId);
		plugin.comprovarExpedientCreat(
				ntiIdentificador,
				unitatAdministrativaSistra,
				identificadorProcedimiento,
				idioma,
				descripcio,
				destinatari,
				representat,
				null, // bantelNumeroEntrada
				true,
				avisEmail,
				avisMobil);
		List<SeuDocument> annexos = new ArrayList<SeuDocument>();
		SeuDocument annex = new SeuDocument();
		annex.setTitol("Document annex amb l'avís");
		annex.setArxiuNom("annex.pdf");
		annex.setArxiuContingut(IOUtils.toByteArray(getNotificacioAnnexPdf()));
		annexos.add(annex);
		try {
			plugin.notificacioCrear(
					expedientIdentificador,
					unitatAdministrativaSistra,
					"L99",
					"O00009390",
					"A04013511",
					destinatari,
					representat,
					idioma,
					"Ofici títol",
					"Ofici text",
					"Avis titol",
					"Avis text",
					"Text avis per mobils",
					null,
					true,
					null);
		/*plugin.notificacioCrear(
				expedientIdentificador,
				unitatAdministrativaSistra,
				"L99",
				"A04013501",
				destinatari,
				representat,
				idioma,
				"Ofici de remissió per notificació NOTIB 09/05/2017 (0)",
				"Ofici text... Lorem ipsum dolor sit amet.",
				"Avís per notificació de NOTIB 09/05/2017 (0)",
				"Avis text... Lorem ipsum dolor sit amet.",
				"Text de l'avís per mòbils",
				true,
				annexos);*/
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}



	private InputStream getAvisAnnexPdf() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/notib/plugin/seu/zonaperAvisAnnex.pdf");
		return is;
	}

	private InputStream getNotificacioAnnexPdf() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/notib/plugin/seu/notificacioAnnex.pdf");
		return is;
	}

}
