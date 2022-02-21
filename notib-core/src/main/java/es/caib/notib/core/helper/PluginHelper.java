package es.caib.notib.core.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.EnviamentSirTipusDocumentEnviarEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.exception.DocumentNotFoundException;
import es.caib.notib.plugin.PropertiesHelper;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.notib.plugin.gesconadm.GcaProcediment;
import es.caib.notib.plugin.gesconadm.GcaServei;
import es.caib.notib.plugin.gesconadm.GesconAdm;
import es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPlugin;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.*;
import es.caib.notib.plugin.unitat.*;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	public static final String GESDOC_AGRUPACIO_TEMPORALS = "tmp";
	public static final String GESDOC_AGRUPACIO_MASSIUS_CSV = "massius_csv";
	public static final String GESDOC_AGRUPACIO_MASSIUS_ZIP = "massius_zip";
	public static final String GESDOC_AGRUPACIO_MASSIUS_ERRORS = "massius_errors";
	public static final String GESDOC_AGRUPACIO_MASSIUS_INFORMES = "massius_informes";

	private DadesUsuariPlugin dadesUsuariPlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private RegistrePlugin registrePlugin;
	private IArxiuPlugin arxiuPlugin;
	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;
	private GestorContingutsAdministratiuPlugin gestorDocumentalAdministratiuPlugin;
	private FirmaServidorPlugin firmaServidorPlugin;

	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ConfigHelper configHelper;
	// REGISTRE
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public RespostaConsultaRegistre crearAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb,
			Long tipusOperacio,
			Long notificacioId,
			String enviamentIds,
			boolean generarJustificant) {
		
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
					tipusOperacio,
					generarJustificant);
			
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

	private static Set<String> blockedObtenirJustificant = null;
	private void initObtenirJustificant(){
		blockedObtenirJustificant = new HashSet<>();
		final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		Runnable clearBlockedRunnable = new Runnable() {
			public void run() {
				blockedObtenirJustificant = null;
				exec.shutdown();
			}
		};

		exec.scheduleAtFixedRate(
				clearBlockedRunnable ,
				getSegonsEntreReintentRegistreProperty(),
				getSegonsEntreReintentRegistreProperty(),
				TimeUnit.SECONDS);

	}

	/**
	 * Recupera el justificant.
	 * L'execució del mètode està controlat per la property "es.caib.notib.plugin.registre.segons.entre.peticions"
	 * que impedeix que es facin peticions consecutives amb intervals de temps inferiors al temps expecificat en segons.
	 *
	 * @param codiDir3Entitat	codi DIR3 de l'entitat
	 * @param numeroRegistreFormatat	número de l'assentament que es vol recuperar
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 */
	public RespostaJustificantRecepcio obtenirJustificant(
			String codiDir3Entitat, 
			String numeroRegistreFormatat) {
		if (blockedObtenirJustificant == null){
			initObtenirJustificant();
		}
		if (blockedObtenirJustificant.contains(numeroRegistreFormatat)) {
			return new RespostaJustificantRecepcio();
		}
		blockedObtenirJustificant.add(numeroRegistreFormatat);
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
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
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
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}
		
		return resposta;
	}

