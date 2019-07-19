package es.caib.notib.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.caib.notib.core.api.dto.AnexoWsDto;
import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.DatosInteresadoWsDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.InteresadoWsDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.RegistreAnnexDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.dto.RegistreInteressatDocumentTipusDtoEnum;
import es.caib.notib.core.api.dto.RegistreInteressatDto;
import es.caib.notib.core.api.dto.RegistreModeFirmaDtoEnum;
import es.caib.notib.core.api.dto.RegistreOrigenDtoEnum;
import es.caib.notib.core.api.dto.RegistreTipusDocumentDtoEnum;
import es.caib.notib.core.api.dto.RegistreTipusDocumentalDtoEnum;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.DadesAnotacio;
import es.caib.notib.plugin.registre.DadesInteressat;
import es.caib.notib.plugin.registre.DadesOficina;
import es.caib.notib.plugin.registre.DadesRepresentat;
import es.caib.notib.plugin.registre.DocumentRegistre;
import es.caib.notib.plugin.registre.Llibre;
import es.caib.notib.plugin.registre.LlibreOficina;
import es.caib.notib.plugin.registre.Oficina;
import es.caib.notib.plugin.registre.Organisme;
import es.caib.notib.plugin.registre.RegistreModeFirmaEnum;
import es.caib.notib.plugin.registre.RegistreOrigenEnum;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RegistreSortida;
import es.caib.notib.plugin.registre.RegistreTipusDocumentEnum;
import es.caib.notib.plugin.registre.RegistreTipusDocumentalEnum;
import es.caib.notib.plugin.registre.RespostaAnotacioRegistre;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.registre.TipusAssumpte;
import es.caib.notib.plugin.registre.TipusRegistreRegweb3Enum;
import es.caib.notib.plugin.seu.SeuPlugin;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";

	private DadesUsuariPlugin dadesUsuariPlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private SeuPlugin seuPlugin;
	private RegistrePlugin registrePlugin;
	private IArxiuPlugin arxiuPlugin;
	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;

	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private AplicacioService aplicacioService;
	
	public RespostaConsultaRegistre registreSortidaAsientoRegistral(
			String codiDir3Entitat, 
			NotificacioEntity notificacio, 
			NotificacioEnviamentEntity enviament, 
			Long tipusOperacio) {
		return getRegistrePlugin().salidaAsientoRegistral(
				codiDir3Entitat, 
				notificacioToAsientoRegistralBean(
						notificacio, 
						enviament), 
				tipusOperacio);
	}
	
	public RespostaConsultaRegistre crearAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio) {
		return getRegistrePlugin().salidaAsientoRegistral(
				codiDir3Entitat, 
				arb, 
				tipusOperacio);
	}
	
	public RespostaJustificantRecepcio obtenirJustificant(String codiDir3Entitat, String numeroRegistreFormatat) {
		return getRegistrePlugin().obtenerJustificante(codiDir3Entitat, numeroRegistreFormatat, 2);
	}
	
	public RespostaJustificantRecepcio obtenirOficiExtern(String codiDir3Entitat, String numeroRegistreFormatat) {
		return getRegistrePlugin().obtenerOficioExterno(codiDir3Entitat, numeroRegistreFormatat);
	}

	public RegistreIdDto registreAnotacioSortida(
			NotificacioDtoV2 notificacio, 
			List<NotificacioEnviamentDtoV2> enviaments, 
			Long tipusOperacio) throws Exception {
		RegistreIdDto rs = new RegistreIdDto();
		String accioDescripcio = "Enviament notificació a registre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("notificacioID", String.valueOf(notificacio.getId()));
		long t0 = System.currentTimeMillis();
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarSalida(
					toRegistreSortida(
							notificacio,
							enviaments),
					"notib");
			if (resposta.getErrorDescripcio() != null) {
				rs.setDescripcioError(resposta.getErrorDescripcio());
			} else {
				rs.setNumeroRegistreFormat(resposta.getNumeroRegistroFormateado());
				rs.setData(resposta.getData());
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_REGISTRE,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			if (ex.getCause() != null) {
				rs.setDescripcioError(errorDescripcio + " :" + ex.getCause().getMessage());
				return rs;
			} else {
				throw new SistemaExternException(
				IntegracioHelper.INTCODI_USUARIS,
				errorDescripcio,
				ex);
			}

		}
		return rs;
	}
	
	public List<String> consultarRolsAmbCodi(
			String usuariCodi) {
		String accioDescripcio = "Consulta rols usuari amb codi";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<String> rols = getDadesUsuariPlugin().consultarRolsAmbCodi(
					usuariCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return rols;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}
	
	public Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean isUuid) {
		if(isUuid) {
			arxiuUuid = "uuid:" + arxiuUuid;
		} else {
			arxiuUuid = "csv:" + arxiuUuid;
		}
		
		String accioDescripcio = "Consulta d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("arxiuUuid", arxiuUuid);
		long t0 = System.currentTimeMillis();
		try {
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					false);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_CUSTODIA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return documentDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_CUSTODIA,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_CUSTODIA,
					errorDescripcio,
					ex);
		}
	}
	
	
	public DadesUsuari dadesUsuariConsultarAmbCodi(
			String usuariCodi) {
		String accioDescripcio = "Consulta d'usuari amb codi";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			DadesUsuari dadesUsuari = getDadesUsuariPlugin().consultarAmbCodi(
					usuariCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}
	public List<DadesUsuari> dadesUsuariConsultarAmbGrup(
			String grupCodi) {
		String accioDescripcio = "Consulta d'usuaris d'un grup";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().consultarAmbGrup(
					grupCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}

	public String gestioDocumentalCreate(
			String agrupacio,
			InputStream contingut) {
		String accioDescripcio = "Creació d'un arxiu";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("agrupacio", agrupacio);
		accioParams.put("contingut", (contingut != null) ? contingut.toString() : "<null>");
		long t0 = System.currentTimeMillis();
		try {
			String gestioDocumentalId = getGestioDocumentalPlugin().create(
					agrupacio,
						contingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	public void gestioDocumentalUpdate(
			String id,
			String agrupacio,
			InputStream contingut) {
		String accioDescripcio = "Modificació d'un arxiu";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		accioParams.put("contingut", (contingut != null) ? contingut.toString() : "<null>");
		long t0 = System.currentTimeMillis();
		try {
			getGestioDocumentalPlugin().update(
					id,
					agrupacio,
					contingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		String accioDescripcio = "Eliminació d'un arxiu";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		long t0 = System.currentTimeMillis();
		try {
			getGestioDocumentalPlugin().delete(
					id,
					agrupacio);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut) {
		String accioDescripcio = "Consultant arxiu de la gestió documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		long t0 = System.currentTimeMillis();
		try {
			getGestioDocumentalPlugin().get(
					id,
					agrupacio,
					contingutOut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	public List<TipusAssumpte> llistarTipusAssumpte(String entitatCodi) throws RegistrePluginException {
		
		List<TipusAssumpte> tipusAssumptes = getRegistrePlugin().llistarTipusAssumpte(entitatCodi);
		
		return tipusAssumptes;
	}

	public List<CodiAssumpte> llistarCodisAssumpte(
			String entitatcodi,
			String tipusAssumpte) throws RegistrePluginException {
		
		List<CodiAssumpte> assumptes = getRegistrePlugin().llistarCodisAssumpte(
				entitatcodi, 
				tipusAssumpte);

		return assumptes;
	}
	
	public Oficina llistarOficinaVirtual(
			String entitatcodi,
			TipusRegistreRegweb3Enum autoritzacio) throws RegistrePluginException {
		
		Oficina oficina = getRegistrePlugin().llistarOficinaVirtual(
				entitatcodi, 
				autoritzacio.getValor());
	
		return oficina;
	}
	
	public List<Oficina> llistarOficines(
			String entitatcodi,
			AutoritzacioRegiWeb3Enum autoritzacio) throws RegistrePluginException {
		
		List<Oficina> oficines = getRegistrePlugin().llistarOficines(
				entitatcodi, 
				autoritzacio.getValor());
	
		return oficines;
	}
	
	public List<LlibreOficina> llistarLlibresOficines(
			String entitatCodi,
			String usuariCodi,
			TipusRegistreRegweb3Enum tipusRegistre){
		
		List<LlibreOficina> llibresOficines = getRegistrePlugin().llistarLlibresOficines(
				entitatCodi, 
				usuariCodi,
				tipusRegistre.getValor());
	
		return llibresOficines;
	}
	
	private Llibre llistarLlibreOrganisme(
			String entitatCodi,
			String organismeCodi) throws RegistrePluginException{
		
		return getRegistrePlugin().llistarLlibreOrganisme(
				entitatCodi, 
				organismeCodi);
	}
	
	public List<Llibre> llistarLlibres(
			String entitatcodi,
			String oficina,
			AutoritzacioRegiWeb3Enum autoritzacio) throws RegistrePluginException {
		
		List<Llibre> llibres = getRegistrePlugin().llistarLlibres(
				entitatcodi, 
				oficina, 
				autoritzacio.getValor());
	
		return llibres;
	}
	
	public List<Organisme> llistarOrganismes(String entitatcodi) throws RegistrePluginException {
		
		List<Organisme> organismes = getRegistrePlugin().llistarOrganismes(entitatcodi);
	
		return organismes;
	}
	
	public List<CodiValor> llistarProvincies() throws SistemaExternException, es.caib.notib.plugin.SistemaExternException {
		return getUnitatsOrganitzativesPlugin().provincies();
	}
	
	public List<CodiValor> llistarLocalitats(String codiProvincia) throws es.caib.notib.plugin.SistemaExternException {
		return getUnitatsOrganitzativesPlugin().localitats(codiProvincia);
	}
	
	public List<CodiValorPais> llistarPaisos() throws es.caib.notib.plugin.SistemaExternException {
		return getUnitatsOrganitzativesPlugin().paisos();
	}
	
	private DocumentRegistre documentToDocumentRegistreDto (DocumentDto documentDto) throws SistemaExternException {
		DocumentRegistre document = new DocumentRegistre();
		
		if(((documentDto.getUuid() != null && !documentDto.getUuid().isEmpty())
			|| (documentDto.getCsv() != null && !documentDto.getCsv().isEmpty()))
			&& (documentDto.getUrl() == null || documentDto.getUrl().isEmpty())
			&& (documentDto.getContingutBase64() == null || documentDto.getContingutBase64().isEmpty())) {
			String id = "";
			boolean uuid = false;
			if(documentDto.getUuid() != null) {
				id = documentDto.getUuid();
				document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
				uuid = true;
			} else if (documentDto.getCsv() != null) {
				id = documentDto.getCsv();
				document.setModeFirma(RegistreModeFirmaEnum.AUTOFIRMA_SI.getValor());
				uuid = false;
			}
			try {
				Document doc = arxiuDocumentConsultar(id, null, uuid);
					
//				document.setArxiuNom(doc.getNom());
//				document.setArxiuContingut(doc.getContingut().getContingut());
//				document.setIdiomaCodi("ca");
//				document.setData(new Date());
//				document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
//				document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.name());
//				document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.name());
				document.setArxiuNom(doc.getNom());
				document.setArxiuContingut(doc.getContingut().getContingut());
				document.setIdiomaCodi("ca");
				document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.name());
				if (doc.getMetadades() != null) {
					document.setData(doc.getMetadades().getDataCaptura());
					document.setOrigen(doc.getMetadades().getOrigen().ordinal());
					document.setTipusDocumental(doc.getMetadades().getTipusDocumental().name());
				}
			} catch(ArxiuException ae) {
				logger.error("Error Obtenint el document uuid/csv: " + id);
			}
		} else if((documentDto.getUrl() != null && !documentDto.getUrl().isEmpty()) 
				&& (documentDto.getUuid() == null || documentDto.getUuid().isEmpty()) 
				&& (documentDto.getCsv() == null || documentDto.getCsv().isEmpty()) 
				&& (documentDto.getContingutBase64() == null || documentDto.getContingutBase64().isEmpty())) {
			document.setNom(documentDto.getUrl());
			document.setArxiuNom(documentDto.getUrl());
			document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.name());
			document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.name());
			document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
			document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
			document.setData(new Date());
			document.setIdiomaCodi("ca");
		} else if((documentDto.getArxiuGestdocId() != null && !documentDto.getArxiuGestdocId().isEmpty()) 
				&& (documentDto.getUrl() == null || documentDto.getUrl().isEmpty())
				&& (documentDto.getUuid() == null || documentDto.getUuid().isEmpty()) 
				&& (documentDto.getCsv() == null || documentDto.getCsv().isEmpty())) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gestioDocumentalGet(
					documentDto.getArxiuGestdocId(), 
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					baos);
			document.setArxiuContingut(baos.toByteArray());
			document.setArxiuNom(documentDto.getArxiuNom());
			document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.name());
			document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.name());
			document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
			document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
			document.setData(new Date());
			document.setIdiomaCodi("ca");
		}
		return document;
	}

	public RegistreAnnexDto documentToRegistreAnnexDto (DocumentEntity document) {
		RegistreAnnexDto annex = new RegistreAnnexDto();
		if((document.getUuid() != null || document.getCsv() != null) && document.getUrl() == null && document.getContingutBase64() == null) {
			String id = "";
			if(document.getUuid() != null) {
				id = document.getUuid();
				try {
					annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
					annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
					annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
					annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);
					annex.setData(new Date());
					annex.setIdiomaCodi("ca");
					DocumentContingut doc = arxiuGetImprimible(id, true);
					annex.setArxiuContingut(doc.getContingut());
					annex.setArxiuNom(doc.getArxiuNom());
				}catch(ArxiuException ae) {
					logger.error("Error Obtenint el document per l'uuid");
				}
			} else if (document.getCsv() != null){
				id = document.getCsv();
				try {
					annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
					annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
					annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
					annex.setModeFirma(RegistreModeFirmaDtoEnum.AUTOFIRMA_SI);
					annex.setData(new Date());
					annex.setIdiomaCodi("ca");
					DocumentContingut doc = arxiuGetImprimible(id, false);
					annex.setArxiuContingut(doc.getContingut());
					annex.setArxiuNom(doc.getArxiuNom());
				}catch(ArxiuException ae) {
					logger.error("Error Obtenint el document per csv");
				}
			}
		} else if(document.getUrl() != null && (document.getUuid() == null && document.getCsv() == null) && document.getContingutBase64() == null) {
			annex.setNom(document.getUrl());
			annex.setArxiuNom(document.getUrl());
			annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
			annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
			annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
			annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);
			annex.setData(new Date());
			annex.setIdiomaCodi("ca");
		} else if(document.getContingutBase64() != null && document.getUrl() == null && (document.getUuid() == null && document.getCsv() == null)) {
			annex.setArxiuContingut(document.getContingutBase64().getBytes());
			annex.setArxiuNom(document.getArxiuNom());
			annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
			annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
			annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
			annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);
			annex.setData(new Date());
			annex.setIdiomaCodi("ca");
		}
		/*Llogica de recerca de document*/
		return annex;
	}
	
	private AnexoWsDto documentToAnexoWs(DocumentEntity document) {
		try {
			AnexoWsDto annex = null;
			Path path = null;
			Map<String, Object> metadades = new HashMap<String, Object>();
			
			if((document.getUuid() != null || document.getCsv() != null) && document.getUrl() == null && document.getContingutBase64() == null) {
				annex = new AnexoWsDto();
				String id = "";
				if(document.getUuid() != null) {
					id = document.getUuid();
					try {
						DocumentContingut doc = arxiuGetImprimible(id, true);
						annex.setFicheroAnexado(doc.getContingut());
						annex.setNombreFicheroAnexado(doc.getArxiuNom());
					}catch(ArxiuException ae) {
						logger.error("Error Obtenint el document per l'uuid");
					}
				} else if (document.getCsv() != null){
					id = document.getCsv();
					try {
						DocumentContingut doc = arxiuGetImprimible(id, false);
						annex.setFicheroAnexado(doc.getContingut());
						annex.setNombreFicheroAnexado(doc.getArxiuNom());
					}catch(ArxiuException ae) {
						logger.error("Error Obtenint el document per el csv");
					}
				}
				path = new File(document.getArxiuNom()).toPath(); 
			}else if(document.getUrl() != null && (document.getUuid() == null && document.getCsv() == null) && document.getContingutBase64() == null) {
				annex = new AnexoWsDto();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream is = null;
				try {
					URL url = new URL(document.getUrl());
	
					is = url.openStream();
					byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
					int n;
	
					while ((n = is.read(byteChunk)) > 0) {
						baos.write(byteChunk, 0, n);
					}
				} catch (IOException e) {
					System.err.printf("Failed while reading bytes from %s: %s", document.getUrl(), e.getMessage());
					e.printStackTrace();
					// Perform any other exception handling that's appropriate.
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				annex.setFicheroAnexado(baos.toByteArray());
				annex.setNombreFicheroAnexado(FilenameUtils.getName(document.getUrl()));
				path = new File(FilenameUtils.getName(document.getUrl())).toPath();
			}else if(document.getArxiuGestdocId() != null && document.getUrl() == null && (document.getUuid() == null && document.getCsv() == null)) {
				annex = new AnexoWsDto();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				gestioDocumentalGet(
						document.getArxiuGestdocId(),
						GESDOC_AGRUPACIO_NOTIFICACIONS,
						output);
				annex.setFicheroAnexado(output.toByteArray());
				annex.setNombreFicheroAnexado(document.getArxiuNom());
				path = new File(document.getArxiuNom()).toPath();
			}
			try {
				annex.setTipoMIMEFicheroAnexado(Files.probeContentType(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			annex.setTipoDocumental((String)metadades.get("tipoDocumental"));
			annex.setValidezDocumento((String)metadades.get("validezDocumento"));
			annex.setTipoDocumento((String)metadades.get("tipoDocumento"));
			annex.setObservaciones((String)metadades.get("observaciones"));
			String origenCiudadanoAdmin = (String)metadades.get("origenCiudadanoAdmin");
			if(origenCiudadanoAdmin != null) {
				annex.setOrigenCiudadanoAdmin(Integer.parseInt(origenCiudadanoAdmin));	
			}
			annex.setFechaCaptura((XMLGregorianCalendar)metadades.get("fechaCaptura"));
			annex.setCsv((String)metadades.get("csv"));
			annex.setTitulo("Annex 1");
			annex.setModoFirma(0);
			/*Llogica de recerca de document*/
			return annex;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
//	private static Map<String, Object> convertNodesFromXml(String xml) throws Exception {
//	    InputStream is = new ByteArrayInputStream(xml.getBytes());
//	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//	    dbf.setNamespaceAware(true);
//	    DocumentBuilder db = dbf.newDocumentBuilder();
//	    org.w3c.dom.Document document = db.parse(is);
//	    return createMap(document.getDocumentElement());
//	}
//	
//	
//	private static Map<String, Object> createMap(Node node) {
//	    Map<String, Object> map = new HashMap<String, Object>();
//	    NodeList nodeList = node.getChildNodes();
//	    for (int i = 0; i < nodeList.getLength(); i++) {
//	        Node currentNode = nodeList.item(i);
//	        if (currentNode.hasAttributes()) {
//	            for (int j = 0; j < currentNode.getAttributes().getLength(); j++) {
//	                Node item = currentNode.getAttributes().item(i);
//	                map.put(item.getNodeName(), item.getTextContent());
//	            }
//	        }
//	        if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
//	            map.putAll(createMap(currentNode));
//	        } else if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
//	            map.put(node.getLocalName(), node.getTextContent());
//	        }
//	    }
//	    return map;
//	}
	
	public DocumentContingut arxiuGetImprimible(
			String id,
			boolean uuidCsv) {
		if(uuidCsv) {
			id = "uuid:" + id;
		} else {
			id = "csv:" + id;
		}
		return getArxiuPlugin().documentImprimible(id);	
	}
	
	private RegistreSortida toRegistreSortida(
			NotificacioDtoV2 notificacio,
			List<NotificacioEnviamentDtoV2> enviaments) throws RegistrePluginException {
		RegistreSortida registreSortida = new RegistreSortida();
		DadesOficina dadesOficina = new DadesOficina();
		Llibre llibreOrganisme = null;
		Oficina oficinaVirtual = null;
		
		if (notificacio.getProcediment().getOrganGestor() != null) {
			llibreOrganisme = llistarLlibreOrganisme(
					notificacio.getEmisorDir3Codi(),
					notificacio.getProcediment().getOrganGestor());
		}
		
		if (notificacio.getProcediment().getOficina() != null) {
			dadesOficina.setOficina(notificacio.getProcediment().getOficina());
		} 
//		else {
//			//oficina virtual
//			oficinaVirtual = llistarOficinaVirtual(
//					notificacio.getEmisorDir3Codi(), 
//					TipusRegistreRegweb3Enum.REGISTRE_SORTIDA);
//			
//			if (oficinaVirtual != null) {
//				dadesOficina.setOficina(oficinaVirtual.getCodi());
//			}
//		}
		
		if (notificacio.getProcediment().getLlibre() != null) {
			dadesOficina.setLlibre(notificacio.getProcediment().getLlibre());
		} else if (llibreOrganisme != null) {
			String llibreCodi = llibreOrganisme.getCodi();
			dadesOficina.setLlibre(llibreCodi);
		}

		registreSortida.setCodiEntitat(notificacio.getEmisorDir3Codi());
		dadesOficina.setOrgan(notificacio.getProcediment().getOrganGestor());
		registreSortida.setDadesOficina(dadesOficina);
		
		
		for(NotificacioEnviamentDtoV2 enviament : enviaments) {
			PersonaDto destinatari = null;
			if(enviament.getDestinataris() != null && enviament.getDestinataris().size() > 0) {
				destinatari =  enviament.getDestinataris().get(0);
			}
			registreSortida.getDadesInteressat().add(personaToDadesInteressat(
					notificacio, 
					enviament.getTitular()));	
			registreSortida.setDadesRepresentat(personaToDadesRepresentat(
					notificacio, 
					enviament.getTitular(),
					destinatari));	
		}
		
		DadesAnotacio dadesAnotacio = new DadesAnotacio();
		dadesAnotacio.setIdiomaCodi("ca");
		
		List<TipusAssumpte> tipusAssumpte = llistarTipusAssumpte(notificacio.getEmisorDir3Codi());
		
		if(notificacio.getProcediment().getTipusAssumpte() != null) {
			dadesAnotacio.setTipusAssumpte(notificacio.getProcediment().getTipusAssumpte());
		} else if (tipusAssumpte != null && ! tipusAssumpte.isEmpty()) {
			String tipusAssumpteCodi = tipusAssumpte.get(0).getCodi();
			dadesAnotacio.setTipusAssumpte(tipusAssumpteCodi);
		}
		
		if(notificacio.getProcediment().getCodiAssumpte() != null) {
			dadesAnotacio.setCodiAssumpte(notificacio.getProcediment().getCodiAssumpte());
		} else if (tipusAssumpte != null && ! tipusAssumpte.isEmpty()) {
			List<CodiAssumpte> codisAssumpte = llistarCodisAssumpte(
					notificacio.getEmisorDir3Codi(), 
					tipusAssumpte.get(0).getCodi());
			if (codisAssumpte != null && ! codisAssumpte.isEmpty()) {
				String codiAssumpte = codisAssumpte.get(0).getCodi();
				dadesAnotacio.setCodiAssumpte(codiAssumpte);
			}
		}
		
		dadesAnotacio.setExtracte(notificacio.getConcepte());
		dadesAnotacio.setUnitatAdministrativa(null);
		dadesAnotacio.setDocfisica(1L);
		dadesAnotacio.setNumExpedient(notificacio.getNumExpedient());
		dadesAnotacio.setObservacions(notificacio.getDescripcio());
//		dadesAnotacio.setRefExterna(notificacio.getRefExterna());
		dadesAnotacio.setCodiUsuari(notificacio.getUsuariCodi());
		registreSortida.setDadesAnotacio(dadesAnotacio);
		if (notificacio.getDocument() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			documents.add(documentToDocumentRegistreDto(notificacio.getDocument()));
			
			registreSortida.setDocuments(documents);
		}
		registreSortida.setAplicacio("NOTIB");
		registreSortida.setVersioNotib(aplicacioService.getVersioActual());
		
		return registreSortida;
	}

	public AsientoRegistralBeanDto notificacioEnviamentsToAsientoRegistralBean(
			NotificacioEntity notificacio, 
			Set<NotificacioEnviamentEntity> enviaments) {
		AsientoRegistralBeanDto registre = new AsientoRegistralBeanDto();
		registre.setEntidadCodigo(notificacio.getEntitat().getCodi());
		registre.setEntidadDenominacion(notificacio.getEntitat().getNom());
		registre.setEntidadRegistralInicioCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralInicioDenominacion(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralOrigenCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralOrigenDenominacion(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralDestinoCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralDestinoDenominacion(notificacio.getProcediment().getOficina());
		registre.setUnidadTramitacionOrigenCodigo(notificacio.getProcediment().getOrganGestor());
		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getProcediment().getOrganGestor());
		registre.setUnidadTramitacionDestinoCodigo(notificacio.getProcediment().getOficina());
		registre.setUnidadTramitacionDestinoDenominacion(notificacio.getProcediment().getOficina());
		registre.setTipoRegistro(2L);
		registre.setLibroCodigo(notificacio.getProcediment().getLlibre());
		registre.setResumen(notificacio.getConcepte());
		/* 1 = Documentació adjunta en suport Paper
		 * 2 = Documentació adjunta digitalitzada i complementàriament en paper
		 * 3 = Documentació adjunta digitalitzada */
		registre.setTipoDocumentacionFisicaCodigo(3L);
		registre.setTipoAsunto(notificacio.getProcediment().getTipusAssumpte());
		registre.setTipoAsuntoDenominacion(notificacio.getProcediment().getTipusAssumpte());
		registre.setCodigoAsunto(notificacio.getProcediment().getCodiAssumpte());
		registre.setCodigoAsuntoDenominacion(notificacio.getProcediment().getCodiAssumpte());
		registre.setIdioma(1L);
//		registre.setReferenciaExterna(notificacio.getRefExterna());
		registre.setNumeroExpediente(notificacio.getNumExpedient());
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
		registre.setCodigoSia(Long.parseLong(notificacio.getProcediment().getCodi()));
		registre.setCodigoUsuario(notificacio.getUsuariCodi());
		registre.setAplicacionTelematica("NOTIB");
		registre.setAplicacion("RWE");
		registre.setVersion("3.1");
		registre.setObservaciones(notificacio.getDescripcio());
		registre.setExpone("");
		registre.setSolicita("");
		registre.setPresencial(false);
		registre.setEstado(notificacio.getEstat().getLongVal());
		registre.setMotivo(notificacio.getDescripcio());
		registre.setInteresados(new ArrayList<InteresadoWsDto>());
		registre.setAnexos(new ArrayList<AnexoWsDto>());
		
		for(NotificacioEnviamentEntity enviament : enviaments) {
			PersonaEntity destinatari = null;
			if(enviament.getDestinataris() != null && enviament.getDestinataris().size() > 0) {
				destinatari =  enviament.getDestinataris().get(0);
			}
			registre.getInteresados().add(personaToRepresentanteEInteresadoWs(enviament.getTitular(), destinatari));	
		}
		if(notificacio.getDocument() != null) {
			registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument()));
		}
		return registre;
	}
	
	public AsientoRegistralBeanDto notificacioToAsientoRegistralBean(
			NotificacioEntity notificacio, 
			NotificacioEnviamentEntity enviament) {
		AsientoRegistralBeanDto registre = new AsientoRegistralBeanDto();
		registre.setEntidadCodigo(notificacio.getEntitat().getCodi());
		registre.setEntidadDenominacion(notificacio.getEntitat().getNom());
		registre.setEntidadRegistralInicioCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralInicioDenominacion(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralOrigenCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralOrigenDenominacion(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralDestinoCodigo(notificacio.getProcediment().getOficina());
		registre.setEntidadRegistralDestinoDenominacion(notificacio.getProcediment().getOficina());
		registre.setUnidadTramitacionOrigenCodigo(notificacio.getProcediment().getOrganGestor());
		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getProcediment().getOrganGestor());
		registre.setUnidadTramitacionDestinoCodigo(notificacio.getProcediment().getOficina());
		registre.setUnidadTramitacionDestinoDenominacion(notificacio.getProcediment().getOficina());
		registre.setTipoRegistro(2L);
		registre.setLibroCodigo(notificacio.getProcediment().getLlibre());
		registre.setResumen(notificacio.getConcepte());
		/* 1 = Documentació adjunta en suport Paper
		 * 2 = Documentació adjunta digitalitzada i complementàriament en paper
		 * 3 = Documentació adjunta digitalitzada */
		registre.setTipoDocumentacionFisicaCodigo(3L);
		registre.setTipoAsunto(notificacio.getProcediment().getTipusAssumpte());
		registre.setTipoAsuntoDenominacion(notificacio.getProcediment().getTipusAssumpte());
		registre.setCodigoAsunto(notificacio.getProcediment().getCodiAssumpte());
		registre.setCodigoAsuntoDenominacion(notificacio.getProcediment().getCodiAssumpte());
		registre.setIdioma(1L);
//		registre.setReferenciaExterna(notificacio.getRefExterna());
		registre.setNumeroExpediente(notificacio.getNumExpedient());
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
		registre.setCodigoSia(Long.parseLong(notificacio.getProcediment().getCodi()));
		registre.setCodigoUsuario(PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.codi.usuari"));
		registre.setAplicacionTelematica("NOTIB");
		registre.setAplicacion("NOTIB");
		registre.setVersion("3.1");
		registre.setObservaciones(notificacio.getDescripcio());
		registre.setExpone("");
		registre.setSolicita("");
		registre.setPresencial(false);
		registre.setEstado(notificacio.getEstat().getLongVal());
		registre.setUnidadTramitacionOrigenCodigo(notificacio.getProcediment().getOrganGestor());
		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getProcediment().getOrganGestor());
		registre.setMotivo(notificacio.getDescripcio());
		registre.setInteresados(new ArrayList<InteresadoWsDto>());
		registre.setAnexos(new ArrayList<AnexoWsDto>());
		PersonaEntity destinatari = null;
		if(enviament.getDestinataris() != null && enviament.getDestinataris().size() > 0) {
			destinatari =  enviament.getDestinataris().get(0);
		}
		registre.getInteresados().add(personaToRepresentanteEInteresadoWs(
						enviament.getTitular(), 
						destinatari));
		if(notificacio.getDocument() != null) {
			registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument()));
		}
		return registre;
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
	
	public InteresadoWsDto personaToInteresadoWs (PersonaEntity persona) {
		InteresadoWsDto interessat = new InteresadoWsDto();
		DatosInteresadoWsDto interessatDades = new DatosInteresadoWsDto();
		interessatDades.setTipoInteresado(persona.getInteressatTipus().getLongVal());
		interessatDades.setTipoDocumentoIdentificacion("N");
		interessatDades.setDocumento(persona.getNif());
		interessatDades.setRazonSocial(persona.getNom());
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
	
	public DadesInteressat personaToDadesInteressat (
			NotificacioDtoV2 notificacio, 
			PersonaDto titular) {
		DadesInteressat dadesInteressat = new DadesInteressat();
		if (titular != null && notificacio != null) {
			dadesInteressat.setEntitatCodi(notificacio.getEmisorDir3Codi());
			dadesInteressat.setAutenticat(false);
			dadesInteressat.setNif(titular.getNif());
			dadesInteressat.setNom(titular.getNom());
			dadesInteressat.setCognom1(titular.getLlinatge1());
			dadesInteressat.setCognom2(titular.getLlinatge2());
			dadesInteressat.setNomAmbCognoms(titular.getNom() + " " + titular.getLlinatges());
			dadesInteressat.setTipusInteressat(titular.getInteressatTipus().getLongVal());
			dadesInteressat.setPaisCodi(null);
			dadesInteressat.setPaisNom(null);
			dadesInteressat.setProvinciaCodi(null);
			dadesInteressat.setProvinciaNom(null);
			dadesInteressat.setMunicipiCodi(null);
			dadesInteressat.setMunicipiNom(null);
		}
		return dadesInteressat;
	}
	
	private DadesRepresentat personaToDadesRepresentat (
			NotificacioDtoV2 notificacio,
			PersonaDto titular,
			PersonaDto destinatari) {
		DadesRepresentat dadesRepresentat = null;
		if (destinatari != null && titular.isIncapacitat()) {
			dadesRepresentat = new DadesRepresentat();
			dadesRepresentat.setEntitatCodi(notificacio.getEmisorDir3Codi());
			dadesRepresentat.setAutenticat(false);
			dadesRepresentat.setNif(destinatari.getNif());
			dadesRepresentat.setNom(destinatari.getNom());
			dadesRepresentat.setCognom1(destinatari.getLlinatge1());
			dadesRepresentat.setCognom2(destinatari.getLlinatge2());
			dadesRepresentat.setNomAmbCognoms(destinatari.getNom() + " " + destinatari.getLlinatges());
			dadesRepresentat.setTipusInteressat(destinatari.getInteressatTipus().getLongVal());
			dadesRepresentat.setPaisCodi(null);
			dadesRepresentat.setPaisNom(null);
			dadesRepresentat.setProvinciaCodi(null);
			dadesRepresentat.setProvinciaNom(null);
			dadesRepresentat.setMunicipiCodi(null);
			dadesRepresentat.setMunicipiNom(null);
		}
		return dadesRepresentat;
	}
	
	public InteresadoWsDto personaToRepresentanteEInteresadoWs (
			PersonaEntity titular, 
			PersonaEntity destinatari) {
		InteresadoWsDto interessat = new InteresadoWsDto();
		if(titular != null) {
			DatosInteresadoWsDto interessatDades = new DatosInteresadoWsDto();
			interessatDades.setTipoInteresado(titular.getInteressatTipus().getLongVal());
			interessatDades.setTipoDocumentoIdentificacion("N");
			interessatDades.setDocumento(titular.getNif());
			interessatDades.setRazonSocial(titular.getRaoSocial());
			interessatDades.setNombre(titular.getNom());
			interessatDades.setApellido1(titular.getLlinatge1());
			interessatDades.setApellido2(titular.getLlinatge2());
			interessatDades.setCodigoDire("");
			interessatDades.setDireccion("");
			interessatDades.setCp("");
			interessatDades.setObservaciones("");
			interessatDades.setEmail(titular.getEmail());
			interessatDades.setDireccionElectronica(titular.getEmail());
			interessatDades.setTelefono(titular.getTelefon());
			interessat.setInteresado(interessatDades);
		}
		if(destinatari != null && titular.isIncapacitat()) {
			DatosInteresadoWsDto representantDades = new DatosInteresadoWsDto();
			representantDades.setTipoInteresado(destinatari.getInteressatTipus().getLongVal());
			representantDades.setTipoDocumentoIdentificacion("N");
			representantDades.setDocumento(destinatari.getNif());
			representantDades.setRazonSocial(destinatari.getRaoSocial());
			representantDades.setNombre(destinatari.getNom());
			representantDades.setApellido1(destinatari.getLlinatge1());
			representantDades.setApellido2(destinatari.getLlinatge2());
			representantDades.setCodigoDire("");
			representantDades.setDireccion("");
			representantDades.setCp("");
			representantDades.setObservaciones("");
			representantDades.setEmail(destinatari.getEmail());
			representantDades.setDireccionElectronica(destinatari.getEmail());
			representantDades.setTelefono(destinatari.getTelefon());
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
	
	public boolean isDadesUsuariPluginDisponible() {
		String pluginClass = getPropertyPluginDadesUsuari();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getDadesUsuariPlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de dades d'usuari",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isGestioDocumentalPluginDisponible() {
		String pluginClass = getPropertyPluginGestioDocumental();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getGestioDocumentalPlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de gestió documental",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isSeuPluginDisponible() {
		String pluginClass = getPropertyPluginSeu();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getSeuPlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de seu electrònica",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isRegistrePluginDisponible() {
		String pluginClass = getPropertyPluginRegistre();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getRegistrePlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de registre",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean isArxiuPluginDisponible() {
		String pluginClass = getPropertyPluginRegistre();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getArxiuPlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin d'arxiu",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}
	

	private boolean dadesUsuariPluginConfiguracioProvada = false;
	private DadesUsuariPlugin getDadesUsuariPlugin() {
		if (dadesUsuariPlugin == null && !dadesUsuariPluginConfiguracioProvada) {
			dadesUsuariPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginDadesUsuari();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					dadesUsuariPlugin = (DadesUsuariPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_USUARIS,
							"Error al crear la instància del plugin de dades d'usuari",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de dades d'usuari no està configurada");
			}
		}
		return dadesUsuariPlugin;
	}

	private boolean gestioDocumentalPluginConfiguracioProvada = false;
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (gestioDocumentalPlugin == null && !gestioDocumentalPluginConfiguracioProvada) {
			gestioDocumentalPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de gestió documental",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de gestió documental no està configurada");
			}
		}
		return gestioDocumentalPlugin;
	}

	private boolean seuPluginConfiguracioProvada = false;
	private SeuPlugin getSeuPlugin() {
		if (seuPlugin == null && !seuPluginConfiguracioProvada) {
			seuPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginSeu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					seuPlugin = (SeuPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de seu electrònica",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de seu electrònica no està configurada");
			}
		}
		return seuPlugin;
	}

	private boolean registrePluginConfiguracioProvada = false;
	private RegistrePlugin getRegistrePlugin() {
		if (registrePlugin == null && !registrePluginConfiguracioProvada) {
			registrePluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginRegistre();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_REGISTRE,
							"Error al crear la instància del plugin de registre",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE,
						"La classe del plugin de registre no està configurada");
			}
		}
		return registrePlugin;
	}
	
	private IArxiuPlugin getArxiuPlugin() {
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (PropertiesHelper.getProperties().isLlegirSystem()) {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.notib.");
					} else {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"es.caib.notib.",
								PropertiesHelper.getProperties().findAll());
					}
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_CUSTODIA,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_CUSTODIA,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}

	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() {
		if (unitatsOrganitzativesPlugin == null) {
			String pluginClass = getPropertyPluginUnitats();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					unitatsOrganitzativesPlugin = (UnitatsOrganitzativesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_REGISTRE,
							"Error al crear la instància del plugin de registre",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE,
						"La classe del plugin de registre no està configurada");
			}
		}
		
		return unitatsOrganitzativesPlugin;
	}

	private String getPropertyPluginUnitats() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.unitats.class");
	}
	private String getPropertyAmbTitularIncapacitat() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.titular.incapacitat");
	}
	private String getPropertyIsMultipleDestinatari() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.destinatari.multiple");
	}
	private String getPropertyPluginDadesUsuari() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.class");
	}
	private String getPropertyPluginGestioDocumental() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesdoc.class");
	}
	private String getPropertyPluginSeu() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.seu.class");
	}
	private String getPropertyPluginRegistre() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.class");
	}
	private String getPropertyPluginArxiu() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.arxiu.class");
	}
	public int getNotificaReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.notifica.enviaments.reintents.maxim");
	}
	public int getNotificaReintentsPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.notifica.enviaments.periode");
	}
	public int getSeuReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.seu.enviaments.reintents.maxim");
	}
	public int getSeuReintentsEnviamentPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.seu.enviaments.periode");
	}
	public int getSeuReintentsConsultaPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.seu.consulta.periode");
	}
	
	public NotificacioComunicacioTipusEnumDto getNotibTipusComunicacioDefecte() {
		NotificacioComunicacioTipusEnumDto tipus = NotificacioComunicacioTipusEnumDto.SINCRON;
		
		try {
			String tipusStr = PropertiesHelper.getProperties().getProperty("es.caib.notib.comunicacio.tipus.defecte", "SINCRON");
			if (tipusStr != null && !tipusStr.isEmpty())
				tipus = NotificacioComunicacioTipusEnumDto.valueOf(tipusStr);
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el tipus de comunicació per defecte. S'utilitzarà el tipus SINCRON.");
		}
				
		return tipus;
	}

	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

}
