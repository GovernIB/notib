package es.caib.notib.plugin.registre;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.InteresadoWsDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.RegistreInteressatDocumentTipusDtoEnum;
import es.caib.notib.core.api.dto.RegistreInteressatDto;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.regweb3.ws.api.v3.AnexoWs;
import es.caib.regweb3.ws.api.v3.AsientoRegistralWs;
import es.caib.regweb3.ws.api.v3.CodigoAsuntoWs;
import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
import es.caib.regweb3.ws.api.v3.IdentificadorWs;
import es.caib.regweb3.ws.api.v3.InteresadoWs;
import es.caib.regweb3.ws.api.v3.JustificanteWs;
import es.caib.regweb3.ws.api.v3.LibroOficinaWs;
import es.caib.regweb3.ws.api.v3.LibroWs;
import es.caib.regweb3.ws.api.v3.OficinaWs;
import es.caib.regweb3.ws.api.v3.OficioWs;
import es.caib.regweb3.ws.api.v3.OrganismoWs;
import es.caib.regweb3.ws.api.v3.RegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.TipoAsuntoWs;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;

/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegweb3Impl extends RegWeb3Utils implements RegistrePlugin{
	
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	
	private static final String OFICINA_VIRTUAL = "Oficina Virtual";

	@Override
	public RespostaAnotacioRegistre registrarSalida(
			RegistreSortida registreSortida,
			String aplicacion) {
		RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
		try {
			resposta = toRespostaAnotacioRegistre(getRegistroSalidaApi().nuevoRegistroSalida(
					registreSortida.getCodiEntitat(),
					toRegistroSalidaWs(
									registreSortida,
									aplicacion)));
					
		} catch (Exception ex) {
			resposta.setErrorDescripcio(ex.getMessage());
			resposta.setData(new Date());
			logger.error("Error a l'hora de registrar la sortida", ex);
		}
		return resposta;
	}
	
	
	
	@Override
	public RespostaConsultaRegistre salidaAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio) {
		RespostaConsultaRegistre rc = new RespostaConsultaRegistre();
		try {
			return toRespostaConsultaRegistre(getAsientoRegistralApi().crearAsientoRegistral(
					codiDir3Entitat, 
					toAsientoRegistralBean(arb), 
					tipusOperacio,
					false));
		} catch (WsI18NException e) {
			rc.setErrorCodi("0");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		} catch (WsValidationException e) {
			rc.setErrorCodi("1");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		} catch (Exception e) {
			e.printStackTrace();
			rc.setErrorCodi("2");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		}
	}

	@Override
	public RespostaJustificantRecepcio obtenerJustificante(
			String codiDir3Entitat, 
			String numeroRegistreFormatat, 
			long tipusRegistre){
		RespostaJustificantRecepcio rj = new RespostaJustificantRecepcio();
		try {
			return toRespostaJustificantRecepcio(getAsientoRegistralApi().obtenerJustificante(codiDir3Entitat, numeroRegistreFormatat, tipusRegistre));
		} catch (WsI18NException e) {
			rj.setErrorCodi("0");
			rj.setErrorDescripcio("No s'ha pogut obtenir el justificant");
			return rj;
		} catch (WsValidationException e) {
			rj.setErrorCodi("1");
			rj.setErrorDescripcio("Error de validació a l'obtenir el justificant");
			return rj;
		} catch (Exception e) {
			rj.setErrorCodi("2");
			rj.setErrorDescripcio("Error obtenint el justificant");
			e.printStackTrace();
			return rj;
		}
	}
	
	@Override
	public RespostaJustificantRecepcio obtenerOficioExterno(
			String codiDir3Entitat, 
			String numeroRegistreFormatat) {
		RespostaJustificantRecepcio rj = new RespostaJustificantRecepcio();
		try {
			return toRespostaJustificantRecepcio(getAsientoRegistralApi().obtenerOficioExterno(codiDir3Entitat, numeroRegistreFormatat));
		} catch (WsI18NException e) {
			rj.setErrorCodi("0");
			rj.setErrorDescripcio("No s'ha pogut obtenir l'ofici extern");
			return rj;
		} catch (WsValidationException e) {
			rj.setErrorCodi("1");
			rj.setErrorDescripcio("Error de validació a l'obtenir l'ofici extern");
			return rj;
		} catch (Exception e) {
			rj.setErrorCodi("2");
			rj.setErrorDescripcio("Error obtenint l'ofici extern");
			return rj;
		}
	}

	public RespostaJustificantRecepcio toRespostaJustificantRecepcio(JustificanteWs ofici){
		RespostaJustificantRecepcio rj = new RespostaJustificantRecepcio();
		rj.setJustificant(ofici.getJustificante());
		return rj;
	}

	public RespostaJustificantRecepcio toRespostaJustificantRecepcio(OficioWs ofici){
		RespostaJustificantRecepcio rj = new RespostaJustificantRecepcio();
		rj.setJustificant(ofici.getOficio());
		return rj;
	}

	private RegistroSalidaWs toRegistroSalidaWs(
			RegistreSortida registreSortida,
			String aplicacion) throws RegistrePluginException {
		RegistroSalidaWs rsw = new RegistroSalidaWs();
		DatosInteresadoWs datosInteresado = new DatosInteresadoWs();
		InteresadoWs interesado = new InteresadoWs();
		AnexoWs anexo = null;
		try {
			rsw.setCodigoUsuario(registreSortida.getDadesAnotacio().getCodiUsuari());
			rsw.setAplicacion(aplicacion);
			rsw.setTipoAsunto(registreSortida.getDadesAnotacio().getTipusAssumpte());
			rsw.setCodigoAsunto(registreSortida.getDadesAnotacio().getCodiAssumpte());
			rsw.setDocFisica(registreSortida.getDadesAnotacio().getDocfisica());
			rsw.setExpone(null);
			rsw.setSolicita(null);
			rsw.setExtracto(registreSortida.getDadesAnotacio().getExtracte());
			rsw.setFecha(new Timestamp((new Date()).getTime()));
			rsw.setIdioma(registreSortida.getDadesAnotacio().getIdiomaCodi().toLowerCase());
			rsw.setLibro(registreSortida.getDadesOficina().getLlibre());
			rsw.setNumExpediente(registreSortida.getDadesAnotacio().getNumExpedient());
			rsw.setObservaciones(registreSortida.getDadesAnotacio().getObservacions());
			rsw.setOficina(registreSortida.getDadesOficina().getOficina());
			rsw.setOrigen(registreSortida.getDadesOficina().getOrgan());
			rsw.setRefExterna(registreSortida.getDadesAnotacio().getRefExterna());
			
			if (registreSortida.getDocuments() != null) {
				for (DocumentRegistre document : registreSortida.getDocuments()) {
					anexo = new AnexoWs();
					anexo.setTitulo(document.getArxiuNom());
					anexo.setFicheroAnexado(document.getArxiuContingut());
					anexo.setModoFirma(document.getModeFirma());
					anexo.setNombreFicheroAnexado(document.getArxiuNom());
					anexo.setTipoDocumento("0" + 2L); //Documento adjunto
					anexo.setValidezDocumento("0" + 1L); //Copia
//					anexo.setOrigenCiudadanoAdmin(1); //Administaración
//					anexo.setTipoDocumental("TD01");
					anexo.setOrigenCiudadanoAdmin(document.getOrigen());
					anexo.setTipoDocumental(getTipusDocumental(document.getTipusDocumental()));
					//Dettached
					if (document.getModeFirma() != null && document.getModeFirma().equals(2)) { 
						anexo.setValidezDocumento("0" + 4L); //Original
						anexo.setNombreFirmaAnexada(document.getArxiuNom());
						anexo.setFirmaAnexada(document.getArxiuContingut()); //doc.getFirmes().get(0).getContingut()
					}
				}
			}
			if (anexo != null) {
				rsw.getAnexos().add(anexo);
			}
			for (DadesInteressat dadesInteressat : registreSortida.getDadesInteressat()) {
				datosInteresado.setApellido1(dadesInteressat.getCognom1());
				datosInteresado.setApellido2(dadesInteressat.getCognom2());
				datosInteresado.setTipoDocumentoIdentificacion(RegistreInteressatDocumentTipusDtoEnum.NIF.name());
				datosInteresado.setDocumento(dadesInteressat.getNif());
				datosInteresado.setNombre(dadesInteressat.getNom());
				datosInteresado.setTipoInteresado(dadesInteressat.getTipusInteressat());
				interesado.setInteresado(datosInteresado);
			}
			rsw.getInteresados().add(interesado);
			
			rsw.setAplicacion("NOTIB");
			rsw.setVersion("1.0.0");
			
			
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió a registroSalidaWs", ex);
			throw new RegistrePluginException("Error conversió a registroSalidaWs", ex);
		}
		return rsw;
	}
	
	private String getTipusDocumental(String tipusDocumental) {
		String ntiTipusDocumental = null;
		
		try {
			logger.info("Conversió a la metadada ntiTipusDocumental...");
			if (tipusDocumental != null) {
				switch (tipusDocumental) {
				case "RESOLUCIO":
					ntiTipusDocumental = "TD01";
					break;
				case "ACORD":
					ntiTipusDocumental = "TD02";
					break;
				case "CONTRACTE":
					ntiTipusDocumental = "TD03";
					break;
				case "CONVENI":
					ntiTipusDocumental = "TD04";
					break;
				case "DECLARACIO":
					ntiTipusDocumental = "TD05";
					break;
				case "COMUNICACIO":
					ntiTipusDocumental = "TD06";
					break;
				case "NOTIFICACIO":
					ntiTipusDocumental = "TD07";
					break;
				case "PUBLICACIO":
					ntiTipusDocumental = "TD08";
					break;
				case "JUSTIFICANT_RECEPCIO":
					ntiTipusDocumental = "TD09";
					break;
				case "ACTA":
					ntiTipusDocumental = "TD10";
					break;
				case "CERTIFICAT":
					ntiTipusDocumental = "TD11";
					break;
				case "DILIGENCIA":
					ntiTipusDocumental = "TD12";
					break;
				case "INFORME":
					ntiTipusDocumental = "TD13";
					break;
				case "SOLICITUD":
					ntiTipusDocumental = "TD14";
					break;
				case "DENUNCIA":
					ntiTipusDocumental = "TD15";
					break;
				case "ALEGACIO":
					ntiTipusDocumental = "TD16";
					break;
				case "RECURS":
					ntiTipusDocumental = "TD17";
					break;
				case "COMUNICACIO_CIUTADA":
					ntiTipusDocumental = "TD18";
					break;
				case "FACTURA":
					ntiTipusDocumental = "TD19";
					break;
				case "ALTRES_INCAUTATS":
					ntiTipusDocumental = "TD20";
					break;
				default:
					ntiTipusDocumental = "TD99";
					break;
				}
			}
		} catch (Exception ex) {
			throw new ArxiuException("Error a l'hora de fer la conversió de la metadada ntiTipusDocumental: ", ex);
		}
		return ntiTipusDocumental;
	}
	
	public AsientoRegistralWs toAsientoRegistralBean(AsientoRegistralBeanDto dto) {
		AsientoRegistralWs ar = new AsientoRegistralWs();
		ar.setAplicacion(dto.getAplicacion());
		ar.setAplicacionTelematica(dto.getAplicacionTelematica());
		ar.setCodigoAsunto(dto.getCodigoAsunto());
		ar.setCodigoAsuntoDenominacion(dto.getCodigoAsuntoDenominacion());
		ar.setCodigoError(dto.getCodigoError());
		ar.setCodigoSia(dto.getCodigoSia());
		ar.setCodigoUsuario(dto.getCodigoUsuario());
		ar.setDescripcionError(dto.getDescripcionError());
		ar.setEntidadCodigo(dto.getEntidadCodigo());
		ar.setEntidadDenominacion(dto.getEntidadDenominacion());
		ar.setEntidadRegistralDestinoCodigo(dto.getEntidadRegistralDestinoCodigo());
		ar.setEntidadRegistralDestinoDenominacion(dto.getEntidadRegistralDestinoDenominacion());
		ar.setEntidadRegistralInicioCodigo(dto.getEntidadRegistralInicioCodigo());
		ar.setEntidadRegistralInicioDenominacion(dto.getEntidadRegistralInicioDenominacion());
		ar.setEntidadRegistralOrigenCodigo(dto.getEntidadRegistralOrigenCodigo());
		ar.setEntidadRegistralOrigenDenominacion(dto.getEntidadRegistralOrigenDenominacion());
		ar.setEstado(dto.getEstado());
		ar.setExpone(dto.getExpone());
//		ar.setFechaRecepcion(dto.getFechaRecepcion());
//		ar.setFechaRegistro(dto.getFechaRegistro());
//		ar.setFechaRegistroDestino(dto.getFechaRegistroDestino());
		ar.setId(dto.getId());
		ar.setIdentificadorIntercambio(dto.getIdentificadorIntercambio());
		ar.setIdioma(dto.getIdioma());
		ar.setLibroCodigo(dto.getLibroCodigo());
		ar.setMotivo(dto.getMotivo());
		ar.setNumeroExpediente(dto.getNumeroExpediente());
		ar.setNumeroRegistro(dto.getNumeroRegistro());
		ar.setNumeroRegistroDestino(dto.getNumeroRegistroDestino());
		ar.setNumeroRegistroFormateado(dto.getNumeroRegistroFormateado());
		ar.setNumeroTransporte(dto.getNumeroTransporte());
		ar.setObservaciones(dto.getObservaciones());
		ar.setPresencial(dto.isPresencial());
		ar.setReferenciaExterna(dto.getReferenciaExterna());
		ar.setResumen(dto.getResumen());
		ar.setSolicita(dto.getSolicita());
		ar.setTipoAsunto(dto.getTipoAsunto());
		ar.setTipoAsuntoDenominacion(dto.getTipoAsuntoDenominacion());
		ar.setTipoDocumentacionFisicaCodigo(dto.getTipoDocumentacionFisicaCodigo());
		ar.setTipoEnvioDocumentacion(dto.getTipoEnvioDocumentacion());
		ar.setTipoRegistro(dto.getTipoRegistro());
		ar.setTipoTransporte(dto.getTipoTransporte());
		ar.setUnidadTramitacionDestinoCodigo(dto.getUnidadTramitacionDestinoCodigo());
		ar.setUnidadTramitacionDestinoDenominacion(dto.getUnidadTramitacionDestinoDenominacion());
		ar.setUnidadTramitacionOrigenCodigo(dto.getUnidadTramitacionOrigenCodigo());
		ar.setUnidadTramitacionOrigenDenominacion(dto.getUnidadTramitacionOrigenDenominacion());
		if(dto.getAnexos().size() > 0) {
			AnexoWs anexe = new AnexoWs();
			anexe.setCsv(dto.getAnexos().get(0).getCsv());
//			anexe.setFechaCaptura(dto.getAnexos().get(0).getFechaCaptura());
			anexe.setFicheroAnexado(dto.getAnexos().get(0).getFicheroAnexado());
			anexe.setFirmaAnexada(dto.getAnexos().get(0).getFirmaAnexada());
			anexe.setModoFirma(dto.getAnexos().get(0).getModoFirma());
			anexe.setNombreFicheroAnexado(dto.getAnexos().get(0).getNombreFicheroAnexado());
			anexe.setNombreFirmaAnexada(dto.getAnexos().get(0).getNombreFirmaAnexada());
			anexe.setObservaciones(dto.getAnexos().get(0).getObservaciones());
			anexe.setOrigenCiudadanoAdmin(dto.getAnexos().get(0).getOrigenCiudadanoAdmin());
			anexe.setTipoDocumental(dto.getAnexos().get(0).getTipoDocumental());
			anexe.setTipoDocumento(dto.getAnexos().get(0).getTipoDocumento());
			anexe.setTipoMIMEFicheroAnexado(dto.getAnexos().get(0).getTipoMIMEFicheroAnexado());
			anexe.setTipoMIMEFirmaAnexada(dto.getAnexos().get(0).getTipoMIMEFirmaAnexada());
			anexe.setTitulo(dto.getAnexos().get(0).getTitulo());
			anexe.setValidezDocumento(dto.getAnexos().get(0).getValidezDocumento());
			ar.getAnexos().add(anexe);
		}
		ar.getInteresados().add(interesadoWsDtoToInteresadoWs(dto.getInteresados().get(0)));
		ar.setVersion(dto.getVersion());
		return ar;
	}
	
	public RespostaAnotacioRegistre toRespostaAnotacioRegistre(IdentificadorWs iw) throws DatatypeConfigurationException {
		RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
		
		resposta.setData(new Timestamp(System.currentTimeMillis()));
		resposta.setNumeroRegistroFormateado(iw.getNumeroRegistroFormateado());
		resposta.setNumero(String.valueOf(iw.getNumero()));
		return resposta;
	}
	
	public RespostaConsultaRegistre toRespostaConsultaRegistre(AsientoRegistralWs ar) {
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		resposta.setRegistreNumeroFormatat(ar.getNumeroRegistroFormateado());
		resposta.setRegistreNumero(Integer.toString(ar.getNumeroRegistro()));
		resposta.setRegistreData(ar.getFechaRegistro());
		switch(ar.getEstado().intValue()) {
		case 1:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.VALID);
			break;
		case 2:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.RESERVA);
			break;
		case 3:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.PENDENT);
			break;
		case 4:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.OFICI_EXTERN);
			break;
		case 5:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.OFICI_INTERN);
			break;
		case 6:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
			break;
		case 7:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.DISTRIBUIT);
			break;
		case 8:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.ANULAT);
			break;
		case 9:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.RECTIFICAT);
			break;
		case 10:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.REBUTJAT);
			break;
		case 11:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.REENVIAT);
			break;
		case 12:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.DISTRIBUINT);
			break;
		case 13:
			resposta.setEstat(NotificacioRegistreEstatEnumDto.OFICI_SIR);
			break;
		}
		resposta.setEntitatCodi(ar.getEntidadCodigo());
		resposta.setEntitatDenominacio(ar.getEntidadDenominacion());
		return resposta;
	}
	
	public AsientoRegistralWs notificacioToAsientoRegistralBean(NotificacioDto notificacio, AnexoWs anexe) {
		AsientoRegistralWs registre = new AsientoRegistralWs();
		registre.setEntidadCodigo(notificacio.getEntitat().getCodi());
		registre.setEntidadDenominacion(notificacio.getEntitat().getNom());
		registre.setEntidadRegistralInicioCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralInicioDenominacion(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralOrigenCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralOrigenDenominacion(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralDestinoCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralDestinoDenominacion(notificacio.getProcediment().getOficina());
		registre.setUnidadTramitacionOrigenCodigo(notificacio.getRegistreOrgan());
		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getRegistreOrgan());
		registre.setUnidadTramitacionDestinoCodigo(notificacio.getProcediment().getOficina());
		registre.setUnidadTramitacionDestinoDenominacion(notificacio.getProcediment().getOficina());
		registre.setTipoRegistro(2L);
		registre.setLibroCodigo(notificacio.getProcediment().getLlibre());
		registre.setResumen(notificacio.getRegistreExtracte());
		/* 1 = Documentació adjunta en suport Paper
		 * 2 = Documentació adjunta digitalitzada i complementàriament en paper
		 * 3 = Documentació adjunta digitalitzada */
		registre.setTipoDocumentacionFisicaCodigo(3L);
		registre.setTipoAsunto(notificacio.getRegistreTipusAssumpte());
		registre.setTipoAsuntoDenominacion(notificacio.getRegistreTipusAssumpte());
		registre.setCodigoAsunto(notificacio.getRegistreTipusAssumpte());
		registre.setCodigoAsuntoDenominacion(notificacio.getRegistreTipusAssumpte());
		registre.setIdioma(1L);
		registre.setReferenciaExterna(notificacio.getRegistreRefExterna());
		registre.setNumeroExpediente(notificacio.getRegistreNumExpedient());
		/*
		 * 
		 * '01' : Servei de missatgers
		 * '02' : Correu postal
		 * '03' : Correu postal certificat
		 * '04' : Burofax
		 * '05' : En ma
		 * '06' : Fax
		 * '07' : Altres
		 * 
		 * */
		if(notificacio.getPagadorPostal() != null) {
			registre.setTipoTransporte("02");
		}else {
			registre.setTipoTransporte("07");
		}
//		registre.setNumeroTransporte();
		registre.setCodigoSia(Long.parseLong(notificacio.getProcediment().getCodi()));
		registre.setCodigoUsuario(notificacio.getUsuariCodi());
		registre.setAplicacionTelematica("SISTRA");
		registre.setAplicacion("RWE");
		registre.setVersion("3.1");
		registre.setObservaciones(notificacio.getRegistreObservacions());
		registre.setExpone("");
		registre.setSolicita("");
		registre.setPresencial(false);
//		registre.setTipoEnvioDocumentacion();
		registre.setEstado(notificacio.getEstat().getLongVal());
		registre.setUnidadTramitacionOrigenCodigo(notificacio.getRegistreOrgan());
		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getRegistreOrgan());
//		registre.setIdentificadorIntercambio();
//		registre.setFechaRecepcion();
//		registre.setCodigoError();
//		registre.setNumeroRegistroDestino();
//		registre.setFechaRegistroDestino();
		registre.setMotivo(notificacio.getDescripcio());
		registre.getInteresados().add(personaToInteresadoWs(notificacio.getEnviaments().iterator().next().getTitular()));
		if(notificacio.getDocument() != null) {
			registre.getAnexos().add(anexe);	
		}
		return registre;
	}
	/*------------------*/
	
