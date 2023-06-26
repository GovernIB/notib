package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.util.NifHelper;
import es.caib.notib.logic.utils.MimeUtils;
import es.caib.notib.persist.entity.EntitatEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper per notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificacioValidatorHelper {

	@Resource
	private CacheHelper cacheHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Autowired
	private RegistreNotificaHelper registreNotificaHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MessageHelper messageHelper;

	public List<String> validarNotificacioMassiu(@NonNull NotificacioDatabaseDto notificacio, @NonNull EntitatEntity entitat, Map<String, Long> documentsProcessatsMassiu) {

		log.info("[NOT-VALIDACIO] Validació notificació de nova notificacio massiva");
		var errors = notificacio.getErrors();
		var emisorDir3Codi = notificacio.getEmisorDir3Codi(); //entitat.getDir3Codi() entidad actual
		var organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);

		// Emisor
		if (emisorDir3Codi == null || emisorDir3Codi.isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.1000"));
		} else if (emisorDir3Codi.length() > 9) {
			errors.add(messageHelper.getMessage("error.validacio.1001"));
		}

		// Entitat
		if (entitat == null) {
			errors.add(messageHelper.getMessage("error.validacio.1010", new Object[] {emisorDir3Codi}));
		} else if (!entitat.isActiva()) {
			errors.add(messageHelper.getMessage("error.validacio.1011"));
		}

		// Concepte
		if (notificacio.getConcepte() == null || notificacio.getConcepte().isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.1030"));
		} else {
			if (notificacio.getConcepte().length() > 240) {
				errors.add(messageHelper.getMessage("error.validacio.1031"));
			}
			var caractersNoValids = validFormat(notificacio.getConcepte());
			if (!caractersNoValids.isEmpty()) {
				errors.add(messageHelper.getMessage("error.validacio.1032", new Object[]{StringUtils.join(caractersNoValids, ',')}));
			}
		}

		// Descripció
		var desc = notificacio.getDescripcio();
		if (!Strings.isNullOrEmpty(desc)) {
			if (desc.length() > 1000) {
				errors.add(messageHelper.getMessage("error.validacio.1040"));
			}
			List<Character> caractersNoValids = validFormat(notificacio.getConcepte());
			if (!caractersNoValids.isEmpty()) {
				errors.add(messageHelper.getMessage("error.validacio.1041", new Object[]{StringUtils.join(caractersNoValids, ',')}));
			}
		}

		// Tipus d'enviament
		if (notificacio.getEnviamentTipus() == null) {
			errors.add(messageHelper.getMessage("error.validacio.1050"));
		}

		// Usuari
		if (notificacio.getUsuariCodi() == null || notificacio.getUsuariCodi().isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.1060"));
		} else if (notificacio.getUsuariCodi().length() > 64) {
			errors.add(messageHelper.getMessage("error.validacio.1061"));
		}

		// Enviaments
		if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.1100"));
		} else {
			for (var enviament : notificacio.getEnviaments()) {
				//Si és comunicació a administració i altres mitjans (persona física/jurídica) --> Excepció
				var senseNif = true;

				// Servei tipus
				// Per defecte es posa a normal
				if (enviament.getServeiTipus() == null) {
					errors.add(messageHelper.getMessage("error.validacio.1101", new Object[]{""}));
				}

				// Titular
				if (enviament.getTitular() == null) {
					errors.add(messageHelper.getMessage("error.validacio.1110", new Object[]{""}));
				} else {
					// - Nom
					if (enviament.getTitular().getNom() != null && enviament.getTitular().getNom().length() > 255) {
						errors.add(messageHelper.getMessage("error.validacio.1132", new Object[]{"", enviament.getTitular().getInteressatTipus(), 255}));
					}
					// - Llinatge 1
					if (enviament.getTitular().getLlinatge1() != null && enviament.getTitular().getLlinatge1().length() > 30) {
						errors.add(messageHelper.getMessage("error.validacio.1134", new Object[]{"", enviament.getTitular().getInteressatTipus(), 30}));
					}
					// - Llinatge 2
					if (enviament.getTitular().getLlinatge2() != null && enviament.getTitular().getLlinatge2().length() > 30) {
						errors.add(messageHelper.getMessage("error.validacio.1135", new Object[]{"", enviament.getTitular().getInteressatTipus(), 30}));
					}
					// - Nif
					if (enviament.getTitular().getNif() != null && enviament.getTitular().getNif().length() > 9) {
						errors.add(messageHelper.getMessage("error.validacio.1137", new Object[]{"", enviament.getTitular().getInteressatTipus(), 9}));
					}
					// - Tipus
					if (enviament.getTitular().getInteressatTipus() == null) {
						errors.add(messageHelper.getMessage("error.validacio.1130", new Object[]{""}));
					}
					if (!InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())
							&& enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty()) {

						var nif = enviament.getTitular().getNif();
						if (NifHelper.isvalid(nif)) {
							senseNif = false;
							switch (enviament.getTitular().getInteressatTipus()) {
								case FISICA:
									if (!NifHelper.isValidNifNie(nif)) {
										errors.add(messageHelper.getMessage("error.validacio.1138", new Object[]{"", "fisica", "Només s'admet NIF/NIE"}));
									}
									break;
								case JURIDICA:
									if (!NifHelper.isValidCif(nif)) {
										errors.add(messageHelper.getMessage("error.validacio.1138", new Object[]{"", "juridica", "Només s'admet CIF"}));
									}
									break;
								case ADMINISTRACIO:
									break;
								default:
									break;
							}
						} else {
							errors.add(messageHelper.getMessage("error.validacio.1138", new Object[]{"", "fisica", ""}));
						}
					}
					// - Email
					var email = enviament.getTitular().getEmail();
					if (email != null && email.length() > 160) {
						errors.add(messageHelper.getMessage("error.validacio.1139", new Object[]{""}));
					}
					if (email != null && (!EmailHelper.isEmailValid(email) || !isEmailValid(email))) {
						errors.add(messageHelper.getMessage("error.validacio.1140", new Object[]{"", 160}));
					}
					// - Telèfon
					if (enviament.getTitular().getTelefon() != null && enviament.getTitular().getTelefon().length() > 16) {
						errors.add(messageHelper.getMessage("error.validacio.1141", new Object[]{"", 16}));
					}
					// - Raó social
					if (enviament.getTitular().getRaoSocial() != null && enviament.getTitular().getRaoSocial().length() > 80) {
						errors.add(messageHelper.getMessage("error.validacio.1143", new Object[]{"", 80}));
					}
					// - Codi Dir3
					if (enviament.getTitular().getDir3Codi() != null && enviament.getTitular().getDir3Codi().length() > 9) {
						errors.add(messageHelper.getMessage("error.validacio.1145", new Object[]{"", 9}));
					}
					// - Incapacitat
					if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
						errors.add(messageHelper.getMessage("error.validacio.1111", new Object[]{""}));
					}
					//   - Persona física
					if (enviament.getTitular().getInteressatTipus() != null) {
						if (enviament.getTitular().getInteressatTipus().equals(InteressatTipus.FISICA)) {
							if (enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.1131", new Object[]{"", "fisica"}));
							}
							if (enviament.getTitular().getLlinatge1() == null || enviament.getTitular().getLlinatge1().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.1133", new Object[]{"", "fisica"}));
							}
							if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.1136", new Object[]{"", "fisica"}));
							}
							//   - Persona jurídica
						} else if (enviament.getTitular().getInteressatTipus().equals(InteressatTipus.JURIDICA)) {
							if ((enviament.getTitular().getRaoSocial() == null || enviament.getTitular().getRaoSocial().isEmpty()) &&
									(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty())) {
								errors.add(messageHelper.getMessage("error.validacio.1142", new Object[]{"", "juridica"}));
							}
							if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.1136", new Object[]{"", "juridica"}));
							}
							//   - Administració
						} else if (enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
							if (enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.1131", new Object[]{"", "administracio"}));
							}
							if (enviament.getTitular().getDir3Codi() == null) {
								errors.add(messageHelper.getMessage("error.validacio.1144", new Object[]{"", "administracio"}));
							}
							OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(enviament.getTitular().getDir3Codi());
							if (organDir3 == null) {
								errors.add(messageHelper.getMessage("error.validacio.1146", new Object[]{"", enviament.getTitular().getDir3Codi()}));
							} else {
								if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
									if (organDir3.getSir() == null || !organDir3.getSir()) {
										errors.add(messageHelper.getMessage("error.validacio.1147", new Object[]{"", enviament.getTitular().getDir3Codi()}));
									}
									if (organigramaByEntitat.containsKey(enviament.getTitular().getDir3Codi())) {
										errors.add(messageHelper.getMessage("error.validacio.1148", new Object[]{"", enviament.getTitular().getDir3Codi()}));
									}
								}
								if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
									enviament.getTitular().setNif(organDir3.getCif());
								}
							}
						}
					}
				}
				// Destinataris.
				// De momento se trata cada línea como 1 notificación con 1 envío y 1 titular

				if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.NOTIFICACIO
						&& !InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) && senseNif) {
					errors.add(messageHelper.getMessage("error.validacio.1102", new Object[]{""}));
				}

				// Entrega postal
				if (enviament.isEntregaPostalActiva()) {
					if (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
						errors.add(messageHelper.getMessage("error.validacio.codi.postal.no.null"));
					}
					if (enviament.getEntregaPostal().getCodiPostal() != null && enviament.getEntregaPostal().getCodiPostal().length() > 10) {
						errors.add(messageHelper.getMessage("error.validacio.codi.postal.longitud.max"));
					}
					if (enviament.getEntregaPostal().getLinea1() != null && enviament.getEntregaPostal().getLinea1().length() > 50) {
						errors.add(messageHelper.getMessage("error.validacio.linea1.entrega.postal.longitud.max"));
					}
					if (enviament.getEntregaPostal().getLinea2() != null && enviament.getEntregaPostal().getLinea2().length() > 50) {
						errors.add(messageHelper.getMessage("error.validacio.linea2.entrega.postal.longitud.max"));
					}
					if (enviament.getEntregaPostal().getLinea1() == null || enviament.getEntregaPostal().getLinea1().isEmpty()) {
						errors.add(messageHelper.getMessage("error.validacio.linea1.entrega.postal.no.null"));
					}
					if (enviament.getEntregaPostal().getLinea2() == null || enviament.getEntregaPostal().getLinea2().isEmpty()) {
						errors.add(messageHelper.getMessage("error.validacio.linea2.entrega.postal.no.null"));
					}
				}

				// Entrega DEH de momento siempre es false
			}
		}

		// Procediment
		if (notificacio.getProcediment() != null && notificacio.getProcediment().getCodi() != null && notificacio.getProcediment().getCodi().length() > 9) {
			errors.add(messageHelper.getMessage("error.validacio.1021"));
		}

		if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.NOTIFICACIO &&
				(notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null)) {

			errors.add(messageHelper.getMessage("error.validacio.1020"));
		}
		if (!organigramaByEntitat.containsKey(notificacio.getOrganGestorCodi())) {
			errors.add(messageHelper.getMessage("error.validacio.1027"));
		}

		// Documents
		var document = notificacio.getDocument();
		if (document == null) {
			errors.add(messageHelper.getMessage("error.validacio.1070"));
			return errors;
		}
		if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
			errors.add(messageHelper.getMessage("error.validacio.1072", new Object[]{""}));
		}

		if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
			(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && documentsProcessatsMassiu.get(document.getArxiuNom()) == null) &&
			((document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
			(document.getCsv() == null || document.getCsv().isEmpty()) &&
			(document.getUuid() == null || document.getUuid().isEmpty()))) {

			errors.add(messageHelper.getMessage("error.validacio.1073", new Object[]{""}));
		}

		if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
				(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {

			if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
				if (!MimeUtils.isFormatValid(document.getMediaType(), document.getContingutBase64())) {
					errors.add(messageHelper.getMessage("error.validacio.1075", new Object[]{""}));
				}
				if (document.getMida() > getMaxSizeFile()) {
					errors.add(messageHelper.getMessage("error.validacio.1085", new Object[]{getMaxSizeFile() / (1024*1024)}));
				}
			}

			// Metadades
			if ((document.getContingutBase64() != null && !document.getContingutBase64().isEmpty())
					&& registreNotificaHelper.isSendDocumentsActive()) {
				if (document.getOrigen() == null) {
					errors.add(messageHelper.getMessage("error.validacio.1080", new Object[]{""}));
				}
				if (document.getValidesa() == null) {
					errors.add(messageHelper.getMessage("error.validacio.1081", new Object[]{""}));
				}
				if (document.getTipoDocumental() == null) {
					errors.add(messageHelper.getMessage("error.validacio.1082", new Object[]{""}));
				}
				if (document.getArxiuNom().toUpperCase().endsWith("PDF") && document.getModoFirma() == null) {
					errors.add(messageHelper.getMessage("error.validacio.1083", new Object[]{""}));
				}
			}
		}
		return errors;
	}


	private ArrayList<Character> validFormat(String value) {

		var CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";
		ArrayList<Character> charsNoValids = new ArrayList<>();
		var chars = value.replace("\n", "").replace("\r", "").toCharArray();
		var esCaracterValid = true;
		for (char aChar : chars) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(aChar) < 0);
			if (!esCaracterValid) {
				charsNoValids.add(aChar);
			}
		}
		return charsNoValids;
	}


	private boolean isEmailValid(String email) {

		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
			return true;
		} catch (Exception e) {
			return false; //no vàlid
		}
	}

	private Long getMaxSizeFile() {
		return configHelper.getConfigAsLong("es.caib.notib.notificacio.document.size");
	}

}
