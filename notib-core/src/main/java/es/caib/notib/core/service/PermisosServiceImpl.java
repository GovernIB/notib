package es.caib.notib.core.service;

import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.service.PermisosService;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.ProcSerOrganEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcSerOrganRepository;
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.core.security.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe que implementa els metodes per consultar i editar les configuracions de l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class PermisosServiceImpl implements PermisosService {

    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private OrganGestorCachable organGestorCachable;
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private OrganigramaHelper organigramaHelper;
    @Autowired
    private ConfigHelper configHelper;

    @Autowired
    private ProcSerRepository procSerRepository;
    @Autowired
    private ProcSerOrganRepository procSerOrganRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;


    public enum ProcSerTipus {
        PROCEDIMENT,
        SERVEI
    }

    // MENUS /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Comprova si un usuari disposa de permisos per notificar sobre algun procediment
    @Override
    @Cacheable(value = "procsersPermisNotificacioMenu", key="#entitatId.toString().concat('-').concat(#usuariCodi)")
    @Transactional(readOnly = true)
    public Boolean hasPermisNotificacio(Long entitatId, String usuariCodi) {

        Permission[] permisos = new Permission[] {ExtendedPermission.NOTIFICACIO};
        return hasPermis(entitatId, usuariCodi, permisos);
    }

    // Comprova si un usuari disposa de permisos per comunicar sobre algun procediment
    @Override
    @Cacheable(value = "procsersPermisComunicacioMenu", key="#entitatId.toString().concat('-').concat(#usuariCodi)")
    @Transactional(readOnly = true)
    public Boolean hasPermisComunicacio(Long entitatId, String usuariCodi) {

        Permission[] permisos = new Permission[] {ExtendedPermission.COMUNICACIO};
        return hasPermis(entitatId, usuariCodi, permisos);
    }

    // Comprova si un usuari disposa de permisos per realitzar comunicacions SIR sobre algun procediment
    @Override
    @Cacheable(value = "procsersPermisComunicacioSirMenu", key="#entitatId.toString().concat('-').concat(#usuariCodi)")
    @Transactional(readOnly = true)
    public Boolean hasPermisComunicacioSir(Long entitatId, String usuariCodi) {

        Permission[] permisos = new Permission[] {ExtendedPermission.COMUNICACIO_SIR};
        return hasPermis(entitatId, usuariCodi, permisos);
    }


    // ÒRGANS ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @Cacheable(value = "organsAmbPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
    @Transactional(readOnly = true)
    public List<CodiValorDto> getOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

        if (permis.isPermisNotCom()) {
            return getOrgansAmbPermisPerNotificar(entitat, grups, permis);
        } else {
            // Per ara només retorna el permís directe sobre òrgan
            return getOrgansAmbPermisDirecte(entitat, grups, permis);
        }
    }

    // Obté òrgans amb permís per notificar per un procediment comú
    @Override
    @Cacheable(value = "organsPermisPerProcedimentComu", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name()).concat('-').concat(#procSetDto.getId().toString())")
    @Transactional(readOnly = true)
    public List<String> getOrgansCodisAmbPermisPerProcedimentComu(Long entitatId, String usuariCodi, PermisEnum permis, ProcSerDto procSetDto) {
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };
        List<OrganGestorEntity> organsAmbPermisComu = getOrgansAmbPermisComu(entitat, grups);
        List<ProcSerOrganEntity> procedimentsOrganAmbPermisDirecte = getProcedimentsOrganAmbPermisDirecte(entitat, permisos, grups, true, null);
        for (ProcSerOrganEntity procedimentOrgan: procedimentsOrganAmbPermisDirecte) {
            if (procedimentOrgan.getProcSer().getId().equals(procSetDto.getId())) {
                organsAmbPermisComu.add(procedimentOrgan.getOrganGestor());
            }
        }

        Set<String> organsDisponibles = new HashSet<>();
        for (OrganGestorEntity organ: organsAmbPermisComu) {
            if (!organsDisponibles.contains(organ.getCodi()))
                organsDisponibles.addAll(organigramaHelper.getCodisOrgansGestorsFillsByOrgan(
                        entitat.getDir3Codi(),
                        organ.getCodi()));
        }

        return new ArrayList<>(organsDisponibles);
    }

    @Override
    @Cacheable(value = "procserOrgansCodisAmbPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
    @Transactional(readOnly = true)
    public List<String> getProcedimentsOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };

        List<ProcSerOrganEntity> procedimentsOrganAmbPermisDirecte = getProcedimentsOrganAmbPermisDirecte(entitat, permisos, grups, true, null);
        List<String> codisProcedimentsOrgans = new ArrayList<>();
        for (ProcSerOrganEntity procSerOrgan : procedimentsOrganAmbPermisDirecte) {
            codisProcedimentsOrgans.add(procSerOrgan.getProcSer().getCodi() + "-" + procSerOrgan.getOrganGestor().getCodi());
        }
        return codisProcedimentsOrgans;
    }


    // PROCEDIMENTS I SERVEIS ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    @Cacheable(value = "procserAmbPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
    @Transactional(readOnly = true)
    public List<CodiValorOrganGestorComuDto> getProcSersAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };
        return getProcSerAmPermis(entitatId, usuariCodi, permisos, null, permis.isPermisNotCom());
    }

    @Override
    @Cacheable(value = "procedimentsAmbPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
    @Transactional(readOnly = true)
    public List<CodiValorOrganGestorComuDto> getProcedimentsAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };
        return getProcSerAmPermis(entitatId, usuariCodi, permisos, ProcSerTipus.PROCEDIMENT, permis.isPermisNotCom());
    }

    @Override
    @Cacheable(value = "serveisAmbPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
    @Transactional(readOnly = true)
    public List<CodiValorOrganGestorComuDto> getServeisAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };
        return getProcSerAmPermis(entitatId, usuariCodi, permisos, ProcSerTipus.PROCEDIMENT, permis.isPermisNotCom());
    }

