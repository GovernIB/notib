package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioPeticions;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class AbstractPluginHelper<T> {

	protected final IntegracioHelper integracioHelper;
	protected final ConfigHelper configHelper;

	protected IntegracioPeticions peticionsPlugin = IntegracioPeticions.builder().organOk(new HashMap<>()).organError(new HashMap<>()).build();
	protected Map<String, T> pluginMap = new HashMap<>();


	public AbstractPluginHelper(IntegracioHelper integracioHelper, ConfigHelper configHelper) {

		this.integracioHelper = integracioHelper;
		this.configHelper = configHelper;
	}

	public abstract boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception;

	protected String getCodiEntitatActual() {

		var codiEntitat = configHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi de l'entitat no pot ser null");
		}
		return codiEntitat;
	}

	public IntegracioPeticions getPeticionsPluginAndReset() {

		IntegracioPeticions peticions = IntegracioPeticions.builder()
				.totalOk(peticionsPlugin.getTotalOk())
				.totalError(peticionsPlugin.getTotalError())
				.organOk(peticionsPlugin.getOrganOk())
				.organError(peticionsPlugin.getOrganError())
				.build();
		peticionsPlugin = IntegracioPeticions.builder().build();
		return peticions;
	}

	public EstatSalut getEstatPlugin() {

		try {
			Instant start = Instant.now();
			EstatSalutEnum estat = getEstat();
			Instant end = Instant.now();
			long latency = Duration.between(start, end).toMillis();
			return EstatSalut.builder().latencia(latency).estat(estat).build();
		} catch (Exception ex) {
			return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
		}
	}

	public void resetPlugin() {
		pluginMap = new HashMap<>();
	}

	abstract protected T getPlugin();
	abstract protected String getPluginClassProperty();
	abstract protected EstatSalutEnum getEstat();

}
