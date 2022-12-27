package es.caib.notib.core.clases;

import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.helper.CallbackHelper;
import es.caib.notib.core.helper.NotificacioHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

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
            log.error(String.format("[Callback] L'event [Id: %d] ha provocat la següent excepcio:", eventId), e);
            callbackHelper.marcarEventNoProcessable(eventId, e.getMessage(), ExceptionUtils.getStackTrace(e));
            return error;
        }
    }
}
