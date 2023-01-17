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
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private ProcSerRepository procSerRepository;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private OrganigramaHelper organigramaHelper;
    @Autowired
    private ProcSerOrganRepository procSerOrganRepository;
    @Resource
    private CacheManager cacheManager;


//    /**
//     * Obté un llistat de tots els procediments sobre els que l'usuari actual té els permisos indicats.
//     * - L'usuari té el permís directament al procediment o
//     * - Si el procediment no requereix permís directe:
//     *    * L'usuari té el permís sobre l'òrgan gestor del procediment o
//     *    * L'usuari té el permís a un òrgan pare del procediment
//     *
//     * @param usuariCodi El codi de l'usuari de la sessió
//     * @param entitat Entitat de la sessió
//     * @param permisos Permisos que volem que tinguin els procediments
//     *
//     * @return Llista dels procediments amb el permís indicat
//     */
//    @Cacheable(value = "procedimentEntitiesPermis",
//            key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
//    public List<ProcSerCacheDto> getProcedimentsWithPermis(String usuariCodi, EntitatEntity entitat, Permission[] permisos) {
//
//        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
//
//        // 1. Obtenim els procediments amb permisos per procediment
//        List<ProcSerCacheDto> procedimentsAmbPermis = getProcedimentsAmbPermisDirecte(entitat, permisos, grups);
//
//        // 2. Consulta els procediments amb permís per òrgan gestor
//        List<ProcSerCacheDto> procedimentsAmbPermisOrgan = getProcedimentsAmbPermisOrganGestor(entitat, permisos, grups);
//
//        // 5. Juntam els procediments amb permís per òrgan gestor amb els procediments amb permís per procediment
//        var procedimentsList = new HashSet<>(procedimentsAmbPermis);
//        procedimentsList.addAll(procedimentsAmbPermisOrgan);
//        List<ProcSerCacheDto> procs = Lists.newArrayList(procedimentsList);
//
//        // 6. Ordenam els procediments
//        Collections.sort(procs, Comparator.comparing(p -> (p.getNom() == null ? "" : p.getNom())));
//
////        removeInactius(permisos, procs);
//
////        for (ProcSerEntity procSer: procs) {
////            Hibernate.initialize(procSer.getOrganGestor().getEntregaCie());
////        }
//        return procs;
//    }

