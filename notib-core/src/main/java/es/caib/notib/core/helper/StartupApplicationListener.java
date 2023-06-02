package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.ProcessosInicialsEnum;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.ConfigService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.OrganGestorService;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private OrganGestorService organService;

    public static int counter = 0;

    private Authentication auth;

    @Synchronized
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("Executant processos inicials. Counter: " + counter++);
        addCustomAuthentication();
        try {

            List<ProcessosInicialsEnum> processos = aplicacioService.getProcessosInicialsPendents();
            for (ProcessosInicialsEnum proces : processos) {
                log.info("Executant procés inicial: {}",  proces);
                switch (proces) {
                    case ACTUALITZAR_REFERENCIES:
                        notificacioService.actualitzarReferencies();
                        break;
                    case PROPIETATS_CONFIG_ENTITATS:
                        configService.crearPropietatsConfigPerEntitats();
                        break;
                    case SINCRONITZAR_ORGANS_NOMS_MULTIDIOMA:
                        organService.sincronitzarOrganNomMultidioma();
                        break;
                    default:
                        log.error("Procés inicial no definit");
                        break;
                }
                aplicacioService.updateProcesInicialExecutat(proces);
            }
            configService.actualitzarPropietatsJBossBdd();
        } catch (Exception ex) {
            log.error("Errror executant els processos inicials", ex);
        }
        restoreAuthentication();
    }

    private void addCustomAuthentication() {

        auth = SecurityContextHolder.getContext().getAuthentication();
        Principal principal = new Principal() {
            public String getName() {
                return "INIT";
            }
        };
        List<GrantedAuthority> rols = new ArrayList<>();
        rols.add(new SimpleGrantedAuthority("NOT_SUPER"));
        rols.add(new SimpleGrantedAuthority("NOT_ADMIN"));
        rols.add(new SimpleGrantedAuthority("tothom"));
        Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, "N/A", rols);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void restoreAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
