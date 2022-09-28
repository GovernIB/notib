package es.caib.notib.logic.cacheable;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.OrganigramaHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.logic.security.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private OrganigramaHelper organigramaHelper;
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

    @Transactional(readOnly = true)
    @Cacheable(value = "organsPermis", key="#entitat.getId().toString().concat('-').concat(#auth.name).concat('-').concat(#permisos[0].getPattern())")
    public List<OrganGestorEntity> findOrgansGestorsWithPermis(EntitatEntity entitat,
                                                               Authentication auth,
                                                               Permission[] permisos) {

        // 1. Obtenim els òrgans gestors amb permisos
        List<OrganGestorEntity> organsDisponibles;
        if (!ExtendedPermission.READ.equals(permisos[0])){
            organsDisponibles = organGestorRepository.findByEntitatAndEstat(entitat, OrganGestorEstatEnum.V);
        } else {
            organsDisponibles = organGestorRepository.findByEntitat(entitat);
        }

        permisosHelper.filterGrantedAll(
                organsDisponibles,
                new PermisosHelper.ObjectIdentifierExtractor<OrganGestorEntity>() {
                    public Long getObjectIdentifier(OrganGestorEntity organGestor) {
                        return organGestor.getId();
                    }
                },
                OrganGestorEntity.class,
                permisos,
                auth);


        List<OrganGestorEntity> resultats = new ArrayList<>(organsDisponibles);
        if (organsDisponibles != null && !organsDisponibles.isEmpty()) {
            Set<OrganGestorEntity> organsGestorsAmbPermis = new HashSet<>(organsDisponibles);

            // 2. Obtenim els òrgans gestors fills dels organs gestors amb permisos
            if (!organsDisponibles.isEmpty()) {
                for (OrganGestorEntity organGestorEntity : organsDisponibles) {
                    List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
                            entitat.getDir3Codi(),
                            organGestorEntity.getCodi());
                    if (organsFills != null)
                        for(String organCodi: organsFills) {
                            OrganGestorEntity organ = organGestorRepository.findByCodi(organCodi);
                            if (ExtendedPermission.READ.equals(permisos[0]) || organ.getEstat() == OrganGestorEstatEnum.V) {
                                organsGestorsAmbPermis.add(organ);
                            }
                        }
                }
            }

            resultats = new ArrayList<>(organsGestorsAmbPermis);
            Collections.sort(resultats, new Comparator<OrganGestorEntity>() {
                @Override
                public int compare(OrganGestorEntity o1, OrganGestorEntity o2) {
                    return o1.getCodi().compareTo(o2.getCodi());
                }
            });
        }

        return resultats;
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
                for (EntitatEntity entitatEntity : entitatsAccessibles) {
                    String cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
                    cacheManager.getCache("organsPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.READ.getPattern()));
                    cacheManager.getCache("organsPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.NOTIFICACIO.getPattern()));
                    cacheManager.getCache("organsPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.ADMINISTRADOR.getPattern()));
                }
            }
        }
        cacheManager.getCache("organsGestorsUsuari").evict(auth.getName());
        cacheManager.getCache("getPermisosEntitatsUsuariActual").evict(auth.getName());

        cacheManager.getCache("entitatsUsuari").evict(auth.getName().concat("-").concat(RolEnumDto.NOT_SUPER.name()));
        cacheManager.getCache("entitatsUsuari").evict(auth.getName().concat("-").concat(RolEnumDto.NOT_ADMIN.name()));
        cacheManager.getCache("entitatsUsuari").evict(auth.getName().concat("-").concat(RolEnumDto.tothom.name()));
        cacheManager.getCache("entitatsUsuari").evict(auth.getName().concat("-").concat(RolEnumDto.NOT_APL.name()));
        cacheManager.getCache("entitatsUsuari").evict(auth.getName().concat("-").concat(RolEnumDto.NOT_ADMIN_ORGAN.name()));

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
