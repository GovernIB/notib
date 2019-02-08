package es.caib.notib.plugin.registre;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.exception.RegistrePluginException;
import es.caib.notib.core.api.ws.registre.CodiAssumpte;
import es.caib.notib.core.api.ws.registre.DocumentRegistre;
import es.caib.notib.core.api.ws.registre.Llibre;
import es.caib.notib.core.api.ws.registre.Oficina;
import es.caib.notib.core.api.ws.registre.Organisme;
import es.caib.notib.core.api.ws.registre.RegistreAssentament;
import es.caib.notib.core.api.ws.registre.RegistreAssentamentInteressat;
import es.caib.notib.core.api.ws.registre.RegistrePluginRegWeb3;
import es.caib.notib.core.api.ws.registre.RespostaAnotacioRegistre;
import es.caib.notib.core.api.ws.registre.RespostaConsultaRegistre;
import es.caib.notib.core.api.ws.registre.RespostaJustificantRecepcio;
import es.caib.notib.core.api.ws.registre.TipusAssumpte;
import es.caib.regweb3.ws.api.v3.AnexoWs;
import es.caib.regweb3.ws.api.v3.CodigoAsuntoWs;
import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
import es.caib.regweb3.ws.api.v3.IdentificadorWs;
import es.caib.regweb3.ws.api.v3.InteresadoWs;
import es.caib.regweb3.ws.api.v3.JustificanteWs;
import es.caib.regweb3.ws.api.v3.LibroWs;
import es.caib.regweb3.ws.api.v3.OficinaWs;
import es.caib.regweb3.ws.api.v3.OrganismoWs;
import es.caib.regweb3.ws.api.v3.RegWebInfoWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroEntradaWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.RegistroEntradaResponseWs;
import es.caib.regweb3.ws.api.v3.RegistroEntradaWs;
import es.caib.regweb3.ws.api.v3.RegistroSalidaResponseWs;
import es.caib.regweb3.ws.api.v3.RegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.TipoAsuntoWs;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;
import es.caib.regweb3.ws.api.v3.utils.WsClientUtils;


