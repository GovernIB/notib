package es.caib.notib.core.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.utils.MimeUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
		List<String> errors = notificacio.getErrors();
		boolean comunicacioSenseAdministracio = false;
		String emisorDir3Codi = notificacio.getEmisorDir3Codi(); //entitat.getDir3Codi() entidad actual
		Map<String, OrganismeDto> organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);

		// Emisor
		if (emisorDir3Codi == null || emisorDir3Codi.isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.emisordir3codi.no.null"));
		} else if (emisorDir3Codi.length() > 9) {
			errors.add(messageHelper.getMessage("error.validacio.emisordir3codi.longitud.max"));
		}

		// Entitat
		if (entitat == null) {
			errors.add(
					messageHelper.getMessage("error.validacio.entitat.no.configurada.amb.codidir3.a")
							+ emisorDir3Codi +
							messageHelper.getMessage("error.validacio.entitat.no.configurada.amb.codidir3.b"));
		} else if (!entitat.isActiva()) {
			errors.add(messageHelper.getMessage("error.validacio.entitat.desactivada.per.enviament.notificacions"));
		}

		// Concepte
		if (notificacio.getConcepte() == null || notificacio.getConcepte().isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.concepte.no.null"));
		} else {
			if (notificacio.getConcepte().length() > 240) {
				errors.add(messageHelper.getMessage("error.validacio.concepte.longitud.max"));
			}
			List<Character> caractersNoValids = validFormat(notificacio.getConcepte());
			if (!caractersNoValids.isEmpty()) {
				errors.add(messageHelper.getMessage("error.validacio.concepte.format.invalid.a") +
						StringUtils.join(caractersNoValids, ',')
						+ messageHelper.getMessage("error.validacio.concepte.format.invalid.b"));
			}
		}

		// Descripció
		String desc = notificacio.getDescripcio();
		if (!Strings.isNullOrEmpty(desc)) {
			if (desc.length() > 1000) {
				errors.add(messageHelper.getMessage("error.validacio.descripcio.notificacio.longitud.max"));
			}
			List<Character> caractersNoValids = validFormat(notificacio.getConcepte());
			if (!caractersNoValids.isEmpty()) {
				errors.add(messageHelper.getMessage("error.validacio.descripcio.invalid.a") +
						StringUtils.join(caractersNoValids, ',')
						+ messageHelper.getMessage("error.validacio.descripcio.invalid.b"));
			}
		}

		// Tipus d'enviament
		if (notificacio.getEnviamentTipus() == null) {
			errors.add(messageHelper.getMessage("error.validacio.tipus.enviament.no.null"));
		}

		// Usuari
		if (notificacio.getUsuariCodi() == null || notificacio.getUsuariCodi().isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.usuari.codi.no.null"));
		} else if (notificacio.getUsuariCodi().length() > 64) {
			errors.add(messageHelper.getMessage("error.validacio.usuari.codi.longitud.max"));
		}

		// Enviaments
		if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
			errors.add(messageHelper.getMessage("error.validacio.enviaments.no.null"));
		} else {
			for (NotEnviamentDatabaseDto enviament : notificacio.getEnviaments()) {
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
					errors.add(messageHelper.getMessage("error.validacio.servei.tipus.no.null"));
				}

				// Titular
				if (enviament.getTitular() == null) {
					errors.add(messageHelper.getMessage("error.validacio.titular.enviament.no.null"));
				} else {
					// - Nom
					if (enviament.getTitular().getNom() != null && enviament.getTitular().getNom().length() > 255) {
						errors.add(messageHelper.getMessage("error.validacio.nom.titular.longitud.max"));
					}
					// - Llinatge 1
					if (enviament.getTitular().getLlinatge1() != null && enviament.getTitular().getLlinatge1().length() > 30) {
						errors.add(messageHelper.getMessage("error.validacio.llinatge1.titular.longitud.max"));
					}
					// - Llinatge 2
					if (enviament.getTitular().getLlinatge2() != null && enviament.getTitular().getLlinatge2().length() > 30) {
						errors.add(messageHelper.getMessage("error.validacio.llinatge2.titular.longitud.max"));
					}
					// - Nif
					if (enviament.getTitular().getNif() != null && enviament.getTitular().getNif().length() > 9) {
						errors.add(messageHelper.getMessage("error.validacio.nif.titular.longitud.max"));
					}
					// - Tipus
					if (enviament.getTitular().getInteressatTipus() == null) {
						errors.add(messageHelper.getMessage("error.validacio.interessat.tipus.titular.enviament.no.null"));
					}
					if (!InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())
							&& enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty()) {

						String nif = enviament.getTitular().getNif();
						if (NifHelper.isvalid(nif)) {
							senseNif = false;
							switch (enviament.getTitular().getInteressatTipus()) {
								case FISICA:
									if (!NifHelper.isValidNifNie(nif)) {
										errors.add(messageHelper.getMessage("error.validacio.nif.titular.tipus.document.no.valid.persona.fisica"));
									}
									break;
								case JURIDICA:
									if (!NifHelper.isValidCif(nif)) {
										errors.add(messageHelper.getMessage("error.validacio.nif.titular.tipus.document.invalid.persona.juridica"));
									}
									break;
								case ADMINISTRACIO:
									break;
							}
						} else {
							errors.add(messageHelper.getMessage("error.validacio.nif.titular.invalid"));
						}
					}
					// - Email
					String email = enviament.getTitular().getEmail();
					if (email != null && email.length() > 160) {
						errors.add(messageHelper.getMessage("error.validacio.email.titular.longitud.max"));
					}
					if (email != null && (!EmailHelper.isEmailValid(email) || !isEmailValid(email))) {
						errors.add(messageHelper.getMessage("error.validacio.email.titular.format.invalid"));
					}
					// - Telèfon
					if (enviament.getTitular().getTelefon() != null && enviament.getTitular().getTelefon().length() > 16) {
						errors.add(messageHelper.getMessage("error.validacio.telefon.longitud.max"));
					}
					// - Raó social
					if (enviament.getTitular().getRaoSocial() != null && enviament.getTitular().getRaoSocial().length() > 80) {
						errors.add(messageHelper.getMessage("error.validacio.rao.social.longitud.max"));
					}
					// - Codi Dir3
					if (enviament.getTitular().getDir3Codi() != null && enviament.getTitular().getDir3Codi().length() > 9) {
						errors.add(messageHelper.getMessage("error.validacio.dir3codi.titular.longitud.max"));
					}
					// - Incapacitat
					if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
						errors.add(messageHelper.getMessage("error.validacio.indicar.destinatari.titular.incapacitat"));
					}
					//   - Persona física
					if (enviament.getTitular().getInteressatTipus() != null) {
						if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
							if (enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.nom.persona.fisica.no.null"));
							}
							if (enviament.getTitular().getLlinatge1() == null || enviament.getTitular().getLlinatge1().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.llinatge1.persona.fisica.titular.enviament.no.null"));
							}
							if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.nif.persona.fisica.titular.enviament.no.null"));
							}
							//   - Persona jurídica
						} else if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
							if ((enviament.getTitular().getRaoSocial() == null || enviament.getTitular().getRaoSocial().isEmpty()) &&
									(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty())) {
								errors.add(messageHelper.getMessage("error.validacio.rao.social.persona.juridica.titular.enviament.no.null"));
							}
							if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.nif.persona.juridica.titular.enviament.no.null"));
							}
							//   - Administració
						} else if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
							if (enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
								errors.add(messageHelper.getMessage("error.validacio.nom.administracio.titular.enviament.no.null"));
							}
							if (enviament.getTitular().getDir3Codi() == null) {
								errors.add(messageHelper.getMessage("error.validacio.dir3codi.administracio.titular.enviament.no.null"));
							}
							OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(enviament.getTitular().getDir3Codi());
							if (organDir3 == null) {
								errors.add(messageHelper.getMessage("error.validacio.dir3codi.invalid.a")
										+ enviament.getTitular().getDir3Codi()
										+ messageHelper.getMessage("error.validacio.dir3codi.invalid.b"));
							} else {
								if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
									if (organDir3.getSir() == null || !organDir3.getSir()) {
										errors.add(messageHelper.getMessage("error.validacio.dir3codi.no.oficina.sir.a")
												+ enviament.getTitular().getDir3Codi()
												+ messageHelper.getMessage("error.validacio.dir3codi.no.oficina.sir.b"));
									}
									if (organigramaByEntitat.containsKey(enviament.getTitular().getDir3Codi())) {
										errors.add(messageHelper.getMessage("error.validacio.dir3.codi.referencia.administracio.propia.entitat.a")
												+ enviament.getTitular().getDir3Codi() +
												messageHelper.getMessage("error.validacio.dir3.codi.referencia.administracio.propia.entitat.b"));
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
						&& !InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) && senseNif) {
					errors.add(messageHelper.getMessage("error.validacio.nif.informat.interessats"));
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
			errors.add(messageHelper.getMessage("error.validacio.procediment.codi.longitud.max"));
		}

		if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.NOTIFICACIO ) {
			if (notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) {
				errors.add(messageHelper.getMessage("error.validacio.procediment.codi.no.null"));
			}
		}
