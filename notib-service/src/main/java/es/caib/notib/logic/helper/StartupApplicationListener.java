package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
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

@Profile("!test")
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
    private OrganGestorService organGestorService;
    @Autowired
    private EnviamentSmService smService;

    private Authentication auth;

    @Synchronized
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("Executant processos inicials.");
        addCustomAuthentication();
        try {

            var processos = aplicacioService.getProcessosInicialsPendents();
            for (var proces : processos) {
                log.info("Executant procés inicial: {}",  proces);
                switch (proces) {
                    case ACTUALITZAR_REFERENCIES:
                        notificacioService.actualitzarReferencies();
                        break;
                    case PROPIETATS_CONFIG_ENTITATS:
                        configService.crearPropietatsConfigPerEntitats();
                        break;
                    case SINCRONITZAR_ORGANS_NOMS_MULTIDIOMA:
                        organGestorService.sincronitzarOrganNomMultidioma(null);
                        break;
                    case AFEGIR_NOTIFICACIONS_MAQUINA_ESTATS:
                        smService.afegirNotificacions();
                        break;
                    default:
                        log.error("Procés inicial no definit");
                        break;
                }
                aplicacioService.updateProcesInicialExecutat(proces);
            }
            configService.actualitzarPropietatsJBossBdd();
            configService.carregarDelaysReintentsRemeses();
        } catch (Exception ex) {
            log.error("Error executant els processos inicials", ex);
        }
        restoreAuthentication();
    }

    private void addCustomAuthentication() {

        auth = SecurityContextHolder.getContext().getAuthentication();
        Principal principal = () -> "INIT";
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
