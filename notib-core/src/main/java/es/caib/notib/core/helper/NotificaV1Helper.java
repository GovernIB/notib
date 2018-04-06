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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaCertificacioDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto.NotificaRespostaDatatEventDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.wsdl.notificaV1.ArrayOfTipoDestinatario;
import es.caib.notib.core.wsdl.notificaV1.CertificacionEnvioRespuesta;
import es.caib.notib.core.wsdl.notificaV1.DatadoEnvio;
import es.caib.notib.core.wsdl.notificaV1.DireccionElectronicaHabilitada;
import es.caib.notib.core.wsdl.notificaV1.Documento;
import es.caib.notib.core.wsdl.notificaV1.IdentificadorEnvio;
import es.caib.notib.core.wsdl.notificaV1.NotificaWsPortType;
import es.caib.notib.core.wsdl.notificaV1.OpcionesEmision;
import es.caib.notib.core.wsdl.notificaV1.ResultadoAlta;
import es.caib.notib.core.wsdl.notificaV1.ResultadoCertificacion;
import es.caib.notib.core.wsdl.notificaV1.ResultadoDatado;
import es.caib.notib.core.wsdl.notificaV1.TipoDestinatario;
import es.caib.notib.core.wsdl.notificaV1.TipoDomicilio;
import es.caib.notib.core.wsdl.notificaV1.TipoEnvio;
import es.caib.notib.core.wsdl.notificaV1.TipoIntento;
import es.caib.notib.core.wsdl.notificaV1.TipoMunicipio;
import es.caib.notib.core.wsdl.notificaV1.TipoOrganismoEmisor;
import es.caib.notib.core.wsdl.notificaV1.TipoOrganismoPagadorCIE;
import es.caib.notib.core.wsdl.notificaV1.TipoOrganismoPagadorCorreos;
import es.caib.notib.core.wsdl.notificaV1.TipoPais;
import es.caib.notib.core.wsdl.notificaV1.TipoPersonaDestinatario;
import es.caib.notib.core.wsdl.notificaV1.TipoProcedimiento;
import es.caib.notib.core.wsdl.notificaV1.TipoProvincia;

/**
 * Helper per a interactuar amb la versió 1 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaV1Helper extends AbstractNotificaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	@Autowired
	private PluginHelper pluginHelper;



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
			logger.error(
					"Error al donar d'alta la notificació a Notific@ (notificacioId=" + notificacioId + ")",
					ex);
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
				respostaDatat.getEstatActualDescripcio(),
				respostaDatat.getNumSeguiment(),
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
			if (notificacio.getCaducitat() != null) {
				opcionesEmision.setCaducidad(
						sdfCaducitat.format(notificacio.getCaducitat()));
			}
			if (notificacio.getRetardPostal() != null) {
				opcionesEmision.setRetardoPostalDeh(
						new Integer(notificacio.getRetardPostal()));
			}
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
		ResultadoDatado resultadoDatado = null;
		try {
			resultadoDatado = getNotificaWs().consultaDatadoEnvio(
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
			if(resultadoDatado != null && ex instanceof SistemaExternException)
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoDatado.getCodigoRespuesta() + "] " + resultadoDatado.getDescripcionRespuesta());
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
//		String nifPerComprovar = (destinatari.getDestinatariNif() != null) ? destinatari.getDestinatariNif() : destinatari.getTitularNif();
//		if (!titularNif.equalsIgnoreCase(nifPerComprovar)) {
//			throw new SistemaExternException(
//					"NOTIFICA",
//					"La identificació retornada no coincideix amb la identificació de la petició (" + 
//					"titularNifPeticio=" + destinatari.getTitularNif() + ", " +
//					"titularNifRetornat=" + titularNif + ")");
//		}
		if (!referenciaEmisor.equals(destinatari.getNotificaReferencia())) {
			throw new SistemaExternException(
					"NOTIFICA",
					"La identificació retornada no coincideix amb la identificació de la petició (" + 
					"referenciaEmisorPeticio=" + destinatari.getNotificaReferencia() + ", " +
					"referenciaEmisorRetornat=" + referenciaEmisor + ")");
		}
	}

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
				new ApiKeySOAPHandlerV1(getApiKeyProperty()),
				/*new FirmaSOAPHandler(
						getKeystorePathProperty(),
						getKeystoreTypeProperty(),
						getKeystorePasswordProperty(),
						getKeystoreCertAliasProperty(),
						getKeystoreCertPasswordProperty()),*/
//				new ChunkedSOAPHandler("false"),
				new WsClientHelper.SOAPLoggingHandler(NotificaWsPortType.class));
		return port;
	}

	public class ApiKeySOAPHandlerV1 implements SOAPHandler<SOAPMessageContext> {
		private final String apiKey;
		public ApiKeySOAPHandlerV1(String apiKey) {
			this.apiKey = apiKey;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					SOAPElement apiKeyElement = factory.createElement(
							new QName(
									"https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", 
									"api_key"));
					apiKeyElement.addTextNode(apiKey);
					SOAPHeader header = envelope.getHeader();
					if (header == null)
						header = envelope.addHeader();
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
	
	private static final Logger logger = LoggerFactory.getLogger(NotificaV1Helper.class);

}
