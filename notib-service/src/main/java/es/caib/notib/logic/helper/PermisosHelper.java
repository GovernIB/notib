/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.persist.acl.NotibMutableAclService;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.acl.AclClassEntity;
import es.caib.notib.persist.entity.acl.AclEntryEntity;
import es.caib.notib.persist.entity.acl.AclObjectIdentityEntity;
import es.caib.notib.persist.entity.acl.AclSidEntity;
import es.caib.notib.persist.repository.acl.AclClassRepository;
import es.caib.notib.persist.repository.acl.AclEntryRepository;
import es.caib.notib.persist.repository.acl.AclObjectIdentityRepository;
import es.caib.notib.persist.repository.acl.AclSidRepository;
import es.caib.notib.logic.intf.acl.ExtendedPermission;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.usuari.DadesUsuari;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;


/**
 * Helper per a la gestió de permisos dins les ACLs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosHelper {

	@Resource
	private LookupStrategy lookupStrategy;
	@Resource
	private NotibMutableAclService aclService;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Resource
	private AclSidRepository aclSidRepository;
	@Resource
	private AclEntryRepository aclEntryRepository;
	@Resource
	private AclClassRepository aclClassRepository;
	@Resource
	private AclObjectIdentityRepository aclObjectIdentityRepository;
	@Autowired
	private AclCache aclCache;
	@Autowired
	private MessageHelper messageHelper;

	public void assignarPermisUsuari(String userName, Long objectIdentifier, Class<?> objectClass, Permission permission) {
		assignarPermisos(new PrincipalSid(userName), objectClass, objectIdentifier, new Permission[] {permission}, false);
	}

	public void assignarPermisRol(String roleName, Long objectIdentifier, Class<?> objectClass, Permission permission) {
		assignarPermisos(new GrantedAuthoritySid(getMapeigRol(roleName)), objectClass, objectIdentifier, new Permission[] {permission}, false);
	}

	public void revocarPermisUsuari(String userName, Long objectIdentifier, Class<?> objectClass, Permission permission) {
		revocarPermisos(new PrincipalSid(userName), objectClass, objectIdentifier, new Permission[] {permission});
	}

	public void revocarPermisRol(String roleName, Long objectIdentifier, Class<?> objectClass, Permission permission) {
		revocarPermisos(new GrantedAuthoritySid(getMapeigRol(roleName)), objectClass, objectIdentifier, new Permission[] {permission});
	}

	public void mourePermisUsuari(String sourceUserName, String targetUserName, Long objectIdentifier, Class<?> objectClass, Permission permission) {
		assignarPermisos(new PrincipalSid(targetUserName), objectClass, objectIdentifier, new Permission[] {permission}, false);
		revocarPermisos(new PrincipalSid(sourceUserName), objectClass, objectIdentifier, new Permission[] {permission});
	}

	public void mourePermisRol(String sourceRoleName, String targetRoleName, Long objectIdentifier, Class<?> objectClass, Permission permission) {
		assignarPermisos(new GrantedAuthoritySid(getMapeigRol(targetRoleName)), objectClass, objectIdentifier, new Permission[] {permission}, false);
		revocarPermisos(new GrantedAuthoritySid(getMapeigRol(sourceRoleName)), objectClass, objectIdentifier, new Permission[] {permission});
	}

	public void filterGrantedAny(Collection<? extends AbstractPersistable<Long>> objects, Class<?> clazz, Permission[] permissions) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ObjectIdentifierExtractor<AbstractPersistable<Long>> o = new ObjectIdentifierExtractor<AbstractPersistable<Long>>() {
			@Override public Long getObjectIdentifier(AbstractPersistable<Long> entitat) {return entitat.getId();
		}};
		filterGrantedAny(objects, o, clazz, permissions, auth);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAny(Collection<?> objects, ObjectIdentifierExtractor objectIdentifierExtractor, Class<?> clazz, Permission[] permissions, Authentication auth) {

		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (!isGrantedAny(objectIdentifier, clazz, permissions, auth)) {
				it.remove();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isGrantedAny(Collection<?> objects, ObjectIdentifierExtractor objectIdentifierExtractor, Class<?> clazz, Permission[] permissions, Authentication auth) {

		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (isGrantedAny(objectIdentifier, clazz, permissions, auth)) {
				return true;
			}
		}
		return false;
	}
	public boolean isGrantedAny(Long objectIdentifier, Class<?> clazz, Permission[] permissions, Authentication auth) {

		boolean[] granted = verificarPermisos(objectIdentifier, clazz, permissions, auth);
		for (int i = 0; i < granted.length; i++) {
			if (granted[i]) {
				return true;
			}
		}
		return false;
	}

	public void filterGrantedAll(Collection<? extends AbstractPersistable<Long>> objects, Class<?> clazz, Permission[] permissions) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ObjectIdentifierExtractor<AbstractPersistable<Long>> o = new ObjectIdentifierExtractor<AbstractPersistable<Long>>() {
			@Override
			public Long getObjectIdentifier(AbstractPersistable<Long> entitat) {
				return entitat.getId();
			}
		};
		filterGrantedAll(objects, o, clazz, permissions, auth);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAll(Collection<?> objects, ObjectIdentifierExtractor objectIdentifierExtractor, Class<?> clazz, Permission[] permissions, Authentication auth) {

		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (!isGrantedAll(objectIdentifier, clazz, permissions, auth)) {
				it.remove();
			}
		}
	}

	public boolean isGrantedAll(Long objectIdentifier, Class<?> clazz, Permission[] permissions, Authentication auth) {

		boolean[] granted = verificarPermisos(objectIdentifier, clazz, permissions, auth);
		boolean result = true;
		for (int i = 0; i < granted.length; i++) {
			if (!granted[i]) {
				result = false;
				break;
			}
		}
		return result;
	}

	private List<AclSidEntity> getSids(Authentication auth) {

		List<AclSidEntity> sids = new ArrayList<AclSidEntity>();
		AclSidEntity userSid = aclSidRepository.getUserSid(auth.getName());
		if (userSid != null) {
			sids.add(userSid);
		}
		List<String> rolesNames = new ArrayList<String>();
		for (GrantedAuthority authority : auth.getAuthorities()) {
			rolesNames.add(authority.getAuthority());
		}
		for (AclSidEntity aclSid: aclSidRepository.findRolesSid(rolesNames)) {
			if (aclSid != null) {
				sids.add(aclSid);
			}
		}
		return sids;
	}

	/**
	 * Consulta si l'usuari autenticat té permís per algún objecte de la classe indicada
	 *
	 * @param clazz Classe dels objectes a consultar
	 * @param permissions Permisos que es vol esbrinar si conté
	 * @return Llista dels identificadors dels objectes seleccionats
	 */
	public boolean isGrantedAny(Class<?> clazz, Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return isGrantedAny(auth, clazz, permissions);
	}

	/**
	 * Consulta si l'objecte auth indicat per paràmetre té permís per algún objecte de la classe indicada
	 *
	 * @param auth Authetication object to check
	 * @param clazz Classe dels objectes a consultar
	 * @param permissions Permisos que es vol esbrinar si conté
	 * @return Llista dels identificadors dels objectes seleccionats
	 */
	public boolean isGrantedAny(Authentication auth, Class<?> clazz, Permission[] permissions) {

		List<AclSidEntity> sids = getSids(auth);
		if (sids.isEmpty()) {
			return false;
		}
		// TODO: no estic segur si hauriem de fer un and binari de totes les mascares en lloc de passar una llista de masks
		List<Integer> masks = new ArrayList<>();
		for (Permission p : permissions){
			masks.add(p.getMask());
		}
		return aclObjectIdentityRepository.hasObjectsWithAnyPermissions(clazz.getName(), sids, masks);
	}

	/**
	 * Obté els identificadors de tots els objectes de la classe especificada sobre
	 * els quals l'usuari actual té permisos
	 *
	 * @param clazz Classe dels objectes a consultar
	 * @param permissions Permisos que es vol esbrinar si conté
	 * @return Llista dels identificadors dels objectes seleccionats
	 */
	public List<Long> getObjectsIdsWithPermission(Class<?> clazz, Permission[] permissions) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<AclSidEntity> sids = getSids(auth);
		if (sids.isEmpty()) {
			return new ArrayList<>();
		}
		// TODO: no estic segur si hauriem de fer un and binari de totes les mascares en lloc de passar una llista de masks
		List<Integer> masks = new ArrayList<>();
		for (Permission p : permissions){
			masks.add(p.getMask());
		}
		return aclObjectIdentityRepository.findObjectsIdWithAnyPermissions(clazz.getName(), sids, masks);
	}

