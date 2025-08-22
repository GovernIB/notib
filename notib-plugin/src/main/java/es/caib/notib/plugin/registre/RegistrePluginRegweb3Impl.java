package es.caib.notib.plugin.registre;

import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.InteresadoWsDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.RegistreInteressatDocumentTipusDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreInteressatDto;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
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
import es.caib.regweb3.ws.api.v3.TipoAsuntoWs;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class RegistrePluginRegweb3Impl extends RegWeb3Utils implements RegistrePlugin{
	
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	private static final String OFICINA_VIRTUAL_DEFAULT = "Oficina Virtual";
	private static final String ERROR_TO_RESPOSTA_CONSULTA = "Error no controlat toRespostaConsultaRegistre ";

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public RegistrePluginRegweb3Impl(Properties properties, boolean configuracioEspecifica) {
		super(properties);
		salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.REGISTRE")));
	}


	@Override
	public RespostaConsultaRegistre salidaAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio, boolean generarJustificant) {

		var rc = new RespostaConsultaRegistre();
		try {
            long startTime = System.currentTimeMillis();
			var asiento = toAsientoRegistralBean(arb);
			logger.info("[REGISTRE] Creant assentament registral codiDir3Entitat " + codiDir3Entitat  + " tipusOperacio " + tipusOperacio + " generarJustificant " + generarJustificant);
			var resposta = getAsientoRegistralApi().crearAsientoRegistral(null, codiDir3Entitat, asiento, tipusOperacio, generarJustificant, false);
			logger.info("[REGISTRE] Resposta assentament registral " + resposta);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return toRespostaConsultaRegistre(resposta);
		} catch (WsI18NException e) {
			rc.setErrorCodi("0");
			rc.setErrorDescripcio(e.getMessage());
            salutPluginComponent.incrementarOperacioError();
			return rc;
		} catch (WsValidationException e) {
			rc.setErrorCodi("1");
			rc.setErrorDescripcio(e.getMessage());
            salutPluginComponent.incrementarOperacioError();
			return rc;
		} catch (Exception e) {
			log.error(ERROR_TO_RESPOSTA_CONSULTA, e);
			rc.setErrorCodi("2");
			rc.setErrorDescripcio(!Strings.isNullOrEmpty(e.getMessage()) ? e.getMessage() : e.getCause().getMessage());
            salutPluginComponent.incrementarOperacioError();
			return rc;
		}
	}
	
	@Override
	public RespostaConsultaRegistre obtenerAsientoRegistral(String codiDir3Entitat, String numeroRegistre, Long tipusOperacio, boolean ambAnnexos) {

		var rc = new RespostaConsultaRegistre();
		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Creant assentament registral codiDir3Entitat " + codiDir3Entitat + " numeroRegistre " + numeroRegistre + " tipusOperacio " + tipusOperacio + " ambAnnexos " + ambAnnexos);
			var asientoRegistralWs = getAsientoRegistralApi().obtenerAsientoRegistral(codiDir3Entitat, numeroRegistre, tipusOperacio, ambAnnexos);
			logger.info("[REGISTRE] Assentament registral " + asientoRegistralWs);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return toRespostaConsultaRegistre(asientoRegistralWs);
		} catch (WsI18NException e) {
			rc.setErrorCodi("0");
			rc.setErrorDescripcio(e.getMessage());
            salutPluginComponent.incrementarOperacioError();
			return rc;
		} catch (WsValidationException e) {
			rc.setErrorCodi("1");
			rc.setErrorDescripcio(e.getMessage());
            salutPluginComponent.incrementarOperacioError();
			return rc;
		} catch (Exception e) {
			log.error(ERROR_TO_RESPOSTA_CONSULTA, e);
			rc.setErrorCodi("2");
			rc.setErrorDescripcio(e.getMessage());
            salutPluginComponent.incrementarOperacioError();
			return rc;
		}
	}
	
	@Override
	public RespostaJustificantRecepcio obtenerJustificante(String codiDir3Entitat, String numeroRegistreFormatat, long tipusRegistre){

		var rj = new RespostaJustificantRecepcio();
		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenint justificant " + codiDir3Entitat + " numeroRegistreFormatat " + numeroRegistreFormatat);
			var justificant = getAsientoRegistralApi().obtenerJustificante(codiDir3Entitat, numeroRegistreFormatat, tipusRegistre);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return toRespostaJustificantRecepcio(justificant);
		} catch (WsI18NException e) {
			rj.setErrorCodi("0");
			rj.setErrorDescripcio("No s'ha pogut obtenir el justificant");
            salutPluginComponent.incrementarOperacioError();
			return rj;
		} catch (WsValidationException e) {
			rj.setErrorCodi("1");
			rj.setErrorDescripcio("Error de validació a l'obtenir el justificant");
            salutPluginComponent.incrementarOperacioError();
			return rj;
		} catch (Exception e) {
			rj.setErrorCodi("2");
			rj.setErrorDescripcio("Error obtenint el justificant");
			log.error("Error no controlat toRespostaJustificantRecepcio ", e);
            salutPluginComponent.incrementarOperacioError();
			return rj;
		}
	}
	
	@Override
	public RespostaJustificantRecepcio obtenerOficioExterno(String codiDir3Entitat, String numeroRegistreFormatat) {

		var rj = new RespostaJustificantRecepcio();
		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenint ofici extern codiDir3Entitat " + codiDir3Entitat + " numeroRegistreFormatat " + numeroRegistreFormatat);
            OficioWs oficioWs = getAsientoRegistralApi().obtenerOficioExterno(codiDir3Entitat, numeroRegistreFormatat);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return toRespostaJustificantRecepcio(oficioWs);
		} catch (WsI18NException e) {
			rj.setErrorCodi("0");
			rj.setErrorDescripcio("No s'ha pogut obtenir l'ofici extern");
            salutPluginComponent.incrementarOperacioError();
			return rj;
		} catch (WsValidationException e) {
			rj.setErrorCodi("1");
			rj.setErrorDescripcio("Error de validació a l'obtenir l'ofici extern");
            salutPluginComponent.incrementarOperacioError();
			return rj;
		} catch (Exception e) {
			rj.setErrorCodi("2");
			rj.setErrorDescripcio("Error obtenint l'ofici extern");
            salutPluginComponent.incrementarOperacioError();
			return rj;
		}
	}

	public RespostaJustificantRecepcio toRespostaJustificantRecepcio(JustificanteWs ofici) {

		var rj = new RespostaJustificantRecepcio();
		rj.setJustificant(ofici.getJustificante());
		return rj;
	}

	public RespostaJustificantRecepcio toRespostaJustificantRecepcio(OficioWs ofici) {

		var rj = new RespostaJustificantRecepcio();
		rj.setJustificant(ofici.getOficio());
		return rj;
	}

	private AsientoRegistralWs toAsientoRegistralBean(AsientoRegistralBeanDto dto) throws Exception{

		var ar = new AsientoRegistralWs();
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
		ar.setId(dto.getId());
		ar.setIdentificadorIntercambio(dto.getIdentificadorIntercambio());
		ar.setIdioma(dto.getIdioma() != null && dto.getIdioma() != 0L ? dto.getIdioma() : 1L);
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
		ar.setTipoDocumentacionFisicaCodigo(dto.getTipoDocumentacionFisicaCodigo());
		ar.setTipoEnvioDocumentacion(dto.getTipoEnvioDocumentacion());
		ar.setTipoRegistro(dto.getTipoRegistro());
		ar.setTipoTransporte(dto.getTipoTransporte());
		ar.setUnidadTramitacionDestinoCodigo(dto.getUnidadTramitacionDestinoCodigo());
		ar.setUnidadTramitacionDestinoDenominacion(dto.getUnidadTramitacionDestinoDenominacion());
		ar.setUnidadTramitacionOrigenCodigo(dto.getUnidadTramitacionOrigenCodigo());
		ar.setUnidadTramitacionOrigenDenominacion(dto.getUnidadTramitacionOrigenDenominacion());
		if(dto.getAnexos() != null) {
			AnexoWs anexe;
			for (var anexo: dto.getAnexos()) {
				if (anexo == null) {
					continue;
				}
				anexe = new AnexoWs();
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
//			if (ar.getAnexos().isEmpty()) {
//				throw new Exception("Error al toAsientoRegistralBean. AsientoRegistralBeanDto conte annexos pero aquests estan buits");
//			}
		}
		//Interessat + representant
		if (dto.getInteresados() != null) {
			InteresadoWs interessat;
			for (var interessatWsDto : dto.getInteresados()) {
				interessat = interesadoWsDtoToInteresadoWs(interessatWsDto);
				ar.getInteresados().add(interessat);
			}
		}
		ar.setVersion(dto.getVersion());
		return ar;
	}
	
	public RespostaAnotacioRegistre toRespostaAnotacioRegistre(IdentificadorWs iw) {

		var resposta = new RespostaAnotacioRegistre();
		resposta.setData(new Timestamp(System.currentTimeMillis()));
		resposta.setNumeroRegistroFormateado(iw.getNumeroRegistroFormateado());
		resposta.setNumero(String.valueOf(iw.getNumero()));
		return resposta;
	}
	
	public RespostaConsultaRegistre toRespostaConsultaRegistre(AsientoRegistralWs ar) {

		var resposta = new RespostaConsultaRegistre();
		resposta.setRegistreNumeroFormatat(ar.getNumeroRegistroFormateado());
		resposta.setRegistreNumero(Integer.toString(ar.getNumeroRegistro()));
		resposta.setRegistreData(timeStampToDate(ar.getFechaRegistro()));
		resposta.setSirRecepecioData(timeStampToDate(ar.getFechaRecepcion()));
		resposta.setSirRegistreDestiData(timeStampToDate(ar.getFechaRegistroDestino()));
		resposta.setNumeroRegistroDestino(ar.getNumeroRegistroDestino());
		resposta.setMotivo(ar.getMotivo());
		resposta.setCodigoEntidadRegistralProcesado(ar.getCodigoEntidadRegistralProcesado());
		resposta.setDecodificacionEntidadRegistralProcesado(ar.getDecodificacionEntidadRegistralProcesado());
		resposta.setOficinaDenominacio(ar.getEntidadDenominacion());
		resposta.setCodiLlibre(ar.getLibroCodigo());
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
			case 14:
				resposta.setEstat(NotificacioRegistreEstatEnumDto.ENVIAT_NOTIFICAR);
				break;
			default:
				log.error("AsientoRegistralWs no te un estat conegut estat -> " +  ar.getEstado());
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

	private Date timeStampToDate(Timestamp timestamp) {

		if (timestamp == null) {
			return null;
		}
		var cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		return cal.getTime();
	}
	
	public RegistreInteressatDto personaToRegistreInteresatDto (PersonaDto persona) {

		var interessat = new RegistreInteressatDto();
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

		var interessat = new InteresadoWs();
		var interessatDades = new DatosInteresadoWs();
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

		var interessat = new InteresadoWs();
		if (interesadoWsDto.getInteresado() != null) {
			String nom = interesadoWsDto.getInteresado().getNombre();
			String raoSocial = interesadoWsDto.getInteresado().getRazonSocial();
			DatosInteresadoWs interessatDades = new DatosInteresadoWs();
			interessatDades.setTipoInteresado(interesadoWsDto.getInteresado().getTipoInteresado());
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
			interessatDades.setLocalidad(interesadoWsDto.getInteresado().getLocalidad());
			interessatDades.setPais(interesadoWsDto.getInteresado().getPais());
			interessatDades.setProvincia(interesadoWsDto.getInteresado().getProvincia());
			interessat.setInteresado(interessatDades);
		}

		if (interesadoWsDto.getRepresentante() != null) {
			String nom = interesadoWsDto.getRepresentante().getNombre();
			String raoSocial = interesadoWsDto.getRepresentante().getRazonSocial();
			DatosInteresadoWs representantDades = new DatosInteresadoWs();
			representantDades.setTipoInteresado(interesadoWsDto.getRepresentante().getTipoInteresado());
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
			representantDades.setLocalidad(interesadoWsDto.getRepresentante().getLocalidad());
			representantDades.setPais(interesadoWsDto.getRepresentante().getPais());
			representantDades.setProvincia(interesadoWsDto.getRepresentante().getProvincia());
			interessat.setRepresentante(representantDades);
		}
		return interessat;
	}

	@Override
	public List<TipusAssumpte> llistarTipusAssumpte(String entitatCodi) throws RegistrePluginException {

		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Llistant tipus assumpte entitatCodi " + entitatCodi);
            List<TipoAsuntoWs> tipoAsuntoWs = getInfoApi().listarTipoAsunto(entitatCodi);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return toTipusAssumpte(tipoAsuntoWs);
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			throw new RegistrePluginException("Error recuperant tipus assumpte", ex);
		}
	}

	@Override
	public List<CodiAssumpte> llistarCodisAssumpte(String entitatCodi, String tipusAssumpte) throws RegistrePluginException {

		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Llistant tipus assumpte entitatCodi " + entitatCodi + " tipusAssumpte " + tipusAssumpte);
            List<CodigoAsuntoWs> codigoAsuntoWs = getInfoApi().listarCodigoAsunto(entitatCodi, tipusAssumpte);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return toCodisAssumpte(codigoAsuntoWs);
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			throw new RegistrePluginException("Error recuperant codi assumpte", ex);
		}
	}

	@Override
	public Oficina llistarOficinaVirtual(String entitatCodi, String nomOficinaVirtualEntitat, Long autoritzacioValor) throws RegistrePluginException {

		String nomOficinaVirtual = nomOficinaVirtualEntitat != null ? nomOficinaVirtualEntitat : OFICINA_VIRTUAL_DEFAULT;
		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenir oficina virtual entitatCodi " + entitatCodi + " nomOficinaVirtualEntitat " + nomOficinaVirtualEntitat + " autoritzacioValor " + autoritzacioValor);
			var oficines = toOficines(getInfoApi().listarOficinas(entitatCodi, autoritzacioValor));
			if (oficines.isEmpty()) {
                salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
				return new Oficina();
			}
			Oficina oficinaVirtual = new Oficina();
			for (Oficina oficina : oficines) {
				if (oficina.getNom().equalsIgnoreCase(nomOficinaVirtual)) {
					oficinaVirtual = oficina;
				}
			}
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return oficinaVirtual;
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			throw new RegistrePluginException("Error recuperant oficina virtual", ex);
		}
	}
	
	@Override
	public List<Oficina> llistarOficines(String entitatCodi, Long autoritzacioValor) throws RegistrePluginException {

		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenir oficines virtual entitatCodi " + entitatCodi + " autoritzacioValor " + autoritzacioValor);
            List<OficinaWs> oficinaWs = getInfoApi().listarOficinas(entitatCodi, autoritzacioValor);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return toOficines(oficinaWs);
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			throw new RegistrePluginException("Error obtenint les oficines", ex);
		}
	}
	
	@Override
	public List<Llibre> llistarLlibres(String entitatCodi, String oficina, Long autoritzacioValor) throws RegistrePluginException {

		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenir llibres entitatCodi " + entitatCodi + " oficina " + oficina + " autoritzacioValor " + autoritzacioValor);
            List<LibroWs> libroWs = getInfoApi().listarLibros(entitatCodi, oficina, autoritzacioValor);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return toLlibres(libroWs);
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			throw new RegistrePluginException("Error obtenint els llibres", ex);
		}
	}

	@Override
	public List<Organisme> llistarOrganismes(String entitatCodi) throws RegistrePluginException {

		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenir llibres entitatCodi " + entitatCodi);
            List<OrganismoWs> organismoWs = getInfoApi().listarOrganismos(entitatCodi);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return toOrganismes(organismoWs);
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			throw new RegistrePluginException("Error obtenint els organismes", ex);
		}
	}

	@Override
	public List<LlibreOficina> llistarLlibresOficines(String entitatCodi, String usuariCodi, Long tipusRegistre) {

		List<LlibreOficina> llibreOficina = null;
		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenir llibres oficines entitatCodi " + entitatCodi + " usuariCodi " + usuariCodi + " tipusRegistre " + tipusRegistre);
            List<LibroOficinaWs> libroOficinaWs = getInfoApi().obtenerLibrosOficinaUsuario(entitatCodi, usuariCodi, tipusRegistre);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            llibreOficina = toLlibreOficina(libroOficinaWs);
		} catch (RegistrePluginException rex) {
            salutPluginComponent.incrementarOperacioError();
			log.error("Error a plugin registre obtenció llibres i oficina", rex);
		} catch (WsI18NException wse) {
            salutPluginComponent.incrementarOperacioError();
			log.error("Error ws obtenció llibres i oficina", wse);
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			log.error("Error a l'hora d'obtenir els llibres i oficina", ex);
		}
		return llibreOficina;
	}
	
	@Override
	public Llibre llistarLlibreOrganisme(String entitatCodi, String organismeCodi) throws RegistrePluginException {

		var llibreOrganisme = new Llibre();
		try {
            long startTime = System.currentTimeMillis();
			logger.info("[REGISTRE] Obtenir llibres oficines entitatCodi " + entitatCodi + " organismeCodi " + organismeCodi);
            LibroWs libroWs = getInfoApi().listarLibroOrganismo(entitatCodi, organismeCodi);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            llibreOrganisme = toLlibreOrganisme(libroWs);
		} catch (RegistrePluginException rex) {
            salutPluginComponent.incrementarOperacioError();
			log.error("Error a plugin registre obtenció llibres d'organisme", rex);
		} catch (WsI18NException wse) {
            salutPluginComponent.incrementarOperacioError();
			log.error("Error ws obtenció llibres organisme", wse);
		} catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
			log.error("Error a l'hora d'obtenir els llibres d'organisme", ex);
		}
		return llibreOrganisme;
	}
	
	private List<LlibreOficina> toLlibreOficina(List<LibroOficinaWs> llibresOficinaWs) throws RegistrePluginException {

		List<LlibreOficina> llibresOficina = new ArrayList<>();
		try {
			LlibreOficina llibreOficina;
			Llibre llibre;
			Oficina oficina;
			for (var llibreOficinaWs : llibresOficinaWs) {
				llibreOficina = new LlibreOficina();
				llibre = new Llibre();
				oficina = new Oficina();
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
			log.error("Error a l'hora de fer la conversió dels llibres i oficines", ex);
			throw new RegistrePluginException("Error conversió de llibres i oficines", ex);
		}
		return llibresOficina;
	}
	
	private Llibre toLlibreOrganisme(LibroWs llibreWs) throws RegistrePluginException {

		try {
			var llibre = new Llibre();
			llibre.setCodi(llibreWs.getCodigoLibro());
			llibre.setNomCurt(llibreWs.getNombreCorto());
			llibre.setNomLlarg(llibreWs.getNombreLargo());
			llibre.setOrganisme(llibreWs.getCodigoOrganismo());
			return llibre;
		} catch (Exception ex) {
			log.error("Error a l'hora de fer la conversió dels llibres i oficines", ex);
			throw new RegistrePluginException("Error conversió de llibres i oficines", ex);
		}
	}
	
	private List<Llibre> toLlibres(List<LibroWs> llibresWs) throws RegistrePluginException {

		try {
			List<Llibre> llibres = new ArrayList<>();
			Llibre llibre;
			for (var llibreWs : llibresWs) {
				llibre = new Llibre();
				llibre.setCodi(llibreWs.getCodigoLibro());
				llibre.setNomCurt(llibreWs.getNombreCorto());
				llibre.setNomLlarg(llibreWs.getNombreLargo());
				llibre.setOrganisme(llibreWs.getCodigoOrganismo());
				llibres.add(llibre);
			}
			return llibres;
		} catch (Exception ex) {
			log.error("Error a l'hora de fer la conversió dels llibres", ex);
			throw new RegistrePluginException("Error conversió de llibres", ex);
		}
	}

	private List<Oficina> toOficines(List<OficinaWs> oficinesWs) throws RegistrePluginException {

		List<Oficina> oficines = new ArrayList<>();
		try {
			Oficina oficina;
			for (var oficinaWs : oficinesWs) {
				oficina = new Oficina();
				oficina.setCodi(oficinaWs.getCodigo());
				oficina.setNom(oficinaWs.getNombre());
				oficines.add(oficina);
			}
		} catch (Exception ex) {
			log.error("Error a l'hora de fer la conversió de les oficines", ex);
			throw new RegistrePluginException("Error conversió de oficines", ex);
		}
		return oficines;
	}
	private List<Organisme> toOrganismes(List<OrganismoWs> organismesWs) throws RegistrePluginException {

		List<Organisme> organismes = new ArrayList<>();
		try {
			Organisme organ;
			for (var organWs : organismesWs) {
				organ = new Organisme();
				organ.setCodi(organWs.getCodigo());
				organ.setNom(organWs.getNombre());
				organismes.add(organ);
			}
		} catch (Exception ex) {
			log.error("Error a l'hora de fer la conversió dels organismes", ex);
			throw new RegistrePluginException("Error conversió organismes", ex);
		}
		return organismes;
	}
	
	private List<TipusAssumpte> toTipusAssumpte(List<TipoAsuntoWs> tipusAssumpteWs) throws RegistrePluginException {

		List<TipusAssumpte> tipusAssumpte = new ArrayList<>();
		try {
			TipusAssumpte tipus;
			for (var tipusWs : tipusAssumpteWs) {
				tipus = new TipusAssumpte();
				tipus.setCodi(tipusWs.getCodigo());
				tipus.setNom(tipusWs.getNombre());
				tipusAssumpte.add(tipus);
			}
		} catch (Exception ex) {
			log.error("Error a l'hora de fer la conversió del tipus d'assumpte", ex);
			throw new RegistrePluginException("Error conversió tipus assumpte", ex);
		}
		return tipusAssumpte;
	}
	
	private List<CodiAssumpte> toCodisAssumpte(List<CodigoAsuntoWs> codisAssumpteWs) throws RegistrePluginException {

		List<CodiAssumpte> codisAssumpte = new ArrayList<>();
		try {
			CodiAssumpte codi;
			for (var codiWs : codisAssumpteWs) {
				codi = new CodiAssumpte();
				codi.setCodi(codiWs.getCodigo());
				codi.setNom(codiWs.getNombre());
				codisAssumpte.add(codi);
			}
		} catch (Exception ex) {
			log.error("Error a l'hora de fer la conversió dels codis d'assumpte", ex);
			throw new RegistrePluginException("Error conversió codis assumpte", ex);
		}
		return codisAssumpte;
	}


    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return salutPluginComponent.teConfiguracioEspecifica();
    }

    @Override
    public EstatSalut getEstatPlugin() {
        return salutPluginComponent.getEstatPlugin();
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        return salutPluginComponent.getPeticionsPlugin();
    }

}