/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegweb3Impl extends RegWeb3Utils implements RegistrePluginRegWeb3{
	
//	protected static RegWebRegistroEntradaWs registroEntradaApi;
//	protected static RegWebRegistroSalidaWs registroSalidaApi;
//	protected static RegWebInfoWs registroInfoApi;

	public String registrarSortida(
			RegistreAssentament registreSortida,
			String aplicacioNom,
			String aplicacioVersio,
			String entitat) throws RegistrePluginException {
		return "OK";
	}
	
	
	
//	public RespostaAnotacioRegistre registrarSortida(
//			RegistreAssentament registreSortida,
//			String aplicacioNom,
//			String aplicacioVersio,
//			String entitat) throws RegistrePluginException {
//		
//		RegWebRegistroSalidaWs registroSalidaApi = null;
//		try {
//			registroSalidaApi = getRegistroSalidaApi();
//		} catch (Exception e) {
//        	logger.error("Error al registrar l'anotació de registre de sortida", e);
//        	throw new RegistrePluginException("Error al registrar l'anotació de registre de sortida", e);
//		}
//		
//		RegistroSalidaWs registroSalidaWs = new RegistroSalidaWs();
//		
//        registroSalidaWs.setOrigen(registreSortida.getOrgan());
//        registroSalidaWs.setOficina(registreSortida.getOficinaCodi());
//        registroSalidaWs.setLibro(registreSortida.getLlibreCodi());
//
//        registroSalidaWs.setExtracto(registreSortida.getExtracte());
//        registroSalidaWs.setDocFisica(registreSortida.getDocumentacioFisicaCodi() != null ? new Long(registreSortida.getDocumentacioFisicaCodi()) : (long)3);
//        registroSalidaWs.setIdioma(registreSortida.getIdiomaCodi());
//        registroSalidaWs.setTipoAsunto(registreSortida.getAssumpteTipusCodi());
//
//        registroSalidaWs.setAplicacion(aplicacioNom);
//        registroSalidaWs.setVersion(aplicacioVersio);
//
//        registroSalidaWs.setCodigoUsuario(registreSortida.getUsuariCodi());
//        registroSalidaWs.setContactoUsuario(registreSortida.getUsuariContacte());
//
//        registroSalidaWs.setNumExpediente(registreSortida.getExpedientNumero());
//
//        registroSalidaWs.setNumTransporte(registreSortida.getTransportNumero());
//        registroSalidaWs.setObservaciones(registreSortida.getObservacions());
//
//        registroSalidaWs.setRefExterna(registreSortida.getReferencia());
//        registroSalidaWs.setCodigoAsunto(registreSortida.getAssumpteCodi());
//        registroSalidaWs.setTipoTransporte(registreSortida.getTransportTipusCodi());
//
//        registroSalidaWs.setExpone(registreSortida.getExposa());
//        registroSalidaWs.setSolicita(registreSortida.getSolicita());
//
//        // Interesados
//        for (RegistreAssentamentInteressat inter: registreSortida.getInteressats()) {
//        	
//        	InteresadoWs interesadoWs = new InteresadoWs();
//
//            DatosInteresadoWs interesado = new DatosInteresadoWs();
//            interesado.setTipoInteresado(Long.valueOf(inter.getTipus().getValor()));
//            interesado.setTipoDocumentoIdentificacion(inter.getDocumentTipus());
//            interesado.setDocumento(inter.getDocumentNum());
//            interesado.setEmail(inter.getEmail());
//            interesado.setNombre(inter.getNom());
//            interesado.setApellido1(inter.getLlinatge1());
//            interesado.setApellido2(inter.getLlinatge2());
//            interesado.setRazonSocial(inter.getRaoSocial());
//            interesado.setPais(inter.getPais() != null ? new Long(inter.getPais()) : null);
//            interesado.setProvincia(inter.getProvincia() != null ? new Long(inter.getProvincia()) : null);
//            interesado.setDireccion(inter.getAdresa());
//            interesado.setCp(inter.getCodiPostal());
//            interesado.setLocalidad(inter.getMunicipi() != null ? new Long(inter.getMunicipi()) : null);
//            interesado.setTelefono(inter.getTelefon());
//            interesadoWs.setInteresado(interesado);
//
//            if (inter.getRepresentant() != null) {
//            	RegistreAssentamentInteressat repre = inter.getRepresentant();
//	            DatosInteresadoWs representante = new DatosInteresadoWs();
//	            representante.setTipoInteresado(Long.valueOf(repre.getTipus().getValor()));
//	            representante.setTipoDocumentoIdentificacion(repre.getDocumentTipus());
//	            representante.setDocumento(repre.getDocumentNum());
//	            representante.setEmail(repre.getEmail());
//	            representante.setNombre(repre.getNom());
//	            representante.setApellido1(repre.getLlinatge1());
//	            representante.setApellido2(repre.getLlinatge2());
//	            representante.setRazonSocial(repre.getRaoSocial());
//	            representante.setPais(repre.getPais() != null ? new Long(repre.getPais()) : null);
//	            representante.setProvincia(repre.getProvincia() != null ? new Long(repre.getProvincia()) : null);
//	            representante.setDireccion(repre.getAdresa());
//	            representante.setCp(repre.getCodiPostal());
//	            representante.setLocalidad(repre.getMunicipi() != null ? new Long(repre.getMunicipi()) : null);
//	            representante.setTelefono(repre.getTelefon());
//	            interesadoWs.setRepresentante(representante);
//            }
//            
//            registroSalidaWs.getInteresados().add(interesadoWs);
//        }
//        
//        for (DocumentRegistre document: registreSortida.getDocuments()) {
//        	AnexoWs anexoWs = new AnexoWs();
//        	
//        	anexoWs.setTitulo(document.getNom());
//            anexoWs.setTipoDocumental("TD01");
//            anexoWs.setTipoDocumento("02");
//            anexoWs.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
//            anexoWs.setObservaciones("");
//            anexoWs.setModoFirma(MODO_FIRMA_ANEXO_SINFIRMA);
//
//            anexoWs.setFicheroAnexado(document.getArxiuContingut());
//            anexoWs.setNombreFicheroAnexado(document.getArxiuNom());
//            anexoWs.setFechaCaptura(new Timestamp(document.getData().getTime()));
//            
//            InputStream is = new BufferedInputStream(new ByteArrayInputStream(document.getArxiuContingut()));
//            try {
//				String mimeType = URLConnection.guessContentTypeFromStream(is);
//				anexoWs.setTipoMIMEFicheroAnexado(mimeType);
//			} catch (IOException e) {
//				throw new RegistrePluginException("Error IOException: " + e);
//			}
//        	
//        	registroSalidaWs.getAnexos().add(anexoWs);
//        }
//
//       
//        RespostaAnotacioRegistre resposta = null;
//        
//        try {
//        	registroSalidaApi = getRegistroSalidaApi();
////            IdentificadorWs identificadorWs = registroSalidaApi.altaRegistroSalida(registroSalidaWs);
//        	IdentificadorWs identificadorWs = registroSalidaApi.nuevoRegistroSalida(entitat, registroSalidaWs);
//            resposta = new RespostaAnotacioRegistre();
//            resposta.setData(identificadorWs.getFecha());
//            resposta.setNumero(identificadorWs.getNumero());
//            resposta.setNumeroRegistroFormateado(identificadorWs.getNumeroRegistroFormateado());
//            resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
//        } catch (WsI18NException e) {
//            String msg = WsClientUtils.toString(e);
//            throw new RegistrePluginException("Error WsI18NException: " + msg);
//        } catch (WsValidationException e) {
//            String msg = WsClientUtils.toString(e);
//            throw new RegistrePluginException("Error WsValidationException: " + msg);
//        } catch (Exception e) {
//        	throw new RegistrePluginException("Error WsValidationException: ", e);
//		}
//        return resposta;
//	}
	
	
	@Override
	public RespostaConsultaRegistre obtenirRegistreSortida(String numRegistre, String usuariCodi, String entitatCodi)
			throws RegistrePluginException {

		RegWebRegistroSalidaWs registroSalidaApi = null;
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		
		try {
			registroSalidaApi = getRegistroSalidaApi();
			
            RegistroSalidaResponseWs registroSalida = registroSalidaApi.obtenerRegistroSalida(numRegistre, usuariCodi, entitatCodi);
            
            resposta.setRegistreNumero(registroSalida.getNumeroRegistroFormateado());
            resposta.setRegistreData(registroSalida.getFechaRegistro());
            resposta.setEntitatCodi(registroSalida.getEntidadCodigo());
            resposta.setEntitatDenominacio(registroSalida.getEntidadDenominacion());
            resposta.setOficinaCodi(registroSalida.getOficinaCodigo());
            resposta.setOficinaDenominacio(registroSalida.getOficinaDenominacion());
            
		} catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsValidationException: " + msg);
        } catch (Exception e) {
        	logger.error("Error al obtenir l'anotació de registre de sortida", e);
			throw new RegistrePluginException("Error al obtenir l'anotació de registre de sortida", e);
		}
		
		return resposta;
	}
	
	@Override
	public void anularRegistreSortida(
			String registreNumero,
			String usuari,
			String entitat,
			boolean anular) throws RegistrePluginException {
		
		RegWebRegistroSalidaWs registroSalidaApi = null;
		
		try {
			registroSalidaApi = getRegistroSalidaApi();
			
			registroSalidaApi.anularRegistroSalida(
					registreNumero, 			// Numero de l'assentament formatejat
					usuari, 					// Codi de l'usuari que vol realitzar l'operació
					entitat, 					// Codi DIR3 de l'entitat al que pertany l'usuari
					anular);					// Indica si s'ha d'anular o no l'assentament.
			
		} catch (Exception e) {
        	logger.error("Error al intentar anular el registre de sortida", e);
			throw new RegistrePluginException("Error al intentar anular el registre de sortida", e);
		}
	}
	
	@Override
	public RespostaAnotacioRegistre registrarEntrada(
			RegistreAssentament registreEntrada,
			String aplicacioNom,
			String aplicacioVersio,
			String entitat) throws RegistrePluginException {
		
		RegWebRegistroEntradaWs registroEntradaApi = null;
				
		try {
			registroEntradaApi = getRegistroEntradaApi();
		} catch (Exception e) {
			logger.error("Error al registrar l'anotació de registre d'entrada", e);
        	throw new RegistrePluginException("Error al registrar l'anotació de registre d'entrada", e);
		}
		
		// 		Camps del registre
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		Camp								| 	Obligatori	|	Descripció
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		Dades de l'ENVIAMENT
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		String desti;  						|	Si			|	Codi DIR3 de l'òrgan destinatari de l'assentament.
		//		String oficina;						|	Si			|	Codi DIR3 de l'entitat registral on es registre l'assentament
		//		String libro;						|	Si			|	Codi del llibre al qual fer l'assentament.
		//		Long docFisica;						|	Si			|	Indica si a l'assentament l'acompanya documentació física o tota la documentació és electrònica. 
		//											|				|	Codi definit a SICRES3:
		//											|				|		'01' = Acompanya documentació física requerida.
		//											|				|		'02' = Acompanya documentació física complementària.
		//											|	Si			|		'03' = No acompanya documentació física ni altres suports.
		//		String idioma;						|				|	Codi de l'idioma en què es fa l'assentament.
		//											|				|	Els codis disponibles són:
		//											|				|		'ca' = Català.
		//											|				|		'es' = Castellà.
		//											|				|		'gl' = Gallec.
		//											|				|		'eu' = Basc.
		//											|				|		'en' = Anglès.
		//		String aplicacion;					|	No			|	Codi de l'aplicació que fa l'assentament
		//		String version;						|	No			|	Versió de l'aplicació que fa l'assentament
		//		String codigoUsuario;				|	Si			|	Nom de l'usuari que fa l'assentament. Si és un registre electrònic serà el nom de l'aplicació
		//		String contactoUsuario;				|	No			|	Contacte de l'usuari d'origen. Si és un registre electrònic serà el responsable del tràmit.
		//		String numExpediente;				|	No			|	Referència al nombre d'expedient de l'assentament
		//	    String numTransporte;				|	No			|	Número del transport d'entrada
		//	    String tipoTransporte;				|	No			|	Forma d'arribada del registre d'entrada. 
		//											|				|	Codi definit a SICRES3:
		//											|				|		'01' = Servei de missatgers
		//											|				|		'02' = Correu postal
		//											|				|		'03' = Correu postal certificat
		//											|				|		'04' = Burofax
		//											|				|		'05' = En ma
		//											|				|		'06' = Fax
		//											|				|		'07' = Altres
		//											|				|		En cas de ser un registre electrònic aquest camp prendrà el valor Altres (07).
		//		String extracto;					|	Si			|	Extracte o resum de l'assentament
		//		String tipoAsunto;					|	Si			|	Codi del tipus assumpte de l'expedient
		//	    String codigoAsunto;				|	No			|	Codi de l'assumpte en destí. Aquest camp contindrà el codi del procediment/tràmit a què pertany l'assentament. Està tipificat a dins REGWEB.
		//	    String refExterna;					|	No			|	Referència externa a qualque element de l'assentament (matrícula de cotxe, rebut,...)
		//	    String observaciones;				|	No			|	Observacions de l'assentament.
		//											|				|
	    //		String expone;						|	No			|	Exposició de fets. Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		//	    String solicita;					|	No			|	Objecte de la sol·licitud. Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		//											|				|
		//		List<InteresadoWs> interesados;		|	Si			|	Dades de l'interessat en l'assentament. Pot haver-n'hi més d'1.
		//		List<AnexoWs> anexos;				|	No			|	Fitxers annexes a l'assentament
		//											|				|		
		//											|				|
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		Dades de la RESPOSTA
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//	    Timestamp fecha;					|				|	Data del assentament
		//	    Integer numero;						|				|	Número de l'assentament
		//	    String numeroRegistroFormateado;	|				|	Número de l'assentament formateat
		
		RegistroEntradaWs registroEntradaWs = new RegistroEntradaWs();

		registroEntradaWs.setDestino(registreEntrada.getOrgan());
		registroEntradaWs.setOficina(registreEntrada.getOficinaCodi());
		registroEntradaWs.setLibro(registreEntrada.getLlibreCodi());
		registroEntradaWs.setDocFisica(registreEntrada.getDocumentacioFisicaCodi() != null ? new Long(registreEntrada.getDocumentacioFisicaCodi()) : (long)3);
		registroEntradaWs.setIdioma(registreEntrada.getIdiomaCodi() == null? "ca" : registreEntrada.getIdiomaCodi().toLowerCase());
		registroEntradaWs.setAplicacion(aplicacioNom);
		registroEntradaWs.setVersion(aplicacioVersio);
		registroEntradaWs.setCodigoUsuario(registreEntrada.getUsuariCodi());
		registroEntradaWs.setContactoUsuario(registreEntrada.getUsuariContacte());
		registroEntradaWs.setNumExpediente(registreEntrada.getExpedientNumero());
		registroEntradaWs.setNumTransporte(registreEntrada.getTransportNumero());
		registroEntradaWs.setTipoTransporte(registreEntrada.getTransportNumero());	// En cas de ser un registre electrònic aquest camp prendrà el valor Altres ("07").
		registroEntradaWs.setExtracto(registreEntrada.getExtracte());
		registroEntradaWs.setTipoAsunto(registreEntrada.getAssumpteTipusCodi());
		registroEntradaWs.setCodigoAsunto(registreEntrada.getAssumpteCodi());
		registroEntradaWs.setRefExterna(registreEntrada.getReferencia());
		registroEntradaWs.setObservaciones(registreEntrada.getObservacions());
		
        registroEntradaWs.setExpone(registreEntrada.getExposa());		// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
        registroEntradaWs.setSolicita(registreEntrada.getSolicita());	// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.

        // Interesados
        for (RegistreAssentamentInteressat inter: registreEntrada.getInteressats()) {
        	InteresadoWs interesadoWs = new InteresadoWs();

            DatosInteresadoWs interesado = new DatosInteresadoWs();
            interesado.setTipoInteresado(Long.valueOf(inter.getTipus().getValor()));
            interesado.setTipoDocumentoIdentificacion(inter.getDocumentTipus());
            interesado.setDocumento(inter.getDocumentNum());
            interesado.setEmail(inter.getEmail());
            interesado.setNombre(inter.getNom());
            interesado.setApellido1(inter.getLlinatge1());
            interesado.setApellido2(inter.getLlinatge2());
            interesado.setRazonSocial(inter.getRaoSocial());
            interesado.setPais(inter.getPais() != null ? new Long(inter.getPais()) : null);
            interesado.setProvincia(inter.getProvincia() != null ? new Long(inter.getProvincia()) : null);
            interesado.setDireccion(inter.getAdresa());
            interesado.setCp(inter.getCodiPostal());
            interesado.setLocalidad(inter.getMunicipi() != null ? new Long(inter.getMunicipi()) : null);
            interesado.setTelefono(inter.getTelefon());
            interesadoWs.setInteresado(interesado);

            if (inter.getRepresentant() != null) {
            	RegistreAssentamentInteressat repre = inter.getRepresentant();
	            DatosInteresadoWs representante = new DatosInteresadoWs();
	            representante.setTipoInteresado(Long.valueOf(repre.getTipus().getValor()));
	            representante.setTipoDocumentoIdentificacion(repre.getDocumentTipus());
	            representante.setDocumento(repre.getDocumentNum());
	            representante.setEmail(repre.getEmail());
	            representante.setNombre(repre.getNom());
	            representante.setApellido1(repre.getLlinatge1());
	            representante.setApellido2(repre.getLlinatge2());
	            representante.setPais(repre.getPais() != null ? new Long(repre.getPais()) : null);
	            representante.setProvincia(repre.getProvincia() != null ? new Long(repre.getProvincia()) : null);
	            representante.setDireccion(repre.getAdresa());
	            representante.setCp(repre.getCodiPostal());
	            representante.setLocalidad(repre.getMunicipi() != null ? new Long(repre.getMunicipi()) : null);
	            representante.setTelefono(repre.getTelefon());
	            interesadoWs.setRepresentante(representante);
            }
            
            registroEntradaWs.getInteresados().add(interesadoWs);
        }
        
        for (DocumentRegistre document: registreEntrada.getDocuments()) {
        	AnexoWs anexoWs = new AnexoWs();
        	
        	anexoWs.setTitulo(document.getNom());
            anexoWs.setTipoDocumental(document.getTipusDocumental());
            anexoWs.setTipoDocumento(document.getTipusDocument());
            anexoWs.setOrigenCiudadanoAdmin(document.getOrigen());
            anexoWs.setObservaciones(document.getObservacions());
            anexoWs.setModoFirma(document.getModeFirma());

            anexoWs.setFicheroAnexado(document.getArxiuContingut());
            anexoWs.setNombreFicheroAnexado(document.getArxiuNom());
            anexoWs.setFechaCaptura(new Timestamp(document.getData().getTime()));
            
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(document.getArxiuContingut()));
            try {
				String mimeType = URLConnection.guessContentTypeFromStream(is);
				anexoWs.setTipoMIMEFicheroAnexado(mimeType);
			} catch (IOException e) {
				throw new RegistrePluginException("Error IOException: " + e);
			}
        	
            registroEntradaWs.getAnexos().add(anexoWs);
        }

       
        RespostaAnotacioRegistre resposta = null;
        try {
//            IdentificadorWs identificadorWs = registroEntradaApi.altaRegistroEntrada(registroEntradaWs);
        	IdentificadorWs identificadorWs = registroEntradaApi.nuevoRegistroEntrada(entitat, registroEntradaWs);
            resposta = new RespostaAnotacioRegistre();
            resposta.setData(identificadorWs.getFecha());
            resposta.setNumero(identificadorWs.getNumero());
            resposta.setNumeroRegistroFormateado(identificadorWs.getNumeroRegistroFormateado());
            resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
        } catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsValidationException: " + msg);
        } catch (Exception e) {
        	throw new RegistrePluginException("Error WsValidationException: ", e);
		}
        return resposta;
	}

	@Override
	public RespostaConsultaRegistre obtenirRegistreEntrada(
			String numRegistre,
			String usuariCodi,
			String entitatCodi) throws RegistrePluginException {
		
		RegWebRegistroEntradaWs registroEntradaApi = null;
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		
		try {
			registroEntradaApi = getRegistroEntradaApi();
			
			
			RegistroEntradaResponseWs registroEntrada = registroEntradaApi.obtenerRegistroEntrada(numRegistre, usuariCodi, entitatCodi);
            
            resposta.setRegistreNumero(registroEntrada.getNumeroRegistroFormateado());
            resposta.setRegistreData(registroEntrada.getFechaRegistro());
            resposta.setEntitatCodi(registroEntrada.getEntidadCodigo());
            resposta.setEntitatDenominacio(registroEntrada.getEntidadDenominacion());
            resposta.setOficinaCodi(registroEntrada.getOficinaCodigo());
            resposta.setOficinaDenominacio(registroEntrada.getOficinaDenominacion());
            
		} catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsValidationException: " + msg);
		} catch (Exception e) {
        	logger.error("Error al obtenir l'anotació de registre d'entrada", e);
			throw new RegistrePluginException("Error al obtenir l'anotació de registre d'entrada", e);
		}
		
		return resposta;
	}
	
