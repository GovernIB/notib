/**
 * 
 */
package es.caib.notib.core.helper;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaCertificacioDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto.NotificaRespostaDatatEventDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.ws.notificacio.NotificacioDestinatariEstatEnum;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.wsdl.notifica.ArrayOfTipoDestinatario;
import es.caib.notib.core.wsdl.notifica.CertificacionEnvioRespuesta;
import es.caib.notib.core.wsdl.notifica.DatadoEnvio;
import es.caib.notib.core.wsdl.notifica.DireccionElectronicaHabilitada;
import es.caib.notib.core.wsdl.notifica.Documento;
import es.caib.notib.core.wsdl.notifica.IdentificadorEnvio;
import es.caib.notib.core.wsdl.notifica.NotificaWsPortType;
import es.caib.notib.core.wsdl.notifica.OpcionesEmision;
import es.caib.notib.core.wsdl.notifica.ResultadoAlta;
import es.caib.notib.core.wsdl.notifica.ResultadoCertificacion;
import es.caib.notib.core.wsdl.notifica.ResultadoDatado;
import es.caib.notib.core.wsdl.notifica.TipoDestinatario;
import es.caib.notib.core.wsdl.notifica.TipoDomicilio;
import es.caib.notib.core.wsdl.notifica.TipoEnvio;
import es.caib.notib.core.wsdl.notifica.TipoIntento;
import es.caib.notib.core.wsdl.notifica.TipoMunicipio;
import es.caib.notib.core.wsdl.notifica.TipoOrganismoEmisor;
import es.caib.notib.core.wsdl.notifica.TipoOrganismoPagadorCIE;
import es.caib.notib.core.wsdl.notifica.TipoOrganismoPagadorCorreos;
import es.caib.notib.core.wsdl.notifica.TipoPais;
import es.caib.notib.core.wsdl.notifica.TipoPersonaDestinatario;
import es.caib.notib.core.wsdl.notifica.TipoProcedimiento;
import es.caib.notib.core.wsdl.notifica.TipoProvincia;
import es.caib.notib.core.wsdl.sede.CertificacionSede;
import es.caib.notib.core.wsdl.sede.ComunicacionSede;
import es.caib.notib.core.wsdl.sede.ResultadoCertificacionSede;
import es.caib.notib.core.wsdl.sede.ResultadoComunicacionSede;
import es.caib.notib.core.wsdl.sede.SedeWsPortType;

