package es.caib.notib.core.helper;

import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.ProcesosInicialsEntity;
import es.caib.notib.core.repository.ProcessosInicialsRepository;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private NotificacioService notificacioService;

    @Autowired
    private ProcessosInicialsRepository processosInicialsRepository;

    public static int counter = 0;

    @Synchronized
    @Transactional
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {
        counter++;
        if (counter != 2) {
            return;
        }
        log.info("Executant processos inicials");
        try {
            List<ProcesosInicialsEntity> processos = processosInicialsRepository.findProcesosInicialsEntityByInitTrue();
            for (ProcesosInicialsEntity proces : processos) {
                log.info("Executant procés inicial: {}",  proces.getCodi());
                switch (proces.getCodi()) {
                    case ACTUALITZAR_REFERENCIES:
                        notificacioService.actualitzarReferencies();
                        break;
                    default:
                        log.error("Procés inicial no definit");
                        break;
                }
                processosInicialsRepository.updateInit(proces.getId(), false);
            }
        } catch (Exception ex) {
            log.error("Errror executant els processos inicials", ex);
        }
    }
}
