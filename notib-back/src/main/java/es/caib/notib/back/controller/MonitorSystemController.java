package es.caib.notib.back.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.back.helper.MonitorHelper;
import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.logic.intf.monitor.MonitorTascaInfo;
import es.caib.notib.logic.intf.service.MonitorTasquesService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador per la gestió d'perfils
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/monitor")
public class MonitorSystemController extends BaseController {

	@Autowired
	private MonitorTasquesService monitorTasquesService;

	
	@GetMapping
	public String get(HttpServletRequest request, Model model) {
		return "monitor";
	}

	@GetMapping(value = "/all")
	@ResponseBody
	public String monitor(HttpServletRequest request, String familia) throws JsonProcessingException {

		var mapper = new ObjectMapper();
		return mapper.writeValueAsString(ejecutar(request));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String,List<String>> ejecutar(HttpServletRequest request) {
		
		Map<String, List<String>> mjson = new LinkedHashMap<>();
		List<String> sistema = new ArrayList<>();
		List<String> hilo = new ArrayList<>();
		List<String> cputime = new ArrayList<>();
		List<String> estado = new ArrayList<>();
		List<String> espera = new ArrayList<>();
		List<String> blockedtime = new ArrayList<>();

		var bean = ManagementFactory.getThreadMXBean();
		sistema.add(getMessage(request, "monitor.procesadores")+": " + Runtime.getRuntime().availableProcessors());
		sistema.add(getMessage(request, "monitor.memoria_disponible")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory()));
		sistema.add(getMessage(request, "monitor.memoria_maxima")+": " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : MonitorHelper.humanReadableByteCount(Runtime.getRuntime().maxMemory())));
		sistema.add(getMessage(request, "monitor.memoria_total")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory()));
		sistema.add(getMessage(request, "monitor.os-name")+": " + MonitorHelper.getName());
		sistema.add(getMessage(request, "monitor.os-arch") + ": " + MonitorHelper.getArch());
		sistema.add(getMessage(request, "monitor.os-version") + ": " + MonitorHelper.getVersion());
		sistema.add(getMessage(request, "monitor.carga_cpu") + ": " + MonitorHelper.getCPULoad());
		
		for (var root : File.listRoots()) {
			sistema.add(getMessage(request, "monitor.space.total") + " " + root.getAbsolutePath()+": " + MonitorHelper.humanReadableByteCount(root.getTotalSpace()));
			sistema.add(getMessage(request, "monitor.space.free") + " " + root.getAbsolutePath()+": " + MonitorHelper.humanReadableByteCount(root.getFreeSpace()));
		}
        
		int numDeadlocked = 0; 
		if (bean.findMonitorDeadlockedThreads() != null) {
			numDeadlocked = bean.findMonitorDeadlockedThreads().length;
		}
		sistema.add(getMessage(request, "monitor.deadlocked")+": " + numDeadlocked);
		sistema.add(getMessage(request, "monitor.daemon_thread")+": " + bean.getDaemonThreadCount());
		bean.resetPeakThreadCount();
		if (bean.isThreadCpuTimeSupported()) {
			long[] ids = bean.getAllThreadIds();
			var info = bean.getThreadInfo(ids);
			Set hs = new HashSet();
			for (var a = 0; a < ids.length; ++a) {
				hs.add(bean.getThreadCpuTime(ids[a]));
			}
			long tiempoCPUTotal =  ((Long)Collections.max(hs)).longValue();
			String nombre;
			long tiempoCPU;
			for (var a = 0; a < ids.length; ++a) {
				nombre = (info[a].getLockName() == null ? info[a].getThreadName() : info[a].getLockName());
				if ("main".equals(nombre)) {
					continue;
				}
				hilo.add(nombre);
				tiempoCPU = (long) (100*((float) bean.getThreadCpuTime(ids[a]) / (float) tiempoCPUTotal));
				cputime.add(((tiempoCPU>100)?100:tiempoCPU) + " %");
				estado.add(getMessage(request, "monitor."+info[a].getThreadState()));
				espera.add(((info[a].getWaitedTime() == -1)? 0:info[a].getWaitedTime()) + " ns");
				blockedtime.add(((info[a].getBlockedTime() == -1)? 0:info[a].getBlockedTime()) + " ns");
			}
		}
		mjson.put("sistema", sistema);
		mjson.put("hilo", hilo);
		mjson.put("cputime", cputime);
		mjson.put("estado", estado);
		mjson.put("espera", espera);
		mjson.put("blockedtime", blockedtime);
		return mjson;
	}

	@SuppressWarnings("unchecked")
	@GetMapping(value="/tasques")
	@ResponseBody
	public List<TasquesSegonPlaInfo> getTasquesJson(HttpServletRequest request) {

		List<TasquesSegonPlaInfo> tasquesSegonPlaInfos = new ArrayList<>();
		var monitorTasques = monitorTasquesService.findAll();
		var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (monitorTasques == null) {
			return tasquesSegonPlaInfos;
		}
		for (var monitorTasca : monitorTasques) {

			var iniciExecucio = monitorTasca.getDataInici() != null ? sdf.format(monitorTasca.getDataInici()) : "-";
			var properaExecucio = !MonitorTascaEstat.EN_EXECUCIO.equals(monitorTasca.getEstat()) && monitorTasca.getProperaExecucio() != null ?
					sdf.format(monitorTasca.getProperaExecucio()) : "-";

			tasquesSegonPlaInfos.add(TasquesSegonPlaInfo.builder()
					.codi(monitorTasca.getCodi())
					.estat(getMessage(request, "monitor.tasques.estat." + monitorTasca.getEstat()))
					.iniciExecucio(getMessage(request, "monitor.tasques.darrer.inici") + ": " + iniciExecucio)
					.tempsExecucio(getMessage(request, "monitor.tasques.temps.execucio") + ": " + monitorTasca.getTempsExecucio())
					.properaExecucio(getMessage(request, "monitor.tasques.propera.execucio") + ": " + properaExecucio)
					.observacions(getMessage(request, "monitor.tasques.observacions") + ": " + monitorTasca.getObservacions())
					.build());
		}
		return tasquesSegonPlaInfos;
	}

	@Builder
	@Getter
	public static class TasquesSegonPlaInfo {

		private String codi;
		private String estat;
		private String iniciExecucio;
		private String tempsExecucio;
		private String properaExecucio;
		private String observacions;
	}
}