//	public RegistreIdDto registreAnotacioSortida(
//			NotificacioDtoV2 notificacio, 
//			List<NotificacioEnviamentDtoV2> enviaments, 
//			Long tipusOperacio) throws Exception {
//		
//		IntegracioInfo info = new IntegracioInfo(
//				IntegracioHelper.INTCODI_REGISTRE, 
//				"Enviament notificació a registre (SIR desactivat)", 
//				IntegracioAccioTipusEnumDto.ENVIAMENT, 
//				new AccioParam("Id de la notificacio", String.valueOf(notificacio.getId())));
//		
//		RegistreIdDto rs = new RegistreIdDto();
//		try {
//			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarSalida(
//					toRegistreSortida(
//							notificacio,
//							enviaments),
//					"notib");
//			if (resposta.getErrorDescripcio() != null) {
//				rs.setDescripcioError(resposta.getErrorDescripcio());
//				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
//			} else {
//				rs.setNumeroRegistreFormat(resposta.getNumeroRegistroFormateado());
//				rs.setData(resposta.getData());
//				integracioHelper.addAccioOk(info);
//			}
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de registre";
//			integracioHelper.addAccioError(info, errorDescripcio, ex);
//			if (ex.getCause() != null) {
//				errorDescripcio += " :" + ex.getCause().getMessage();
//				rs.setDescripcioError(errorDescripcio);
//				return rs;
//			} else {
//				throw new SistemaExternException(
//				IntegracioHelper.INTCODI_REGISTRE,
//				errorDescripcio,
//				ex);
//			}
//		}
//		return rs;
//	}
	
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
	
	public OficinaDto llistarOficinaVirtual(
			String entitatcodi,
			String nomOficinaVirtual,
			TipusRegistreRegweb3Enum autoritzacio) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la oficina virtual", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi),
				new AccioParam("Tipud de registre", autoritzacio.name()));
		OficinaDto oficinaDto = new OficinaDto();
		try {
			Oficina oficina = getRegistrePlugin().llistarOficinaVirtual(
					entitatcodi, 
					nomOficinaVirtual,
					autoritzacio.getValor());
			if (oficina != null) {
				oficinaDto.setCodi(oficina.getCodi());
				oficinaDto.setNom(oficina.getNom());
			}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir la oficina virtual";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}

		return oficinaDto;
	}
	
	public List<OficinaDto> llistarOficines(
			String entitatcodi,
			AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de oficines", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi),
				new AccioParam("Tipud de registre", autoritzacio.name()));
		
		List<OficinaDto> oficinesDto = new ArrayList<OficinaDto>();
		try {
			List<Oficina> oficines = getRegistrePlugin().llistarOficines(
					entitatcodi, 
					autoritzacio.getValor());
			if (oficines != null) {
				for (Oficina oficina : oficines) {
					OficinaDto oficinaDto = new OficinaDto();
					oficinaDto.setCodi(oficina.getCodi());
					oficinaDto.setNom(oficina.getNom());
					oficinesDto.add(oficinaDto);
				}
			}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar les oficines";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}
	
		return oficinesDto;
	}
	
	public List<OficinaDto> oficinesSIRUnitat(String unitatCodi, Map<String, NodeDir3> arbreUnitats) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Obtenir llista de les oficines SIR d'una unitat organitzativa",
												IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", unitatCodi));
		List<OficinaSIR> oficinesTF = null;
		List<OficinaDto> oficinesSIR = null;
		try {
			oficinesTF = getUnitatsOrganitzativesPlugin().oficinesSIRUnitat(unitatCodi, arbreUnitats);
			oficinesSIR = conversioTipusHelper.convertirList(oficinesTF, OficinaDto.class);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar les oficines d'una unitat organitzativa";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
		return oficinesSIR;
	}
	
	public List<OficinaDto> oficinesSIREntitat(String entitatCodi) throws SistemaExternException {

		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Obtenir llista de les oficines SIR d'una entitat",
												IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", entitatCodi));
		List<OficinaSIR> oficinesTF = null;
		List<OficinaDto> oficinesSIR = null;
		try {
			oficinesTF = getUnitatsOrganitzativesPlugin().getOficinesSIREntitat(entitatCodi);
			oficinesSIR = conversioTipusHelper.convertirList(oficinesTF, OficinaDto.class);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar les oficines SIR d'una entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	
		return oficinesSIR;
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
	
	public LlibreDto llistarLlibreOrganisme(
			String entitatCodi,
			String organismeCodi) throws SistemaExternException{
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de llibres per organisme", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatCodi),
				new AccioParam("Codi de l'organisme", organismeCodi));
		LlibreDto llibreDto = new LlibreDto();
		try {
			Llibre llibre = getRegistrePlugin().llistarLlibreOrganisme(
					entitatCodi, 
					organismeCodi);
			if (llibre != null) {
				llibreDto.setCodi(llibre.getCodi());
				llibreDto.setNomCurt(llibre.getNomCurt());
				llibreDto.setNomLlarg(llibre.getNomLlarg());
				llibreDto.setOrganismeCodi(llibre.getOrganisme());
			}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els llibres d'un organisme";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}
		
		return llibreDto;

	}
	
	public List<LlibreDto> llistarLlibres(
			String entitatcodi,
			String oficina,
			AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Obtenir la llista de llibres d'una oficina", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi),
				new AccioParam("Oficina", oficina));
		
		List<LlibreDto> llibresDto = new ArrayList<LlibreDto>();
		try {
			List<Llibre> llibres = getRegistrePlugin().llistarLlibres(
					entitatcodi, 
					oficina, 
					autoritzacio.getValor());
			if (llibres != null) {
				for (Llibre llibre : llibres) {
					LlibreDto llibreDto = new LlibreDto();
					llibreDto.setCodi(llibre.getCodi());
					llibreDto.setNomCurt(llibre.getNomCurt());
					llibreDto.setNomLlarg(llibre.getNomLlarg());
					llibreDto.setOrganismeCodi(llibre.getOrganisme());
					llibresDto.add(llibreDto);
				}
			}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els llibres d'una oficina";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE,
					errorDescripcio,
					ex);
		}

		return llibresDto;
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
			info.addParam("Rols Consultats: ", StringUtils.join(rols, ", "));
			integracioHelper.addAccioOk(info, false);
			return rols;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
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
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
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
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
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
			String versio,
			boolean isUuid) {
		
		return arxiuDocumentConsultar(arxiuUuid, versio, false, isUuid);
	}

	public Document arxiuDocumentConsultar(
			String identificador,
			String versio,
			boolean ambContingut,
			boolean isUuid) throws DocumentNotFoundException{

		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_ARXIU,
				"Consulta d'un document",
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("identificador del document", identificador),
				new AccioParam("Versio", versio));

		try {
			identificador = isUuid ? "uuid:" + identificador : "csv:" + identificador;
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					identificador,
					versio,
					ambContingut);
			integracioHelper.addAccioOk(info);
			return documentDetalls;
		} catch (Exception ex) {
			DocumentNotFoundException ex1 = new DocumentNotFoundException(
					isUuid ? "UUID" : "CSV",
					identificador,
					ex);
			integracioHelper.addAccioError(info, ex1.getMessage(), ex1);
			throw ex1;
		}
	}
	
	public DocumentContingut arxiuGetImprimible(
			String id,
			boolean isUuid) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_ARXIU, 
				"Obtenir versió imprimible d'un document", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Identificador del document", id),
				new AccioParam("Tipus d'identificador", isUuid ? "uuid" : "csv"));
		
		DocumentContingut documentContingut = null;
		try {
			id = isUuid ? "uuid:" + id : "csv:" + id;
			documentContingut = getArxiuPlugin().documentImprimible(id);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut recuperar el document amb " + id;
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
			String errorDescripcio = "Error al accedir al plugin de gestió documental per a obtenir el document amb id: " + (agrupacio != null ? agrupacio + "/" : "") + id;
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	// GESTOR CONTINGUTS ADMINISTRATIU (ROLSAC)
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public List<ProcSerDto> getProcedimentsGda() {
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESCONADM, 
				"Obtenir tots els procediments", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		
		List<ProcSerDto> procediments = new ArrayList<ProcSerDto>();
		try {
			List<GcaProcediment> procs = getGestorDocumentalAdministratiuPlugin().getAllProcediments();
			if (procs != null)
				for (GcaProcediment proc: procs) {
					ProcSerDto dto = new ProcSerDto();
					dto.setCodi(proc.getCodiSIA());
					dto.setNom(proc.getNom());
					dto.setComu(proc.isComu());
					if (proc.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(proc.getUnitatAdministrativacodi());
					}
					procediments.add(dto);
				}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESCONADM,
					errorDescripcio,
					ex);
		}
		
		return procediments;
	}
	
	public int getTotalProcediments(String codiDir3) {
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESCONADM, 
				"Recuperant el total de procediments", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		int totalElements = 0;
		try {
			totalElements = getGestorDocumentalAdministratiuPlugin().getTotalProcediments(codiDir3);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir el número total d'elemetns";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESCONADM,
					errorDescripcio,
					ex);
		}
		
		return totalElements;
	}

	public ProcSerDto getProcSerByCodiSia(String codiSia, boolean isServei) {

		String msg = "Obtenint " + (isServei ? "servei" : "procediment") + " amb codi SIA " + codiSia + " del gestor documental administratiu";
		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM, msg, IntegracioAccioTipusEnumDto.ENVIAMENT);
		try {
			GesconAdm proc = getGestorDocumentalAdministratiuPlugin().getProcSerByCodiSia(codiSia, isServei);
			if (proc == null) {
				return null;
			}
			ProcSerDto procSer = new ProcSerDto();
			procSer.setCodi(proc.getCodiSIA());
			procSer.setNom(proc.getNom());
			procSer.setComu(proc.isComu());
			procSer.setOrganGestor(proc.getUnitatAdministrativacodi());
			integracioHelper.addAccioOk(info);
			return procSer;
		} catch (Exception ex) {
			String errorDescripcio = "Error " + msg.toLowerCase();
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}
	
	public List<ProcSerDto> getProcedimentsGdaByEntitat(
			String codiDir3,
			int numPagina) {
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESCONADM, 
				"Obtenir procediments per entitat", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		
		List<ProcSerDto> procediments = new ArrayList<ProcSerDto>();
		try {
			List<GcaProcediment> procs = getGestorDocumentalAdministratiuPlugin().getProcedimentsByUnitat(
					codiDir3,
					numPagina);
			if (procs != null)
				for (GcaProcediment proc: procs) {
					ProcSerDto dto = new ProcSerDto();
					dto.setCodi(proc.getCodiSIA());
					dto.setNom(proc.getNom());
					dto.setComu(proc.isComu());
					dto.setUltimaActualitzacio(proc.getDataActualitzacio());
					if (proc.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(proc.getUnitatAdministrativacodi());
					}
					procediments.add(dto);
				}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESCONADM,
					errorDescripcio,
					ex);
		}
		
		return procediments;
	}

	public int getTotalServeis(String codiDir3) {
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESCONADM,
				"Recuperant el total de serveis",
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		int totalElements = 0;
		try {
			totalElements = getGestorDocumentalAdministratiuPlugin().getTotalServeis(codiDir3);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir el número total d'elemetns";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESCONADM,
					errorDescripcio,
					ex);
		}

		return totalElements;
	}

	public List<ProcSerDto> getServeisGdaByEntitat(
			String codiDir3,
			int numPagina) {
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_GESCONADM,
				"Obtenir serveis per entitat",
				IntegracioAccioTipusEnumDto.ENVIAMENT);

		List<ProcSerDto> serveis = new ArrayList<>();
		try {
			List<GcaServei> servs = getGestorDocumentalAdministratiuPlugin().getServeisByUnitat(
					codiDir3,
					numPagina);
			if (servs != null)
				for (GcaServei servei: servs) {
					ProcSerDto dto = new ProcSerDto();
					dto.setCodi(servei.getCodiSIA());
					dto.setNom(servei.getNom());
					dto.setComu(servei.isComu());
					dto.setUltimaActualitzacio(servei.getDataActualitzacio());
					if (servei.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(servei.getUnitatAdministrativacodi());
					}
					serveis.add(dto);
				}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESCONADM,
					errorDescripcio,
					ex);
		}

		return serveis;
	}
	
	// UNITATS ORGANITZATIVES
	// /////////////////////////////////////////////////////////////////////////////////////

	public Map<String, NodeDir3> getOrganigramaPerEntitat(String entitatcodi) throws SistemaExternException {
		logger.info("Obtenir l'organigrama per entitat");
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenir organigrama per entitat", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi));

		String protocol = configHelper.getConfig("es.caib.notib.plugin.unitats.dir3.protocol");
		
		Map<String, NodeDir3> organigrama = null;
		String filenameOrgans = getOrganGestorsFile();
		if (filenameOrgans != null && !filenameOrgans.isEmpty()) {
			filenameOrgans = filenameOrgans + "_" + entitatcodi + ".json";
		}
		try {
			if ("SOAP".equalsIgnoreCase(protocol)) {
				logger.info("Obtenir l'organigrama per entitat SOAP");
				organigrama = getUnitatsOrganitzativesPlugin().organigramaPerEntitatWs(entitatcodi, null, null);
			} else {
				logger.info("Obtenir l'organigrama per entitat REST");
				organigrama = getUnitatsOrganitzativesPlugin().organigramaPerEntitat(entitatcodi);
			}
			if (filenameOrgans != null && !filenameOrgans.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.writeValue(new File(filenameOrgans), organigrama);
			}

			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			logger.info("Error al obtenir l'organigrama per entitat");
			String errorDescripcio = "Error al obtenir l'organigrama per entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (filenameOrgans != null && !filenameOrgans.isEmpty()) {
				File file = new File(filenameOrgans);
				if (file.exists()) {
					try {
						ObjectMapper mapper = new ObjectMapper();
						Map<String, Object> map = mapper.readValue(new FileReader(file), Map.class);
						organigrama = new HashMap<>();

						for (Map.Entry<String, Object> entry : map.entrySet()) {
							NodeDir3 node = mapper.convertValue(entry.getValue(), NodeDir3.class);
							organigrama.put(entry.getKey(), node);
						}
						return organigrama;
					} catch (IOException e) {
						logger.info("Error al procesar map l'organigrama per entitat");
						e.printStackTrace();
					}
				}
			}
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
		return organigrama;
	}

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
	
	
	public List<OrganGestorDto> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenir llista de tots els organismes a partir d'un text", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Text de la cerca", codi));

		// Eliminam espais
		codi = codi != null ? codi.trim() : null;
		municipi = municipi != null ? municipi.trim() : null;

		List<NodeDir3> organismesNodeDir3 = null;
		List<OrganGestorDto> organismes = null;
		try {
			if (denominacio != null) {
				denominacio = denominacio.replaceAll(" ", "%20");
			}
			organismesNodeDir3 = getUnitatsOrganitzativesPlugin().cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
			organismes = conversioTipusHelper.convertirList(organismesNodeDir3, OrganGestorDto.class);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar organismes  a partir d'un text";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	
		return organismes;
	}
	
	
	public List<OrganGestorDto> unitatsPerCodi(String codi) throws SistemaExternException {
		return cercaUnitats(codi,null,null,null,null,null,null,null);
	}
	
	
	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenir llista de tots els organismes a partir d'un text", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Text de la cerca", denominacio));

		List<ObjetoDirectorio> organismesDir3 = null;
		List<OrganGestorDto> organismes = null;
		try {
			organismesDir3 = getUnitatsOrganitzativesPlugin().unitatsPerDenominacio(denominacio);
			organismes = conversioTipusHelper.convertirList(organismesDir3, OrganGestorDto.class);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar organismes  a partir d'un text";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	
		return organismes;
	}
	
	
	public List<CodiValor> llistarNivellsAdministracions() throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenint llista dels nivells de les administracions", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		
		List<CodiValor> nivellsAdministracio = null;
		try {
			nivellsAdministracio = getUnitatsOrganitzativesPlugin().nivellsAdministracio();
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els nivells de les administracions";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
		
		return nivellsAdministracio;
	}
	
	public List<CodiValor> llistarComunitatsAutonomes() throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenint llista les comunitats autònomes", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		
		List<CodiValor> comunitatsAutonomes = null;
		try {
			comunitatsAutonomes = getUnitatsOrganitzativesPlugin().comunitatsAutonomes();
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar les comunitats autònomes";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
		
		return comunitatsAutonomes;
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
	
	public List<CodiValor> llistarProvincies(String codiCA) throws SistemaExternException {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS, 
				"Obtenint llista de províncies", 
				IntegracioAccioTipusEnumDto.ENVIAMENT);
		
		List<CodiValor> provincies = null;
		try {
			provincies = getUnitatsOrganitzativesPlugin().provincies(codiCA);
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

	public byte[] firmaServidorFirmar(
			NotificacioEntity notificacio,
			FitxerDto fitxer,
			TipusFirma tipusFirma,
			String motiu,
			String idioma) {
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_FIRMASERV, 
				"Firma en servidor d'un document", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("notificacioId", notificacio.getId().toString()),
				new AccioParam("títol", fitxer.getNom()));
		try {
			byte [] firmaContingut = getFirmaServidorPlugin().firmar(fitxer.getNom(), motiu, fitxer.getContingut(), tipusFirma, idioma);
			integracioHelper.addAccioOk(info);
			return firmaContingut;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de firma en servidor: " + ex.getMessage();
			integracioHelper.addAccioError(
					info,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_FIRMASERV,
					errorDescripcio,
					ex);
		}
	}
	
	//////////////////////////////////////////////
	
//	private DocumentRegistre documentToDocumentRegistreDto (DocumentDto documentDto) throws SistemaExternException {
//		DocumentRegistre document = new DocumentRegistre();
//		
//		if(((documentDto.getUuid() != null && !documentDto.getUuid().isEmpty())
//			|| (documentDto.getCsv() != null && !documentDto.getCsv().isEmpty()))
//			&& (documentDto.getUrl() == null || documentDto.getUrl().isEmpty())
//			&& (documentDto.getContingutBase64() == null || documentDto.getContingutBase64().isEmpty())) {
//			DocumentContingut doc = null;
//			String id = "";
//			if(documentDto.getUuid() != null) {
//				id = documentDto.getUuid();
//				doc = arxiuGetImprimible(id, true);
//				document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
//				Document docDetall = arxiuDocumentConsultar(id, null);
//				if (docDetall.getMetadades() != null) {
//					document.setData(docDetall.getMetadades().getDataCaptura());
//					document.setOrigen(docDetall.getMetadades().getOrigen().ordinal());
//					document.setTipusDocumental(docDetall.getMetadades().getTipusDocumental().toString());
//					
//					//Recuperar csv
//					Map<String, Object> metadadesAddicionals = docDetall.getMetadades().getMetadadesAddicionals();
//					if (metadadesAddicionals != null && metadadesAddicionals.containsKey("csv")) {
//						document.setCsv((String)metadadesAddicionals.get("csv"));
//					}
//				}
//			} else if (documentDto.getCsv() != null) {
//				id = documentDto.getCsv();
//				doc = arxiuGetImprimible(id, false);	
//				document.setModeFirma(RegistreModeFirmaEnum.AUTOFIRMA_SI.getValor());
//				
//				document.setData(new Date());
//				document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
//				document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.getValor());
//				document.setCsv(documentDto.getCsv());
//			}
//			try {
//				if (doc != null) {
//					document.setArxiuNom(doc.getArxiuNom());
//					document.setArxiuContingut(doc.getContingut());
//				}
//				document.setIdiomaCodi("ca");
//				document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
//				
//			} catch(ArxiuException ae) {
//				logger.error("Error Obtenint el document uuid/csv: " + id);
//			}
//		} else if((documentDto.getUrl() != null && !documentDto.getUrl().isEmpty()) 
//				&& (documentDto.getUuid() == null || documentDto.getUuid().isEmpty()) 
//				&& (documentDto.getCsv() == null || documentDto.getCsv().isEmpty()) 
//				&& (documentDto.getContingutBase64() == null || documentDto.getContingutBase64().isEmpty())) {
//			document.setNom(documentDto.getUrl());
//			document.setArxiuNom(documentDto.getArxiuNom());
//			document.setArxiuContingut(getUrlDocumentContent(documentDto.getUrl()));
//			document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
//			document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.getValor());
//			document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
//			document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
//			document.setData(new Date());
//			document.setIdiomaCodi("ca");
//		} else if((documentDto.getArxiuGestdocId() != null && !documentDto.getArxiuGestdocId().isEmpty()) 
//				&& (documentDto.getUrl() == null || documentDto.getUrl().isEmpty())
//				&& (documentDto.getUuid() == null || documentDto.getUuid().isEmpty()) 
//				&& (documentDto.getCsv() == null || documentDto.getCsv().isEmpty())) {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			gestioDocumentalGet(
//					documentDto.getArxiuGestdocId(), 
//					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
//					baos);
//			document.setArxiuContingut(baos.toByteArray());
//			document.setArxiuNom(documentDto.getArxiuNom());
//			document.setTipusDocument(RegistreTipusDocumentEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
//			document.setTipusDocumental(RegistreTipusDocumentalEnum.NOTIFICACIO.getValor());
//			document.setOrigen(RegistreOrigenEnum.ADMINISTRACIO.getValor());
//			document.setModeFirma(RegistreModeFirmaEnum.SENSE_FIRMA.getValor());
//			document.setData(new Date());
//			document.setIdiomaCodi("ca");
//		}
//		return document;
//	}

	public RegistreAnnexDto documentToRegistreAnnexDto (DocumentEntity document) {
		RegistreAnnexDto annex = new RegistreAnnexDto();
		annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
		annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
		annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
		annex.setData(new Date());
		annex.setIdiomaCodi("ca");

		if((document.getUuid() != null || document.getCsv() != null) && document.getUrl() == null && document.getContingutBase64() == null) {
			boolean loadFromArxiu = isReadDocsMetadataFromArxiu() && document.getUuid() != null || document.getCsv() == null;
			DocumentContingut doc;
			if(loadFromArxiu) {
				try {
					annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);

					doc = arxiuGetImprimible(document.getUuid(), true);
					annex.setArxiuContingut(doc.getContingut());
					annex.setArxiuNom(doc.getArxiuNom());
				}catch(ArxiuException ae) {
					logger.error("Error Obtenint el document per l'uuid");
				}

			} else {
				try {
					annex.setModeFirma(RegistreModeFirmaDtoEnum.AUTOFIRMA_SI);

					doc = arxiuGetImprimible(document.getCsv(), false);
					annex.setArxiuContingut(doc.getContingut());
					annex.setArxiuNom(doc.getArxiuNom());
				}catch(ArxiuException ae) {
					logger.error("Error Obtenint el document per csv");
				}
			}

		} else if(document.getUrl() != null && (document.getUuid() == null && document.getCsv() == null) && document.getContingutBase64() == null) {
			annex.setNom(document.getUrl());
			annex.setArxiuNom(document.getArxiuNom());
			annex.setArxiuContingut(getUrlDocumentContent(document.getUrl()));
			annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);

		} else if(document.getContingutBase64() != null && document.getUrl() == null && (document.getUuid() == null && document.getCsv() == null)) {
			annex.setArxiuContingut(document.getContingutBase64().getBytes());
			annex.setArxiuNom(document.getArxiuNom());
			annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);

		}
		/*Llogica de recerca de document*/
		return annex;
	}
	
	private AnexoWsDto documentToAnexoWs(DocumentEntity document, int idx, boolean isComunicacioSir) {
		try {
			if (HibernateHelper.isProxy(document))
				document = HibernateHelper.deproxy(document);
			AnexoWsDto annex = null;
			Path path = null;
			
			boolean enviarCsv = !isComunicacioSir ||
					(isComunicacioSir && (EnviamentSirTipusDocumentEnviarEnumDto.TOT.equals(getEnviamentSirTipusDocumentEnviar()) ||
							EnviamentSirTipusDocumentEnviarEnumDto.CSV.equals(getEnviamentSirTipusDocumentEnviar())));
			
			boolean enviarContingut = !isComunicacioSir ||
					(isComunicacioSir && (EnviamentSirTipusDocumentEnviarEnumDto.TOT.equals(getEnviamentSirTipusDocumentEnviar()) ||
							EnviamentSirTipusDocumentEnviarEnumDto.BINARI.equals(getEnviamentSirTipusDocumentEnviar())));
			
			boolean enviarTipoMIMEFicheroAnexado = Boolean.TRUE;

			// Metadades per defecte (per si no estan emplenades (notificacions antigues)
			Integer origen = document.getOrigen() != null ? document.getOrigen().getValor() : OrigenEnum.ADMINISTRACIO.getValor();
			String validezDocumento = document.getValidesa() != null ? document.getValidesa().getValor() : ValidesaEnum.ORIGINAL.getValor();
			String tipoDocumental = document.getTipoDocumental() != null ? document.getTipoDocumental().getValor() : TipusDocumentalEnum.NOTIFICACIO.getValor();
			Integer modoFirma = document.getModoFirma() != null ? (document.getModoFirma() ? 1 : 0) : 0;

			if((document.getUuid() != null || document.getCsv() != null) && document.getUrl() == null && document.getContingutBase64() == null) {
				annex = new AnexoWsDto();
				String id = "";
				DocumentContingut doc = null;
				Document docDetall = null;
				boolean loadFromArxiu = isReadDocsMetadataFromArxiu() && document.getUuid() != null || document.getCsv() == null;
				if(loadFromArxiu) {
					id = document.getUuid();
					docDetall = arxiuDocumentConsultar(id, null, true, true);
					if (docDetall != null) {
						doc = docDetall.getContingut();
						if (docDetall.getMetadades() != null && enviarCsv) {
							// Recuperar csv
							Map<String, Object> metadadesAddicionals = docDetall.getMetadades().getMetadadesAddicionals();
							if (metadadesAddicionals != null) {
								if (metadadesAddicionals.containsKey("csv"))
									annex.setCsv((String) metadadesAddicionals.get("csv"));
								else if (metadadesAddicionals.containsKey("eni:csv"))
									annex.setCsv((String) metadadesAddicionals.get("eni:csv"));
							}
						}
					}
				} else {
					if (enviarContingut) {
						id = document.getCsv();
						docDetall = arxiuDocumentConsultar(id, null, true, false);
						if (docDetall != null)
							doc = docDetall.getContingut();
					}
					if (enviarCsv)
						annex.setCsv(document.getCsv());
				}
				
				if (enviarContingut) {
					annex.setFicheroAnexado(doc.getContingut());
					annex.setNombreFicheroAnexado(doc.getArxiuNom());
				} else {
					enviarTipoMIMEFicheroAnexado = Boolean.FALSE;
				}
					
				if (docDetall != null && docDetall.getMetadades() != null) {
					if (docDetall.getMetadades().getTipusDocumental() != null) {
						annex.setTipoDocumental(docDetall.getMetadades().getTipusDocumental().toString());
					} else if (docDetall.getMetadades().getTipusDocumentalAddicional() != null) {
						annex.setTipoDocumental(docDetall.getMetadades().getTipusDocumentalAddicional());
					}
					annex.setOrigenCiudadanoAdmin(docDetall.getMetadades().getOrigen().ordinal());
					annex.setFechaCaptura(toXmlGregorianCalendar(docDetall.getMetadades().getDataCaptura()));
					annex.setValidezDocumento(estatElaboracioToValidesa(docDetall.getMetadades().getEstatElaboracio()));
					annex.setModoFirma(getModeFirma(docDetall, doc.getArxiuNom()));
				}
				
				if (enviarTipoMIMEFicheroAnexado) {
					path = new File(doc.getArxiuNom()).toPath();
				}
			}else if(document.getUrl() != null && (document.getUuid() == null && document.getCsv() == null) && document.getContingutBase64() == null) {
				annex = new AnexoWsDto();
				annex.setFicheroAnexado(getUrlDocumentContent(document.getUrl()));
				annex.setNombreFicheroAnexado(FilenameUtils.getName(document.getUrl()));
				
				//Metadades
				annex.setTipoDocumental(tipoDocumental);
				annex.setOrigenCiudadanoAdmin(origen);
				annex.setValidezDocumento(validezDocumento);
				annex.setModoFirma(modoFirma);
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
				annex.setTipoDocumental(tipoDocumental);
				annex.setOrigenCiudadanoAdmin(origen);
				annex.setValidezDocumento(validezDocumento);
				annex.setModoFirma(modoFirma);
				annex.setFechaCaptura(toXmlGregorianCalendar(new Date()));

				path = new File(document.getArxiuNom()).toPath();
			}
			
			if (enviarTipoMIMEFicheroAnexado) {
				try {
					/*  TODO: Revisar perque amb els tests unitaris Files.exists(path) es false en Tomcat
					 *	(Això causa que fallin els tests en Tomcat) */
					annex.setTipoMIMEFicheroAnexado(Files.probeContentType(path));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			annex.setTitulo("Annex " + idx);
			annex.setTipoDocumento(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
			return annex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_REGISTRE, 
					"Error obtenint les dades del document '" + (document != null ? document.getId() : "") + "': " + ex.getMessage(),
					ex.getCause());
		}
	}

	public String estatElaboracioToValidesa(DocumentEstatElaboracio estatElaboracio) {
		if (estatElaboracio == null)
			return ValidesaEnum.ORIGINAL.getValor();	// Valor per defecte
		switch (estatElaboracio) {
			case COPIA_CF:
			case COPIA_DP:
			case COPIA_PR:
				return ValidesaEnum.COPIA_AUTENTICA.getValor();
			case ALTRES:
				return ValidesaEnum.COPIA.getValor();
			case ORIGINAL:
			default:
				return ValidesaEnum.ORIGINAL.getValor();
		}
	}
	public Integer getModeFirma(Document document, String nom) {
		Integer modeFirma = 0;
		if (nom != null && nom.toLowerCase().endsWith("pdf") &&
				(document.getFirmes() != null && !document.getFirmes().isEmpty()))
			modeFirma = 1;
		return modeFirma;
	}

	public byte[] getUrlDocumentContent(String urlPath) throws SistemaExternException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			URL url = new URL(urlPath);

			is = url.openStream();
			byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
			int n;

			while ((n = is.read(byteChunk)) > 0) {
				baos.write(byteChunk, 0, n);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			logger.error("Error al obtenir document de la URL: " + urlPath, e);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, "Error al obtenir document de la URL: " + urlPath);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public AsientoRegistralBeanDto notificacioEnviamentsToAsientoRegistralBean(
			NotificacioEntity notificacio, 
			Set<NotificacioEnviamentEntity> enviaments,
			boolean inclou_documents) throws RegistrePluginException {

		AsientoRegistralBeanDto registre = notificacioToAsientoRegistralBean(notificacio, inclou_documents);
		for(NotificacioEnviamentEntity enviament : enviaments) {
			registre.getInteresados().add(enviamentToRepresentanteEInteresadoWs(enviament));
		}
		return registre;
	}

	public AsientoRegistralBeanDto notificacioToAsientoRegistralBean(
			NotificacioEntity notificacio,
			NotificacioEnviamentEntity enviament,
			boolean inclou_documents,
			boolean isComunicacioSir) throws RegistrePluginException {
		AsientoRegistralBeanDto registre = notificacioToAsientoRegistralBean(notificacio, inclou_documents, isComunicacioSir);
		logger.info("Afegint els interessats ...");
		registre.getInteresados().add(enviamentToRepresentanteEInteresadoWs(enviament));
		logger.info("Interessats afegits correctament");
		return registre;
	}

	private InteresadoWsDto enviamentToRepresentanteEInteresadoWs(NotificacioEnviamentEntity enviament) {
		PersonaEntity destinatari = null;
		if(enviament.getDestinataris() != null && enviament.getDestinataris().size() > 0) {
			destinatari =  enviament.getDestinataris().get(0);
		}
		return personaToRepresentanteEInteresadoWs(enviament.getTitular(), destinatari);
	}
	
	private AsientoRegistralBeanDto notificacioToAsientoRegistralBean(NotificacioEntity notificacio,
			  boolean inclou_documents) throws RegistrePluginException {
		return notificacioToAsientoRegistralBean(notificacio, inclou_documents, false);
	}

	private AsientoRegistralBeanDto notificacioToAsientoRegistralBean(NotificacioEntity notificacio,
																	  boolean inclou_documents, 
																	  boolean isComunicacioSir) throws RegistrePluginException {

		logger.info("Preparant AsientoRegistralBeanDto");
		AsientoRegistralBeanDto registre = new AsientoRegistralBeanDto();
		registre.setEntidadCodigo(notificacio.getEntitat().getDir3Codi());
		registre.setEntidadDenominacion(notificacio.getEntitat().getNom());
		DadesOficina dadesOficina = new DadesOficina();
		String dir3Codi;
		String organisme = null;

		if (notificacio.getNotificaEnviamentNotificaData() != null && notificacio.getEntitat().getDir3CodiReg() != null) {
			dir3Codi = notificacio.getEntitat().getDir3CodiReg();
			organisme = notificacio.getEntitat().getDir3CodiReg();
		} else {
			dir3Codi = notificacio.getEmisorDir3Codi();
			if (notificacio.getProcediment() != null) {
				organisme = notificacio.getProcediment().getOrganGestor() != null ? notificacio.getProcediment().getOrganGestor().getCodi() : null;
			} else {
				organisme = notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null;
			}
		}

		logger.info("Recuperant informació de l'oficina i registre...");
		setOficina(
				notificacio,
				dadesOficina,
				dir3Codi);
		logger.info("Recuperant informació del llibre");
		setLlibre(
				notificacio,
				dadesOficina,
				dir3Codi);

		if (dadesOficina.getOficinaCodi() != null) {
			//Codi Dir3 de l’oficina origen (obligatori)
			registre.setEntidadRegistralOrigenCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralOrigenDenominacion(dadesOficina.getOficinaNom());
			//Codi Dir3 de l’oficina inicial
			//registre.setEntidadRegistralInicioCodigo(dadesOficina.getOficinaCodi());
			//registre.setEntidadRegistralInicioDenominacion(dadesOficina.getOficinaNom());
			//Codi Dir3 de l’oficina destí
			//registre.setEntidadRegistralDestinoCodigo(dadesOficina.getOficinaCodi());
			//registre.setEntidadRegistralDestinoDenominacion(dadesOficina.getOficinaNom());
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
		logger.info("Preparant dades de sortida");
		registre.setTipoRegistro(2L);

		String tipusEnv = NotificaEnviamentTipusEnumDto.NOTIFICACIO == notificacio.getEnviamentTipus() ? "Notificacio" : "Comunicacio";
		registre.setResumen(tipusEnv + " - " + notificacio.getConcepte());
		/* 1 = Documentació adjunta en suport Paper
		 * 2 = Documentació adjunta digitalitzada i complementàriament en paper
		 * 3 = Documentació adjunta digitalitzada */
		registre.setTipoDocumentacionFisicaCodigo(3L);
		if (notificacio.getProcediment() != null) {
			logger.info("Afegint dades del procediment");
			registre.setTipoAsunto(notificacio.getProcediment().getTipusAssumpte());
			registre.setTipoAsuntoDenominacion(notificacio.getProcediment().getTipusAssumpte());
			registre.setCodigoAsunto(notificacio.getProcediment().getCodiAssumpte());
			registre.setCodigoAsuntoDenominacion(notificacio.getProcediment().getCodiAssumpte());
		}
		logger.info("Afegint dades de l'idioma");
		registre.setIdioma(notificacio.getIdioma() != null ? (notificacio.getIdioma().ordinal() + 1) : 1L);
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
//		if(notificacio.getPagadorPostal() != null) {
//			registre.setTipoTransporte("02");
//		}else {
//			registre.setTipoTransporte("07");
//		}
		if (notificacio.getProcediment() != null) {
			logger.info("Afegint codi Sia");
			try {
				registre.setCodigoSia(Long.parseLong(notificacio.getProcediment().getCodi()));
			} catch (NumberFormatException nfe) {}
		}
		logger.info("Afegint dades varies del registre");
		registre.setCodigoUsuario(notificacio.getUsuariCodi());
		registre.setAplicacionTelematica("NOTIB v." + CacheHelper.appVersion);
		registre.setAplicacion("RWE");
		registre.setVersion("3.1");
		registre.setObservaciones("Notib: " + notificacio.getUsuariCodi());
		registre.setExpone("");
		registre.setSolicita("");
		registre.setPresencial(false);
		registre.setEstado(notificacio.getEstat() != null ? notificacio.getEstat().getLongVal() : null);
		registre.setMotivo(notificacio.getDescripcio());
		registre.setInteresados(new ArrayList<InteresadoWsDto>());
		registre.setAnexos(new ArrayList<AnexoWsDto>());

		if (inclou_documents) {
			logger.info("Incloguent documents ...");
			if (notificacio.getDocument() != null) {
				registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument(), 1, isComunicacioSir));
			}
			if (notificacio.getDocument2() != null) {
				registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument2(), 2, isComunicacioSir));
			}
			if (notificacio.getDocument3() != null) {
				registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument3(), 3, isComunicacioSir));
			}
			if (notificacio.getDocument4() != null) {
				registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument4(), 4, isComunicacioSir));
			}
			if (notificacio.getDocument5() != null) {
				registre.getAnexos().add(documentToAnexoWs(notificacio.getDocument5(), 5, isComunicacioSir));
			}
		}
		logger.info("Retornant registre");
		return registre;
	}

	public InteresadoWsDto personaToRepresentanteEInteresadoWs (
			PersonaEntity titular, 
			PersonaEntity destinatari) {
		InteresadoWsDto interessat = new InteresadoWsDto();
		if(titular != null) {
			DatosInteresadoWsDto interessatDades = persona2DatosInteresadoWsDto(titular);
			interessat.setInteresado(interessatDades);
		}
		if(destinatari != null && titular != null && titular.isIncapacitat()) {
			DatosInteresadoWsDto representantDades = persona2DatosInteresadoWsDto(destinatari);
			interessat.setRepresentante(representantDades);	
		}
		return interessat;
	}

	private DatosInteresadoWsDto persona2DatosInteresadoWsDto(PersonaEntity persona) {
		DatosInteresadoWsDto interessatDades = new DatosInteresadoWsDto();
		if (persona.getInteressatTipus() != null)
			interessatDades.setTipoInteresado(persona.getInteressatTipus().getLongVal());
		if (persona.getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
			interessatDades.setDocumento(persona.getDir3Codi() != null ? persona.getDir3Codi().trim() : null);
			interessatDades.setTipoDocumentoIdentificacion("O");
		}  else if (persona.getInteressatTipus() == InteressatTipusEnumDto.FISICA) {
			interessatDades.setDocumento(persona.getNif() != null ? persona.getNif().trim() : null);
			if (isDocumentEstranger(persona.getNif()))
				interessatDades.setTipoDocumentoIdentificacion("E");
			else
				interessatDades.setTipoDocumentoIdentificacion("N");
		} else if (persona.getInteressatTipus() == InteressatTipusEnumDto.JURIDICA) {
			interessatDades.setDocumento(persona.getNif() != null ? persona.getNif().trim() : null);
			interessatDades.setTipoDocumentoIdentificacion("C");
		}
		String raoSocial = persona.getRaoSocial() == null || persona.getRaoSocial().length() <= 80 ?
				persona.getRaoSocial() : persona.getRaoSocial().substring(0, 80);
		String nom = persona.getNom() == null || persona.getNom().length() <= 30 ?
				persona.getNom() : persona.getNom().substring(0, 30);
		interessatDades.setRazonSocial(raoSocial);
		interessatDades.setNombre(nom);
		interessatDades.setApellido1(persona.getLlinatge1());
		interessatDades.setApellido2(persona.getLlinatge2());
		interessatDades.setCodigoDire("");
		interessatDades.setDireccion("");
		interessatDades.setCp("");
		interessatDades.setObservaciones("");
		interessatDades.setEmail(persona.getEmail());
		interessatDades.setDireccionElectronica(persona.getEmail());
		interessatDades.setTelefono(persona.getTelefon());
		return interessatDades;
	}

	public void addOficinaAndLlibreRegistre(NotificacioEntity notificacio){
		DadesOficina dadesOficina = new DadesOficina();
		String dir3Codi;

		if (notificacio.getEntitat().getDir3CodiReg() != null) {
			dir3Codi = notificacio.getEntitat().getDir3CodiReg();
		} else {
			dir3Codi = notificacio.getEmisorDir3Codi();
		}
		try {
			setOficina(
					notificacio,
					dadesOficina,
					dir3Codi);
		} catch (RegistrePluginException e) {}

		try {
			setLlibre(
					notificacio,
					dadesOficina,
					dir3Codi);
		} catch (RegistrePluginException e) {}
	}

	private void setOficina(
			NotificacioEntity notificacio,
			DadesOficina dadesOficina,
			String dir3Codi) throws RegistrePluginException {
		if (notificacio.getEntitat().isOficinaEntitat() && notificacio.getEntitat().getOficina() != null) {
			dadesOficina.setOficinaCodi(notificacio.getEntitat().getOficina());
			dadesOficina.setOficinaNom(notificacio.getEntitat().getOficina());
		} else if (!notificacio.getEntitat().isOficinaEntitat() &&
				notificacio.getOrganGestor() != null && notificacio.getOrganGestor().getOficina() != null) {
			OrganGestorEntity organGestor = notificacio.getOrganGestor();
			dadesOficina.setOficinaCodi(organGestor.getOficina());
			dadesOficina.setOficinaNom(organGestor.getOficinaNom());
		} else {
			OficinaDto oficinaVirtual = llistarOficinaVirtual(
					dir3Codi,
					notificacio.getEntitat().getNomOficinaVirtual(),
					TipusRegistreRegweb3Enum.REGISTRE_SORTIDA);
			
			if (oficinaVirtual != null) {
				dadesOficina.setOficinaCodi(oficinaVirtual.getCodi());
				dadesOficina.setOficinaNom(oficinaVirtual.getNom());
			}
		}
		if (dadesOficina.getOficinaCodi() == null) {
			throw new RegistrePluginException("No hi ha definida cap oficina per realitzar el registre");
		}

		// Associam la oficina amb l'entitat de la notificació
		notificacio.setRegistreOficinaNom(dadesOficina.getOficinaNom());
	}
	
	private void setLlibre(
			NotificacioEntity notificacio,
			DadesOficina dadesOficina,
			String dir3Codi) throws RegistrePluginException {
		
		LlibreDto llibreOrganisme = null;
		if (!notificacio.getEntitat().isLlibreEntitat()) {
			if (notificacio.getProcediment() != null && notificacio.getProcediment().getOrganGestor().getLlibre() != null) {
				dadesOficina.setLlibreCodi(notificacio.getProcediment().getOrganGestor().getLlibre());
				dadesOficina.setLlibreNom(notificacio.getProcediment().getOrganGestor().getLlibreNom());
			} else {
				String organGestor = null;
				if (notificacio.getProcediment() != null) {
					organGestor = notificacio.getProcediment().getOrganGestor().getCodi();
				} else if (notificacio.getOrganGestor() != null) {
					organGestor = notificacio.getOrganGestor().getCodi();
				}
				if (organGestor != null) {
					llibreOrganisme = llistarLlibreOrganisme(
							dir3Codi,
							organGestor);
					if (llibreOrganisme != null) {
						dadesOficina.setLlibreCodi(llibreOrganisme.getCodi());
						dadesOficina.setLlibreNom(llibreOrganisme.getNomCurt());
					}
				}
			}
		} else {
			if (notificacio.getEntitat().getLlibre() != null) {
				dadesOficina.setLlibreCodi(notificacio.getEntitat().getLlibre());
				dadesOficina.setLlibreNom(notificacio.getEntitat().getLlibreNom());
			} else {
				llibreOrganisme = llistarLlibreOrganisme(
						dir3Codi,
						dir3Codi);
				if (llibreOrganisme != null) {
					dadesOficina.setLlibreCodi(llibreOrganisme.getCodi());
					dadesOficina.setLlibreNom(llibreOrganisme.getNomCurt());
				}
			}
		}
		if (dadesOficina.getLlibreCodi() == null) {
			throw new RegistrePluginException("No hi ha definit cap llibre per realitzar el registre");
		}

		// Associam el llibre amb l'entitat de la notificació
		notificacio.setRegistreLlibreNom(dadesOficina.getLlibreNom());
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

	private DadesUsuariPlugin getDadesUsuariPlugin() {
		loadPluginProperties("USUARIS");
		if (dadesUsuariPlugin == null) {
			String pluginClass = getPropertyPluginDadesUsuari();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					dadesUsuariPlugin = (DadesUsuariPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("Error al crear la instància del plugin de dades d'usuari (" + pluginClass + "): ", ex);
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_USUARIS,
							"Error al crear la instància del plugin de dades d'usuari",
							ex);
				}
			} else {
				logger.error("La classe del plugin d'usuari no està definida");
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de dades d'usuari no està configurada");
			}
		}
		return dadesUsuariPlugin;
	}

	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		loadPluginProperties("GES_DOC");
		if (gestioDocumentalPlugin == null) {
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("Error al crear la instància del plugin de gestió documental (" + pluginClass + "): ", ex);
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de gestió documental",
							ex);
				}
			} else {
				logger.error("La classe del plugin de gestió documental no està definida");
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de gestió documental no està configurada");
			}
		}
		return gestioDocumentalPlugin;
	}

	private RegistrePlugin getRegistrePlugin() {
		loadPluginProperties("REGISTRE");
		if (registrePlugin == null) {
			String pluginClass = getPropertyPluginRegistre();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("Error al crear la instància del plugin de registre (" + pluginClass + "): ", ex);
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_REGISTRE,
							"Error al crear la instància del plugin de registre",
							ex);
				}
			} else {
				logger.error("La classe del plugin de registre no està definida");
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE,
						"La classe del plugin de registre no està configurada");
			}
		}
		return registrePlugin;
	}
	
	private IArxiuPlugin getArxiuPlugin() {
		loadPluginProperties("ARXIU");
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (ConfigHelper.JBossPropertiesHelper.getProperties().isLlegirSystem()) {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.notib.");
					} else {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"es.caib.notib.",
								ConfigHelper.JBossPropertiesHelper.getProperties().findAll());
					}
				} catch (Exception ex) {
					logger.error("Error al crear la instància del plugin d'arxiu digital (" + pluginClass + "): ", ex);
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_ARXIU,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				logger.error("La classe del plugin d'arxiu digital no està definida");
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_ARXIU,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}

	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() {
		loadPluginProperties("DIR3");
		if (unitatsOrganitzativesPlugin == null) {
			String pluginClass = getPropertyPluginUnitats();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					unitatsOrganitzativesPlugin = (UnitatsOrganitzativesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("Error al crear la instància del plugin de DIR3 (" + pluginClass + "): ", ex);
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_REGISTRE,
							"Error al crear la instància del plugin de DIR3",
							ex);
				}
			} else {
				logger.error("La classe del plugin de DIR3 no està configurada");
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE,
						"La classe del plugin de DIR3 no està configurada");
			}
		}
		
		return unitatsOrganitzativesPlugin;
	}
	
	private GestorContingutsAdministratiuPlugin getGestorDocumentalAdministratiuPlugin() {
		loadPluginProperties("GESCONADM");
		if (gestorDocumentalAdministratiuPlugin == null) {
			String pluginClass = getPropertyPluginGestorDocumentalAdministratu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestorDocumentalAdministratiuPlugin = (GestorContingutsAdministratiuPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("Error al crear la instància del plugin de gestor documental administratiu (" + pluginClass + "): ", ex);
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESCONADM,
							"Error al crear la instància del plugin de gestor documental administratiu",
							ex);
				}
			} else {
				logger.error("La classe del plugin del gestor documental administratiu no està configurada");
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_GESCONADM,
						"La classe del plugin del gestor documental administratiu no està configurada");
			}
		}
		
		return gestorDocumentalAdministratiuPlugin;
	}
	private FirmaServidorPlugin getFirmaServidorPlugin() {
		loadPluginProperties("FIRMA");
		if (firmaServidorPlugin != null) {
			return firmaServidorPlugin;
		}
		String pluginClass = getPropertyPluginFirmaServidor();
//		String pluginClass = configHelper.getConfig("es.caib.notib.plugin.signatura.class");;
		if (pluginClass == null || pluginClass.length() == 0) {
			String error = "No està configurada la classe per al plugin de firma en servidor";
			logger.error(error);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, error);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			firmaServidorPlugin = (FirmaServidorPlugin)clazz.newInstance();
//			signaturaPlugin = (SignaturaPlugin)clazz.newInstance();
			return firmaServidorPlugin;
		} catch (Exception ex) {
			String error = "Error al crear la instància del plugin de firma en servidor" ;
			logger.error(error + " (" + pluginClass + "): ", ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, error, ex);
		}
	}

	private final static Map<String, Boolean> propertiesLoaded = new HashMap<>();
	private synchronized void loadPluginProperties(String codeProperties) {
		if (!propertiesLoaded.containsKey(codeProperties) || !propertiesLoaded.get(codeProperties)) {
			propertiesLoaded.put(codeProperties, true);
			Map<String, String> pluginProps = configHelper.getGroupProperties(codeProperties);
			for (Map.Entry<String, String> entry : pluginProps.entrySet() ) {
				String value = entry.getValue() == null ? "" : entry.getValue();
				PropertiesHelper.getProperties().setProperty(entry.getKey(), value);
			}
		}
	}

	/**
	 * Esborra les properties del grup indicat per paràmetre de la memòria.
	 *
	 * @param codeProperties Codi del grup de propietats que vols esborrar de memòria.
	 */
	public void reloadProperties(String codeProperties) {
		if (propertiesLoaded.containsKey(codeProperties))
			propertiesLoaded.put(codeProperties, false);
	}
	public void resetPlugins() {
		registrePlugin = null;
		gestorDocumentalAdministratiuPlugin = null;
		dadesUsuariPlugin = null;
		unitatsOrganitzativesPlugin = null;
		arxiuPlugin = null;
		gestioDocumentalPlugin = null;
		firmaServidorPlugin = null;
	}
	private String getPropertyPluginUnitats() {
		return configHelper.getConfig("es.caib.notib.plugin.unitats.class");
	}
	private String getPropertyPluginDadesUsuari() {
		return configHelper.getConfig("es.caib.notib.plugin.dades.usuari.class");
	}
	private String getPropertyPluginGestioDocumental() {
		return configHelper.getConfig("es.caib.notib.plugin.gesdoc.class");
	}
	private String getPropertyPluginRegistre() {
		return configHelper.getConfig("es.caib.notib.plugin.registre.class");
	}
	private String getPropertyPluginArxiu() {
		return configHelper.getConfig("es.caib.notib.plugin.arxiu.class");
	}
	private String getPropertyPluginGestorDocumentalAdministratu() {
		return configHelper.getConfig("es.caib.notib.plugin.gesconadm.class");
	}
	private String getPropertyPluginFirmaServidor() {
		return configHelper.getConfig("es.caib.notib.plugin.firmaservidor.class");
	}
	public int getRegistreReintentsPeriodeProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.registre.enviaments.periode");
	}
	public int getNotificaReintentsPeriodeProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.notifica.enviaments.periode");
	}
	public int getConsultaReintentsPeriodeProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.periode");
	}
	public int getConsultaSirReintentsPeriodeProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode");
	}
	public int getSegonsEntreReintentRegistreProperty() {
		return configHelper.getAsInt("es.caib.notib.plugin.registre.segons.entre.peticions");
	}
	public int getRegistreReintentsMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.registre.enviaments.reintents.maxim");
	}
	public int getNotificaReintentsMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.notifica.enviaments.reintents.maxim");
	}
	public int getConsultaReintentsMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim");
	}
	public int getConsultaReintentsDEHMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.deh.reintents.maxim");
	}
	public int getConsultaReintentsCIEMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.cie.reintents.maxim");
	}
	public int getConsultaSirReintentsMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim");
	}
	public String getOrganGestorsFile() {
		return configHelper.getConfig("es.caib.notib.plugin.unitats.fitxer");
	}

	public NotificacioComunicacioTipusEnumDto getNotibTipusComunicacioDefecte() {
		NotificacioComunicacioTipusEnumDto tipus = NotificacioComunicacioTipusEnumDto.SINCRON;
		
		try {
			String tipusStr = configHelper.getConfig("es.caib.notib.comunicacio.tipus.defecte");
			if (tipusStr != null && !tipusStr.isEmpty())
				tipus = NotificacioComunicacioTipusEnumDto.valueOf(tipusStr);
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el tipus de comunicació per defecte. S'utilitzarà el tipus SINCRON.");
		}
				
		return tipus;
	}

	public void setDadesUsuariPlugin(DadesUsuariPlugin dadesUsuariPlugin) {
		this.dadesUsuariPlugin = dadesUsuariPlugin;
	}
	
	public void setGestioDocumentalPlugin(GestioDocumentalPlugin gestioDocumentalPlugin) {
		this.gestioDocumentalPlugin = gestioDocumentalPlugin;
	}
	
	public void setRegistrePlugin(RegistrePlugin registrePlugin) {
		this.registrePlugin = registrePlugin;
	}
	
	public void setArxiuPlugin(IArxiuPlugin arxiuPlugin) {
		this.arxiuPlugin = arxiuPlugin;
	}
	
	public void setUnitatsOrganitzativesPlugin(UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin) {
		this.unitatsOrganitzativesPlugin = unitatsOrganitzativesPlugin;
	}

	private boolean isReadDocsMetadataFromArxiu() {
		return configHelper.getAsBoolean(
				"es.caib.notib.documents.metadades.from.arxiu");
	}

	private static boolean isDocumentEstranger(String nie) {
		if (nie == null) {
			return false;
		}
		String aux = nie.toUpperCase();
		return aux.startsWith("X") || aux.startsWith("Y") || aux.startsWith("Z");
	}

	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}
	
	private EnviamentSirTipusDocumentEnviarEnumDto getEnviamentSirTipusDocumentEnviar() {
		EnviamentSirTipusDocumentEnviarEnumDto tipus = EnviamentSirTipusDocumentEnviarEnumDto.TOT;
		
		try {
			String tipusStr = configHelper.getConfig("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar");
			if (tipusStr != null && !tipusStr.isEmpty())
				tipus = EnviamentSirTipusDocumentEnviarEnumDto.valueOf(tipusStr);
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el tipus de document a enviar per a l'enviament SIR per defecte. S'utilitzarà el tipus TOT (CSV i binari).");
		}
				
		return tipus;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

}