//		else if ((notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) && notificacio.getOrganGestorCodi() == null){
//			errors.add(messageHelper.getMessage("error.validacio.organ.gestor.no.null.comunicacio.administracio.sense.procediment"));
//		}
//		if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO && comunicacioSenseAdministracio) {
//			if (notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) {
//				errors.add(messageHelper.getMessage("error.validacio.procediment.codi.no.null"));
//			}
//		}
		if (!organigramaByEntitat.containsKey(notificacio.getOrganGestorCodi())) {
			errors.add(messageHelper.getMessage("error.validacio.organ.gestor.no.organ.entitat"));
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
			errors.add(messageHelper.getMessage("error.validacio.document.no.null"));
		} else {
			if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
				errors.add(messageHelper.getMessage("error.validacio.arxiu.nom.longitud.max"));
			}

			if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
					(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) &&
							documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {
				if ((document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
						(document.getCsv() == null || document.getCsv().isEmpty()) &&
						(document.getUrl() == null || document.getUrl().isEmpty()) &&
						(document.getUuid() == null || document.getUuid().isEmpty())) {
					errors.add(messageHelper.getMessage("error.validacio.document.necessari"));
				}
			}

			if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
					(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) &&
							documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {

				if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
					if (!MimeUtils.isFormatValid(document.getMediaType(), document.getContingutBase64())) {
						errors.add(messageHelper.getMessage("error.validacio.document.format.invalid"));
					}
					if (document.getMida() > getMaxSizeFile()) {
						errors.add(messageHelper.getMessage("error.validacio.document.longitud.max.a")
								+ getMaxSizeFile() / (1024*1024) +
								messageHelper.getMessage("error.validacio.document.longitud.max.b"));
					}
				}

				// Metadades
				if ((document.getContingutBase64() != null && !document.getContingutBase64().isEmpty())
						&& registreNotificaHelper.isSendDocumentsActive()) {
					if (document.getOrigen() == null) {
						errors.add(messageHelper.getMessage("error.validacio.metadades.document.origen.no.informat"));
					}
					if (document.getValidesa() == null) {
						errors.add(messageHelper.getMessage("error.validacio.metadades.document.validesa.no.informat"));
					}
					if (document.getTipoDocumental() == null) {
						errors.add(messageHelper.getMessage("error.validacio.metadades.document.tipus.documental.no.informat"));
					}
					if (document.getArxiuNom().toUpperCase().endsWith("PDF") && document.getModoFirma() == null) {
						errors.add(messageHelper.getMessage("error.validacio.metadades.document.mode.firma.no.informat"));
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

	private Long getMaxSizeFile() {
		return configHelper.getAsLong("es.caib.notib.notificacio.document.size");
	}

}
