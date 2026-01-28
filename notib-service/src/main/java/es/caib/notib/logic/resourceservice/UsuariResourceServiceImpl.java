package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.UsuariResource;
import es.caib.notib.logic.intf.resourceservice.UsuariResourceService;
import es.caib.notib.persist.resourceentity.UsuariResourceEntity;
import es.caib.notib.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementació del servei de gestió d'usuaris de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity> implements UsuariResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UsuariResourceRepository usuariResourceRepository;

	@Override
	public void refresh() {
		UsuariResource usuariFromAuth = getUsuariResourceFromAuth();
		if (usuariFromAuth != null) {
			Optional<UsuariResourceEntity> usuariOptional = usuariResourceRepository.findById(authenticationHelper.getCurrentUserName());
			if (usuariOptional.isPresent()) {
				UsuariResourceEntity usuari = usuariOptional.get();
				usuari.setNomSencer(usuariFromAuth.getNomSencer());
				usuari.setNif(usuariFromAuth.getNif());
				usuari.setEmail(usuariFromAuth.getEmail());
				usuariResourceRepository.save(usuari);
			} else {
				UsuariResourceEntity usuari = UsuariResourceEntity.builder().
					resource(usuariFromAuth).
					build();
				usuariResourceRepository.save(usuari);
			}
		}
	}

	private UsuariResource getUsuariResourceFromAuth() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getPrincipal() != null) {
			if (authentication.getPrincipal() instanceof Jwt) {
				// Authenticació provinent de Spring Boot
				UsuariResource usuariResource = new UsuariResource();
				Jwt jwt = (Jwt) authentication.getPrincipal();
				usuariResource.setCodi(authentication.getName());
				usuariResource.setNomSencer(jwt.getClaimAsString("name"));
				usuariResource.setNif(jwt.getClaimAsString("nif"));
				usuariResource.setEmail(jwt.getClaimAsString("email"));
				return usuariResource;
			} else if (authentication.getPrincipal() instanceof User) {
				UsuariResource usuariResource = new UsuariResource();
				/*WebSecurityConfig.PreauthWebAuthenticationDetails authDetails = (WebSecurityConfig.PreauthWebAuthenticationDetails)authentication.getDetails();
				usuari.setCodi(authDetails.getPreferredUsername());
				usuari.setNom(authDetails.getName());
				usuari.setNif(authDetails.getNif());
				usuari.setEmail(authDetails.getEmail());
				usuari.setRols(authDetails.getOriginalRoles());*/
				return usuariResource;
			}
		}
		return null;
	}

}