//    @Cacheable(value = "procedimentEntitiesPermisMenu",
//            key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
//    public List<ProcSerCacheDto> getProcedimentsWithPermisMenu(String usuariCodi, EntitatEntity entitat, Permission[] permisos) {
//
//        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
//
//        // 1. Obtenim els procediments amb permisos per procediment
//        List<ProcSerCacheDto> procedimentsAmbPermis = getProcedimentsAmbPermisDirecte(entitat, permisos, grups);
//
//        // 2. Consulta els procediments amb permís per òrgan gestor
//        List<ProcSerCacheDto> procedimentsAmbPermisOrgan = getProcedimentsAmbPermisOrganGestor(entitat, permisos, grups);
//
//        // 2.1
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        var comuns = getProcedimentOrganWithPermis(auth, entitat, permisos).stream().map(p -> p.getProcSer()).collect(Collectors.toList());
//
//        // 2.2
//        List<ProcSerCacheDto> comunsGlobals = getProcedimentsComusWithPermisOrganGestor(entitat, permisos, grups);
//
//        // 5. Juntam els procediments amb permís per òrgan gestor amb els procediments amb permís per procediment
//        var procedimentsList = new HashSet<>(procedimentsAmbPermis);
//        procedimentsList.addAll(procedimentsAmbPermisOrgan);
//        procedimentsList.addAll(comuns);
//        procedimentsList.addAll(comunsGlobals);
//
//        var procs = Lists.newArrayList(procedimentsList);
////        removeInactius(permisos, procs);
//
//        // 6. Ordenam els procediments
//        Collections.sort(procs, Comparator.comparing(p -> (p.getNom() == null ? "" : p.getNom())));
//
//        return procs;
//    }
//
//    /**
//     * Consulta un llistat dels permisos de procediments donats a un òrgan gestor concret.
//     *
//     * @param auth Objecte de l'autentificació de la sessió actual
//     * @param entitat Entitat de la sessió
//     * @param permisos Permisos que volem que tinguin els procediments
//     * @return Retorna una llista de tuples procediment-organ amb tots els permisos a un organ gestor de tots els procediments.
//     */
//    @Cacheable(value = "procedimentEntitiessOrganPermis", key="#entitat.getId().toString().concat('-').concat(#auth.name).concat('-').concat(#permisos[0].getPattern())")
//    public List<ProcSerOrganCacheDto> getProcedimentOrganWithPermis(Authentication auth, EntitatEntity entitat, Permission[] permisos) {
//
//        // 1. Obtenim els procediments amb permisos per procediment
//        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(auth.getName());
//        List<ProcSerOrganEntity> procedimentOrgans = procSerOrganRepository.findProcedimentsOrganByEntitatAndGrup(entitat, grups);
//        if (procedimentOrgans == null)
//            return new ArrayList<>();
//        List<ProcSerOrganEntity> procedimentOrgansAmbPermis = new ArrayList<>(procedimentOrgans);
//        permisosHelper.filterGrantedAny(
//                procedimentOrgansAmbPermis,
//                (PermisosHelper.ObjectIdentifierExtractor<ProcSerOrganEntity>) procedimentOrgan -> procedimentOrgan.getId(),
//                ProcSerOrganEntity.class,
//                permisos,
//                auth);
//        removeProcedimentsInactius(permisos, procedimentOrgansAmbPermis);
////        for (ProcSerOrganEntity procSerOrgan: procedimentOrgansAmbPermis) {
////            Hibernate.initialize(procSerOrgan.getOrganGestor().getEntregaCie());
////        }
//        return procedimentOrgansAmbPermis.stream().map(ProcSerCacheable::toProcSerOrganCacheDto).collect(Collectors.toList());
//    }
//
//    private List<ProcSerCacheDto> getProcedimentsAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups) {
//
//        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcedimentEntity.class, permisos);
//
//        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
//        if (procedimentsAmbPermisIds.isEmpty()){
//            return new ArrayList<>();
//        }
//
//        List<ProcSerEntity> procs = procSerRepository.findProcedimentsByEntitatAndGrupAndIds(entitat, grups, procedimentsAmbPermisIds);
//        if (procs == null) {
//            return new ArrayList<>();
//        }
//        removeInactiusEntities(permisos, procs);
//        return procs.stream().map(ProcSerCacheable::toProcSerCacheDto).collect(Collectors.toList());
//    }
//    private List<ProcSerCacheDto> getProcedimentsAmbPermisOrganGestor(EntitatEntity entitat, Permission[] permisos, List<String> grups) {
//
//        // 2. Obtenim els òrgans gestors amb permisos
//        List<OrganGestorEntity> organsGestorsAmbPermis = organGestorHelper.findOrganismesEntitatAmbPermis(entitat, permisos);
//
//        // 3. Obtenim els òrgans gestors fills dels organs gestors amb permisos
//        List<String> organsGestorsCodisAmbPermis = new ArrayList<>();
//        if (!organsGestorsAmbPermis.isEmpty()) {
//            Set<String> codisOrgansAmbDescendents = new HashSet<>();
//            for (OrganGestorEntity organGestorEntity : organsGestorsAmbPermis) {
//                codisOrgansAmbDescendents.addAll(organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestorEntity.getCodi()));
//            }
//            organsGestorsCodisAmbPermis = new ArrayList<>(codisOrgansAmbDescendents);
//        }
//
//        // Si no te permís a cap organ gestor
//        if (organsGestorsCodisAmbPermis.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        // 4. Obtenim els procediments amb permisos per òrgan gestor
//        List<ProcSerEntity> procs = procSerRepository.findProcedimentsAccesiblesPerOrganGestor(organsGestorsCodisAmbPermis, grups);
//        removeInactiusEntities(permisos, procs);
//
//        return procs.stream().map(ProcSerCacheable::toProcSerCacheDto).collect(Collectors.toList());
//    }
//
//    private List<ProcSerCacheDto> getProcedimentsComusWithPermisOrganGestor(EntitatEntity entitat, Permission[] permisos, List<String> grups) {
//
//        List<ProcSerEntity> procedimentsComunsSenseAccesDirecte = new ArrayList<>();
//
//        List<Long> organsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, new Permission[]{ ExtendedPermission.COMUNS });
//
//        if (organsAmbPermisIds != null && !organsAmbPermisIds.isEmpty()) {
//            procedimentsComunsSenseAccesDirecte = procSerRepository.findComusByEntitatSenseAccesDirecte(entitat, grups);
//        }
//        if (procedimentsComunsSenseAccesDirecte == null)
//            return new ArrayList<ProcSerCacheDto>();
//
//        var procediments = procedimentsComunsSenseAccesDirecte.stream().map(ProcSerCacheable::toProcSerCacheDto).collect(Collectors.toList());
//
//        removeInactius(permisos, procediments);
//        return procediments;
//    }

    private void removeInactiusEntities(Permission[] permisos, List<ProcSerEntity> procs) {
        // Si consultam els permisos per notificar, eliminam els procediments inactius
        List<Permission> permisList = Arrays.asList(permisos);
        if ((permisList.size() == 1 && (permisList.contains(ExtendedPermission.NOTIFICACIO) || permisList.contains(ExtendedPermission.COMUNICACIO_SIR))) ||
                (permisList.size() == 2 && permisList.contains(ExtendedPermission.NOTIFICACIO) && permisList.contains(ExtendedPermission.COMUNICACIO_SIR))) {
            Iterator<ProcSerEntity> it = procs.iterator();
            while (it.hasNext()) {
                ProcSerEntity curr = it.next();
                if (!curr.isActiu()) {
                    it.remove();
                }
            }
        }
    }

    private void removeInactius(Permission[] permisos, List<ProcSerCacheDto> procs) {
        // Si consultam els permisos per notificar, eliminam els procediments inactius
        List<Permission> permisList = Arrays.asList(permisos);
        if ((permisList.size() == 1 && (permisList.contains(ExtendedPermission.NOTIFICACIO) || permisList.contains(ExtendedPermission.COMUNICACIO_SIR))) ||
                (permisList.size() == 2 && permisList.contains(ExtendedPermission.NOTIFICACIO) && permisList.contains(ExtendedPermission.COMUNICACIO_SIR))) {
            Iterator<ProcSerCacheDto> it = procs.iterator();
            while (it.hasNext()) {
                ProcSerCacheDto curr = it.next();
                if (!curr.isActiu()) {
                    it.remove();
                }
            }
        }
    }

    private void removeProcedimentsInactius(Permission[] permisos, List<ProcSerOrganEntity> procOrgs) {
        // Si consultam els permisos per notificar, eliminam els procediments inactius
        List<Permission> permisList = Arrays.asList(permisos);
        if ((permisList.size() == 1 && (permisList.contains(ExtendedPermission.NOTIFICACIO) || permisList.contains(ExtendedPermission.COMUNICACIO_SIR))) ||
                (permisList.size() == 2 && permisList.contains(ExtendedPermission.NOTIFICACIO) && permisList.contains(ExtendedPermission.COMUNICACIO_SIR))) {
            Iterator<ProcSerOrganEntity> it = procOrgs.iterator();
            while (it.hasNext()) {
                ProcSerOrganEntity curr = it.next();
                if (!curr.getProcSer().isActiu()) {
                    it.remove();
                }
            }
        }
    }

    public static ProcSerCacheDto toProcSerCacheDto(ProcSerEntity procSer) {
        if (procSer == null)
            return null;

        return ProcSerCacheDto.builder()
                .id(procSer.getId())
                .codi(procSer.getCodi())
                .nom(procSer.getNom())
                .comu(procSer.isComu())
                .actiu(procSer.isActiu())
                .tipus(procSer.getTipus())
                .organGestor(procSer.getOrganGestor() != null ? OrganGestorCacheDto.builder()
                        .id(procSer.getOrganGestor().getId())
                        .codi(procSer.getOrganGestor().getCodi())
                        .nom(procSer.getOrganGestor().getNom())
                        .estat(procSer.getOrganGestor().getEstat())
                        .build() : null)
                .build();
    }
    public static OrganGestorCacheDto toOrganGestorCacheDto(OrganGestorEntity organ) {
        if (organ == null)
            return null;

        return OrganGestorCacheDto.builder()
                        .id(organ.getId())
                        .codi(organ.getCodi())
                        .nom(organ.getNom())
                        .estat(organ.getEstat())
                        .build();
    }
    public static ProcSerOrganCacheDto toProcSerOrganCacheDto(ProcSerOrganEntity procSerOrgan) {
        if (procSerOrgan == null)
            return null;

        var organ = procSerOrgan.getOrganGestor() != null ? OrganGestorCacheDto.builder()
                .id(procSerOrgan.getOrganGestor().getId())
                .codi(procSerOrgan.getOrganGestor().getCodi())
                .nom(procSerOrgan.getOrganGestor().getNom())
                .estat(procSerOrgan.getOrganGestor().getEstat())
                .build() : null;
        var procser = toProcSerCacheDto(procSerOrgan.getProcSer());

        return ProcSerOrganCacheDto.builder()
                .id(procSerOrgan.getId())
                .organGestor(organ)
                .procSer(procser)
                .build();
    }

    public void clearAuthenticationProcedimentsCaches(Authentication auth) {

        var permisos = new Permission[] {ExtendedPermission.USUARI, ExtendedPermission.APLICACIO, ExtendedPermission.ADMINISTRADORENTITAT};
        var entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class, permisos);
        if (entitatsIds == null || entitatsIds.isEmpty()) {
            return;
        }
        List<EntitatEntity> entitatsAccessibles = entitatRepository.findByIds(entitatsIds);
        if (entitatsAccessibles == null) {
            return;
        }
        for (var entitatEntity : entitatsAccessibles) {
            var cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
            for (var permis: PermisEnum.values()) {
                cacheManager.getCache("organsAmbPermis").evict(cacheKeyPrefix.concat(permis.name()));
                cacheManager.getCache("procsersAmbPermis").evict(cacheKeyPrefix.concat(PermisEnum.CONSULTA.name()));
                cacheManager.getCache("procedimentsAmbPermis").evict(cacheKeyPrefix.concat(PermisEnum.CONSULTA.name()));
                cacheManager.getCache("serveisAmbPermis").evict(cacheKeyPrefix.concat(PermisEnum.CONSULTA.name()));
            }

            cacheManager.getCache("procedimentEntitiessOrganPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.READ.getPattern()));
            cacheManager.getCache("procedimentEntitiessOrganPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.NOTIFICACIO.getPattern()));
            cacheManager.getCache("procedimentEntitiessOrganPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.ADMINISTRADOR.getPattern()));

            cacheManager.getCache("procsersPermis").evict(cacheKeyPrefix.concat(PermisEnum.NOTIFICACIO.name()));
            cacheManager.getCache("procsersPermis").evict(cacheKeyPrefix.concat(PermisEnum.COMUNIACIO_SIR.name()));
            cacheManager.getCache("procsersPermis").evict(cacheKeyPrefix.concat(PermisEnum.GESTIO.name()));

            cacheManager.getCache("procsersPermisNotificacioMenu").evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));
            cacheManager.getCache("procsersPermisComunicacioMenu").evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));
            cacheManager.getCache("procsersPermisComunicacioSirMenu").evict(entitatEntity.getId().toString().concat("-").concat(auth.getName()));
        }
    }
}
