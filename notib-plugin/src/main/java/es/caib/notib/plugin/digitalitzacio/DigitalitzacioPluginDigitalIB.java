/**
 *
 */
package es.caib.notib.plugin.digitalitzacio;

import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioEstat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.ApiMassiveScanWebSimple;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleAvailableProfile;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleAvailableProfiles;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleProfileRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleSignatureParameters;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleStatus;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleSubtransactionResult;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.beans.MassiveScanWebSimpleSubtransactionResultRequest;
import org.fundaciobit.apisib.apimassivescanwebsimple.v1.jersey.ApiMassiveScanWebSimpleJersey;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Implementació del plugin de portafirmes emprant el portafirmes de la CAIB desenvolupat per l'IBIT (PortaFIB).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
//public class DigitalitzacioPluginDigitalIB extends RipeaAbstractPluginProperties implements DigitalitzacioPlugin {
public class DigitalitzacioPluginDigitalIB extends AbstractSalutPlugin implements DigitalitzacioPlugin {

    private final Properties properties;
    private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public DigitalitzacioPluginDigitalIB(Properties properties) {
        this.properties = properties;
	}

	public DigitalitzacioPluginDigitalIB(Properties properties, boolean configuracioEspecifica) {

        this.properties = properties;
        salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
        logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.DIGITALITZACIO")));
	}

	@Override
    public List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(String idioma) throws SistemaExternException {
		List<DigitalitzacioPerfil> perfilsDisponibles = new ArrayList<DigitalitzacioPerfil>();

		try {
			MassiveScanWebSimpleAvailableProfiles profiles = getDigitalitzacioClient().getAvailableProfiles(idioma);
			List<MassiveScanWebSimpleAvailableProfile> profilesList = profiles.getAvailableProfiles();
			if (profilesList == null || profilesList.size() == 0) {
				if (isDebug()) {
					log.error("NO HI HA PERFILS PER AQUEST USUARI APLICACIÓ");
				}
			} else {
				if (isDebug()) {
					log.info(" ---- Perfils Disponibles ----");
				}
				int i = 1;
				Map<Integer, MassiveScanWebSimpleAvailableProfile> profilesByIndex;
				profilesByIndex = new HashMap<>();
				for (MassiveScanWebSimpleAvailableProfile profile : profilesList) {
					if (isDebug()) {
						log.info(i + ".- " + profile.getName() + "(CODI: " + profile.getCode() + "): " + profile.getDescription());
					}
					profilesByIndex.put(i,profile);
					i++;
				}
				if (isDebug()) {
					log.info(" -----------------------------");
				}
			}
			for (MassiveScanWebSimpleAvailableProfile scanWebSimpleAvailableProfile : profilesList) {
				DigitalitzacioPerfil perfil = new DigitalitzacioPerfil();
				perfil.setCodi(scanWebSimpleAvailableProfile.getCode());
				perfil.setNom(scanWebSimpleAvailableProfile.getName());
				perfil.setDescripcio(scanWebSimpleAvailableProfile.getDescription());
				perfil.setTipus(scanWebSimpleAvailableProfile.getProfileType());
				perfilsDisponibles.add(perfil);
			}
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut recuperar els perfils de l'usuari aplicació configurat (" + "idioma=" + idioma + ")", ex);
		}

		return perfilsDisponibles;
	}

