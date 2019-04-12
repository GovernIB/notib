/**
 * 
 */
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

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
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
import es.caib.notib.plugin.registre.Oficina;
import es.caib.notib.plugin.registre.Organisme;
import es.caib.notib.plugin.registre.RegistreModeFirmaEnum;
import es.caib.notib.plugin.registre.RegistreOrigenEnum;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RegistrePluginRegWeb3;
import es.caib.notib.plugin.registre.RegistreSortida;
import es.caib.notib.plugin.registre.RegistreTipusDocumentEnum;
import es.caib.notib.plugin.registre.RegistreTipusDocumentalEnum;
import es.caib.notib.plugin.registre.RespostaAnotacioRegistre;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import es.caib.notib.plugin.registre.TipusAssumpte;
import es.caib.notib.plugin.seu.SeuPlugin;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.ArxiuException;
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

	@Autowired
	private IntegracioHelper integracioHelper;
	
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

	public RegistreIdDto registreAnotacioSortida(
			NotificacioDtoV2 notificacio, 
			NotificacioEnviamentDtoV2 enviament, 
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
							enviament),
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
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
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
	public DocumentRegistre documentToDocumentRegistreDto (DocumentDto documentDto) throws SistemaExternException {
		DocumentRegistre document = new DocumentRegistre();
		
		if((documentDto.getUuid() != null || documentDto.getCsv() != null) && documentDto.getUrl() == null && documentDto.getContingutBase64() == null) {
			String id = "";
			
			if(documentDto.getUuid() != null) {
				id = documentDto.getUuid();
				document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
			} else if (documentDto.getCsv() != null) {
				id = documentDto.getCsv();
				document.setModeFirma(RegistreModeFirmaEnum.AUTOFIRMA_SI.getValor());
			}
			try {
				DocumentContingut doc = arxiuGetImprimible(id, true);
					
				document.setArxiuNom(doc.getArxiuNom());
				document.setArxiuContingut(doc.getContingut());
				document.setIdiomaCodi("ca");
				document.setData(new Date());
				document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
				document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.name());
				document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.name());
			} catch(ArxiuException ae) {
				logger.error("Error Obtenint el document uuid/csv: " + id);
			}
		} else if(documentDto.getUrl() != null && (documentDto.getUuid() == null && documentDto.getCsv() == null) && documentDto.getContingutBase64() == null) {
			document.setNom(documentDto.getUrl());
			document.setArxiuNom(documentDto.getUrl());
			document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.name());
			document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.name());
			document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
			document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
			document.setData(new Date());
			document.setIdiomaCodi("ca");
		} else if(documentDto.getArxiuGestdocId() != null && documentDto.getUrl() == null && (documentDto.getUuid() == null && documentDto.getCsv() == null)) {
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
					logger.error("Error Obtenint el document per l'uuid");
				}
			}
		}else if(document.getUrl() != null && (document.getUuid() == null && document.getCsv() == null) && document.getContingutBase64() == null) {
			annex.setNom(document.getUrl());
			annex.setArxiuNom(document.getUrl());
			annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
			annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
			annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
			annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);
			annex.setData(new Date());
			annex.setIdiomaCodi("ca");
		}else if(document.getContingutBase64() != null && document.getUrl() == null && (document.getUuid() == null && document.getCsv() == null)) {
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
	
	public AnexoWsDto documentToAnexoWs(DocumentEntity document) {
		try {
			document.setMetadades("<?xml version=\"1.0\" encoding=\"UTF-8\"?><metadades><tipoDocumental>TD01</tipoDocumental><validezDocumento>02</validezDocumento><tipoDocumento>01</tipoDocumento><observaciones>anexo detached</observaciones><origenCiudadanoAdmin>0</origenCiudadanoAdmin></metadades>");
			AnexoWsDto annex = null;
			Path path = null;
			Map<String, Object> metadades = convertNodesFromXml(document.getMetadades());
			
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
			annex.setOrigenCiudadanoAdmin(Integer.parseInt((String)metadades.get("origenCiudadanoAdmin")));
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
	
	public static Map<String, Object> convertNodesFromXml(String xml) throws Exception {
	    InputStream is = new ByteArrayInputStream(xml.getBytes());
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document document = db.parse(is);
	    return createMap(document.getDocumentElement());
	}
	
	
	public static Map<String, Object> createMap(Node node) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    NodeList nodeList = node.getChildNodes();
	    for (int i = 0; i < nodeList.getLength(); i++) {
	        Node currentNode = nodeList.item(i);
	        if (currentNode.hasAttributes()) {
	            for (int j = 0; j < currentNode.getAttributes().getLength(); j++) {
	                Node item = currentNode.getAttributes().item(i);
	                map.put(item.getNodeName(), item.getTextContent());
	            }
	        }
	        if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
	            map.putAll(createMap(currentNode));
	        } else if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
	            map.put(node.getLocalName(), node.getTextContent());
	        }
	    }
	    return map;
	}
	
	
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
	
	public List<Oficina> llistarOficines(
			AutoritzacioRegiWeb3Enum autoritzacio) throws RegistrePluginException {
		
		List<Oficina> oficines = getRegistrePlugin().llistarOficines(
				getPropertyPluginCodiEntitatDir3(), 
				autoritzacio.getValor());
	
		return oficines;
	}
	
	public List<Llibre> llistarLlibres(
			String oficina,
			AutoritzacioRegiWeb3Enum autoritzacio) throws RegistrePluginException {
		
		List<Llibre> llibres = getRegistrePlugin().llistarLlibres(
				getPropertyPluginCodiEntitatDir3(), 
				oficina, 
				autoritzacio.getValor());
	
		return llibres;
	}
	
	public List<Organisme> llistarOrganismes() throws RegistrePluginException {
		
		List<Organisme> organismes = getRegistrePlugin().llistarOrganismes(
				getPropertyPluginCodiEntitatDir3());
	
		return organismes;
	}
	private RegistreSortida toRegistreSortida(
			NotificacioDtoV2 notificacio,
			NotificacioEnviamentDtoV2 enviament) {
		RegistreSortida registreSortida = new RegistreSortida();
		DadesOficina dadesOficina = new DadesOficina();
		Long tipusInteressat;
		Long docFisica;
		
		switch (enviament.getTitular().getInteressatTipus()) {
		case ADMINISTRACIO:
			tipusInteressat = 1L;
			break;
		case FISICA:
			tipusInteressat = 2L;
			break;
		case JURIDICA:
			tipusInteressat = 3L;
			break;
		default:
			tipusInteressat = 1L;
			break;
		}
		
		switch (notificacio.getDocFisica()) {
		case ACOMPANYA_DOCUMENTACIO_FISICA_REQUERIDA:
			docFisica = 1L;
			break;
		case ACOMPANYA_DOCUMENTACIO_FISICA_COMPLEMENTARIA:
			docFisica = 2L;
			break;
		case NO_ACOMPANYA_DOCUMENTACIO:
			docFisica = 3L;
			break;
		default:
			docFisica = 1L;
			break;
		}
		dadesOficina.setOrgan(notificacio.getEmisorDir3Codi());
		dadesOficina.setOficina(notificacio.getProcediment().getOficina());
		dadesOficina.setLlibre(notificacio.getProcediment().getLlibre());
		registreSortida.setDadesOficina(dadesOficina);
		
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setEntitatCodi(notificacio.getEmisorDir3Codi());
		dadesInteressat.setAutenticat(false);
		dadesInteressat.setNif(enviament.getTitularNif());
		dadesInteressat.setNom(enviament.getTitular().getNom());
		dadesInteressat.setCognom1(enviament.getTitular().getLlinatge1());
		dadesInteressat.setCognom2(enviament.getTitular().getLlinatge2());
		dadesInteressat.setNomAmbCognoms(enviament.getTitularNomLlinatge());
		dadesInteressat.setTipusInteressat(tipusInteressat);
		dadesInteressat.setPaisCodi(null);
		dadesInteressat.setPaisNom(null);
		dadesInteressat.setProvinciaCodi(null);
		dadesInteressat.setProvinciaNom(null);
		dadesInteressat.setMunicipiCodi(null);
		dadesInteressat.setMunicipiNom(null);
		registreSortida.setDadesInteressat(dadesInteressat);
		
		DadesRepresentat dadesRepresentat = new DadesRepresentat();
		registreSortida.setDadesRepresentat(dadesRepresentat);
		
		DadesAnotacio dadesAnotacio = new DadesAnotacio();
		dadesAnotacio.setIdiomaCodi(notificacio.getIdioma().getText());
		dadesAnotacio.setTipusAssumpte(notificacio.getProcediment().getTipusAssumpte());
		dadesAnotacio.setCodiAssumpte(notificacio.getProcediment().getCodiAssumpte());
		dadesAnotacio.setExtracte(notificacio.getExtracte());
		dadesAnotacio.setUnitatAdministrativa(null);
		dadesAnotacio.setDocfisica(docFisica);
		dadesAnotacio.setNumExpedient(notificacio.getNumExpedient());
		dadesAnotacio.setObservacions(notificacio.getObservacions());
		dadesAnotacio.setRefExterna(notificacio.getRefExterna());
		dadesAnotacio.setCodiUsuari(notificacio.getUsuariCodi());
		registreSortida.setDadesAnotacio(dadesAnotacio);
		if (notificacio.getDocument() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			documents.add(documentToDocumentRegistreDto(notificacio.getDocument()));
			
			registreSortida.setDocuments(documents);
		}
		return registreSortida;
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
		registre.setUnidadTramitacionOrigenCodigo(notificacio.getOrgan());
		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getOrgan());
		registre.setUnidadTramitacionDestinoCodigo(notificacio.getProcediment().getOficina());
		registre.setUnidadTramitacionDestinoDenominacion(notificacio.getProcediment().getOficina());
		registre.setTipoRegistro(2L);
		registre.setLibroCodigo(notificacio.getProcediment().getLlibre());
		registre.setResumen(notificacio.getExtracte());
		/* 1 = Documentació adjunta en suport Paper
		 * 2 = Documentació adjunta digitalitzada i complementàriament en paper
		 * 3 = Documentació adjunta digitalitzada */
		registre.setTipoDocumentacionFisicaCodigo(3L);
		registre.setTipoAsunto(notificacio.getTipusAssumpte());
		registre.setTipoAsuntoDenominacion(notificacio.getTipusAssumpte());
		registre.setCodigoAsunto(notificacio.getCodiAssumpte());
		registre.setCodigoAsuntoDenominacion(notificacio.getCodiAssumpte());
		registre.setIdioma(1L);
		registre.setReferenciaExterna(notificacio.getRefExterna());
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
//		registre.setNumeroTransporte();
		registre.setCodigoSia(Long.parseLong(notificacio.getProcediment().getCodi()));
		registre.setCodigoUsuario(notificacio.getUsuariCodi());
		registre.setAplicacionTelematica("NOTIB");
		registre.setAplicacion("NOTIB");
		registre.setVersion("3.1");
		registre.setObservaciones(notificacio.getObservacions());
		registre.setExpone("");
		registre.setSolicita("");
		registre.setPresencial(false);
