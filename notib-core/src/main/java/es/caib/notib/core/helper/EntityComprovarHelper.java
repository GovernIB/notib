/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupProcedimentRepository;
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
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Resource
	private GrupRepository grupRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private GrupProcedimentRepository grupProcedimentRepository;

	public EntitatEntity comprovarEntitat(
			Long entitatId,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAdminEntitat,
			boolean comprovarPermisAplicacio) throws NotFoundException {
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			throw new NotFoundException(
					entitatId,
					EntitatEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisUsuari) {
			boolean esLectorEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.USUARI},
					auth);
			if (!esLectorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"USUARI");
			}
		}	
		if (comprovarPermisAdminEntitat) {
			boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT},
					auth);
			if (!esAdministradorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
		}
		if (comprovarPermisAplicacio) {
			boolean esUsuariAplicacio = permisosHelper.isGrantedAny(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.APLICACIO},
					auth);
			if (!esUsuariAplicacio) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"APLICACIO");
			}
		}
		return entitat;
	}
	
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
	
	public GrupProcedimentEntity comprovarGrupProcediment(
			Long grupProcedimentId) {
		GrupProcedimentEntity grupProcediment = grupProcedimentRepository.findOne(grupProcedimentId);
		if (grupProcediment == null) {
			throw new NotFoundException(
					grupProcediment,
					GrupProcedimentEntity.class);
		}
		
		return grupProcediment;
	}
	
	
	public ProcedimentEntity comprovarProcediment(
			EntitatEntity entitat,
			Long id) {
		
		ProcedimentEntity procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			throw new NotFoundException(
					id,
					ProcedimentEntity.class);
		}
		
		if (entitat != null && !entitat.equals(procediment.getEntitat())) {
			throw new ValidationException(
					id,
					ProcedimentEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del procediment");
		}
		
		return procediment;
	}
	public NotificacioEntity comprovarNotificacio(
			EntitatEntity entitat,
			Long id,
			boolean comprovarPermisConsulta,
			boolean comprovarPermisProcessar,
			boolean comprovarPermisNotificacio,
			boolean comprovarPermisGestio) {
		
		NotificacioEntity notificacio = comprovarNotificacio(entitat, id);
		
		comprovarPermisosNotificacio(
				notificacio,
				id,
				comprovarPermisConsulta,
				comprovarPermisProcessar,
				comprovarPermisNotificacio,
				comprovarPermisGestio);
		
		return notificacio;
	}

	public NotificacioEntity comprovarNotificacio(
			EntitatEntity entitat,
			Long id) {
		
		NotificacioEntity notificacio = notificacioRepository.findById(id);
		if (notificacio == null) {
			throw new NotFoundException(
					id,
					ProcedimentEntity.class);
		}
		
		if (entitat != null && !entitat.equals(notificacio.getEntitat())) {
			throw new ValidationException(
					id,
					ProcedimentEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de la notificació");
		}
		
		return notificacio;
	}
	
	public ProcedimentEntity comprovarProcediment(
			EntitatEntity entitat,
			Long id,
			boolean comprovarPermisConsulta,
			boolean comprovarPermisProcessar,
			boolean comprovarPermisNotificacio,
			boolean comprovarPermisGestio) {
		
		ProcedimentEntity procediment = comprovarProcediment(entitat, id);
		
		comprovarPermisosProcediment(
				procediment,
				id,
				comprovarPermisConsulta,
				comprovarPermisProcessar,
				comprovarPermisNotificacio,
				comprovarPermisGestio);
		
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
	
	public void comprovarPermisosProcediment(
			ProcedimentEntity procediment,
			Long procedimentId,
			boolean comprovarPermisConsulta,
			boolean comprovarPermisProcessar,
			boolean comprovarPermisNotificacio,
			boolean comprovarPermisGestio) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisConsulta) {
			boolean granted = permisosHelper.isGrantedAll(
					procediment.getId(),
					ProcedimentEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
			if (!granted) {
				throw new PermissionDeniedException(
						procedimentId,
						ProcedimentEntity.class,
						auth.getName(),
						"READ");
			}
		}
		if (comprovarPermisProcessar) {
			boolean granted = permisosHelper.isGrantedAll(
					procediment.getId(),
					ProcedimentEntity.class,
					new Permission[] {ExtendedPermission.PROCESSAR},
					auth);
			if (!granted) {
				throw new PermissionDeniedException(
						procedimentId,
						ProcedimentEntity.class,
						auth.getName(),
						"PROCESSAR");
			}
		}
		if (comprovarPermisNotificacio) {
			boolean granted = permisosHelper.isGrantedAll(
					procediment.getId(),
					ProcedimentEntity.class,
					new Permission[] {ExtendedPermission.NOTIFICACIO},
					auth);
			if (!granted) {
				throw new PermissionDeniedException(
						procedimentId,
						ProcedimentEntity.class,
						auth.getName(),
						"NOTIFICACIO");
			}
		}
		if (comprovarPermisGestio) {
			boolean granted = permisosHelper.isGrantedAll(
					procediment.getId(),
					ProcedimentEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRATION},
					auth);
			if (!granted) {
				throw new PermissionDeniedException(
						procedimentId,
						ProcedimentEntity.class,
						auth.getName(),
						"ADMINISTRATION");
			}
		}
	}
	public void comprovarPermisosNotificacio(
			NotificacioEntity notificacio,
			Long notificacioId,
			boolean comprovarPermisConsulta,
			boolean comprovarPermisProcessar,
			boolean comprovarPermisNotificacio,
			boolean comprovarPermisGestio) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisConsulta) {
			boolean granted = permisosHelper.isGrantedAll(
					notificacio.getId(),
					NotificacioEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
			if (!granted) {
				throw new SecurityException("Sense permisos per accedir a la notificació ("
						+ "id=" + notificacioId + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
		if (comprovarPermisProcessar) {
			boolean granted = permisosHelper.isGrantedAll(
					notificacio.getId(),
					NotificacioEntity.class,
					new Permission[] {ExtendedPermission.PROCESSAR},
					auth);
			if (!granted) {
				throw new SecurityException("Sense permisos per processar la notificació ("
						+ "id=" + notificacioId + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
		if (comprovarPermisNotificacio) {
			boolean granted = permisosHelper.isGrantedAll(
					notificacio.getId(),
					NotificacioEntity.class,
					new Permission[] {ExtendedPermission.NOTIFICACIO},
					auth);
			if (!granted) {
				throw new SecurityException("Sense permisos per crear una notificació ("
						+ "id=" + notificacioId + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
		if (comprovarPermisGestio) {
			boolean granted = permisosHelper.isGrantedAll(
					notificacio.getId(),
					NotificacioEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRATION},
					auth);
			if (!granted) {
				throw new SecurityException("Sense permisos per gestionar la notificació ("
						+ "id=" + notificacioId + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
	}
	

	public List<ProcedimentDto> findPermisProcedimentsUsuariActualAndEntitat(
			Permission[] permisos,
			EntitatEntity entitatActual) {
		List<ProcedimentDto> resposta = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<ProcedimentEntity> procediments = procedimentRepository.findByEntitat(entitatActual);
		
		permisosHelper.filterGrantedAny(
				procediments,
				new ObjectIdentifierExtractor<ProcedimentEntity>() {
					public Long getObjectIdentifier(ProcedimentEntity procediment) {
						return procediment.getId();
					}
				},
				ProcedimentEntity.class,
				permisos,
				auth);
		
		resposta = conversioTipusHelper.convertirList(
				procediments,
				ProcedimentDto.class);
		
		return resposta;
	}
	public List<ProcedimentDto> findPermisProcediments(
			List<ProcedimentEntity> procediments,
			Permission[] permisos) {
		List<ProcedimentDto> resposta = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAny(
				procediments,
				new ObjectIdentifierExtractor<ProcedimentEntity>() {
					public Long getObjectIdentifier(ProcedimentEntity procediment) {
						return procediment.getId();
					}
				},
				ProcedimentEntity.class,
				permisos,
				auth);
	
		resposta = conversioTipusHelper.convertirList(
				procediments,
				ProcedimentDto.class);
		return resposta;
	}
	
	public List<ProcedimentDto> findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
			List<ProcedimentDto> procediments,
			EntitatEntity entitatActual,
			Permission[] permisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<ProcedimentEntity> procedimentsEntity = new ArrayList<ProcedimentEntity>();
		List<ProcedimentDto> resposta;
		
		//Conversió de dto a entity per comprovar permisos
		for (ProcedimentDto procedimentDto : procediments) {
			ProcedimentEntity procedimentEntity = procedimentRepository.findByIdAndEntitat(
					procedimentDto.getId(),
					entitatActual);
			if (procedimentEntity != null && procedimentEntity.isAgrupar()) {
					procedimentsEntity.add(procedimentEntity);
			}
		}
		permisosHelper.filterGrantedAny(
				procedimentsEntity,
				new ObjectIdentifierExtractor<ProcedimentEntity>() {
					public Long getObjectIdentifier(ProcedimentEntity procedimentsEntity) {
						return procedimentsEntity.getId();
					}
				},
				ProcedimentEntity.class,
				permisos,
				auth);
		
		resposta = conversioTipusHelper.convertirList(
				procedimentsEntity,
				ProcedimentDto.class);
		
		return resposta;
	}
	
	public List<ProcedimentDto> findByPermisConsultaProcedimentsUsuariActual(
			List<ProcedimentDto> procediments,
			Permission[] permisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<ProcedimentEntity> procedimentsEntity = new ArrayList<ProcedimentEntity>();
		List<ProcedimentDto> resposta;
		
		//Conversió de dto a entity per comprovar permisos
		for (ProcedimentDto procedimentDto : procediments) {
			ProcedimentEntity procedimentEntity = procedimentRepository.findOne(procedimentDto.getId());
			procedimentsEntity.add(procedimentEntity);
		}
		permisosHelper.filterGrantedAny(
				procedimentsEntity,
				new ObjectIdentifierExtractor<ProcedimentEntity>() {
					public Long getObjectIdentifier(ProcedimentEntity procedimentsEntity) {
						return procedimentsEntity.getId();
					}
				},
				ProcedimentEntity.class,
				permisos,
				auth);
		
		resposta = conversioTipusHelper.convertirList(
				procedimentsEntity,
				ProcedimentDto.class);
		
		return resposta;
	}
	
	public List<ProcedimentDto> findByPermisProcedimentsUsuariActual(
			List<ProcedimentDto> procediments,
			EntitatEntity entitatActual,
			Permission[] permisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<ProcedimentEntity> procedimentsEntity = new ArrayList<ProcedimentEntity>();
		List<ProcedimentDto> resposta;
		
		//Conversió de dto a entity per comprovar permisos
		for (ProcedimentDto procedimentDto : procediments) {
			ProcedimentEntity procedimentEntity = procedimentRepository.findByIdAndEntitat(
					procedimentDto.getId(),
					entitatActual);
			if (procedimentEntity != null)
				procedimentsEntity.add(procedimentEntity);
		}
		permisosHelper.filterGrantedAny(
				procedimentsEntity,
				new ObjectIdentifierExtractor<ProcedimentEntity>() {
					public Long getObjectIdentifier(ProcedimentEntity procedimentsEntity) {
						return procedimentsEntity.getId();
					}
				},
				ProcedimentEntity.class,
				permisos,
				auth);
		
		resposta = conversioTipusHelper.convertirList(
				procedimentsEntity,
				ProcedimentDto.class);
		
		return resposta;
	}
	public List<EntitatDto> findPermisEntitat(
			Permission[] permisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<EntitatEntity> entitatsEntity = entitatRepository.findAll();
		List<EntitatDto> resposta;
		
		permisosHelper.filterGrantedAny(
				entitatsEntity,
				new ObjectIdentifierExtractor<EntitatEntity>() {
					public Long getObjectIdentifier(EntitatEntity entitatEntity) {
						return entitatEntity.getId();
					}
				},
				EntitatEntity.class,
				permisos,
				auth);
		resposta = conversioTipusHelper.convertirList(
				entitatsEntity,
				EntitatDto.class);
		
		return resposta;
	}
	public List<ProcedimentDto> findGrupProcedimentsUsuariActual() {
		
		return null;
	}
	
}
