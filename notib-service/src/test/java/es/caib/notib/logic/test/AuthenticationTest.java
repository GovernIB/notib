package es.caib.notib.logic.test;

import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AuthenticationTest {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UsuariRepository usuariRepository;

    @Transactional
    public void autenticarUsuari(String usuariCodi) {
        log.debug("Autenticant usuari " + usuariCodi + "...");
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuariCodi);
        org.springframework.security.core.Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        UsuariEntity usuariEntity = usuariRepository.findById(usuariCodi).orElse(null);
        if (usuariEntity == null) {
            usuariRepository.save(
                    UsuariEntity.getBuilder(
                            usuariCodi,
                            usuariCodi + "@mail.com",
                            "CA")
                            .nom(usuariCodi)
                            .llinatges(usuariCodi)
                            .build());
        }
        log.debug("... usuari " + usuariCodi + " autenticat correctament");
    }
}
