/**
 * 
 */
package es.caib.notib.plugin.seu;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import org.jboss.mx.util.MBeanProxyCreationException;

import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.seu.SeuNotificacioEstat.ZonaperJustificantEstat;
import es.caib.notib.plugin.utils.PropertiesHelper;
import es.caib.notib.plugin.utils.WsClientHelper;
import es.caib.regtel.ws.v2.model.aviso.Aviso;
import es.caib.regtel.ws.v2.model.datosexpediente.DatosExpediente;
import es.caib.regtel.ws.v2.model.datosinteresado.DatosInteresado;
import es.caib.regtel.ws.v2.model.datosinteresado.IdentificacionInteresadoDesglosada;
import es.caib.regtel.ws.v2.model.datosnotificacion.DatosNotificacion;
import es.caib.regtel.ws.v2.model.datosregistrosalida.DatosRegistroSalida;
import es.caib.regtel.ws.v2.model.datosrepresentado.DatosRepresentado;
import es.caib.regtel.ws.v2.model.detalleacuserecibo.DetalleAcuseRecibo;
import es.caib.regtel.ws.v2.model.documento.Documento;
import es.caib.regtel.ws.v2.model.documento.Documentos;
import es.caib.regtel.ws.v2.model.oficinaregistral.OficinaRegistral;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision;
import es.caib.regtel.ws.v2.model.resultadoregistro.ResultadoRegistro;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentoExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventoExpediente;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;

