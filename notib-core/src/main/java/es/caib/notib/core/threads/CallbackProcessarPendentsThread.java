package es.caib.notib.core.threads;

import es.caib.notib.core.helper.CallbackHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.concurrent.Callable;

@Slf4j
public class CallbackProcessarPendentsThread implements Callable<Boolean> {

    protected Long enviamentId;
    protected CallbackHelper callbackHelper;
    @Getter
    public boolean error;

    public CallbackProcessarPendentsThread(Long enviamentId, CallbackHelper callbackHelper) {
        this.enviamentId = enviamentId;
        this.callbackHelper= callbackHelper;
    }

    @Override
    public Boolean call() throws Exception {

        log.info("[REG] >>> Realitzant registre de la notificació amb id enviamentId");
        try {
            return callbackHelper.notifica(enviamentId);
        } catch (Exception e) {
            error = true;
            log.error(String.format("[Callback] L'enviament [Id: %d] ha provocat la següent excepcio:", enviamentId), e);
            callbackHelper.marcarEventNoProcessable(enviamentId, e.getMessage(), ExceptionUtils.getStackTrace(e));
            return error;
        }
    }
}
