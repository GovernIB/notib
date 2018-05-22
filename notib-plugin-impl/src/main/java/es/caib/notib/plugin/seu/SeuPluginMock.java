/**
 * 
 */
package es.caib.notib.plugin.seu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.seu.SeuNotificacioEstat.ZonaperJustificantEstat;

/**
 * Implementació de proves pel plugin de comunicació amb el ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeuPluginMock implements SeuPlugin {

	@Override
	public boolean comprovarExpedientCreat(
			String expedientIdentificador,
			String unitatAdministrativa,
			String identificadorProcedimiento,
			String idioma,
			String descripcio,
			SeuPersona destinatari,
			SeuPersona representat,
			String bantelNumeroEntrada,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil) throws SistemaExternException {
		String clauSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			return true;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear l'expedient a la zona personal (" +
					"identificadorSistra=" + expedientIdentificador + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"descripcio=" + descripcio + ", " +
					"destinatariNif=" + destinatari.getNif() + ")",
					ex);
		}
	}

	@Override
	public void avisCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String titol,
			String text,
			String textSms,
			List<SeuDocument> annexos) throws SistemaExternException {
		
	}

	@Override
	public SeuNotificacioResultat notificacioCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String registreLlibre,
			String registreOficinaCodi,
			String registreOrganCodi,
			SeuPersona destinatari,
			SeuPersona representat,
			String idioma,
			String oficiTitol,
			String oficiText,
			String avisTitol,
			String avisText,
			String avisTextSms,
			Date dataCaducitat,
			boolean confirmarRecepcio,
			List<SeuDocument> annexos) throws SistemaExternException {
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			System.out.println(">>> Paràmetres: (" +
					"identificadorSistra=" + expedientIdentificador + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"registreLlibre=" + registreLlibre + ", " +
					"registreOficinaCodi=" + registreOficinaCodi + ", " +
					"registreOrganCodi=" + registreOrganCodi + ", " +
					"destinatari=" + destinatari + ", " +
					"representat=" + representat + ", " +
					"idioma=" + idioma + ", " +
					"oficiTitol=" + oficiTitol + ", " +
					"avisTitol=" + avisTitol + ", " +
					"confirmarRecepcio=" + confirmarRecepcio + ")");
			SeuNotificacioResultat resultat = new SeuNotificacioResultat();
			resultat.setRegistreData(new Date());
			resultat.setRegistreNumero(new Long(System.currentTimeMillis()).toString());
			return resultat;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear la notificació (" +
							"identificadorSistra=" + identificadorSistra + ", " +
							"clauSistra=" + clauSistra + ", " +
							"unitatAdministrativa=" + unitatAdministrativa + ", " +
							"oficiTitol=" + oficiTitol + ", " +
							"destinatariNif=" + destinatari.getNif() + ")",
					ex);
		}
	}

	@Override
	public SeuNotificacioEstat notificacioObtenirJustificantRecepcio(
			String registreNumero) throws SistemaExternException {
		SeuNotificacioEstat notificacioEstat = new SeuNotificacioEstat();
		notificacioEstat.setEstat(ZonaperJustificantEstat.PENDENT);
		return notificacioEstat;
	}

	@Override
	public SeuDocument notificacioObtenirFitxerJustificantRecepcio(Long seuFitxerCodi, String seuFitxerClau)
			throws SistemaExternException {
		SeuDocument document = new SeuDocument();
		document.setArxiuNom("nomDelFitxer.pdf");
		return document;
	}

	private String getExpedientClau(
			String expedientIdentificador,
			String unitatAdministrativa) throws NoSuchAlgorithmException {
		String missatge = expedientIdentificador + "/" + unitatAdministrativa;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(missatge.getBytes());
		StringBuilder hexString = new StringBuilder();
	    for (int i = 0; i < digest.length; i++) {
	        String hex = Integer.toHexString(0xFF & digest[i]);
	        if (hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString().toUpperCase();
	}


}
