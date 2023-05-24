package es.caib.notib.logic.helper;

import org.apache.log4j.lf5.LogLevel;
import org.slf4j.Logger;

public class LogTimeHelper {

    private Logger logger;
    private long startTime;
    private double elapsedTime;

    public LogTimeHelper(Logger logger) {
        this.logger = logger;
        this.startTime = System.nanoTime();
    }

    public void info(String message) {
        log(LogLevel.INFO, message, true);
    }
    public void infoWithoutTime(String message) {
        log(LogLevel.INFO, message, false);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message, true);
    }
    public void debugWithoutTime(String message) {
        log(LogLevel.DEBUG, message, false);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message, true);
    }
    public void errorWithoutTime(String message) {
        log(LogLevel.ERROR, message, false);
    }

    public void log(LogLevel logLevel, String message, boolean includeTime) {
        if (includeTime) {
            calculateTimes();
        }
        String logMsg = message + (includeTime ? " (Temps de procés: " + elapsedTime +" ms)" : "");

        switch (logLevel.getLabel()) {
            case "INFO" :
                logger.info(logMsg);
                break;
            case "DEBUG" :
                logger.debug(logMsg);
                break;
            case "ERROR" :
                logger.error(logMsg);
                break;
        }
    }

    private void calculateTimes() {
        long currentTime = System.nanoTime();
        elapsedTime = (currentTime - startTime) / 10e6;
        startTime = currentTime;
    }

}