/**
 * Helper per a interactuar amb el servei web de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioDestinatariRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	@Autowired
	private PluginHelper pluginHelper;

	private boolean modeTest;



	@Transactional
	public boolean enviament(
			Long notificacioId) {
		NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
		if (!NotificacioEstatEnumDto.PENDENT.equals(notificacio.getEstat())) {
			throw new ValidationException(
					notificacioId,
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.PENDENT);
		}
		try {
			TipoEnvio tipoEnvio = generarTipoEnvio(notificacio);
			ResultadoAlta resultadoAlta = getNotificaWs().altaEnvio(tipoEnvio);
			if ("000".equals(resultadoAlta.getCodigoRespuesta())) {
				if (resultadoAlta.getIdentificadores() != null && resultadoAlta.getIdentificadores().getItem() != null) {
					for (IdentificadorEnvio identificadorEnvio: resultadoAlta.getIdentificadores().getItem()) {
						for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
							if (enviament.getNotificaReferencia().equals(identificadorEnvio.getReferenciaEmisor())) {
								enviament.updateNotificaIdentificador(
										identificadorEnvio.getIdentificador());
								enviament.updateNotificaEstat(
										new Date(),
										NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA,
										true);
							}
						}
					}
				}
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).build();
				notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
				notificacio.updateEventAfegir(event);
				notificacio.updateErrorNotifica(
						false,
						null);
				notificacioEventRepository.save(event);
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta()).
						build();
				notificacio.updateErrorNotifica(
						true,
						event);
				notificacio.updateEventAfegir(event);
				notificacioEventRepository.save(event);
			}
		} catch (Exception ex) {
			String errorDescripcio;
			if (ex instanceof SOAPFaultException) {
				errorDescripcio = ex.getMessage();
			} else {
				errorDescripcio = ExceptionUtils.getStackTrace(ex);
			}
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
					notificacio).
					error(true).
					errorDescripcio(errorDescripcio).
					build();
			notificacio.updateEventAfegir(event);
			notificacio.updateErrorNotifica(
					true,
					event);
			notificacioEventRepository.save(event);
		}
		return NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat());
	}

	public NotificaRespostaEstatDto refrescarEstat(
			NotificacioEnviamentEntity destinatari) throws SistemaExternException {
		NotificaRespostaDatatDto respostaDatat = enviamentDatat(destinatari);
		destinatari.updateNotificaEstat(
				respostaDatat.getDataActualitzacio(),
				getEstatNotifica(respostaDatat.getEstatActual()),
				true);
		NotificaRespostaEstatDto resposta = new NotificaRespostaEstatDto();
		resposta.setData(respostaDatat.getDataActualitzacio());
		resposta.setEstatCodi(respostaDatat.getEstatActual());
		resposta.setEstatDescripcio(respostaDatat.getEstatActualDescripcio());
		resposta.setNumSeguiment(respostaDatat.getNumSeguiment());
		if (isEstatFinal(respostaDatat.getEstatActual())) {
			//NotificaRespostaCertificacioDto respostaCertificacio = enviamentCertificacio(destinatari);
			enviamentCertificacio(destinatari);
			// TODO
		}
		return resposta;
	}

	@Transactional
	public boolean comunicacioSeu(
			Long notificacioDestinatariId) {
		NotificacioEnviamentEntity enviament = notificacioDestinatariRepository.findOne(
				notificacioDestinatariId);
		NotificacioEntity notificacio = enviament.getNotificacio();
		NotificacioEventEntity event;
		boolean error = false;
		try {
			ComunicacionSede comunicacionSede = new ComunicacionSede();
			comunicacionSede.setIdentificadorDestinatario(enviament.getNotificaIdentificador());
			comunicacionSede.setFecha(
					toXmlGregorianCalendar(enviament.getSeuDataFi()));
			comunicacionSede.setOrganismoRemisor(notificacio.getEntitat().getDir3Codi());
			ResultadoComunicacionSede resultadoComunicacion = getSedeWs().comunicacionSede(comunicacionSede);
			if ("000".equals(resultadoComunicacion.getCodigoRespuesta())) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
						notificacio).
						enviament(enviament).
						build();
				notificacioEventRepository.save(event);
				enviament.updateNotificaError(
						false,
						null);
			} else {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
						notificacio).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoComunicacion.getCodigoRespuesta() + "] " + resultadoComunicacion.getDescripcionRespuesta()).
						build();
				enviament.updateNotificaError(
						true,
						event);
				notificacioEventRepository.save(event);
				error = true;
			}
		} catch (Exception ex) {
			logger.error(
					"Error al comunicar el canvi d'estat d'una notificació de la seu a Notifica (" +
					"notificacioId=" + notificacio.getId() + ", " +
					"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			enviament.updateNotificaError(
					true,
					event);
			notificacioEventRepository.save(event);
			error = true;
		}
		enviament.updateSeuNotificaInformat();
		notificacio.updateEventAfegir(event);
		return !error;
	}

	@Transactional
	public void certificacioSeu(
			NotificacioEnviamentEntity enviament,
			byte[] document) {
		NotificacioEntity notificacio = enviament.getNotificacio();
		NotificacioEventEntity event;
		try {
			CertificacionSede certificacionSede = new CertificacionSede();
			certificacionSede.setEnvioDestinatario(enviament.getNotificaIdentificador());
			if (NotificacioDestinatariEstatEnumDto.LLEGIDA.equals(enviament.getSeuEstat())) {
				certificacionSede.setEstado("notificada");
			} else if (NotificacioDestinatariEstatEnumDto.REBUTJADA.equals(enviament.getSeuEstat())) {
				certificacionSede.setEstado("rehusada");
			}
			certificacionSede.setFecha(
					toXmlGregorianCalendar(enviament.getSeuDataFi()));
			certificacionSede.setDocumento(
					Base64.encodeBase64String(document));
			certificacionSede.setHashDocumento(
					Base64.encodeBase64String(
							Hex.decodeHex(
									DigestUtils.sha1Hex(document).toCharArray())));
			//certificacionSede.setCsv(value);
			certificacionSede.setOrganismoRemisor(notificacio.getEntitat().getDir3Codi());
			ResultadoCertificacionSede resultadoCertificacion = getSedeWs().certificacionSede(certificacionSede);
			if ("000".equals(resultadoCertificacion.getCodigoRespuesta())) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
						notificacio).
						enviament(enviament).
						build();
				notificacioEventRepository.save(event);
			} else {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
						notificacio).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoCertificacion.getCodigoRespuesta() + "] " + resultadoCertificacion.getDescripcionRespuesta()).
						build();
				enviament.updateSeuError(
						true,
						event,
						true);
				notificacioEventRepository.save(event);
			}
		} catch (Exception ex) {
			logger.error(
					"Error al enviar la certificació d'una notificació de la seu a Notifica (" +
					"notificacioId=" + notificacio.getId() + ", " +
					"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			enviament.updateSeuError(
					true,
					event,
					true);
			notificacioEventRepository.save(event);
		}
		enviament.updateSeuNotificaInformat();
		notificacio.updateEventAfegir(event);
	}

	public String generarReferencia(NotificacioEnviamentEntity notificacioDestinatari) throws GeneralSecurityException {
		return xifrarIdPerNotifica(notificacioDestinatari.getId());
	}

	public boolean isConnexioNotificaDisponible() {
		return getUrlProperty() != null && getApiKeyProperty() != null;
	}

	public void setModeTest(boolean modeTest) {
		this.modeTest = modeTest;
	}



	private TipoEnvio generarTipoEnvio(
			NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException {
		TipoEnvio envio = new TipoEnvio();
		TipoOrganismoEmisor organismoEmisor = new TipoOrganismoEmisor();
		organismoEmisor.setCodigoDir3(notificacio.getEntitat().getDir3Codi());
		organismoEmisor.setNombre(notificacio.getEntitat().getNom());
		envio.setOrganismoEmisor(organismoEmisor);
		envio.setConcepto(notificacio.getConcepte());
		if (notificacio.getEnviamentDataProgramada() != null) {
			envio.setFechaEnvioProgramado(
					toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
		}
		if (notificacio.getEnviamentTipus() != null) {
			envio.setTipoEnvio(notificacio.getEnviamentTipus().getText());
		}
		if (notificacio.getPagadorCorreusCodiDir3() != null) {
			TipoOrganismoPagadorCorreos pagadorCorreos = new TipoOrganismoPagadorCorreos();
			pagadorCorreos.setCodigoDir3(notificacio.getPagadorCorreusCodiDir3());
			pagadorCorreos.setCodigoClienteFacturacionCorreos(notificacio.getPagadorCorreusCodiClientFacturacio());
			pagadorCorreos.setNumeroContratoCorreos(notificacio.getPagadorCorreusContracteNum());
			pagadorCorreos.setFechaVigencia(
					toXmlGregorianCalendar(notificacio.getPagadorCieDataVigencia()));
			envio.setOrganismoPagadorCorreos(pagadorCorreos);
		}
		if (notificacio.getPagadorCieCodiDir3() != null) {
			TipoOrganismoPagadorCIE pagadorCie = new TipoOrganismoPagadorCIE();
			pagadorCie.setCodigoDir3(notificacio.getPagadorCieCodiDir3());
			pagadorCie.setFechaVigencia(
					toXmlGregorianCalendar(notificacio.getPagadorCieDataVigencia()));
			envio.setOrganismoPagadorCie(pagadorCie);
		}
		Documento documento = new Documento();
		documento.setHashSha1(notificacio.getDocumentHash());
		documento.setNormalizado(notificacio.isDocumentNormalitzat() ? "si" : "no");
		documento.setGenerarCsv(notificacio.isDocumentGenerarCsv() ? "si" : "no");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				notificacio.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				baos);
		documento.setContenido(new String(Base64.encodeBase64(baos.toByteArray())));
		envio.setDocumento(documento);
		TipoProcedimiento procedimiento = null;
		if (notificacio.getProcedimentCodiSia() != null) {
			if (procedimiento == null)
				procedimiento = new TipoProcedimiento();
			procedimiento.setCodigoSia(notificacio.getProcedimentCodiSia());
		}
		if (notificacio.getProcedimentDescripcioSia() != null) {
			if (procedimiento == null)
				procedimiento = new TipoProcedimiento();
			procedimiento.setDescripcionSia(notificacio.getProcedimentDescripcioSia());
		}
		envio.setProcedimiento(procedimiento);
		envio.setDestinatarios(generarDestinatarios(notificacio));
		return envio;
	}

	private ArrayOfTipoDestinatario generarDestinatarios(
			NotificacioEntity notificacio) throws GeneralSecurityException {
		SimpleDateFormat sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
		ArrayOfTipoDestinatario destinatarios = new ArrayOfTipoDestinatario();
		for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
			TipoDestinatario destinatario = new TipoDestinatario();
			if (enviament.getNotificaReferencia() == null) {
				enviament.updateNotificaReferencia(
						xifrarIdPerNotifica(enviament.getId()));
			}
			destinatario.setReferenciaEmisor(enviament.getNotificaReferencia());
			TipoPersonaDestinatario personaTitular = new TipoPersonaDestinatario();
			personaTitular.setNif(enviament.getTitularNif());
			personaTitular.setNombre(enviament.getTitularNom());
			personaTitular.setApellidos(
					concatenarLlinatges(
							enviament.getTitularLlinatge1(),
							enviament.getTitularLlinatge2()));
			personaTitular.setTelefono(enviament.getTitularTelefon());
			personaTitular.setEmail(enviament.getTitularEmail());
			destinatario.setTitular(personaTitular);
			TipoPersonaDestinatario personaDestinatario = new TipoPersonaDestinatario();
			personaDestinatario.setNif(enviament.getDestinatariNif());
			personaDestinatario.setNombre(enviament.getDestinatariNom());
			personaDestinatario.setApellidos(
					concatenarLlinatges(
							enviament.getDestinatariLlinatge1(),
							enviament.getDestinatariLlinatge2()));
			personaDestinatario.setTelefono(enviament.getDestinatariTelefon());
			personaDestinatario.setEmail(enviament.getDestinatariEmail());
			destinatario.setDestinatario(personaDestinatario);
			String serveiTipusText = null;
			if (enviament.getServeiTipus() != null) {
				switch (enviament.getServeiTipus()) {
				case NORMAL:
					serveiTipusText = "normal";
					break;
				case URGENT:
					serveiTipusText = "urgente";
					break;
				}
			}
			destinatario.setServicio(serveiTipusText);
			if (enviament.getDomiciliTipus() != null) {
				String domiciliTipusText = null;
				switch (enviament.getDomiciliTipus()) {
				case CONCRETO:
					domiciliTipusText = "concreto";
					break;
				case FISCAL:
					domiciliTipusText = "fiscal";
					break;
				}
				destinatario.setTipoDomicilio(domiciliTipusText);
				TipoDomicilio domicilio = new TipoDomicilio();
				String domiciliConcretTipusText = null;
				if (enviament.getDomiciliConcretTipus() != null) {
					switch (enviament.getDomiciliConcretTipus())  {
					case APARTAT_CORREUS:
						domiciliConcretTipusText = "apartado_correos";
						break;
					case ESTRANGER:
						domiciliConcretTipusText = "extranjero";
						break;
					case NACIONAL:
						domiciliConcretTipusText = "nacional";
						break;
					case SENSE_NORMALITZAR:
						domiciliConcretTipusText = "sin_normalizar";
						break;
					}
					domicilio.setTipoDomicilioConcreto(domiciliConcretTipusText);
				}
				domicilio.setTipoVia(
						viaTipusToString(enviament.getDomiciliViaTipus()));
				domicilio.setNombreVia(enviament.getDomiciliViaNom());
				String numeracioTipusText = null;
				if (enviament.getDomiciliNumeracioTipus() != null) {
					switch (enviament.getDomiciliNumeracioTipus()) {
					case APARTAT_CORREUS:
						numeracioTipusText = "apc";
						break;
					case NUMERO:
						numeracioTipusText = "num";
						break;
					case PUNT_KILOMETRIC:
						numeracioTipusText = "pkm";
						break;
					case SENSE_NUMERO:
						numeracioTipusText = "s/n";
						break;
					}
					domicilio.setCalificadorNumero(numeracioTipusText);
				}
				domicilio.setNumeroCasa(enviament.getDomiciliNumeracioNumero());
				domicilio.setPuntoKilometrico(enviament.getDomiciliNumeracioPuntKm());
				domicilio.setApartadoCorreos(enviament.getDomiciliApartatCorreus());
				domicilio.setBloque(enviament.getDomiciliBloc());
				domicilio.setPortal(enviament.getDomiciliPortal());
				domicilio.setEscalera(enviament.getDomiciliEscala());
				domicilio.setPlanta(enviament.getDomiciliPlanta());
				domicilio.setPuerta(enviament.getDomiciliPorta());
				domicilio.setComplemento(enviament.getDomiciliComplement());
				domicilio.setPoblacion(enviament.getDomiciliPoblacio());
				if (enviament.getDomiciliMunicipiCodiIne() != null || enviament.getDomiciliMunicipiNom() != null) {
					TipoMunicipio municipio = new TipoMunicipio();
					municipio.setCodigoIne(enviament.getDomiciliMunicipiCodiIne());
					municipio.setNombre(enviament.getDomiciliMunicipiNom());
					domicilio.setMunicipio(municipio);
				}
				domicilio.setCodigoPostal(enviament.getDomiciliCodiPostal());
				if (enviament.getDomiciliProvinciaCodi() != null || enviament.getDomiciliProvinciaNom() != null) {
					TipoProvincia provincia = new TipoProvincia();
					provincia.setCodigoProvincia(enviament.getDomiciliProvinciaCodi());
					provincia.setNombre(enviament.getDomiciliProvinciaNom());
					domicilio.setProvincia(provincia);
				}
				if (enviament.getDomiciliPaisCodiIso() != null || enviament.getDomiciliPaisNom() != null) {
					TipoPais pais = new TipoPais();
					pais.setCodigoIso3166(enviament.getDomiciliPaisCodiIso());
					pais.setNombre(enviament.getDomiciliPaisNom());
					domicilio.setPais(pais);
				}
				domicilio.setLinea1(enviament.getDomiciliLinea1());
				domicilio.setLinea2(enviament.getDomiciliLinea2());
				domicilio.setCie(enviament.getDomiciliCie());
				destinatario.setDomicilio(domicilio);
			}
			OpcionesEmision opcionesEmision = new OpcionesEmision();
			if (enviament.getCaducitat() != null) {
				opcionesEmision.setCaducidad(
						sdfCaducitat.format(enviament.getCaducitat()));
			}
			opcionesEmision.setRetardoPostalDeh(
					new Integer(enviament.getRetardPostal()));
			destinatario.setOpcionesEmision(opcionesEmision);
			DireccionElectronicaHabilitada deh = new DireccionElectronicaHabilitada();
			deh.setCodigoProcedimiento(enviament.getDehProcedimentCodi());
			deh.setNif(enviament.getDehNif());
			deh.setObligado((enviament.getDehObligat() != null) ? enviament.getDehObligat() : false);
			destinatario.setDireccionElectronica(deh);
			destinatarios.getItem().add(destinatario);
		}
		return destinatarios;
	}

	/*private NotificacioDto generarNotificacioDto(
			TipoEnvio envio) throws ParseException, DatatypeConfigurationException {
		NotificacioDto notificacio = new NotificacioDto();
		notificacio.setEnviamentTipus(
				NotificaEnviamentTipusEnumDto.toEnum(envio.getTipoEnvio()));
		notificacio.setEnviamentDataProgramada(
				toDate(envio.getFechaEnvioProgramado()));
		notificacio.setConcepte(envio.getConcepto());
		if (envio.getOrganismoPagadorCorreos() != null) {
			notificacio.setPagadorCorreusCodiDir3(envio.getOrganismoPagadorCorreos().getCodigoDir3());
			notificacio.setPagadorCorreusContracteNum(envio.getOrganismoPagadorCorreos().getNumeroContratoCorreos());
			notificacio.setPagadorCorreusCodiClientFacturacio(envio.getOrganismoPagadorCorreos().getCodigoClienteFacturacionCorreos());
		}
		if (envio.getOrganismoPagadorCie() != null) {
			notificacio.setPagadorCieCodiDir3(envio.getOrganismoPagadorCie().getCodigoDir3());
			notificacio.setPagadorCieDataVigencia(
					toDate(envio.getOrganismoPagadorCie().getFechaVigencia()));
		}
		if (envio.getProcedimiento() != null) {
			notificacio.setProcedimentCodiSia(envio.getProcedimiento().getCodigoSia());
			notificacio.setProcedimentDescripcioSia(envio.getProcedimiento().getDescripcionSia());
		}
		if (envio.getDocumento() != null) {
			notificacio.setDocumentContingutBase64(envio.getDocumento().getContenido());
			notificacio.setDocumentSha1(envio.getDocumento().getHashSha1());
			notificacio.setDocumentNormalitzat(
					"si".equalsIgnoreCase(envio.getDocumento().getNormalizado()));
			notificacio.setDocumentGenerarCsv(
					"si".equalsIgnoreCase(envio.getDocumento().getGenerarCsv()));
		}
		if (envio.getDestinatarios() != null && envio.getDestinatarios().getItem() != null) {
			List<NotificacioDestinatariDto> destinataris = new ArrayList<NotificacioDestinatariDto>();
			for (TipoDestinatario destinatario: envio.getDestinatarios().getItem()) {
				destinataris.add(generarDestinatariDto(destinatario));
			}
			notificacio.setDestinataris(destinataris);
		}
		return notificacio;
	}
	private NotificacioDestinatariDto generarDestinatariDto(
			TipoDestinatario destinatario) throws ParseException {
		SimpleDateFormat sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
		NotificacioDestinatariDto destinatari = new NotificacioDestinatariDto();
		destinatari.setTitularNom(destinatario.getTitular().getNombre());
		destinatari.setTitularLlinatge1(destinatario.getTitular().getApellidos());
		destinatari.setTitularNif(destinatario.getTitular().getNif());
		destinatari.setTitularTelefon(destinatario.getTitular().getTelefono());
		destinatari.setTitularEmail(destinatario.getTitular().getEmail());
		destinatari.setDestinatariNom(destinatario.getDestinatario().getNombre());
		String[] llinatgesSeparats = separarLlinatges(
				destinatario.getDestinatario().getApellidos());
		destinatari.setDestinatariLlinatge1(llinatgesSeparats[0]);
		destinatari.setDestinatariLlinatge2(llinatgesSeparats[1]);
		destinatari.setDestinatariNif(destinatario.getDestinatario().getNif());
		destinatari.setDestinatariTelefon(destinatario.getDestinatario().getTelefono());
		destinatari.setDestinatariEmail(destinatario.getDestinatario().getEmail());
		destinatari.setDomiciliTipus(
				NotificaDomiciliTipusEnumDto.toEnum(destinatario.getTipoDomicilio()));
		if (destinatario.getDomicilio() != null) {
			destinatari.setDomiciliConcretTipus(
					NotificaDomiciliConcretTipusEnumDto.toEnum(destinatario.getDomicilio().getTipoDomicilioConcreto()));
			destinatari.setDomiciliViaTipus(destinatario.getDomicilio().getTipoVia());
			destinatari.setDomiciliViaNom(destinatario.getDomicilio().getNombreVia());
			destinatari.setDomiciliNumeracioTipus(
					NotificaDomiciliNumeracioTipusEnumDto.toEnum(destinatario.getDomicilio().getCalificadorNumero()));
			destinatari.setDomiciliNumeracioNumero(destinatario.getDomicilio().getNumeroCasa());
			destinatari.setDomiciliNumeracioPuntKm(destinatario.getDomicilio().getPuntoKilometrico());
			destinatari.setDomiciliApartatCorreus(destinatario.getDomicilio().getApartadoCorreos());
			destinatari.setDomiciliBloc(destinatario.getDomicilio().getBloque());
			destinatari.setDomiciliPortal(destinatario.getDomicilio().getPortal());
			destinatari.setDomiciliEscala(destinatario.getDomicilio().getEscalera());
			destinatari.setDomiciliPlanta(destinatario.getDomicilio().getPlanta());
			destinatari.setDomiciliPorta(destinatario.getDomicilio().getPuerta());
			destinatari.setDomiciliComplement(destinatario.getDomicilio().getComplemento());
			destinatari.setDomiciliPoblacio(destinatario.getDomicilio().getPoblacion());
			if (destinatario.getDomicilio().getMunicipio() != null) {
				destinatari.setDomiciliMunicipiCodiIne(destinatario.getDomicilio().getMunicipio().getCodigoIne());
				destinatari.setDomiciliMunicipiNom(destinatario.getDomicilio().getMunicipio().getNombre());
			}
			destinatari.setDomiciliCodiPostal(destinatario.getDomicilio().getCodigoPostal());
			if (destinatario.getDomicilio().getProvincia() != null) {
				destinatari.setDomiciliProvinciaCodi(destinatario.getDomicilio().getProvincia().getCodigoProvincia());
				destinatari.setDomiciliProvinciaNom(destinatario.getDomicilio().getProvincia().getNombre());
			}
			if (destinatario.getDomicilio().getPais() != null) {
				destinatari.setDomiciliPaisCodiIso(destinatario.getDomicilio().getPais().getCodigoIso3166());
				destinatari.setDomiciliPaisNom(destinatario.getDomicilio().getPais().getNombre());
			}
			destinatari.setDomiciliLinea1(destinatario.getDomicilio().getLinea1());
			destinatari.setDomiciliLinea2(destinatario.getDomicilio().getLinea2());
			destinatari.setDomiciliCie(destinatario.getDomicilio().getCie());
		}
		if (destinatario.getDireccionElectronica() != null) {
			destinatari.setDehObligat(destinatario.getDireccionElectronica().isObligado());
			destinatari.setDehNif(destinatario.getDireccionElectronica().getNif());
			destinatari.setDehProcedimentCodi(destinatario.getDireccionElectronica().getCodigoProcedimiento());
		}
		destinatari.setServeiTipus(NotificaServeiTipusEnumDto.toEnum(destinatario.getServicio()));
		if (destinatario.getOpcionesEmision() != null) {
			destinatari.setRetardPostal(destinatario.getOpcionesEmision().getRetardoPostalDeh());
			if (destinatario.getOpcionesEmision().getCaducidad() != null) {
				destinatari.setCaducitat(
						sdfCaducitat.parse(destinatario.getOpcionesEmision().getCaducidad()));
			}
		}
		destinatari.setReferencia(destinatario.getReferenciaEmisor());
		return destinatari;
	}*/

	/*private NotificacioDto enviamentInfo(
			NotificacioDestinatariEntity destinatari) throws SistemaExternException {
		NotificacioEntity notificacio = destinatari.getNotificacio();
		String errorPrefix = "Error al consultar els detalls d'un enviament fet amb Notifica (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ")";
		try {
			InfoEnvio infoEnvio = new InfoEnvio();
			infoEnvio.setEnvioDestinatario(destinatari.getNotificaIdentificador());
			ResultadoInfoEnvio resultadoEnvio = getNotificaWs().infoEnvio(infoEnvio);
			if ("000".equals(resultadoEnvio.getCodigoRespuesta())) {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
						notificacio).
						notificacioDestinatari(destinatari).
						build();
				notificacio.updateEventAfegir(event);
				return generarNotificacioDto(resultadoEnvio.getInfoEnvio());
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
						notificacio).
						notificacioDestinatari(destinatari).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoEnvio.getCodigoRespuesta() + "] " + resultadoEnvio.getDescripcionRespuesta()).
						build();
				notificacio.updateEventAfegir(event);
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoEnvio.getCodigoRespuesta() + "] " + resultadoEnvio.getDescripcionRespuesta());
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
					notificacio).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}*/

	private NotificaRespostaDatatDto enviamentDatat(
			NotificacioEnviamentEntity enviament) {
		NotificacioEntity notificacio = enviament.getNotificacio();
		String errorPrefix = "Error al consultar el datat d'un enviament fet amb Notifica (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
		try {
			ResultadoDatado resultadoDatado = getNotificaWs().consultaDatadoEnvio(
					enviament.getNotificaIdentificador());
			if ("000".equals(resultadoDatado.getCodigoRespuesta())) {
				DatadoEnvio datadoEnvio = resultadoDatado.getDatado();
				comprovarIdentificadorEnviament(
						notificacio,
						enviament,
						datadoEnvio.getIdentificadorEnvio().getIdentificador(),
						datadoEnvio.getIdentificadorEnvio().getNifTitular(),
						datadoEnvio.getIdentificadorEnvio().getReferenciaEmisor());
				/*destinatari.updateNotificaDatat(
						NotificaEstatEnumDto.toEnum(datadoEnvio.getEstadoActual()),
						datadoEnvio.getNccIdExterno(),
						toDate(datadoEnvio.getFechaActualizacion()));*/
				NotificaRespostaDatatDto resposta = new NotificaRespostaDatatDto();
				resposta.setRespostaCodi(resultadoDatado.getCodigoRespuesta());
				resposta.setRespostaDescripcio(resultadoDatado.getDescripcionRespuesta());
				if (datadoEnvio.getIdentificadorEnvio() != null) {
					resposta.setIdentificador(datadoEnvio.getIdentificadorEnvio().getIdentificador());
					resposta.setReferenciaEmisor(datadoEnvio.getIdentificadorEnvio().getReferenciaEmisor());
					resposta.setTitularNif(datadoEnvio.getIdentificadorEnvio().getNifTitular());
				}
				resposta.setEstatActual(datadoEnvio.getEstadoActual());
				resposta.setEstatActualDescripcio(datadoEnvio.getDescripcionEstadoActual());
				resposta.setDataActualitzacio(
						toDate(datadoEnvio.getFechaActualizacion()));
				resposta.setNumSeguiment(datadoEnvio.getNccIdExterno());
				if (datadoEnvio.getDatado() != null && datadoEnvio.getDatado().getItem() != null) {
					List<NotificaRespostaDatatEventDto> events = new ArrayList<NotificaRespostaDatatEventDto>();
					for (TipoIntento intento: datadoEnvio.getDatado().getItem()) {
						NotificaRespostaDatatEventDto event = new NotificaRespostaDatatEventDto();
						event.setEstat(intento.getEstado());
						event.setDescripcio(intento.getDescripcion());
						event.setData(toDate(intento.getFecha()));
						events.add(event);
					}
					resposta.setEvents(events);
				}
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_DATAT,
						notificacio).
						enviament(enviament).
						build();
				notificacio.updateEventAfegir(event);
				return resposta;
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_DATAT,
						notificacio).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoDatado.getCodigoRespuesta() + "] " + resultadoDatado.getDescripcionRespuesta()).
						build();
				notificacio.updateEventAfegir(event);
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoDatado.getCodigoRespuesta() + "] " + resultadoDatado.getDescripcionRespuesta());
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_DATAT,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}

	private NotificaRespostaCertificacioDto enviamentCertificacio(
			NotificacioEnviamentEntity enviament) {
		NotificacioEntity notificacio = enviament.getNotificacio();
		String errorPrefix = "Error al consultar la certificació d'un enviament fet amb Notifica (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
		try {
			ResultadoCertificacion resultadoCertificacion = getNotificaWs().consultaCertificacionEnvio(
					enviament.getNotificaIdentificador());
			if ("000".equals(resultadoCertificacion.getCodigoRespuesta())) {
				CertificacionEnvioRespuesta certificacion = resultadoCertificacion.getCertificacion();
				comprovarIdentificadorEnviament(
						notificacio,
						enviament,
						certificacion.getIdentificadorEnvio().getIdentificador(),
						certificacion.getIdentificadorEnvio().getNifTitular(),
						certificacion.getIdentificadorEnvio().getReferenciaEmisor());
				//String gestioDocumentalId = null;
				NotificaCertificacioArxiuTipusEnumDto arxiuTipus = null;
				byte[] decodificat = null;
				if (certificacion.getPdfCertificado() != null) {
					arxiuTipus = NotificaCertificacioArxiuTipusEnumDto.PDF;
					decodificat = Base64.decodeBase64(certificacion.getPdfCertificado().getBytes());
				} else if (certificacion.getXmlCertificado() != null) {
					arxiuTipus = NotificaCertificacioArxiuTipusEnumDto.XML;
					decodificat = Base64.decodeBase64(certificacion.getXmlCertificado().getBytes());
				}
				/*gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
						new ByteArrayInputStream(decodificat));
				destinatari.updateNotificaCertificacio(
						NotificaCertificacioTipusEnumDto.toEnum(certificacion.getCertificacion()),
						arxiuTipus,
						gestioDocumentalId,
						certificacion.getNccIdExterno(),
						toDate(certificacion.getFechaActualizacion()));*/
				NotificaRespostaCertificacioDto resposta = new NotificaRespostaCertificacioDto();
				resposta.setRespostaCodi(resultadoCertificacion.getCodigoRespuesta());
				resposta.setRespostaDescripcio(resultadoCertificacion.getDescripcionRespuesta());
				if (certificacion.getIdentificadorEnvio() != null) {
					resposta.setIdentificador(certificacion.getIdentificadorEnvio().getIdentificador());
					resposta.setReferenciaEmisor(certificacion.getIdentificadorEnvio().getReferenciaEmisor());
					resposta.setTitularNif(certificacion.getIdentificadorEnvio().getNifTitular());
				}
				resposta.setCertificacioTipus(
						NotificaCertificacioTipusEnumDto.toEnum(certificacion.getCertificacion()));
				resposta.setCertificatTipus(arxiuTipus);
				resposta.setCertificatContingut(decodificat);
				resposta.setDataActualitzacio(
						toDate(certificacion.getFechaActualizacion()));
				resposta.setNumSeguiment(certificacion.getNccIdExterno());
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_CERT,
						notificacio).
						enviament(enviament).
						build();
				notificacio.updateEventAfegir(event);
				return resposta;
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_CERT,
						notificacio).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoCertificacion.getCodigoRespuesta() + "] " + resultadoCertificacion.getDescripcionRespuesta()).
						build();
				notificacio.updateEventAfegir(event);
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoCertificacion.getCodigoRespuesta() + "] " + resultadoCertificacion.getDescripcionRespuesta());
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_CERT,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}

	private static final String[] estatsNotifica = new String[] {
			"ausente",
			"desconocido",
			"direccion_incorrecta",
			"enviado_deh",
			"enviado_ci",
			"entregado_op",
			"leida",
			"error",
			"extraviada",
			"fallecido",
			"notificada",
			"pendiente_envio",
			"pendiente_cie",
			"pendiente_deh",
			"pendiente_sede",
			"rehusada",
			"expirada",
			"envio_programado",
			"sin_informacion"};
	private static final NotificacioDestinatariEstatEnumDto[] estatsNotib = new NotificacioDestinatariEstatEnumDto[] {
			NotificacioDestinatariEstatEnumDto.ABSENT,
			NotificacioDestinatariEstatEnumDto.DESCONEGUT,
			NotificacioDestinatariEstatEnumDto.ADRESA_INCORRECTA,
			NotificacioDestinatariEstatEnumDto.ENVIADA_DEH,
			NotificacioDestinatariEstatEnumDto.ENVIADA_CI,
			NotificacioDestinatariEstatEnumDto.ENTREGADA_OP,
			NotificacioDestinatariEstatEnumDto.LLEGIDA,
			NotificacioDestinatariEstatEnumDto.ERROR_ENTREGA,
			NotificacioDestinatariEstatEnumDto.EXTRAVIADA,
			NotificacioDestinatariEstatEnumDto.MORT,
			NotificacioDestinatariEstatEnumDto.NOTIFICADA,
			NotificacioDestinatariEstatEnumDto.PENDENT_ENVIAMENT,
			NotificacioDestinatariEstatEnumDto.PENDENT_CIE,
			NotificacioDestinatariEstatEnumDto.PENDENT_DEH,
			NotificacioDestinatariEstatEnumDto.PENDENT_SEU,
			NotificacioDestinatariEstatEnumDto.REBUTJADA,
			NotificacioDestinatariEstatEnumDto.EXPIRADA,
			NotificacioDestinatariEstatEnumDto.ENVIAMENT_PROGRAMAT,
			NotificacioDestinatariEstatEnumDto.SENSE_INFORMACIO};
	private NotificacioDestinatariEstatEnumDto getEstatNotifica(
			String estatCodi) {
		for (int i = 0; i < estatsNotifica.length; i++) {
			if (estatCodi.equalsIgnoreCase(estatsNotifica[i])) {
				return estatsNotib[i];
			}
		}
		return null;
	}
	private boolean isEstatFinal(
			String estatCodi) {
		boolean[] esFinal = new boolean[] {
			true,
			true,
			true,
			false,
			false,
			false,
			true,
			true,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			true,
			true,
			false,
			false};
		for (int i = 0; i < estatsNotifica.length; i++) {
			if (estatCodi.equalsIgnoreCase(estatsNotifica[i])) {
				return esFinal[i];
			}
		}
		return false;
	}

	private String viaTipusToString(NotificaDomiciliViaTipusEnumDto viaTipus) {
		if (viaTipus != null) {
			switch (viaTipus) {
			case ALAMEDA:
				return "ALMDA";
			case AVENIDA:
				return "AVDA";
			case AVINGUDA:
				return "AVGDA";
			case BARRIO:
				return "BAR";
			case BULEVAR:
				return "BVR";
			case CALLE:
				return "CALLE";
			case CALLEJA:
				return "CJA";
			case CAMI:
				return "CAMÍ";
			case CAMINO:
				return "CAMNO";
			case CAMPO:
				return "CAMPO";
			case CARRER:
				return "CARR";
			case CARRERA:
				return "CRA";
			case CARRETERA:
				return "CTRA";
			case CUESTA:
				return "CSTA";
			case EDIFICIO:
				return "EDIF";
			case ENPARANTZA:
				return "EPTZA";
			case ESTRADA:
				return "ESTR";
			case GLORIETA:
				return "GTA";
			case JARDINES:
				return "JARD";
			case JARDINS:
				return "JARDI";
			case KALEA:
				return "KALEA";
			case OTROS:
				return "OTROS";
			case PARQUE:
				return "PRQUE";
			case PASAJE:
				return "PSJ";
			case PASEO:
				return "PASEO";
			case PASSATGE:
				return "PASTG";
			case PASSEIG:
				return "PSG";
			case PLACETA:
				return "PLCTA";
			case PLAZA:
				return "PLAZA";
			case PLAZUELA:
				return "PLZA";
			case PLAÇA:
				return "PLAÇA";
			case POBLADO:
				return "POBL";
			case POLIGONO:
				return "POLIG";
			case PRAZA:
				return "PRAZA";
			case RAMBLA:
				return "RAMBL";
			case RONDA:
				return "RONDA";
			case RUA:
				return "RÚA";
			case SECTOR:
				return "SECT";
			case TRAVESIA:
				return "TRAV";
			case TRAVESSERA:
				return "TRAVS";
			case URBANIZACION:
				return "URB";
			case VIA:
				return "VIA";
			default:
				return null;
			}
		} else {
			return null;
		}
	}

	private void comprovarIdentificadorEnviament(
			NotificacioEntity notificacio,
			NotificacioEnviamentEntity destinatari,
			String identificador,
			String titularNif,
			String referenciaEmisor) {
		if (!identificador.equals(destinatari.getNotificaIdentificador())) {
			throw new SistemaExternException(
					"NOTIFICA",
					"La identificació retornada no coincideix amb la identificació de la petició (" + 
					"identificadorPeticio=" + destinatari.getNotificaIdentificador() + ", " +
					"identificadorRetornat=" + identificador + ")");
		}
		String nifPerComprovar = (destinatari.getDestinatariNif() != null) ? destinatari.getDestinatariNif() : destinatari.getTitularNif();
		if (!titularNif.equalsIgnoreCase(nifPerComprovar)) {
			throw new SistemaExternException(
					"NOTIFICA",
					"La identificació retornada no coincideix amb la identificació de la petició (" + 
					"titularNifPeticio=" + destinatari.getTitularNif() + ", " +
					"titularNifRetornat=" + titularNif + ")");
		}
		if (!referenciaEmisor.equals(destinatari.getNotificaReferencia())) {
			throw new SistemaExternException(
					"NOTIFICA",
					"La identificació retornada no coincideix amb la identificació de la petició (" + 
					"referenciaEmisorPeticio=" + destinatari.getNotificaReferencia() + ", " +
					"referenciaEmisorRetornat=" + referenciaEmisor + ")");
		}
	}

	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}
	private Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private String xifrarIdPerNotifica(Long id) throws GeneralSecurityException {
		// Si el mode test està actiu concatena la data actual a l'identificador de
		// base de dades per a generar l'id de Notifica. Si no ho fessim així es
		// duplicarien els ids de Notifica en cada execució del test i les cridades
		// a Notifica donarien error.
		long idlong = (modeTest) ? id.longValue() + System.currentTimeMillis() : id.longValue();
		byte[] bytes = longToBytes(idlong);
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.ENCRYPT_MODE, rc4Key);
		byte[] xifrat = cipher.doFinal(bytes);
		return new String(Base64.encodeBase64(xifrat));
	}
	@SuppressWarnings("unused")
	private Long desxifrarIdPerNotifica(String idXifrat) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.DECRYPT_MODE, rc4Key);
		byte[] desxifrat = cipher.doFinal(Base64.decodeBase64(idXifrat.getBytes()));
		return new Long(bytesToLong(desxifrat));
	}

	private byte[] longToBytes(long l) {
		byte[] result = new byte[Long.SIZE / Byte.SIZE];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}
	private long bytesToLong(byte[] b) {
		long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}

	private String concatenarLlinatges(
			String llinatge1,
			String llinatge2) {
		if (llinatge1 == null && llinatge2 == null) {
			return null;
		}
		StringBuilder llinatges = new StringBuilder();
		llinatges.append(llinatge1.trim());
		if (llinatge2 != null && !llinatge2.trim().isEmpty()) {
			llinatges.append(" ");
			llinatges.append(llinatge2);
		}
		return llinatges.toString();
	}

	/*private String[] separarLlinatges(
			String llinatges) {
		int indexEspai = llinatges.indexOf(" ");
		if (indexEspai != -1) {
			return new String[] {
					llinatges.substring(0, indexEspai),
					llinatges.substring(indexEspai + 1)};
		} else {
			return new String[] {
					llinatges,
					null};
		}
	}*/

	private NotificaWsPortType getNotificaWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
		NotificaWsPortType port = new WsClientHelper<NotificaWsPortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/NotificaWS.wsdl"),
				getUrlProperty(),
				new QName(
						"https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", 
						"NotificaWsService"),
				getUsernameProperty(),
				getPasswordProperty(),
				NotificaWsPortType.class,
				new ApiKeySOAPHandler(getApiKeyProperty()),
				/*new FirmaSOAPHandler(
						getKeystorePathProperty(),
						getKeystoreTypeProperty(),
						getKeystorePasswordProperty(),
						getKeystoreCertAliasProperty(),
						getKeystoreCertPasswordProperty()),*/
				new WsClientHelper.SOAPLoggingHandler());
		return port;
	}

	private SedeWsPortType getSedeWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
		SedeWsPortType port = new WsClientHelper<SedeWsPortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/SedeWs.wsdl"),
				getUrlProperty(),
				new QName(
						"https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/",
						"SedeWsService"),
				getUsernameProperty(),
				getPasswordProperty(),
				SedeWsPortType.class);
		return port;
	}

	private String getUrlProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.url");
	}
	private String getUsernameProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.username");
	}
	private String getPasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.password");
	}
	private String getClauXifratIdsProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.clau.xifrat.ids",
				"P0rt4FI8");
	}
	private String getApiKeyProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.apikey");
	}
	@SuppressWarnings("unused")
	private String getKeystorePathProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.path");
	}
	@SuppressWarnings("unused")
	private String getKeystoreTypeProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.type");
	}
	@SuppressWarnings("unused")
	private String getKeystorePasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.password");
	}
	@SuppressWarnings("unused")
	private String getKeystoreCertAliasProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.cert.alias");
	}
	@SuppressWarnings("unused")
	private String getKeystoreCertPasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.cert.password");
	}

	public class ApiKeySOAPHandler implements SOAPHandler<SOAPMessageContext> {
		private final String apiKey;
		public ApiKeySOAPHandler(String apiKey) {
			this.apiKey = apiKey;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean)context.get(
					MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					SOAPElement apiKeyElement = factory.createElement("api_key");
					apiKeyElement.addTextNode(apiKey);
					SOAPHeader header = envelope.addHeader();
					header.addChildElement(apiKeyElement);
				} catch (SOAPException ex) {
					logger.error(
							"No s'ha pogut afegir l'API key a la petició SOAP per Notifica",
							ex);
	        	}
	        }
	        return true;
	    }
		@Override
		public boolean handleFault(SOAPMessageContext context) {
			return false;
		}
		@Override
		public void close(MessageContext context) {
		}
		@Override
		public Set<QName> getHeaders() {
			return new TreeSet<QName>();
		}
	}

	public class FirmaSOAPHandler implements SOAPHandler<SOAPMessageContext> {
		private String keystoreLocation;
		private String keystoreType;
		private String keystorePassword;
		private String keystoreCertAlias;
		private String keystoreCertPassword;
		public FirmaSOAPHandler(
				String keystoreLocation,
				String keystoreType,
				String keystorePassword,
				String keystoreCertAlias,
				String keystoreCertPassword) {
			this.keystoreLocation = keystoreLocation;
			this.keystoreType = keystoreType;
			this.keystorePassword = keystorePassword;
			this.keystoreCertAlias = keystoreCertAlias;
			this.keystoreCertPassword = keystoreCertPassword;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean)context.get(
					MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				try {
					Document document = toDocument(context.getMessage());
					Properties cryptoProperties = getCryptoProperties();
			        WSSecHeader header = new WSSecHeader();
			        header.setMustUnderstand(false);
			        header.insertSecurityHeader(document);
					WSSecSignature signer = new WSSecSignature();
					signer.setUserInfo(keystoreCertAlias, keystoreCertPassword);
					Crypto crypto = CryptoFactory.getInstance(cryptoProperties);
					Document signedDoc = signer.build(
							document,
							crypto,
							header);
					context.getMessage().getSOAPPart().setContent(
							new DOMSource(signedDoc));
				} catch (Exception ex) {
					throw new RuntimeException(
							"No s'ha pogut firmar el missatge SOAP",
							ex);
				}
				@SuppressWarnings("unchecked")
				Map<String, List<String>> headers = (Map<String, List<String>>)context.get(
						MessageContext.HTTP_REQUEST_HEADERS);
				if (headers != null) {
					for (String header: headers.keySet()) {
						List<String> values = headers.get(header);
						System.out.println(">>> " + header);
						for (String value: values) {
							System.out.println(">>>      " + value);
						}
					}
				}
			}
			return true;
		}
		@Override
		public boolean handleFault(SOAPMessageContext context) {
			return false;
		}
		@Override
		public void close(MessageContext context) {
		}
		@Override
		public Set<QName> getHeaders() {
			return new TreeSet<QName>();
		}
		private Properties getCryptoProperties() {
			Properties cryptoProperties = new Properties();
			cryptoProperties.put(
					"org.apache.ws.security.crypto.provider",
					"org.apache.ws.security.components.crypto.Merlin");
			cryptoProperties.put(
					"org.apache.ws.security.crypto.merlin.file",
					keystoreLocation);
			cryptoProperties.put(
					"org.apache.ws.security.crypto.merlin.keystore.type",
					keystoreType);
			if (keystorePassword != null && !keystorePassword.isEmpty()) {
				cryptoProperties.put(
						"org.apache.ws.security.crypto.merlin.keystore.password",
						keystorePassword);
			}
			cryptoProperties.put(
					"org.apache.ws.security.crypto.merlin.keystore.alias",
					keystoreCertAlias);
			return cryptoProperties;
		}
		private Document toDocument(SOAPMessage soapMsg) throws SOAPException, TransformerException {
			Source src = soapMsg.getSOAPPart().getContent();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMResult result = new DOMResult();
			transformer.transform(src, result);
			return (Document) result.getNode();
		}
	}
	
	
	//@SuppressWarnings("incomplete-switch")
	public static NotificacioDestinatariEstatEnum calcularEstat(
			NotificacioEnviamentEntity enviament) {
		NotificacioDestinatariEstatEnumDto estatCalculatDto = NotificacioEnviamentEntity.calcularEstatCombinatNotificaSeu(
				enviament);
		NotificacioDestinatariEstatEnum estatCalculat = null;
		switch (estatCalculatDto) {
		case ABSENT:
			estatCalculat = NotificacioDestinatariEstatEnum.ABSENT;
			break;
		case ADRESA_INCORRECTA:
			estatCalculat = NotificacioDestinatariEstatEnum.ADRESA_INCORRECTA;
			break;
		case DESCONEGUT:
			estatCalculat = NotificacioDestinatariEstatEnum.DESCONEGUT;
			break;
		case ENTREGADA_OP:
			estatCalculat = NotificacioDestinatariEstatEnum.ENTREGADA_OP;
			break;
		case ENVIADA_CI:
			estatCalculat = NotificacioDestinatariEstatEnum.ENVIADA_CI;
			break;
		case ENVIADA_DEH:
			estatCalculat = NotificacioDestinatariEstatEnum.ENVIADA_DEH;
			break;
		case ENVIAMENT_PROGRAMAT:
			estatCalculat = NotificacioDestinatariEstatEnum.ENVIAMENT_PROGRAMAT;
			break;
		case ERROR_ENTREGA:
			estatCalculat = NotificacioDestinatariEstatEnum.ERROR_ENTREGA;
			break;
		case EXPIRADA:
			estatCalculat = NotificacioDestinatariEstatEnum.EXPIRADA;
			break;
		case EXTRAVIADA:
			estatCalculat = NotificacioDestinatariEstatEnum.EXTRAVIADA;
			break;
		case LLEGIDA:
			estatCalculat = NotificacioDestinatariEstatEnum.LLEGIDA;
			break;
		case MORT:
			estatCalculat = NotificacioDestinatariEstatEnum.MORT;
			break;
		case NOTIFICADA:
			estatCalculat = NotificacioDestinatariEstatEnum.NOTIFICADA;
			break;
		case PENDENT_CIE:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_CIE;
			break;
		case PENDENT_DEH:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_DEH;
			break;
		case PENDENT_ENVIAMENT:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_ENVIAMENT;
			break;
		case PENDENT_SEU:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_SEU;
			break;
		case REBUTJADA:
			estatCalculat = NotificacioDestinatariEstatEnum.REBUTJADA;
			break;
		case SENSE_INFORMACIO:
			estatCalculat = NotificacioDestinatariEstatEnum.SENSE_INFORMACIO;
			break;
		case NOTIB_ENVIADA:
			estatCalculat = NotificacioDestinatariEstatEnum.NOTIB_ENVIADA;
			break;
		case NOTIB_PENDENT:
			estatCalculat = NotificacioDestinatariEstatEnum.NOTIB_PENDENT;
			break;
		}
		return estatCalculat;
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaHelper.class);

}
