package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.IntegracioApp;
import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.HibernateHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.AnexoWsDto;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.DatosInteresadoWsDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.InteresadoWsDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.RegistreTipusDocumentDtoEnum;
import es.caib.notib.logic.intf.dto.notificacio.EnviamentSirTipusDocumentEnviarEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.util.EidasValidator;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
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
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import liquibase.pro.packaged.N;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class RegistrePluginHelper extends AbstractPluginHelper<RegistrePlugin> {

	public static final String GRUP = "REGISTRE";

	private final CacheHelper cacheHelper;
	private final ArxiuPluginHelper arxiuPluginHelper;
	private final GestioDocumentalPluginHelper gestioDocumentalPluginHelper;
	private final NotificacioEnviamentRepository notificacioEnviamentRepository;
	private final NotificacioRepository notificacioRepository;

	private static Set<String> blockedObtenirJustificant = null;

	public RegistrePluginHelper(IntegracioHelper integracioHelper,
								ConfigHelper configHelper,
								EntitatRepository entitatRepository,
								@Lazy CacheHelper cacheHelper,
								ArxiuPluginHelper arxiuPluginHelper,
								GestioDocumentalPluginHelper gestioDocumentalPluginHelper,
								NotificacioEnviamentRepository notificacioEnviamentRepository,
								NotificacioRepository notificacioRepository) {

		super(integracioHelper, configHelper, entitatRepository);
		this.cacheHelper = cacheHelper;
		this.arxiuPluginHelper = arxiuPluginHelper;
		this.gestioDocumentalPluginHelper = gestioDocumentalPluginHelper;
        this.notificacioEnviamentRepository = notificacioEnviamentRepository;
		this.notificacioRepository = notificacioRepository;
	}


	// REGISTRE
	// /////////////////////////////////////////////////////////////////////////////////////

	public RespostaConsultaRegistre crearAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio,
														  Long notificacioId, String enviamentIds, boolean generarJustificant) {

		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Enviament notificació a registre (SIR activat)", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Id de la notificacio", String.valueOf(notificacioId)),
				new AccioParam("Ids dels enviaments", enviamentIds),
				new AccioParam("Tipus d'operacio", String.valueOf(tipusOperacio)));

		var resposta = new RespostaConsultaRegistre();
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		var notificacio = notificacioRepository.findById(notificacioId).get();
		info.setNotificacioId(notificacioId);
		info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			resposta = getPlugin().salidaAsientoRegistral(codiDir3Entitat, arb, tipusOperacio, generarJustificant);
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
			resposta.setErrorCodi("3");
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
		}
		return resposta;
	}

	public RespostaConsultaRegistre obtenerAsientoRegistral(String codiDir3Entitat, String numeroRegistreFormatat, Long tipusRegistre,  boolean ambAnnexos) {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Consulta de assentament registral SIR", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat),
				new AccioParam("Tipus de registre", String.valueOf(tipusRegistre)),
				new AccioParam("Amb annexos?", String.valueOf(ambAnnexos)));
		var resposta = new RespostaConsultaRegistre();
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			resposta = getPlugin().obtenerAsientoRegistral(codiDir3Entitat, numeroRegistreFormatat, tipusRegistre, ambAnnexos);
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
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
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
		IntegracioInfo info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir justificant de registre", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat));

		var resposta = new RespostaJustificantRecepcio();
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			resposta = getPlugin().obtenerJustificante(codiDir3Entitat, numeroRegistreFormatat,2);
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
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
		return resposta;
	}
	
	public RespostaJustificantRecepcio obtenirOficiExtern(String codiDir3Entitat, String numeroRegistreFormatat) {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir ofici extern", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Número de registre", numeroRegistreFormatat));

		var resposta = new RespostaJustificantRecepcio();
		EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			resposta = getPlugin().obtenerOficioExterno(codiDir3Entitat, numeroRegistreFormatat);
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
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
		return resposta;
	}
	
	public List<TipusAssumpte> llistarTipusAssumpte(String codiDir3Entitat) throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir llista de tipus d'assumpte", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat));
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var tipusAssumptes = getPlugin().llistarTipusAssumpte(codiDir3Entitat);
			integracioHelper.addAccioOk(info);
			return tipusAssumptes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els tipus d'assumpte";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
	}

	public List<CodiAssumpte> llistarCodisAssumpte(String codiDir3Entitat, String tipusAssumpte) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir la llista de codis d'assumpte", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Tipus d'assumpte", tipusAssumpte));
		var entitat = cacheHelper.findEntitatByCodi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var assumptes = getPlugin().llistarCodisAssumpte(codiDir3Entitat, tipusAssumpte);
			integracioHelper.addAccioOk(info);
			return assumptes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els codis d'assumpte";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
	}
	
	public OficinaDto llistarOficinaVirtual(String codiDir3Entitat, String nomOficinaVirtual, TipusRegistreRegweb3Enum autoritzacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir la oficina virtual", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Tipus de registre", autoritzacio.name()));

		var oficinaDto = new OficinaDto();
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var oficina = getPlugin().llistarOficinaVirtual(codiDir3Entitat, nomOficinaVirtual, autoritzacio.getValor());
			if (oficina != null) {
				oficinaDto.setCodi(oficina.getCodi());
				oficinaDto.setNom(oficina.getNom());
			}
			integracioHelper.addAccioOk(info);
			return oficinaDto;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir la oficina virtual";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
	}
	
	public List<OficinaDto> llistarOficines(String codiDir3Entitat, AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir la llista de oficines", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Tipud de registre", autoritzacio.name()));

		List<OficinaDto> oficinesDto = new ArrayList<>();
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var oficines = getPlugin().llistarOficines(codiDir3Entitat, autoritzacio.getValor());
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
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
	}
	
	public List<LlibreOficina> llistarLlibresOficines(String codiDir3Entitat, String usuariCodi, TipusRegistreRegweb3Enum tipusRegistre) throws SistemaExternException{
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir la llista de llibre amb oficina", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Codi de l'usuari", usuariCodi),
				new AccioParam("Tipud de registre", tipusRegistre.name()));
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var llibresOficines = getPlugin().llistarLlibresOficines(codiDir3Entitat, usuariCodi, tipusRegistre.getValor());
			integracioHelper.addAccioOk(info);
			return llibresOficines;
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar els llibres amb oficina";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
	}
	
	public LlibreDto llistarLlibreOrganisme(String codiDir3Entitat, String organismeCodi) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir la llista de llibres per organisme", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Codi de l'organisme", organismeCodi));

		var llibreDto = new LlibreDto();
		var entitat = cacheHelper.findEntitatByCodi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat + "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var llibre = getPlugin().llistarLlibreOrganisme(codiDir3Entitat, organismeCodi);
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
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
	}
	
	public List<LlibreDto> llistarLlibres(String codiDir3Entitat, String oficina, AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir la llista de llibres d'una oficina", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
				new AccioParam("Oficina", oficina));

		List<LlibreDto> llibresDto = new ArrayList<>();
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var llibres = getPlugin().llistarLlibres(codiDir3Entitat, oficina, autoritzacio.getValor());
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
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
	}
	
	public List<Organisme> llistarOrganismes(String codiDir3Entitat) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Obtenir llista d'organismes", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat));
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			configHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var organismes = getPlugin().llistarOrganismes(codiDir3Entitat);
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			String errorDescripcio = "Error al llistar organismes";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), errorDescripcio, ex);
		}
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
					docDetall = arxiuPluginHelper.arxiuDocumentConsultar(id, null, true, true);
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
						docDetall = arxiuPluginHelper.arxiuDocumentConsultar(id, null, true, false);
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
				gestioDocumentalPluginHelper.gestioDocumentalGet(document.getArxiuGestdocId(), PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, output);
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
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg, ex.getCause());
		}
	}

	public String estatElaboracioToValidesa(DocumentEstatElaboracio estatElaboracio) {

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
	public Integer getModeFirma(Document document, String nom) {
		return nom != null && nom.toLowerCase().endsWith("pdf") && (document.getFirmes() != null && !document.getFirmes().isEmpty()) ? 1 : 0;
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
		var parcial = "";
		if (isComunicacioSir && notificacio.getConcepte().contains("PARCIAL")) {
		}
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
			var tipo = EidasValidator.isFormatEidas(persona.getNif())? "X" : isDocumentEstranger(persona.getNif()) ? "E" : "N";
			interessatDades.setTipoDocumentoIdentificacion(tipo);
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
			var tipo = EidasValidator.isFormatEidas(persona.getNif()) ? "X" : "C";
			interessatDades.setTipoDocumentoIdentificacion(tipo);
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

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {

		var enviament = notificacioEnviamentRepository.findTopByRegistreNumeroFormatatNotNullOrderByIdDesc().orElseThrow();
		var resposta = obtenerAsientoRegistral(enviament.getNotificacio().getEntitat().getDir3Codi(), enviament.getRegistreNumeroFormatat(), 2L,  /*registre sortida*/ false);
		return !resposta.isError();
	}

	@Override
	protected RegistrePlugin getPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}

		var plugin = pluginMap.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPluginClassProperty();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "\"La classe del plugin de registre no està definida\"";
			log.error(msg);
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg);
		}
		try {
			var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
			var propietats = configHelper.getAllEntityProperties(codiEntitat);
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (RegistrePlugin) clazz.getDeclaredConstructor(Properties.class, String.class, boolean.class).newInstance(propietats, codiEntitat, configuracioEspecifica);
			pluginMap.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "\"Error al crear la instància del plugin de registre (\" + pluginClass + \") \"";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg, ex);
		}
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

	// PROPIETATS PLUGIN

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.registre.class");
	}

	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.REG;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}

	private int getSegonsEntreReintentRegistreProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.plugin.registre.segons.entre.peticions");
	}
	public boolean isReadDocsMetadataFromArxiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.documents.metadades.from.arxiu");
	}

	// Mètodes pels tests

	public void setRegistrePlugin(RegistrePlugin registrePlugin) {
		this.pluginMap.put(getCodiEntitatActual(), registrePlugin);
	}

	public void setRegistrePlugin(Map<String, RegistrePlugin> registrePlugin) {
		pluginMap = registrePlugin;
	}

}
