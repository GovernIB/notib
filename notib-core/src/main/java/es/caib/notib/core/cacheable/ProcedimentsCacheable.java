package es.caib.notib.core.cacheable;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.ProcedimentOrganRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class ProcedimentsCacheable {
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private OrganigramaHelper organigramaHelper;
    @Autowired
    private ProcedimentOrganRepository procedimentOrganRepository;

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
    public List<ProcedimentEntity> getProcedimentsWithPermis(
            String usuariCodi,
            EntitatEntity entitat,
            Permission[] permisos) {
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);

        // 1. Obtenim els procediments amb permisos per procediment
        List<ProcedimentEntity> procedimentsAmbPermis = getProcedimentsAmbPermisDirecte(entitat, permisos, grups);

        // 2. Consulta els procediments amb permís per òrgan gestor
        List<ProcedimentEntity> procedimentsAmbPermisOrgan = getProcedimentsAmbPermisOrganGestor(entitat, permisos, grups);

        // 5. Juntam els procediments amb permís per òrgan gestor amb els procediments amb permís per procediment
        List<ProcedimentEntity> setProcediments = new ArrayList<ProcedimentEntity>(procedimentsAmbPermis);
        setProcediments.addAll(procedimentsAmbPermisOrgan);
        return setProcediments;
    }

    private List<ProcedimentEntity> getProcedimentsAmbPermisDirecte(EntitatEntity entitat,
                                                                    Permission[] permisos,
                                                                    List<String> grups) {
        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(
                ProcedimentEntity.class,
                permisos
        );

        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
        if (procedimentsAmbPermisIds.isEmpty()){
            return new ArrayList<>();
        }

        return  procedimentRepository.findProcedimentsByEntitatAndGrupAndIds(entitat,
                grups,
                procedimentsAmbPermisIds);
    }
    private List<ProcedimentEntity> getProcedimentsAmbPermisOrganGestor(EntitatEntity entitat,
                                                                        Permission[] permisos,
                                                                        List<String> grups) {
        // 2. Obtenim els òrgans gestors amb permisos
        List<OrganGestorEntity> organsGestorsAmbPermis = organGestorHelper.findOrganismesEntitatAmbPermis(entitat,
                permisos);

        // 3. Obtenim els òrgans gestors fills dels organs gestors amb permisos
        List<String> organsGestorsCodisAmbPermis = new ArrayList<String>();
        if (!organsGestorsAmbPermis.isEmpty()) {
            Set<String> codisOrgansAmbDescendents = new HashSet<String>();
            for (OrganGestorEntity organGestorEntity : organsGestorsAmbPermis) {
                codisOrgansAmbDescendents.addAll(
                        organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
                                entitat.getDir3Codi(),
                                organGestorEntity.getCodi()));
            }
            organsGestorsCodisAmbPermis = new ArrayList<String>(codisOrgansAmbDescendents);
        }

        // Si no te permís a cap organ gestor
        if (organsGestorsCodisAmbPermis.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. Obtenim els procediments amb permisos per òrgan gestor
        return procedimentRepository.findProcedimentsAccesiblesPerOrganGestor(organsGestorsCodisAmbPermis, grups);
    }

    /**
     * Consulta un llistat dels permisos de procediments donats a un òrgan gestor concret.
     *
     * @param usuariCodi El codi de l'usuari de la sessió
     * @param auth Objecte de l'autentificació de la sessió actual
     * @param entitat Entitat de la sessió
     * @param permisos Permisos que volem que tinguin els procediments
     * @return Retorna una llista de tuples procediment-organ amb tots els permisos a un organ gestor de tots els procediments.
     */
    @Cacheable(value = "procedimentEntitiessOrganPermis", key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
    public List<ProcedimentOrganEntity> getProcedimentOrganWithPermis(
            String usuariCodi,
            Authentication auth,
            EntitatEntity entitat,
            Permission[] permisos) {
        // 1. Obtenim els procediments amb permisos per procediment
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        List<ProcedimentOrganEntity> procedimentOrgans = procedimentOrganRepository.findProcedimentsOrganByEntitatAndGrup(entitat, grups);
        List<ProcedimentOrganEntity> procedimentOrgansAmbPermis = new ArrayList<ProcedimentOrganEntity>(procedimentOrgans);
        permisosHelper.filterGrantedAny(
                procedimentOrgansAmbPermis,
                new PermisosHelper.ObjectIdentifierExtractor<ProcedimentOrganEntity>() {
                    public Long getObjectIdentifier(ProcedimentOrganEntity procedimentOrgan) {
                        return procedimentOrgan.getId();
                    }
                },
                ProcedimentOrganEntity.class,
                permisos,
                auth);
        return procedimentOrgansAmbPermis;
    }
}
