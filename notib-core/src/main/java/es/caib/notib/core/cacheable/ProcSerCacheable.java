package es.caib.notib.core.cacheable;

import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.security.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
        Permission[] permisos = new Permission[] {ExtendedPermission.USUARI,
                ExtendedPermission.APLICACIO,
                ExtendedPermission.ADMINISTRADORENTITAT};

        List<Long> entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class,
                permisos);
        if (entitatsIds != null && !entitatsIds.isEmpty()) {
            List<EntitatEntity> entitatsAccessibles = entitatRepository.findByIds(entitatsIds);
            if (entitatsAccessibles != null) {
                Cache cacheOrgansAmbPermis = cacheManager.getCache("organsAmbPermis");
                Cache cacheProcsersAmbPermis = cacheManager.getCache("procsersAmbPermis");
                Cache cacheProcedimentsAmbPermis = cacheManager.getCache("procedimentsAmbPermis");
                Cache cacheServeisAmbPermis = cacheManager.getCache("serveisAmbPermis");
                Cache cacheProcserOrgansCodisAmbPermis = cacheManager.getCache("procserOrgansCodisAmbPermis");
                Cache cacheNotificacioMenu = cacheManager.getCache("procsersPermisNotificacioMenu");
                Cache cacheComunicacioMenu = cacheManager.getCache("procsersPermisComunicacioMenu");
                Cache cacheComunicacioSirMenu = cacheManager.getCache("procsersPermisComunicacioSirMenu");
                for (EntitatEntity entitatEntity : entitatsAccessibles) {
                    String cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
                    for (PermisEnum permis: PermisEnum.values()) {
                        if (cacheOrgansAmbPermis != null) cacheOrgansAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));
                        if (cacheProcsersAmbPermis != null) cacheProcsersAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));
                        if (cacheProcedimentsAmbPermis != null) cacheProcedimentsAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));
                        if (cacheServeisAmbPermis != null) cacheServeisAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));
                        if (cacheProcserOrgansCodisAmbPermis != null) cacheProcserOrgansCodisAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));
                    }

                    if (cacheNotificacioMenu != null) cacheNotificacioMenu.evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));
                    if (cacheComunicacioMenu != null) cacheComunicacioMenu.evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));
                    if (cacheComunicacioSirMenu != null) cacheComunicacioSirMenu.evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));
                }
            }
        }
    }
}
