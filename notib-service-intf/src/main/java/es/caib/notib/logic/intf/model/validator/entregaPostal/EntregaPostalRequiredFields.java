package es.caib.notib.logic.intf.model.validator.entregaPostal;

import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.EntregaPostalResource;
import es.caib.notib.logic.intf.model.validator.ValidatorHelper;
import es.caib.notib.logic.intf.service.ConfigService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * Valida els camps obligatoris en funció del valor del camp source.
 *
 * @author Límit Tecnologies
 */
@Component
@AllArgsConstructor
public class EntregaPostalRequiredFields extends ValidatorHelper implements CustomValidator<EntregaPostalResource> {

	private final ConfigService configService;

	private final String CARACTERS_NO_PERMESOS_MSG = "entregapostal.form.valid.caracters.no.permesos";

	@Override
	public boolean validate(EntregaPostalResource value, ConstraintValidatorContext context) {

		if (value == null) {
			context.disableDefaultConstraintViolation();
			return true;
		}
		boolean valid = true;
		switch (value.getDomiciliConcretTipus()) {
			case NACIONAL:
				valid = validNacional(value, context);
				break;
			case ESTRANGER:
				valid = validEstranger(value, context);
				break;
			case APARTAT_CORREUS:
				valid = validApartatCorreus(value, context);
				break;
			case SENSE_NORMALITZAR:
				valid = validSenseNormalitar(value, context);
				break;
			default:
		}

		context.disableDefaultConstraintViolation();
		return valid;
	}

