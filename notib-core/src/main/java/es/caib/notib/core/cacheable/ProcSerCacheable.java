package es.caib.notib.core.cacheable;

import com.google.common.collect.Lists;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.OrganGestorHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.core.repository.ProcSerOrganRepository;
import es.caib.notib.core.security.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

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

    /**
     * Obté un llistat de tots els procediments sobre els que l'usuari actual té els permisos indicats.
     * - L'usuari té el permís directament al procediment o
     * - Si el procediment no requereix permís directe:
     *    * L'usuari té el permís sobre l'òrgan gestor del procediment o
     *    * L'usuari té el permís a un òrgan pare del procediment
     *
     * @param usuariCodi El codi de l'usuari de la sessió
     * @param entitat Entitat de la sessió
     * @param permisos Permisos que volem que tinguin els procediments
     *
     * @return Llista dels procediments amb el permís indicat
     */
    @Cacheable(value = "procedimentEntitiesPermis",
            key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
    public List<ProcSerEntity> getProcedimentsWithPermis(String usuariCodi, EntitatEntity entitat, Permission[] permisos) {

        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);

        // 1. Obtenim els procediments amb permisos per procediment
        List<ProcSerEntity> procedimentsAmbPermis = getProcedimentsAmbPermisDirecte(entitat, permisos, grups);

        // 2. Consulta els procediments amb permís per òrgan gestor
        List<ProcSerEntity> procedimentsAmbPermisOrgan = getProcedimentsAmbPermisOrganGestor(entitat, permisos, grups);

        // 2.1
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ProcSerOrganEntity> c = getProcedimentOrganWithPermis(auth, entitat, permisos);
        List<ProcSerEntity> comuns = new ArrayList<>();
        for (ProcSerOrganEntity proc : c) {
            comuns.add(proc.getProcSer());
        }

        // 2.2
        List<ProcSerEntity> comunsGlobals = getProcedimentsComusWithPermisOrganGestor(entitat, permisos, grups);

        // 5. Juntam els procediments amb permís per òrgan gestor amb els procediments amb permís per procediment
        Set<ProcSerEntity> procedimentsList = new HashSet<>(procedimentsAmbPermis);
        procedimentsList.addAll(procedimentsAmbPermisOrgan);
        procedimentsList.addAll(comuns);
        procedimentsList.addAll(comunsGlobals);

        List<ProcSerEntity> procs = Lists.newArrayList(procedimentsList);

        // 6. Ordenam els procediments
        Collections.sort(procs, new Comparator<ProcSerEntity>() {
            @Override
            public int compare(ProcSerEntity p1, ProcSerEntity p2) {
                return (p1.getNom()==null?"":p1.getNom()).compareTo(p2.getNom()==null?"":p2.getNom());
            }
        });

        return procs;
    }

    private List<ProcSerEntity> getProcedimentsAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups) {

        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcedimentEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
        if (procedimentsAmbPermisIds.isEmpty()){
            return new ArrayList<>();
        }

        return  procSerRepository.findProcedimentsByEntitatAndGrupAndIds(entitat, grups, procedimentsAmbPermisIds);
    }
    private List<ProcSerEntity> getProcedimentsAmbPermisOrganGestor(EntitatEntity entitat, Permission[] permisos, List<String> grups) {

        // 2. Obtenim els òrgans gestors amb permisos
        List<OrganGestorEntity> organsGestorsAmbPermis = organGestorHelper.findOrganismesEntitatAmbPermis(entitat, permisos);

        // 3. Obtenim els òrgans gestors fills dels organs gestors amb permisos
        List<String> organsGestorsCodisAmbPermis = new ArrayList<>();
        if (!organsGestorsAmbPermis.isEmpty()) {
            Set<String> codisOrgansAmbDescendents = new HashSet<>();
            for (OrganGestorEntity organGestorEntity : organsGestorsAmbPermis) {
                codisOrgansAmbDescendents.addAll(organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestorEntity.getCodi()));
            }
            organsGestorsCodisAmbPermis = new ArrayList<>(codisOrgansAmbDescendents);
        }

        // Si no te permís a cap organ gestor
        if (organsGestorsCodisAmbPermis.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. Obtenim els procediments amb permisos per òrgan gestor
        return procSerRepository.findProcedimentsAccesiblesPerOrganGestor(organsGestorsCodisAmbPermis, grups);
    }

    /**
     * Consulta un llistat dels permisos de procediments donats a un òrgan gestor concret.
     *
     * @param auth Objecte de l'autentificació de la sessió actual
     * @param entitat Entitat de la sessió
     * @param permisos Permisos que volem que tinguin els procediments
     * @return Retorna una llista de tuples procediment-organ amb tots els permisos a un organ gestor de tots els procediments.
     */
    @Cacheable(value = "procedimentEntitiessOrganPermis", key="#entitat.getId().toString().concat('-').concat(#auth.name).concat('-').concat(#permisos[0].getPattern())")
    public List<ProcSerOrganEntity> getProcedimentOrganWithPermis(Authentication auth, EntitatEntity entitat, Permission[] permisos) {

        // 1. Obtenim els procediments amb permisos per procediment
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(auth.getName());
        List<ProcSerOrganEntity> procedimentOrgans = procSerOrganRepository.findProcedimentsOrganByEntitatAndGrup(entitat, grups);
        List<ProcSerOrganEntity> procedimentOrgansAmbPermis = new ArrayList<ProcSerOrganEntity>(procedimentOrgans);
        permisosHelper.filterGrantedAny(
                procedimentOrgansAmbPermis,
                new PermisosHelper.ObjectIdentifierExtractor<ProcSerOrganEntity>() {
                    public Long getObjectIdentifier(ProcSerOrganEntity procedimentOrgan) {
                        return procedimentOrgan.getId();
                    }
                },
                ProcSerOrganEntity.class,
                permisos,
                auth);
        return procedimentOrgansAmbPermis;
    }

    public List<ProcSerEntity> getProcedimentsComusWithPermisOrganGestor(EntitatEntity entitat, Permission[] permisos, List<String> grups) {

        List<ProcSerEntity> procedimentsComunsSenseAccesDirecte = new ArrayList<>();

        List<Long> organsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, new Permission[]{ ExtendedPermission.COMUNS });

        if (organsAmbPermisIds != null && !organsAmbPermisIds.isEmpty()) {
            procedimentsComunsSenseAccesDirecte = procSerRepository.findComusByEntitatSenseAccesDirecte(entitat, grups);
        }

        return procedimentsComunsSenseAccesDirecte;
    }

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
                for (EntitatEntity entitatEntity : entitatsAccessibles) {
                    String cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
                    cacheManager.getCache("procedimentEntitiesPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.READ.getPattern()));
                    cacheManager.getCache("procedimentEntitiesPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.NOTIFICACIO.getPattern()));
                    cacheManager.getCache("procedimentEntitiesPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.ADMINISTRADOR.getPattern()));

//                    cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
                    cacheManager.getCache("procedimentEntitiessOrganPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.READ.getPattern()));
                    cacheManager.getCache("procedimentEntitiessOrganPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.NOTIFICACIO.getPattern()));
                    cacheManager.getCache("procedimentEntitiessOrganPermis").evict(cacheKeyPrefix.concat(ExtendedPermission.ADMINISTRADOR.getPattern()));

                    // La funció de la caché esta definida emb els serveis dels procediments
//                    cacheKeyPrefix = entitatEntity.getId().toString().concat("-").concat(auth.getName()).concat("-");
//                    cacheManager.getCache("procedimentsPermis").evict(cacheKeyPrefix.concat(PermisEnum.CONSULTA.name()));
//                    cacheManager.getCache("procedimentsPermis").evict(cacheKeyPrefix.concat(PermisEnum.NOTIFICACIO.name()));
//                    cacheManager.getCache("procedimentsPermis").evict(cacheKeyPrefix.concat(PermisEnum.GESTIO.name()));

                    cacheManager.getCache("procsersPermis").evict(cacheKeyPrefix.concat(PermisEnum.CONSULTA.name()));
                    cacheManager.getCache("procsersPermis").evict(cacheKeyPrefix.concat(PermisEnum.NOTIFICACIO.name()));
                    cacheManager.getCache("procsersPermis").evict(cacheKeyPrefix.concat(PermisEnum.COMUNIACIO_SIR.name()));
                    cacheManager.getCache("procsersPermis").evict(cacheKeyPrefix.concat(PermisEnum.GESTIO.name()));
                }
            }
        }
    }
}
