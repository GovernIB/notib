/**
 * 
 */
package es.caib.notib.plugin.firmaservidor;

import es.caib.notib.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAvailableProfile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignedFileInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaEnServidorSimpleJersey;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Implementació del plugin de signatura emprant el portafirmes
 * de la CAIB desenvolupat per l'IBIT (PortaFIB).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class FirmaSimpleServidorPluginPortafib implements FirmaServidorPlugin {

	private static final String PROPERTIES_BASE = "es.caib.notib.plugin.firmaservidor.portafib.";

	private final Properties properties;

	public FirmaSimpleServidorPluginPortafib(Properties properties) {
		this.properties = properties;
	}

	@Override
	public byte[] firmar(String nom, String motiu, byte[] contingut, TipusFirma tipusFirma, String idioma) throws SistemaExternException {
		return signar(null, nom, motiu, tipusFirma.name(), contingut, null);
	}

	public byte[] signar(String id, String nom, String motiu, String tipusFirma, byte[] contingut, String tipusDocumental) {

		ApiFirmaEnServidorSimple api = new ApiFirmaEnServidorSimpleJersey(getPropertyEndpoint(), getPropertyUsername(), getPropertyPassword());
		FirmaSimpleFile fileToSign = new FirmaSimpleFile(nom, "application/pdf", contingut);
		FirmaSimpleSignatureResult result;
		try {
//			getAvailableProfiles(api);
			String perfil = getPropertyPerfil();
			result = internalSignDocument(api, perfil, fileToSign, motiu, tipusDocumental);
			return result.getSignedFile().getData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}  

	protected FirmaSimpleSignatureResult internalSignDocument(ApiFirmaEnServidorSimple api, final String perfil, FirmaSimpleFile fileToSign, String motiu, String tipusDocumental) throws Exception, FileNotFoundException, IOException {

		var signID = "999";
		var name = fileToSign.getNom();
		var reason = motiu;
		var location = getPropertyLocation();

		var signNumber = 1;
		var languageSign = "ca";
		var tipusDocumentalID = tipusDocumental != null ? Long.valueOf(tipusDocumental.substring(2)) : null;
		var fileInfoSignature = new FirmaSimpleFileInfoSignature(fileToSign, signID, name, reason, location, signNumber, languageSign, tipusDocumentalID);
		var languageUI = "ca";
		var certificat = getPropertyUsuariFirma();
		String administrationID = null;
		var signerEmail = getPropertySignerEmail();
		var commonInfo = new FirmaSimpleCommonInfo(perfil, languageUI, certificat, administrationID, signerEmail);
		log.debug("languageUI = |" + languageUI + "|");
		var signature = new FirmaSimpleSignDocumentRequest(commonInfo, fileInfoSignature);
		var fullResults = api.signDocument(signature);
		var transactionStatus = fullResults.getStatus();
		var status = transactionStatus.getStatus();
		switch (status) {
			case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
				throw new SistemaExternException("API de firma simple ha tornat status erroni: Initializing ...Unknown Error (???)");

			case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
				throw new SistemaExternException("API de firma simple ha tornat status erroni: In PROGRESS ...Unknown Error (???)");

			case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
				throw new SistemaExternException("Error durant la realització de les firmes: " + transactionStatus.getErrorMessage() +"\r\n" +transactionStatus.getErrorStackTrace());

			case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
				throw new SistemaExternException("S'ha cancel·lat el procés de firmat.");

			case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
				log.debug(" ===== RESULTAT  =========");
				log.debug(" ---- Signature [ " + fullResults.getSignID() + " ]");
				log.debug(FirmaSimpleSignedFileInfo.toString(fullResults.getSignedFileInfo()));
				return fullResults;
			default:
				throw new SistemaExternException("Status de firma desconegut");
		}
	}

	private void getAvailableProfiles(ApiFirmaEnServidorSimple api) throws Exception {

		final String languagesUI[] = new String[] { "ca", "es" };
		List<FirmaSimpleAvailableProfile> listProfiles;
		for (var languageUI : languagesUI) {
			log.info(" ==== LanguageUI : " + languageUI + " ===========");
			listProfiles = api.getAvailableProfiles(languageUI);
			if (listProfiles.size() == 0) {
				log.info("NO HI HA PERFILS PER AQUEST USUARI APLICACIÓ");
				continue;
			}
			for (var ap : listProfiles) {
			  log.info("  + " + ap.getName() + ":");
			  log.info("      * Codi: " + ap.getCode());
			  log.info("      * Desc: " + ap.getDescription());
			}
		}
	 }


	private String getPropertyEndpoint() {
		return properties.getProperty(PROPERTIES_BASE + "endpoint");
	}

	private String getPropertyUsername() {
		return properties.getProperty(PROPERTIES_BASE + "auth.username");
	}

	private String getPropertyPassword() {
		return properties.getProperty(PROPERTIES_BASE + "auth.password");
	}

	private String getPropertyPerfil() {
		return properties.getProperty(PROPERTIES_BASE + "perfil");
	}

	private String getPropertyLocation() {
		return properties.getProperty(PROPERTIES_BASE + "location", "Palma");
	}

	private String getPropertySignerEmail() {
		return properties.getProperty(PROPERTIES_BASE + "signerEmail", "suport@caib.es");
	}

	private String getPropertyUsuariFirma() {
		return properties.getProperty(PROPERTIES_BASE + "username");
	}

}