//		registre.setTipoEnvioDocumentacion();
		registre.setEstado(notificacio.getEstat().getLongVal());
		registre.setUnidadTramitacionOrigenCodigo(notificacio.getOrgan());
		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getOrgan());
//		registre.setIdentificadorIntercambio();
//		registre.setFechaRecepcion();
//		registre.setCodigoError();
//		registre.setNumeroRegistroDestino();
//		registre.setFechaRegistroDestino();
		registre.setMotivo(notificacio.getDescripcio());
		registre.setInteresados(new ArrayList<InteresadoWsDto>());
		registre.setAnexos(new ArrayList<AnexoWsDto>());
		registre.getInteresados().add(personaToInteresadoWs(enviament.getTitular()));
		if(notificacio.getDocument() != null) {
			registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument()));
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
	
	public InteresadoWsDto personaToInteresadoWs (PersonaEntity persona) {
		InteresadoWsDto interessat = new InteresadoWsDto();
		DatosInteresadoWsDto interessatDades = new DatosInteresadoWsDto();
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
	
	/*
	public SeuNotificacioResultat seuNotificacioDestinatariEnviar(
			NotificacioEnviamentEntity notificacioDestinatari) {
		NotificacioEntity notificacio = notificacioDestinatari.getNotificacio();
		String accioDescripcio = "Enviament d'una notificació a la seu electrònica per un destinatari";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", notificacio.getSeuExpedientIdentificadorEni());
		accioParams.put("expedientUnitatOrganitzativa", notificacio.getSeuExpedientUnitatOrganitzativa());
		accioParams.put("expedientSerieDocumental", notificacio.getSeuExpedientSerieDocumental());
		accioParams.put("expedientTitol", notificacio.getSeuExpedientTitol());
		accioParams.put("registreOficina", notificacio.getSeuRegistreOficina());
		accioParams.put("registreLlibre", notificacio.getSeuRegistreLlibre());
		accioParams.put("idioma", notificacio.getSeuIdioma());
		accioParams.put("avisTitol", notificacio.getSeuAvisTitol());
		accioParams.put("oficiTitol", notificacio.getSeuOficiTitol());
		accioParams.put("destinatariNif", notificacioDestinatari.getDestinatariNif());
		boolean isNotificacio = NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus());
		long t0 = System.currentTimeMillis();
		try {
			SeuPersona representant = new SeuPersona();
			SeuPersona representat = null;
			
			if (notificacioDestinatari.getDestinatariNif() != null) {
				// Representant
				representant.setNif(notificacioDestinatari.getDestinatariNif());
				representant.setNom(notificacioDestinatari.getDestinatariNom());
				representant.setLlinatge1(notificacioDestinatari.getDestinatariLlinatge1());
				representant.setLlinatge2(notificacioDestinatari.getDestinatariLlinatge2());
				// Representat
				representat = new SeuPersona();
				representat.setNif(notificacioDestinatari.getTitularNif());
				representat.setNom(notificacioDestinatari.getTitularNom());
				representat.setLlinatge1(notificacioDestinatari.getTitularLlinatge1());
				representat.setLlinatge2(notificacioDestinatari.getTitularLlinatge2());
			} else {
				// Representant
				representant.setNif(notificacioDestinatari.getTitularNif());
				representant.setNom(notificacioDestinatari.getTitularNom());
				representant.setLlinatge1(notificacioDestinatari.getTitularLlinatge1());
				representant.setLlinatge2(notificacioDestinatari.getTitularLlinatge2());
			}
			
			
			
			String telefonMobil = null;
			if (isTelefonMobil(notificacioDestinatari.getDestinatariTelefon())) {
				telefonMobil = notificacioDestinatari.getDestinatariTelefon();
			}
			getSeuPlugin().comprovarExpedientCreat(
					notificacio.getSeuExpedientIdentificadorEni(),
					notificacio.getSeuExpedientUnitatOrganitzativa(),
					notificacio.getSeuProcedimentCodi(),
					notificacio.getSeuIdioma(),
					notificacio.getSeuExpedientTitol(),
					representant,
					representat,
					null, //bantelNumeroEntrada,
					true,
					notificacioDestinatari.getDestinatariEmail(),
					telefonMobil);
			List<SeuDocument> annexos = new ArrayList<SeuDocument>();
			SeuDocument annex = new SeuDocument();
			annex.setArxiuNom(notificacio.getDocumentArxiuNom());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gestioDocumentalGet(
					notificacio.getDocumentArxiuId(),
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					baos);
			annex.setArxiuContingut(baos.toByteArray());
			annexos.add(annex);
			SeuNotificacioResultat notificacioResultat = getSeuPlugin().notificacioCrear(
					notificacio.getSeuExpedientIdentificadorEni(),
					notificacio.getSeuExpedientUnitatOrganitzativa(),
					notificacio.getSeuRegistreLlibre(),
					notificacio.getSeuRegistreOficina(),
					notificacio.getSeuRegistreOrgan(),
					representant,
					representat,
					notificacio.getSeuIdioma(),
					notificacio.getSeuOficiTitol(),
					notificacio.getSeuOficiText(),
					notificacio.getSeuAvisTitol(),
					notificacio.getSeuAvisText(),
					notificacio.getSeuAvisTextMobil(),
					notificacio.getCaducitat(),
					isNotificacio,
					annexos);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return notificacioResultat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de seu electrònica";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_SEU,
					errorDescripcio,
					ex);
		}
	}

	public SeuNotificacioEstat seuNotificacioComprovarEstat(
			NotificacioEnviamentEntity notificacioDestinatari) {
		NotificacioEntity notificacio = notificacioDestinatari.getNotificacio();
		String accioDescripcio = "Consulta de l'estat d'una notificació a la seu electrònica";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", notificacio.getSeuExpedientIdentificadorEni());
		accioParams.put("expedientUnitatOrganitzativa", notificacio.getSeuExpedientUnitatOrganitzativa());
		accioParams.put("expedientSerieDocumental", notificacio.getSeuExpedientSerieDocumental());
		accioParams.put("expedientTitol", notificacio.getSeuExpedientTitol());
		accioParams.put("registreNumero", notificacioDestinatari.getSeuRegistreNumero());
		long t0 = System.currentTimeMillis();
		try {
			SeuNotificacioEstat notificacioEstat = getSeuPlugin().notificacioObtenirJustificantRecepcio(
					notificacioDestinatari.getSeuRegistreNumero());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return notificacioEstat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de seu electrònica";
			if (ex.getMessage().contains("No existeix la notificació"))
				errorDescripcio = "Error al consultar la notificació. No existeix la notificació";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_SEU,
					errorDescripcio,
					ex);
		}
	}
	
	public SeuDocument obtenirJustificant(NotificacioEnviamentEntity notificacioDestinatari) {
		
		SeuDocument seuDocument = null;
		try {
			seuDocument = getSeuPlugin().notificacioObtenirFitxerJustificantRecepcio(
					notificacioDestinatari.getSeuFitxerCodi(),
					notificacioDestinatari.getSeuFitxerClau());
		} catch (Exception ex) {
			// TODO: handle exception
		}
		return seuDocument;
	}
	 */
	/*public RespostaAnotacioRegistre registreSortida(Notificacio notificacio) {
		String accioDescripcio = "Anotació al registre de sortida";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("notificacio.cifEntitat", notificacio.getCifEntitat());
		accioParams.put("notificacio.concepte", notificacio.getConcepte());
		accioParams.put("notificacio.enviamentTipus", notificacio.getEnviamentTipus().getText());
		long t0 = System.currentTimeMillis();
		try {
			RespostaAnotacioRegistre resposta = null;
			if (	getPropertyRegistrePluginDesactivat() &&
					!getPropertyRegistrePluginObligatori()) return null;
			if (	getPropertyRegistrePluginObligatori() ||
					notificacio.isRegistreEnviar()) {
				RegistreAssentament registreSortida = new RegistreAssentament();
				List<RegistreAssentamentInteressat> registreAssentamentInteressat = new ArrayList<RegistreAssentamentInteressat>();
				DocumentRegistre document = new DocumentRegistre();
				EntitatEntity entitat = entitatRepository.findByDir3Codi(notificacio.getCifEntitat());
				registreSortida.setOrganisme(entitat.getDir3Codi());
				registreSortida.setOficina(notificacio.getRegistreOficina());
				registreSortida.setLlibre(notificacio.getRegistreLlibre());
				registreSortida.setExtracte(notificacio.getSeuAvisText()); // Preguntar si es correcte
				registreSortida.setAssumpteTipus(notificacio.getRegistreTipusAssumpte());
				registreSortida.setAssumpteCodi(notificacio.getRegistreCodiAssumpte());
				registreSortida.setIdioma(notificacio.getRegistreIdioma());
				registreSortida.setDocumentacioFisicaCodi(notificacio.getDocumentacioFisicaCodi());
				for (NotificacioDestinatari destinatari: notificacio.getDestinataris()) {
					RegistreAssentamentInteressat interessat = new RegistreAssentamentInteressat();
					if (esPersonaJuridica(destinatari.getDestinatariNif())) {
						interessat.setTipus("3");
						interessat.setDocumentTipus("CIF");
						interessat.setRaoSocial(destinatari.getDestinatariNom());
					} else {
						interessat.setTipus("2");
						interessat.setDocumentTipus("NIF");
						interessat.setNom(destinatari.getDestinatariNom());
					}
					interessat.setDocumentNum(destinatari.getDestinatariNif());
					interessat.setLlinatge1(destinatari.getDestinatariLlinatge1());
					interessat.setLlinatge2(destinatari.getDestinatariLlinatge2());
					interessat.setPais(destinatari.getDomiciliPaisCodiIso());
					interessat.setProvincia(destinatari.getDomiciliProvinciaCodi());
					interessat.setMunicipi(destinatari.getDomiciliMunicipiCodiIne());
					interessat.setAdresa(destinatari.getDireccio());
					interessat.setCodiPostal(destinatari.getDomiciliCodiPostal());
					interessat.setEmail(destinatari.getDestinatariEmail());
					interessat.setTelefon(destinatari.getDestinatariTelefon());
					registreAssentamentInteressat.add(interessat);
				}
				document.setTitol("Document de la notificació");
				document.setArxiuNom(notificacio.getDocumentArxiuNom());
				byte[] contingut = Base64.decode(notificacio.getDocumentContingutBase64());
				document.setArxiuContingut(contingut);
				document.setArxiuMida(contingut.length);
				InputStream is = new BufferedInputStream(new ByteArrayInputStream(contingut));
				String mimeType = URLConnection.guessContentTypeFromStream(is);
				document.setTipusMIMEFitxerAnexat(mimeType);
				document.setTipusDocumental(notificacio.getRegistreTipusDocumental());
				document.setOrigenCiutadaAdmin(notificacio.getRegistreOrigenCiutadaAdmin());
				document.setDataCaptura(notificacio.getRegistreDataCaptura());
				registreSortida.setInteressats(registreAssentamentInteressat);
				registreSortida.setDocument(document);
				resposta = getRegistrePlugin().registrarSortida(registreSortida);
			}
			return resposta;
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
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}*/

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
	
	public boolean isArxiuEmprarSir() {
		String sir = getPropertyEmprarSir();
		return Boolean.valueOf(sir);
	}

	/*private boolean esPersonaJuridica(String codiId) {
		String letrasCif = "ABCDEFGHJKLMNPQRSVW";
		String primeraLletraCif = codiId.toUpperCase().substring(0, 1);
        return letrasCif.contains(primeraLletraCif);
	}*/

	private boolean isTelefonMobil(String telefonMobil) {
		if (telefonMobil == null) {
			return false;
		}
		String telefonTrim = telefonMobil.replace(" ", "");
		return (
				telefonTrim.startsWith("00346") ||
				telefonTrim.startsWith("+346") ||
				telefonTrim.startsWith("6"));
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
	
	private boolean arxiuPluginConfiguracioProvada = false;
	private IArxiuPlugin getArxiuPlugin() {
		if (arxiuPlugin == null && !arxiuPluginConfiguracioProvada) {
			arxiuPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					arxiuPlugin = (IArxiuPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_REGISTRE,
							"Error al crear la instància del plugin d'arxiu",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE,
						"La classe del plugin d'arxiu no està configurada");
			}
		}
		return arxiuPlugin;
	}

	private String getPropertyEmprarSir() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.emprar.sir");
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
	private String getPropertyPluginRegistreRegWeb3() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.regweb.class");
	}
	private String getPropertyPluginCodiEntitatDir3() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.regweb.entitat.dir3");
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