//	/**
//	 * Obté els objectes de la classe especificada sobre
//	 * els quals l'usuari actual té permisos
//	 *
//	 * @param clazz Classe dels objectes a consultar
//	 * @param permission Permís que es vol esbrinar si conté
//	 * @return Llista dels identificadors dels objectes seleccionats
//	 */
//	public <T> List<T> getObjectsWithPermission(AclObjectIdentityInstanceRepository repository, Class<?> clazz, Permission permission) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		List<AclSidEntity> sids = new ArrayList<AclSidEntity>();
//		AclSidEntity userSid = aclSidRepository.getUserSid(auth.getName());
//		if (userSid != null) {
//			sids.add(userSid);
//		}
//		List<String> rolesNames = new ArrayList<String>();
//		for (GrantedAuthority authority : auth.getAuthorities()) {
//			rolesNames.add(authority.getAuthority());
//		}
//		for (AclSidEntity aclSid: aclSidRepository.findRolesSid(rolesNames)) {
//			if (aclSid != null) {
//				sids.add(aclSid);
//			}
//		}
//		return repository.findObjectsWithPermissions(clazz.getName(), sids, permission.getMask());
//	}

	public List<PermisDto> findPermisos(Long objectIdentifier, Class<?> objectClass) {

		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			return findPermisosPerAcl(acl);
		} catch (NotFoundException nfex) {
			return new ArrayList<>();
		}
	}
	
	public PermisDto findPermis(Long objectIdentifier, Class<?> objectClass, Long permisId) {

		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			return findPermisAclById(acl, permisId);
		} catch (NotFoundException nfex) {
			return new PermisDto();
		}
	}
	
	public boolean hasAnyPermis(Long objectIdentifier, Class<?> objectClass) {

		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			return (acl.getEntries() != null && !acl.getEntries().isEmpty());
		} catch (NotFoundException nfex) {
			return false;
		}
	}

	public boolean hasPermission(Long objectIdentifier, Class<?> objectClass, Permission[] permissions) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities()) {
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		}
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		try {
			Acl acl = aclService.readAclById(oid);
			List<Permission> ps = Arrays.asList(permissions);
			return acl.isGranted(ps, sids, false);
		} catch (NotFoundException nfex) {
			return false;
		}
	}
	
	public Map<Long, List<PermisDto>> findPermisos(List<Long> objectIdentifiers, Class<?> objectClass) {

		try {
			Map<Long, List<PermisDto>> resposta = new HashMap<Long, List<PermisDto>>();
			List<ObjectIdentity> oids = new ArrayList<ObjectIdentity>();
			for (Long objectIdentifier: objectIdentifiers) {
				ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
				oids.add(oid);
			}
			if (!oids.isEmpty()) {
				Map<ObjectIdentity, Acl> acls = lookupStrategy.readAclsById(oids, null);
				for (ObjectIdentity oid: acls.keySet()) {
					resposta.put((Long)oid.getIdentifier(), findPermisosPerAcl(acls.get(oid)));
				}
			}
			return resposta;
		} catch (NotFoundException nfex) {
			return new HashMap<>();
		}
	}
	public void updatePermis(Long objectIdentifier, Class<?> objectClass, PermisDto permis) {

		if (TipusEnumDto.USUARI.equals(permis.getTipus())) {
			assignarPermisos(new PrincipalSid(permis.getPrincipal()), objectClass, objectIdentifier, getPermissionsFromPermis(permis),true);
		} else if (TipusEnumDto.ROL.equals(permis.getTipus())) {
			assignarPermisos(new GrantedAuthoritySid(getMapeigRol(permis.getPrincipal())), objectClass, objectIdentifier, getPermissionsFromPermis(permis),true);
		}
	}
	public void deletePermis(Long objectIdentifier, Class<?> objectClass, Long permisId) {

		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			Sid sid = null;
			for (AccessControlEntry ace: acl.getEntries()) {
				if (permisId.equals(ace.getId())) {
					sid = ace.getSid();
					assignarPermisos(ace.getSid(), objectClass, objectIdentifier, new Permission[] {}, true);
					break;
				}
			}
			// asseguram que s'eliminin de BBDD!!
			if (sid != null) {
				aclService.deleteEntries(oid, sid);
			}
		} catch (NotFoundException nfex) {
		}
	}
	public void deleteAcl(Long objectIdentifier, Class<?> objectClass) {

		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			aclService.deleteAcl(oid, true);
		} catch (NotFoundException nfex) {
		}
	}



	private List<PermisDto> findPermisosPerAcl(Acl acl) {

		if (acl == null) {
			return new ArrayList<>();
		}
		List<PermisDto> resposta = new ArrayList<PermisDto>();
		Map<String, PermisDto> permisosUsuari = new HashMap<String, PermisDto>();
		Map<String, PermisDto> permisosRol = new HashMap<String, PermisDto>();
		for (var ace: acl.getEntries()) {
			PermisDto permis = null;
			if (ace.getSid() instanceof PrincipalSid) {
				var principal = ((PrincipalSid)ace.getSid()).getPrincipal();
				permis = permisosUsuari.get(principal);
				if (permis == null) {
					permis = new PermisDto();
					permis.setId((Long)ace.getId());
					permis.setPrincipal(principal);
					var usuari = cacheHelper.findUsuariAmbCodi(principal);
					if(usuari != null) {
						permis.setNomSencerAmbCodi(usuari.getNomSencerAmbCodi()!=null?usuari.getNomSencerAmbCodi():principal);
					}else {
						permis.setNomSencerAmbCodi(principal);
					}

					permis.setTipus(TipusEnumDto.USUARI);
					permisosUsuari.put(principal, permis);
				}
			} else if (ace.getSid() instanceof GrantedAuthoritySid) {
				var grantedAuthority = ((GrantedAuthoritySid)ace.getSid()).getGrantedAuthority();
				permis = permisosRol.get(grantedAuthority);
				if (permis == null) {
					permis = new PermisDto();
					permis.setId((Long)ace.getId());
					permis.setPrincipal(grantedAuthority);
					permis.setNomSencerAmbCodi(grantedAuthority);
					permis.setTipus(TipusEnumDto.ROL);
					permisosRol.put(grantedAuthority, permis);
				}
			}
			if (permis == null) {
				continue;
			}
			if (ExtendedPermission.READ.equals(ace.getPermission())) {
				permis.setRead(true);
			}
			if (ExtendedPermission.WRITE.equals(ace.getPermission())) {
				permis.setWrite(true);
			}
			if (ExtendedPermission.CREATE.equals(ace.getPermission())) {
				permis.setCreate(true);
			}
			if (ExtendedPermission.DELETE.equals(ace.getPermission())) {
				permis.setDelete(true);
			}
			if (ExtendedPermission.ADMINISTRATION.equals(ace.getPermission())) {
				permis.setAdministration(true);
			}
			if (ExtendedPermission.USUARI.equals(ace.getPermission())) {
				permis.setUsuari(true);
			}
			if (ExtendedPermission.ADMINISTRADOR.equals(ace.getPermission())) {
				permis.setAdministrador(true);
			}
			if (ExtendedPermission.ADMINISTRADORENTITAT.equals(ace.getPermission())) {
				permis.setAdministradorEntitat(true);
			}
			if (ExtendedPermission.APLICACIO.equals(ace.getPermission())) {
				permis.setAplicacio(true);
			}
			if (ExtendedPermission.PROCESSAR.equals(ace.getPermission())) {
				permis.setProcessar(true);
			}
			if (ExtendedPermission.COMUNS.equals(ace.getPermission())) {
				permis.setComuns(true);
			}
			if (ExtendedPermission.NOTIFICACIO.equals(ace.getPermission())) {
				permis.setNotificacio(true);
			}
			if (ExtendedPermission.COMUNICACIO.equals(ace.getPermission())) {
				permis.setComunicacio(true);
			}
			if (ExtendedPermission.COMUNICACIO_SIR.equals(ace.getPermission())) {
				permis.setComunicacioSir(true);
			}
			if (ExtendedPermission.COMUNICACIO_SENSE_PROCEDIMENT.equals(ace.getPermission())) {
				permis.setComunicacioSenseProcediment(true);
			}
		}
		resposta.addAll(permisosUsuari.values());
		resposta.addAll(permisosRol.values());
		return resposta;
	}
	
	private PermisDto findPermisAclById(Acl acl, Long permisId) {

		if (acl == null || permisId == null) {
			return null;
		}
		PermisDto permis = null;
		for (AccessControlEntry ace: acl.getEntries()) {
			if (ace.getSid() instanceof PrincipalSid && permisId.equals((Long)ace.getId())) {
				String principal = ((PrincipalSid)ace.getSid()).getPrincipal();
				permis = new PermisDto();
				permis.setId(permisId);
				permis.setPrincipal(principal);
				DadesUsuari usuari = cacheHelper.findUsuariAmbCodi(principal);
				if(usuari != null) {
					permis.setNomSencerAmbCodi(usuari.getNomSencerAmbCodi()!=null?usuari.getNomSencerAmbCodi():principal);
				}else {
					permis.setNomSencerAmbCodi(principal);
				}
					permis.setTipus(TipusEnumDto.USUARI);
			} else if (ace.getSid() instanceof GrantedAuthoritySid && permisId.equals((Long)ace.getId())) {
				String grantedAuthority = ((GrantedAuthoritySid)ace.getSid()).getGrantedAuthority();
				permis = new PermisDto();
				permis.setId((Long)ace.getId());
				permis.setPrincipal(grantedAuthority);
				permis.setNomSencerAmbCodi(grantedAuthority);
				permis.setTipus(TipusEnumDto.ROL);
			}
			if (permis == null) {
				continue;
			}
			if (ExtendedPermission.READ.equals(ace.getPermission())) {
				permis.setRead(true);
			}
			if (ExtendedPermission.WRITE.equals(ace.getPermission())) {
				permis.setWrite(true);
			}
			if (ExtendedPermission.CREATE.equals(ace.getPermission())) {
				permis.setCreate(true);
			}
			if (ExtendedPermission.DELETE.equals(ace.getPermission())) {
				permis.setDelete(true);
			}
			if (ExtendedPermission.ADMINISTRATION.equals(ace.getPermission())) {
				permis.setAdministration(true);
			}
			if (ExtendedPermission.USUARI.equals(ace.getPermission())) {
				permis.setUsuari(true);
			}
			if (ExtendedPermission.ADMINISTRADOR.equals(ace.getPermission())) {
				permis.setAdministrador(true);
			}
			if (ExtendedPermission.ADMINISTRADORENTITAT.equals(ace.getPermission())) {
				permis.setAdministradorEntitat(true);
			}
			if (ExtendedPermission.APLICACIO.equals(ace.getPermission())) {
				permis.setAplicacio(true);
			}
			if (ExtendedPermission.PROCESSAR.equals(ace.getPermission())) {
				permis.setProcessar(true);
			}
			if (ExtendedPermission.COMUNS.equals(ace.getPermission())) {
				permis.setComuns(true);
			}
			if (ExtendedPermission.NOTIFICACIO.equals(ace.getPermission())) {
				permis.setNotificacio(true);
			}
			if (ExtendedPermission.COMUNICACIO_SIR.equals(ace.getPermission())) {
				permis.setComunicacioSir(true);
			}
		}
		return permis;
	}
	
	private void assignarPermisos(Sid sid, Class<?> objectClass, Serializable objectIdentifier, Permission[] permissions, boolean netejarAbans) {

		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl;
		try {
			acl = (MutableAcl)aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			acl = aclService.createAcl(oid);
		}
		if (netejarAbans) {
			// Es recorren girats perque cada vegada que s'esborra un ace
			// es reorganitzen els índexos
			for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
				AccessControlEntry ace = acl.getEntries().get(i);
				if (ace.getSid().equals(sid)) {
					acl.deleteAce(i);
				}
			}
		}
		aclService.updateAcl(acl);
		for (Permission permission: permissions) {
			acl.insertAce(acl.getEntries().size(), permission, sid,true);
		}
		aclService.updateAcl(acl);
	}

	private void revocarPermisos(Sid sid, Class<?> objectClass, Serializable objectIdentifier, Permission[] permissions) throws NotFoundException {

		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		try {
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			List<Integer> indexosPerEsborrar = new ArrayList<Integer>();
			int aceIndex = 0;
			for (AccessControlEntry ace: acl.getEntries()) {
				if (ace.getSid().equals(sid)) {
					for (Permission p: permissions) {
						if (p.equals(ace.getPermission())) {
							indexosPerEsborrar.add(aceIndex);
						}
					}
				}
				aceIndex++;
			}
			for (Integer index: indexosPerEsborrar) {
				acl.deleteAce(index);
			}
			aclService.updateAcl(acl);
		} catch (NotFoundException nfex) {
			// Si no troba l'ACL no fa res
		}
	}

	private boolean[] verificarPermisos(Long objectIdentifier, Class<?> clazz, Permission[] permissions, Authentication auth) {

		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities()) {
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		}
		boolean[] granted = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++) {
			granted[i] = false;
		}
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(clazz, objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			List<Permission> ps = new ArrayList<Permission>();
			for (int i = 0; i < permissions.length; i++) {
				try {
					ps.add(permissions[i]);
					granted[i] = acl.isGranted(ps, sids, false);
					ps.clear();
				} catch (NotFoundException ex) {}
			}
		} catch (NotFoundException ex) {}
		return granted;
	}

	private Permission[] getPermissionsFromPermis(PermisDto permis) {

		List<Permission> permissions = new ArrayList<Permission>();
//		if (permis.isRead())
//			permissions.add(ExtendedPermission.READ);
//		if (permis.isWrite())
//			permissions.add(ExtendedPermission.WRITE);
//		if (permis.isCreate())
//			permissions.add(ExtendedPermission.CREATE);
//		if (permis.isDelete())
//			permissions.add(ExtendedPermission.DELETE);
//		if (permis.isAdministration())
//			permissions.add(ExtendedPermission.ADMINISTRATION);
		if (permis.isUsuari()) {
			permissions.add(ExtendedPermission.USUARI);
		}
		if (permis.isAdministrador()) {
			permissions.add(ExtendedPermission.ADMINISTRADOR);
		}
		if (permis.isAdministradorEntitat()) {
			permissions.add(ExtendedPermission.ADMINISTRADORENTITAT);
		}
		if (permis.isAplicacio()) {
			permissions.add(ExtendedPermission.APLICACIO);
		}
		if (permis.isRead()) {
			permissions.add(ExtendedPermission.READ);
		}
		if (permis.isAdministration()) {
			permissions.add(ExtendedPermission.ADMINISTRATION);
		}
		if (permis.isProcessar()) {
			permissions.add(ExtendedPermission.PROCESSAR);
		}
		if (permis.isComuns()) {
			permissions.add(ExtendedPermission.COMUNS);
		}
		if (permis.isNotificacio()) {
			permissions.add(ExtendedPermission.NOTIFICACIO);
		}
		if (permis.isComunicacioSir()) {
			permissions.add(ExtendedPermission.COMUNICACIO_SIR);
		}
		return permissions.toArray(new Permission[permissions.size()]);
	}

	private String getMapeigRol(String rol) {
		return configHelper.getConfig("es.caib.notib.mapeig.rol." + rol, rol);
	}

	public void revocarPermisosEntity(Long objectIdentifier, Class<?> clazz) {

		List<PermisDto> permisosActuals = findPermisos(objectIdentifier, clazz);
		for (PermisDto permisDto : permisosActuals) {
			permisDto.revocaPermisos();
			updatePermis(objectIdentifier, clazz, permisDto);
		}
		aclCache.clearCache();
	}

	public List<PermisDto> ordenarPermisos(PaginacioParamsDto paginacioParams, List<PermisDto> permisos) {

		if (paginacioParams == null || permisos == null) {
			return permisos;
		}
		final String ordre = paginacioParams.getOrdres() != null && !paginacioParams.getOrdres().isEmpty() && paginacioParams.getOrdres().get(0).getCamp() != null
				? paginacioParams.getOrdres().get(0).getCamp() : null;

		if (ordre == null) {
			return permisos;
		}
		boolean desc = paginacioParams.getOrdres().get(0).getDireccio().equals(PaginacioParamsDto.OrdreDireccioDto.DESCENDENT);
		Comparator<PermisDto> comp = null;
		switch (ordre) {
			case "tipus":
				comp = desc ? PermisDto.decending(PermisDto.sortByTipus()) : PermisDto.sortByTipus();
				break;
			case "nomSencerAmbCodi":
				comp = desc ? PermisDto.decending(PermisDto.sortByNomSencerAmbCodiComparator()) : PermisDto.sortByNomSencerAmbCodiComparator();
				break;
			case "organCodiNom":
				comp = desc ? PermisDto.decending(PermisDto.sortByOrganCodiNomComparator()) : PermisDto.sortByOrganCodiNomComparator();
				break;
			case "read":
				comp = desc ? PermisDto.decending(PermisDto.sortByRead()) : PermisDto.sortByRead();
				break;
			case "processar":
				comp = desc ? PermisDto.decending(PermisDto.sortByProcessar()) : PermisDto.sortByProcessar();
				break;
			case "notificacio":
				comp = desc ? PermisDto.decending(PermisDto.sortByNotificacio()) : PermisDto.sortByNotificacio();
				break;
			case "comuns":
				comp = desc ? PermisDto.decending(PermisDto.sortByComuns()) : PermisDto.sortByComuns();
				break;
			case "administration":
				comp = desc ? PermisDto.decending(PermisDto.sortByAdministration()) : PermisDto.sortByAdministration();
				break;
			case "administrador":
				comp = desc ? PermisDto.decending(PermisDto.sortByAdministrador()) : PermisDto.sortByAdministrador();
				break;
			case "comunicacioSir":
				comp = desc ? PermisDto.decending(PermisDto.sortByComunicacioSir()) : PermisDto.sortByComunicacioSir();
			default:
				break;
		}
		if (comp != null) {
			Collections.sort(permisos, comp);
		}
		return permisos;
	}

	@Transactional
	public void actualitzarPermisosOrgansObsolets(List<NodeDir3> unitatsWs, List<OrganGestorEntity> organsDividits, List<OrganGestorEntity> organsFusionats,
												  List<OrganGestorEntity> organsSubstituits, ProgresActualitzacioDto progres) {

		AclClassEntity classname = aclClassRepository.findByClassname("es.caib.notib.core.entity.OrganGestorEntity");
		List<String> organsFusionatsProcessats = new ArrayList<>();
		int nombreUnitatsTotal = unitatsWs.size();
		int nombreUnitatsProcessades = 0;
		// Actualitzam permisos en l'ordre en que ens arriben del Dir3
		for(NodeDir3 unitat: unitatsWs) {
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.permisos.unitat", new Object[] {unitat.getCodi()}));
			progres.setProgres(63 + (nombreUnitatsProcessades++ * 18)/nombreUnitatsTotal);
			OrganGestorEntity organOrigen = getOrgan(organsDividits, unitat.getCodi());
			if (organOrigen != null) {
				for (OrganGestorEntity organDesti : organOrigen.getNous()) {
					duplicaPermisos(classname, organOrigen, organDesti);
				}
				continue;
			}
			organOrigen = getOrgan(organsFusionats, unitat.getCodi());
			if (organOrigen != null && !organsFusionatsProcessats.contains(organOrigen.getCodi())) {
				OrganGestorEntity organDesti = organOrigen.getNous().get(0);
				List<OrganGestorEntity> organsOrigen = organDesti.getAntics();
				for(OrganGestorEntity origen: organsOrigen)
					organsFusionatsProcessats.add(origen.getCodi());
				duplicaPermisos(classname, organsOrigen, organDesti);
				continue;
			}
			organOrigen = getOrgan(organsSubstituits, unitat.getCodi());
			if (organOrigen != null) {
				OrganGestorEntity organDesti = organOrigen.getNous().get(0);
				duplicaPermisos(classname, organOrigen, organDesti);
				continue;
			}
		}
	}

	private OrganGestorEntity getOrgan(List<OrganGestorEntity> llista, String codi) {

		for (OrganGestorEntity organ: llista) {
			if (organ.getCodi().equals(codi)) {
				return organ;
			}
		}
		return null;
	}

