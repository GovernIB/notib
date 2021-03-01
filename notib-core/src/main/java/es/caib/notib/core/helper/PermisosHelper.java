/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.entity.acl.AclSidEntity;
import es.caib.notib.core.repository.acl.AclObjectIdentityRepository;
import es.caib.notib.core.repository.acl.AclSidRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.core.security.NotibMutableAclService;
import es.caib.notib.plugin.usuari.DadesUsuari;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
	@Resource
	private AclSidRepository aclSidRepository;
	@Resource
	private AclObjectIdentityRepository aclObjectIdentityRepository;

	public void assignarPermisUsuari(
			String userName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
	}
	public void assignarPermisRol(
			String roleName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
	}

	public void revocarPermisUsuari(
			String userName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		revocarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}
	public void revocarPermisRol(
			String roleName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}

	public void mourePermisUsuari(
			String sourceUserName,
			String targetUserName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(targetUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
		revocarPermisos(
				new PrincipalSid(sourceUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}
	public void mourePermisRol(
			String sourceRoleName,
			String targetRoleName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(targetRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(sourceRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}

	public void filterGrantedAny(
			Collection<? extends AbstractPersistable<Long>> objects,
			Class<?> clazz,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		filterGrantedAny(
				objects,
				new ObjectIdentifierExtractor<AbstractPersistable<Long>>() {
					@Override
					public Long getObjectIdentifier(AbstractPersistable<Long> entitat) {
						return entitat.getId();
					}
				},
				clazz,
				permissions,
				auth);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAny(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAny(
					objectIdentifier,
					clazz,
					permissions,
					auth))
				it.remove();
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isGrantedAny(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(it.next());
			if (isGrantedAny(
					objectIdentifier,
					clazz,
					permissions,
					auth))
				return true;
		}
		return false;
	}
	public boolean isGrantedAny(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				auth);
		for (int i = 0; i < granted.length; i++) {
			if (granted[i])
				return true;
		}
		return false;
	}

	public void filterGrantedAll(
			Collection<? extends AbstractPersistable<Long>> objects,
			Class<?> clazz,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		filterGrantedAll(
				objects,
				new ObjectIdentifierExtractor<AbstractPersistable<Long>>() {
					@Override
					public Long getObjectIdentifier(AbstractPersistable<Long> entitat) {
						return entitat.getId();
					}
				},
				clazz,
				permissions,
				auth);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAll(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAll(
					objectIdentifier,
					clazz,
					permissions,
					auth))
				it.remove();
		}
	}
	public boolean isGrantedAll(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				auth);
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

	public List<PermisDto> findPermisos(
			Long objectIdentifier,
			Class<?> objectClass) {
		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			return new ArrayList<PermisDto>();
		}
		return findPermisosPerAcl(acl);
	}
	
	public PermisDto findPermis(
			Long objectIdentifier,
			Class<?> objectClass,
			Long permisId) {
		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			return new PermisDto();
		}
		return findPermisAclById(acl, permisId);
	}
	
	public boolean hasAnyPermis(
			Long objectIdentifier,
			Class<?> objectClass) {
		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			return false;
		}
		return (acl.getEntries() != null && !acl.getEntries().isEmpty());
		
	}
	public boolean haPermission(
			Long objectIdentifier,
			Class<?> objectClass,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities())
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));

		ObjectIdentity oid = new ObjectIdentityImpl(
				objectClass,
				objectIdentifier);
		Acl acl = aclService.readAclById(oid);
		List<Permission> ps = Arrays.asList(permissions);
		try {
			return acl.isGranted(
					ps,
					sids,
					false);
		} catch (NotFoundException nfex) {
			return false;
		}
	}
	
	public Map<Long, List<PermisDto>> findPermisos(
			List<Long> objectIdentifiers,
			Class<?> objectClass) {
		try {
			Map<Long, List<PermisDto>> resposta = new HashMap<Long, List<PermisDto>>();
			List<ObjectIdentity> oids = new ArrayList<ObjectIdentity>();
			for (Long objectIdentifier: objectIdentifiers) {
				ObjectIdentity oid = new ObjectIdentityImpl(
						objectClass,
						objectIdentifier);
				oids.add(oid);
			}
			if (!oids.isEmpty()) {
				Map<ObjectIdentity, Acl> acls = lookupStrategy.readAclsById(oids, null);
				for (ObjectIdentity oid: acls.keySet()) {
					resposta.put(
							(Long)oid.getIdentifier(),
							findPermisosPerAcl(acls.get(oid)));
				}
			}
			return resposta;
		} catch (NotFoundException nfex) {
			return new HashMap<Long, List<PermisDto>>();
		}
	}
	public void updatePermis(
			Long objectIdentifier,
			Class<?> objectClass,
			PermisDto permis) {
		if (TipusEnumDto.USUARI.equals(permis.getTipus())) {
			assignarPermisos(
					new PrincipalSid(permis.getPrincipal()),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		} else if (TipusEnumDto.ROL.equals(permis.getTipus())) {
			assignarPermisos(
					new GrantedAuthoritySid(getMapeigRol(permis.getPrincipal())),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		}
	}
	public void deletePermis(
			Long objectIdentifier,
			Class<?> objectClass,
			Long permisId) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			Sid sid = null;
			for (AccessControlEntry ace: acl.getEntries()) {
				if (permisId.equals(ace.getId())) {
					sid = ace.getSid(); 
					assignarPermisos(
							ace.getSid(),
							objectClass,
							objectIdentifier,
							new Permission[] {},
							true);
					break;
				}
			}
			// asseguram que s'eliminin de BBDD!!
			if (sid != null)
				aclService.deleteEntries( 
						oid, 
						sid);
		} catch (NotFoundException nfex) {
		}
	}
	public void deleteAcl(
			Long objectIdentifier,
			Class<?> objectClass) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			aclService.deleteAcl(oid, true);
		} catch (NotFoundException nfex) {
		}
	}



	private List<PermisDto> findPermisosPerAcl(Acl acl) {
		List<PermisDto> resposta = new ArrayList<PermisDto>();
		if (acl != null) {
			Map<String, PermisDto> permisosUsuari = new HashMap<String, PermisDto>();
			Map<String, PermisDto> permisosRol = new HashMap<String, PermisDto>();
			for (AccessControlEntry ace: acl.getEntries()) {
				PermisDto permis = null;
				if (ace.getSid() instanceof PrincipalSid) {
					String principal = ((PrincipalSid)ace.getSid()).getPrincipal();
					permis = permisosUsuari.get(principal);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Long)ace.getId());
						permis.setPrincipal(principal);
						DadesUsuari usuari = cacheHelper.findUsuariAmbCodi(principal);
						if(usuari != null) {
							permis.setNomSencerAmbCodi(usuari.getNomSencerAmbCodi()!=null?usuari.getNomSencerAmbCodi():principal);
						}else {
							permis.setNomSencerAmbCodi(principal);
						}
						
						permis.setTipus(TipusEnumDto.USUARI);
						permisosUsuari.put(principal, permis);
					}
				} else if (ace.getSid() instanceof GrantedAuthoritySid) {
					String grantedAuthority = ((GrantedAuthoritySid)ace.getSid()).getGrantedAuthority();
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
				if (permis != null) {
					if (ExtendedPermission.READ.equals(ace.getPermission()))
						permis.setRead(true);
					if (ExtendedPermission.WRITE.equals(ace.getPermission()))
						permis.setWrite(true);
					if (ExtendedPermission.CREATE.equals(ace.getPermission()))
						permis.setCreate(true);
					if (ExtendedPermission.DELETE.equals(ace.getPermission()))
						permis.setDelete(true);
					if (ExtendedPermission.ADMINISTRATION.equals(ace.getPermission()))
						permis.setAdministration(true);
					if (ExtendedPermission.USUARI.equals(ace.getPermission()))
						permis.setUsuari(true);
					if (ExtendedPermission.ADMINISTRADOR.equals(ace.getPermission()))
						permis.setAdministrador(true);
					if (ExtendedPermission.ADMINISTRADORENTITAT.equals(ace.getPermission()))
						permis.setAdministradorEntitat(true);
					if (ExtendedPermission.APLICACIO.equals(ace.getPermission()))
						permis.setAplicacio(true);
					if (ExtendedPermission.PROCESSAR.equals(ace.getPermission()))
						permis.setProcessar(true);
					if (ExtendedPermission.NOTIFICACIO.equals(ace.getPermission()))
						permis.setNotificacio(true);
					if (ExtendedPermission.COMUNS.equals(ace.getPermission()))
						permis.setComuns(true);
				}
			}
			resposta.addAll(permisosUsuari.values());
			resposta.addAll(permisosRol.values());
		}
		return resposta;
	}
	
	private PermisDto findPermisAclById(Acl acl, Long permisId) {
		PermisDto permis = null;
		if (acl != null && permisId != null) {
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
				if (permis != null) {
					if (ExtendedPermission.READ.equals(ace.getPermission()))
						permis.setRead(true);
					if (ExtendedPermission.WRITE.equals(ace.getPermission()))
						permis.setWrite(true);
					if (ExtendedPermission.CREATE.equals(ace.getPermission()))
						permis.setCreate(true);
					if (ExtendedPermission.DELETE.equals(ace.getPermission()))
						permis.setDelete(true);
					if (ExtendedPermission.ADMINISTRATION.equals(ace.getPermission()))
						permis.setAdministration(true);
					if (ExtendedPermission.USUARI.equals(ace.getPermission()))
						permis.setUsuari(true);
					if (ExtendedPermission.ADMINISTRADOR.equals(ace.getPermission()))
						permis.setAdministrador(true);
					if (ExtendedPermission.ADMINISTRADORENTITAT.equals(ace.getPermission()))
						permis.setAdministradorEntitat(true);
					if (ExtendedPermission.APLICACIO.equals(ace.getPermission()))
						permis.setAplicacio(true);
					if (ExtendedPermission.PROCESSAR.equals(ace.getPermission()))
						permis.setProcessar(true);
					if (ExtendedPermission.NOTIFICACIO.equals(ace.getPermission()))
						permis.setNotificacio(true);
					if (ExtendedPermission.COMUNS.equals(ace.getPermission()))
						permis.setComuns(true);
				}
			}
		}
		return permis;
	}
	
	private void assignarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions,
			boolean netejarAbans) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl = null;
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
				if (ace.getSid().equals(sid))
					acl.deleteAce(i);
			}
		}
		aclService.updateAcl(acl);
		for (Permission permission: permissions) {
			acl.insertAce(
					acl.getEntries().size(),
					permission,
					sid,
					true);
		}
		aclService.updateAcl(acl);
	}

	private void revocarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions) throws NotFoundException {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		try {
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			List<Integer> indexosPerEsborrar = new ArrayList<Integer>();
			int aceIndex = 0;
			for (AccessControlEntry ace: acl.getEntries()) {
				if (ace.getSid().equals(sid)) {
					for (Permission p: permissions) {
						if (p.equals(ace.getPermission()))
							indexosPerEsborrar.add(aceIndex);
					}
				}
				aceIndex++;
			}
			for (Integer index: indexosPerEsborrar)
				acl.deleteAce(index);
			aclService.updateAcl(acl);
		} catch (NotFoundException nfex) {
			// Si no troba l'ACL no fa res
		}
	}

	private boolean[] verificarPermisos(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities())
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		boolean[] granted = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++)
			granted[i] = false;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(
					clazz,
					objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			List<Permission> ps = new ArrayList<Permission>();
			for (int i = 0; i < permissions.length; i++) {
				try {
					ps.add(permissions[i]);
					granted[i] = acl.isGranted(
							ps,
							sids,
							false);
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
		if (permis.isUsuari())
			permissions.add(ExtendedPermission.USUARI);
		if (permis.isAdministrador())
			permissions.add(ExtendedPermission.ADMINISTRADOR);
		if (permis.isAdministradorEntitat())
			permissions.add(ExtendedPermission.ADMINISTRADORENTITAT);
		if (permis.isAplicacio())
			permissions.add(ExtendedPermission.APLICACIO);
		if (permis.isRead())
			permissions.add(ExtendedPermission.READ);
		if (permis.isAdministration())
			permissions.add(ExtendedPermission.ADMINISTRATION);
		if (permis.isProcessar())
			permissions.add(ExtendedPermission.PROCESSAR);
		if (permis.isNotificacio())
			permissions.add(ExtendedPermission.NOTIFICACIO);
		if (permis.isComuns())
			permissions.add(ExtendedPermission.COMUNS);
		
		return permissions.toArray(new Permission[permissions.size()]);
	}

	private String getMapeigRol(String rol) {
		String propertyMapeig = 
				(String)PropertiesHelper.getProperties().get(
						"es.caib.notib.mapeig.rol." + rol);
		if (propertyMapeig != null)
			return propertyMapeig;
		else
			return rol;
	}

	public void revocarPermisosEntity(
			Long objectIdentifier,
			Class<?> clazz) {
		List<PermisDto> permisosActuals = findPermisos(objectIdentifier, clazz);
		for (PermisDto permisDto : permisosActuals) {
			permisDto.revocaPermisos();
			updatePermis(objectIdentifier, clazz, permisDto);
		}
	}

	public interface ObjectIdentifierExtractor<T> {
		public Long getObjectIdentifier(T object);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PermisosHelper.class);
	
}
