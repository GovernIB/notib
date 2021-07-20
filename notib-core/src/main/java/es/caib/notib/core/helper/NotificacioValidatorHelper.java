package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.EntitatEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

	public List<String> validarNotificacioMassiu(
			@NonNull NotificacioDatabaseDto notificacio,
			@NonNull EntitatEntity entitat,
			Map<String, Long> documentsProcessatsMassiu) {
		log.info("[NOT-VALIDACIO] Validació notificació de nova notificacio massiva");

		List<String> errors = new ArrayList<String>();
		boolean comunicacioSenseAdministracio = false;

		String emisorDir3Codi = notificacio.getEmisorDir3Codi(); //entitat.getDir3Codi() entidad actual

		Map<String, OrganismeDto> organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);

		// Emisor
		if (emisorDir3Codi == null || emisorDir3Codi.isEmpty()) {
			errors.add("[1000] El camp 'emisorDir3Codi' no pot ser null.");
		} else if (emisorDir3Codi.length() > 9) {
			errors.add("[1001] El camp 'emisorDir3Codi' no pot tenir una longitud superior a 9 caràcters.");
		}

		// Entitat
		if (entitat == null) {
			errors.add("[1010] No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 " + emisorDir3Codi + ". (emisorDir3Codi)");
		} else if (!entitat.isActiva()) {
			errors.add("[1011] L'entitat especificada està desactivada per a l'enviament de notificacions");
		}

		// Concepte
		if (notificacio.getConcepte() == null || notificacio.getConcepte().isEmpty()) {
			errors.add("[1030] El concepte de la notificació no pot ser null.");
		} else {
			if (notificacio.getConcepte().length() > 240) {
				errors.add("[1031] El concepte de la notificació no pot tenir una longitud superior a 240 caràcters.");
			}
			if (!validFormat(notificacio.getConcepte()).isEmpty()) {
				errors.add("[1032] El format del camp concepte no és correcte. Inclou els caràcters (" +
						listToString(validFormat(notificacio.getConcepte())) +") que no són correctes");
			}
		}
		// Tipus d'enviament
		if (notificacio.getEnviamentTipus() == null) {
			errors.add("[1050] El tipus d'enviament de la notificació no pot ser null.");
		}

		// Usuari
		if (notificacio.getUsuariCodi() == null || notificacio.getUsuariCodi().isEmpty()) {
			errors.add("[1070] El camp 'usuariCodi' no pot ser null (Requisit per fer el registre de sortida).");
		} else if (notificacio.getUsuariCodi().length() > 64) {
			errors.add("[1071] El camp 'usuariCodi' no pot pot tenir una longitud superior a 64 caràcters.");
		}

		// Enviaments
		if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
			errors.add("[1100] El camp 'enviaments' no pot ser null.");
		} else {
			for (NotificacioEnviamentDtoV2 enviament : notificacio.getEnviaments()) {
				//Si és comunicació a administració i altres mitjans (persona física/jurídica) --> Excepció
				if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
					if ((enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA) ||
							(enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.JURIDICA)) {
						comunicacioSenseAdministracio = true;
					}
				}
				boolean senseNif = true;

				// Servei tipus
				// Per defecte es posa a normal
				if (enviament.getServeiTipus() == null) {
					errors.add("[1101] El camp 'serveiTipus' d'un enviament no pot ser null.");
				}

				// Titular
				if (enviament.getTitular() == null) {
					errors.add("[1110] El titular d'un enviament no pot ser null.");
				} else {
					// - Nom
					if (enviament.getTitular().getNom() != null && enviament.getTitular().getNom().length() > 255) {
						errors.add("[1112] El camp 'nom' del titular no pot ser tenir una longitud superior a 255 caràcters.");
					}
					// - Llinatge 1
					if (enviament.getTitular().getLlinatge1() != null && enviament.getTitular().getLlinatge1().length() > 40) {
						errors.add("[1113] El camp 'llinatge1' del titular no pot ser major que 40 caràcters.");
					}
					// - Llinatge 2
					if (enviament.getTitular().getLlinatge2() != null && enviament.getTitular().getLlinatge2().length() > 40) {
						errors.add("[1114] El camp 'llinatge2' del titular no pot ser major que 40 caràcters.");
					}
					// - Nif
					if (enviament.getTitular().getNif() != null && enviament.getTitular().getNif().length() > 9) {
						errors.add("[1115] El camp 'nif' del titular d'un enviament no pot tenir una longitud superior a 9 caràcters.");
					}
					// - Tipus
					if (enviament.getTitular().getInteressatTipus() == null) {
						errors.add("[1111] El camp 'interessat_tipus' del titular d'un enviament no pot ser null.");
					}
					if (enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty()) {
						if (NifHelper.isvalid(enviament.getTitular().getNif())) {
							senseNif = false;
							switch (enviament.getTitular().getInteressatTipus()) {
								case FISICA:
									if (!NifHelper.isValidNifNie(enviament.getTitular().getNif())) {
										errors.add("[1123] El camp 'nif' del titular no és un tipus de document vàlid per a persona física. Només s'admet NIF/NIE.");
									}
									break;
								case JURIDICA:
									if (!NifHelper.isValidCif(enviament.getTitular().getNif())) {
										errors.add("[1123] El camp 'nif' del titular no és un tipus de document vàlid per a persona jurídica. Només s'admet CIF.");
									}
									break;
								case ADMINISTRACIO:
									break;
							}
						} else {
							errors.add("[1116] El 'nif' del titular no és vàlid.");
						}
					}
					// - Email
					if (enviament.getTitular().getEmail() != null && enviament.getTitular().getEmail().length() > 160) {
						errors.add("[1117] El camp 'email' del titular no pot ser major que 160 caràcters.");
					}
					if (enviament.getTitular().getEmail() != null && !isEmailValid(enviament.getTitular().getEmail())) {
						errors.add("[1118] El format del camp 'email' del titular no és correcte");
					}
					// - Telèfon
					if (enviament.getTitular().getTelefon() != null && enviament.getTitular().getTelefon().length() > 16) {
						errors.add("[1119] El camp 'telefon' del titular no pot ser major que 16 caràcters.");
					}
					// - Raó social
					if (enviament.getTitular().getRaoSocial() != null && enviament.getTitular().getRaoSocial().length() > 80) {
						errors.add("[1120] El camp 'raoSocial' del titular no pot ser major que 80 caràcters.");
					}
					// - Codi Dir3
					if (enviament.getTitular().getDir3Codi() != null && enviament.getTitular().getDir3Codi().length() > 9) {
						errors.add("[1121] El camp 'dir3Codi' del titular no pot ser major que 9 caràcters.");
					}
					// - Incapacitat
					if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
						errors.add("[1122] En cas de titular amb incapacitat es obligatori indicar un destinatari.");
					}
					//   - Persona física
					if (enviament.getTitular().getInteressatTipus() != null) {
						if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
							if (enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
								errors.add("[1130] El camp 'nom' de la persona física titular no pot ser null.");
							}
							if (enviament.getTitular().getLlinatge1() == null || enviament.getTitular().getLlinatge1().isEmpty()) {
								errors.add("[1131] El camp 'llinatge1' de la persona física titular d'un enviament no pot ser null en el cas de persones físiques.");
							}
							if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
								errors.add("[1132] El camp 'nif' de la persona física titular d'un enviament no pot ser null.");
							}
							//   - Persona jurídica
						} else if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
							if ((enviament.getTitular().getRaoSocial() == null || enviament.getTitular().getRaoSocial().isEmpty()) &&
									(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty())) {
								errors.add("[1140] El camp 'raoSocial/nom' de la persona jurídica titular d'un enviament no pot ser null.");
							}
							if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
								errors.add("[1141] El camp 'nif' de la persona jurídica titular d'un enviament no pot ser null.");
							}
							//   - Administració
						} else if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
							if (enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
								errors.add("[1150] El camp 'nom' de l'administració titular d'un enviament no pot ser null.");
							}
							if (enviament.getTitular().getDir3Codi() == null) {
								errors.add("[1151] El camp 'dir3codi' de l'administració titular d'un enviament no pot ser null.");
							}
							OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(enviament.getTitular().getDir3Codi());
							if (organDir3 == null) {
								errors.add("[1152] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") no es correspon a un codi Dir3 vàlid.");
							} else {
								if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
									if (organDir3.getSir() == null || !organDir3.getSir()) {
										errors.add("[1153] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") no disposa d'oficina SIR. És obligatori per a comunicacions.");
									}
									if (organigramaByEntitat.containsKey(enviament.getTitular().getDir3Codi())) {
										errors.add("[1154] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") fa referència a una administració de la pròpia entitat. No es pot utilitzar Notib per enviar comunicacions dins la pròpia entitat.");
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

				if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.NOTIFICACIO && senseNif) {
					errors.add("[1220] En una notificació, com a mínim un dels interessats ha de tenir el Nif informat.");
				}

				// Entrega postal
				if (enviament.isEntregaPostalActiva()) {
					if (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
						errors.add("[1231] El camp 'codiPostal' no pot ser null (indicar 00000 en cas de no disposar del codi postal).");
					}
					if (enviament.getEntregaPostal().getCodiPostal() != null && enviament.getEntregaPostal().getCodiPostal().length() > 10) {
						errors.add("[1242] El camp 'codiPostal' no pot contenir més de 10 caràcters).");
					}
					if (enviament.getEntregaPostal().getLinea1() != null && enviament.getEntregaPostal().getLinea1().length() > 50) {
						errors.add("[1248] El camp 'linea1' de l'entrega postal no pot contenir més de 50 caràcters.");
					}
					if (enviament.getEntregaPostal().getLinea2() != null && enviament.getEntregaPostal().getLinea2().length() > 50) {
						errors.add("[1249] El camp 'linea2' de l'entrega postal no pot contenir més de 50 caràcters.");
					}
					if (enviament.getEntregaPostal().getLinea1() == null || enviament.getEntregaPostal().getLinea1().isEmpty()) {
						errors.add("[1290] El camp 'linea1' no pot ser null.");
					}
					if (enviament.getEntregaPostal().getLinea2() == null || enviament.getEntregaPostal().getLinea2().isEmpty()) {
						errors.add("[1291] El camp 'linea2' no pot ser null.");
					}
				}

				// Entrega DEH de momento siempre es false
			}
		}

		// Procediment
		if (notificacio.getProcediment() != null && notificacio.getProcediment().getCodi() != null && notificacio.getProcediment().getCodi().length() > 9) {
			errors.add("[1021] El camp 'procedimentCodi' no pot tenir una longitud superior a 9 caràcters.");
		}

		if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.NOTIFICACIO ) {
			if (notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) {
				errors.add("[1020] El camp 'procedimentCodi' no pot ser null.");
			}
		} else if ((notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null)
				&& notificacio.getOrganGestorCodi() == null){
			errors.add("[1022] El camp 'organ gestor' no pot ser null en una comunicació amb l'administració on no s'especifica un procediment.");
		}
		if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO && comunicacioSenseAdministracio) {
			if (notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) {
				errors.add("[1020] El camp 'procedimentCodi' no pot ser null.");
			}
		}
		if (!organigramaByEntitat.containsKey(notificacio.getOrganGestorCodi())) {
			errors.add("[1023] El camp 'organ gestor' no es correspon a cap Òrgan Gestor de l'entitat especificada.");
		}
		//TODO: está fallando en REST y aquí esta validación. Es correcto???
		// Respuesta: Por ahora no pongas esta validación. Lo consultaré con la DGTIC. Pero el problema no es la validación. Son los datos utilizados.
		// Otra cosa es que la validación NO se tiene que hacer si notificacio.getOrganGestor() == null en el caso de REST.
		// Entiendo que para CSV siempre tienen que enviarla.
		// En el CSV me indican órgano y procedimiento. Si el procedimiento no es común y es de otro órgano => error
