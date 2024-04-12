package es.caib.notib.plugin.utils;

import org.slf4j.Logger;

public class NotibLoggerPlugin {

    private boolean mostrarLogs = false;

    private Logger log;

    public NotibLoggerPlugin(Logger log) {
        this.log = log;
    }

    public void setMostrarLogs(Boolean mostrarLogs) {
        this.mostrarLogs = mostrarLogs;
    }

    public void info(String msg) {

        try {
            if (mostrarLogs) {
                log.info(msg);
            }
        } catch (Exception ex) {
            log.error("Error creant el log ", ex);
        }
    }

    public void error(String msg, Exception exception) {

        try {
            if (mostrarLogs) {
                log.error(msg, exception);
            }
        } catch (Exception ex) {
            log.error("Error creant el log ", ex);
        }
    }

}
