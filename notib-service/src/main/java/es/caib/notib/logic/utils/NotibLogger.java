package es.caib.notib.logic.utils;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.objectes.LoggingTipus;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class NotibLogger {

    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private MessageHelper messageHelper;

    @Setter
    private static Map<LoggingTipus, Boolean> logs = new HashMap<>();

    private static NotibLogger INSTANCE = null;

    public static NotibLogger getInstance() {
        return INSTANCE;
    }


    @PostConstruct
    public void postConstruct() {
        INSTANCE = this;
    }

    public static final String PREFIX = "es.caib.notib.log.tipus.";

    public void info(String msg, Logger log, LoggingTipus tipus) {

        if (log == null || !mostrarLog(tipus)) {
            return;
        }
        log.info(msg);
    }

    public void info(String msg, Exception ex, Logger log, LoggingTipus tipus) {

        if (log == null ||!mostrarLog(tipus)) {
            return;
        }
        log.info(msg, ex);
    }

    public void error(String msg, Exception ex, Logger log, LoggingTipus tipus) {

        if (log == null ||!mostrarLog(tipus)) {
            return;
        }
        log.error(msg, ex);
    }

    public void error(String msg, Logger log, LoggingTipus tipus) {

        if (log == null ||!mostrarLog(tipus)) {
            return;
        }
        log.error(msg);
    }

    private boolean mostrarLog(LoggingTipus tipus) {

        if (tipus == null) {
            return false;
        }
        if (!logs.containsKey(tipus)) {
            getLogTipus(tipus);
        }
        return logs.get(tipus);
    }

    public void setLogTipus(String key) {

        if (Strings.isNullOrEmpty(key)) {
            return;
        }
        try {
            var tipus = LoggingTipus.valueOf(key.split(PREFIX)[1]);
            logs.put(tipus, configHelper.getConfigAsBoolean(key));
        } catch (Exception ex) {
            log.error("Error inesperat al obtenir el tipus de la key " + key);
        }
    }

    private void getLogTipus(LoggingTipus tipus) {

        try {
            logs.put(tipus, configHelper.getConfigAsBoolean(PREFIX + tipus));
        } catch (Exception ex) {
            logs.put(tipus, false);
            log.error("Error obtenint la config key ", ex);
        }
    }

    public void printInfoSistema(Logger log, LoggingTipus tipus) {

        if (log == null || !mostrarLog(tipus)) {
            return;
        }
        try {
            log.info("********************* INFO DEL ESTAT DEL SISTEMA *********************");
            log.info("monitor.procesadores " + Runtime.getRuntime().availableProcessors());
            log.info("monitor.memoria_disponible " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory()));
            log.info("monitor.memoria_maxima" + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : MonitorHelper.humanReadableByteCount(Runtime.getRuntime().maxMemory())));
            log.info("monitor.memoria_total " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory()));
            log.info("monitor.os-name " + MonitorHelper.getName());
            log.info("monitor.os-arch " + MonitorHelper.getArch());
            log.info("monitor.os-version " + MonitorHelper.getVersion());
            log.info("monitor.carga_cpu " + MonitorHelper.getCPULoad());

            for (var root : File.listRoots()) {
                log.info("monitor.space.total " + root.getAbsolutePath() + ": " + MonitorHelper.humanReadableByteCount(root.getTotalSpace()));
                log.info("monitor.space.free " + root.getAbsolutePath() + ": " + MonitorHelper.humanReadableByteCount(root.getFreeSpace()));
            }
            List<InfoThread> infoThreads = new ArrayList<>();
            var bean = ManagementFactory.getThreadMXBean();
            if (bean.isThreadCpuTimeSupported()) {
                long[] ids = bean.getAllThreadIds();
                var info = bean.getThreadInfo(ids);
                Set hs = new HashSet();
                for (var a = 0; a < ids.length; ++a) {
                    hs.add(bean.getThreadCpuTime(ids[a]));
                }
                long tiempoCPUTotal = ((Long) Collections.max(hs)).longValue();
                String nombre;
                long tiempoCPU;
                for (var a = 0; a < ids.length; ++a) {
                    if (info[a] == null) {
                        continue;
                    }
                    var inf = new InfoThread();
                    nombre = (info[a].getLockName() == null ? info[a].getThreadName() : info[a].getLockName());
                    if ("main".equals(nombre)) {
                        continue;
                    }
                    inf.setHilo(nombre);
                    tiempoCPU = (long) (100 * ((float) bean.getThreadCpuTime(ids[a]) / (float) tiempoCPUTotal));
                    inf.setCputime(((tiempoCPU > 100) ? 100 : tiempoCPU) + " %");
                    inf.setEstado(messageHelper.getMessage("monitor." + info[a].getThreadState()));
                    inf.setEspera(((info[a].getWaitedTime() == -1) ? 0 : info[a].getWaitedTime()) + " ns");
                    inf.setBlockedtime(((info[a].getBlockedTime() == -1) ? 0 : info[a].getBlockedTime()) + " ns");
                    infoThreads.add(inf);
                }
            }
            log.info(infoThreads.toString());
            log.info("**********************************************************************");
        } catch (Exception ex) {
            log.error("Error mostrant les metriques del sistema", ex);
        }
    }

    @Data
    public static class InfoThread {

        private String sistema;
        private String hilo;
        private String cputime;
        private String estado;
        private String espera;
        private String blockedtime;

    }

}