//	private void duplicaPermisos(AclClassEntity classname, OrganGestorEntity organFusio) {
//		Set<AclEntryEntity> permisosNous = new HashSet<>();
//		Set<AclEntryEntity> permisosAntics = new HashSet<>();
//		for (OrganGestorEntity antic: organFusio.getAntics()) {
//			AclObjectIdentityEntity objectIdentity = aclObjectIdentityRepository.findByClassnameAndObjectId(classname, antic.getId());
//			if (objectIdentity != null) {
//				permisosAntics.addAll(aclEntryRepository.findByAclObjectIdentity(objectIdentity));
//			}
//		}
//		if (permisosAntics.isEmpty()) {
//			return;
//		}
//		duplicaEntradesPermisos(classname, organFusio, permisosAntics.iterator().next().getAclObjectIdentity(), permisosAntics, permisosNous);
//		aclEntryRepository.save(permisosNous);
//	}

	private void duplicaPermisos(AclClassEntity classname, OrganGestorEntity organOrigen, OrganGestorEntity organDesti) {
//		Set<AclEntryEntity> permisosNous = new HashSet<>();
//		Set<AclEntryEntity> permisosAntics = new HashSet<>();
//		AclObjectIdentityEntity objectIdentityAntic = aclObjectIdentityRepository.findByClassnameAndObjectId(classname, organOrigen.getId());
//		if (objectIdentityAntic == null) {
//			return;
//		}
//		permisosAntics.addAll(aclEntryRepository.findByAclObjectIdentity(objectIdentityAntic));
//		duplicaEntradesPermisos(classname, organDesti, objectIdentityAntic, permisosAntics, permisosNous);
//		aclEntryRepository.save(permisosNous);
		duplicaPermisos(classname, Arrays.asList(organOrigen), organDesti);
	}

	private void duplicaPermisos(AclClassEntity classname, List<OrganGestorEntity> organsOrigen, OrganGestorEntity organDesti) {

		Set<AclEntryEntity> permisosDesti = new HashSet<>();
		Set<AclEntryEntity> permisosOrigen = new HashSet<>();
		AclSidEntity ownerSid = null;
		for (OrganGestorEntity organOrigen: organsOrigen) {
			AclObjectIdentityEntity objectIdentityAntic = aclObjectIdentityRepository.findByClassnameAndObjectId(classname, organOrigen.getId());
			if (objectIdentityAntic == null) {
				continue;
			}
			if (ownerSid == null) {
				ownerSid = objectIdentityAntic.getOwnerSid();
			}
			permisosOrigen.addAll(aclEntryRepository.findByAclObjectIdentity(objectIdentityAntic));
		}
		duplicaEntradesPermisos(classname, organDesti, ownerSid, permisosOrigen, permisosDesti);
		aclEntryRepository.saveAll(permisosDesti);
	}

	private void duplicaEntradesPermisos(AclClassEntity classname, OrganGestorEntity organNou, AclSidEntity ownerSid, Set<AclEntryEntity> permisosOrigen, Set<AclEntryEntity> permisosDesti) {

		AclObjectIdentityEntity objectIdentityNou = aclObjectIdentityRepository.findByClassnameAndObjectId(classname, organNou.getId());
		if (objectIdentityNou == null) {
			objectIdentityNou = AclObjectIdentityEntity.builder().classname(classname).objectId(organNou.getId()).ownerSid(ownerSid).build();
			aclObjectIdentityRepository.save(objectIdentityNou);
		}
		for (AclEntryEntity permisAntic : permisosOrigen) {
			permisosDesti.add(AclEntryEntity.builder().aclObjectIdentity(objectIdentityNou).sid(permisAntic.getSid()).order(permisosDesti.size())
										.mask(permisAntic.getMask()).granting(permisAntic.getGranting()).build());
		}
	}

	public void eliminarPermisosOrgan(OrganGestorEntity organGestor) {
		AclClassEntity classname = aclClassRepository.findByClassname("es.caib.ripea.core.entity.OrganGestorEntity");
		AclObjectIdentityEntity objectIdentity = aclObjectIdentityRepository.findByClassnameAndObjectId(classname, organGestor.getId());
		List<AclEntryEntity> permisos = aclEntryRepository.findByAclObjectIdentity(objectIdentity);
		aclEntryRepository.deleteInBatch(permisos);
	}

	public interface ObjectIdentifierExtractor<T> {
		public Long getObjectIdentifier(T object);
	}
}