/**
 * Implementació de del plugin de comunicació amb la seu electrònica
 * emprant SISTRA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeuPluginSistra implements SeuPlugin {

	private static final String NTI_EXPID_PREFIX = "ES_"; 



	@Override
	public boolean comprovarExpedientCreat(
			String expedientIdentificador,
			String unitatAdministrativa,
			String identificadorProcedimiento,
			String idioma,
			String descripcio,
			SeuPersona representant,
			SeuPersona representat,
			String bantelNumeroEntrada,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil) throws SistemaExternException {
		comprovarZonaPersonalCreada(representant);
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			identificadorSistra = getExpedientIdentificadorPerSistra(expedientIdentificador);
			Expediente expediente = new Expediente();
			expediente.setIdentificadorExpediente(identificadorSistra);
			expediente.setUnidadAdministrativa(new Long(unitatAdministrativa).longValue());
			expediente.setClaveExpediente(clauSistra);
			expediente.setIdioma(idioma);
			expediente.setDescripcion(descripcio);
			expediente.setAutenticado(true);
			expediente.setIdentificadorProcedimiento(
					newJAXBElement(
							"identificadorProcedimiento",
							identificadorProcedimiento,
							String.class));
			expediente.setNifRepresentante(
					newJAXBElement(
							"nifRepresentante",
							representant.getNif(),
							String.class));

			if (representat != null) {
				expediente.setNifRepresentado(
						newJAXBElement(
								"nifRepresentado",
								representat.getNif(),
								String.class));
				expediente.setNombreRepresentado(
						newJAXBElement(
								"nombreRepresentado",
								representat.getNom(),
								String.class));
			}
			expediente.setNumeroEntradaBTE(
					newJAXBElement(
							"numeroEntradaBTE",
							bantelNumeroEntrada,
							String.class));
			ConfiguracionAvisosExpediente configuracionAvisos = new ConfiguracionAvisosExpediente();
			configuracionAvisos.setHabilitarAvisos(
					newJAXBElement(
							"habilitarAvisos",
							new Boolean(avisosHabilitats),
							Boolean.class));
			configuracionAvisos.setAvisoEmail(
					newJAXBElement(
							"avisoEmail",
							avisosEmail,
							String.class));
			configuracionAvisos.setAvisoSMS(
					newJAXBElement(
							"avisoSMS",
							avisosMobil,
							String.class));
			expediente.setConfiguracionAvisos(
					newJAXBElement(
							"configuracionAvisos",
							configuracionAvisos,
							ConfiguracionAvisosExpediente.class));
			getZonaperWs().altaExpediente(expediente);
			return true;
		} catch (SOAPFaultException sfex) {
			if (sfex.getMessage() != null && sfex.getMessage().contains("Ya existe un expediente")) {
				return false;
			} else {
				throw new SistemaExternException(
						"No s'ha pogut crear l'expedient a la zona personal (" +
						"identificadorSistra=" + identificadorSistra + ", " +
						"clauSistra=" + clauSistra + ", " +
						"unitatAdministrativa=" + unitatAdministrativa + ", " +
						"descripcio=" + descripcio + ", " +
						"destinatariNif=" + representant.getNif() + ")",
						sfex);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear l'expedient a la zona personal (" +
					"identificadorSistra=" + identificadorSistra + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"descripcio=" + descripcio + ", " +
					"destinatariNif=" + representant.getNif() + ")",
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
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			identificadorSistra = getExpedientIdentificadorPerSistra(expedientIdentificador);
			EventoExpediente evento = new EventoExpediente();
			evento.setTitulo(titol);
			evento.setTexto(text);
			evento.setTextoSMS(
					newJAXBElement(
							"textoSMS",
							textSms,
							String.class));
			evento.setFecha(
					newJAXBElement(
							"fecha",
							new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
							String.class));
			if (annexos != null && !annexos.isEmpty()) {
				DocumentosExpediente documentos = new DocumentosExpediente();
				for (SeuDocument annex: annexos) {
					DocumentoExpediente documento = new DocumentoExpediente();
					documento.setTitulo(
							newJAXBElement(
									"titulo",
									annex.getTitol(),
									String.class));
					documento.setNombre(
							newJAXBElement(
									"nombre",
									annex.getArxiuNom(),
									String.class));
					documento.setContenidoFichero(
							newJAXBElement(
									"contenidoFichero",
									annex.getArxiuContingut(),
									byte[].class));
					documento.setEstructurado(
							newJAXBElement(
									"estructurado",
									new Boolean(false),
									Boolean.class));
					documentos.getDocumento().add(documento);
				}
				evento.setDocumentos(
						newJAXBElement(
								"documentos",
								documentos,
								DocumentosExpediente.class));
			}
			getZonaperWs().altaEventoExpediente(
					new Long(unitatAdministrativa).longValue(),
					identificadorSistra,
					clauSistra,
					evento);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear l'event a la zona personal (" +
					"identificadorSistra=" + identificadorSistra + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"titol=" + titol + ")",
					ex);
		}
	}

	@Override
	public SeuNotificacioResultat notificacioCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String registreOficinaCodi,
			String registreOficinaOrganCodi,
			SeuPersona destinatari,
			SeuPersona representat,
			String idioma,
			String oficiTitol,
			String oficiText,
			String avisTitol,
			String avisText,
			String avisTextSms,
			boolean confirmarRecepcio,
			List<SeuDocument> annexos) throws SistemaExternException {
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			identificadorSistra = getExpedientIdentificadorPerSistra(expedientIdentificador);
			DatosRegistroSalida notificacion = new DatosRegistroSalida();
			DatosExpediente datosExpediente = new DatosExpediente();
			datosExpediente.setIdentificadorExpediente(identificadorSistra);
			datosExpediente.setUnidadAdministrativa(new Long(unitatAdministrativa).longValue());
			datosExpediente.setClaveExpediente(clauSistra);
			notificacion.setDatosExpediente(datosExpediente);
			OficinaRegistral oficinaRegistral = new OficinaRegistral();
			oficinaRegistral.setCodigoOficina(registreOficinaCodi);
			oficinaRegistral.setCodigoOrgano(registreOficinaOrganCodi);
			notificacion.setOficinaRegistral(oficinaRegistral);
			DatosInteresado datosInteresado = new DatosInteresado();
			datosInteresado.setNif(destinatari.getNif());
			IdentificacionInteresadoDesglosada idInteresado = new IdentificacionInteresadoDesglosada();
			idInteresado.setNombre(destinatari.getNom());
			idInteresado.setApellido1(destinatari.getLlinatge1());
			idInteresado.setApellido2(destinatari.getLlinatge2());
			datosInteresado.setNombreApellidosDesglosado(idInteresado);
			datosInteresado.setCodigoPais(
					newJAXBElement(
							"codigoPais",
							destinatari.getPaisCodi(),
							String.class));
			datosInteresado.setNombrePais(
					newJAXBElement(
							"nombrePais",
							destinatari.getPaisNom(),
							String.class));
			datosInteresado.setCodigoProvincia(
					newJAXBElement(
							"codigoProvincia",
							destinatari.getProvinciaCodi(),
							String.class));
			datosInteresado.setNombreProvincia(
					newJAXBElement(
							"nombreProvincia",
							destinatari.getProvinciaNom(),
							String.class));
			datosInteresado.setCodigoLocalidad(
					newJAXBElement(
							"codigoLocalidad",
							destinatari.getMunicipiCodi(),
							String.class));
			datosInteresado.setNombreLocalidad(
					newJAXBElement(
							"nombreLocalidad",
							destinatari.getMunicipiNom(),
							String.class));
			notificacion.setDatosInteresado(datosInteresado);
			DatosNotificacion datosNotificacion = new DatosNotificacion();
			String assumpteTipus = getNotificacioAssumpteTipus();
			datosNotificacion.setTipoAsunto(
					(assumpteTipus != null) ? assumpteTipus : "OT");
			datosNotificacion.setIdioma(idioma);
			OficioRemision oficioRemision = new OficioRemision();
			oficioRemision.setTitulo(oficiTitol);
			oficioRemision.setTexto(oficiText);
			datosNotificacion.setOficioRemision(oficioRemision);
			if (avisTitol != null) {
				Aviso aviso = new Aviso();
				aviso.setTitulo(avisTitol);
				aviso.setTexto(avisText);
				aviso.setTextoSMS(
						newJAXBElement(
								"textoSMS",
								avisTextSms,
								String.class));
				datosNotificacion.setAviso(aviso);
			}
			datosNotificacion.setAcuseRecibo(confirmarRecepcio);
			notificacion.setDatosNotificacion(datosNotificacion);
			if (representat != null) {
				DatosRepresentado datosRepresentado = new DatosRepresentado();
				datosRepresentado.setNif(representat.getNif());
				datosRepresentado.setNombreApellidos(representat.getLlinatgesComaNom());
				notificacion.setDatosRepresentado(datosRepresentado);
			}
			if (annexos != null && !annexos.isEmpty()) {
				Documentos documentos = new Documentos();
				for (SeuDocument annex: annexos) {
					Documento documento = new Documento();
					documento.setDatosFichero(
							newJAXBElement(
									"datosFichero",
									annex.getArxiuContingut(),
									byte[].class));
					documento.setNombre(
							newJAXBElement(
									"nombre",
									annex.getArxiuNomSenseExtensio(),
									String.class));
					documento.setExtension(
							newJAXBElement(
									"extension",
									annex.getArxiuExtensio(),
									String.class));
					documento.setVersion(
							newJAXBElement(
									"version",
									1,
									Integer.class));
					documento.setModelo(
							newJAXBElement(
									"modelo",
									"GE0005ANEXGEN",
									String.class));
					documentos.getDocumentos().add(documento);
				}
				notificacion.setDocumentos(
						newJAXBElement(
								"documentos",
								documentos,
								Documentos.class));
			}
			ResultadoRegistro resultado = getRegtelWs().registroSalida(notificacion);
			SeuNotificacioResultat resultat = new SeuNotificacioResultat();
			resultat.setRegistreData(
					resultado.getFechaRegistro().toGregorianCalendar().getTime());
			resultat.setRegistreNumero(
					resultado.getNumeroRegistro());
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

	public SeuNotificacioEstat notificacioObtenirJustificantRecepcio(
			String registreNumero) throws SistemaExternException {
		try {
			DetalleAcuseRecibo acuseRecibo = getRegtelWs().obtenerDetalleAcuseRecibo(registreNumero);
			SeuNotificacioEstat notificacioEstat = new SeuNotificacioEstat();
			if (acuseRecibo.getFechaAcuseRecibo() != null) {
				XMLGregorianCalendar cal = acuseRecibo.getFechaAcuseRecibo().getValue();
				notificacioEstat.setData(cal.toGregorianCalendar().getTime());
			}
			if (acuseRecibo.getEstado() != null) {
				switch (acuseRecibo.getEstado()) {
				case ENTREGADA:
					notificacioEstat.setEstat(ZonaperJustificantEstat.LLEGIDA);
					break;
				case PENDIENTE:
					notificacioEstat.setEstat(ZonaperJustificantEstat.PENDENT);
					break;
				case RECHAZADA:
					notificacioEstat.setEstat(ZonaperJustificantEstat.REBUTJADA);
					break;
				default:
					notificacioEstat.setEstat(ZonaperJustificantEstat.PENDENT);
					break;
				}
			}
			return notificacioEstat;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut obtenir el justificant de recepció (" +
							"registreNumero=" + registreNumero + ")",
					ex);
		}
	}



	private void comprovarZonaPersonalCreada(
			SeuPersona persona) throws SistemaExternException {
		boolean existeix;
		try {
			existeix = getZonaperWs().existeZonaPersonalUsuario(
					persona.getNif());
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut verificar l'existència de la zona personal (" +
					"nif=" + persona.getNif() + ")",
					ex);
		}
		if (!existeix) {
			try {
				getZonaperWs().altaZonaPersonalUsuario(
						persona.getNif(),
						persona.getNom(),
						persona.getLlinatge1(),
						persona.getLlinatge2());
			} catch (Exception ex) {
				throw new SistemaExternException(
						"No s'ha pogut donar d'alta la zona personal (" +
						"nif=" + persona.getNif() + ", " +
						"nom=" + persona.getNom() + ", " +
						"llinatge1=" + persona.getLlinatge1() + ", " +
						"llinatge2=" + persona.getLlinatge2() + ")",
						ex);
			}
		}
	}

	private es.caib.zonaper.ws.v2.services.BackofficeFacade getZonaperWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		return new WsClientHelper<es.caib.zonaper.ws.v2.services.BackofficeFacade>().generarClientWs(
				getClass().getResource("/es/caib/notib/plugin/wsdl/sistra/zonaper/BackofficeFacade.wsdl"),
				getBaseUrl() + "/zonaperws/services/v2/BackofficeFacade",
				new QName(
						"urn:es:caib:zonaper:ws:v2:services",
						"BackofficeFacadeService"),
				getUsername(),
				getPassword(),
				null,
				es.caib.zonaper.ws.v2.services.BackofficeFacade.class);
	}

	private es.caib.regtel.ws.v2.services.BackofficeFacade getRegtelWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		return new WsClientHelper<es.caib.regtel.ws.v2.services.BackofficeFacade>().generarClientWs(
				getClass().getResource("/es/caib/notib/plugin/wsdl/sistra/regtel/BackofficeFacade.wsdl"),
				getBaseUrl() + "/regtelws/services/v2/BackofficeFacade",
				new QName(
						"urn:es:caib:regtel:ws:v2:services",
						"BackofficeFacadeService"),
				getUsername(),
				getPassword(),
				null,
				es.caib.regtel.ws.v2.services.BackofficeFacade.class);
	}

	private <T> JAXBElement<T> newJAXBElement(
			String qname,
			T valor,
			Class<T> tipus) {
		return new JAXBElement<T>(
				new QName(qname),
				tipus,
				valor);
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

	private String getExpedientIdentificadorPerSistra(
			String expedientIdentificador) {
		if (expedientIdentificador.length() > 50 && expedientIdentificador.startsWith(NTI_EXPID_PREFIX)) {
			return expedientIdentificador.substring(NTI_EXPID_PREFIX.length() + 19);
			//return expedientIdentificador.substring(NTI_EXPID_PREFIX.length());
		} else {
			return expedientIdentificador;
		}
	}

	private String getBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.seu.sistra.base.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.seu.sistra.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.seu.sistra.password");
	}
	private String getNotificacioAssumpteTipus() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.seu.notificacio.assumpte.tipus");
	}

}