	private boolean validNacional(EntregaPostalResource value, ConstraintValidatorContext context) {

		var valid = true;
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliViaTipus, value.getDomiciliViaTipus(), valid, context);
		var msg = "entregapostal.form.valid.nacional.notempty";
		if (StringUtils.isBlank(value.getDomiciliViaNom())) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliViaNom, context, msg);
		} else {
			var charsNoValids = validFormatCampEntregaPostal(value.getDomiciliViaNom());
			if (!charsNoValids.isEmpty()) {
				valid = addMessage(EntregaPostalResource.Fields.domiciliViaNom, context, CARACTERS_NO_PERMESOS_MSG, charsNoValids);
			}
		}
		if (StringUtils.isEmpty(value.getDomiciliNumeracioNumero()) && StringUtils.isEmpty(value.getDomiciliNumeracioPuntKm())) {
			var message = "entregapostal.form.valid.nacional.puntkm.numcasa";
			valid = false;
			addMessage(EntregaPostalResource.Fields.domiciliNumeracioNumero, context, message);
			addMessage(EntregaPostalResource.Fields.domiciliNumeracioPuntKm, context, message);
		}
		if (!StringUtils.isEmpty(value.getDomiciliNumeracioPuntKm()) && !StringUtils.isEmpty(value.getDomiciliNumeracioNumero())) {
			var message = "entregapostal.form.valid.nacional.punkkm.numcasa.plens";
			valid = false;
			addMessage(EntregaPostalResource.Fields.domiciliNumeracioNumero, context, message);
			addMessage(EntregaPostalResource.Fields.domiciliNumeracioPuntKm, context, message);
		}
		if (StringUtils.isEmpty(value.getDomiciliCodiPostal()) || value.getDomiciliCodiPostal().length() != 5) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliCodiPostal, context, "entregapostal.form.valid.codi.postal.buit");
		}
		if (StringUtils.isBlank(value.getDomiciliPoblacio())) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliPoblacio, context, msg);
		} else {
			var charsNoValids = validFormatCampEntregaPostal(value.getDomiciliPoblacio());
			if (!charsNoValids.isEmpty()) {
				valid = addMessage(EntregaPostalResource.Fields.domiciliPoblacio, context, CARACTERS_NO_PERMESOS_MSG, charsNoValids);
			}
		}
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliPaisCodiIso, value.getDomiciliPaisCodiIso(), valid, context, msg);
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliProvinciaCodi, value.getDomiciliProvinciaCodi(), valid, context, msg);
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliMunicipiCodiIne, value.getDomiciliMunicipiCodiIne(), valid, context, msg);

		return valid;
	}

	private boolean validEstranger(EntregaPostalResource value, ConstraintValidatorContext context) {

		var valid = true;
		var msg = "entregapostal.form.valid.estranger.notempty";
		if (StringUtils.isBlank(value.getDomiciliViaNom())) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliViaNom, context, msg);
		} else {
			var charsNoValids = validFormatCampEntregaPostal(value.getDomiciliViaNom());
			if (!charsNoValids.isEmpty()) {
				valid = addMessage(EntregaPostalResource.Fields.domiciliViaNom, context, CARACTERS_NO_PERMESOS_MSG, charsNoValids);
			}
		}
		if (StringUtils.isBlank(value.getDomiciliPoblacio())) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliPoblacio, context, msg);
		} else {
			var charsNoValids = validFormatCampEntregaPostal(value.getDomiciliPoblacio());
			if (!charsNoValids.isEmpty()) {
				valid = addMessage(EntregaPostalResource.Fields.domiciliPoblacio, context, CARACTERS_NO_PERMESOS_MSG, charsNoValids);
			}
		}
		if (StringUtils.isEmpty(value.getDomiciliCodiPostal()) || value.getDomiciliCodiPostal().length() != 5) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliNumeracioNumero, context, "entregapostal.form.valid.codi.postal.buit");
		}

		valid = validateNotNull(EntregaPostalResource.Fields.domiciliPaisCodiIso, value.getDomiciliPaisCodiIso(), valid, context);

		return valid;
	}

	private boolean validApartatCorreus(EntregaPostalResource value, ConstraintValidatorContext context) {

		var valid = true;
		var msg = "entregapostal.form.valid.apcorreu.notempty";
		if (StringUtils.isEmpty(value.getDomiciliCodiPostal()) || value.getDomiciliCodiPostal().length() > 5) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliNumeracioNumero, context, "entregapostal.form.valid.codi.postal.buit");
		}
		if (StringUtils.isBlank(value.getDomiciliPoblacio())) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliPoblacio, context, msg);
		} else {
			var charsNoValids = validFormatCampEntregaPostal(value.getDomiciliPoblacio());
			if (!charsNoValids.isEmpty()) {
				valid = addMessage(EntregaPostalResource.Fields.domiciliPoblacio, context, CARACTERS_NO_PERMESOS_MSG, charsNoValids);
			}
		}
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliApartatCorreus, value.getDomiciliApartatCorreus(), valid, context, msg);
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliPaisCodiIso, value.getDomiciliPaisCodiIso(), valid, context, msg);
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliProvinciaCodi, value.getDomiciliProvinciaCodi(), valid, context, msg);
		valid = validateNotNull(EntregaPostalResource.Fields.domiciliMunicipiCodiIne, value.getDomiciliMunicipiCodiIne(), valid, context, msg);

		return valid;
	}

	private boolean validSenseNormalitar(EntregaPostalResource value, ConstraintValidatorContext context) {

		var valid = true;
		var msg = "entregapostal.form.valid.no.normalitzat.notempty";
		if (StringUtils.isBlank(value.getDomiciliLinea1())) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliLinea1, context, msg);
		} else {
			var charsNoValids = validFormatCampEntregaPostal(value.getDomiciliLinea1());
			if (!charsNoValids.isEmpty()) {
				valid = addMessage(EntregaPostalResource.Fields.domiciliLinea1, context, CARACTERS_NO_PERMESOS_MSG, charsNoValids);
			}
		}
		if (StringUtils.isBlank(value.getDomiciliLinea2())) {
			valid = addMessage(EntregaPostalResource.Fields.domiciliLinea2, context, msg);
		} else {
			var charsNoValids = validFormatCampEntregaPostal(value.getDomiciliLinea2());
			if (!charsNoValids.isEmpty()) {
				addMessage(EntregaPostalResource.Fields.domiciliLinea2, context, CARACTERS_NO_PERMESOS_MSG, charsNoValids);
			}
		}
		if (StringUtils.isBlank(value.getDomiciliCodiPostal()) || value.getDomiciliCodiPostal().length() > 5) {
			valid = false;
			addMessage(EntregaPostalResource.Fields.domiciliCodiPostal, context, "entregapostal.form.valid.codi.postal.buit");
		}
		return valid;
	}

	private Set<Character> validFormatCampEntregaPostal(String value) {

		String CONTROL_CARACTERS = " 0123456789(),/-_.;ªºÑÇñçÁÉÍÓÚÀÈÌÒÙáéíóúàèìòùüABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		var apostrofPermes = Boolean.parseBoolean(configService.getPropertyValue("es.caib.notib.notifica.apostrof.permes"));
		CONTROL_CARACTERS += apostrofPermes ? "'" : "";
		Set<Character> charsNoValids = new HashSet<>();
		char[] chars = value.replace("\n", "").replace("\r", "").toCharArray();

		boolean esCaracterValid = true;
		for (int i = 0; i < chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(chars[i]) < 0);
			if (!esCaracterValid) {
				charsNoValids.add(chars[i]);
			}
		}
		return charsNoValids;
	}

}
