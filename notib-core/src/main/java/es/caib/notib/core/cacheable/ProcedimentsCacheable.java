package es.caib.notib.core.cacheable;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.GrupProcedimentRepository;
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
    private PluginHelper pluginHelper;
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private GrupProcedimentRepository grupProcedimentRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private OrganigramaHelper organigramaHelper;
    @Autowired
    private ProcedimentOrganRepository procedimentOrganRepository;

    @Cacheable(value = "procedimentEntitiesPermis",
            key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
    public List<ProcedimentEntity> getProcedimentsWithPermis(
            String usuariCodi,
            EntitatEntity entitat,
            Permission[] permisos) {

        // 1. Obtenim els procediments amb permisos per procediment
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(
                ProcedimentEntity.class,
                permisos
        );
        // selecciona els expedients amb permisos per procediment de l'entitat i dels grups
        List<ProcedimentEntity> procedimentsAmbPermis =  procedimentRepository.findProcedimentsByEntitatAndGrupAndIds(entitat,
                grups,
                procedimentsAmbPermisIds);

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

        // 4. Obtenim els procediments amb permisos per òrgan gestor
        List<ProcedimentEntity> procedimentsAmbPermisOrgan = new ArrayList<ProcedimentEntity>();
        if (!organsGestorsCodisAmbPermis.isEmpty()) {
            procedimentsAmbPermisOrgan = procedimentRepository.findByOrganGestorCodiInAndGrup(organsGestorsCodisAmbPermis, grups);
        }

        // 5. Juntam els procediments amb permís per òrgan gestor amb els procediments amb permís per procediment
        List<ProcedimentEntity> setProcediments = new ArrayList<ProcedimentEntity>(procedimentsAmbPermis);
        setProcediments.addAll(procedimentsAmbPermisOrgan);
        return setProcediments;
    }

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
