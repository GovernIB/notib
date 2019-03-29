package es.caib.notib.plugin.registre;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.RegistreInteressatDocumentTipusDtoEnum;
import es.caib.notib.core.api.dto.RegistreInteressatDto;
import es.caib.regweb3.ws.api.v3.CodigoAsuntoWs;
import es.caib.regweb3.ws.api.v3.TipoAsuntoWs;
import es.caib.regweb3.ws.v3.impl.AnexoWs;
import es.caib.regweb3.ws.v3.impl.AsientoRegistralBean;
import es.caib.regweb3.ws.v3.impl.DatosInteresadoWs;
import es.caib.regweb3.ws.v3.impl.IdentificadorWs;
import es.caib.regweb3.ws.v3.impl.InteresadoWs;
import es.caib.regweb3.ws.v3.impl.JustificanteWs;
import es.caib.regweb3.ws.v3.impl.OficioBean;
import es.caib.regweb3.ws.v3.impl.RegistroSalidaWs;
import es.caib.regweb3.ws.v3.impl.WsI18NException;
import es.caib.regweb3.ws.v3.impl.WsValidationException;


/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegweb3Impl extends RegWeb3Utils implements RegistrePlugin{
	
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";


	@Override
	public RespostaAnotacioRegistre registrarSalida(
			RegistreSortida registreSortida,
			String aplicacion) throws RegistrePluginException {
		RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();;
		try {
			resposta = toRespostaAnotacioRegistre(getRegistroSalidaApi().altaRegistroSalida(
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
	public RespostaConsultaRegistre comunicarAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio) {
		RespostaConsultaRegistre rc = new RespostaConsultaRegistre();
		try {
			return toRespostaConsultaRegistre(
					getAsientoRegistralApi().comunicarAsientoRegistral(
							codiDir3Entitat, 
							toAsientoRegistralBean(arb), 
							tipusOperacio));
		} catch (WsI18NException e) {
			rc.setErrorCodi("0");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		} catch (WsValidationException e) {
			rc.setErrorCodi("1");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		} catch (Exception e) {
			rc.setErrorCodi("2");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		}
	}
	
	@Override
	public RespostaConsultaRegistre salidaAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio) {
		RespostaConsultaRegistre rc = new RespostaConsultaRegistre();
		try {
			return toRespostaConsultaRegistre(getAsientoRegistralApi().crearAsientoRegistral(codiDir3Entitat, toAsientoRegistralBean(arb)));
		} catch (WsI18NException e) {
			rc.setErrorCodi("0");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		} catch (WsValidationException e) {
			rc.setErrorCodi("1");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		} catch (Exception e) {
			rc.setErrorCodi("2");
			rc.setErrorDescripcio(e.getMessage());
			return rc;
		}
	}
	
	@Override
	public RespostaJustificantRecepcio obtenerJustificante(String codiDir3Entitat, String numeroRegistreFormatat, String llibre, Long tipusRegistre) {
		RespostaJustificantRecepcio rj = new RespostaJustificantRecepcio();
		try {
			return toRespostaJustificantRecepcio(getAsientoRegistralApi().obtenerJustificante(codiDir3Entitat, numeroRegistreFormatat, llibre, tipusRegistre));
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
			return rj;
		}
	}
	
	@Override
	public RespostaJustificantRecepcio obtenerOficioExterno(String codiDir3Entitat, String numeroRegistreFormatat, String llibre) {
		RespostaJustificantRecepcio rj = new RespostaJustificantRecepcio();
		try {
			return toRespostaJustificantRecepcio(getAsientoRegistralApi().obtenerOficioExterno(
					codiDir3Entitat,
					numeroRegistreFormatat, 
					llibre));
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

	public RespostaJustificantRecepcio toRespostaJustificantRecepcio(OficioBean ofici){
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
			rsw.setFecha(toXmlGregorianCalendar(new Date()));
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
					anexo.setOrigenCiudadanoAdmin(1); //Administaración
					anexo.setTipoDocumental("TD01");
				}
			}
			if (anexo != null) {
				rsw.getAnexos().add(anexo);
			}
			datosInteresado.setApellido1(registreSortida.getDadesInteressat().getCognom1());
			datosInteresado.setApellido2(registreSortida.getDadesInteressat().getCognom2());
			datosInteresado.setDocumento(registreSortida.getDadesInteressat().getNif());
			datosInteresado.setNombre(registreSortida.getDadesInteressat().getNom());
			datosInteresado.setTipoInteresado(registreSortida.getDadesInteressat().getTipusInteressat());
			interesado.setInteresado(datosInteresado);
			
			rsw.getInteresados().add(interesado);
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió a registroSalidaWs", ex);
			throw new RegistrePluginException("Error conversió a registroSalidaWs", ex);
		}
		return rsw;
	}
	public AsientoRegistralBean toAsientoRegistralBean(AsientoRegistralBeanDto dto) {
		AsientoRegistralBean ar = new AsientoRegistralBean();
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
		ar.setFechaRecepcion(dto.getFechaRecepcion());
		ar.setFechaRegistro(dto.getFechaRegistro());
		ar.setFechaRegistroDestino(dto.getFechaRegistroDestino());
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
		ar.setVersion(dto.getVersion());
		return ar;
	}
	
	public RespostaAnotacioRegistre toRespostaAnotacioRegistre(IdentificadorWs iw) throws DatatypeConfigurationException {
		RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
		
		resposta.setData(toDate(iw.getFecha()));
		resposta.setNumeroRegistroFormateado(iw.getNumeroRegistroFormateado());
		resposta.setNumero(String.valueOf(iw.getNumero()));
		return resposta;
	}
	
	public RespostaConsultaRegistre toRespostaConsultaRegistre(AsientoRegistralBean ar) {
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		
		return resposta;
	}
	
	
	
	public AsientoRegistralBean notificacioToAsientoRegistralBean(NotificacioDto notificacio, AnexoWs anexe) {
		AsientoRegistralBean registre = new AsientoRegistralBean();
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
		registre.setAplicacionTelematica("NOTIB");
		registre.setAplicacion("NOTIB");
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
//		interessatDades.setTipoInteresado(persona.getTipusInteressat());/*--------------*/
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
			throw new RegistrePluginException("Error obtenint tipus assumpte", ex);
		}
	}

	@Override
	public List<CodiAssumpte> llistarCodisAssumpte(String entitatCodi, String tipusAssumpte) throws RegistrePluginException {
		try {
			return toCodisAssumpte(getInfoApi().listarCodigoAsunto(
					entitatCodi,
					tipusAssumpte));
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir el tipus d'assumpte", ex);
			throw new RegistrePluginException("Error obtenint tipus assumpte", ex);
		}
	}

	@Override
	public List<Oficina> llistarOficines(String entitatCodi, Long autoritzacioValor) {
		return null;
	}

	@Override
	public List<Llibre> llistarLlibres(String entitatCodi, String oficina, Long autoritzacioValor) {
		return null;
	}

	@Override
	public List<Organisme> llistarOrganismes(String entitatCodi) {
		return null;
	}

	private List<TipusAssumpte> toTipusAssumpte(List<TipoAsuntoWs> tipoAsunto) throws RegistrePluginException {
		List<TipusAssumpte> tipusAssumpte = new ArrayList<TipusAssumpte>();
		try {
			TipusAssumpte tipus = new TipusAssumpte();
			
			for (TipoAsuntoWs tipo : tipoAsunto) {
				tipus.setCodi(tipo.getCodigo());
				tipus.setNom(tipo.getNombre());
				tipusAssumpte.add(tipus);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió del tipus d'assumpte", ex);
			throw new RegistrePluginException("Error conversió tipus assumpte", ex);
		}
		return tipusAssumpte;
	}
	
	private List<CodiAssumpte> toCodisAssumpte(List<CodigoAsuntoWs> codigoAsunto) throws RegistrePluginException {
		List<CodiAssumpte> codisAssumpte = new ArrayList<CodiAssumpte>();
		try {
			CodiAssumpte codi = new CodiAssumpte();
			
			for (CodigoAsuntoWs codigo : codigoAsunto) {
				codi.setCodi(codigo.getCodigo());
				codi.setNom(codigo.getNombre());
				codisAssumpte.add(codi);
			}
		} catch (Exception ex) {
			logger.error("Error a l'hora de fer la conversió dels codis d'assumpte", ex);
			throw new RegistrePluginException("Error conversió codis assumpte", ex);
		}
		return codisAssumpte;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegistrePluginRegweb3Impl.class);
}
