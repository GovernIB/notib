package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.IntegracioApp;
import es.caib.comanda.salut.model.IntegracioInfo;
import es.caib.comanda.salut.model.IntegracioPeticions;
import es.caib.comanda.salut.model.IntegracioSalut;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.plugin.SalutPlugin;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	protected IntegracioPeticions peticionsPlugin = IntegracioPeticions.builder().organOk(new HashMap<>()).organError(new HashMap<>()).build();
	protected Map<String, T> pluginMap = new HashMap<>();

	public AbstractPluginHelper(IntegracioHelper integracioHelper,
								ConfigHelper configHelper) {

		this.integracioHelper = integracioHelper;
		this.configHelper = configHelper;
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

//	public IntegracioPeticions getPeticionsPluginAndReset() {
//		IntegracioPeticions peticions = IntegracioPeticions.builder()
//				.totalOk(peticionsPlugin.getTotalOk())
//				.totalError(peticionsPlugin.getTotalError())
//				.organOk(peticionsPlugin.getOrganOk())
//				.organError(peticionsPlugin.getOrganError())
//				.build();
//		peticionsPlugin = IntegracioPeticions.builder().build();
//		return peticions;
//	}

//	public EstatSalut getEstatPlugin() {
//
//		try {
//			Instant start = Instant.now();
//			EstatSalutEnum estat = getEstat();
//			Instant end = Instant.now();
//			int latency = (int) Duration.between(start, end).toMillis();
//
//			return EstatSalut.builder()
//					.latencia(latency)
//					.estat(estat)
//					.build();
//		} catch (NotImplementedException ex) {
//
//		} catch (Exception ex) {
//			return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
//		}
//	}

	public List<IntegracioSalut> getIntegracionsSalut() {

		List<String> codisFiltrats = getCodisFiltrats();
		return codisFiltrats.stream()
				.map(codiEntitat -> obtenirIntegracioSalut(codisFiltrats, codiEntitat))
				.collect(Collectors.toList());
	}

	public List<IntegracioInfo> getIntegracionsInfo() {

		List<String> codisFiltrats = getCodisFiltrats();
		return codisFiltrats.stream()
				.map(codiEntitat -> obtenirIntegracioInfo(codisFiltrats, codiEntitat))
				.collect(Collectors.toList());
	}

	@NotNull
	private List<String> getCodisFiltrats() {
		AtomicBoolean foundFirstWithConfig = new AtomicBoolean(false);
		List<String> codisFiltrats = pluginMap.keySet().stream()
				.filter(codiEntitat -> shouldInclude(codiEntitat, foundFirstWithConfig))
				.collect(Collectors.toList());
		return codisFiltrats;
	}

	private boolean shouldInclude(String codiEntitat, AtomicBoolean foundFirstWithConfig) {
		boolean hasConfigEspecifica = pluginMap.get(codiEntitat).teConfiguracioEspecifica();

		if (!hasConfigEspecifica && !foundFirstWithConfig.get()) {
			foundFirstWithConfig.set(true);
			return true; // Inclou el primer que compleix !teConfiguracioEspecifica
		}

		return hasConfigEspecifica; // Inclou només els elements que no compleixen teConfiguracioEspecifica o el primer que sí
	}

	private IntegracioInfo obtenirIntegracioInfo(List<String> codisFiltrats, String codiEntitat) {
		boolean showInfoEspecifica = pluginMap.get(codiEntitat).teConfiguracioEspecifica() && codisFiltrats.size() > 1;
		String codiIntegracio = getCodiApp().name();
		String nomIntegracio = getCodiApp().getNom();

		return IntegracioInfo.builder()
				.codi(showInfoEspecifica ? setFormatIntegracio(codiIntegracio, codiEntitat, 16) : codiIntegracio)
				.nom(showInfoEspecifica ? setFormatIntegracio(nomIntegracio, codiEntitat, 255) : nomIntegracio)
				.build();
	}

	private IntegracioSalut obtenirIntegracioSalut(List<String> codisFiltrats, String codiEntitat) {
		T plugin = pluginMap.get(codiEntitat);
		boolean showInfoEspecifica = plugin.teConfiguracioEspecifica() && codisFiltrats.size() > 1;
		String codiIntegracio = getCodiApp().name();
		EstatSalut estatSalut = plugin.getEstatPlugin();

		return IntegracioSalut.builder()
				.codi(showInfoEspecifica ? setFormatIntegracio(codiIntegracio, codiEntitat, 16) : codiIntegracio)
				.estat(estatSalut.getEstat())
				.latencia(estatSalut.getLatencia())
				.peticions(plugin.getPeticionsPlugin())
				.build();
	}

	private String setFormatIntegracio(String text, String codiEntitat, int maxLength) {
		String codi = GLOBAL.equals(codiEntitat) ? text : text + "-" + codiEntitat;
		return codi.length() > maxLength ? codi.substring(0, maxLength) : codi;
	}


	abstract protected T getPlugin();
	abstract protected String getPluginClassProperty();
	abstract protected IntegracioApp getCodiApp();

}
