package es.caib.notib.logic.helper;

import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.statemachine.AccioMassivaElementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AccioMassivaHelper {

    @Autowired
    private AccioMassivaElementRepository elementRepository;

    public void actualitzar(NotificacioEntity notificacio, String error, String errorStackStrace) {

        for (var enviament : notificacio.getEnviaments()) {
            if (enviament.getAccioMassivaElemId() == null) {
                return;
            }
            var element = elementRepository.findByElementId(enviament.getAccioMassivaElemId());
            element.actualitzar(error, errorStackStrace);
        }
    }

    @Transactional
    public void actualitzar(NotificacioEnviamentEntity enviament, String error, String errorStackStrace) {

            if (enviament.getAccioMassivaElemId() == null) {
                return;
            }
            try {
                var element = elementRepository.findById(enviament.getAccioMassivaElemId()).orElseThrow();
                element.actualitzar(error, errorStackStrace);
                elementRepository.save(element);
            } catch (Exception ex) {
                log.error("[AccioMassivaHelper] Error actualitzant l'element "  + enviament.getAccioMassivaElemId()  +" de l'accio massiva ");
            }
    }

}
