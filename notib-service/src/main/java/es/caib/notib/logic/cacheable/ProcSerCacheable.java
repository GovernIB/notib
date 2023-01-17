package es.caib.notib.logic.cacheable;

import com.google.common.collect.Lists;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.OrganGestorHelper;
import es.caib.notib.logic.helper.OrganigramaHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.intf.acl.ExtendedPermission;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorCacheDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerCacheDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerOrganCacheDto;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        var permisos = new Permission[] {ExtendedPermission.USUARI, ExtendedPermission.APLICACIO, ExtendedPermission.ADMINISTRADORENTITAT};
        var entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class, permisos);
        if (entitatsIds == null || entitatsIds.isEmpty()) {
            return;
        }
        var entitatsAccessibles = entitatRepository.findByIds(entitatsIds);
        if (entitatsAccessibles == null) {
            return;
        }
        var cacheOrgansAmbPermis = cacheManager.getCache("organsAmbPermis");
        var cacheProcsersAmbPermis = cacheManager.getCache("procsersAmbPermis");
        var cacheProcedimentsAmbPermis = cacheManager.getCache("procedimentsAmbPermis");
        var cacheServeisAmbPermis = cacheManager.getCache("serveisAmbPermis");
        var cacheProcserOrgansCodisAmbPermis = cacheManager.getCache("procserOrgansCodisAmbPermis");
        var cacheNotificacioMenu = cacheManager.getCache("procsersPermisNotificacioMenu");
        var cacheComunicacioMenu = cacheManager.getCache("procsersPermisComunicacioMenu");
        var cacheComunicacioSirMenu = cacheManager.getCache("procsersPermisComunicacioSirMenu");
        for (var entitatEntity : entitatsAccessibles) {
            var cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
            for (var permis: PermisEnum.values()) {
                if (cacheOrgansAmbPermis != null) { cacheOrgansAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));}
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
