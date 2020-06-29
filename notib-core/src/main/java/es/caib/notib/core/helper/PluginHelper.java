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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.AnexoWsDto;
import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.DatosInteresadoWsDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.InteresadoWsDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
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
import es.caib.notib.core.api.dto.RegistreValidezDocumentDtoEnum;
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
import es.caib.notib.plugin.registre.Interessat;
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
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
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
	private RegistrePlugin registrePlugin;
	private IArxiuPlugin arxiuPlugin;
	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;

	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private AplicacioService aplicacioService;
	
	
	// REGISTRE
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public RespostaConsultaRegistre registreSortidaAsientoRegistral(
			String codiDir3Entitat, 
			NotificacioEntity notificacio, 
			NotificacioEnviamentEntity enviament, 
			Long tipusOperacio) throws RegistrePluginException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Enviament notificació a registre (SIR activat)", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Id de la notificacio", String.valueOf(notificacio.getId())),
				new AccioParam("Id de l'enviament", String.valueOf(enviament.getId())),
				new AccioParam("Tipus d'operacio", String.valueOf(tipusOperacio)));
		
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		
		try {
			resposta = getRegistrePlugin().salidaAsientoRegistral(
					codiDir3Entitat, 
					notificacioToAsientoRegistralBean(
							notificacio, 
							enviament), 
					tipusOperacio);
			
			if (resposta.getErrorCodi() == null) {
				integracioHelper.addAccioOk(info);
			} else {
				integracioHelper.addAccioError(info, "Error creant assentament registral: " + resposta.getErrorDescripcio());
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
			}
			resposta.setErrorDescripcio(errorDescripcio);
		}
		
		return resposta;
	}
	
	public RespostaConsultaRegistre crearAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb,
			Long tipusOperacio,
			Long notificacioId,
			String enviamentIds) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Enviament notificació a registre (SIR activat)", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Id de la notificacio", String.valueOf(notificacioId)),
				new AccioParam("Ids dels enviaments", enviamentIds),
				new AccioParam("Tipus d'operacio", String.valueOf(tipusOperacio)));
		
		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		
		try {
			resposta = getRegistrePlugin().salidaAsientoRegistral(
					codiDir3Entitat, 
					arb, 
					tipusOperacio);
			
			if (resposta.getErrorCodi() == null) {
				integracioHelper.addAccioOk(info);
			} else {
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
			}
			resposta.setErrorDescripcio(errorDescripcio);
		}
	
		return resposta;

	}
	
	public RespostaConsultaRegistre obtenerAsientoRegistral(
			String codiDir3Entitat, 
			String numeroRegistreFormatat, 
			Long tipusRegistre, 
			boolean ambAnnexos) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Consulta de assentament registral SIR", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat),
				new AccioParam("Tipus de registre", String.valueOf(tipusRegistre)),
				new AccioParam("Amb annexos?", String.valueOf(ambAnnexos)));

		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
		
		try {
			resposta = getRegistrePlugin().obtenerAsientoRegistral(
					codiDir3Entitat, 
					numeroRegistreFormatat, 
					tipusRegistre, 
					ambAnnexos);
			
			if (resposta.getErrorCodi() == null) {
				integracioHelper.addAccioOk(info);
			} else {
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
			}
		
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
			}
			resposta.setErrorDescripcio(errorDescripcio);
		}
		
		return resposta;
	}
	
	public RespostaJustificantRecepcio obtenirJustificant(
			String codiDir3Entitat, 
			String numeroRegistreFormatat) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir justificant de registre", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat));
		
		RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
		
		try {
			resposta = getRegistrePlugin().obtenerJustificante(
				codiDir3Entitat, 
				numeroRegistreFormatat, 
				2);
			
			if (resposta.getErrorCodi() == null) {
				integracioHelper.addAccioOk(info);
			} else {
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
			}
		
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
			}
			resposta.setErrorDescripcio(errorDescripcio);
		}
		
		return resposta;
	}
	
	public RespostaJustificantRecepcio obtenirOficiExtern(
			String codiDir3Entitat, 
			String numeroRegistreFormatat) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir ofici extern", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat));
			
		RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
		
		try {
			resposta = getRegistrePlugin().obtenerOficioExterno(
					codiDir3Entitat, 
					numeroRegistreFormatat);
			
			if (resposta.getErrorCodi() == null) {
				integracioHelper.addAccioOk(info);
			} else {
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
			}
		
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
			}
			resposta.setErrorDescripcio(errorDescripcio);
		}
		
		return resposta;
	}

	public RegistreIdDto registreAnotacioSortida(
			NotificacioDtoV2 notificacio, 
			List<NotificacioEnviamentDtoV2> enviaments, 
			Long tipusOperacio) throws Exception {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Enviament notificació a registre (SIR desactivat)", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Id de la notificacio", String.valueOf(notificacio.getId())));
		
		RegistreIdDto rs = new RegistreIdDto();
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarSalida(
					toRegistreSortida(
							notificacio,
							enviaments),
					"notib");
			if (resposta.getErrorDescripcio() != null) {
				rs.setDescripcioError(resposta.getErrorDescripcio());
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
			} else {
				rs.setNumeroRegistreFormat(resposta.getNumeroRegistroFormateado());
				rs.setData(resposta.getData());
				integracioHelper.addAccioOk(info);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
				rs.setDescripcioError(errorDescripcio);
				return rs;
			} else {
				throw new SistemaExternException(
				IntegracioHelper.INTCODI_REGISTRE,
				errorDescripcio,
				ex);
			}
		}
		return rs;
	}
	
	public List<TipusAssumpte> llistarTipusAssumpte(String entitatCodi) throws SistemaExternException {

		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir llista de tipus d'assumpte", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatCodi));
		
		List<TipusAssumpte> tipusAssumptes = null;
		try {
			tipusAssumptes = getRegistrePlugin().llistarTipusAssumpte(entitatCodi);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els tipus d'assumpte";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}
		return tipusAssumptes;
	}

	public List<CodiAssumpte> llistarCodisAssumpte(
			String entitatcodi,
			String tipusAssumpte) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de codis d'assumpte", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi),
				new AccioParam("Tipus d'assumpte", tipusAssumpte));

		List<CodiAssumpte> assumptes = null;
		try {
			assumptes = getRegistrePlugin().llistarCodisAssumpte(
					entitatcodi, 
					tipusAssumpte);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els codis d'assumpte";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}

		return assumptes;
	}
	
	public Oficina llistarOficinaVirtual(
			String entitatcodi,
			TipusRegistreRegweb3Enum autoritzacio) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la oficina virtual", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi),
				new AccioParam("Tipud de registre", autoritzacio.name()));
		
		Oficina oficina = null;
		try {
			oficina = getRegistrePlugin().llistarOficinaVirtual(
					entitatcodi, 
					autoritzacio.getValor());
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir la oficina virtual";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}

		return oficina;
	}
	
	public List<Oficina> llistarOficines(
			String entitatcodi,
			AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de oficines", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi),
				new AccioParam("Tipud de registre", autoritzacio.name()));
		
		List<Oficina> oficines = null;
		try {
			oficines = getRegistrePlugin().llistarOficines(
					entitatcodi, 
					autoritzacio.getValor());
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar les oficines";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}
	
		return oficines;
	}
	
	public List<LlibreOficina> llistarLlibresOficines(
			String entitatCodi,
			String usuariCodi,
			TipusRegistreRegweb3Enum tipusRegistre) throws SistemaExternException{
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de llibre amb oficina", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatCodi),
				new AccioParam("Codi de l'usuari", usuariCodi),
				new AccioParam("Tipud de registre", tipusRegistre.name()));
		
		List<LlibreOficina> llibresOficines = null; 
		try {
			llibresOficines = getRegistrePlugin().llistarLlibresOficines(
					entitatCodi, 
					usuariCodi,
					tipusRegistre.getValor());
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els llibres amb oficina";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}

	
		return llibresOficines;
	}
	
	private Llibre llistarLlibreOrganisme(
			String entitatCodi,
			String organismeCodi) throws SistemaExternException{
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de llibres per organisme", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatCodi),
				new AccioParam("Codi de l'organisme", organismeCodi));
		
		Llibre llibre = null;
		try {
			llibre = getRegistrePlugin().llistarLlibreOrganisme(
					entitatCodi, 
					organismeCodi);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els llibres d'un organisme";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}
		
		return llibre;

	}
	
	public List<Llibre> llistarLlibres(
			String entitatcodi,
			String oficina,
			AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de llibres d'una oficina", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi),
				new AccioParam("Oficina", oficina));
		
		List<Llibre> llibres = null;
		try {
			llibres = getRegistrePlugin().llistarLlibres(
					entitatcodi, 
					oficina, 
					autoritzacio.getValor());
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els llibres d'una oficina";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}

		return llibres;
	}
	
	public List<Organisme> llistarOrganismes(String entitatcodi) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir llista d'organismes", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi));
		
		List<Organisme> organismes = null;
		try {
			organismes = getRegistrePlugin().llistarOrganismes(entitatcodi);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar organismes";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}

	
		return organismes;
	}
	
	// USUARIS
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public List<String> consultarRolsAmbCodi(
			String usuariCodi) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_USUARIS, 
				"Consulta rols usuari amb codi", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi d'usuari", usuariCodi));
		
		try {
			List<String> rols = getDadesUsuariPlugin().consultarRolsAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(info);
			return rols;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}
	
	public DadesUsuari dadesUsuariConsultarAmbCodi(
			String usuariCodi) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_USUARIS, 
				"Consulta d'usuari amb codi", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi d'usuari", usuariCodi));
		
		try {
			DadesUsuari dadesUsuari = getDadesUsuariPlugin().consultarAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(info);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}
	
	public List<DadesUsuari> dadesUsuariConsultarAmbGrup(
			String grupCodi) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_USUARIS, 
				"Consulta d'usuaris d'un grup", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi de grup", grupCodi));
		
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().consultarAmbGrup(
					grupCodi);
			integracioHelper.addAccioOk(info);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}

	// ARXIU 
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_ARXIU, 
				"Consulta d'un document", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("UUID del document", arxiuUuid),
				new AccioParam("Versio", versio));
		
		try {
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					false);
			integracioHelper.addAccioOk(info);
			return documentDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}
	
	public DocumentContingut arxiuGetImprimible(
			String id,
			boolean uuidCsv) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_ARXIU, 
				"Obtenir versió imprimible d'un document", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Identificador del document", id),
				new AccioParam("Tipus d'identificador", uuidCsv ? "uuid" : "csv"));
		
		DocumentContingut documentContingut = null;
		try {
			if(uuidCsv) {
				id = "uuid:" + id;
			} else {
				id = "csv:" + id;
			}
			documentContingut = getArxiuPlugin().documentImprimible(id);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut recuperar el document amb el uuid/csv proporcionat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
		return documentContingut;	
	}
	
	
	// GESTOR DOCUMENTAL
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public String gestioDocumentalCreate(
			String agrupacio,
			byte[] contingut) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESDOC, 
				"Creació d'un arxiu", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Agrupacio", agrupacio),
				new AccioParam("Núm bytes", (contingut != null) ? Integer.toString(contingut.length) : "0"));
		
		try {
			String gestioDocumentalId = getGestioDocumentalPlugin().create(
					agrupacio,
					new ByteArrayInputStream(contingut));
			info.getParams().add(new AccioParam("Id retornat", gestioDocumentalId));
			integracioHelper.addAccioOk(info);
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear document a dins la gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	public void gestioDocumentalUpdate(
			String id,
			String agrupacio,
			byte[] contingut) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESDOC, 
				"Modificació d'un arxiu", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Id del document", id),
				new AccioParam("Agrupacio", agrupacio),
				new AccioParam("Núm bytes", (contingut != null) ? Integer.toString(contingut.length) : "0"));
		
		try {
			getGestioDocumentalPlugin().update(
					id,
					agrupacio,
					new ByteArrayInputStream(contingut));
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESDOC, 
				"Eliminació d'un arxiu", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Id del document", id),
				new AccioParam("Agrupacio", agrupacio));
		
		try {
			getGestioDocumentalPlugin().delete(
					id,
					agrupacio);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
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
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESDOC, 
				"Consultant arxiu de la gestió documental", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Id del document", id),
				new AccioParam("Agrupacio", agrupacio));
		
		try {
			getGestioDocumentalPlugin().get(
					id,
					agrupacio,
					contingutOut);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	// UNITATS ORGANITZATIVES
	// /////////////////////////////////////////////////////////////////////////////////////
	
	
	public List<ObjetoDirectorio> llistarOrganismesPerEntitat(String entitatcodi) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenir llista d'organismes per entitat", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi));
		
		List<ObjetoDirectorio> organismes = null;
		try {
			organismes = getUnitatsOrganitzativesPlugin().unitatsPerEntitat(entitatcodi, true);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar organismes per entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	
		return organismes;
	}
	
	public String getDenominacio(String codiDir3) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenir denominació d'organisme", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'organisme", codiDir3));
		
		String denominacio = null;
		try {
			denominacio = getUnitatsOrganitzativesPlugin().unitatDenominacio(codiDir3);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir denominació de organisme";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
		return denominacio;
		
	}

	public List<CodiValorPais> llistarPaisos() throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenint llista de països", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		
		List<CodiValorPais> paisos = null;
		try {
			paisos = getUnitatsOrganitzativesPlugin().paisos();
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar països";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
		
		return paisos;
	}

	public List<CodiValor> llistarProvincies() throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenint llista de províncies", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		
		List<CodiValor> provincies = null;
		try {
			provincies = getUnitatsOrganitzativesPlugin().provincies();
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistat províncies";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
		
		return provincies;
	}
	
	public List<CodiValor> llistarLocalitats(String codiProvincia) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenint llista de localitats d'una província", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi de la província", codiProvincia));
		
		List<CodiValor> localitats = null;
		try {
			localitats = getUnitatsOrganitzativesPlugin().localitats(codiProvincia);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistat els municipis d'una província";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
		
		return localitats;
	}
	
	
	//////////////////////////////////////////////
	
	private DocumentRegistre documentToDocumentRegistreDto (DocumentDto documentDto) throws SistemaExternException {
		DocumentRegistre document = new DocumentRegistre();
		
		if(((documentDto.getUuid() != null && !documentDto.getUuid().isEmpty())
			|| (documentDto.getCsv() != null && !documentDto.getCsv().isEmpty()))
			&& (documentDto.getUrl() == null || documentDto.getUrl().isEmpty())
			&& (documentDto.getContingutBase64() == null || documentDto.getContingutBase64().isEmpty())) {
			DocumentContingut doc = null;
			String id = "";
			if(documentDto.getUuid() != null) {
				id = documentDto.getUuid();
				doc = arxiuGetImprimible(id, true);
				document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
				Document docDetall = arxiuDocumentConsultar(id, null);
				if (docDetall.getMetadades() != null) {
					document.setData(docDetall.getMetadades().getDataCaptura());
					document.setOrigen(docDetall.getMetadades().getOrigen().ordinal());
					document.setTipusDocumental(docDetall.getMetadades().getTipusDocumental().toString());
					
					//Recuperar csv
					Map<String, Object> metadadesAddicionals = docDetall.getMetadades().getMetadadesAddicionals();
					if (metadadesAddicionals != null && metadadesAddicionals.containsKey("csv")) {
						document.setCsv((String)metadadesAddicionals.get("csv"));
					}
				}
			} else if (documentDto.getCsv() != null) {
				id = documentDto.getCsv();
				doc = arxiuGetImprimible(id, false);	
				document.setModeFirma(RegistreModeFirmaEnum.AUTOFIRMA_SI.getValor());
				
				document.setData(new Date());
				document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
				document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.getValor());
				document.setCsv(documentDto.getCsv());
			}
			try {
				if (doc != null) {
					document.setArxiuNom(doc.getArxiuNom());
					document.setArxiuContingut(doc.getContingut());
				}
				document.setIdiomaCodi("ca");
				document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
				
			} catch(ArxiuException ae) {
				logger.error("Error Obtenint el document uuid/csv: " + id);
			}
		} else if((documentDto.getUrl() != null && !documentDto.getUrl().isEmpty()) 
				&& (documentDto.getUuid() == null || documentDto.getUuid().isEmpty()) 
				&& (documentDto.getCsv() == null || documentDto.getCsv().isEmpty()) 
				&& (documentDto.getContingutBase64() == null || documentDto.getContingutBase64().isEmpty())) {
			document.setNom(documentDto.getUrl());
			document.setArxiuNom(documentDto.getUrl());
			document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
			document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.getValor());
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
			document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
			document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.getValor());
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
			if((document.getUuid() != null || document.getCsv() != null) && document.getUrl() == null && document.getContingutBase64() == null) {
				annex = new AnexoWsDto();
				String id = "";
				DocumentContingut doc;
				Document docDetall = null;
				if(document.getUuid() != null) {
					id = document.getUuid();
					try {
						doc = arxiuGetImprimible(id, true);
						annex.setFicheroAnexado(doc.getContingut());
						annex.setNombreFicheroAnexado(doc.getArxiuNom());
						docDetall = arxiuDocumentConsultar(id, null);
						
						if (docDetall != null) {
							annex.setTipoDocumental(docDetall.getMetadades().getTipusDocumental().toString());
							annex.setOrigenCiudadanoAdmin(docDetall.getMetadades().getOrigen().ordinal());
							annex.setFechaCaptura(toXmlGregorianCalendar(docDetall.getMetadades().getDataCaptura()));
							
							//Recuperar csv
							Map<String, Object> metadadesAddicionals = docDetall.getMetadades().getMetadadesAddicionals();
							if (metadadesAddicionals != null && metadadesAddicionals.containsKey("csv")) {
								document.setCsv((String)metadadesAddicionals.get("csv"));
							}
						}
					}catch(ArxiuException ae) {
						logger.error("Error Obtenint el document per l'uuid");
					}
				} else if (document.getCsv() != null){
					id = document.getCsv();
					try {
						doc = arxiuGetImprimible(id, false);
						annex.setFicheroAnexado(doc.getContingut());
						annex.setNombreFicheroAnexado(doc.getArxiuNom());
						annex.setCsv(document.getCsv());
						
						annex.setTipoDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO.getValor());
						annex.setOrigenCiudadanoAdmin(0);	
						annex.setFechaCaptura(toXmlGregorianCalendar(new Date()));
					}catch(ArxiuException ae) {
						logger.error("Error Obtenint el document per el csv");
					}
				}
				
				annex.setTipoDocumento(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
				annex.setValidezDocumento(RegistreValidezDocumentDtoEnum.ORIGINAL.getValor());
				
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
				
				//Metadades
				annex.setTipoDocumento(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
				annex.setTipoDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO.getValor());
				annex.setOrigenCiudadanoAdmin(0);	
				annex.setValidezDocumento(RegistreValidezDocumentDtoEnum.ORIGINAL.getValor());
				annex.setFechaCaptura(toXmlGregorianCalendar(new Date()));
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

				//Metadades
				annex.setTipoDocumento(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
				annex.setTipoDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO.getValor());
				annex.setOrigenCiudadanoAdmin(0);	
				annex.setValidezDocumento(RegistreValidezDocumentDtoEnum.ORIGINAL.getValor());
				annex.setFechaCaptura(toXmlGregorianCalendar(new Date()));
				
				path = new File(document.getArxiuNom()).toPath();
			}
			try {
				annex.setTipoMIMEFicheroAnexado(Files.probeContentType(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			annex.setTitulo("Annex 1");
			annex.setModoFirma(0);
			/*Llogica de recerca de document*/
			return annex;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	private RegistreSortida toRegistreSortida(
			NotificacioDtoV2 notificacio,
			List<NotificacioEnviamentDtoV2> enviaments) throws RegistrePluginException {
		RegistreSortida registreSortida = new RegistreSortida();
		DadesOficina dadesOficina = new DadesOficina();
		Llibre llibreOrganisme = null;
		Oficina oficinaVirtual = null;
		String dir3Codi;
		String organisme;
		
		if (notificacio.getEntitat().getDir3CodiReg() != null) {
			dir3Codi = notificacio.getEntitat().getDir3CodiReg();
			organisme = notificacio.getEntitat().getDir3CodiReg();
		} else {
			dir3Codi = notificacio.getEmisorDir3Codi();
			organisme = notificacio.getProcediment().getOrganGestor();
		}
		
		if (notificacio.getProcediment().getOficina() != null) {
			dadesOficina.setOficinaCodi(notificacio.getProcediment().getOficina());
		} else {
			//oficina virtual
			oficinaVirtual = llistarOficinaVirtual(
					dir3Codi, 
					TipusRegistreRegweb3Enum.REGISTRE_SORTIDA);
			
			if (oficinaVirtual != null) {
				dadesOficina.setOficinaCodi(oficinaVirtual.getCodi());
			}
		}
		
		if (notificacio.getProcediment().getLlibre() != null) {
			dadesOficina.setLlibreCodi(notificacio.getProcediment().getLlibre());
		} else {
			if (notificacio.getProcediment().getOrganGestor() != null) {
				llibreOrganisme = llistarLlibreOrganisme(
						dir3Codi,
						notificacio.getProcediment().getOrganGestor());
			}
			if (llibreOrganisme != null) {
				String llibreCodi = llibreOrganisme.getCodi();
				dadesOficina.setLlibreCodi(llibreCodi);
			}
		}
		
		registreSortida.setCodiEntitat(dir3Codi);
		dadesOficina.setOrgan(organisme);
		registreSortida.setDadesOficina(dadesOficina);
		
		
		for(NotificacioEnviamentDtoV2 enviament : enviaments) {
			PersonaDto destinatari = null;
			if(enviament.getDestinataris() != null && enviament.getDestinataris().size() > 0) {
				destinatari =  enviament.getDestinataris().get(0);
			}
			registreSortida.getDadesInteressat().add(personaToDadesInteressatIRepresenat(
					notificacio, 
					enviament.getTitular(),
					destinatari));	
//			registreSortida.setDadesRepresentat(personaToDadesRepresentat(
//					notificacio, 
//					enviament.getTitular(),
//					destinatari));	
		}
		
		DadesAnotacio dadesAnotacio = new DadesAnotacio();
		dadesAnotacio.setIdiomaCodi("ca");
		
		List<TipusAssumpte> tipusAssumpte = llistarTipusAssumpte(dir3Codi);
		
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
					dir3Codi, 
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
		dadesAnotacio.setObservacions("Notib: " + notificacio.getUsuariCodi());
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
			Set<NotificacioEnviamentEntity> enviaments) throws RegistrePluginException {
		AsientoRegistralBeanDto registre = new AsientoRegistralBeanDto();
		registre.setEntidadCodigo(notificacio.getEntitat().getCodi());
		registre.setEntidadDenominacion(notificacio.getEntitat().getNom());
		DadesOficina dadesOficina = new DadesOficina();
		String dir3Codi;
		String organisme = null;
		
		if (notificacio.getEntitat().getDir3CodiReg() != null) {
			dir3Codi = notificacio.getEntitat().getDir3CodiReg();
			organisme = notificacio.getEntitat().getDir3CodiReg();
		} else {
			dir3Codi = notificacio.getEmisorDir3Codi();
			organisme = notificacio.getProcediment().getOrganGestor() != null ? notificacio.getProcediment().getOrganGestor().getCodi() : null;
		}

		try {
			logger.debug("[OFC_VIRTUAL] Recuperant informació de l'oficina i registre...");
			setOficina(
					notificacio,
					dadesOficina,
					dir3Codi);
			setLlibre(
					notificacio, 
					dadesOficina, 
					dir3Codi);
		} catch (RegistrePluginException ex) {
			throw new RegistrePluginException("[OFC_VIRTUAL] No s'han pogut recuperar les dedes de l'oficina o llibre", ex);
		}
		
		if (dadesOficina.getOficinaCodi() != null) {
			//Codi Dir3 de l’oficina inicial
			registre.setEntidadRegistralInicioCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralInicioDenominacion(dadesOficina.getOficinaNom());
			//Codi Dir3 de l’oficina origen (obligatori)
			registre.setEntidadRegistralOrigenCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralOrigenDenominacion(dadesOficina.getOficinaNom());
			//Codi Dir3 de l’oficina destí
			registre.setEntidadRegistralDestinoCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralDestinoDenominacion(dadesOficina.getOficinaNom());
		}
		if (dadesOficina.getLlibreCodi() != null) {
			registre.setLibroCodigo(dadesOficina.getLlibreCodi());
		}
		if (organisme != null) {
			//Codi Dir3 de l’organisme origen
			registre.setUnidadTramitacionOrigenCodigo(organisme);
			registre.setUnidadTramitacionOrigenDenominacion(organisme);
			//Codi Dir3 de l’organisme destí
			registre.setUnidadTramitacionDestinoCodigo(organisme);
			registre.setUnidadTramitacionDestinoDenominacion(organisme);
		}
		
		//Salida
		registre.setTipoRegistro(2L);
		
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
		registre.setObservaciones("Notib: " + notificacio.getUsuariCodi());
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
			NotificacioEnviamentEntity enviament) throws RegistrePluginException {
		AsientoRegistralBeanDto registre = new AsientoRegistralBeanDto();
		registre.setEntidadCodigo(notificacio.getEntitat().getCodi());
		registre.setEntidadDenominacion(notificacio.getEntitat().getNom());
		
		DadesOficina dadesOficina = new DadesOficina();
		String dir3Codi;
		String organisme = null;
		
		if (notificacio.getEntitat().getDir3CodiReg() != null) {
			dir3Codi = notificacio.getEntitat().getDir3CodiReg();
			organisme = notificacio.getEntitat().getDir3CodiReg();
		} else {
			dir3Codi = notificacio.getEmisorDir3Codi();
			organisme = notificacio.getProcediment().getOrganGestor() != null ? notificacio.getProcediment().getOrganGestor().getCodi() : null;
		}

		try {
			logger.debug("[OFC_VIRTUAL] Recuperant informació de l'oficina i registre...");
			setOficina(
					notificacio,
					dadesOficina,
					dir3Codi);
			setLlibre(
					notificacio, 
					dadesOficina, 
					dir3Codi);
		} catch (RegistrePluginException ex) {
			throw new RegistrePluginException("[OFC_VIRTUAL] No s'han pogut recuperar les dedes de l'oficina o llibre", ex);
		}
		
		if (dadesOficina.getOficinaCodi() != null) {
			//Codi Dir3 de l’oficina inicial
			registre.setEntidadRegistralInicioCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralInicioDenominacion(dadesOficina.getOficinaNom());
			//Codi Dir3 de l’oficina origen (obligatori)
			registre.setEntidadRegistralOrigenCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralOrigenDenominacion(dadesOficina.getOficinaNom());
			//Codi Dir3 de l’oficina destí
			registre.setEntidadRegistralDestinoCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralDestinoDenominacion(dadesOficina.getOficinaNom());
		}
		if (dadesOficina.getLlibreCodi() != null) {
			registre.setLibroCodigo(dadesOficina.getLlibreCodi());
		}
		if (organisme != null) {
			//Codi Dir3 de l’organisme origen
			registre.setUnidadTramitacionOrigenCodigo(organisme);
			registre.setUnidadTramitacionOrigenDenominacion(organisme);
			//Codi Dir3 de l’organisme destí
			registre.setUnidadTramitacionDestinoCodigo(organisme);
			registre.setUnidadTramitacionDestinoDenominacion(organisme);
		}
		
		registre.setTipoRegistro(2L);

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
		registre.setObservaciones("Notib: " + notificacio.getUsuariCodi());
		registre.setExpone("");
		registre.setSolicita("");
		registre.setPresencial(false);
		registre.setEstado(notificacio.getEstat().getLongVal());
//		registre.setUnidadTramitacionOrigenCodigo(notificacio.getProcediment().getOrganGestor().getCodi());
//		registre.setUnidadTramitacionOrigenDenominacion(notificacio.getProcediment().getOrganGestor().getCodi());
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
		if (persona.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
			interessat.setDocumentNumero(persona.getDir3Codi());
			interessat.setDocumentTipus(RegistreInteressatDocumentTipusDtoEnum.CODI_ORIGEN);
		} else {
			interessat.setDocumentNumero(persona.getNif());
			if (isDocumentEstranger(persona.getNif()))
				interessat.setDocumentTipus(RegistreInteressatDocumentTipusDtoEnum.DOCUMENT_IDENTIFICACIO_EXTRANGERS);
			else
				interessat.setDocumentTipus(RegistreInteressatDocumentTipusDtoEnum.NIF);
		}
		return interessat;
	}
	
	public InteresadoWsDto personaToInteresadoWs (PersonaEntity persona) {
		InteresadoWsDto interessat = new InteresadoWsDto();
		DatosInteresadoWsDto interessatDades = new DatosInteresadoWsDto();
		interessatDades.setTipoInteresado(persona.getInteressatTipus().getLongVal());
		if (persona.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
			interessatDades.setDocumento(persona.getDir3Codi());
			interessatDades.setTipoDocumentoIdentificacion("O");
		} else if (persona.getInteressatTipus() == InteressatTipusEnumDto.FISICA) {
			interessatDades.setDocumento(persona.getNif());
			if (isDocumentEstranger(persona.getNif()))
				interessatDades.setTipoDocumentoIdentificacion("E");
			else
				interessatDades.setTipoDocumentoIdentificacion("N");
		} else if (persona.getInteressatTipus() == InteressatTipusEnumDto.JURIDICA) {
			interessatDades.setDocumento(persona.getNif());
			interessatDades.setTipoDocumentoIdentificacion("C");
		}
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
	
	public DadesInteressat personaToDadesInteressatIRepresenat (
			NotificacioDtoV2 notificacio, 
			PersonaDto titular,
			PersonaDto destinatari) {
		String dir3Codi;

		if (notificacio.getEntitat().getDir3CodiReg() != null)
			dir3Codi = notificacio.getEntitat().getDir3CodiReg();
		else
			dir3Codi = notificacio.getEmisorDir3Codi();
		DadesInteressat interessatRepresentat = new DadesInteressat();
		Interessat dadesRepresentat = null;
		Interessat dadesInteressat = new Interessat();
		if (titular != null && notificacio != null) {
			dadesInteressat.setNom(titular.getNom());
			dadesInteressat.setEntitatCodi(dir3Codi);
			dadesInteressat.setAutenticat(false);
			if (titular.getInteressatTipus() != null) {
				dadesInteressat.setTipusInteressat(titular.getInteressatTipus().getLongVal());
			}
			if (titular.getInteressatTipus() != null && titular.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
				dadesInteressat.setNif(titular.getDir3Codi());
				dadesInteressat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.CODI_ORIGEN);
			} else if (titular.getInteressatTipus() != null && titular.getInteressatTipus() == InteressatTipusEnumDto.JURIDICA){
				dadesInteressat.setNom(titular.getRaoSocial());
				dadesInteressat.setNif(titular.getNif());
				dadesInteressat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.CIF);
			} else {
				dadesInteressat.setNif(titular.getNif());
				if (isDocumentEstranger(titular.getNif()))
					dadesInteressat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.DOCUMENT_IDENTIFICACIO_EXTRANGERS);
				else
					dadesInteressat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.NIF);
			}
			dadesInteressat.setCognom1(titular.getLlinatge1());
			dadesInteressat.setCognom2(titular.getLlinatge2());
			dadesInteressat.setNomAmbCognoms(titular.getNom() + " " + titular.getLlinatges());
			dadesInteressat.setPaisCodi(null);
			dadesInteressat.setPaisNom(null);
			dadesInteressat.setProvinciaCodi(null);
			dadesInteressat.setProvinciaNom(null);
			dadesInteressat.setMunicipiCodi(null);
			dadesInteressat.setMunicipiNom(null);
		}
		interessatRepresentat.setInteressat(dadesInteressat);
		
		if (destinatari != null && titular.isIncapacitat()) {
			dadesRepresentat = new Interessat();
			dadesRepresentat.setEntitatCodi(dir3Codi);
			dadesRepresentat.setAutenticat(false);
			if (destinatari.getInteressatTipus() != null) {
				dadesRepresentat.setTipusInteressat(destinatari.getInteressatTipus().getLongVal());
			}
			if (destinatari.getInteressatTipus() != null && destinatari.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
				dadesRepresentat.setNif(destinatari.getDir3Codi());
				dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.CODI_ORIGEN);
			} else if (destinatari.getInteressatTipus() != null && destinatari.getInteressatTipus() == InteressatTipusEnumDto.JURIDICA){
				dadesRepresentat.setNif(destinatari.getNif());
				dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.CIF);
			} else {
				dadesRepresentat.setNif(destinatari.getNif());
				if (isDocumentEstranger(titular.getNif()))
					dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.DOCUMENT_IDENTIFICACIO_EXTRANGERS);
				else
					dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.NIF);
			}
			dadesRepresentat.setNom(destinatari.getNom());
			dadesRepresentat.setCognom1(destinatari.getLlinatge1());
			dadesRepresentat.setCognom2(destinatari.getLlinatge2());
			dadesRepresentat.setNomAmbCognoms(destinatari.getNom() + " " + destinatari.getLlinatges());
			dadesRepresentat.setPaisCodi(null);
			dadesRepresentat.setPaisNom(null);
			dadesRepresentat.setProvinciaCodi(null);
			dadesRepresentat.setProvinciaNom(null);
			dadesRepresentat.setMunicipiCodi(null);
			dadesRepresentat.setMunicipiNom(null);
		}
		interessatRepresentat.setRepresentat(dadesRepresentat);
		
		return interessatRepresentat;
	}
	
	@SuppressWarnings("unused")
	private DadesRepresentat personaToDadesRepresentat (
			NotificacioDtoV2 notificacio,
			PersonaDto titular,
			PersonaDto destinatari) {
		String dir3Codi;

		if (notificacio.getEntitat().getDir3CodiReg() != null)
			dir3Codi = notificacio.getEntitat().getDir3CodiReg();
		else
			dir3Codi = notificacio.getEmisorDir3Codi();
		
		DadesRepresentat dadesRepresentat = null;
		if (destinatari != null && titular.isIncapacitat()) {
			dadesRepresentat = new DadesRepresentat();
			dadesRepresentat.setEntitatCodi(dir3Codi);
			dadesRepresentat.setAutenticat(false);
			if (destinatari.getInteressatTipus() != null) {
				dadesRepresentat.setTipusInteressat(destinatari.getInteressatTipus().getLongVal());
			}
			if (destinatari.getInteressatTipus() != null && destinatari.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
				dadesRepresentat.setNif(destinatari.getDir3Codi());
				dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.CODI_ORIGEN);
			} else if (destinatari.getInteressatTipus() != null && destinatari.getInteressatTipus() == InteressatTipusEnumDto.JURIDICA){
				dadesRepresentat.setNif(destinatari.getNif());
				dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.CIF);
			} else {
				dadesRepresentat.setNif(destinatari.getNif());
				if (isDocumentEstranger(destinatari.getNif()))
					dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.DOCUMENT_IDENTIFICACIO_EXTRANGERS);
				else
					dadesRepresentat.setTipusDocumentIdentificacio(RegistreInteressatDocumentTipusDtoEnum.NIF);
			}
			dadesRepresentat.setNom(destinatari.getNom());
			dadesRepresentat.setCognom1(destinatari.getLlinatge1());
			dadesRepresentat.setCognom2(destinatari.getLlinatge2());
			dadesRepresentat.setNomAmbCognoms(destinatari.getNom() + " " + destinatari.getLlinatges());
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
			if (titular.getInteressatTipus() != null)
				interessatDades.setTipoInteresado(titular.getInteressatTipus().getLongVal());
			if (titular.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
				interessatDades.setDocumento(titular.getDir3Codi());
				interessatDades.setTipoDocumentoIdentificacion("O");
			}  else if (titular.getInteressatTipus() == InteressatTipusEnumDto.FISICA) {
				interessatDades.setDocumento(titular.getNif());
				if (isDocumentEstranger(titular.getNif()))
					interessatDades.setTipoDocumentoIdentificacion("E");
				else
					interessatDades.setTipoDocumentoIdentificacion("N");
			} else if (titular.getInteressatTipus() == InteressatTipusEnumDto.JURIDICA) {
				interessatDades.setDocumento(titular.getNif());
				interessatDades.setTipoDocumentoIdentificacion("C");
			}
			interessatDades.setRazonSocial(titular.getNom());
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
			if (destinatari.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
				representantDades.setDocumento(destinatari.getDir3Codi());
				representantDades.setTipoDocumentoIdentificacion("O");
			} else if (destinatari.getInteressatTipus() == InteressatTipusEnumDto.FISICA) {
				representantDades.setDocumento(destinatari.getNif());
				if (isDocumentEstranger(destinatari.getNif()))
					representantDades.setTipoDocumentoIdentificacion("E");
				else
					representantDades.setTipoDocumentoIdentificacion("N");
			} else if (destinatari.getInteressatTipus() == InteressatTipusEnumDto.JURIDICA) {
				representantDades.setDocumento(destinatari.getNif());
				representantDades.setTipoDocumentoIdentificacion("C");
			}
			representantDades.setRazonSocial(destinatari.getNom());
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
	
	private void setOficina(
			NotificacioEntity notificacio,
			DadesOficina dadesOficina,
			String dir3Codi) throws RegistrePluginException {
		Oficina oficinaVirtual;
		if (notificacio.getProcediment().getOficina() != null) {
			dadesOficina.setOficinaCodi(notificacio.getProcediment().getOficina());
			dadesOficina.setOficinaNom(notificacio.getProcediment().getOficina());
		} else {
			//oficina virtual
			oficinaVirtual = llistarOficinaVirtual(
					dir3Codi, 
					TipusRegistreRegweb3Enum.REGISTRE_SORTIDA);
			
			if (oficinaVirtual != null) {
				dadesOficina.setOficinaCodi(oficinaVirtual.getCodi());
				dadesOficina.setOficinaNom(oficinaVirtual.getNom());
			}
		}
	}
	
	private void setLlibre(
			NotificacioEntity notificacio,
			DadesOficina dadesOficina,
			String dir3Codi) throws RegistrePluginException {
		Llibre llibreOrganisme = null;
		if (notificacio.getProcediment().getLlibre() != null) {
			dadesOficina.setLlibreCodi(notificacio.getProcediment().getLlibre());
			dadesOficina.setLlibreNom(notificacio.getProcediment().getLlibre());
		} else {
			if (notificacio.getProcediment().getOrganGestor() != null) {
				llibreOrganisme = llistarLlibreOrganisme(
						dir3Codi,
						notificacio.getProcediment().getOrganGestor().getCodi());
			}
			if (llibreOrganisme != null) {
				dadesOficina.setLlibreCodi(llibreOrganisme.getCodi());
				dadesOficina.setLlibreNom(llibreOrganisme.getNomCurt());
			}
		}
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
	
	private static boolean isDocumentEstranger(String nie) {
		boolean isNie = false;
		if (nie != null && (nie.startsWith("X") || nie.startsWith("Y") || nie.startsWith("Z")))
			isNie = true;
		return isNie;
    }
	
	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
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
							IntegracioHelper.INTCODI_ARXIU,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_ARXIU,
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
	private String getPropertyPluginDadesUsuari() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.class");
	}
	private String getPropertyPluginGestioDocumental() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesdoc.class");
	}
	private String getPropertyPluginRegistre() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.class");
	}
	private String getPropertyPluginArxiu() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.arxiu.class");
	}
	
	
	public int getRegistreReintentsPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.registre.enviaments.periode");
	}
	public int getNotificaReintentsPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.notifica.enviaments.periode");
	}
	public int getConsultaReintentsPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.periode");
	}
	public int getConsultaSirReintentsPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode");
	}
	
	public int getRegistreReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.registre.enviaments.reintents.maxim",
				3);
	}
	public int getNotificaReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.notifica.enviaments.reintents.maxim",
				3);
	}
	public int getConsultaReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim",
				3);
	}
	public int getConsultaSirReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim",
				3);
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