	@Override
    public DigitalitzacioTransaccioResposta iniciarProces(String codiPerfil, String idioma, UsuariDto funcionari, String urlReturn) throws SistemaExternException {

		// Si tipus de perfil val MassiveScanWebSimpleAvailableProfile.PROFILE_TYPE_ONLY_SCAN
		final var profileCode = codiPerfil;
		final var view = MassiveScanWebSimpleGetTransactionIdRequest.VIEW_FULLSCREEN;
		DigitalitzacioTransaccioResposta transaccioResponse = null;
		MassiveScanWebSimpleGetTransactionIdRequest transacctionIdRequest = null;
		var returnScannedFile = false;
		var returnSignedFile = false;
		final String transactionName = "Transaccio " + System.currentTimeMillis();
		try {
			// En cas de no especificar cap perfil agafar per propietats
			if (codiPerfil == null)
				codiPerfil = getPerfil();

			if (codiPerfil == null) {
                throw new SistemaExternException("No s'ha especificat cap perfil per poder iniciar el procés de digitalització.");
            }
            var profileRequest = new MassiveScanWebSimpleProfileRequest(codiPerfil, idioma);
            var scanWebProfileSelected = getDigitalitzacioClient().getProfile(profileRequest);
            switch (scanWebProfileSelected.getProfileType()) {
                // Només escaneig
                case 1:
                    transacctionIdRequest = new MassiveScanWebSimpleGetTransactionIdRequest(transactionName, codiPerfil, view, idioma, funcionari.getCodi());
                    returnScannedFile = true;
                    returnSignedFile = false;
                    break;
                // Escaneig + firma
                case 2:
                    MassiveScanWebSimpleSignatureParameters signatureParameters;
                    signatureParameters = new MassiveScanWebSimpleSignatureParameters(idioma, funcionari.getNom(), funcionari.getNif());
                    transacctionIdRequest = new MassiveScanWebSimpleGetTransactionIdRequest(transactionName, profileCode, view, idioma, funcionari.getNom(), signatureParameters);
                    returnScannedFile = false;
                    returnSignedFile = true;
                    break;
            }
            if (transacctionIdRequest != null) {
                transaccioResponse = new DigitalitzacioTransaccioResposta();
                var idTransaccio = getDigitalitzacioClient().getTransactionID(transacctionIdRequest);
                var urlRedireccio = startTransaction(idTransaccio, urlReturn + idTransaccio);
                transaccioResponse.setIdTransaccio(idTransaccio);
                transaccioResponse.setUrlRedireccio(urlRedireccio);
                transaccioResponse.setReturnScannedFile(returnScannedFile);
                transaccioResponse.setReturnSignedFile(returnSignedFile);
            }
		} catch (Exception ex) {
			throw new SistemaExternException("El procés de digitalització ha fallat (" + "perfil=" + codiPerfil + ", " + "idioma=" + idioma + ", " + "urlReturn=" + urlReturn + ")", ex);
		}
		return transaccioResponse;
	}

