/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorCieFormatFullaEntity;
import es.caib.notib.core.entity.PagadorCieFormatSobreEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PagadorCieFormatFullaRepository;
import es.caib.notib.core.repository.PagadorCieFormatSobreRepository;
import es.caib.notib.core.repository.PagadorCieRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.ProcedimentOrganRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;

/**
 * Helper per a la comprovació de l'existencia d'entitats de base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
	private PagadorCieFormatFullaRepository pagadorCieFormatFullaRepository;
	@Resource
	private PagadorCieFormatSobreRepository pagadorCieFormatSobreRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Resource
	private GrupRepository grupRepository;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private GrupProcedimentRepository grupProcedimentRepository;
	@Autowired
	private ProcedimentOrganRepository procedimentOrganRepository;
	
	public EntitatEntity comprovarEntitat(
			Long entitatId,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAdminEntitat,
			boolean comprovarPermisAplicacio) throws NotFoundException {
		return comprovarEntitat(entitatId, comprovarPermisUsuari, comprovarPermisAdminEntitat, comprovarPermisAplicacio, false);
	}
	public EntitatEntity comprovarEntitat(
			Long entitatId,
			boolean comprovarPermisSuper,
			boolean comprovarPermisAdminEntitat,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAplicacio) throws NotFoundException {
		if (entitatId == null) {
			throw new NotFoundException(
					entitatId,
					EntitatEntity.class);
		}
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			throw new NotFoundException(
					entitatId,
					EntitatEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean tePermis = !(comprovarPermisUsuari || comprovarPermisAdminEntitat || comprovarPermisAplicacio);
		
		if (comprovarPermisSuper) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_SUPER")) {
					tePermis = true;
					break;
				}
			}
		}
		if (comprovarPermisUsuari) {
			if (permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.USUARI},
					auth))
				tePermis = true;
		}	
		if (comprovarPermisAdminEntitat) {
			if (permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT},
					auth))
				tePermis = true;
		}
		if (comprovarPermisAplicacio) {
			if (permisosHelper.isGrantedAny(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.APLICACIO},
					auth))
				tePermis = true;
		}
		if (tePermis) {
			return entitat;
		} else {
			throw new PermissionDeniedException(
					entitatId,
					EntitatEntity.class,
					auth.getName(),
					comprovarPermisUsuari ? "USUARI" : comprovarPermisAplicacio ? "APLICACIO" : "ADMINISTRADORENTITAT");
		}
	}
	
	public void comprovarPermisos(
			Long entitatId,
			boolean comprovarSuper,
			boolean comprovarAdmin,
			boolean comprovarUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean tePermis = !(comprovarSuper || comprovarAdmin || comprovarUser);
		if (comprovarSuper) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_SUPER")) {
					tePermis = true;
					break;
				}
			}
		}
		if (comprovarAdmin) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_ADMIN")) {
					tePermis = true;
					break;
				}
			}
		}
		if (comprovarUser) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("tothom")) {
					tePermis = true;
					break;
				}
			}
		}
		
		// Comprovarem que es compleixi algun dels permisos demanats
		if (tePermis) {
			return;
		} else {
			throw new PermissionDeniedException(
					entitatId,
					EntitatEntity.class,
					auth.getName(),
					comprovarUser ? "USUARI" : comprovarAdmin ? "ADMINISTRATION" : "SUPERUSUARI");
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
	
	public PagadorCieFormatFullaEntity comprovarPagadorCieFormatFulla(
			Long formatFullaId) {
		PagadorCieFormatFullaEntity pagadorCieFormatFulla = pagadorCieFormatFullaRepository.findOne(formatFullaId);
		if (pagadorCieFormatFulla == null) {
			throw new NotFoundException(
					formatFullaId,
					PagadorPostalEntity.class);
		}
		
		return pagadorCieFormatFulla;
	}
	
	public PagadorCieFormatSobreEntity comprovarPagadorCieFormatSobre(
			Long formatSobreId) {
		PagadorCieFormatSobreEntity pagadorCieFormatSobre = pagadorCieFormatSobreRepository.findOne(formatSobreId);
		if (pagadorCieFormatSobre == null) {
			throw new NotFoundException(
					formatSobreId,
					PagadorPostalEntity.class);
		}
		
		return pagadorCieFormatSobre;
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
		return comprovarProcediment(entitat.getId(), id);
	}
	
	public ProcedimentEntity comprovarProcediment(
			Long entitatId,
			Long id) {
		
		ProcedimentEntity procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			throw new NotFoundException(
					id,
					ProcedimentEntity.class);
		}
		
		if (entitatId != null && !entitatId.equals(procediment.getEntitat().getId())) {
			throw new ValidationException(
					id,
					ProcedimentEntity.class,
					"L'entitat especificada (id=" + entitatId + ") no coincideix amb l'entitat del procediment");
		}
		
		return procediment;
	}
	
	public void comprovarPermisAdminEntitatOAdminOrgan(
			Long entitatId,
			Long organGestorId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (organGestorId != null) {
			boolean hasPermisAdmin = permisosHelper.isGrantedAll(
					organGestorId, 
					OrganGestorEntity.class, 
					new Permission[] {ExtendedPermission.ADMINISTRADOR}, 
					auth);
			if (!hasPermisAdmin) {
				throw new PermissionDeniedException(
						organGestorId,
						OrganGestorEntity.class,
						auth.getName(),
						"ADMINISTRADOR");
			}
		} else {
			boolean hasPermisAdmin = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT},
					auth);
			if (!hasPermisAdmin) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
		}
		
	}
	
	public void comprovarPermisosOrganGestor(String organCodiDir3) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		OrganGestorEntity organGestorEntity = organGestorRepository.findByCodi(organCodiDir3);
		Boolean hasPermisAdminOrgan = permisosHelper.isGrantedAny(
				organGestorEntity.getId(), 
				OrganGestorEntity.class, 
				new Permission[] {ExtendedPermission.ADMINISTRADOR}, 
				auth);
		if (!hasPermisAdminOrgan)
			throw new PermissionDeniedException(
					organGestorEntity.getId(),
					OrganGestorEntity.class,
					auth.getName(),
					"ADMINISTRADOR");
	}
	
	public OrganGestorEntity comprovarOrganGestor(
			EntitatEntity entitat,
			Long id) {
		
		OrganGestorEntity organGestor = organGestorRepository.findOne(id);
		if (organGestor == null) {
			throw new NotFoundException(
					id,
					OrganGestorEntity.class);
		}
		
		if (entitat != null && !entitat.equals(organGestor.getEntitat())) {
			throw new ValidationException(
					id,
					OrganGestorEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de l'organ gestor");
		}
		
		return organGestor;
	}
	public OrganGestorEntity comprovarOrganGestor(
			EntitatEntity entitat,
			String codi) {
		
		OrganGestorEntity organGestor = organGestorRepository.findByCodi(codi);
		if (organGestor == null) {
			throw new NotFoundException(
					codi,
					OrganGestorEntity.class);
		}
		
		if (entitat != null && !entitat.equals(organGestor.getEntitat())) {
			throw new ValidationException(
					codi,
					OrganGestorEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de l'organ gestor");
		}
		
		return organGestor;
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
			Long entitatId,
			Long procedimentId,
			boolean comprovarPermisConsulta,
			boolean comprovarPermisProcessar,
			boolean comprovarPermisNotificacio,
			boolean comprovarPermisGestio) {
		
		EntitatEntity entitatEntity = comprovarEntitat(entitatId);
		
		return comprovarProcediment(
				entitatEntity,
				procedimentId,
				comprovarPermisConsulta,
				comprovarPermisProcessar,
				comprovarPermisNotificacio,
				comprovarPermisGestio);
	}
	
	public ProcedimentEntity comprovarProcediment(
			EntitatEntity entitat,
			Long procedimentId,
			boolean comprovarPermisConsulta,
			boolean comprovarPermisProcessar,
			boolean comprovarPermisNotificacio,
			boolean comprovarPermisGestio) {
		
		ProcedimentEntity procediment = comprovarProcediment(entitat, procedimentId);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisConsulta) {
			checkPermisProcediment(procediment, auth, PermisEnum.CONSULTA);
		}
		if (comprovarPermisProcessar) {
			checkPermisProcediment(procediment, auth, PermisEnum.PROCESSAR);
		}
		if (comprovarPermisNotificacio) {
			checkPermisProcediment(procediment, auth, PermisEnum.NOTIFICACIO);
		}
		if (comprovarPermisGestio) {
			checkPermisProcediment(procediment, auth, PermisEnum.GESTIO);
		}
		
		return procediment;
	}
	private void checkPermisProcediment(ProcedimentEntity procediment, Authentication auth, PermisEnum permis) {
		boolean granted = hasPermisProcediment(procediment, permis);
		if (!granted) {
			throw new PermissionDeniedException(
					procediment.getId(),
					ProcedimentEntity.class,
					auth.getName(),
					getPermissionName(permis));
		}
	}
	
	public ProcedimentEntity comprovarProcedimentOrgan(
			EntitatEntity entitat,
			Long procedimentId,
			ProcedimentOrganEntity procedimentOrgan,
			boolean comprovarPermisConsulta,
			boolean comprovarPermisProcessar,
			boolean comprovarPermisNotificacio,
			boolean comprovarPermisGestio) {
		
		ProcedimentEntity procediment = comprovarProcediment(entitat, procedimentId);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisConsulta) {
			checkPermisProcedimentOrgan(procedimentOrgan, procediment, auth, PermisEnum.CONSULTA);
		}
		if (comprovarPermisProcessar) {
			checkPermisProcedimentOrgan(procedimentOrgan, procediment, auth, PermisEnum.PROCESSAR);
		}
		if (comprovarPermisNotificacio) {
			checkPermisProcedimentOrgan(procedimentOrgan, procediment, auth, PermisEnum.NOTIFICACIO);
		}
		if (comprovarPermisGestio) {
			checkPermisProcedimentOrgan(procedimentOrgan, procediment, auth, PermisEnum.GESTIO);
		}
		
		return procediment;
	}
	private void checkPermisProcedimentOrgan(
			ProcedimentOrganEntity procedimentOrgan,
			ProcedimentEntity procediment,
			Authentication auth,
			PermisEnum permis) {
		boolean granted = hasPermisProcediment(procediment, permis);
		if (!granted && procediment.isComu())
			granted = hasPermisProcedimentOrgan(procedimentOrgan, permis);
		if (!granted) {
			throw new PermissionDeniedException(
					procediment.getId(),
					ProcedimentEntity.class,
					auth.getName(),
					getPermissionName(permis));
		}
	}
	
	public boolean hasPermisProcediment(
			Long procedimentId,
			PermisEnum permis) {
		ProcedimentEntity procediment = procedimentRepository.findById(procedimentId);
		return hasPermisProcediment(procediment, permis);
	}
	public boolean hasPermisProcediment(ProcedimentEntity procediment, PermisEnum permis) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
		procediments.add(procediment);
		EntitatEntity entitat = procediment.getEntitat();
		
		// 1. Comprovam si el procediment té assignat el permís d'administration
		Permission[] permisos = getPermissionsFromName(permis);
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
		if (!procediments.isEmpty())
			return true;
		
		// 2. Comprovam si algun organ pare del procediment té permis d'administration
		List<OrganGestorEntity> organsGestors = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(
				entitat.getDir3Codi(), 
				procediment.getOrganGestor().getCodi());
		permisosHelper.filterGrantedAny(
				organsGestors,
				new ObjectIdentifierExtractor<OrganGestorEntity>() {
					public Long getObjectIdentifier(OrganGestorEntity organGestor) {
						return organGestor.getId();
					}
				},
				OrganGestorEntity.class,
				permisos,
				auth);
		if (!organsGestors.isEmpty())
			return true;
		
		return false;
	}
	
	public boolean hasPermisProcedimentOrgan(
			Long procedimentOrganId,
			PermisEnum permis) {
		ProcedimentOrganEntity procedimentOrgan = procedimentOrganRepository.findOne(procedimentOrganId);
		return hasPermisProcedimentOrgan(procedimentOrgan, permis);
	}
	public boolean hasPermisProcedimentOrgan(
			ProcedimentOrganEntity procedimentOrgan,
			PermisEnum permis) {
		if (procedimentOrgan != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			List<ProcedimentOrganEntity> procedimentOrgans = new ArrayList<ProcedimentOrganEntity>();
			procedimentOrgans.add(procedimentOrgan);
			
			Permission[] permisos = getPermissionsFromName(permis);
			permisosHelper.filterGrantedAny(
					procedimentOrgans,
					new ObjectIdentifierExtractor<ProcedimentOrganEntity>() {
						public Long getObjectIdentifier(ProcedimentOrganEntity procedimentOrgan) {
							return procedimentOrgan.getId();
						}
					},
					ProcedimentOrganEntity.class,
					permisos,
					auth);
			if (!procedimentOrgans.isEmpty())
				return true;
		}
		return false;
	}
	
	public Permission[] getPermissionsFromName(PermisEnum permis) {
		Permission perm = getPermissionFromName(permis);
		if (perm == null)
			return null;
		else
			return new Permission[] {perm};
	}

	public Permission[] getPermissionsFromName(String permis) {
		Permission perm = getPermissionFromName(permis);
		if (perm == null)
			return null;
		else
			return new Permission[] {perm};
	}
	
	public Permission getPermissionFromName(PermisEnum permis) {
		switch (permis) {
		case CONSULTA: return ExtendedPermission.READ;
		case PROCESSAR: return ExtendedPermission.PROCESSAR;
		case NOTIFICACIO: return ExtendedPermission.NOTIFICACIO;
		case GESTIO: return ExtendedPermission.ADMINISTRATION;
		default: return null;
		}
	}

	public Permission getPermissionFromName(String permis) {
		switch (permis) {
			case "CONSULTA": return ExtendedPermission.READ;
			case "PROCESSAR": return ExtendedPermission.PROCESSAR;
			case "NOTIFICACIO": return ExtendedPermission.NOTIFICACIO;
			case "GESTIO": return ExtendedPermission.ADMINISTRATION;
			default: return null;
		}
	}
	
	public String getPermissionName(PermisEnum permis) {
		switch (permis) {
		case CONSULTA: return "READ";
		case PROCESSAR: return "PROCESSAR";
		case NOTIFICACIO: return "NOTIFICACIO";
		case GESTIO: return "ADMINISTRATION";
		default: return null;
		}
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
	
	@Cacheable(value = "getPermisosEntitatsUsuariActual", key="#auth.name")
	public Map<RolEnumDto, Boolean> getPermisosEntitatsUsuariActual(Authentication auth) {
		List<EntitatEntity> entitatsEntity = entitatRepository.findAll();
		List<OrganGestorEntity> organsGestorsEntity = organGestorRepository.findAll();
		Map<RolEnumDto, Boolean> hasPermisos = new HashMap<RolEnumDto, Boolean>();
		
		Boolean hasPermisUsuariEntitat = permisosHelper.isGrantedAny(
				entitatsEntity, 
				new ObjectIdentifierExtractor<EntitatEntity>() {
					public Long getObjectIdentifier(EntitatEntity entitatEntity) {
						return entitatEntity.getId();
					}
				}, 
				EntitatEntity.class, 
				new Permission[] {ExtendedPermission.USUARI}, 
				auth);
		Boolean hasPermisAdminEntitat = permisosHelper.isGrantedAny(
				entitatsEntity, 
				new ObjectIdentifierExtractor<EntitatEntity>() {
					public Long getObjectIdentifier(EntitatEntity entitatEntity) {
						return entitatEntity.getId();
					}
				}, 
				EntitatEntity.class, 
				new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT}, 
				auth);		
		Boolean hasPermisAplicacioEntitat = permisosHelper.isGrantedAny(
				entitatsEntity, 
				new ObjectIdentifierExtractor<EntitatEntity>() {
					public Long getObjectIdentifier(EntitatEntity entitatEntity) {
						return entitatEntity.getId();
					}
				}, 
				EntitatEntity.class, 
				new Permission[] {ExtendedPermission.APLICACIO}, 
				auth);
		Boolean hasPermisAdminOrgan = permisosHelper.isGrantedAny(
				organsGestorsEntity, 
				new ObjectIdentifierExtractor<OrganGestorEntity>() {
					public Long getObjectIdentifier(OrganGestorEntity organGestorEntity) {
						return organGestorEntity.getId();
					}
				}, 
				OrganGestorEntity.class, 
				new Permission[] {ExtendedPermission.ADMINISTRADOR}, 
				auth);
		
		hasPermisos.put(RolEnumDto.tothom, hasPermisUsuariEntitat);
		hasPermisos.put(RolEnumDto.NOT_ADMIN, hasPermisAdminEntitat);
		hasPermisos.put(RolEnumDto.NOT_APL, hasPermisAplicacioEntitat);
		hasPermisos.put(RolEnumDto.NOT_ADMIN_ORGAN, hasPermisAdminOrgan);

		if (getGenerarLogsPermisosOrgan()) {
			log.info("### PERMISOS - Obtenir Permisos ###########################################");
			log.info("### -----------------------------------------------------------------------");
			log.info("### Usuari: " + auth.getName());
			log.info("### Rols: ");
			if (auth.getAuthorities() != null)
				for (GrantedAuthority authority : auth.getAuthorities()) {
					log.info("### # " + authority.getAuthority());
				}
			log.info("### Permís Usuari: " + hasPermisUsuariEntitat);
			log.info("### Permís Adm entitat: " + hasPermisAdminEntitat);
			log.info("### Permís Adm òrgan: " + hasPermisAdminOrgan);
			log.info("### Permís Aplicació: " + hasPermisAplicacioEntitat);
			log.info("### -----------------------------------------------------------------------");
		}

		return hasPermisos;
		
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

	public boolean getGenerarLogsPermisosOrgan() {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.notib.permisos.organ.logs", false);
	}
	
}
