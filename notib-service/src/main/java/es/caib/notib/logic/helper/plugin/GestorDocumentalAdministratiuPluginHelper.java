package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class GestorDocumentalAdministratiuPluginHelper extends AbstractPluginHelper<GestorContingutsAdministratiuPlugin> {

	private final EntitatRepository entitatRepository;

	public GestorDocumentalAdministratiuPluginHelper(IntegracioHelper integracioHelper,
                                                     ConfigHelper configHelper,
                                                     EntitatRepository entitatRepository) {
		super(integracioHelper, configHelper);
		this.entitatRepository = entitatRepository;
	}


	// GESTOR CONTINGUTS ADMINISTRATIU (ROLSAC)
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public List<ProcSerDto> getProcedimentsGda() {

		var info = new IntegracioInfo(IntegracioCodi.GESCONADM,"Obtenir tots els procediments", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			List<ProcSerDto> procediments = new ArrayList<>();
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var procs = getPlugin().getAllProcediments();
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
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
	}
	
	public int getTotalProcediments(String codiDir3Entitat) {

		var info = new IntegracioInfo(IntegracioCodi.GESCONADM,"Recuperant el total de procediments", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
		try {
			if (entitat == null) {
				throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat + "no trobada");
			}
			var entitatCodi = entitat.getCodi();
			info.setCodiEntitat(entitatCodi);
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var totalElements = getPlugin().getTotalProcediments(codiDir3Entitat);
			integracioHelper.addAccioOk(info);
			return totalElements;
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir el número total d'elements";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			if (entitat != null) {
				peticionsPlugin.updatePeticioError(entitat.getCodi());
			}
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
	}

	public List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3) {

		var info = new IntegracioInfo(IntegracioCodi.GESCONADM,"Obtenir procediments per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		List<ProcSerDto> procediments = new ArrayList<>();
		try {
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var procs = getPlugin().getProcedimentsByUnitat(codiDir3);
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
			peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
		return procediments;
	}

	public ProcSerDto getProcSerByCodiSia(String codiSia, boolean isServei) {

		var msg = "Obtenint " + (isServei ? "servei" : "procediment") + " amb codi SIA " + codiSia + " del gestor documental administratiu";
		var info = new IntegracioInfo(IntegracioCodi.GESCONADM, msg, IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var proc = getPlugin().getProcSerByCodiSia(codiSia, isServei);
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
			peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
	}
	
	public List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3, int numPagina) {

		var info = new IntegracioInfo(IntegracioCodi.GESCONADM,"Obtenir procediments per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		List<ProcSerDto> procediments = new ArrayList<>();
		try {
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var procs = getPlugin().getProcedimentsByUnitat(codiDir3, numPagina);
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
			peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
	}

	public int getTotalServeis(String codiDir3) {

		var info = new IntegracioInfo(IntegracioCodi.GESCONADM,"Recuperant el total de serveis", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var totalElements = getPlugin().getTotalServeis(codiDir3);
			integracioHelper.addAccioOk(info);
			return totalElements;
		} catch (Exception ex) {
			var errorDescripcio = "Error al obtenir el número total d'elements";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
	}

	public List<ProcSerDto> getServeisGdaByEntitat(String codiDir3) {

		var info = new IntegracioInfo(IntegracioCodi.GESCONADM,"Obtenir serveis per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			List<ProcSerDto> serveis = new ArrayList<>();
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var servs = getPlugin().getServeisByUnitat(codiDir3);
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
			peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
	}

	public List<ProcSerDto> getServeisGdaByEntitat(String codiDir3, int numPagina) {

		var info = new IntegracioInfo(IntegracioCodi.GESCONADM,"Obtenir serveis per entitat", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var entitatCodi = getCodiEntitatActual();
		info.setCodiEntitat(entitatCodi);
		try {
			List<ProcSerDto> serveis = new ArrayList<>();
			peticionsPlugin.updatePeticioTotal(entitatCodi);
			var servs = getPlugin().getServeisByUnitat(codiDir3, numPagina);
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
			peticionsPlugin.updatePeticioError(entitatCodi);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), errorDescripcio, ex);
		}
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
				var procediments = plugin.getProcedimentsByUnitat(entitat.getDir3Codi());
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setCorrecte(procediments != null && !procediments.isEmpty());
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
			var procediments = getProcedimentsGdaByEntitat(entitat.getDir3Codi());
			diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(procediments != null && !procediments.isEmpty());
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}

	@Override
	protected GestorContingutsAdministratiuPlugin getPlugin() {

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
			var msg = "La classe del plugin del gestor documental administratiu no està configurada";
			log.error(msg);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (GestorContingutsAdministratiuPlugin) clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
			pluginMap.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin de gestor documental administratiu (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioCodi.GESCONADM.name(), msg, ex);
		}
	}

	@Override
	protected EstatSalutEnum getEstat() {
		// TODO: Petició per comprovar la salut
		return EstatSalutEnum.UP;
	}

	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.gesconadm.class");
	}

	// Mètodes pels tests
	public void setGestorDocumentalAdministratiuPlugin(Map<String, GestorContingutsAdministratiuPlugin> gestorDocumentalAdministratiuPlugin) {
		pluginMap = gestorDocumentalAdministratiuPlugin;
	}

}
