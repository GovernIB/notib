package es.caib.notib.logic.clases;

import es.caib.notib.logic.helper.CallbackHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class CallbackProcessarPendentsThread implements Callable<Boolean> {

    protected Long eventId;
    protected CallbackHelper callbackHelper;
    @Getter
    public boolean error;

    public CallbackProcessarPendentsThread(Long eventId, CallbackHelper callbackHelper) {
        this.eventId = eventId;
        this.callbackHelper= callbackHelper;
    }

    @Override
    public Boolean call() throws Exception {

        log.info("[REG] >>> Realitzant registre de la notificació amb id " + eventId);
        try {
            return callbackHelper.notifica(eventId);
        } catch (Exception e) {
            error = true;
            log.error("Error registrant la notificació amb id " + eventId, e);
            throw new RuntimeException(e);
        }
    }
}