	@Override
    public DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) throws SistemaExternException {

		var resposta = new DigitalitzacioResultat();
		try {
			var subs = getDigitalitzacioClient().getSubTransactionsOfTransaction(idTransaccio);
			List<String> subtransacions = subs.getSubtransacions();
			var subTransactionID = subtransacions.get(0);
			var resultRequest = new MassiveScanWebSimpleSubtransactionResultRequest(subTransactionID, returnScannedFile, returnSignedFile);
			var result = getDigitalitzacioClient().getSubTransactionResult(resultRequest);
			MassiveScanWebSimpleStatus transactionStatus = result.getStatus();
			if (isDebug()) {
				log.info(MassiveScanWebSimpleSubtransactionResult.toString(result));
			}
			var status = transactionStatus.getStatus();
            String errorMsg;
            switch (status) {
                case MassiveScanWebSimpleStatus.STATUS_REQUESTED_ID:
                    errorMsg = "S'ha rebut un estat inconsistent del procés" + " (requestedid). Pot ser el PLugin no està ben desenvolupat." + " Consulti amb el seu administrador.";
                    log.error(errorMsg);
                    resposta.setError(true);
                    resposta.setEstat(DigitalitzacioEstat.REQUESTED_ID);
                    return resposta;
                case MassiveScanWebSimpleStatus.STATUS_IN_PROGRESS:
                    errorMsg = "S'ha rebut un estat inconsistent del procés" + " (En Progrés). Pot ser el PLugin no està ben desenvolupat." + " Consulti amb el seu administrador.";
                    log.error(errorMsg);
                    resposta.setError(true);
                    resposta.setEstat(DigitalitzacioEstat.IN_PROGRESS);
                    return resposta;
                case MassiveScanWebSimpleStatus.STATUS_FINAL_ERROR:
                    errorMsg = "Error durant la realització de l'escaneig/còpia autèntica: " + transactionStatus.getErrorMessage();
                    resposta.setError(true);
                    resposta.setEstat(DigitalitzacioEstat.FINAL_ERROR);
                    String desc = transactionStatus.getErrorStackTrace();
                    if (desc != null) {
                        log.error(desc);
                    }
                    log.error(errorMsg);
                    return resposta;
                case MassiveScanWebSimpleStatus.STATUS_CANCELLED: {
                    errorMsg = "Durant el procés, l'usuari ha cancelat la transacció.";
                    log.error(errorMsg);
                    resposta.setError(true);
                    resposta.setEstat(DigitalitzacioEstat.CANCELLED);
                    return resposta;
                }
                case MassiveScanWebSimpleStatus.STATUS_FINAL_OK: {
                    resposta.setError(false);
                    if (result.getScannedFile() != null) {
                        errorMsg = "La recuperació del fitxer escanejat s'ha realitzat amb èxit.";
                        resposta.setContingut(result.getScannedFile().getData());
                        resposta.setNomDocument(result.getScannedFile().getNom());
                        resposta.setMimeType(result.getScannedFile().getMime());
                        log.debug(errorMsg);
                    }
                    if (result.getSignedFile() != null) {
                        errorMsg = "La recuperació del fitxer escanejat i firmat s'ha realitzat amb èxit.";
                        resposta.setContingut(result.getSignedFile().getData());
                        resposta.setNomDocument(result.getSignedFile().getNom());
                        resposta.setMimeType(result.getSignedFile().getMime());
                        resposta.setEniTipoFirma(result.getSignedFileInfo().getEniTipoFirma());
                        log.debug(errorMsg);
                    }
                    if (result.getScannedFileInfo() != null) {
                        resposta.setIdioma(result.getScannedFileInfo().getDocumentLanguage());
                        resposta.setResolucion(result.getScannedFileInfo().getPppResolution());
                    }
                }
                break;
			    default:
				    throw new SistemaExternException("Codi d'estat desconegut (" + status + ")");
			}
		} catch (Exception ex) {
			throw new SistemaExternException("S'ha produït un error recuperant el resultat de digitalització (" + "idTransaccio="
                    + idTransaccio + ", " + "returnScannedFile=" + returnScannedFile + ", " + "returnSignedFile=" + returnSignedFile + ")", ex);
		} finally {
			try {
				getDigitalitzacioClient().closeTransaction(idTransaccio);
			} catch (Exception ex) {
				throw new SistemaExternException("S'ha produït un error tancant la transacció", ex);
			}
		}
		return resposta;
	}

	public void tancarTransaccio(String idTransaccio) throws SistemaExternException {

		try {
			getDigitalitzacioClient().closeTransaction(idTransaccio);
		} catch (Exception ex) {
			throw new SistemaExternException("S'ha produït un error tancant la transacció", ex);
		}
	}

	private String startTransaction(String idTransaccio, String urlReturn) throws SistemaExternException {

        if (isDebug()) {
			log.info("Iniciant transacció " + idTransaccio);
		}		
		String urlRedireccio = null;
		try {
			var startTransactionInfo = new MassiveScanWebSimpleStartTransactionRequest(idTransaccio, urlReturn);
			urlRedireccio = getDigitalitzacioClient().startTransaction(startTransactionInfo);
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut iniciar la transacció (" + "transactionId=" + idTransaccio + ", " + "returnUrl=" + urlReturn + ")", ex);
		}
		return urlRedireccio;
	}

	private ApiMassiveScanWebSimple getDigitalitzacioClient() throws MalformedURLException {

		var apiRestUrl = getBaseUrl();
		var api = new ApiMassiveScanWebSimpleJersey(apiRestUrl, getUsername(), getPassword());
		return api;
	}
	
	private String getBaseUrl() {
		return properties.getProperty("es.caib.notib.plugin.digitalitzacio.digitalib.base.url");
	}

	private String getUsername() {
		return properties.getProperty("es.caib.notib.plugin.digitalitzacio.digitalib.username");
	}

	private String getPassword() {
		return properties.getProperty("es.caib.notib.plugin.digitalitzacio.digitalib.password");
	}

	private String getPerfil() {
		return properties.getProperty("es.caib.notib.plugin.digitalitzacio.digitalib.perfil");
	}

	private boolean isDebug() {
		return Boolean.parseBoolean(properties.getProperty("es.caib.notib.plugin.digitalitzacio.digitalib.log"));
	}


    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin, String codiEntiat) {
        salutPluginComponent.init(registry, codiPlugin, codiEntiat);
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return salutPluginComponent.teConfiguracioEspecifica();
    }

    @Override
    public EstatSalut getEstatPlugin() {
        return salutPluginComponent.getEstatPlugin();
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        return salutPluginComponent.getPeticionsPlugin();
    }
}