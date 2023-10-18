package es.caib.notib.logic.handler;

import es.caib.notib.logic.helper.EmailNotificacioHelper;
import es.caib.notib.persist.entity.NotificacioEntity;
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

        var t = new Thread(() -> {
            try {
                emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
            } catch (Exception ex) {
                log.error("Error enviant els emails per la notificacio " + notificacio.getId(), ex);
            }
        });
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
