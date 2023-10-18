package es.caib.notib.core.handler;

import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.helper.EmailNotificacioHelper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;

@Builder
@Slf4j
public class EnviamentEmailNotificacioHandler implements TransactionSynchronization {

    private EmailNotificacioHelper emailNotificacioHelper;
    private NotificacioEntity notificacio;

    @Override
    @Transactional(readOnly = true)
    public void afterCommit() {

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
                } catch (Exception ex) {
                    log.error("Error enviant els emails per la notificacio " + notificacio.getId(), ex);
                }
            }
        };
        t.start();
    }

    @Override
    public void suspend() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void flush() {

    }

    @Override
    public void beforeCommit(boolean b) {

    }

    @Override
    public void beforeCompletion() {

    }

    @Override
    public void afterCompletion(int i) {

    }
}
