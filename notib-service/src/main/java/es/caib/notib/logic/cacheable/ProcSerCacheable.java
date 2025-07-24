package es.caib.notib.logic.cacheable;

import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.intf.acl.ExtendedPermission;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Utilitat per a accedir a les caches dels procediments. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ProcSerCacheable {

    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private PermisosHelper permisosHelper;
    @Resource
    private CacheManager cacheManager;

    public void clearAuthenticationProcedimentsCaches(Authentication auth) {

        var permisos = new Permission[] {ExtendedPermission.USUARI, ExtendedPermission.APLICACIO, ExtendedPermission.ADMINISTRADORENTITAT, ExtendedPermission.ADMINISTRADORLECTURA};
        var entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class, permisos);
        if (entitatsIds == null || entitatsIds.isEmpty()) {
            return;
        }
        var entitatsAccessibles = entitatRepository.findByIds(entitatsIds);
        if (entitatsAccessibles == null) {
            return;
        }
        var cacheOrgansAmbPermis = cacheManager.getCache("organsAmbPermis");
        var cacheOrgansAmbPermisPerConsulta = cacheManager.getCache("organsAmbPermisPerConsulta");
        var cacheProcsersAmbPermis = cacheManager.getCache("procsersAmbPermis");
        var cacheProcedimentsAmbPermis = cacheManager.getCache("procedimentsAmbPermis");
        var cacheServeisAmbPermis = cacheManager.getCache("serveisAmbPermis");
        var cacheProcserOrgansCodisAmbPermis = cacheManager.getCache("procserOrgansCodisAmbPermis");
        var cacheNotificacioMenu = cacheManager.getCache("procsersPermisNotificacioMenu");
        var cacheComunicacioMenu = cacheManager.getCache("procsersPermisComunicacioMenu");
        var cacheComunicacioSirMenu = cacheManager.getCache("procsersPermisComunicacioSirMenu");
        String cacheKeyPrefix;
        for (var entitatEntity : entitatsAccessibles) {
            cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
            for (var permis: PermisEnum.values()) {
                if (cacheOrgansAmbPermis != null) { cacheOrgansAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));}
                if (cacheOrgansAmbPermisPerConsulta != null) { cacheOrgansAmbPermisPerConsulta.evict(cacheKeyPrefix.concat(permis.name())); }
                if (cacheProcsersAmbPermis != null) { cacheProcsersAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));}
                if (cacheProcedimentsAmbPermis != null) { cacheProcedimentsAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));}
                if (cacheServeisAmbPermis != null) { cacheServeisAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));}
                if (cacheProcserOrgansCodisAmbPermis != null) { cacheProcserOrgansCodisAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));}
            }

            if (cacheNotificacioMenu != null) { cacheNotificacioMenu.evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));}
            if (cacheComunicacioMenu != null) { cacheComunicacioMenu.evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));}
            if (cacheComunicacioSirMenu != null) { cacheComunicacioSirMenu.evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));}
        }
    }
}
