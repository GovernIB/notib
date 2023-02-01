package es.caib.notib.logic.cacheable;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.logic.intf.acl.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Utilitat per a accedir a les caches dels permisos. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class PermisosCacheable {

    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private ConfigHelper configHelper;

    @Cacheable(value = "getPermisosEntitatsUsuariActual", key="#auth.name")
    public Map<RolEnumDto, Boolean> getPermisosEntitatsUsuariActual(Authentication auth) {

        var hasPermisUsuariEntitat = permisosHelper.isGrantedAny(auth, EntitatEntity.class, new Permission[] {ExtendedPermission.USUARI});
        var hasPermisAdminEntitat = permisosHelper.isGrantedAny(auth, EntitatEntity.class, new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT});
        var hasPermisAplicacioEntitat = permisosHelper.isGrantedAny(auth, EntitatEntity.class, new Permission[] {ExtendedPermission.APLICACIO});
        var hasPermisAdminOrgan = permisosHelper.isGrantedAny(auth, OrganGestorEntity.class, new Permission[] {ExtendedPermission.ADMINISTRADOR});

        Map<RolEnumDto, Boolean> hasPermisos = new HashMap<>();
        hasPermisos.put(RolEnumDto.tothom, hasPermisUsuariEntitat);
        hasPermisos.put(RolEnumDto.NOT_ADMIN, hasPermisAdminEntitat);
        hasPermisos.put(RolEnumDto.NOT_APL, hasPermisAplicacioEntitat);
        hasPermisos.put(RolEnumDto.NOT_ADMIN_ORGAN, hasPermisAdminOrgan);

        if (!getGenerarLogsPermisosOrgan()) {
            return hasPermisos;
        }

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
        return hasPermisos;
    }

    @Cacheable(value = "entitatsUsuari", key="#usuariCodi.concat('-').concat(#rolActual)")
    public List<EntitatDto> findEntitatsAccessiblesUsuari(String usuariCodi, String rolActual) {

        log.info("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ", rol=" + rolActual + ")");
        if (rolActual != null && rolActual.equals("NOT_ADMIN_ORGAN")) {
            var permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};
            var organGestorsIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, permisos);
            // Consulta totes les entitats dels organs gestors amb permisos
            var entitats = entitatRepository.findByOrganGestorsIds(organGestorsIds);
            var resposta = conversioTipusHelper.convertirList(entitatRepository.findByIds(entitats), EntitatDto.class);
            for(var dto : resposta) {
                dto.setUsuariActualAdministradorOrgan(true);
                dto.setUsuariActualAdministradorEntitat(true);
            }
            return resposta;
        }

        Permission[] permisos = new Permission[] {rolActual != null && rolActual.equals("tothom") ? ExtendedPermission.USUARI : ExtendedPermission.ADMINISTRADORENTITAT};
        var entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class, permisos);
        var entitatsDisponibles = !entitatsIds.isEmpty() ? entitatRepository.findByIdsAndActiva(entitatsIds, true) : new ArrayList<EntitatEntity>();
        var resposta = !entitatsDisponibles.isEmpty() ? conversioTipusHelper.convertirList(entitatsDisponibles, EntitatDto.class) : new ArrayList<EntitatDto>();

        permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};
        var organGestorsAmbPermisos = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, permisos);
        for(var dto : resposta) {
            dto.setUsuariActualAdministradorEntitat(false);
            if (!organGestorsAmbPermisos.isEmpty()) {
                dto.setUsuariActualAdministradorOrgan(organGestorRepository.isAnyOfEntitat(organGestorsAmbPermisos, dto.getId()));
            }
        }
        return resposta;
    }

    @Cacheable(value = "organsGestorsUsuari", key="#auth.name")
    public List<OrganGestorDto> findOrgansGestorsAccessiblesUsuari(Authentication auth) {

        var organsGestors = organGestorRepository.findAll();
        Permission[] permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};
        permisosHelper.filterGrantedAny(organsGestors,
                new PermisosHelper.ObjectIdentifierExtractor<OrganGestorEntity>() {
                    public Long getObjectIdentifier(OrganGestorEntity organGestor) {
                        return organGestor.getId();
                    }
                }, OrganGestorEntity.class, permisos, auth);

        if (getGenerarLogsPermisosOrgan()) {
            log.info("### PERMISOS - Obtenir Òrgans gestors #####################################");
            log.info("### -----------------------------------------------------------------------");
            log.info("### Usuari: " + auth.getName());
            log.info("### Òrgans: ");
            if (organsGestors != null)
                for (var organGestor : organsGestors) {
                    log.info("### # " + organGestor.getCodi() + " - " + organGestor.getNom());
                }
            log.info("### -----------------------------------------------------------------------");
        }
        return conversioTipusHelper.convertirList(organsGestors, OrganGestorDto.class);
    }

    @Resource
    private CacheManager cacheManager;

    public void clearAuthenticationPermissionsCaches(Authentication auth) {

        var permisos = new Permission[] {ExtendedPermission.USUARI, ExtendedPermission.APLICACIO, ExtendedPermission.ADMINISTRADORENTITAT};
        var entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class, permisos);
        if (entitatsIds != null && !entitatsIds.isEmpty()) {
            var entitatsAccessibles = entitatRepository.findByIds(entitatsIds);
            if (entitatsAccessibles != null) {
                var cacheOrgansAmbPermis = cacheManager.getCache("organsAmbPermis");
                for (var entitatEntity : entitatsAccessibles) {
                    var cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
                    for (var permis: PermisEnum.values()) {
                        if (cacheOrgansAmbPermis != null)
                            cacheOrgansAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));
                    }
                }
            }
        }
        var cacheOrgansGestorsUsuari = cacheManager.getCache("organsGestorsUsuari");
        var cacheEntitatsUsuariActual = cacheManager.getCache("getPermisosEntitatsUsuariActual");
        var cacheEntitatsUsuari = cacheManager.getCache("entitatsUsuari");

        if (cacheOrgansGestorsUsuari != null) {
            cacheOrgansGestorsUsuari.evict(auth.getName());
        }
        if (cacheEntitatsUsuariActual != null) {
            cacheEntitatsUsuariActual.evict(auth.getName());
        }
        for (var rol: RolEnumDto.values()) {
            if (cacheEntitatsUsuari != null) {
                cacheEntitatsUsuari.evict(auth.getName().concat("-").concat(rol.name()));
            }
        }
    }

    @CacheEvict(value = "entitatsUsuari", allEntries = true)
    public void evictAllFindEntitatsAccessiblesUsuari() {
    }
    @CacheEvict(value = "getPermisosEntitatsUsuariActual", allEntries = true)
    public void evictAllPermisosEntitatsUsuariActual() {
    }
    @CacheEvict(value = "organsGestorsUsuari", allEntries = true)
    public void evictAllFindOrgansGestorsAccessiblesUsuari() {
    }

    public boolean getGenerarLogsPermisosOrgan() {
        return configHelper.getConfigAsBoolean("es.caib.notib.permisos.organ.logs");
    }
}
