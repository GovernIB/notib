package es.caib.notib.plugin.registre;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.RegistreInteressatDto;
import es.caib.notib.core.api.exception.RegistrePluginException;
import es.caib.notib.core.api.ws.registre.RegistreAssentament;
import es.caib.notib.core.api.ws.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.notib.core.api.ws.registre.RespostaConsultaRegistre;
import es.caib.notib.core.api.ws.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.registre.sortida.RegistrePlugin;
import es.caib.regweb3.ws.v3.impl.AnexoWs;
import es.caib.regweb3.ws.v3.impl.AsientoRegistralBean;
import es.caib.regweb3.ws.v3.impl.DatosInteresadoWs;
import es.caib.regweb3.ws.v3.impl.InteresadoWs;
import es.caib.regweb3.ws.v3.impl.JustificanteWs;
import es.caib.regweb3.ws.v3.impl.OficioBean;
import es.caib.regweb3.ws.v3.impl.WsI18NException;
import es.caib.regweb3.ws.v3.impl.WsValidationException;


/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegweb3Impl extends RegWeb3Utils implements RegistrePlugin{
	
//	protected static RegWebRegistroEntradaWs registroEntradaApi;
//	protected static RegWebRegistroSalidaWs registroSalidaApi;
//	protected static RegWebInfoWs registroInfoApi;
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";

	@Override
	public String registrarSortida(
			RegistreAssentament registreSortida,
			String aplicacioNom,
			String aplicacioVersio,
			String entitat) throws RegistrePluginException {
		return "OK";
	}
	
	@Override
	public RespostaConsultaRegistre comunicarAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio) {
		RespostaConsultaRegistre rc = new RespostaConsultaRegistre();
		try {
			return toRespostaConsultaRegistre(getAsientoRegistralApi().comunicarAsientoRegistral(codiDir3Entitat, toAsientoRegistralBean(arb), tipusOperacio));
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
			return toRespostaJustificantRecepcio(getAsientoRegistralApi().obtenerOficioExterno(codiDir3Entitat, numeroRegistreFormatat, llibre));
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
		interessat.setDocumentTipus(RegistreInteressatDocumentTipusEnum.NIF);
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
		interessatDades.setCodigoDire("");
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
	
	private static final Logger logger = LoggerFactory.getLogger(RegistrePluginRegweb3Impl.class);
	
}
