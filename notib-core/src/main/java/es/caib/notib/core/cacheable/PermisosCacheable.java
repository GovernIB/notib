package es.caib.notib.core.cacheable;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.security.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<RolEnumDto, Boolean> hasPermisos = new HashMap<RolEnumDto, Boolean>();

        Boolean hasPermisUsuariEntitat = permisosHelper.isGrantedAny(
                auth,
                EntitatEntity.class,
                new Permission[] {ExtendedPermission.USUARI});

        Boolean hasPermisAdminEntitat = permisosHelper.isGrantedAny(
                auth,
                EntitatEntity.class,
                new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT});

        Boolean hasPermisAplicacioEntitat = permisosHelper.isGrantedAny(
                auth,
                EntitatEntity.class,
                new Permission[] {ExtendedPermission.APLICACIO});

        Boolean hasPermisAdminOrgan = permisosHelper.isGrantedAny(
                auth,
                OrganGestorEntity.class,
                new Permission[] {ExtendedPermission.ADMINISTRADOR});

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

    @Cacheable(value = "entitatsUsuari", key="#usuariCodi.concat('-').concat(#rolActual)")
    public List<EntitatDto> findEntitatsAccessiblesUsuari(
            String usuariCodi,
            String rolActual) {
        log.info("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ", rol=" + rolActual + ")");

        if (rolActual != null && rolActual.equals("NOT_ADMIN_ORGAN")) {
            Permission[] permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};
            List<Long> organGestorsIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class,
                    permisos);
            // Consulta totes les entitats dels organs gestors amb permisos
            List<Long> entitats = entitatRepository.findByOrganGestorsIds(organGestorsIds);

            List<EntitatDto> resposta = conversioTipusHelper.convertirList(
                    entitatRepository.findByIds(entitats),
                    EntitatDto.class);

            for(EntitatDto dto : resposta) {
                dto.setUsuariActualAdministradorOrgan(true);
                dto.setUsuariActualAdministradorEntitat(true);
            }

            return resposta;
        } else {
            Permission[] permisos;
            if (rolActual != null && rolActual.equals("tothom")) {
                permisos = new Permission[] {ExtendedPermission.USUARI};
            } else {
                permisos = new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT};
            }

            List<Long> entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class,
                    permisos);
            List<EntitatDto> resposta;

            List<EntitatEntity> entitatsDisponibles;
            if (!entitatsIds.isEmpty()){
                entitatsDisponibles = entitatRepository.findByIdsAndActiva(entitatsIds, true);
            } else {
                entitatsDisponibles = new ArrayList<>();
            }
            if (!entitatsDisponibles.isEmpty()){
                resposta = conversioTipusHelper.convertirList(
                        entitatsDisponibles,
                        EntitatDto.class);
            } else {
                resposta = new ArrayList<>();
            }

            permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};


            List<Long> organGestorsAmbPermisos = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class,
                    permisos);
            for(EntitatDto dto : resposta) {
                dto.setUsuariActualAdministradorEntitat(true);
                if (!organGestorsAmbPermisos.isEmpty()) {
                    dto.setUsuariActualAdministradorOrgan(
                            organGestorRepository.isAnyOfEntitat(organGestorsAmbPermisos, dto.getId()));
                } else{
                    dto.setUsuariActualAdministradorOrgan(false);
                }

            }

            return resposta;
        }

    }

    @Cacheable(value = "organsGestorsUsuari", key="#auth.name")
    public List<OrganGestorDto> findOrgansGestorsAccessiblesUsuari(Authentication auth) {
        List<OrganGestorEntity> organsGestors = organGestorRepository.findAll();
        Permission[] permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};

        permisosHelper.filterGrantedAny(
                organsGestors,
                new PermisosHelper.ObjectIdentifierExtractor<OrganGestorEntity>() {
                    public Long getObjectIdentifier(OrganGestorEntity organGestor) {
                        return organGestor.getId();
                    }
                },
                OrganGestorEntity.class,
                permisos,
                auth);

        if (getGenerarLogsPermisosOrgan()) {
            log.info("### PERMISOS - Obtenir Òrgans gestors #####################################");
            log.info("### -----------------------------------------------------------------------");
            log.info("### Usuari: " + auth.getName());
            log.info("### Òrgans: ");
            if (organsGestors != null)
                for (OrganGestorEntity organGestor : organsGestors) {
                    log.info("### # " + organGestor.getCodi() + " - " + organGestor.getNom());
                }
            log.info("### -----------------------------------------------------------------------");
        }
        return conversioTipusHelper.convertirList(
                organsGestors,
                OrganGestorDto.class);
    }

    @Resource
    private CacheManager cacheManager;

    public void clearAuthenticationPermissionsCaches(Authentication auth) {
        Permission[] permisos = new Permission[] {ExtendedPermission.USUARI,
                ExtendedPermission.APLICACIO,
                ExtendedPermission.ADMINISTRADORENTITAT};

        List<Long> entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class,
                permisos);
        if (entitatsIds != null && !entitatsIds.isEmpty()) {
            List<EntitatEntity> entitatsAccessibles = entitatRepository.findByIds(entitatsIds);
            if (entitatsAccessibles != null) {
                Cache cacheOrgansAmbPermis = cacheManager.getCache("organsAmbPermis");
                Cache cacheOrgansAmbPermisPerConsulta = cacheManager.getCache("organsAmbPermisPerConsulta");
                for (EntitatEntity entitatEntity : entitatsAccessibles) {
                    String cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
                    for (PermisEnum permis: PermisEnum.values()) {
                        if (cacheOrgansAmbPermis != null)
                            cacheOrgansAmbPermis.evict(cacheKeyPrefix.concat(permis.name()));
                        if (cacheOrgansAmbPermisPerConsulta != null)
                            cacheOrgansAmbPermisPerConsulta.evict(cacheKeyPrefix.concat(permis.name()));
                    }
                }
            }
        }
        Cache cacheOrgansGestorsUsuari = cacheManager.getCache("organsGestorsUsuari");
        Cache cacheEntitatsUsuariActual = cacheManager.getCache("getPermisosEntitatsUsuariActual");
        Cache cacheEntitatsUsuari = cacheManager.getCache("entitatsUsuari");

        if (cacheOrgansGestorsUsuari != null)
            cacheOrgansGestorsUsuari.evict(auth.getName());
        if (cacheEntitatsUsuariActual != null)
            cacheEntitatsUsuariActual.evict(auth.getName());

        for (RolEnumDto rol: RolEnumDto.values()) {
            if (cacheEntitatsUsuari != null)
                cacheEntitatsUsuari.evict(auth.getName().concat("-").concat(rol.name()));
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
        return configHelper.getAsBoolean("es.caib.notib.permisos.organ.logs");
    }
}
