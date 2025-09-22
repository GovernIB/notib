package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.ms.broker.model.Integracio;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.SalutPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class AbstractPluginHelper<T extends SalutPlugin> {

	protected final static String GLOBAL = "GLOBAL";

	protected final IntegracioHelper integracioHelper;
	protected final ConfigHelper configHelper;
	protected final EntitatRepository entitatRepository;
    protected final MeterRegistry meterRegistry;

//	protected IntegracioPeticions peticionsPlugin = IntegracioPeticions.builder().organOk(new HashMap<>()).organError(new HashMap<>()).build();
	protected Map<String, T> pluginMap = new HashMap<>();

	public AbstractPluginHelper(IntegracioHelper integracioHelper,
								ConfigHelper configHelper,
								EntitatRepository entitatRepository,
                                MeterRegistry meterRegistry) {

		this.integracioHelper = integracioHelper;
		this.configHelper = configHelper;
		this.entitatRepository = entitatRepository;
        this.meterRegistry = meterRegistry;
	}

	protected String getCodiEntitatActual() {

		var codiEntitat = configHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi de l'entitat no pot ser null");
		}
		return codiEntitat;
	}

	public void resetPlugin() {
		pluginMap = new HashMap<>();
	}

	public IntegracioSalut getIntegracionsSalut() {

		Map<String, IntegracioSalut> integracionsMap = createIntegracionsFromPlugins();
        if (integracionsMap.size() == 1) {
            var entry = integracionsMap.entrySet().iterator().next();
            return entry.getValue();
        }
        // if nomes te un elment retornar-lo
        // TODO AQUI FER LA LOGICA PER EMPLENAR EL MAP  si integracionsMap té mes d'un element. Sino ja retornar tal com esta
        // crear nou integracio salut, posar codi plugin i anar afegin la info de cada una de les IntegracioSalut del integracionsMap
        var keys = integracionsMap.keySet();
        Map<String, IntegracioPeticions> peticionsMap = new HashMap<>();
        var totalOk = 0L;
        var totalError = 0L;
        var peticionsOkUltimPeriode = 0L;
        var peticionsErrorUltimPeriode = 0L;
        var totalTempsMig = 0;
        var tempsMigUltimPeriode = 0;
        for (var entitatCodi : keys) {
            var integracionsEntitat = integracionsMap.get(entitatCodi);
            var peticionsEntitat = integracionsEntitat.getPeticions();
            peticionsMap.put(entitatCodi, peticionsEntitat);
            totalOk += peticionsEntitat.getTotalOk();
            totalError += peticionsEntitat.getTotalError();
            peticionsOkUltimPeriode += peticionsEntitat.getPeticionsOkUltimPeriode();
            peticionsErrorUltimPeriode += peticionsEntitat.getPeticionsErrorUltimPeriode();
            totalTempsMig += peticionsEntitat.getTotalTempsMig();
            tempsMigUltimPeriode += peticionsEntitat.getTempsMigUltimPeriode();
        }
        var peticions = IntegracioPeticions.builder()
                .totalOk(totalOk)
                .totalError(totalError)
                .peticionsOkUltimPeriode(peticionsOkUltimPeriode)
                .totalTempsMig(totalTempsMig)
                .peticionsErrorUltimPeriode(peticionsErrorUltimPeriode)
                .peticionsPerEntorn(peticionsMap)
                .tempsMigUltimPeriode(tempsMigUltimPeriode)
                .build();
//        var codi = !integracionsMap.isEmpty() ? integracionsMap.get(keys.iterator().next()).getCodi() : "";
        var codi = getCodiApp().name();
        return IntegracioSalut.builder().codi(codi).peticions(peticions).build();
	}

	private Map<String, IntegracioSalut> createIntegracionsFromPlugins() {

		Map<String, IntegracioSalut> integracioResult = new HashMap<>();
		String codiIntegracio = getCodiApp().name();

		pluginMap.forEach((codiEntitat, plugin) -> {
			EstatSalut estatSalut = plugin.getEstatPlugin();

			if (plugin.teConfiguracioEspecifica()) {
				integracioResult.put(codiEntitat, createIntegracioForPlugin(codiIntegracio, codiEntitat, estatSalut, plugin));
			} else {
				mergeGlobalIntegracio(integracioResult, plugin, estatSalut, codiIntegracio);
			}
		});

        return integracioResult;
	}

	private static <T extends SalutPlugin> void mergeGlobalIntegracio(Map<String, IntegracioSalut> integracioResult, T plugin, EstatSalut estatSalut, String codiIntegracio) {
		integracioResult.merge(GLOBAL,
				IntegracioSalut.builder()
						.codi(codiIntegracio)
						.estat(estatSalut.getEstat())
						.latencia(estatSalut.getLatencia())
						.peticions(plugin.getPeticionsPlugin())
						.build(),
				(existing, nou) -> {
					existing.getPeticions().setTotalOk(existing.getPeticions().getTotalOk() + nou.getPeticions().getTotalOk());
					existing.getPeticions().setTotalError(existing.getPeticions().getTotalError() + nou.getPeticions().getTotalError());
					return existing;
				});
	}

	private IntegracioSalut createIntegracioForPlugin(String codiIntegracio, String codiEntitat, EstatSalut estatSalut, T plugin) {
		return IntegracioSalut.builder()
				.codi(setFormatIntegracio(codiIntegracio, codiEntitat, 16))
				.estat(estatSalut.getEstat())
				.latencia(estatSalut.getLatencia())
				.peticions(plugin.getPeticionsPlugin())
				.build();
	}

	private void addFilteredEntitiesToIntegracions(Map<String, IntegracioSalut> integracionsMap) {
		String codiIntegracio = getCodiApp().name();

		getEntitatsFiltrades().stream()
				.filter(entitat -> (entitat.isConfiguracioEspecifica() && !integracionsMap.containsKey(entitat.getCodi())) || (!entitat.isConfiguracioEspecifica() && !integracionsMap.containsKey(GLOBAL)))
				.forEach(entitat -> integracionsMap.put(entitat.getCodi(),
						IntegracioSalut.builder()
								.codi(entitat.isConfiguracioEspecifica() ? setFormatIntegracio(codiIntegracio, entitat.getCodi(), 16) : codiIntegracio)
								.estat(EstatSalutEnum.UNKNOWN)
								.build()));
	}


	public List<IntegracioInfo> getIntegracionsInfo() {

		List<CodiBool> entitatsFiltrades = getEntitatsFiltrades();
		return entitatsFiltrades.stream()
				.map(entitat -> obtenirIntegracioInfo(entitat))
				.collect(Collectors.toList());
	}

	private IntegracioInfo obtenirIntegracioInfo(CodiBool entitat) {
		boolean showInfoEspecifica = entitat.isConfiguracioEspecifica();
		String codiIntegracio = getCodiApp().name();
		String nomIntegracio = getCodiApp().getNom();

		return IntegracioInfo.builder()
				.codi(showInfoEspecifica ? setFormatIntegracio(codiIntegracio, entitat.getCodi(), 16) : codiIntegracio)
				.nom(showInfoEspecifica ? setFormatIntegracio(nomIntegracio, entitat.getCodi(), 255) : nomIntegracio)
				.build();
	}


	private List<CodiBool> getEntitatsFiltrades() {
		AtomicBoolean foundFirstWithConfig = new AtomicBoolean(false);
		AtomicBoolean hasConfiguracioEspecifica = new AtomicBoolean(false);

		List<CodiBool> codisFiltrats = entitatRepository.findCodiAllActives().stream()
				.filter(codiEntitat -> shouldInclude(codiEntitat, foundFirstWithConfig, hasConfiguracioEspecifica))
				.map(codiEntitat -> CodiBool.builder().codi(codiEntitat).configuracioEspecifica(hasConfiguracioEspecifica.get()).build())
				.collect(Collectors.toList());
		return codisFiltrats;
	}

	private boolean shouldInclude(String codiEntitat, AtomicBoolean foundFirstWithConfig, AtomicBoolean hasConfiguracioEspecifica) {
		boolean hasConfigEspecifica = pluginMap.get(codiEntitat) != null ?
				pluginMap.get(codiEntitat).teConfiguracioEspecifica() :
				configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
		hasConfiguracioEspecifica.set(hasConfigEspecifica);

		if (!hasConfigEspecifica && !foundFirstWithConfig.get()) {
			foundFirstWithConfig.set(true);
			return true; // Inclou el primer que compleix !teConfiguracioEspecifica
		}

		return hasConfigEspecifica; // Inclou només els elements que no compleixen teConfiguracioEspecifica o el primer que sí
	}

	private String setFormatIntegracio(String text, String codiEntitat, int maxLength) {
		String codi = GLOBAL.equals(codiEntitat) ? text : text + "-" + codiEntitat;
		return codi.length() > maxLength ? codi.substring(0, maxLength) : codi;
	}


	abstract protected T getPlugin();
	abstract protected String getPluginClassProperty();
	abstract protected IntegracioApp getCodiApp();
	abstract protected String getConfigGrup();

	public abstract boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception;


	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter @Setter
	public static class CodiBool implements Serializable {
		private String codi;
		private boolean configuracioEspecifica;
	}
}
