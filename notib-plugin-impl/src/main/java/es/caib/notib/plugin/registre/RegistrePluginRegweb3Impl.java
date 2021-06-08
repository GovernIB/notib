package es.caib.notib.plugin.registre;

import es.caib.notib.core.api.dto.*;
import es.caib.regweb3.ws.api.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegweb3Impl extends RegWeb3Utils implements RegistrePlugin{
	
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	
	private static final String OFICINA_VIRTUAL_DEFAULT = "Oficina Virtual";


	@Override
	public RespostaConsultaRegistre salidaAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio,
			boolean generarJustificant) {
		RespostaConsultaRegistre rc = new RespostaConsultaRegistre();
		
			try {			
				AsientoRegistralWs asiento = toAsientoRegistralBean(arb);
				return toRespostaConsultaRegistre(getAsientoRegistralApi().crearAsientoRegistral(
						null,
						codiDir3Entitat, 
						asiento, 
						tipusOperacio,
						generarJustificant,
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
	public RespostaConsultaRegistre obtenerAsientoRegistral(
			String codiDir3Entitat, 
			String numeroRegistre,
			Long tipusOperacio,
			boolean ambAnnexos) {
		RespostaConsultaRegistre rc = new RespostaConsultaRegistre();
		try {
//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//			logger.info(ow.writeValueAsString(arb.getInteresados()));
			AsientoRegistralWs asientoRegistralWs = getAsientoRegistralApi().obtenerAsientoRegistral(
					codiDir3Entitat, 
					numeroRegistre, 
					tipusOperacio, 
					ambAnnexos);
			
			return toRespostaConsultaRegistre(asientoRegistralWs);
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
			return toRespostaJustificantRecepcio(getAsientoRegistralApi().obtenerJustificante(
					codiDir3Entitat, 
					numeroRegistreFormatat, 
					tipusRegistre));
//			return toRespostaJustificantRecepcio(getRegistroSalidaApi().obtenerJustificante(
//					codiDir3Entitat, 
//					numeroRegistreFormatat));
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

	private AsientoRegistralWs toAsientoRegistralBean(AsientoRegistralBeanDto dto) {
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
		//ar.setTipoAsunto(dto.getTipoAsunto());
		//ar.setTipoAsuntoDenominacion(dto.getTipoAsuntoDenominacion());
		ar.setTipoDocumentacionFisicaCodigo(dto.getTipoDocumentacionFisicaCodigo());
		ar.setTipoEnvioDocumentacion(dto.getTipoEnvioDocumentacion());
		ar.setTipoRegistro(dto.getTipoRegistro());
		ar.setTipoTransporte(dto.getTipoTransporte());
		ar.setUnidadTramitacionDestinoCodigo(dto.getUnidadTramitacionDestinoCodigo());
		ar.setUnidadTramitacionDestinoDenominacion(dto.getUnidadTramitacionDestinoDenominacion());
		ar.setUnidadTramitacionOrigenCodigo(dto.getUnidadTramitacionOrigenCodigo());
		ar.setUnidadTramitacionOrigenDenominacion(dto.getUnidadTramitacionOrigenDenominacion());
		if(dto.getAnexos() != null) {
			for (AnexoWsDto anexo: dto.getAnexos()) {
				AnexoWs anexe = new AnexoWs();
				anexe.setCsv(anexo.getCsv());
				anexe.setFicheroAnexado(anexo.getFicheroAnexado());
				anexe.setFirmaAnexada(anexo.getFirmaAnexada());
				anexe.setModoFirma(anexo.getModoFirma());
				anexe.setNombreFicheroAnexado(anexo.getNombreFicheroAnexado());
				anexe.setNombreFirmaAnexada(anexo.getNombreFirmaAnexada());
				anexe.setObservaciones(anexo.getObservaciones());
				anexe.setOrigenCiudadanoAdmin(anexo.getOrigenCiudadanoAdmin());
				anexe.setTipoDocumental(anexo.getTipoDocumental());
				anexe.setTipoDocumento(anexo.getTipoDocumento());
				anexe.setTipoMIMEFicheroAnexado(anexo.getTipoMIMEFicheroAnexado());
				anexe.setTipoMIMEFirmaAnexada(anexo.getTipoMIMEFirmaAnexada());
				anexe.setTitulo(anexo.getTitulo());
				anexe.setValidezDocumento(anexo.getValidezDocumento());				
				ar.getAnexos().add(anexe);
			}
		}
		//Interessat + representant
		if (dto.getInteresados() != null) {
			for (InteresadoWsDto interessatWsDto : dto.getInteresados()) {
				InteresadoWs interessat = interesadoWsDtoToInteresadoWs(interessatWsDto);
				ar.getInteresados().add(interessat);
			}
		}
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
		resposta.setSirRecepecioData(ar.getFechaRecepcion());
		resposta.setSirRegistreDestiData(ar.getFechaRegistroDestino());
		resposta.setNumeroRegistroDestino(ar.getNumeroRegistroDestino());
		resposta.setMotivo(ar.getMotivo());
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
		if (ar.getCodigoError() != null && !ar.getCodigoError().isEmpty()) {
			resposta.setErrorCodi(ar.getCodigoError());
			resposta.setErrorDescripcio(ar.getDescripcionError());
		}
		return resposta;
	}
	
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
			String nom = interesadoWsDto.getInteresado().getNombre();
			String raoSocial = interesadoWsDto.getInteresado().getRazonSocial();
			DatosInteresadoWs interessatDades = new DatosInteresadoWs();
			interessatDades.setTipoInteresado(interesadoWsDto.getInteresado().getTipoInteresado().longValue());
			interessatDades.setTipoDocumentoIdentificacion(interesadoWsDto.getInteresado().getTipoDocumentoIdentificacion());
			interessatDades.setDocumento(interesadoWsDto.getInteresado().getDocumento());
			interessatDades.setNombre(nom);
			interessatDades.setRazonSocial(raoSocial != null ? raoSocial : nom);
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
			String nom = interesadoWsDto.getRepresentante().getNombre();
			String raoSocial = interesadoWsDto.getRepresentante().getRazonSocial();
			DatosInteresadoWs representantDades = new DatosInteresadoWs();
			representantDades.setTipoInteresado(interesadoWsDto.getRepresentante().getTipoInteresado().longValue());
			representantDades.setTipoDocumentoIdentificacion(interesadoWsDto.getInteresado().getTipoDocumentoIdentificacion());
			representantDades.setDocumento(interesadoWsDto.getRepresentante().getDocumento());
			representantDades.setRazonSocial(raoSocial != null ? raoSocial : nom);
			representantDades.setNombre(nom);
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
			throw new RegistrePluginException("Error recuperant codi assumpte", ex);
		}
	}

	@Override
	public Oficina llistarOficinaVirtual(
			String entitatCodi, 
			String nomOficinaVirtualEntitat,
			Long autoritzacioValor) throws RegistrePluginException {
		List<Oficina> oficines = new ArrayList<Oficina>();
		Oficina oficinaVirtual = new Oficina();
		String nomOficinaVirtual = nomOficinaVirtualEntitat != null ? nomOficinaVirtualEntitat : OFICINA_VIRTUAL_DEFAULT;
		try {
			oficines = toOficines(getInfoApi().listarOficinas(
					entitatCodi,
					autoritzacioValor));
			if (oficines != null) {
				for (Oficina oficina : oficines) {
					if (oficina.getNom().equalsIgnoreCase(nomOficinaVirtual)) {
						oficinaVirtual = oficina;
					}
				}
			}
		} catch (Exception ex) {
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
			throw new RegistrePluginException("Error obtenint les oficines", ex);
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
			throw new RegistrePluginException("Error obtenint els llibres", ex);
		}
	}

	@Override
	public List<Organisme> llistarOrganismes(String entitatCodi) throws RegistrePluginException {
		try {
			return toOrganismes(getInfoApi().listarOrganismos(
					entitatCodi));
		} catch (Exception ex) {
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
			logger.error("Error a plugin registre obtenció llibres d'organisme", rex);
		} catch (WsI18NException wse) {
			logger.error("Error ws obtenció llibres organisme", wse);
		} catch (Exception ex) {
			logger.error("Error a l'hora d'obtenir els llibres d'organisme", ex);
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

	private static final Logger logger = LoggerFactory.getLogger(RegistrePluginRegweb3Impl.class);

}
