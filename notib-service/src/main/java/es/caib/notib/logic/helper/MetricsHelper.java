package es.caib.notib.logic.helper;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

@Component
public class MetricsHelper {

//	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private ConfigHelper configHelper;

	public Timer.Context iniciMetrica () {
		if (metricRegistry == null) {
			metricRegistry = new MetricRegistry();
			metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
			metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
			metricRegistry.register("jvm.thread-states", new ThreadStatesGaugeSet());
			metricRegistry.register("jvm.fd.usage", new FileDescriptorRatioGauge());
		}
		Thread currentThread= Thread.currentThread();
		String clazz = null;
		String method = null;
		//El StackTrace de la classe d'on es crida el mètode iniciaMetrica
		if (currentThread.getStackTrace()[2] != null) {
			clazz = Thread.currentThread().getStackTrace()[2].getClassName();
			method = Thread.currentThread().getStackTrace()[2].getMethodName();
		}
		
		if (getGenerarMetriques() && clazz != null && method != null) {
			metricRegistry.counter(MetricRegistry.name(clazz, method + ".count")).inc();
			final Timer timer = metricRegistry.timer(MetricRegistry.name(clazz, method));
			final Timer.Context context = timer.time();
			return context;
		}
		
		return null;
	}
	
	public void fiMetrica(Timer.Context timer) {
		if (getGenerarMetriques()) {
			timer.stop();
		}
	}
	
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
	
	private boolean getGenerarMetriques() {
		return configHelper.getConfigAsBoolean("es.caib.notib.metriques.generar");
	}
}