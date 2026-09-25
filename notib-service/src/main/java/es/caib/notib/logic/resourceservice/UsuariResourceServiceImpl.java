package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.UsuariResource;
import es.caib.notib.logic.intf.model.auth.NotibAuthenticationDetails;
import es.caib.notib.logic.intf.resourceservice.UsuariResourceService;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.UsuariResourceEntity;
import es.caib.notib.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementació del servei de gestió d'usuaris de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl
	extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity>
	implements UsuariResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UsuariResourceRepository usuariResourceRepository;
	private final CacheHelper cacheHelper;
	private final PermisosHelper permisosHelper;

	@Override
	protected void afterConversion(UsuariResourceEntity entity, UsuariResource resource) {
		// Rols/grups (no els NOT_XXX de l'aplicació, que ja es mostren al selector de rol de la
		// capçalera) que l'usuari té assignats i que s'utilitzen en algun permís (ACL) concedit -es
		// mostren al perfil de l'usuari amb el codi, sense traduir. Només es calcula per l'usuari
		// autenticat actual (l'únic cas d'ús, la seva pròpia pantalla de perfil): calcular-ho també
		// per cada fila d'un llistat d'usuaris hi afegiria una crida al plugin de directori extern per
		// usuari mostrat.
		if (!Objects.equals(entity.getCodi(), authenticationHelper.getCurrentUserName())) {
			return;
		}
		var rols = cacheHelper.findRolsUsuariAmbCodi(entity.getCodi());
		resource.setRolsAmbPermis(permisosHelper.filterRolsAmbAlgunPermis(rols));
	}

	@Override
	public void refresh() {
		UsuariResource usuariFromAuth = getUsuariResourceFromAuth();
		if (usuariFromAuth != null) {
			Optional<UsuariResourceEntity> usuariOptional = usuariResourceRepository.findById(authenticationHelper.getCurrentUserName());
			if (usuariOptional.isPresent()) {
				UsuariResourceEntity usuariFromDb = usuariOptional.get();
				if (hasToUpdateUsuari(usuariFromDb, usuariFromAuth)) {
					usuariFromDb.setNomSencer(usuariFromAuth.getNomSencer());
					usuariFromDb.setNif(usuariFromAuth.getNif());
					usuariFromDb.setEmail(usuariFromAuth.getEmail());
					usuariResourceRepository.save(usuariFromDb);
				}
			} else {
				UsuariResourceEntity usuari = UsuariResourceEntity.builder().
					resource(usuariFromAuth).
					build();
				usuariResourceRepository.save(usuari);
			}
		}
	}

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return "id:'" + authenticationHelper.getCurrentUserName() + "'";
	}

	@Override
	protected void beforeCreateEntity(UsuariResourceEntity entity, UsuariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		beforeCreateUpdate(entity, resource);
	}

	@Override
	protected void beforeUpdateEntity(UsuariResourceEntity entity, UsuariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		beforeCreateUpdate(entity, resource);
	}

	private void beforeCreateUpdate(UsuariResourceEntity entity, UsuariResource resource) {

		if (resource.getIdioma() == null && entity.getIdioma() == null) {
			entity.setIdioma(Idioma.CA);
		}
	}

	private UsuariResource getUsuariResourceFromAuth() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null) {
			if (authentication.getPrincipal() instanceof Jwt) {
				// Authenticació provinent de Spring Boot
				UsuariResource usuariResource = new UsuariResource();
				Jwt jwt = (Jwt)authentication.getPrincipal();
				usuariResource.setCodi(authentication.getName());
				usuariResource.setNomSencer(jwt.getClaimAsString("name"));
				usuariResource.setNif(jwt.getClaimAsString("nif"));
				usuariResource.setEmail(jwt.getClaimAsString("email"));
				usuariResource.setIdioma(Idioma.CA);
				return usuariResource;
			} else if (authentication.getPrincipal() instanceof User) {
				UsuariResource usuariResource = new UsuariResource();
				NotibAuthenticationDetails details = (NotibAuthenticationDetails)authentication.getDetails();
				usuariResource.setCodi(authentication.getName());
				usuariResource.setNomSencer(details.getName());
				usuariResource.setNif(details.getNif());
				usuariResource.setEmail(details.getEmail());
				usuariResource.setIdioma(Idioma.CA);
				return usuariResource;
			}
		}
		return null;
	}

	private boolean hasToUpdateUsuari(UsuariResourceEntity usuariFromDb, UsuariResource usuariFromAuth) {
		return !Objects.equals(usuariFromDb.getNomSencer(), usuariFromAuth.getNomSencer()) ||
			!Objects.equals(usuariFromDb.getNif(), usuariFromAuth.getNif()) ||
			!Objects.equals(usuariFromDb.getEmail(), usuariFromAuth.getEmail());
	}

}
