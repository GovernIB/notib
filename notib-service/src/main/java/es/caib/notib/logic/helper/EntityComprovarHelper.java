/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.logic.intf.acl.ExtendedPermission;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.PermissionDeniedException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupEntity;
import es.caib.notib.persist.entity.GrupProcSerEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.persist.entity.cie.PagadorCieFormatFullaEntity;
import es.caib.notib.persist.entity.cie.PagadorCieFormatSobreEntity;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorCieFormatFullaRepository;
import es.caib.notib.persist.repository.PagadorCieFormatSobreRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	private ProcSerRepository procSerRepository;
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
	private GrupProcSerRepository grupProcedimentRepository;
	@Autowired
	private ProcSerOrganRepository procedimentOrganRepository;

	public EntitatEntity comprovarEntitat(Long entitatId, boolean comprovarPermisUsuari, boolean comprovarPermisAdminEntitat, boolean comprovarPermisAplicacio) throws NotFoundException {
		return comprovarEntitat(entitatId, comprovarPermisUsuari, comprovarPermisAdminEntitat, comprovarPermisAplicacio, false);
	}
	public EntitatEntity comprovarEntitat(Long entitatId, boolean comprovarPermisSuper, boolean comprovarPermisAdminEntitat, boolean comprovarPermisUsuari, boolean comprovarPermisAplicacio) throws NotFoundException {

		if (entitatId == null) {
			throw new NotFoundException(entitatId, EntitatEntity.class);
		}
		var entitat = entitatRepository.findById(entitatId).orElse(null);
		if (entitat == null) {
			throw new NotFoundException(entitatId, EntitatEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		var tePermis = !(comprovarPermisUsuari || comprovarPermisAdminEntitat || comprovarPermisAplicacio);
		
		if (comprovarPermisSuper) {
			for (var ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_SUPER")) {
					tePermis = true;
					break;
				}
			}
		}
		if (comprovarPermisUsuari && (permisosHelper.isGrantedAll(entitatId, EntitatEntity.class, new Permission[] {ExtendedPermission.USUARI}, auth))) {
				tePermis = true;
		}	
		if (comprovarPermisAdminEntitat && (permisosHelper.isGrantedAll(entitatId, EntitatEntity.class, new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT}, auth))) {
			tePermis = true;
		}
		if (comprovarPermisAplicacio && (permisosHelper.isGrantedAny(entitatId, EntitatEntity.class, new Permission[] {ExtendedPermission.APLICACIO}, auth))) {
			tePermis = true;
		}
		if (!tePermis) {
			var tipus = comprovarPermisUsuari ? "USUARI" : comprovarPermisAplicacio ? "APLICACIO" : "ADMINISTRADORENTITAT";
			throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(), tipus);
		}
		return entitat;
	}
	
	public void comprovarPermisos(Long entitatId, boolean comprovarSuper, boolean comprovarAdmin, boolean comprovarUser) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		var tePermis = !(comprovarSuper || comprovarAdmin || comprovarUser);
		if (comprovarSuper) {
			for (var ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_SUPER")) {
					tePermis = true;
					break;
				}
			}
		}
		if (comprovarAdmin) {
			for (var ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_ADMIN")) {
					tePermis = true;
					break;
				}
			}
		}
		if (comprovarUser) {
			for (var ga: auth.getAuthorities()) {
				if (ga.toString().equals("tothom")) {
					tePermis = true;
					break;
				}
			}
		}
		
		// Comprovarem que es compleixi algun dels permisos demanats
		if (!tePermis) {
			var tipus = comprovarUser ? "USUARI" : comprovarAdmin ? "ADMINISTRATION" : "SUPERUSUARI";
			throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(), tipus);
		}
	}

	public EntitatEntity comprovarEntitat(Long id) {

		var entitat = entitatRepository.findById(id).orElse(null);
		if (entitat == null) {
			throw new NotFoundException(id, EntitatEntity.class);
		}
		return entitat;
	}

	public EntitatEntity comprovarEntitatAplicacio(String dir3Codi) {

		var entitat = entitatRepository.findByDir3Codi(dir3Codi);
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAll(entitat.getId(), EntitatEntity.class, new Permission[] {ExtendedPermission.APLICACIO}, auth)) {
			throw new PermissionDeniedException(entitat.getId(), EntitatEntity.class, auth.getName(), "APLICACIO");
		}
		return entitat;
	}

	public NotificacioEntity comprovarNotificacioAplicacio(String referencia) {

		var notificacioEnviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
		if (notificacioEnviament == null) {
			throw new NotFoundException("ref:" + referencia, NotificacioEnviamentEntity.class);
		}
		var entitat = notificacioEnviament.getNotificacio().getEntitat();
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAll(entitat.getId(), EntitatEntity.class, new Permission[] {ExtendedPermission.APLICACIO}, auth)) {
			throw new PermissionDeniedException(entitat.getId(), EntitatEntity.class, auth.getName(), "APLICACIO");
		}
		return notificacioEnviament.getNotificacio();
	}
	
	public PagadorPostalEntity comprovarPagadorPostal(Long pagadorPostalId) {

		var pagadorPostal = pagadorPostalRepository.findById(pagadorPostalId).orElse(null);
		if (pagadorPostal == null) {
			throw new NotFoundException(pagadorPostalId, PagadorPostalEntity.class);
		}
		return pagadorPostal;
	}
	
	public PagadorCieEntity comprovarPagadorCie(Long pagadorCieId) {

		var pagadorCie = pagadorCieRepository.findById(pagadorCieId).orElse(null);
		if (pagadorCie == null) {
			throw new NotFoundException(pagadorCieId, PagadorPostalEntity.class);
		}
		return pagadorCie;
	}
	
	public PagadorCieFormatFullaEntity comprovarPagadorCieFormatFulla(Long formatFullaId) {

		var pagadorCieFormatFulla = pagadorCieFormatFullaRepository.findById(formatFullaId).orElse(null);
		if (pagadorCieFormatFulla == null) {
			throw new NotFoundException(formatFullaId, PagadorPostalEntity.class);
		}
		return pagadorCieFormatFulla;
	}
	
	public PagadorCieFormatSobreEntity comprovarPagadorCieFormatSobre(Long formatSobreId) {

		var pagadorCieFormatSobre = pagadorCieFormatSobreRepository.findById(formatSobreId).orElse(null);
		if (pagadorCieFormatSobre == null) {
			throw new NotFoundException(formatSobreId, PagadorPostalEntity.class);
		}
		return pagadorCieFormatSobre;
	}
	
	public GrupProcSerEntity comprovarGrupProcediment(Long grupProcedimentId) {

		var grupProcediment = grupProcedimentRepository.findById(grupProcedimentId).orElse(null);
		if (grupProcediment == null) {
			throw new NotFoundException(grupProcediment, GrupProcSerEntity.class);
		}
		return grupProcediment;
	}
	
	public ProcSerEntity comprovarProcediment(EntitatEntity entitat, Long id) {
		return comprovarProcediment(entitat.getId(), id);
	}
	
	public ProcSerEntity comprovarProcediment(Long entitatId, Long id) {

		var procediment = procSerRepository.findById(id).orElse(null);
		if (procediment == null) {
			throw new NotFoundException(id, ProcedimentEntity.class);
		}
		if (entitatId != null && !entitatId.equals(procediment.getEntitat().getId())) {
			throw new ValidationException(id, ProcedimentEntity.class, "L'entitat especificada (id=" + entitatId + ") no coincideix amb l'entitat del procediment");
		}
		return procediment;
	}
	
	public void comprovarPermisAdminEntitatOAdminOrgan(Long entitatId, Long organGestorId) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (organGestorId != null) {
			var hasPermisAdmin = permisosHelper.isGrantedAll(organGestorId, OrganGestorEntity.class, new Permission[] {ExtendedPermission.ADMINISTRADOR}, auth);
			if (!hasPermisAdmin) {
				throw new PermissionDeniedException(organGestorId, OrganGestorEntity.class, auth.getName(), "ADMINISTRADOR");
			}
			return;
		}
		var hasPermisAdmin = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class, new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT}, auth);
		if (!hasPermisAdmin) {
			throw new PermissionDeniedException(entitatId, EntitatEntity.class, auth.getName(), "ADMINISTRADORENTITAT");
		}
	}
	
	public void comprovarPermisosOrganGestor(String organCodiDir3) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		var organGestorEntity = organGestorRepository.findByCodi(organCodiDir3);
		var hasPermisAdminOrgan = permisosHelper.isGrantedAny(organGestorEntity.getId(), OrganGestorEntity.class, new Permission[]{ExtendedPermission.ADMINISTRADOR}, auth);
		if (Boolean.FALSE.equals(hasPermisAdminOrgan)) {
			throw new PermissionDeniedException(organGestorEntity.getId(), OrganGestorEntity.class, auth.getName(), "ADMINISTRADOR");
		}
	}
	
	public OrganGestorEntity comprovarOrganGestor(EntitatEntity entitat, Long id) {
		
		var organGestor = organGestorRepository.findById(id).orElse(null);
		if (organGestor == null) {
			throw new NotFoundException(id, OrganGestorEntity.class);
		}
		if (entitat != null && !entitat.equals(organGestor.getEntitat())) {
			throw new ValidationException(id, OrganGestorEntity.class, "L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de l'organ gestor");
		}
		return organGestor;
	}
	public OrganGestorEntity comprovarOrganGestor(EntitatEntity entitat, String codi) {
		
		var organGestor = organGestorRepository.findByCodi(codi);
		if (organGestor == null) {
			throw new NotFoundException(codi, OrganGestorEntity.class);
		}
		if (entitat != null && !entitat.equals(organGestor.getEntitat())) {
			throw new ValidationException(codi, OrganGestorEntity.class, "L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de l'organ gestor");
		}
		return organGestor;
	}

	public NotificacioEntity comprovarNotificacio(EntitatEntity entitat, Long id) {
		
		var notificacio = notificacioRepository.findById(id).orElse(null);
		if (notificacio == null) {
			throw new NotFoundException(id, NotificacioEntity.class);
		}
		if (entitat != null && !entitat.equals(notificacio.getEntitat())) {
			throw new ValidationException(id, NotificacioEntity.class, "L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de la notificació");
		}
		return notificacio;
	}
	
	public ProcSerEntity comprovarProcediment(Long entitatId, Long procedimentId, boolean comprovarPermisConsulta, boolean comprovarPermisProcessar, boolean comprovarPermisNotificacio, boolean comprovarPermisGestio, boolean comprovarPermisComunicacioSir) {

		var entitatEntity = comprovarEntitat(entitatId);
		return comprovarProcediment(entitatEntity, procedimentId, comprovarPermisConsulta, comprovarPermisProcessar, comprovarPermisNotificacio, comprovarPermisGestio, comprovarPermisComunicacioSir);
	}
	
	public ProcSerEntity comprovarProcediment(EntitatEntity entitat, Long procedimentId, boolean comprovarPermisConsulta, boolean comprovarPermisProcessar, boolean comprovarPermisNotificacio, boolean comprovarPermisGestio, boolean comprovarPermisComunicacioSir) {

		var procediment = comprovarProcediment(entitat, procedimentId);
		var auth = SecurityContextHolder.getContext().getAuthentication();
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
		if (comprovarPermisComunicacioSir) {
			checkPermisProcediment(procediment, auth, PermisEnum.COMUNICACIO_SIR);
		}
		return procediment;
	}

	private void checkPermisProcediment(ProcSerEntity procediment, Authentication auth, PermisEnum permis) {

		var granted = hasPermisProcediment(procediment, permis);
		if (!granted) {
			throw new PermissionDeniedException(procediment.getId(), ProcedimentEntity.class, auth.getName(), getPermissionName(permis));
		}
	}
	
	public ProcSerEntity comprovarProcedimentOrgan(EntitatEntity entitat, Long procedimentId, ProcSerOrganEntity procedimentOrgan, boolean comprovarPermisConsulta, boolean comprovarPermisProcessar, boolean comprovarPermisNotificacio, boolean comprovarPermisGestio, boolean comprovarPermisComunicacioSir) {

		var procediment = comprovarProcediment(entitat, procedimentId);
		var auth = SecurityContextHolder.getContext().getAuthentication();
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
		if (comprovarPermisComunicacioSir) {
			checkPermisProcedimentOrgan(procedimentOrgan, procediment, auth, PermisEnum.COMUNICACIO_SIR);
		}
		return procediment;
	}
	private void checkPermisProcedimentOrgan(ProcSerOrganEntity procedimentOrgan, ProcSerEntity procediment, Authentication auth, PermisEnum permis) {

		// TODO: Comprovar que es correcte --> Al modificar notificació ha donat un error
		boolean granted = hasPermisProcediment(procediment, permis);
		if (!granted && procediment.isComu()) {
			granted = hasPermisProcedimentOrgan(procedimentOrgan, permis);
		}
		if (!granted) {
			throw new PermissionDeniedException(procediment.getId(), ProcedimentEntity.class, auth.getName(), getPermissionName(permis));
		}
	}

	/**
	 * Comprova si l'usuari authenticat té un determinat permís per a un òrgan gestor.
	 *
	 * @param organGestorEntity Organ gestor.
	 * @param permis Permís a comprovar.
	 * @return boleà indicant si té permís.
	 */
	public boolean hasPermisOrganGestor(OrganGestorEntity organGestorEntity, PermisEnum permis) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		var entitat = organGestorEntity.getEntitat();
		Permission[] permisos = getPermissionsFromName(permis);

		var organsGestors = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(entitat.getDir3Codi(), organGestorEntity.getCodi());
		permisosHelper.filterGrantedAny(organsGestors, (ObjectIdentifierExtractor<OrganGestorEntity>) AbstractPersistable::getId, OrganGestorEntity.class, permisos, auth);
		return !organsGestors.isEmpty();
	}

	public boolean hasPermisProcediment(Long procedimentId, PermisEnum permis) {

		var procediment = procSerRepository.findById(procedimentId).orElseThrow();
		return hasPermisProcediment(procediment, permis);
	}

	/**
	 * Comprova si l'usuari té el permís indicat per al procediment indicat per paràmetre o
	 * si l'òrgan gestor del procediment o algún òrgan pare té el permís.
	 *
	 * @param procediment
	 * @param permis
	 * @return
	 */
	public boolean hasPermisProcediment(ProcSerEntity procediment, PermisEnum permis) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		List<ProcSerEntity> procediments = new ArrayList<>();
		procediments.add(procediment);
		// 1. Comprovam si el procediment té assignat el permís d'administration
		Permission[] permisos = getPermissionsFromName(permis);
		permisosHelper.filterGrantedAny(procediments, (ObjectIdentifierExtractor<ProcSerEntity>) AbstractPersistable::getId, ProcedimentEntity.class, permisos, auth);
		if (!procediments.isEmpty()) {
			return true;
		}
		// 2. Comprovam si l'òrgan del procediment o algun organ pare té el permis
		return hasPermisOrganGestor(procediment.getOrganGestor(), permis);
	}
	
	public boolean hasPermisProcedimentOrgan(Long procedimentOrganId, PermisEnum permis) {

		var procedimentOrgan = procedimentOrganRepository.findById(procedimentOrganId).orElseThrow();
		return hasPermisProcedimentOrgan(procedimentOrgan, permis);
	}
	public boolean hasPermisProcedimentOrgan(ProcSerOrganEntity procedimentOrgan, PermisEnum permis) {

		if (procedimentOrgan == null) {
			return false;
		}
			var auth = SecurityContextHolder.getContext().getAuthentication();
			List<ProcSerOrganEntity> procedimentOrgans = new ArrayList<>();
			procedimentOrgans.add(procedimentOrgan);
			Permission[] permisos = getPermissionsFromName(permis);
			permisosHelper.filterGrantedAny(procedimentOrgans, (ObjectIdentifierExtractor<ProcSerOrganEntity>) AbstractPersistable::getId, ProcSerOrganEntity.class, permisos, auth);
		return !procedimentOrgans.isEmpty();
	}
	
	public Permission[] getPermissionsFromName(PermisEnum permis) {

		var perm = getPermissionFromName(permis);
		return perm != null ? new Permission[] {perm} : null;
	}

	public Permission[] getPermissionsFromName(String permis) {

		var perm = getPermissionFromName(permis);
		return perm == null ? new Permission[] {perm} : null;
	}
	
	public Permission getPermissionFromName(PermisEnum permis) {

		switch (permis) {
			case CONSULTA: return ExtendedPermission.READ;
			case PROCESSAR: return ExtendedPermission.PROCESSAR;
			case NOTIFICACIO: return ExtendedPermission.NOTIFICACIO;
			case COMUNICACIO: return ExtendedPermission.COMUNICACIO;
			case COMUNICACIO_SIR: return ExtendedPermission.COMUNICACIO_SIR;
			case GESTIO: return ExtendedPermission.ADMINISTRATION;
			case COMUNS: return ExtendedPermission.COMUNS;
			case ADMIN: return ExtendedPermission.ADMINISTRADOR;
			default: return null;
		}
	}

	public Permission getPermissionFromName(String permis) {

		switch (permis) {
			case "CONSULTA": return ExtendedPermission.READ;
			case "PROCESSAR": return ExtendedPermission.PROCESSAR;
			case "NOTIFICACIO": return ExtendedPermission.NOTIFICACIO;
			case "COMUNICACIO": return ExtendedPermission.COMUNICACIO;
			case "GESTIO": return ExtendedPermission.ADMINISTRATION;
			case "COMUNICACIO_SIR": return ExtendedPermission.COMUNICACIO_SIR;
			case "COMUNS": return ExtendedPermission.COMUNS;
			case "ADMIN": return ExtendedPermission.ADMINISTRADOR;
			default: return null;
		}
	}
	
	public String getPermissionName(PermisEnum permis) {

		switch (permis) {
			case CONSULTA: return "READ";
			case PROCESSAR: return "PROCESSAR";
			case NOTIFICACIO: return "NOTIFICACIO";
			case COMUNICACIO: return "COMUNICACIO";
			case GESTIO: return "ADMINISTRATION";
			case COMUNICACIO_SIR: return "COMUNIACIO_SIR";
			case ADMIN: return "ADMINISTRADOR";
			default: return null;
		}
	}
	
	public GrupEntity comprovarGrup(Long grupId) {

		var grup = grupRepository.findById(grupId).orElse(null);
		if (grup == null) {
			throw new NotFoundException(grupId, GrupEntity.class);
		}
		return grup;
	}
	
	public List<GrupEntity> comprovarGrups(List<GrupDto> grups) {
		
		List<GrupEntity> grupsEntity = new ArrayList<>();

		Optional<GrupEntity> opt;
		for (GrupDto grupdto : grups) {
			opt = grupRepository.findById(grupdto.getId());

			if (opt.isEmpty()) {
				throw new NotFoundException(grupdto.getId(), GrupEntity.class);
			}
			grupsEntity.add(opt.get());
		}
		return grupsEntity;
	}

	public List<EntitatDto> findPermisEntitat(Permission[] permisos) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		var entitatsEntity = entitatRepository.findAll();
		permisosHelper.filterGrantedAny(entitatsEntity, (ObjectIdentifierExtractor<EntitatEntity>) AbstractPersistable::getId, EntitatEntity.class, permisos, auth);
		return conversioTipusHelper.convertirList(entitatsEntity, EntitatDto.class);
	}

}