//		if (notificacio.getProcediment() != null && !notificacio.getProcediment().isComu()) {
//		 if (notificacio.getOrganGestorCodi() != notificacio.getProcediment().getOrganGestor()) {
//				errors.add("[1024] El camp 'organ gestor' no es correspon a l'òrgan gestor de l'procediment.");
//			}
//		}

		// Documents
		DocumentDto document = notificacio.getDocument();
		if (document == null) {
			errors.add("[1060] El camp 'document' no pot ser null.");
		} else {
			if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
				errors.add("[1072] El camp 'arxiuNom' no pot pot tenir una longitud superior a 200 caràcters.");
			}

			if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
					(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) &&
							documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {
				if ((document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
						(document.getCsv() == null || document.getCsv().isEmpty()) &&
						(document.getUrl() == null || document.getUrl().isEmpty()) &&
						(document.getUuid() == null || document.getUuid().isEmpty())) {
					errors.add("[1062] És necessari incloure un document (contingutBase64, CSV, UUID o URL) a la notificació.");
				}
			}

			if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
					(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) &&
							documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {

				if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
					if (!isFormatValid(document.getContingutBase64())) {
						errors.add("[1063] El format del document no és vàlid. Les notificacions i comunicacions a ciutadà només admeten els formats PDF i ZIP.");
					}
					if (document.getMida() > getMaxSizeFile()) {
						errors.add("[1065] La longitud del document supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
					}
				}

				// Metadades
				if ((document.getContingutBase64() != null && !document.getContingutBase64().isEmpty())
						&& registreNotificaHelper.isSendDocumentsActive()) {
					if (document.getOrigen() == null) {
						errors.add("[1066] Error en les metadades del document. No està informat l'ORIGEN del document");
					}
					if (document.getValidesa() == null) {
						errors.add("[1066] Error en les metadades del document. No està informat la VALIDESA(Estat elaboració) del document");
					}
					if (document.getTipoDocumental() == null) {
						errors.add("[1066] Error en les metadades del document. No està informat el TIPUS DOCUMENTAL del document");
					}
					if (document.getArxiuNom().toUpperCase().endsWith("PDF") && document.getModoFirma() == null) {
						errors.add("[1066] Error en les metadades del document. No està informat el MODE de FIRMA del document tipus PDF");
					}
				}
			}
		}
		return errors;
	}


	private ArrayList<Character> validFormat(String value) {
		String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";
		ArrayList<Character> charsNoValids = new ArrayList<Character>();
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

	private StringBuilder listToString(ArrayList<?> list) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			str.append(list.get(i));
		}
		return str;
	}

	private boolean isEmailValid(String email) {
		boolean valid = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (Exception e) {
			valid = false; //no vàlid
		}
		return valid;
	}

	private boolean isFormatValid(String docBase64) {
		boolean valid = true;
		String[] formatsValids = {"JVBERi0","UEsDBBQAAAAIA"}; //PDF / ZIP

		if (!(docBase64.startsWith(formatsValids[0]) || docBase64.startsWith(formatsValids[1])))
			valid = false;

		return valid;
	}

	private Long getMaxSizeFile() {
		return configHelper.getAsLong("es.caib.notib.notificacio.document.size");
	}

}
