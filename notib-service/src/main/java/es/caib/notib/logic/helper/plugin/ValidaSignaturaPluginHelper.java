package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ValidaSignaturaPluginHelper extends AbstractPluginHelper<IValidateSignaturePlugin> {

	private final IntegracioHelper integracioHelper;
	private final ConfigHelper configHelper;

	public ValidaSignaturaPluginHelper(IntegracioHelper integracioHelper,
                                       ConfigHelper configHelper) {
		super(integracioHelper, configHelper);
		this.integracioHelper = integracioHelper;
		this.configHelper = configHelper;
	}


	public SignatureInfoDto detectSignedAttachedUsingValidateSignaturePlugin(byte[] documentContingut, String nom, String firmaContentType) {

		var info = new IntegracioInfo(IntegracioCodiEnum.VALIDASIG, "Validació firmes de document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Nom del document", nom), new AccioParam("ContentType", firmaContentType));
		try {
			var validationRequest = new ValidateSignatureRequest();
			validationRequest.setSignatureData(documentContingut);
			var sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(true);
			validationRequest.setSignatureRequestedInformation(sri);
			peticionsPlugin.updatePeticioTotal(getCodiEntitatActual());
			var validateSignatureResponse = getPlugin().validateSignature(validationRequest);

			var validationStatus = validateSignatureResponse.getValidationStatus();
			var signatureInfoDto = validationStatus.getStatus() == 1 ? SignatureInfoDto.builder().signed(true).error(false).build()
					: SignatureInfoDto.builder().signed(true).error(true).errorMsg(validationStatus.getErrorMsg()).build();
			info.addParam("Document firmat", Boolean.toString(signatureInfoDto.isSigned()));
			info.addParam("Error de firma", Boolean.toString(signatureInfoDto.isError()));
			if (signatureInfoDto.isError()) {
				info.addParam("Missatge d'error", signatureInfoDto.getErrorMsg());
			}
			integracioHelper.addAccioOk(info);
			return signatureInfoDto;
		} catch (Exception e) {
			var throwable = ExceptionUtils.getRootCause(e) != null ? ExceptionUtils.getRootCause(e) : e;
			var isNull = throwable == null || Strings.isNullOrEmpty(throwable.getMessage());
			if (!isNull && (throwable.getMessage().contains("El formato de la firma no es valido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)")
					|| throwable.getMessage().contains("El formato de la firma no es válido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)")
					|| throwable.getMessage().contains("El documento OOXML no está firmado(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)"))) {

				info.addParam("Document firmat", "false");
				info.addParam("Error de firma", "false");
				info.addParam("Missatge d'error", throwable.getMessage());
				integracioHelper.addAccioOk(info);
				return SignatureInfoDto.builder().signed(false).error(false).build();
			}
			log.error("Error al detectar firma de document", e);
			integracioHelper.addAccioError(info, "Error al validar la firma", throwable);
			peticionsPlugin.updatePeticioError(getCodiEntitatActual());
			return SignatureInfoDto.builder().signed(false).error(true).errorMsg(e.getMessage()).build();
		}
	}

	@Override
	protected IValidateSignaturePlugin getPlugin() {

		NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] Instanciant plugin de validacio de signatura", log, LoggingTipus.VALIDATE_SIGNATURE);
		var entitatCodi = getCodiEntitatActual();
		NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] Entitat codi " + entitatCodi, log, LoggingTipus.VALIDATE_SIGNATURE);
		var plugin = pluginMap.get(entitatCodi);
		if (plugin != null) {
			NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] Plugin previament instanciat per la entitat " + entitatCodi, log, LoggingTipus.VALIDATE_SIGNATURE);
			return plugin;
		}
		var pluginClass = getPluginClassProperty();
		NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] Plugin class " + pluginClass, log, LoggingTipus.VALIDATE_SIGNATURE);
		if (Strings.isNullOrEmpty(pluginClass)) {
			var error = "No està configurada la classe per al plugin de validació de firma";
			log.error(error);
			throw new SistemaExternException(IntegracioCodiEnum.VALIDASIG.name(), error);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] Innstanciant plugin per la entitat " + entitatCodi, log, LoggingTipus.VALIDATE_SIGNATURE);
			var properties = configHelper.getAllEntityProperties(entitatCodi);
			var endpoint = properties.get("es.caib.notib.plugins.validatesignature.afirmacxf.endpoint");
			var username = properties.get("es.caib.notib.plugins.validatesignature.afirmacxf.authorization.username");
			var transformersPath = properties.get("es.caib.notib.plugins.validatesignature.afirmacxf.TransformersTemplatesPath");
			NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] Endpoint " + endpoint, log, LoggingTipus.VALIDATE_SIGNATURE);
			NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] Username " + username, log, LoggingTipus.VALIDATE_SIGNATURE);
			NotibLogger.getInstance().info("[VALIDATE_SIGNATURE] TransformersTemplatesPath " + transformersPath, log, LoggingTipus.VALIDATE_SIGNATURE);
			plugin = (IValidateSignaturePlugin) clazz.getDeclaredConstructor(String.class, Properties.class).newInstance(ConfigDto.prefix + ".", properties);
			pluginMap.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioCodiEnum.VALIDASIG.name(), "Error al crear la instància del plugin de validació de signatures", ex);
		}
	}

	@Override
	protected EstatSalutEnum getEstat() {
		// TODO: Petició per comprovar la salut
		return EstatSalutEnum.UP;
	}


	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.validatesignature.class");
	}

}
