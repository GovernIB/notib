package es.caib.notib.logic.utils;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.persist.repository.config.ConfigRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class NotibLogger {

    @Autowired
    private ConfigHelper configHelper;

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

}