//	public RegistreAnotacioDto notificacioToRegistreAnotacioV2(NotificacioEntity notificacio) {
//		RegistreAnotacioDto registre = new RegistreAnotacioDto();
//		registre.setAssumpteCodi(notificacio.getRegistreCodiAssumpte());
//		registre.setAssumpteExtracte(notificacio.getConcepte());
//		registre.setAssumpteIdiomaCodi(notificacio.getRegistreIdioma());
//		registre.setAssumpteTipus(notificacio.getRegistreTipusAssumpte());
//		registre.setEntitatCodi(notificacio.getEntitat().getCodi());
//		registre.setExpedientNumero(notificacio.getRegistreNumExpedient());
//		registre.setObservacions(notificacio.getRegistreObservacions());
//		registre.setLlibre(notificacio.getProcediment().getLlibre());
//		registre.setOficina(notificacio.getProcediment().getOficina());
//		registre.setAnnexos(new ArrayList<RegistreAnnexDto>());
//		registre.getAnnexos().add(documentToRegistreAnnexDto(notificacio.getDocument()));
//		List<RegistreInteressatDto> interessats = new ArrayList<RegistreInteressatDto>();
//		interessats.add(personaToRegistreInteresatDto(notificacio.getEnviaments().iterator().next().getTitular()));
//		registre.setInteressats(interessats);
//		return registre;
//	}
	
	public RegistreInteressatDto personaToRegistreInteresatDto (PersonaDto persona) {
		RegistreInteressatDto interessat = new RegistreInteressatDto();
		interessat.setNom(persona.getNom());
		interessat.setLlinatge1(persona.getLlinatge1());
		interessat.setLlinatge2(persona.getLlinatge2());
		interessat.setRaoSocial(persona.getRaoSocial());
		interessat.setTelefon(persona.getTelefon());
		interessat.setDocumentNumero(persona.getNif());
		interessat.setDocumentTipus(RegistreInteressatDocumentTipusDtoEnum.NIF);
		return interessat;
	}
	
	public InteresadoWs personaToInteresadoWs (PersonaDto persona) {
		InteresadoWs interessat = new InteresadoWs();
		DatosInteresadoWs interessatDades = new DatosInteresadoWs();
		interessatDades.setTipoInteresado(persona.getInteressatTipus().getLongVal());
		interessatDades.setTipoDocumentoIdentificacion("N");
		interessatDades.setDocumento(persona.getNif());
		interessatDades.setRazonSocial(persona.getRaoSocial());
		interessatDades.setNombre(persona.getNom());
		interessatDades.setApellido1(persona.getLlinatge1());
		interessatDades.setApellido2(persona.getLlinatge2());
		interessatDades.setDireccion("");
		interessatDades.setCp("");
		interessatDades.setObservaciones("");
		interessatDades.setEmail(persona.getEmail());
		interessatDades.setDireccionElectronica(persona.getEmail());
		interessatDades.setTelefono(persona.getTelefon());
		interessat.setInteresado(interessatDades);
		return interessat;
	}
	
	public InteresadoWs interesadoWsDtoToInteresadoWs(InteresadoWsDto interesadoWsDto) {
		InteresadoWs interessat = new InteresadoWs();
		
		if(interesadoWsDto.getInteresado() != null) {
			DatosInteresadoWs interessatDades = new DatosInteresadoWs();
			interessatDades.setTipoInteresado(interesadoWsDto.getInteresado().getTipoInteresado().longValue());
			interessatDades.setTipoDocumentoIdentificacion("N");
			interessatDades.setDocumento(interesadoWsDto.getInteresado().getDocumento());
			interessatDades.setRazonSocial(interesadoWsDto.getInteresado().getRazonSocial());
			interessatDades.setNombre(interesadoWsDto.getInteresado().getNombre());
			interessatDades.setApellido1(interesadoWsDto.getInteresado().getApellido1());
			interessatDades.setApellido2(interesadoWsDto.getInteresado().getApellido2());
			interessatDades.setDireccion(interesadoWsDto.getInteresado().getDireccion());
			interessatDades.setCp(interesadoWsDto.getInteresado().getCp());
			interessatDades.setObservaciones(interesadoWsDto.getInteresado().getObservaciones());
			interessatDades.setEmail(interesadoWsDto.getInteresado().getEmail());
			interessatDades.setDireccionElectronica(interesadoWsDto.getInteresado().getDireccionElectronica());
			interessatDades.setTelefono(interesadoWsDto.getInteresado().getTelefono());
			interessatDades.setCanal(interesadoWsDto.getInteresado().getCanal());
//			interessatDades.setDireCodigo(interesadoWsDto.getInteresado().getCodigoDire());
			interessatDades.setLocalidad(interesadoWsDto.getInteresado().getLocalidad());
			interessatDades.setPais(interesadoWsDto.getInteresado().getPais());
			interessatDades.setProvincia(interesadoWsDto.getInteresado().getProvincia());
			interessat.setInteresado(interessatDades);
		}

		if(interesadoWsDto.getRepresentante() != null) {
			DatosInteresadoWs representantDades = new DatosInteresadoWs();
			representantDades.setTipoInteresado(interesadoWsDto.getRepresentante().getTipoInteresado().longValue());
			representantDades.setTipoDocumentoIdentificacion("N");
			representantDades.setDocumento(interesadoWsDto.getRepresentante().getDocumento());
			representantDades.setRazonSocial(interesadoWsDto.getRepresentante().getRazonSocial());
			representantDades.setNombre(interesadoWsDto.getRepresentante().getNombre());
			representantDades.setApellido1(interesadoWsDto.getRepresentante().getApellido1());
			representantDades.setApellido2(interesadoWsDto.getRepresentante().getApellido2());
			representantDades.setDireccion(interesadoWsDto.getRepresentante().getDireccion());
			representantDades.setCp(interesadoWsDto.getRepresentante().getCp());
			representantDades.setObservaciones(interesadoWsDto.getRepresentante().getObservaciones());
			representantDades.setEmail(interesadoWsDto.getRepresentante().getEmail());
			representantDades.setDireccionElectronica(interesadoWsDto.getRepresentante().getDireccionElectronica());
			representantDades.setTelefono(interesadoWsDto.getRepresentante().getTelefono());
			representantDades.setCanal(interesadoWsDto.getRepresentante().getCanal());
//			representantDades.setCodigoDire(interesadoWsDto.getRepresentante().getCodigoDire());
			representantDades.setLocalidad(interesadoWsDto.getRepresentante().getLocalidad());
			representantDades.setPais(interesadoWsDto.getRepresentante().getPais());
			representantDades.setProvincia(interesadoWsDto.getRepresentante().getProvincia());
			interessat.setRepresentante(representantDades);
		}
		
		return interessat;
	}
	
	public static DocumentBuilder getDocumentBuilder() throws Exception {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setCoalescing(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setValidating(false);
			return dbf.newDocumentBuilder();
    	} catch (Exception exc) {
    		throw new Exception(exc.getMessage());
    	}
	}


	@Override
	public List<TipusAssumpte> llistarTipusAssumpte(String entitatcodi) throws RegistrePluginException {
		try {
			return toTipusAssumpte(getInfoApi().listarTipoAsunto(entitatcodi));
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir el tipus d'assumpte", ex);
			throw new RegistrePluginException("Error recuperant tipus assumpte", ex);
		}
	}

	@Override
	public List<CodiAssumpte> llistarCodisAssumpte(String entitatCodi, String tipusAssumpte) throws RegistrePluginException {
		try {
			return toCodisAssumpte(getInfoApi().listarCodigoAsunto(
					entitatCodi,
					tipusAssumpte));
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir el codi d'assumpte", ex);
			throw new RegistrePluginException("Error recuperant codi assumpte", ex);
		}
	}

	@Override
	public Oficina llistarOficinaVirtual(String entitatCodi, Long autoritzacioValor) throws RegistrePluginException {
		List<Oficina> oficines = new ArrayList<Oficina>();
		Oficina oficinaVirtual = new Oficina();
		try {
			oficines = toOficines(getInfoApi().listarOficinas(
					entitatCodi,
					autoritzacioValor));
			if (oficines != null) {
				for (Oficina oficina : oficines) {
					if (oficina.getNom().equalsIgnoreCase(OFICINA_VIRTUAL)) {
						oficinaVirtual = oficina;
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir l'oficina virtual", ex);
			throw new RegistrePluginException("Error recuperant oficina virtual", ex);
		}
		return oficinaVirtual;
	}
	
	@Override
	public List<Oficina> llistarOficines(String entitatCodi, Long autoritzacioValor) throws RegistrePluginException {
		try {
			return toOficines(getInfoApi().listarOficinas(
					entitatCodi,
					autoritzacioValor));
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir el tipus d'assumpte", ex);
			throw new RegistrePluginException("Error obtenint tipus assumpte", ex);
		}
	}
	
	@Override
	public List<Llibre> llistarLlibres(String entitatCodi, String oficina, Long autoritzacioValor) throws RegistrePluginException {
		try {
			return toLlibres(getInfoApi().listarLibros(
					entitatCodi,
					oficina,
					autoritzacioValor));
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir els llibres", ex);
			throw new RegistrePluginException("Error obtenint els llibres", ex);
		}
	}

	@Override
	public List<Organisme> llistarOrganismes(String entitatCodi) throws RegistrePluginException {
		try {
			return toOrganismes(getInfoApi().listarOrganismos(
					entitatCodi));
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir els organismes", ex);
			throw new RegistrePluginException("Error obtenint els organismes", ex);
		}
	}

	@Override
	public List<LlibreOficina> llistarLlibresOficines(
			String entitatCodi, 
			String usuariCodi, 
			Long tipusRegistre) {
		List<LlibreOficina> llibreOficina = null;
		try {
			llibreOficina = toLlibreOficina(getInfoApi().obtenerLibrosOficinaUsuario(
					entitatCodi, 
					usuariCodi, 
					tipusRegistre));
		} catch (RegistrePluginException rex) {
			logger.error("Error a plugin registre obtenció llibres i oficina", rex);
		} catch (WsI18NException wse) {
			logger.error("Error ws obtenció llibres i oficina", wse);
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir els llibres i oficina", ex);
		}
		return llibreOficina;
	}
	
	@Override
	public Llibre llistarLlibreOrganisme(
			String entitatCodi, 
			String organismeCodi) throws RegistrePluginException {
		Llibre llibreOrganisme = new Llibre();
		try {
			llibreOrganisme = toLlibreOrganisme(getInfoApi().listarLibroOrganismo(
					entitatCodi, 
					organismeCodi));
			
		} catch (RegistrePluginException rex) {
			logger.error("Error a plugin registre obtenció llibres i oficina", rex);
		} catch (WsI18NException wse) {
			logger.error("Error ws obtenció llibres i oficina", wse);
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir els llibres i oficina", ex);
		}
		return llibreOrganisme;
	}
	
	private List<LlibreOficina> toLlibreOficina(List<LibroOficinaWs> llibresOficinaWs) throws RegistrePluginException {
		List<LlibreOficina> llibresOficina = new ArrayList<LlibreOficina>();
		try {
			for (LibroOficinaWs llibreOficinaWs : llibresOficinaWs) {
				LlibreOficina llibreOficina = new LlibreOficina();
				Llibre llibre = new Llibre();
				Oficina oficina = new Oficina();
				//Llibre
				llibre.setCodi(llibreOficinaWs.getLibroWs().getCodigoLibro());
				llibre.setNomCurt(llibreOficinaWs.getLibroWs().getNombreCorto());
				llibre.setNomLlarg(llibreOficinaWs.getLibroWs().getNombreLargo());
				llibre.setOrganisme(llibreOficinaWs.getLibroWs().getCodigoOrganismo());
				//Oficina
				oficina.setCodi(llibreOficinaWs.getOficinaWs().getCodigo());
				oficina.setNom(llibreOficinaWs.getOficinaWs().getNombre());
				//Llibre + Oficina
				llibreOficina.setLlibre(llibre);
				llibreOficina.setOficina(oficina);
				llibresOficina.add(llibreOficina);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió dels llibres i oficines", ex);
			throw new RegistrePluginException("Error conversió de llibres i oficines", ex);
		}
		return llibresOficina;
	}
	
	private Llibre toLlibreOrganisme(LibroWs llibreWs) throws RegistrePluginException {
		Llibre llibre = new Llibre();
		try {
			llibre.setCodi(llibreWs.getCodigoLibro());
			llibre.setNomCurt(llibreWs.getNombreCorto());
			llibre.setNomLlarg(llibreWs.getNombreLargo());
			llibre.setOrganisme(llibreWs.getCodigoOrganismo());
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió dels llibres i oficines", ex);
			throw new RegistrePluginException("Error conversió de llibres i oficines", ex);
		}
		return llibre;
	}
	
	private List<Llibre> toLlibres(List<LibroWs> llibresWs) throws RegistrePluginException {
		List<Llibre> llibres = new ArrayList<Llibre>();
		try {
			for (LibroWs llibreWs : llibresWs) {
				Llibre llibre = new Llibre();
				llibre.setCodi(llibreWs.getCodigoLibro());
				llibre.setNomCurt(llibreWs.getNombreCorto());
				llibre.setNomLlarg(llibreWs.getNombreLargo());
				llibre.setOrganisme(llibreWs.getCodigoOrganismo());
				llibres.add(llibre);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió dels llibres", ex);
			throw new RegistrePluginException("Error conversió de llibres", ex);
		}
		return llibres;
	}
	private List<Oficina> toOficines(List<OficinaWs> oficinesWs) throws RegistrePluginException {
		List<Oficina> oficines = new ArrayList<Oficina>();
		
		try {
			for (OficinaWs oficinaWs : oficinesWs) {
				Oficina oficina = new Oficina();
				oficina.setCodi(oficinaWs.getCodigo());
				oficina.setNom(oficinaWs.getNombre());
				oficines.add(oficina);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió de les oficines", ex);
			throw new RegistrePluginException("Error conversió de oficines", ex);
		}
		return oficines;
	}
	private List<Organisme> toOrganismes(List<OrganismoWs> organismesWs) throws RegistrePluginException {
		List<Organisme> organismes = new ArrayList<Organisme>();
		
		try {
			for (OrganismoWs organWs : organismesWs) {
				Organisme organ = new Organisme();
				organ.setCodi(organWs.getCodigo());
				organ.setNom(organWs.getNombre());
				organismes.add(organ);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió dels organismes", ex);
			throw new RegistrePluginException("Error conversió organismes", ex);
		}
		return organismes;
	}
	
	private List<TipusAssumpte> toTipusAssumpte(List<TipoAsuntoWs> tipusAssumpteWs) throws RegistrePluginException {
		List<TipusAssumpte> tipusAssumpte = new ArrayList<TipusAssumpte>();
		try {
			for (TipoAsuntoWs tipusWs : tipusAssumpteWs) {
				TipusAssumpte tipus = new TipusAssumpte();
				tipus.setCodi(tipusWs.getCodigo());
				tipus.setNom(tipusWs.getNombre());
				tipusAssumpte.add(tipus);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió del tipus d'assumpte", ex);
			throw new RegistrePluginException("Error conversió tipus assumpte", ex);
		}
		return tipusAssumpte;
	}
	
	private List<CodiAssumpte> toCodisAssumpte(List<CodigoAsuntoWs> codisAssumpteWs) throws RegistrePluginException {
		List<CodiAssumpte> codisAssumpte = new ArrayList<CodiAssumpte>();
		try {
			for (CodigoAsuntoWs codiWs : codisAssumpteWs) {
				CodiAssumpte codi = new CodiAssumpte();
				codi.setCodi(codiWs.getCodigo());
				codi.setNom(codiWs.getNombre());
				codisAssumpte.add(codi);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió dels codis d'assumpte", ex);
			throw new RegistrePluginException("Error conversió codis assumpte", ex);
		}
		return codisAssumpte;
	}


	@Override
	public RespostaConsultaRegistre comunicarAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb,
			Long tipusOperacio) {
		// TODO Auto-generated method stub
		return null;
	}
	private static final Logger logger = LoggerFactory.getLogger(RegistrePluginRegweb3Impl.class);

}
