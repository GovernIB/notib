package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import com.itextpdf.text.pdf.PdfReader;
import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.exception.DocumentNotFoundException;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.AnexoWsDto;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.DatosInteresadoWsDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.InteresadoWsDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.RegistreAnnexDto;
import es.caib.notib.logic.intf.dto.RegistreModeFirmaDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreOrigenDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreTipusDocumentDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreTipusDocumentalDtoEnum;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.notificacio.EnviamentSirTipusDocumentEnviarEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.carpeta.CarpetaPlugin;
import es.caib.notib.plugin.carpeta.MissatgeCarpetaParams;
import es.caib.notib.plugin.carpeta.VincleInteressat;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPlugin;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.DadesOficina;
import es.caib.notib.plugin.registre.Llibre;
import es.caib.notib.plugin.registre.LlibreOficina;
import es.caib.notib.plugin.registre.Organisme;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.registre.TipusAssumpte;
import es.caib.notib.plugin.registre.TipusRegistreRegweb3Enum;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
import es.caib.notib.plugin.unitat.OficinaSir;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
	private Map<String, GestioDocumentalPlugin> gestioDocumentalPlugin = new HashMap<>();
	private Map<String, RegistrePlugin> registrePlugin = new HashMap<>();
	private Map<String, IArxiuPlugin> arxiuPlugin = new HashMap<>();
	private Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugin = new HashMap<>();
	private Map<String, GestorContingutsAdministratiuPlugin> gestorDocumentalAdministratiuPlugin = new HashMap<>();
	private Map<String, FirmaServidorPlugin> firmaServidorPlugin = new HashMap<>();
	private Map<String, IValidateSignaturePlugin> validaSignaturaPlugins = new HashMap<>();
	private Map<String, CarpetaPlugin> carpetaPlugin = new HashMap<>();

	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper eventHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Resource
	private MessageHelper messageManager;
	@Resource
	private EntitatRepository entitatRepository;

	private static Set<String> blockedObtenirJustificant = null;


	// REGISTRE
	// /////////////////////////////////////////////////////////////////////////////////////

	public RespostaConsultaRegistre crearAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio,
														  Long notificacioId, String enviamentIds, boolean generarJustificant) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Enviament notificació a registre (SIR activat)", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Id de la notificacio", String.valueOf(notificacioId)),
				new AccioParam("Ids dels enviaments", enviamentIds),
				new AccioParam("Tipus d'operacio", String.valueOf(tipusOperacio)));

		var resposta = new RespostaConsultaRegistre();
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			resposta = getRegistrePlugin(entitat.getCodi()).salidaAsientoRegistral(codiDir3Entitat, arb, tipusOperacio, generarJustificant);
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

	public RespostaConsultaRegistre obtenerAsientoRegistral(String codiDir3Entitat, String numeroRegistreFormatat, Long tipusRegistre,  boolean ambAnnexos) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Consulta de assentament registral SIR", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat),
				new AccioParam("Tipus de registre", String.valueOf(tipusRegistre)),
				new AccioParam("Amb annexos?", String.valueOf(ambAnnexos)));
		var resposta = new RespostaConsultaRegistre();
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			resposta = getRegistrePlugin(entitat.getCodi()).obtenerAsientoRegistral(codiDir3Entitat, numeroRegistreFormatat, tipusRegistre, ambAnnexos);
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

	private void initObtenirJustificant(){

		blockedObtenirJustificant = new HashSet<>();
		final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		Runnable clearBlockedRunnable = () -> {
			blockedObtenirJustificant = null;
			exec.shutdown();
		};
		exec.scheduleAtFixedRate(clearBlockedRunnable, getSegonsEntreReintentRegistreProperty(), getSegonsEntreReintentRegistreProperty(), TimeUnit.SECONDS);
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
	public RespostaJustificantRecepcio obtenirJustificant(String codiDir3Entitat, String numeroRegistreFormatat) {

		if (blockedObtenirJustificant == null){
			initObtenirJustificant();
		}
		if (blockedObtenirJustificant.contains(numeroRegistreFormatat)) {
			return new RespostaJustificantRecepcio();
		}
		blockedObtenirJustificant.add(numeroRegistreFormatat);
		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir justificant de registre", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat));

		var resposta = new RespostaJustificantRecepcio();
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			resposta = getRegistrePlugin(entitat.getCodi()).obtenerJustificante(codiDir3Entitat, numeroRegistreFormatat,2);
			if (resposta.getErrorCodi() == null) {
				integracioHelper.addAccioOk(info);
			} else {
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
			}
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
			}
			resposta.setErrorDescripcio(errorDescripcio);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
		return resposta;
	}
	
	public RespostaJustificantRecepcio obtenirOficiExtern(String codiDir3Entitat, String numeroRegistreFormatat) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir ofici extern", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat));

		var resposta = new RespostaJustificantRecepcio();
		try {
			EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			resposta = getRegistrePlugin(entitat.getCodi()).obtenerOficioExterno(codiDir3Entitat, numeroRegistreFormatat);
			if (resposta.getErrorCodi() == null) {
				integracioHelper.addAccioOk(info);
			} else {
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
			}
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (ex.getCause() != null) {
				errorDescripcio += " :" + ex.getCause().getMessage();
			}
			resposta.setErrorDescripcio(errorDescripcio);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
		return resposta;
	}
	
	public List<TipusAssumpte> llistarTipusAssumpte(String codiDir3Entitat) throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir llista de tipus d'assumpte", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat));
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var tipusAssumptes = getRegistrePlugin(entitat.getCodi()).llistarTipusAssumpte(codiDir3Entitat);
			integracioHelper.addAccioOk(info);
			return tipusAssumptes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els tipus d'assumpte";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}

	public List<CodiAssumpte> llistarCodisAssumpte(String codiDir3Entitat, String tipusAssumpte) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir la llista de codis d'assumpte", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Tipus d'assumpte", tipusAssumpte));
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var assumptes = getRegistrePlugin(entitat.getCodi()).llistarCodisAssumpte(codiDir3Entitat, tipusAssumpte);
			integracioHelper.addAccioOk(info);
			return assumptes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els codis d'assumpte";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}
	
	public OficinaDto llistarOficinaVirtual(String codiDir3Entitat, String nomOficinaVirtual, TipusRegistreRegweb3Enum autoritzacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir la oficina virtual", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Tipus de registre", autoritzacio.name()));

		try {
			var oficinaDto = new OficinaDto();
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var oficina = getRegistrePlugin(entitat.getCodi()).llistarOficinaVirtual(codiDir3Entitat, nomOficinaVirtual, autoritzacio.getValor());
			if (oficina != null) {
				oficinaDto.setCodi(oficina.getCodi());
				oficinaDto.setNom(oficina.getNom());
			}
			integracioHelper.addAccioOk(info);
			return oficinaDto;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir la oficina virtual";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}
	
	public List<OficinaDto> llistarOficines(String codiDir3Entitat, AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir la llista de oficines", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Tipud de registre", autoritzacio.name()));

		try {
			List<OficinaDto> oficinesDto = new ArrayList<>();
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var oficines = getRegistrePlugin(entitat.getCodi()).llistarOficines(codiDir3Entitat, autoritzacio.getValor());
			if (oficines != null) {
				OficinaDto oficinaDto;
				for (var oficina : oficines) {
					oficinaDto = new OficinaDto();
					oficinaDto.setCodi(oficina.getCodi());
					oficinaDto.setNom(oficina.getNom());
					oficinesDto.add(oficinaDto);
				}
			}
			integracioHelper.addAccioOk(info);
			return oficinesDto;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar les oficines";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}
	
	public List<OficinaDto> oficinesSIRUnitat(String unitatCodi, Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Obtenir llista de les oficines SIR d'una unitat organitzativa",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", unitatCodi));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var oficinesTF = getUnitatsOrganitzativesPlugin().oficinesSIRUnitat(unitatCodi, arbreUnitats);
			var oficinesSIR = oficinesTF.stream().map(o -> toOficinaDto(o)).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return oficinesSIR;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar les oficines d'una unitat organitzativa";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}
	
	public List<OficinaDto> oficinesEntitat(String codiDir3Entitat) throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Obtenir llista de les oficines SIR d'una entitat",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", codiDir3Entitat));
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var oficinesTF = getUnitatsOrganitzativesPlugin().getOficinesEntitat(codiDir3Entitat);
			var oficinesSIR = oficinesTF.stream().map(this::toOficinaDto).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return oficinesSIR;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar les oficines SIR d'una entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	private OficinaDto toOficinaDto(OficinaSir oficinaSir) {
		return OficinaDto.builder().codi(oficinaSir.getCodi()).nom(oficinaSir.getNom()).organCodi(oficinaSir.getOrganCodi()).sir(oficinaSir.isSir()).build();
	}
	
	public List<LlibreOficina> llistarLlibresOficines(String codiDir3Entitat, String usuariCodi, TipusRegistreRegweb3Enum tipusRegistre) throws SistemaExternException{
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir la llista de llibre amb oficina", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Codi de l'usuari", usuariCodi),
				new AccioParam("Tipud de registre", tipusRegistre.name()));
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var llibresOficines = getRegistrePlugin(entitat.getCodi()).llistarLlibresOficines(codiDir3Entitat, usuariCodi, tipusRegistre.getValor());
			integracioHelper.addAccioOk(info);
			return llibresOficines;
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els llibres amb oficina";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}
	
	public LlibreDto llistarLlibreOrganisme(String codiDir3Entitat, String organismeCodi) throws SistemaExternException{
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir la llista de llibres per organisme", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Codi de l'organisme", organismeCodi));

		try {
			var llibreDto = new LlibreDto();
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat + "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var llibre = getRegistrePlugin(entitat.getCodi()).llistarLlibreOrganisme(codiDir3Entitat, organismeCodi);
			if (llibre != null) {
				llibreDto.setCodi(llibre.getCodi());
				llibreDto.setNomCurt(llibre.getNomCurt());
				llibreDto.setNomLlarg(llibre.getNomLlarg());
				llibreDto.setOrganismeCodi(llibre.getOrganisme());
			}
			integracioHelper.addAccioOk(info);
			return llibreDto;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els llibres d'un organisme";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}
	
	public List<LlibreDto> llistarLlibres(String codiDir3Entitat, String oficina, AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir la llista de llibres d'una oficina", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Oficina", oficina));

		try {
			List<LlibreDto> llibresDto = new ArrayList<>();
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var llibres = getRegistrePlugin(entitat.getCodi()).llistarLlibres(codiDir3Entitat, oficina, autoritzacio.getValor());
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
			return llibresDto;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els llibres d'una oficina";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}
	
	public List<Organisme> llistarOrganismes(String codiDir3Entitat) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, "Obtenir llista d'organismes", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat));
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var organismes = getRegistrePlugin(entitat.getCodi()).llistarOrganismes(codiDir3Entitat);
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar organismes";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, errorDescripcio, ex);
		}
	}
	
	// USUARIS
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public List<String> consultarRolsAmbCodi(String usuariCodi) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_USUARIS,"Consulta rols usuari amb codi",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Codi d'usuari", usuariCodi));
		try {
			var rols = getDadesUsuariPlugin().consultarRolsAmbCodi(usuariCodi);
			info.addParam("Rols Consultats: ", StringUtils.join(rols, ", "));
			integracioHelper.addAccioOk(info, false);
			return rols;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}
	
	public DadesUsuari dadesUsuariConsultarAmbCodi(String usuariCodi) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_USUARIS,"Consulta d'usuari amb codi", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi d'usuari", usuariCodi));
		try {
			var dadesUsuari = getDadesUsuariPlugin().consultarAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}
	
	public List<DadesUsuari> dadesUsuariConsultarAmbGrup(String grupCodi) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_USUARIS,"Consulta d'usuaris d'un grup", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi de grup", grupCodi));
		try {
			var dadesUsuari = getDadesUsuariPlugin().consultarAmbGrup(grupCodi);
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, errorDescripcio, ex);
		}
	}

	// ARXIU 
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public Document arxiuDocumentConsultar(String arxiuUuid, String versio, boolean isUuid) {
		return arxiuDocumentConsultar(arxiuUuid, versio, false, isUuid);
	}

	public Document arxiuDocumentConsultar(String identificador, String versio, boolean ambContingut, boolean isUuid) throws DocumentNotFoundException {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_ARXIU, "Consulta d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("identificador del document", identificador),
				new AccioParam("Versio", versio));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			identificador = isUuid ? "uuid:" + identificador : "csv:" + identificador;
			var documentDetalls = getArxiuPlugin().documentDetalls(identificador, versio, ambContingut);
			integracioHelper.addAccioOk(info);
			return documentDetalls;
		} catch (Exception ex) {
			var ex1 = new DocumentNotFoundException(isUuid ? "UUID" : "CSV", identificador, ex);
			integracioHelper.addAccioError(info, ex1.getMessage(), ex1);
			throw ex1;
		}
	}
	
	public DocumentContingut arxiuGetImprimible(String id, boolean isUuid) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_ARXIU, "Obtenir versió imprimible d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador del document", id),
				new AccioParam("Tipus d'identificador", isUuid ? "uuid" : "csv"));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			id = isUuid ? "uuid:" + id : "csv:" + id;
			var documentContingut = getArxiuPlugin().documentImprimible(id);
			integracioHelper.addAccioOk(info);
			return documentContingut;
		} catch (Exception ex) {
			var errorDescripcio = "No s'ha pogut recuperar el document amb " + id;
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, errorDescripcio, ex);
		}
	}
	
	
	// GESTOR DOCUMENTAL
	// /////////////////////////////////////////////////////////////////////////////////////

	@Synchronized
	public String gestioDocumentalCreate(String agrupacio, byte[] contingut) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESDOC, "Creació d'un arxiu", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Agrupacio", agrupacio),
				new AccioParam("Núm bytes", (contingut != null) ? Integer.toString(contingut.length) : "0"));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var gestioDocumentalId = getGestioDocumentalPlugin().create(agrupacio, new ByteArrayInputStream(contingut));
			info.getParams().add(new AccioParam("Id retornat", gestioDocumentalId));
			integracioHelper.addAccioOk(info);
			return gestioDocumentalId;
		} catch (Exception ex) {
			var errorDescripcio = "Error al crear document a dins la gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}

	@Synchronized
	public void gestioDocumentalUpdate(String id, String agrupacio, byte[] contingut) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESDOC, "Modificació d'un arxiu", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Id del document", id),
				new AccioParam("Agrupacio", agrupacio),
				new AccioParam("Núm bytes", (contingut != null) ? Integer.toString(contingut.length) : "0"));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			getGestioDocumentalPlugin().update(id, agrupacio, new ByteArrayInputStream(contingut));
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}
	
	public void gestioDocumentalDelete(String id, String agrupacio) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESDOC,"Eliminació d'un arxiu", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Id del document", id), new AccioParam("Agrupacio", agrupacio));
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			getGestioDocumentalPlugin().delete(id, agrupacio);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}
	
	public void gestioDocumentalGet(String id, String agrupacio, OutputStream contingutOut) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESDOC, "Consultant arxiu de la gestió documental", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Id del document", id),
				new AccioParam("Agrupacio", agrupacio));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			getGestioDocumentalPlugin().get(id, agrupacio, contingutOut);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de gestió documental per a obtenir el document amb id: " + (agrupacio != null ? agrupacio + "/" : "") + id;
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, errorDescripcio, ex);
		}
	}
	
	// GESTOR CONTINGUTS ADMINISTRATIU (ROLSAC)
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public List<ProcSerDto> getProcedimentsGda() {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM,"Obtenir tots els procediments", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
		List<ProcSerDto> procediments = new ArrayList<>();
			var procs = getGestorDocumentalAdministratiuPlugin().getAllProcediments();
			if (procs != null) {
				ProcSerDto dto;
				for (var proc : procs) {
					dto = new ProcSerDto();
					dto.setCodi(proc.getCodiSIA());
					dto.setNom(proc.getNom());
					dto.setComu(proc.isComu());
					if (proc.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(proc.getUnitatAdministrativacodi());
					}
					procediments.add(dto);
				}
			}
			integracioHelper.addAccioOk(info);
			return procediments;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}
	
	public int getTotalProcediments(String codiDir3Entitat) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM,"Recuperant el total de procediments", IntegracioAccioTipusEnumDto.ENVIAMENT);
		try {
			var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat + "no trobada");
			}
			info.setCodiEntitat(entitat.getCodi());
			var totalElements = getGestorDocumentalAdministratiuPlugin().getTotalProcediments(codiDir3Entitat);
			integracioHelper.addAccioOk(info);
			return totalElements;
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir el número total d'elements";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}

	public List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM,"Obtenir procediments per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		List<ProcSerDto> procediments = new ArrayList<>();
		try {
			var procs = getGestorDocumentalAdministratiuPlugin().getProcedimentsByUnitat(codiDir3);
			if (procs != null) {
				ProcSerDto dto;
				for (var proc : procs) {
					dto = new ProcSerDto();
					dto.setCodi(proc.getCodiSIA());
					dto.setNom(proc.getNom());
					dto.setComu(proc.isComu());
					dto.setUltimaActualitzacio(proc.getDataActualitzacio());
					if (proc.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(proc.getUnitatAdministrativacodi());
					}
					procediments.add(dto);
				}
			}
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
		return procediments;
	}

	public ProcSerDto getProcSerByCodiSia(String codiSia, boolean isServei) {

		var msg = "Obtenint " + (isServei ? "servei" : "procediment") + " amb codi SIA " + codiSia + " del gestor documental administratiu";
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM, msg, IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var proc = getGestorDocumentalAdministratiuPlugin().getProcSerByCodiSia(codiSia, isServei);
			if (proc == null) {
				return null;
			}
			var procSer = new ProcSerDto();
			procSer.setCodi(proc.getCodiSIA());
			procSer.setNom(proc.getNom());
			procSer.setComu(proc.isComu());
			procSer.setOrganGestor(proc.getUnitatAdministrativacodi());
			integracioHelper.addAccioOk(info);
			return procSer;
		} catch (Exception ex) {
			var errorDescripcio = "Error " + msg.toLowerCase();
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}
	
	public List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3, int numPagina) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM,"Obtenir procediments per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		List<ProcSerDto> procediments = new ArrayList<>();
		try {
			var procs = getGestorDocumentalAdministratiuPlugin().getProcedimentsByUnitat(codiDir3, numPagina);
			if (procs != null) {
				ProcSerDto dto;
				for (var proc : procs) {
					dto = new ProcSerDto();
					dto.setCodi(proc.getCodiSIA());
					dto.setNom(proc.getNom());
					dto.setComu(proc.isComu());
					dto.setUltimaActualitzacio(proc.getDataActualitzacio());
					if (proc.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(proc.getUnitatAdministrativacodi());
					}
					procediments.add(dto);
				}
			}
			integracioHelper.addAccioOk(info);
			return procediments;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}

	public int getTotalServeis(String codiDir3) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM,"Recuperant el total de serveis", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var totalElements = getGestorDocumentalAdministratiuPlugin().getTotalServeis(codiDir3);
			integracioHelper.addAccioOk(info);
			return totalElements;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir el número total d'elements";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}

	public List<ProcSerDto> getServeisGdaByEntitat(String codiDir3) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM,"Obtenir serveis per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			List<ProcSerDto> serveis = new ArrayList<>();
			var servs = getGestorDocumentalAdministratiuPlugin().getServeisByUnitat(codiDir3);
			if (servs != null) {
				ProcSerDto dto;
				for (var servei : servs) {
					dto = new ProcSerDto();
					dto.setCodi(servei.getCodiSIA());
					dto.setNom(servei.getNom());
					dto.setComu(servei.isComu());
					dto.setUltimaActualitzacio(servei.getDataActualitzacio());
					if (servei.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(servei.getUnitatAdministrativacodi());
					}
					serveis.add(dto);
				}
			}
			integracioHelper.addAccioOk(info);
			return serveis;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}

	public List<ProcSerDto> getServeisGdaByEntitat(String codiDir3, int numPagina) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_GESCONADM,"Obtenir serveis per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			List<ProcSerDto> serveis = new ArrayList<>();
			var servs = getGestorDocumentalAdministratiuPlugin().getServeisByUnitat(codiDir3, numPagina);
			if (servs != null) {
				ProcSerDto dto;
				for (var servei : servs) {
					dto = new ProcSerDto();
					dto.setCodi(servei.getCodiSIA());
					dto.setNom(servei.getNom());
					dto.setComu(servei.isComu());
					dto.setUltimaActualitzacio(servei.getDataActualitzacio());
					if (servei.getUnitatAdministrativacodi() != null) {
						dto.setOrganGestor(servei.getUnitatAdministrativacodi());
					}
					serveis.add(dto);
				}
			}
			integracioHelper.addAccioOk(info);
			return serveis;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir els procediments del gestor documental administratiu";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, errorDescripcio, ex);
		}
	}
	
	// UNITATS ORGANITZATIVES
	// /////////////////////////////////////////////////////////////////////////////////////

	public Map<String, NodeDir3> getOrganigramaPerEntitat(String codiDir3Entitat) throws SistemaExternException {

		log.info("Obtenir l'organigrama per entitat");
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenir organigrama per entitat",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat));

		var protocol = configHelper.getConfig("es.caib.notib.plugin.unitats.dir3.protocol");
		try {
			EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat + "no trobada");
			}
			if (Strings.isNullOrEmpty(configHelper.getEntitatActualCodi())) {
				configHelper.setEntitatCodi(entitat.getCodi());
			}
			info.setCodiEntitat(entitat.getCodi());
			Map<String, NodeDir3> organigrama;
			if ("SOAP".equalsIgnoreCase(protocol)) {
				log.info("Obtenir l'organigrama per entitat SOAP");
				organigrama = getUnitatsOrganitzativesPlugin().organigramaPerEntitat(codiDir3Entitat, null, null);
			} else {
				log.info("Obtenir l'organigrama per entitat REST");
				organigrama = getUnitatsOrganitzativesPlugin().organigramaPerEntitat(codiDir3Entitat);
			}
			integracioHelper.addAccioOk(info);
			return organigrama;
		} catch (Exception ex) {
			log.info("Error al obtenir l'organigrama per entitat");
			String errorDescripcio = "Error al obtenir l'organigrama per entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public List<NodeDir3> getOrganNomMultidioma(EntitatEntity entitat) {
		return unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), null, null);
	}

	public List<NodeDir3> unitatsOrganitzativesFindByPare(String entitatCodi, String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Consulta llista d'unitats donat un pare", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("unitatPare", pareCodi),
				new AccioParam("fechaActualizacion", dataActualitzacio == null ? null : dataActualitzacio.toString()),
				new AccioParam("fechaSincronizacion", dataSincronitzacio == null ? null : dataSincronitzacio.toString()));
		try {
			ConfigHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			var unitatsOrganitzatives = getUnitatsOrganitzativesPlugin().findAmbPare(pareCodi, dataActualitzacio, dataSincronitzacio);
			removeUnitatsSubstitutedByItself(unitatsOrganitzatives);
			if (unitatsOrganitzatives == null || unitatsOrganitzatives.isEmpty()) {
				var errorMissatge = messageManager.getMessage("organgestor.actualitzacio.sense.canvis");
				info.addParam("Resultat", "No s'han obtingut canvis.");
				integracioHelper.addAccioOk(info);
				throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
			}
			integracioHelper.addAccioOk(info);
			return unitatsOrganitzatives;
		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public NodeDir3 unitatOrganitzativaFindByCodi(String entitatCodi, String codi, Date dataActualitzacio, Date dataSincronitzacio) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Consulta llista d'unitats donat un codi", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("codi", codi),
				new AccioParam("fechaActualizacion", dataActualitzacio == null ? null : dataActualitzacio.toString()),
				new AccioParam("fechaSincronizacion", dataSincronitzacio == null ? null : dataSincronitzacio.toString()));
		try {
			ConfigHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			var unitatOrganitzativa = getUnitatsOrganitzativesPlugin().findAmbCodi(codi, dataActualitzacio, dataSincronitzacio);
			integracioHelper.addAccioOk(info);
			return unitatOrganitzativa;
		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			var error = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(info, error, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, error, ex);
		}
	}

	/**
	 * Remove from list unitats that are substituted by itself
	 * for example if webservice returns two elements:
	 *
	 * UnitatOrganitzativa(codi=A00000010, estat=E, historicosUO=[A00000010])
	 * UnitatOrganitzativa(codi=A00000010, estat=V, historicosUO=null)
	 *
	 * then remove the first one.
	 * That way this transition can be treated by application the same way as transition CANVI EN ATRIBUTS
	 */
	private void removeUnitatsSubstitutedByItself(List<NodeDir3> unitatsOrganitzatives) {
		if (CollectionUtils.isNotEmpty(unitatsOrganitzatives)) {
			Iterator<NodeDir3> i = unitatsOrganitzatives.iterator();
			while (i.hasNext()) {
				NodeDir3 unitatOrganitzativa = i.next();
				if (CollectionUtils.isNotEmpty(unitatOrganitzativa.getHistoricosUO()) && unitatOrganitzativa.getHistoricosUO().size() == 1 && unitatOrganitzativa.getHistoricosUO().get(0).equals(unitatOrganitzativa.getCodi())) {
					i.remove();
				}
			}
		}
	}


	public List<ObjetoDirectorio> llistarOrganismesPerEntitat(String entitatcodi) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenir llista d'organismes per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var organismes = getUnitatsOrganitzativesPlugin().unitatsPerEntitat(entitatcodi, true);
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar organismes per entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}
	
	public String getDenominacio(String codiDir3) {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenir denominació d'organisme", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'organisme", codiDir3));
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var denominacio = getUnitatsOrganitzativesPlugin().unitatDenominacio(codiDir3);
			integracioHelper.addAccioOk(info);
			return denominacio;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir denominació de organisme";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}
	
	public List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma,
											 Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenir llista de tots els organismes a partir d'un text",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", codi));

		info.setCodiEntitat(getCodiEntitatActual());
		// Eliminam espais
		codi = codi != null ? codi.trim() : null;
		municipi = municipi != null ? municipi.trim() : null;
		try {
			if (denominacio != null) {
				denominacio = denominacio.replaceAll(" ", "%20");
			}
			var organismesNodeDir3 = getUnitatsOrganitzativesPlugin().cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
			var organismes = organismesNodeDir3.stream().map(this::toOrganGestorDto).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar organismes  a partir d'un text";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public List<OrganGestorDto> unitatsPerCodi(String codi) throws SistemaExternException {
		return cercaUnitats(codi,null,null,null,null,null,null,null);
	}

	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenir llista de tots els organismes a partir d'un text",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", denominacio));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var organismesDir3 = getUnitatsOrganitzativesPlugin().unitatsPerDenominacio(denominacio);
			var organismes = organismesDir3.stream().map(this::toOrganGestorDto).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar organismes  a partir d'un text";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	private OrganGestorDto toOrganGestorDto(NodeDir3 organ) {
		return OrganGestorDto.builder().codi(organ.getCodi()).nom(organ.getDenominacionCooficial()).nomEs(organ.getDenominacio())
				.estat(organGestorEstatForName(organ.getEstat())).sir(organ.getTieneOficinaSir()).cif(organ.getCif()).build();
	}
	private OrganGestorEstatEnum organGestorEstatForName(String estatNom) {

		for (var estat: OrganGestorEstatEnum.values()) {
			if (estat.name().equalsIgnoreCase(estatNom)) {
				return estat;
			}
		}
		return null;
	}

	private OrganGestorDto toOrganGestorDto(ObjetoDirectorio organ) {
		return OrganGestorDto.builder().codi(organ.getCodi()).nom(organ.getDenominacio()).build();
	}
	
	public List<CodiValor> llistarNivellsAdministracions() throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenint llista dels nivells de les administracions", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var nivellsAdministracio = getUnitatsOrganitzativesPlugin().nivellsAdministracio();
			integracioHelper.addAccioOk(info);
			return nivellsAdministracio;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els nivells de les administracions";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}
	
	public List<CodiValor> llistarComunitatsAutonomes() throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenint llista les comunitats autònomes", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var comunitatsAutonomes = getUnitatsOrganitzativesPlugin().comunitatsAutonomes();
			integracioHelper.addAccioOk(info);
			return comunitatsAutonomes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar les comunitats autònomes";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public List<CodiValorPais> llistarPaisos() throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenint llista de països", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var paisos = getUnitatsOrganitzativesPlugin().paisos();
			integracioHelper.addAccioOk(info);
			return paisos;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar països";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public List<CodiValor> llistarProvincies() throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenint llista de províncies", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var provincies = getUnitatsOrganitzativesPlugin().provincies();
			integracioHelper.addAccioOk(info);
			return provincies;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistat províncies";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}
	
	public List<CodiValor> llistarProvincies(String codiCA) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS,"Obtenint llista de províncies", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var provincies = getUnitatsOrganitzativesPlugin().provincies(codiCA);
			integracioHelper.addAccioOk(info);
			return provincies;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistat províncies";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}
	
	public List<CodiValor> llistarLocalitats(String codiProvincia) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Obtenint llista de localitats d'una província", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi de la província", codiProvincia));

		info.setCodiEntitat(getCodiEntitatActual());
		try {
			var localitats = getUnitatsOrganitzativesPlugin().localitats(codiProvincia);
			integracioHelper.addAccioOk(info);
			return localitats;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistat els municipis d'una província";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorDescripcio, ex);
		}
	}

	public byte[] firmaServidorFirmar(NotificacioEntity notificacio, FitxerDto fitxer, TipusFirma tipusFirma, String motiu, String idioma) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_FIRMASERV, "Firma en servidor d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("notificacioId", notificacio.getId().toString()),
				new AccioParam("títol", fitxer.getNom()));

		info.setCodiEntitat(notificacio.getEntitat().getCodi());
		try {
			var firmaContingut = getFirmaServidorPlugin().firmar(fitxer.getNom(), motiu, fitxer.getContingut(), tipusFirma, idioma);
			integracioHelper.addAccioOk(info);
			return firmaContingut;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de firma en servidor: " + ex.getMessage();
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, errorDescripcio, ex);
		}
	}
	
	public RegistreAnnexDto documentToRegistreAnnexDto (DocumentEntity document) {

		var annex = new RegistreAnnexDto();
		annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
		annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
		annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
		annex.setData(new Date());
		annex.setIdiomaCodi("ca");

		if((document.getUuid() != null || document.getCsv() != null) && document.getContingutBase64() == null) {
			var loadFromArxiu = isReadDocsMetadataFromArxiu() && document.getUuid() != null || document.getCsv() == null;
			DocumentContingut doc;
			if(loadFromArxiu) {
				try {
					annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);

					doc = arxiuGetImprimible(document.getUuid(), true);
					annex.setArxiuContingut(doc.getContingut());
					annex.setArxiuNom(doc.getArxiuNom());
				} catch (ArxiuException ae) {
					log.error("Error Obtenint el document per l'uuid");
				}
				return annex;
			}
			try {
				annex.setModeFirma(RegistreModeFirmaDtoEnum.AUTOFIRMA_SI);
				doc = arxiuGetImprimible(document.getCsv(), false);
				annex.setArxiuContingut(doc.getContingut());
				annex.setArxiuNom(doc.getArxiuNom());
			} catch (ArxiuException ae) {
				log.error("Error Obtenint el document per csv");
			}
			return annex;
		}
		if(document.getContingutBase64() != null && (document.getUuid() == null && document.getCsv() == null)) {
			annex.setArxiuContingut(document.getContingutBase64().getBytes());
			annex.setArxiuNom(document.getArxiuNom());
			annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);
		}
		return annex;
	}
	
	private AnexoWsDto documentToAnexoWs(DocumentEntity document, int idx, boolean isComunicacioSir) {

		try {
			if (HibernateHelper.isProxy(document)) {
				document = HibernateHelper.deproxy(document);
			}
			AnexoWsDto annex = null;
			Path path;
			var enviarCsv = !isComunicacioSir || (isComunicacioSir && (EnviamentSirTipusDocumentEnviarEnumDto.TOT.equals(getEnviamentSirTipusDocumentEnviar()) ||
							EnviamentSirTipusDocumentEnviarEnumDto.CSV.equals(getEnviamentSirTipusDocumentEnviar())));
			
			var enviarContingut = !isComunicacioSir || (isComunicacioSir && (EnviamentSirTipusDocumentEnviarEnumDto.TOT.equals(getEnviamentSirTipusDocumentEnviar()) ||
							EnviamentSirTipusDocumentEnviarEnumDto.BINARI.equals(getEnviamentSirTipusDocumentEnviar())));

			var enviarTipoMIMEFicheroAnexado = Boolean.TRUE;

			// Metadades per defecte (per si no estan emplenades (notificacions antigues)
			Integer origen = document.getOrigen() != null ? document.getOrigen().getValor() : OrigenEnum.ADMINISTRACIO.getValor();
			var validezDocumento = document.getValidesa() != null ? document.getValidesa().getValor() : ValidesaEnum.ORIGINAL.getValor();
			var tipoDocumental = document.getTipoDocumental() != null ? document.getTipoDocumental().getValor() : TipusDocumentalEnum.NOTIFICACIO.getValor();
			Integer modoFirma = document.getModoFirma() != null ? (document.getModoFirma() ? 1 : 0) : 0;

			if((document.getUuid() != null || document.getCsv() != null) && document.getContingutBase64() == null) {
				annex = new AnexoWsDto();
				var id = "";
				DocumentContingut doc = null;
				Document docDetall = null;
				var loadFromArxiu = isReadDocsMetadataFromArxiu() && document.getUuid() != null || document.getCsv() == null;
				if(loadFromArxiu) {
					id = document.getUuid();
					docDetall = arxiuDocumentConsultar(id, null, true, true);
					if (docDetall != null) {
						doc = docDetall.getContingut();
						if (docDetall.getMetadades() != null && enviarCsv) {
							// Recuperar csv
							var metadadesAddicionals = docDetall.getMetadades().getMetadadesAddicionals();
							if (metadadesAddicionals != null) {
								if (metadadesAddicionals.containsKey("csv")) {
									annex.setCsv((String) metadadesAddicionals.get("csv"));
								} else if (metadadesAddicionals.containsKey("eni:csv")) {
									annex.setCsv((String) metadadesAddicionals.get("eni:csv"));
								}
							}
						}
					}
				} else {
					if (enviarContingut) {
						id = document.getCsv();
						docDetall = arxiuDocumentConsultar(id, null, true, false);
						if (docDetall != null) {
							doc = docDetall.getContingut();
						}
					}
					if (enviarCsv) {
						annex.setCsv(document.getCsv());
					}
				}
				
				if (enviarContingut && doc != null) {
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
				
				if (Boolean.TRUE.equals(enviarTipoMIMEFicheroAnexado)) {
					path = new File(doc.getArxiuNom()).toPath();
				}
			} else if(document.getArxiuGestdocId() != null && (document.getUuid() == null && document.getCsv() == null)) {
				annex = new AnexoWsDto();
				var output = new ByteArrayOutputStream();
				gestioDocumentalGet(document.getArxiuGestdocId(), GESDOC_AGRUPACIO_NOTIFICACIONS, output);
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
			
			if (Boolean.TRUE.equals(enviarTipoMIMEFicheroAnexado)) {
				annex.setTipoMIMEFicheroAnexado(document.getMediaType());
			}
			annex.setTitulo("Annex " + idx);
			annex.setTipoDocumento(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI.getValor());
			return annex;
		} catch (Exception ex) {
			var msg = "Error obtenint les dades del document '" + (document != null ? document.getId() : "") + "': " + ex.getMessage();
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, msg, ex.getCause());
		}
	}

	public static String estatElaboracioToValidesa(DocumentEstatElaboracio estatElaboracio) {

		if (estatElaboracio == null) {
			return ValidesaEnum.ORIGINAL.getValor(); // Valor per defecte
		}
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
	public static Integer getModeFirma(Document document, String nom) {
		return nom != null && nom.toLowerCase().endsWith("pdf") && (document.getFirmes() != null && !document.getFirmes().isEmpty()) ? 1 : 0;
	}

	public byte[] getUrlDocumentContent(String urlPath) throws SistemaExternException {

		var baos = new ByteArrayOutputStream();
		try {
			try (var is = new URL(urlPath).openStream()) {
				var byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
				int n;
				while ((n = is.read(byteChunk)) > 0) {
					baos.write(byteChunk, 0, n);
				}
				return baos.toByteArray();
			}
		} catch (Exception e) {
			log.error("Error al obtenir document de la URL: " + urlPath, e);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, "Error al obtenir document de la URL: " + urlPath);
		}
	}

	public AsientoRegistralBeanDto notificacioEnviamentsToAsientoRegistralBean(NotificacioEntity notificacio, Set<NotificacioEnviamentEntity> enviaments, boolean inclou_documents) throws RegistrePluginException {

		var registre = notificacioToAsientoRegistralBean(notificacio, inclou_documents);
		for (var enviament : enviaments) {
			registre.getInteresados().add(enviamentToRepresentanteEInteresadoWs(enviament));
		}
		return registre;
	}

	public AsientoRegistralBeanDto notificacioToAsientoRegistralBean(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, boolean inclou_documents, boolean isComunicacioSir) throws RegistrePluginException {

		var registre = notificacioToAsientoRegistralBean(notificacio, inclou_documents, isComunicacioSir);
		log.info("Afegint els interessats ...");
		registre.getInteresados().add(enviamentToRepresentanteEInteresadoWs(enviament));
		log.info("Interessats afegits correctament");
		return registre;
	}

	private InteresadoWsDto enviamentToRepresentanteEInteresadoWs(NotificacioEnviamentEntity enviament) {

		PersonaEntity destinatari = null;
		if(enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
			destinatari =  enviament.getDestinataris().get(0);
		}
		return personaToRepresentanteEInteresadoWs(enviament.getTitular(), destinatari);
	}
	
	private AsientoRegistralBeanDto notificacioToAsientoRegistralBean(NotificacioEntity notificacio, boolean inclou_documents) throws RegistrePluginException {
		return notificacioToAsientoRegistralBean(notificacio, inclou_documents, false);
	}

	private AsientoRegistralBeanDto notificacioToAsientoRegistralBean(NotificacioEntity notificacio, boolean inclou_documents, boolean isComunicacioSir) throws RegistrePluginException {

		log.info("Preparant AsientoRegistralBeanDto");
		var registre = new AsientoRegistralBeanDto();
		registre.setEntidadCodigo(notificacio.getEntitat().getDir3Codi());
		registre.setEntidadDenominacion(notificacio.getEntitat().getNom());
		var dadesOficina = new DadesOficina();
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
		log.info("Recuperant informació de l'oficina i registre...");
		setOficina(notificacio, dadesOficina, dir3Codi);
		log.info("Recuperant informació del llibre");
		setLlibre(notificacio, dadesOficina, dir3Codi);

		if (dadesOficina.getOficinaCodi() != null) {
			registre.setEntidadRegistralOrigenCodigo(dadesOficina.getOficinaCodi());
			registre.setEntidadRegistralOrigenDenominacion(dadesOficina.getOficinaNom());
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
		log.info("Preparant dades de sortida");
		registre.setTipoRegistro(2L);
		var tipusEnv = EnviamentTipus.NOTIFICACIO == notificacio.getEnviamentTipus() ? "Notificacio" : "Comunicacio";
		registre.setResumen(tipusEnv + " - " + notificacio.getConcepte());
		/* 1 = Documentació adjunta en suport Paper
		 * 2 = Documentació adjunta digitalitzada i complementàriament en paper
		 * 3 = Documentació adjunta digitalitzada */
		registre.setTipoDocumentacionFisicaCodigo(3L);
		if (notificacio.getProcediment() != null) {
			log.info("Afegint dades del procediment");
			registre.setTipoAsunto(notificacio.getProcediment().getTipusAssumpte());
			registre.setTipoAsuntoDenominacion(notificacio.getProcediment().getTipusAssumpte());
			registre.setCodigoAsunto(notificacio.getProcediment().getCodiAssumpte());
			registre.setCodigoAsuntoDenominacion(notificacio.getProcediment().getCodiAssumpte());
		}
		log.info("Afegint dades de l'idioma");
		registre.setIdioma(notificacio.getIdioma() != null ? (notificacio.getIdioma().ordinal() + 1) : 1L);
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
		if (notificacio.getProcediment() != null) {
			log.info("Afegint codi Sia");
			try {
				registre.setCodigoSia(Long.parseLong(notificacio.getProcediment().getCodi()));
			} catch (NumberFormatException nfe) {
				log.error("Error afegint el codi SIA");
			}
		}
		log.info("Afegint dades varies del registre");
		registre.setCodigoUsuario(notificacio.getUsuariCodi());
		registre.setAplicacionTelematica("NOTIB v." + CacheHelper.getAppVersion());
		registre.setAplicacion("RWE");
		registre.setVersion("3.1");
		registre.setObservaciones("Notib: " + notificacio.getUsuariCodi());
		registre.setExpone("");
		registre.setSolicita("");
		registre.setPresencial(false);
		registre.setEstado(notificacio.getEstat() != null ? notificacio.getEstat().getLongVal() : null);
		registre.setMotivo(notificacio.getDescripcio());
		registre.setInteresados(new ArrayList<>());
		registre.setAnexos(new ArrayList<>());
		if (inclou_documents) {
			log.info("Incloguent documents ...");
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
		log.info("Retornant registre");
		return registre;
	}

	public InteresadoWsDto personaToRepresentanteEInteresadoWs (PersonaEntity titular, PersonaEntity destinatari) {

		var interessat = new InteresadoWsDto();
		if(titular != null) {
			var interessatDades = persona2DatosInteresadoWsDto(titular);
			interessat.setInteresado(interessatDades);
		}
		if(destinatari != null && titular != null && titular.isIncapacitat()) {
			var representantDades = persona2DatosInteresadoWsDto(destinatari);
			interessat.setRepresentante(representantDades);	
		}
		return interessat;
	}

	private DatosInteresadoWsDto persona2DatosInteresadoWsDto(PersonaEntity persona) {

		var interessatDades = new DatosInteresadoWsDto();
		if (persona.getInteressatTipus() != null) {
			Long tipo = InteressatTipus.FISICA_SENSE_NIF.equals(persona.getInteressatTipus()) ? 2l: persona.getInteressatTipus().getLongVal();
			interessatDades.setTipoInteresado(tipo);
		}
		if (persona.getInteressatTipus() == InteressatTipus.ADMINISTRACIO) {
			interessatDades.setDocumento(persona.getDir3Codi() != null ? persona.getDir3Codi().trim() : null);
			interessatDades.setTipoDocumentoIdentificacion("O");
		}  else if (persona.getInteressatTipus() == InteressatTipus.FISICA) {
			interessatDades.setDocumento(persona.getNif() != null ? persona.getNif().trim() : null);
			interessatDades.setTipoDocumentoIdentificacion(isDocumentEstranger(persona.getNif()) ? "E" : "N");
		}  else if (persona.getInteressatTipus() == InteressatTipus.FISICA_SENSE_NIF) {
			// Pot tenir un document (No NIF), que s'ha desat al camp NIF
			if (persona.getNif() != null && !persona.getNif().isEmpty()) {
				interessatDades.setDocumento(persona.getNif());
				if (persona.getDocumentTipus() != null) {
					interessatDades.setTipoDocumentoIdentificacion(persona.getDocumentTipus() == DocumentTipus.PASSAPORT ? "P" : "X");
				}
			}
		} else if (persona.getInteressatTipus() == InteressatTipus.JURIDICA) {
			interessatDades.setDocumento(persona.getNif() != null ? persona.getNif().trim() : null);
			interessatDades.setTipoDocumentoIdentificacion("C");
		}
		var raoSocial = persona.getRaoSocial() == null || persona.getRaoSocial().length() <= 80 ? persona.getRaoSocial() : persona.getRaoSocial().substring(0, 80);
		var nom = persona.getNom() == null || persona.getNom().length() <= 30 ? persona.getNom() : persona.getNom().substring(0, 30);
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

		var dadesOficina = new DadesOficina();
		var dir3Codi = notificacio.getEntitat().getDir3CodiReg() != null && !notificacio.getEntitat().getDir3CodiReg().isEmpty() ?
				notificacio.getEntitat().getDir3CodiReg() :  notificacio.getEmisorDir3Codi();
		try {
			setOficina(notificacio, dadesOficina, dir3Codi);
		} catch (RegistrePluginException ex) {
			log.error("Error afegint oficina i llibre registre", ex);
		}
		try {
			setLlibre(notificacio, dadesOficina, dir3Codi);
		} catch (RegistrePluginException ex) {
			log.error("Error afegint oficina i llibre registre", ex);
		}
	}

	private void setOficina(NotificacioEntity notificacio, DadesOficina dadesOficina, String dir3Codi) throws RegistrePluginException {

		if (notificacio.getEntitat().isOficinaEntitat() && notificacio.getEntitat().getOficina() != null) {
			dadesOficina.setOficinaCodi(notificacio.getEntitat().getOficina());
			dadesOficina.setOficinaNom(notificacio.getEntitat().getOficina());
		} else if (!notificacio.getEntitat().isOficinaEntitat() && notificacio.getOrganGestor() != null && notificacio.getOrganGestor().getOficina() != null) {
			var organGestor = notificacio.getOrganGestor();
			dadesOficina.setOficinaCodi(organGestor.getOficina());
			dadesOficina.setOficinaNom(organGestor.getOficinaNom());
		} else {
			var oficinaVirtual = llistarOficinaVirtual(dir3Codi, notificacio.getEntitat().getNomOficinaVirtual(), TipusRegistreRegweb3Enum.REGISTRE_SORTIDA);
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
	
	private void setLlibre(NotificacioEntity notificacio, DadesOficina dadesOficina, String dir3Codi) throws RegistrePluginException {
		
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
					llibreOrganisme = llistarLlibreOrganisme(dir3Codi, organGestor);
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
				llibreOrganisme = llistarLlibreOrganisme(dir3Codi, dir3Codi);
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

	// Validació de firmes
	public SignatureInfoDto detectSignedAttachedUsingValidateSignaturePlugin(byte[] documentContingut, String nom, String firmaContentType) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_VALIDASIG, "Validació firmes de document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Nom del document", nom), new AccioParam("ContentType", firmaContentType));
		try {
			var validationRequest = new ValidateSignatureRequest();
			validationRequest.setSignatureData(documentContingut);
			var sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(true);
			validationRequest.setSignatureRequestedInformation(sri);
			var validateSignatureResponse = getValidaSignaturaPlugin().validateSignature(validationRequest);

			var validationStatus = validateSignatureResponse.getValidationStatus();
			var signatureInfoDto = validationStatus.getStatus() == 1 ? SignatureInfoDto.builder().signed(true).error(false).build()
					: SignatureInfoDto.builder().signed(true).error(true).errorMsg(validationStatus.getErrorMsg()).build();
			info.addParam("Document firmat", Boolean.toString(signatureInfoDto.isSigned()));
			info.addParam("Error de firma", Boolean.toString(signatureInfoDto.isError()));
			if (signatureInfoDto.isError()) {
				info.addParam("Missatge d'error", signatureInfoDto.getErrorMsg());
			}
			integracioHelper.addAccioOk(info);
			return signatureInfoDto;
		} catch (Exception e) {
			var throwable = ExceptionUtils.getRootCause(e) != null ? ExceptionUtils.getRootCause(e) : e;
			if (throwable.getMessage().contains("El formato de la firma no es valido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") || throwable.getMessage().contains("El formato de la firma no es válido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") || throwable.getMessage().contains("El documento OOXML no está firmado(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)")) {
				info.addParam("Document firmat", "false");
				info.addParam("Error de firma", "false");
				info.addParam("Missatge d'error", throwable.getMessage());
				integracioHelper.addAccioOk(info);
				return SignatureInfoDto.builder().signed(false).error(false).build();
			}
			log.error("Error al detectar firma de document", e);
			integracioHelper.addAccioError(info, "Error al validar la firma", throwable);
			return SignatureInfoDto.builder().signed(false).error(true).errorMsg(e.getMessage()).build();
		}
	}

	// CARPETA

	public void enviarNotificacioMobil(NotificacioEnviamentEntity e) {

		if (e.isPerEmail() || InteressatTipus.ADMINISTRACIO.equals(e.getTitular().getInteressatTipus())) {
			return;
		}
		var info = new IntegracioInfo(IntegracioHelper.CARPETA, "Enviar notificació mòvil", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var eventInfo = NotificacioEventHelper.EventInfo.builder().enviament(e).tipus(NotificacioEventTipusEnumDto.API_CARPETA).build();
		try {
			if (!enviarCarpeta()) {
				throw new Exception("El plugin de CARPETA no està configurat");
			}
			var res = getCarpetaPlugin().enviarNotificacioMobil(crearMissatgeCarpetaParams(e));
			if (!Strings.isNullOrEmpty(res.getCode()) && "OK".equalsIgnoreCase(res.getCode())) {
				integracioHelper.addAccioOk(info);
			} else {
				eventInfo.setError(true);
				eventInfo.setErrorDescripcio(res.getMessage());
				integracioHelper.addAccioError(info, res.getMessage());
			}
		} catch (Exception ex) {
			var msg = "Error al enviar notificació mòvil";
			log.error(msg, ex);
			eventInfo.setError(true);
			eventInfo.setErrorDescripcio(ex.getMessage());
			integracioHelper.addAccioError(info, msg, ex);
		}
		eventHelper.addEvent(eventInfo);
	}

	public static MissatgeCarpetaParams crearMissatgeCarpetaParams(NotificacioEnviamentEntity enviament) {

		var not = enviament.getNotificacio();
		var entitat = not.getEntitat();
		var isRepresentant = enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty();
		var interessat = isRepresentant ? enviament.getDestinataris().get(0) : enviament.getTitular();
		var idioma = not.getIdioma();
		var organ = not.getOrganGestor();
		var nomOrgan = Idioma.ES.equals(idioma) && !Strings.isNullOrEmpty(organ.getNomEs()) ? organ.getNomEs() : organ.getNom();
		var dataCompareixenca = not.getEnviamentDataProgramada() != null ? not.getEnviamentDataProgramada() : not.getNotificaEnviamentData();
		return MissatgeCarpetaParams.builder().nifDestinatari(interessat.getNif()).nomCompletDestinatari(interessat.getNomSencer())
				.codiDir3Entitat(entitat.getDir3Codi()).nomEntitat(entitat.getNom())
				.codiOrganEmisor(not.getEmisorDir3Codi()).nomOrganEmisor(nomOrgan)
				.concepteNotificacio(not.getConcepte()).descNotificacio(not.getDescripcio()).uuIdNotificacio(not.getReferencia())
				.tipus(not.getEnviamentTipus()).vincleInteressat(isRepresentant ? VincleInteressat.REPRESENTANT :VincleInteressat.TITULAR)
				.codiSiaProcediment(not.getProcediment().getCodi()).nomProcediment(not.getProcediment().getNom())
				.caducitatNotificacio(not.getCaducitat())
				.dataDisponibleCompareixenca(dataCompareixenca)
				.numExpedient(not.getNumExpedient())
				.build();
	}

	private boolean isFitxerSigned(byte[] contingut, String contentType) {

		if (!contentType.equals("application/pdf")) {
			return false;
		}
		try {
			var reader = new PdfReader(contingut);
			var acroFields = reader.getAcroFields();
			var signatureNames = acroFields.getSignatureNames();
			return signatureNames != null && !signatureNames.isEmpty();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}


	public boolean isDadesUsuariPluginDisponible() {

		var pluginClass = getPropertyPluginDadesUsuari();
		if (pluginClass == null || pluginClass.length() == 0) {
			return false;
		}
		try {
			return getDadesUsuariPlugin() != null;
		} catch (SistemaExternException sex) {
			log.error("Error al obtenir la instància del plugin de dades d'usuari", sex);
			return false;
		}
	}

	public boolean isGestioDocumentalPluginDisponible() {

		var pluginClass = getPropertyPluginGestioDocumental();
		if (pluginClass == null || pluginClass.length() == 0) {
			return false;
		}
		try {
			return getGestioDocumentalPlugin() != null;
		} catch (SistemaExternException sex) {
			log.error("Error al obtenir la instància del plugin de gestió documental", sex);
			return false;
		}
	}

	public boolean isArxiuPluginDisponible() {

		var pluginClass = getPropertyPluginRegistre();
		if (pluginClass == null || pluginClass.length() == 0) {
			return false;
		}
		try {
			return getArxiuPlugin() != null;
		} catch (SistemaExternException sex) {
			log.error("Error al obtenir la instància del plugin d'arxiu", sex);
			return false;
		}
	}

	private DadesUsuariPlugin getDadesUsuariPlugin() {

		if (dadesUsuariPlugin != null) {
			return dadesUsuariPlugin;
		}
		var pluginClass = getPropertyPluginDadesUsuari();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "La classe del plugin d'usuari no està definida";
			log.error(msg);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			dadesUsuariPlugin = pluginClass.endsWith("DadesUsuariPluginKeycloak") ?
							(DadesUsuariPlugin)clazz.getDeclaredConstructor(String.class, Properties.class).newInstance("es.caib.notib.plugin.dades.usuari.",
									configHelper.getAllEntityProperties(null))
							: (DadesUsuariPlugin) clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(null));
			return dadesUsuariPlugin;
		} catch (Exception ex) {
			log.error("Error al crear la instància del plugin de dades d'usuari (" + pluginClass + "): ", ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_USUARIS, "Error al crear la instància del plugin de dades d'usuari", ex);
		}
	}

	private String getCodiEntitatActual() {

		var codiEntitat = configHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi de l'entitat no pot ser null");
		}
		return codiEntitat;
	}

	private GestioDocumentalPlugin getGestioDocumentalPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = gestioDocumentalPlugin.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginGestioDocumental();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "La classe del plugin de gestió documental no està configurada";
			log.error(msg);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (GestioDocumentalPlugin)clazz.getDeclaredConstructor(Properties.class)
					.newInstance(configHelper.getAllEntityProperties(codiEntitat));
			gestioDocumentalPlugin.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin de gestió documental (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESDOC, msg, ex);
		}
	}

	private RegistrePlugin getRegistrePlugin(String codiEntitat) {

		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}

		var plugin = registrePlugin.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginRegistre();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "\"La classe del plugin de registre no està definida\"";
			log.error(msg);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (RegistrePlugin)clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
			registrePlugin.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "\"Error al crear la instància del plugin de registre (\" + pluginClass + \") \"";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, msg, ex);
		}
	}
	
	private IArxiuPlugin getArxiuPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = arxiuPlugin.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginArxiu();
		if (Strings.isNullOrEmpty(pluginClass)) {
			String msg = "La classe del plugin d'arxiu digital no està definida";
			log.error(msg);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (IArxiuPlugin)clazz.getDeclaredConstructor(String.class, Properties.class).newInstance("es.caib.notib.", configHelper.getEnvironmentProperties());
			arxiuPlugin.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin d'arxiu digital (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_ARXIU, msg, ex);
		}
	}

	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = unitatsOrganitzativesPlugin.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginUnitats();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "La classe del plugin de DIR3 no està configurada";
			log.error(msg);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (UnitatsOrganitzativesPlugin)clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
			unitatsOrganitzativesPlugin.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin de DIR3 (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_REGISTRE, msg, ex);
		}
	}
	
	private GestorContingutsAdministratiuPlugin getGestorDocumentalAdministratiuPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = gestorDocumentalAdministratiuPlugin.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginGestorDocumentalAdministratu();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "La classe del plugin del gestor documental administratiu no està configurada";
			log.error(msg);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (GestorContingutsAdministratiuPlugin)clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
			gestorDocumentalAdministratiuPlugin.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin de gestor documental administratiu (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_GESCONADM, msg, ex);
		}
	}

	private FirmaServidorPlugin getFirmaServidorPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = firmaServidorPlugin.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginFirmaServidor();
		if (pluginClass == null || pluginClass.length() == 0) {
			var error = "No està configurada la classe per al plugin de firma en servidor";
			log.error(error);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, error);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (FirmaServidorPlugin)clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
			firmaServidorPlugin.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var error = "Error al crear la instància del plugin de firma en servidor" ;
			log.error(error + " (" + pluginClass + "): ", ex);
			throw new SistemaExternException(IntegracioHelper.INTCODI_FIRMASERV, error, ex);
		}
	}

	private IValidateSignaturePlugin getValidaSignaturaPlugin() {

		var entitatCodi = configHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(entitatCodi)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = validaSignaturaPlugins.get(entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginValidaSignatura();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var error = "No està configurada la classe per al plugin de validació de firma";
			log.error(error);
			throw new SistemaExternException(IntegracioHelper.INTCODI_VALIDASIG, error);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (IValidateSignaturePlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
					.newInstance(ConfigDto.prefix + ".", configHelper.getAllEntityProperties(entitatCodi));
			validaSignaturaPlugins.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_VALIDASIG, "Error al crear la instància del plugin de validació de signatures", ex);
		}
	}

	public CarpetaPlugin getCarpetaPlugin() {

		var entitatCodi = configHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(entitatCodi)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = carpetaPlugin.get(entitatCodi);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPropertyPluginCarpetaClass();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var error = "No està configurada la classe per al plugin de CARPETA";
			log.error(error);
			throw new SistemaExternException(IntegracioHelper.CARPETA, error);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (CarpetaPlugin)clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(entitatCodi));
			carpetaPlugin.put(entitatCodi, plugin);
			return plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.CARPETA, "Error al crear la instància del plugin de CARPETA", ex);
		}
	}

	public void resetPlugins(String grup) {

		switch (grup) {
			case "ARXIU":
				arxiuPlugin = new HashMap<>();
				break;
			case "USUARIS":
				dadesUsuariPlugin = null;
				break;
			case "FIRMA":
				firmaServidorPlugin = new HashMap<>();
				break;
			case "GESCONADM":
				gestorDocumentalAdministratiuPlugin = new HashMap<>();
				break;
			case "GES_DOC":
				gestioDocumentalPlugin = new HashMap<>();
				break;
			case "REGISTRE":
				registrePlugin = new HashMap<>();
				break;
			case "DIR3":
				unitatsOrganitzativesPlugin = new HashMap<>();
				break;
			default:
				break;
		}
	}

	public void resetAllPlugins() {

		dadesUsuariPlugin = null;
		registrePlugin = new HashMap<>();
		gestorDocumentalAdministratiuPlugin = new HashMap<>();
		unitatsOrganitzativesPlugin = new HashMap<>();
		arxiuPlugin = new HashMap<>();
		gestioDocumentalPlugin = new HashMap<>();
		firmaServidorPlugin = new HashMap<>();
	}


	// PROPIETATS PLUGIN

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
	private String getPropertyPluginValidaSignatura() {
		return configHelper.getConfig("es.caib.notib.plugin.validatesignature.class");
	}

	private String getPropertyPluginCarpetaClass() {
		return configHelper.getConfig("es.caib.notib.plugin.carpeta.class");
	}

	public boolean enviarCarpeta() {

		return !Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.usuari")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.contrasenya")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.missatge.codi.comunicacio")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.missatge.codi.notificacio")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.class")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.url")) &&
				configHelper.getConfigAsBoolean("es.caib.notib.plugin.carpeta.msg.actiu");
	}

	public int getSegonsEntreReintentRegistreProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.plugin.registre.segons.entre.peticions");
	}

	// PROPIETATS TASQUES EN SEGON PLA

	public int getRegistreReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.periode");
	}
	public int getNotificaReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.periode");
	}
	public int getConsultaReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.periode");
	}
	public int getConsultaSirReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode");
	}
	public int getRegistreReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.reintents.maxim");
	}
	public int getNotificaReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.reintents.maxim");
	}
	public int getConsultaReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim");
	}
	public int getConsultaReintentsDEHMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.deh.reintents.maxim");
	}
	public int getConsultaReintentsCIEMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.cie.reintents.maxim");
	}
	public int getConsultaSirReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim");
	}

	public NotificacioComunicacioTipusEnumDto getNotibTipusComunicacioDefecte() {

		var tipus = NotificacioComunicacioTipusEnumDto.SINCRON;
		try {
			var tipusStr = configHelper.getConfig("es.caib.notib.comunicacio.tipus.defecte");
			if (tipusStr != null && !tipusStr.isEmpty()) {
				tipus = NotificacioComunicacioTipusEnumDto.valueOf(tipusStr);
			}
		} catch (Exception ex) {
			log.error("No s'ha pogut obtenir el tipus de comunicació per defecte. S'utilitzarà el tipus SINCRON.");
		}
		return tipus;
	}


	// Mètodes pels tests
	public void setDadesUsuariPlugin(DadesUsuariPlugin dadesUsuariPlugin) {
		this.dadesUsuariPlugin = dadesUsuariPlugin;
	}

	public void setGestioDocumentalPlugin(GestioDocumentalPlugin gestioDocumentalPlugin) {
		this.gestioDocumentalPlugin.put(getCodiEntitatActual(), gestioDocumentalPlugin);
	}
	public void setGestioDocumentalPlugin(Map<String, GestioDocumentalPlugin> gestioDocumentalPlugin) {
		this.gestioDocumentalPlugin = gestioDocumentalPlugin;
	}
	
	public void setRegistrePlugin(RegistrePlugin registrePlugin) {
		this.registrePlugin.put(getCodiEntitatActual(), registrePlugin);
	}
	public void setRegistrePlugin(Map<String, RegistrePlugin> registrePlugin) {
		this.registrePlugin = registrePlugin;
	}

	public void setArxiuPlugin(IArxiuPlugin arxiuPlugin) {
		this.arxiuPlugin.put(getCodiEntitatActual(), arxiuPlugin);
	}
	public void setArxiuPlugin(Map<String, IArxiuPlugin> arxiuPlugin) {
		this.arxiuPlugin = arxiuPlugin;
	}
	
	public void setUnitatsOrganitzativesPlugin(UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin) {
		this.unitatsOrganitzativesPlugin.put(getCodiEntitatActual(), unitatsOrganitzativesPlugin);
	}
	public void setUnitatsOrganitzativesPlugin(Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugin) {
		this.unitatsOrganitzativesPlugin = unitatsOrganitzativesPlugin;
	}

	public void setGestorDocumentalAdministratiuPlugin(Map<String, GestorContingutsAdministratiuPlugin> gestorDocumentalAdministratiuPlugin) {
		this.gestorDocumentalAdministratiuPlugin = gestorDocumentalAdministratiuPlugin;
	}

	public boolean isReadDocsMetadataFromArxiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.documents.metadades.from.arxiu");
	}

	private static boolean isDocumentEstranger(String nie) {

		if (nie == null) {
			return false;
		}
		var aux = nie.toUpperCase();
		return aux.startsWith("X") || aux.startsWith("Y") || aux.startsWith("Z");
	}

	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {

		if (date == null) {
			return null;
		}
		var gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}
	
	private EnviamentSirTipusDocumentEnviarEnumDto getEnviamentSirTipusDocumentEnviar() {

		var tipus = EnviamentSirTipusDocumentEnviarEnumDto.TOT;
		try {
			var tipusStr = configHelper.getConfig("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar");
			if (tipusStr != null && !tipusStr.isEmpty()) {
				tipus = EnviamentSirTipusDocumentEnviarEnumDto.valueOf(tipusStr);
			}
		} catch (Exception ex) {
			log.error("No s'ha pogut obtenir el tipus de document a enviar per a l'enviament SIR per defecte. S'utilitzarà el tipus TOT (CSV i binari).");
		}
		return tipus;
	}

}
