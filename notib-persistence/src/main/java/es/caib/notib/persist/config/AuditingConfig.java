/**
 * 
 */
package es.caib.notib.persist.config;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuració per a les entitats de base de dades auditables.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig implements EnvironmentAware {

	@Setter
	private Environment environment;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private AplicacioService aplicacioService;

	@Bean
	public AuditorAware<UsuariEntity> auditorProvider() {
		return () -> {
			var authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated() || "SCHEDULLER".equals(authentication.getName()) || "anonymousUser".equals(authentication.getName())) {
				return Optional.empty();
			}

			var usuari = usuariRepository.getByCodiReadOnlyNewTransaction(authentication.getName());
			if (!usuari.isEmpty()) {
				return usuari;
			}
			aplicacioService.crearUsuari(authentication.getName());
			return usuariRepository.getByCodi(authentication.getName());
		};
	}

}
