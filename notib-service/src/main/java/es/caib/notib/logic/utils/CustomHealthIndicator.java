package es.caib.notib.logic.utils;


import es.caib.notib.logic.intf.service.AplicacioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomHealthIndicator implements HealthIndicator {


    private final AplicacioService aplicacioService;

    public CustomHealthIndicator(AplicacioService aplicacioService) {

        this.aplicacioService = aplicacioService;
    }

    @Override
    public Health health() {

        boolean isHealthy = checkAplicacioService();
        return  isHealthy ? Health.up().build() : Health.down().withDetail("Error", "Servei caigut").build();
    }

    private boolean checkAplicacioService() {

        try {
            aplicacioService.existeixUsuariNotib("test");
            return true; // or false based on your checks
        } catch (Exception ex) {
            log.error("Error al consultar l'aplicacio. ", ex);
            return false;
        }
    }
}