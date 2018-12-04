/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.PagadorCieRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;


/**
 * Helper per a la comprovació de l'existencia d'entitats de base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntityComprovarHelper {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Resource
	private PagadorPostalRepository pagadorPostalRepository;
	@Resource
	private PagadorCieRepository pagadorCieRepository;
	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private GrupRepository grupRepository;
	
	@Resource
	private PermisosHelper permisosHelper;



	public void comprovarPermisos(
			Long entitatId,
			boolean comprovarSuper,
			boolean comprovarAdmin,
			boolean comprovarUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean esAdministradorEntitat = false;
		boolean esSuperAdministrador = false;
		boolean esUsuari = false;
		if (comprovarSuper) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_SUPER")) {
					esSuperAdministrador = true;
					break;
				}
			}
		}
		if (comprovarAdmin) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_ADMIN")) {
					esAdministradorEntitat = true;
					break;
				}
			}
		}
		if (comprovarUser) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_USER")) {
					esUsuari = true;
					break;
				}
			}
		}
		if (comprovarAdmin && !esAdministradorEntitat) {
			if (esSuperAdministrador && !esSuperAdministrador) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION");
				}
		} else if (comprovarAdmin && esAdministradorEntitat) {
			return;
		}
		if (esUsuari && !esAdministradorEntitat) {
			if (comprovarSuper && !esSuperAdministrador) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"USUARI");
			}
		}
		if (esUsuari && esAdministradorEntitat && !esSuperAdministrador) {
			throw new PermissionDeniedException(
					entitatId,
					EntitatEntity.class,
					auth.getName(),
					"SUPERUSUARI");
		}
	}

	public EntitatEntity comprovarEntitat(
			Long id) {
		EntitatEntity entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			throw new NotFoundException(
					id,
					EntitatEntity.class);
		}
		return entitat;
	}

	public EntitatEntity comprovarEntitatAplicacio(
			String dir3Codi) {
		EntitatEntity entitat = entitatRepository.findByDir3Codi(dir3Codi);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAll(
				entitat.getId(),
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.APLICACIO},
				auth)) {
			throw new PermissionDeniedException(
					entitat.getId(),
					EntitatEntity.class,
					auth.getName(),
					"APLICACIO");
			
		}
		return entitat;
	}

	public NotificacioEntity comprovarNotificacioAplicacio(
			String referencia) {
		NotificacioEnviamentEntity notificacioEnviament = notificacioEnviamentRepository.findByNotificaReferencia(
				referencia);
		if (notificacioEnviament == null) {
			throw new NotFoundException(
					"ref:" + referencia,
					NotificacioEnviamentEntity.class);
		}
		EntitatEntity entitat = notificacioEnviament.getNotificacio().getEntitat();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAll(
				entitat.getId(),
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.APLICACIO},
				auth)) {
			throw new PermissionDeniedException(
					entitat.getId(),
					EntitatEntity.class,
					auth.getName(),
					"APLICACIO");
			
		}
		return notificacioEnviament.getNotificacio();
	}
	
	public PagadorPostalEntity comprovarPagadorPostal(
			Long pagadorPostalId) {
		PagadorPostalEntity pagadorPostal = pagadorPostalRepository.findOne(pagadorPostalId);
		if (pagadorPostal == null) {
			throw new NotFoundException(
					pagadorPostalId,
					PagadorPostalEntity.class);
		}
		
		return pagadorPostal;
	}
	
	public PagadorCieEntity comprovarPagadorCie(
			Long pagadorCieId) {
		PagadorCieEntity pagadorCie = pagadorCieRepository.findOne(pagadorCieId);
		if (pagadorCie == null) {
			throw new NotFoundException(
					pagadorCieId,
					PagadorPostalEntity.class);
		}
		
		return pagadorCie;
	}
	
	public ProcedimentEntity comprovarProcediment(
			Long procedimentId) {
		ProcedimentEntity procediment = procedimentRepository.findOne(procedimentId);
		if (procediment == null) {
			throw new NotFoundException(
					procedimentId,
					ProcedimentEntity.class);
		}
		
		return procediment;
	}
	
	public GrupEntity comprovarGrup(
			Long grupId) {
		GrupEntity grup = grupRepository.findOne(grupId);
		if (grup == null) {
			throw new NotFoundException(
					grupId,
					GrupEntity.class);
		}
		
		return grup;
	}
	
	public List<GrupEntity> comprovarGrups(
			List<GrupDto> grups) {
		
		List<GrupEntity> grupsEntity = new ArrayList<GrupEntity>();
		
		for (GrupDto grupdto : grups) {
			GrupEntity grup = grupRepository.findOne(grupdto.getId());
			
			grupsEntity.add(grup);
			
			if (grup == null) {
				throw new NotFoundException(
						grupdto.getId(),
						GrupEntity.class);
			}
		}
		return grupsEntity;
	}
}
