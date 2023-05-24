package es.caib.notib.back.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.back.helper.MonitorHelper;
import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.logic.intf.monitor.MonitorTascaInfo;
import es.caib.notib.logic.intf.service.MonitorTasquesService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	private MonitorTasquesService monitortasquesService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		return "monitor";
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
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
		
		for (File root : File.listRoots()) {
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
			ThreadInfo[] info = bean.getThreadInfo(ids);
			Set hs = new HashSet();
			for (int a = 0; a < ids.length; ++a) {
				hs.add(bean.getThreadCpuTime(ids[a]));
			}
			long tiempoCPUTotal =  ((Long)Collections.max(hs)).longValue();
			for (int a = 0; a < ids.length; ++a) {
				String nombre = (info[a].getLockName() == null ? info[a].getThreadName() : info[a].getLockName());
				if (!"main".equals(nombre)) {
					hilo.add(nombre);
					long tiempoCPU = (long) ((float)100*((float) bean.getThreadCpuTime(ids[a]) / (float) tiempoCPUTotal));
					cputime.add(((tiempoCPU>100)?100:tiempoCPU) + " %");
					estado.add(getMessage(request, "monitor."+info[a].getThreadState()));
					espera.add(((info[a].getWaitedTime() == -1)? 0:info[a].getWaitedTime()) + " ns");
					blockedtime.add(((info[a].getBlockedTime() == -1)? 0:info[a].getBlockedTime()) + " ns");
				}
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
	@RequestMapping(value="/tasques", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, JSONArray> getTasquesJson(HttpServletRequest request) {

		Map<String, JSONArray> tasques = new HashMap<>();
		JSONArray tasca = new JSONArray();
		JSONArray estat = new JSONArray();
		JSONArray iniciExecucio = new JSONArray();
		JSONArray tempsExecucio = new JSONArray();
		JSONArray properaExecucio = new JSONArray();
		JSONArray observacions = new JSONArray();
		JSONArray identificadors = new JSONArray();

		List<MonitorTascaInfo> monitorTasques = monitortasquesService.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		for(MonitorTascaInfo monitorTasca : monitorTasques) {

			identificadors.put(monitorTasca.getCodi());
			tasca.put(getMessage(request, "monitor.tasques.tasca") + ": " + getMessage(request, "monitor.tasques.tasca.codi." + monitorTasca.getCodi()));
			estat.put(getMessage(request, "monitor.tasques.estat") + ": " + getMessage(request, "monitor.tasques.estat." + monitorTasca.getEstat()));

			String strDataInici = "-";
			if (monitorTasca.getDataInici() != null) {
				strDataInici = sdf.format(monitorTasca.getDataInici());
			}
			iniciExecucio.put(getMessage(request, "monitor.tasques.darrer.inici") + ": " + strDataInici);

			@SuppressWarnings("unused")
			String difDataSegons = "-";
			if (monitorTasca.getDataInici() != null) {
				long difDatas = System.currentTimeMillis() - monitorTasca.getDataInici().getTime();
				difDataSegons = ((int) (difDatas / 1000) % 60) + "s";
			}
			tempsExecucio.put(getMessage(request, "monitor.tasques.temps.execucio") + ": " + monitorTasca.getTempsExecucio());

			String strProperaExecucio = !MonitorTascaEstat.EN_EXECUCIO.equals(monitorTasca.getEstat()) && monitorTasca.getProperaExecucio() != null
										? sdf.format(monitorTasca.getProperaExecucio()) : "-";
			properaExecucio.put(getMessage(request, "monitor.tasques.propera.execucio") + ": " + strProperaExecucio);
			observacions.put(getMessage(request, "monitor.tasques.observacions") + ": " + monitorTasca.getObservacions());
		}
		tasques.put("tasca", tasca);
		tasques.put("estat", estat);
		tasques.put("iniciExecucio", iniciExecucio);
		tasques.put("tempsExecucio", tempsExecucio);
		tasques.put("properaExecucio", properaExecucio);
		tasques.put("observacions", observacions);
		tasques.put("identificadors", identificadors);
		return tasques;
	}
}