//    @Override
//    @Cacheable(value = "procserAmbPermisProcediment", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name()).concat('-').concat(#tipus.name())")
//    @Transactional(readOnly = true)
//    public List<CodiValorOrganGestorComuDto> getProcSersAmbPermisProcediment(Long entitatId, String usuariCodi, PermisEnum permis, ProcSerTipus tipus) {
//        Set<ProcSerEntity> procSerAmbPermis = new HashSet<>();
//
//        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };
//        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
//        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
//
//        // 1. Obtenim procediments amb permís directe sobre procediments
//        procSerAmbPermis.addAll(getProcSerAmbPermisDirecte(entitat, permisos, grups, true, tipus));
//
//        return procedimentsToCodiValorOrganGestorComuDto(procSerAmbPermis);
//    }
//
//    @Override
//    @Cacheable(value = "procserAmbPermisOrgan", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name()).concat('-').concat(#tipus.name())")
//    @Transactional(readOnly = true)
//    public List<CodiValorOrganGestorComuDto> getProcSersAmbPermisOrgan(Long entitatId, String usuariCodi, PermisEnum permis, ProcSerTipus tipus) {
//        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };
//        return getProcSerAmPermis(entitatId, usuariCodi, permisos, null, permis.isPermisNotCom());
//    }




    // MÈTODES PRIVATS
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // MENUS /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Boolean hasPermis(Long entitatId, String usuariCodi, Permission[] permisos) {
        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);

        // 1. Comprovam si es té permís directe sobre procediments
        if (hasProcSerAmbPermisDirecte(entitat, permisos, grups, true))
            return true;

        // 2. Comprovam si es té permís directe sobre procediment/òrgan
        if (hasProcedimentsComunsAmbPermisDirecte(entitat, permisos, grups, true))
            return true;

        // 3. Comprovam si es té permís per a comunicacions sense procediment

        if ((ExtendedPermission.COMUNICACIO.equals(permisos[0]) || ExtendedPermission.COMUNICACIO_SIR.equals(permisos[0])) &&
                hasPermisComunicacionsSenseProcediment(entitat, grups))
            return true;

        // 4. Comprovam si es té permís sobre procediments comuns per òrgan gestor
        if (hasProcSerComunsAmbPermisPerOrgan(entitat, grups, true))
            return true;

        // 5. Comprovam si es té permís sobre procediments per òrgan gestor
        return hasProcSerAmbPermisPerOrgan(entitat, permisos, grups, true);
    }


    // PERMIS DIRECTE

    private Boolean hasProcSerAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius) {

        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcedimentEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
        return hasElementsGivenIds(
                procedimentsAmbPermisIds,
                removeInactius,
                new ProcSerActiusPermisDirecteCountCommand(),
                new ProcSerPermisDirecteCountCommand(),
                entitat,
                grups,
                null);
    }
    public class ProcSerPermisDirecteCountCommand implements Command<Long, Long> {
        @Override
        public Long execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerRepository.countProcedimentsByEntitatAndGrupAndIds(entitat, grups, subList);
        }
    }
    public class ProcSerActiusPermisDirecteCountCommand implements Command<Long, Long> {
        @Override
        public Long execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerRepository.countProcedimentsActiusByEntitatAndGrupAndIds(entitat, grups, subList);
        }
    }


    // PERMIS PROCEDIMENT-ORGAN

    private Boolean hasProcedimentsComunsAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius) {

        List<Long> procedimentsComunsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcSerOrganEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
        return hasElementsGivenIds(
                procedimentsComunsAmbPermisIds,
                removeInactius,
                new ProcedimentsActiusPermisComuDirecteCountCommand(),
                new ProcedimentsPermisComuDirecteCountCommand(),
                entitat,
                grups,
                null);
    }
    public class ProcedimentsPermisComuDirecteCountCommand implements Command<Long, Long> {
        @Override
        public Long execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerOrganRepository.countProcedimentsByEntitatAndGrupAndIds(entitat, grups, subList);
        }
    }
    public class ProcedimentsActiusPermisComuDirecteCountCommand implements Command<Long, Long> {
        @Override
        public Long execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerOrganRepository.countProcedimentsActiusByEntitatAndGrupAndIds(entitat, grups, subList);
        }
    }


    // PERMIS COMUNICACIONS SENSE PROCEDIMENTS
    private Boolean hasPermisComunicacionsSenseProcediment(EntitatEntity entitat, List<String> grups) {

        List<Long> organsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcSerOrganEntity.class, new Permission[]{ExtendedPermission.COMUNICACIO_SENSE_PROCEDIMENT});

        return hasElementsGivenIds(
                organsAmbPermisIds,
                new OrgansPermisSenseProcedimentCountCommand(),
                entitat,
                grups);
    }
    public class OrgansPermisSenseProcedimentCountCommand implements Command<Long, Long> {
        @Override
        public Long execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return organGestorRepository.countVigentsByEntitatAndIds(entitat, subList);
        }
    }


    // PERMIS PROCEDIMENTS COMUNS

    private Boolean hasProcSerComunsAmbPermisPerOrgan(EntitatEntity entitat, List<String> grups, boolean removeInactius) {

        Boolean hasProcedimentsAmbPermis = false;
        List<OrganGestorEntity> organsAmbPermis = getOrgansAmbPermisComu(entitat, grups);

        if (organsAmbPermis.isEmpty()){
            return false;
        }

        if (removeInactius)
            hasProcedimentsAmbPermis = procSerRepository.countProcedimentsComusActiusByEntitatSenseAccesDirecte(entitat, grups) > 0;
        else
            hasProcedimentsAmbPermis = procSerRepository.countProcedimentsComusByEntitatSenseAccesDirecte(entitat, grups) > 0;

        return hasProcedimentsAmbPermis;
    }


    // PERMIS PER ÒRGAN

    private Boolean hasProcSerAmbPermisPerOrgan(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius) {

        boolean consulta = ExtendedPermission.READ.equals(permisos[0]);

        // 1. Obtenim els òrgans gestors amb permisos
        List<Long> organsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, permisos);
        List<OrganGestorEntity> organsAmbPermis = getListGivenIds(
                organsAmbPermisIds,
                consulta ? new OrgansPermisCommand() : new OrgansVigentsPermisCommand(),
                entitat,
                grups);
        if (organsAmbPermis == null || organsAmbPermis.isEmpty())
            return false;

        // 2. Obtenim els òrgans gestors fills dels organs gestors amb permisos
        List<String> organsGestorsCodisAmbPermis = new ArrayList<>();
        if (!organsAmbPermis.isEmpty()) {
            Set<String> codisOrgansAmbDescendents = new HashSet<>();
            for (OrganGestorEntity organGestorEntity : organsAmbPermis) {
                codisOrgansAmbDescendents.addAll(organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestorEntity.getCodi()));
            }
            organsGestorsCodisAmbPermis = new ArrayList<>(codisOrgansAmbDescendents);
        }

        // 3. Comprovam els procediments amb permisos per òrgan gestor
        return hasElementsGivenIds(
                organsGestorsCodisAmbPermis,
                removeInactius,
                new ProcSerActiusPermisOrganCountCommand(),
                new ProcSerPermisOrganCountCommand(),
                entitat,
                grups,
                null);
    }
    public class ProcSerPermisOrganCountCommand implements Command<Long, String> {
        @Override
        public Long execute(EntitatEntity entitat, List<String> grups, List<String> subList, ProcSerTipus tipus) {
            return procSerRepository.countProcedimentsAccesiblesPerOrganGestor(entitat, grups, subList);
        }
    }
    public class ProcSerActiusPermisOrganCountCommand implements Command<Long, String> {
        @Override
        public Long execute(EntitatEntity entitat, List<String> grups, List<String> subList, ProcSerTipus tipus) {
            return procSerRepository.countProcedimentsActiusAccesiblesPerOrganGestor(entitat, grups, subList);
        }
    }



    // ORGANS ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<CodiValorDto> getOrgansAmbPermisDirecte(EntitatEntity entitat, List<String> grups, PermisEnum permis) {
        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };

        List<OrganGestorEntity> organs = getOrgansAmbPermis(
                entitat,
                permisos,
                grups);

        // Afegim els òrgans fills
        return getOrgansAfegintFills(entitat, new HashSet<>(organs), permis);
    }

    private List<CodiValorDto> getOrgansAmbPermisPerNotificar(EntitatEntity entitat, List<String> grups, PermisEnum permis) {
        Permission[] permisos = new Permission[] { entityComprovarHelper.getPermissionFromName(permis) };


        // 1. Obté òrgans amb permís comú
        List<OrganGestorEntity> organsAmbPermisComu = new ArrayList<>();
        if (hasProcSerComunsAmbPermisPerOrgan(entitat, grups, true)) {
            organsAmbPermisComu = getOrgansAmbPermisComu(entitat, grups);
        }

        // 2. Obté òrgans amb permis per comunicacions sense procediments --> Només per els permisos de COMUNICACIO i COMUNICACIO_SIR
        List<OrganGestorEntity> organsAmbPermisComunicacionsSenseProcediment = new ArrayList<>();
        if (PermisEnum.COMUNICACIO.equals(permis) || PermisEnum.COMUNICACIO_SIR.equals(permis)) {
            organsAmbPermisComunicacionsSenseProcediment = getOrgansAmbPermisComunicacionsSenseProcediment(entitat, grups);
        }

        // 3. Obté procediemtns d'òrgans amb permis per organ
        List<ProcSerEntity> procSerAmbPermisOrgan = getProcSerAmbPermisPerOrgan(entitat, permisos, grups, true, null);

        // 4. Obté procedimetns d'òrgans amb permís per procediment/organ
        List<ProcSerEntity> procSerAmbPermisProcedimentOrgan = getProcSerComunsAmbPermisPerOrgan(entitat, grups, true, null);

        // 5. Obté procediments d'òrgans amb permis per procediment directe
        List<ProcSerEntity> procSerAmbPermisDirecte = getProcSerAmbPermisDirecte(entitat, permisos, grups, true, null);

        // Agrupam els òrgans
        Set<OrganGestorEntity> organs = new HashSet<>(organsAmbPermisComu);
        organs.addAll(organsAmbPermisComunicacionsSenseProcediment);

        // Agrupam els procediments
        Set<ProcSerEntity> procSers = new HashSet<>(procSerAmbPermisOrgan);
        procSers.addAll(procSerAmbPermisProcedimentOrgan);
        procSers.addAll(procSerAmbPermisDirecte);

        // Afegim els òrgans dels procediments al conjunt d'òrgans
        for(ProcSerEntity procSer: procSers) {
            organs.add(procSer.getOrganGestor());
        }

        // Afegim els òrgans fills
        return getOrgansAfegintFills(entitat, organs, permis);
    }


    // PERMIS DIRECTE

    private List<OrganGestorEntity> getOrgansAmbPermis(EntitatEntity entitat, Permission[] permisos, List<String> grups) {
        boolean consulta = ExtendedPermission.READ.equals(permisos[0]);
        List<Long> organsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, permisos);
        return getListGivenIds(
                organsAmbPermisIds,
                consulta ? new OrgansPermisCommand() : new OrgansVigentsPermisCommand(),
                entitat,
                grups);
    }


    // PERMÍS ORGANS COMUNS

    private List<OrganGestorEntity> getOrgansAmbPermisComu(EntitatEntity entitat, List<String> grups) {

        List<Long> organsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, new Permission[]{ExtendedPermission.COMUNS});

        return getListGivenIds(
                organsAmbPermisIds,
                new OrgansVigentsPermisCommand(),
                entitat,
                grups);
    }


    // PERMÍS COMUNICACIONS SENSE PROCEDIMENT

    private List<OrganGestorEntity> getOrgansAmbPermisComunicacionsSenseProcediment(EntitatEntity entitat, List<String> grups) {

        List<Long> organsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcSerOrganEntity.class, new Permission[]{ExtendedPermission.COMUNICACIO_SENSE_PROCEDIMENT});

        return getListGivenIds(
                organsAmbPermisIds,
                new OrgansVigentsPermisCommand(),
                entitat,
                grups);
    }


    // PERMIS PROCEDIMENT-ORGAN AMB PERMIS DIRECTE
    private List<ProcSerOrganEntity> getProcedimentsOrganAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius, ProcSerTipus tipus) {

        List<Long> procedimentsOrganAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcSerOrganEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment comú/organ de l'entitat i dels grups
        return getListGivenIds(
                procedimentsOrganAmbPermisIds,
                removeInactius,
                new ProcedimentsOrganActiusAmbPermisDirecteCommand(),
                new ProcedimentsOrganAmbPermisDirecteCommand(),
                entitat,
                grups,
                tipus);
    }
    public class ProcedimentsOrganAmbPermisDirecteCommand implements Command<List<ProcSerOrganEntity>, Long> {
        @Override
        public List<ProcSerOrganEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerOrganRepository.findByEntitatAndGrupAndIds(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }
    public class ProcedimentsOrganActiusAmbPermisDirecteCommand implements Command<List<ProcSerOrganEntity>, Long> {
        @Override
        public List<ProcSerOrganEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerOrganRepository.findActiusByEntitatAndGrupAndIds(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }


    // AFEGIR ORGANS FILLS

    private List<CodiValorDto> getOrgansAfegintFills(EntitatEntity entitat, Set<OrganGestorEntity> organs, PermisEnum permis) {
        Set<CodiValorDto> resposta = new HashSet<>();

        boolean entitatPermesa = configHelper.getAsBoolean("es.caib.notib.notifica.dir3.entitat.permes");
        boolean isOficinaOrganSir = !entitat.isOficinaEntitat() && PermisEnum.COMUNICACIO_SIR.equals(permis);

        Set<String> codis = new HashSet<>();
        for (OrganGestorEntity organ : organs) {
            codis.add(organ.getCodi());
        }

        for(OrganGestorEntity organ: organs) {

            boolean excloure = isOficinaOrganSir && Strings.isNullOrEmpty(organ.getOficina());
            if ((entitatPermesa || !organ.getCodi().equals(entitat.getDir3Codi())) && !excloure) {
                resposta.add(CodiValorDto.builder().codi(organ.getCodi()).valor(organ.getCodi() + " - " + organ.getNom()).build());
            }
            //buscar fills
            List<String> codiFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organ.getCodi());
            for (String fill : codiFills) {
                if (codis.contains(fill)) {
                    continue;
                }
                OrganGestorEntity organFill = organGestorRepository.findByCodi(fill);
                if (organFill != null) {
                    boolean excloureFill = isOficinaOrganSir && Strings.isNullOrEmpty(organFill.getOficina());
                    if (!excloureFill) {
                        resposta.add(CodiValorDto.builder().codi(organFill.getCodi()).valor(organFill.getCodi() + " - " + organFill.getNom()).build());
                    }
                }
            }
        }

        List<CodiValorDto> organsAmbPermis = new ArrayList<>(resposta);
        if (!organsAmbPermis.isEmpty()) {
            Collections.sort(organsAmbPermis, new Comparator<CodiValorDto>() {
                @Override
                public int compare(CodiValorDto o1, CodiValorDto o2) {
                    return o1.getValor().compareTo(o2.getValor());
                }
            });
        }

        return organsAmbPermis;
    }


    // PROCEDIMENTS I SERVEIS ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<CodiValorOrganGestorComuDto> getProcSerAmPermis(Long entitatId, String usuariCodi, Permission[] permisos, ProcSerTipus tipus, boolean incloureComuns) {

        Set<ProcSerEntity> procSerAmbPermis = new HashSet<>();

        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);


        // 1. Obtenim procediments amb permís directe sobre procediments
        procSerAmbPermis.addAll(getProcSerAmbPermisDirecte(entitat, permisos, grups, true, tipus));

        // 2. Obtenim procediments amb permís directe sobre procediment/òrgan
        procSerAmbPermis.addAll(getProcedimentsForProcedimentsOrganAmbPermisDirecte(entitat, permisos, grups, true, tipus));

        // 3. Obtenim procediments amb permís sobre procediments comuns per òrgan gestor
        if (incloureComuns)
            procSerAmbPermis.addAll(getProcSerComunsAmbPermisPerOrgan(entitat, grups, true, tipus));

        // 4. Obtenim procediments amb permís sobre procediments per òrgan gestor
        procSerAmbPermis.addAll(getProcSerAmbPermisPerOrgan(entitat, permisos, grups, true, tipus));

        return procedimentsToCodiValorOrganGestorComuDto(procSerAmbPermis);
    }

    // PERMÍS DIRECTE

    private List<ProcSerEntity> getProcSerAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius, ProcSerTipus tipus) {

        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcedimentEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
        return getListGivenIds(
                procedimentsAmbPermisIds,
                removeInactius,
                new ProcSerActiusPermisDirecteCommand(),
                new ProcSerPermisDirecteCommand(),
                entitat,
                grups,
                tipus);
    }
    public class ProcSerPermisDirecteCommand implements Command<List<ProcSerEntity>, Long> {
        @Override
        public List<ProcSerEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerRepository.findProcedimentsByEntitatAndGrupAndIds(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }
    public class ProcSerActiusPermisDirecteCommand implements Command<List<ProcSerEntity>, Long> {
        @Override
        public List<ProcSerEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerRepository.findProcedimentsActiusByEntitatAndGrupAndIds(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }


    // PERMÍS PROCEDIMENT COMU ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ProcSerEntity> getProcSerComunsAmbPermisPerOrgan(EntitatEntity entitat, List<String> grups, boolean removeInactius, ProcSerTipus tipus) {

        List<ProcSerEntity> procedimentsComunsSenseAccesDirecte = new ArrayList<>();
        List<OrganGestorEntity> organsAmbPermis = getOrgansAmbPermisComu(entitat, grups);

        if (organsAmbPermis != null && !organsAmbPermis.isEmpty()) {
            if (removeInactius)
                procedimentsComunsSenseAccesDirecte = procSerRepository.findProcedimentsComusActiusByEntitatSenseAccesDirecte(entitat, grups, tipus == null, tipus != null ? tipus.name() : null);
            else
                procedimentsComunsSenseAccesDirecte = procSerRepository.findProcedimentsComusByEntitatSenseAccesDirecte(entitat, grups, tipus == null, tipus != null ? tipus.name() : null);
        }

        return procedimentsComunsSenseAccesDirecte;
    }

    // PERMÍS PROCEDIMENT-ORGAN //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ProcSerEntity> getProcedimentsForProcedimentsOrganAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius, ProcSerTipus tipus) {

        List<Long> procedimentsOrganAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcSerOrganEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment comú/organ de l'entitat i dels grups
        return getListGivenIds(
                procedimentsOrganAmbPermisIds,
                removeInactius,
                new ProcedimentsActiusForProcedimentsOrganAmbPermisDirecteCommand(),
                new ProcedimentsForProcedimentsOrganAmbPermisDirecteCommand(),
                entitat,
                grups,
                tipus);
    }
    public class ProcedimentsForProcedimentsOrganAmbPermisDirecteCommand implements Command<List<ProcSerEntity>, Long> {
        @Override
        public List<ProcSerEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerOrganRepository.findProcedimentsByEntitatAndGrupAndIds(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }
    public class ProcedimentsActiusForProcedimentsOrganAmbPermisDirecteCommand implements Command<List<ProcSerEntity>, Long> {
        @Override
        public List<ProcSerEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return procSerOrganRepository.findProcedimentsActiusByEntitatAndGrupAndIds(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }

    // PERMÍS PER ORGAN //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ProcSerEntity> getProcSerAmbPermisPerOrgan(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius, ProcSerTipus tipus) {

        // 1. Obtenim els òrgans gestors amb permisos
        List<OrganGestorEntity> organsAmbPermis = getOrgansAmbPermis(
                entitat,
                permisos,
                grups);

        // 2. Obtenim els òrgans gestors fills dels organs gestors amb permisos
        List<String> organsGestorsCodisAmbPermis = new ArrayList<>();
        if (!organsAmbPermis.isEmpty()) {
            Set<String> codisOrgansAmbDescendents = new HashSet<>();
            for (OrganGestorEntity organGestorEntity : organsAmbPermis) {
                codisOrgansAmbDescendents.addAll(organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestorEntity.getCodi()));
            }
            organsGestorsCodisAmbPermis = new ArrayList<>(codisOrgansAmbDescendents);
        }

        // 3. Obtenim els procediments amb permisos per òrgan gestor
        return getListGivenIds(
                organsGestorsCodisAmbPermis,
                removeInactius,
                new ProcedimentsActiusPermisOrganCommand(),
                new ProcedimentsPermisOrganCommand(),
                entitat,
                grups,
                tipus);
    }

    public class OrgansPermisCommand implements Command<List<OrganGestorEntity>, Long> {
        @Override
        public List<OrganGestorEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return organGestorRepository.findByEntitatAndIds(entitat, subList);
        }
    }
    public class OrgansVigentsPermisCommand implements Command<List<OrganGestorEntity>, Long> {
        @Override
        public List<OrganGestorEntity> execute(EntitatEntity entitat, List<String> grups, List<Long> subList, ProcSerTipus tipus) {
            return organGestorRepository.findByEntitatAndIds(entitat, subList);
        }
    }
    public class ProcedimentsPermisOrganCommand implements Command<List<ProcSerEntity>, String> {
        @Override
        public List<ProcSerEntity> execute(EntitatEntity entitat, List<String> grups, List<String> subList, ProcSerTipus tipus) {
            return procSerRepository.findProcedimentsAccesiblesPerOrganGestor(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }
    public class ProcedimentsActiusPermisOrganCommand implements Command<List<ProcSerEntity>, String> {
        @Override
        public List<ProcSerEntity> execute(EntitatEntity entitat, List<String> grups, List<String> subList, ProcSerTipus tipus) {
            return procSerRepository.findProcedimentsActiusAccesiblesPerOrganGestor(entitat, grups, subList, tipus == null, tipus != null ? tipus.name() : null);
        }
    }

    private List<CodiValorOrganGestorComuDto> procedimentsToCodiValorOrganGestorComuDto(Set<ProcSerEntity> procSers) {
        List<CodiValorOrganGestorComuDto> response = new ArrayList<>();
        for (ProcSerEntity procSer : procSers) {
            response.add(CodiValorOrganGestorComuDto.builder()
                    .codi(procSer.getId().toString())
                    .valor(procSer.getCodi() + ((procSer.getNom() != null && !procSer.getNom().isEmpty()) ? " - " + procSer.getNom() : ""))
                    .organGestor(procSer.getOrganGestor() != null ? procSer.getOrganGestor().getCodi() : "")
                    .comu(procSer.isComu())
                    .build());
        }
        if (!response.isEmpty()) {
            Collections.sort(response, new Comparator<CodiValorOrganGestorComuDto>() {
                @Override
                public int compare(CodiValorOrganGestorComuDto p1, CodiValorOrganGestorComuDto p2) {
                    return p1.getCodi().compareTo(p2.getCodi());
                }
            });
        }
        return response;
    }




    // Auxiliar - Obtenir llistat a partir de Ids, tenint en compte que Oracle només permet posar 1000 elements en la clausula IN

    private <U>Boolean hasElementsGivenIds(
            List<U> ids,
            Command<Long, U> commandCount,
            EntitatEntity entitat,
            List<String> grups) {

        Boolean hasElements = false;

        if (ids == null || ids.isEmpty())
            return false;

        int maxInSize = 1000;
        int numElements = ids.size();
        int nParts = (numElements / maxInSize) + 1;
        int inici = 0;
        int fi = numElements - maxInSize > 0 ? maxInSize : numElements;
        List<U>  subList;
        for (int foo= 0; foo < nParts&& !hasElements; foo++) {
            subList = ids.subList(inici, fi);
            if (!subList.isEmpty()) {
                hasElements = commandCount.execute(entitat, grups, subList, null) > 0;
            }
            inici = fi + 1 ;
            fi = numElements - inici > maxInSize ? maxInSize : numElements;
        }
        return hasElements;
    }

    private <U> Boolean hasElementsGivenIds(
            List<U> ids,
            boolean removeInactius,
            Command<Long,U> commandCountRemovingInactius,
            Command<Long, U> commandCountMantainingInactius,
            EntitatEntity entitat,
            List<String> grups,
            ProcSerTipus tipus) {

        Boolean hasElements = false;

        if (ids == null || ids.isEmpty())
            return false;

        int maxInSize = 1000;
        int numElements = ids.size();
        int nParts = (numElements / maxInSize) + 1;
        int inici = 0;
        int fi = numElements - maxInSize > 0 ? maxInSize : numElements;
        List<U>  subList;
        for (int foo= 0; foo < nParts&& !hasElements; foo++) {
            subList = ids.subList(inici, fi);
            if (!subList.isEmpty()) {
                if (removeInactius)
                    hasElements = commandCountRemovingInactius.execute(entitat, grups, subList, tipus) > 0;
                else
                    hasElements = commandCountMantainingInactius.execute(entitat, grups, subList, tipus) > 0;
            }
            inici = fi + 1 ;
            fi = numElements - inici > maxInSize ? maxInSize : numElements;
        }
        return hasElements;
    }

    private <T,U> List<T> getListGivenIds(
            List<U> ids,
            Command<List<T>, U> command,
            EntitatEntity entitat,
            List<String> grups) {
        List<T> llistaResultats = new ArrayList<>();

        if (ids == null || ids.isEmpty())
            return llistaResultats;

        int maxInSize = 1000;
        int numElements = ids.size();
        int nParts = (numElements / maxInSize) + 1;
        int inici = 0;
        int fi = numElements - maxInSize > 0 ? maxInSize : numElements;
        List<U>  subList;
        for (int foo= 0; foo < nParts; foo++) {
            subList = ids.subList(inici, fi);
            if (!subList.isEmpty()) {
                llistaResultats.addAll(command.execute(entitat, grups, subList, null));
            }
            inici = fi + 1 ;
            fi = numElements - inici > maxInSize ? maxInSize : numElements;
        }
        return llistaResultats;
    }
    private <T,U> List<T> getListGivenIds(
            List<U> ids,
            boolean removeInactius,
            Command<List<T>,U> commandRemovingInactius,
            Command<List<T>,U> commandMantainingInactius,
            EntitatEntity entitat,
            List<String> grups,
            ProcSerTipus tipus) {
        List<T> llistaResultats = new ArrayList<>();

        if (ids == null || ids.isEmpty())
            return llistaResultats;

        int maxInSize = 1000;
        int numElements = ids.size();
        int nParts = (numElements / maxInSize) + 1;
        int inici = 0;
        int fi = numElements - maxInSize > 0 ? maxInSize : numElements;
        List<U>  subList;
        for (int foo= 0; foo < nParts; foo++) {
            subList = ids.subList(inici, fi);
            if (!subList.isEmpty()) {
                if (removeInactius)
                    llistaResultats.addAll(commandRemovingInactius.execute(entitat, grups, subList, tipus));
                else
                    llistaResultats.addAll(commandMantainingInactius.execute(entitat, grups, subList, tipus));
            }
            inici = fi + 1 ;
            fi = numElements - inici > maxInSize ? maxInSize : numElements;
        }
        return llistaResultats;
    }

//    public interface Command<T> {
//        public T execute(EntitatEntity entitat, List<String> grups, List<Long> subList);
//    }

    public interface Command<T,U> {
        public T execute(EntitatEntity entitat, List<String> grups, List<U> subList, ProcSerTipus tipus);
    }

}