//	@Override
//	private RespostaAnotacioRegistre obtenirRegistreEntradaId(
//			Integer anyRegistre,
//			Integer numRegistre,
//			String llibre,
//			String usuariCodi,
//			String entitatCodi) throws RegistrePluginException {
//		
//		RegWebRegistroEntradaWs registroEntradaApi = null;
//		RespostaAnotacioRegistre resposta = null;
//		
//		try {
//			registroEntradaApi = getRegistroEntradaApi();
//			
//			
//			IdentificadorWs registroEntrada = registroEntradaApi.obtenerRegistroEntradaID(anyRegistre, numRegistre, llibre, usuariCodi, entitatCodi);
//            
//			resposta.setNumero(registroEntrada.getNumero());
//			resposta.setData(registroEntrada.getFecha());
//			resposta.setNumeroRegistroFormateado(registroEntrada.getNumeroRegistroFormateado());
//            
//            
//		} catch (WsI18NException e) {
//            String msg = WsClientUtils.toString(e);
//            throw new RegistrePluginException("Error WsI18NException: " + msg);
//        } catch (WsValidationException e) {
//            String msg = WsClientUtils.toString(e);
//            throw new RegistrePluginException("Error WsValidationException: " + msg);
//		} catch (Exception e) {
//        	logger.error("Error al obtenir l'Id de l'anotació de registre d'entrada", e);
//			throw new RegistrePluginException("Error al obtenir l'Id de l'anotació de registre d'entrada", e);
//		}
//		
//		return resposta;
//	}
	
	@Override
	public RespostaJustificantRecepcio obtenirJustificantEntrada(
			Integer anyRegistre,
			Integer numRegistre,
			String llibre,
			String usuariCodi,
			String entitatCodi) throws RegistrePluginException {
		
		RegWebRegistroEntradaWs registroEntradaApi = null;
		RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
		
		try {
			registroEntradaApi = getRegistroEntradaApi();
			
			IdentificadorWs registroEntrada = registroEntradaApi.obtenerRegistroEntradaID(anyRegistre, numRegistre, llibre, usuariCodi, entitatCodi);
			JustificanteWs justificant = registroEntradaApi.obtenerJustificante(entitatCodi, registroEntrada.getNumeroRegistroFormateado());
			
			resposta.setJustificant(justificant.getJustificante());
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			
		} catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsValidationException: " + msg);
		} catch (Exception e) {
        	logger.error("Error al obtenir l'anotació de registre d'entrada", e);
			throw new RegistrePluginException("Error al obtenir l'anotació de registre d'entrada", e);
		}
		
		return resposta;
		
	}
	
	@Override
	public void anularRegistreEntrada(
			String registreNumero,
			String usuari,
			String entitat,
			boolean anular) throws RegistrePluginException {
		
		RegWebRegistroEntradaWs registroEntradaApi = null;
		
		try {
			registroEntradaApi = getRegistroEntradaApi();
			
			registroEntradaApi.anularRegistroEntrada(
					registreNumero, 			// Numero de l'assentament formatejat
					usuari, 					// Codi de l'usuari que vol realitzar l'operació
					entitat, 					// Codi DIR3 de l'entitat al que pertany l'usuari
					anular);					// Indica si s'ha d'anular o no l'assentament.
			
		} catch (Exception e) {
        	logger.error("Error al intentar anular el registre d'entrada", e);
			throw new RegistrePluginException("Error al intentar anular el registre d'entrada", e);
		}
	}
	
	
	@Override
	public List<TipusAssumpte> llistarTipusAssumpte(
			String entitat) throws RegistrePluginException {
		
		RegWebInfoWs registroInfoApi = null;
		List<TipusAssumpte> tipusAssumptes = new ArrayList<TipusAssumpte>();
		
		try {
			registroInfoApi = getInfoApi();
			
			List<TipoAsuntoWs> tiposAsunto = registroInfoApi.listarTipoAsunto(
					entitat);					// Codi DIR3 de l'òrgan arrel de l'entitat a consultar
			for (TipoAsuntoWs tipoAsunto : tiposAsunto) {
				TipusAssumpte tipusAssumpte = new TipusAssumpte();
				tipusAssumpte.setCodi(tipoAsunto.getCodigo());
				tipusAssumpte.setNom(tipoAsunto.getNombre());
				tipusAssumptes.add(tipusAssumpte);
			}
			
		} catch (Exception e) {
        	logger.error("Error al intentar llistar els tipus d'assumpte", e);
			throw new RegistrePluginException("Error al intentar llistar els tipus d'assumpte", e);
		}
		
		return tipusAssumptes;
	}
	
	@Override
	public List<CodiAssumpte> llistarCodisAssumpte(
			String entitat,
			String tipusAssumpte) throws RegistrePluginException {
		
		RegWebInfoWs registroInfoApi = null;
		List<CodiAssumpte> codiAssumptes = new ArrayList<CodiAssumpte>();
		
		try {
			registroInfoApi = getInfoApi();
			
			List<CodigoAsuntoWs> codigosAsunto = registroInfoApi.listarCodigoAsunto(
					entitat,					// Codi de l'usuari que vol realitzar l'operació
					tipusAssumpte);				// Codi SICRES del tipus d'assumpte.
			for (CodigoAsuntoWs codigoAsunto : codigosAsunto) {
				CodiAssumpte codiAssumpte = new CodiAssumpte();
				codiAssumpte.setCodi(codigoAsunto.getCodigo());
				codiAssumpte.setNom(codigoAsunto.getNombre());
				codiAssumpte.setTipusAssumpte(codigoAsunto.getTipoAsunto().getCodigo());
				codiAssumptes.add(codiAssumpte);
			}
			
		} catch (Exception e) {
        	logger.error("Error al intentar llistar els codis d'assumpte", e);
			throw new RegistrePluginException("Error al intentar llistar els codis d'assumpte", e);
		}
		
		return codiAssumptes;
	}
	
	@Override
	public List<Oficina> llistarOficines(
			String entitat,
			Long autoritzacio) throws RegistrePluginException {
		
		RegWebInfoWs registroInfoApi = null;
		List<Oficina> oficines = new ArrayList<Oficina>();
		
		try {
			registroInfoApi = getInfoApi();
			
			List<OficinaWs> oficinas = registroInfoApi.listarOficinas(
					entitat,					// Codi DIR3 de l'òrgan arrel de l'entitat a consultar
					autoritzacio);				// Tipo de permís damunt els llibres que vol consultar. 
												// 		1 = Registro entrada 
												//		2 = Registro salida
												// 		3 = Consulta Registro entrada.
												//		4 = Consulta Registro salida.
			for (OficinaWs ofic : oficinas) {
				Oficina oficina = new Oficina();
				oficina.setCodi(ofic.getCodigo());
				oficina.setNom(ofic.getNombre());
				oficines.add(oficina);
			}
			
		} catch (Exception e) {
        	logger.error("Error al intentar llistar les oficines", e);
			throw new RegistrePluginException("Error al intentar llistar les oficines", e);
		}
		
		return oficines;
	}
	
	@Override
	public List<Llibre> llistarLlibres(
			String entitat,
			String oficina,
			Long autoritzacio) throws RegistrePluginException {
		
		RegWebInfoWs registroInfoApi = null;
		List<Llibre> llibres = new ArrayList<Llibre>();
	
		try {
			registroInfoApi = getInfoApi();
			
			List<LibroWs> libros = registroInfoApi.listarLibros(
					entitat,					// Codi DIR3 de l'òrgan arrel de l'entitat a consultar
					oficina,					// Codi DIR3 de l'oficina a consultar
					autoritzacio);				// Tipo de permís damunt els llibres que vol consultar. 
												// 		1 = Registro entrada 
												//		2 = Registro salida
												// 		3 = Consulta Registro entrada.
												//		4 = Consulta Registro salida.
			for (LibroWs libro : libros) {
				Llibre llibre = new Llibre();
				llibre.setCodi(libro.getCodigoLibro());
				llibre.setOrganisme(libro.getCodigoOrganismo());
				llibre.setNomCurt(libro.getNombreCorto());
				llibre.setNomLlarg(libro.getNombreLargo());
				llibres.add(llibre);
			}
			
		} catch (Exception e) {
        	logger.error("Error al intentar llistar els llibres", e);
			throw new RegistrePluginException("Error al intentar llistar els llibres", e);
		}
		
		return llibres;
	}
	
	@Override
	public List<Organisme> llistarOrganismes(
			String entitat) throws RegistrePluginException {
		
		RegWebInfoWs registroInfoApi = null;
		List<Organisme> organismes = new ArrayList<Organisme>(); 
		
		try {
			registroInfoApi = getInfoApi();
			
			List<OrganismoWs> organismos = registroInfoApi.listarOrganismos(
					entitat);					// Codi DIR3 de l'òrgan arrel de l'entitat a consultar
			
			for (OrganismoWs organismo : organismos) {
				Organisme organisme = new Organisme();
				organisme.setCodi(organismo.getCodigo());
				organisme.setNom(organismo.getNombre());
				organismes.add(organisme);
			}
			
		} catch (Exception e) {
        	logger.error("Error al intentar llistar els organismes", e);
			throw new RegistrePluginException("Error al intentar llistar els organismes", e);
		}
		
		return organismes;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegistrePluginRegweb3Impl.class);
}
