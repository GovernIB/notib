package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
import es.caib.notib.plugin.unitat.OficinaSir;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class UnitatsOrganitzativesPluginHelper extends AbstractPluginHelper<UnitatsOrganitzativesPlugin> {

	public static final String GRUP = "DIR3";

	private final MessageHelper messageManager;

	public UnitatsOrganitzativesPluginHelper(IntegracioHelper integracioHelper,
                                             ConfigHelper configHelper,
											 EntitatRepository entitatRepository,
                                             MessageHelper messageManager,
                                             MeterRegistry meterRegistry) {

		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
		this.messageManager = messageManager;
    }


	// UNITATS ORGANITZATIVES
	// /////////////////////////////////////////////////////////////////////////////////////

	public Map<String, NodeDir3> getOrganigramaPerEntitat(String codiDir3Entitat) throws SistemaExternException {

		log.info("Obtenir l'organigrama per entitat");
		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenir organigrama per entitat",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat));

		var protocol = configHelper.getConfig("es.caib.notib.plugin.unitats.dir3.protocol");
		EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat + "no trobada");
			}
			if (Strings.isNullOrEmpty(configHelper.getEntitatActualCodi())) {
				configHelper.setEntitatCodi(entitat.getCodi());
			}
			info.setCodiEntitat(entitat.getCodi());
			// peticionsPlugin.updatePeticioTotal(entitat.getCodi());
			Map<String, NodeDir3> organigrama;
			if ("SOAP".equalsIgnoreCase(protocol)) {
				log.info("Obtenir l'organigrama per entitat SOAP");
				organigrama = getPlugin().organigramaPerEntitat(codiDir3Entitat, null, null);
			} else {
				log.info("Obtenir l'organigrama per entitat REST");
				organigrama = getPlugin().organigramaPerEntitat(codiDir3Entitat);
			}
			integracioHelper.addAccioOk(info);
			return organigrama;
		} catch (Exception ex) {
			log.info("Error al obtenir l'organigrama per entitat");
			String errorDescripcio = "Error al obtenir l'organigrama per entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

//	public List<NodeDir3> getOrganNomMultidioma(EntitatEntity entitat) {
//		return unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), null, null);
//	}

	public byte[] unitatsOrganitzativesFindByPareJSON(String entitatCodi, String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) {

		var info = new IntegracioInfo(IntegracioCodi.UNITATS, "Obtenier llista JSON d'unitats donat un pare", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("unitatPare", pareCodi));
		try {
			ConfigHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var unitatsOrganitzatives = getPlugin().findAmbPareJson(pareCodi, dataActualitzacio, dataSincronitzacio);
			integracioHelper.addAccioOk(info);
			return unitatsOrganitzatives;
		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	public List<NodeDir3> unitatsOrganitzativesFindByPare(String entitatCodi, String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) {

		var info = new IntegracioInfo(IntegracioCodi.UNITATS, "Consulta llista d'unitats donat un pare", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("unitatPare", pareCodi),
				new AccioParam("fechaActualizacion", dataActualitzacio == null ? null : dataActualitzacio.toString()),
				new AccioParam("fechaSincronizacion", dataSincronitzacio == null ? null : dataSincronitzacio.toString()));
		try {
			ConfigHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var unitatsOrganitzatives = getPlugin().findAmbPare(pareCodi, dataActualitzacio, dataSincronitzacio);
			removeUnitatsSubstitutedByItself(unitatsOrganitzatives);
			if (unitatsOrganitzatives == null || unitatsOrganitzatives.isEmpty()) {
				var errorMissatge = messageManager.getMessage("organgestor.actualitzacio.sense.canvis");
				info.addParam("Resultat", "No s'han obtingut canvis.");
				integracioHelper.addAccioOk(info);
				throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorMissatge);
			}
			integracioHelper.addAccioOk(info);
			return unitatsOrganitzatives;
		} catch (SistemaExternException sex) {
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw sex;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	public NodeDir3 unitatOrganitzativaFindByCodi(String entitatCodi, String codi, Date dataActualitzacio, Date dataSincronitzacio) {

		var info = new IntegracioInfo(IntegracioCodi.UNITATS, "Consulta llista d'unitats donat un codi", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("codi", codi),
				new AccioParam("fechaActualizacion", dataActualitzacio == null ? null : dataActualitzacio.toString()),
				new AccioParam("fechaSincronizacion", dataSincronitzacio == null ? null : dataSincronitzacio.toString()));
		try {
			ConfigHelper.setEntitatCodi(entitatCodi);
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var unitatOrganitzativa = getPlugin().findAmbCodi(codi, dataActualitzacio, dataSincronitzacio);
			integracioHelper.addAccioOk(info);
			return unitatOrganitzativa;
		} catch (SistemaExternException sex) {
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw sex;
		} catch (Exception ex) {
			var error = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(info, error, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), error, ex);
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
		
		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenir llista d'organismes per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'entitat", entitatcodi));

		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(getCodiEntitatActual());
			var organismes = getPlugin().unitatsPerEntitat(entitatcodi, true);
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar organismes per entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}
	
	public String getDenominacio(String codiDir3) {
		
            var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenir denominació d'organisme", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi Dir3 de l'organisme", codiDir3));
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var denominacio = getPlugin().unitatDenominacio(codiDir3);
			integracioHelper.addAccioOk(info);
			return denominacio;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir denominació de organisme";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}
	
	public List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma,
											 Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenir llista de tots els organismes a partir d'un text",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", codi));

		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		// Eliminam espais
		codi = codi != null ? codi.trim() : null;
		municipi = municipi != null ? municipi.trim() : null;
		try {
			if (denominacio != null) {
				denominacio = denominacio.replaceAll(" ", "%20");
			}
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var organismesNodeDir3 = getPlugin().cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
			var organismes = organismesNodeDir3.stream().map(this::toOrganGestorDto).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar organismes  a partir d'un text";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

//	public List<OrganGestorDto> unitatsPerCodi(String codi) throws SistemaExternException {
//		return cercaUnitats(codi,null,null,null,null,null,null,null);
//	}

	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenir llista de tots els organismes a partir d'un text",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", denominacio));

		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var organismesDir3 = getPlugin().unitatsPerDenominacio(denominacio);
			var organismes = organismesDir3.stream().map(this::toOrganGestorDto).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return organismes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar organismes  a partir d'un text";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	private OrganGestorDto toOrganGestorDto(NodeDir3 organ) {

		var cif = !Strings.isNullOrEmpty(organ.getCif()) && !"null".equals(organ.getCif()) ? organ.getCif() : null;
		return OrganGestorDto.builder().codi(organ.getCodi()).nom(organ.getDenominacionCooficial()).nomEs(organ.getDenominacio())
				.estat(organGestorEstatForName(organ.getEstat())).sir(organ.getTieneOficinaSir()).cif(cif).build();
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
		
		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenint llista dels nivells de les administracions", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var nivellsAdministracio = getPlugin().nivellsAdministracio();
			integracioHelper.addAccioOk(info);
			return nivellsAdministracio;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar els nivells de les administracions";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}
	
	public List<CodiValor> llistarComunitatsAutonomes() throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenint llista les comunitats autònomes", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var comunitatsAutonomes = getPlugin().comunitatsAutonomes();
			integracioHelper.addAccioOk(info);
			return comunitatsAutonomes;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar les comunitats autònomes";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	public List<CodiValorPais> llistarPaisos() throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenint llista de països", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var paisos = getPlugin().paisos();
			integracioHelper.addAccioOk(info);
			return paisos;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar països";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	public List<CodiValor> llistarProvincies() throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenint llista de províncies", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var provincies = getPlugin().provincies();
			integracioHelper.addAccioOk(info);
			return provincies;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistat províncies";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}
	
	public List<CodiValor> llistarProvincies(String codiCA) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.UNITATS,"Obtenint llista de províncies", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var provincies = getPlugin().provincies(codiCA);
			integracioHelper.addAccioOk(info);
			return provincies;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistat províncies";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}
	
	public List<CodiValor> llistarLocalitats(String codiProvincia) throws SistemaExternException {
		
		var info = new IntegracioInfo(IntegracioCodi.UNITATS, "Obtenint llista de localitats d'una província", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi de la província", codiProvincia));

		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var localitats = getPlugin().localitats(codiProvincia);
			integracioHelper.addAccioOk(info);
			return localitats;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistat els municipis d'una província";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	public List<OficinaDto> oficinesSIRUnitat(String unitatCodi, Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioCodi.UNITATS, "Obtenir llista de les oficines SIR d'una unitat organitzativa",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", unitatCodi));

		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var oficinesTF = getPlugin().oficinesSIRUnitat(unitatCodi, arbreUnitats);
			var oficinesSIR = oficinesTF.stream().map(o -> toOficinaDto(o)).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return oficinesSIR;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar les oficines d'una unitat organitzativa";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			// peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	public List<OficinaDto> oficinesEntitat(String codiDir3Entitat) throws SistemaExternException {

		var info = new IntegracioInfo(IntegracioCodi.UNITATS, "Obtenir llista de les oficines SIR d'una entitat",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Text de la cerca", codiDir3Entitat));
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			info.setCodiEntitat(entitatCodi);
			// peticionsPlugin.updatePeticioTotal(entitatCodi);
			var oficinesTF = getPlugin().getOficinesEntitat(codiDir3Entitat);
			var oficinesSIR = oficinesTF.stream().map(this::toOficinaDto).collect(Collectors.toList());
			integracioHelper.addAccioOk(info);
			return oficinesSIR;
		} catch (Exception ex) {
			var errorDescripcio = "Error al llistar les oficines SIR d'una entitat";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				// peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorDescripcio, ex);
		}
	}

	private OficinaDto toOficinaDto(OficinaSir oficinaSir) {
		return OficinaDto.builder().codi(oficinaSir.getCodi()).nom(oficinaSir.getNom()).organCodi(oficinaSir.getOrganCodi()).sir(oficinaSir.isSir()).build();
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {

		var entitats = entitatRepository.findAll();
		IntegracioDiagnostic diagnostic;
		var diagnosticOk = true;
		String codi;
		for (var entitat : entitats) {
			codi = entitat.getCodi();
			try {
				var plugin = pluginMap.get(codi);
				if (plugin == null)  {
					continue;
				}
				var unitats = plugin.cercaUnitats(entitat.getDir3Codi(), null, null, null, null, null, null, null);
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setCorrecte(unitats != null && !unitats.isEmpty());
				diagnostics.put(codi, diagnostic);
			} catch(Exception ex) {
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setErrMsg(ex.getMessage());
				diagnostics.put(codi, diagnostic);
				diagnosticOk = false;
			}
		}
		if (diagnostics.isEmpty() && !entitats.isEmpty()) {
			var entitat = entitatRepository.findByCodi(getCodiEntitatActual());
			var unitats = cercaUnitats(entitat.getDir3Codi(), null, null, null, null, null, null, null);
			diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(unitats != null && !unitats.isEmpty());
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}

	@Override
	protected UnitatsOrganitzativesPlugin getPlugin() {

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
			var msg = "La classe del plugin de DIR3 no està configurada";
			log.error(msg);
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg);
		}
		try {
			var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
			var propietats = configHelper.getAllEntityProperties(codiEntitat);
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (UnitatsOrganitzativesPlugin) clazz.getDeclaredConstructor(Properties.class, boolean.class, String.class)
                    .newInstance(propietats, configuracioEspecifica, codiEntitat);
            plugin.init(meterRegistry, getCodiApp().name());
			pluginMap.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin de DIR3 (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg, ex);
		}
	}


	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.unitats.class");
	}

	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.DIR;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}

	public void setUnitatsOrganitzativesPlugin(UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin) {
		this.pluginMap.put(getCodiEntitatActual(), unitatsOrganitzativesPlugin);
	}
	public void setUnitatsOrganitzativesPlugin(Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugin) {
		pluginMap = unitatsOrganitzativesPlugin;
		unitatsOrganitzativesPlugin.forEach((organ, plugin) -> pluginMap.put(organ, plugin));
	}

}
