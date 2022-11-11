package es.caib.notib.core.service;

import es.caib.notib.core.api.service.PermisosService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.core.security.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private PermisosHelper permisosHelper;

    @Autowired
    private ProcSerRepository procSerRepository;


    @Override
    @Transactional(readOnly = true)
    public Boolean hasPermisNotificacio(Long entitatId, String usuariCodi) {

        Boolean hasProcedimentsAmbPermis = false;

        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);

        // 1. Comprovam si es té permís directe sobre procediments
        Permission[] permisos = new Permission[] {ExtendedPermission.NOTIFICACIO};
        hasProcedimentsAmbPermis = hasProcedimentsAmbPermisDirecte(entitat, permisos, grups, true);

        // 2. Comprovam si es té permís sobre procediments comuns per òrgan gestor

        // 3. Comprovam si es té permís sobre procediments per òrgan gestor




        return hasProcedimentsAmbPermis;
    }

    @Override
    public Boolean hasPermisComunicacio(Long entitatId, String usuariCodi) {

        Boolean hasProcedimentsAmbPermis = false;

        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);

        // 1. Comprovam si es té permís directe sobre procediments
        Permission[] permisos = new Permission[] {ExtendedPermission.COMUNICACIO};
        hasProcedimentsAmbPermis = hasProcedimentsAmbPermisDirecte(entitat, permisos, grups, true);

        // 2. Comprovam si es té permís per a comunicacions sense procediment

        // 3. Comprovam si es té permís sobre procediments comuns per òrgan gestor

        // 4. Comprovam si es té permís sobre procediments per òrgan gestor


        return hasProcedimentsAmbPermis;
    }

    @Override
    public Boolean hasPermisComunicacioSir(Long entitatId, String usuariCodi) {

        Boolean hasProcedimentsAmbPermis = false;

        List<String> grups = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);

        // 1. Comprovam si es té permís directe sobre procediments
        Permission[] permisos = new Permission[] {ExtendedPermission.COMUNICACIO_SIR};
        hasProcedimentsAmbPermis = hasProcedimentsAmbPermisDirecte(entitat, permisos, grups, true);

        // 2. Comprovam si es té permís per a comunicacions sense procediment

        // 3. Comprovam si es té permís sobre procediments comuns per òrgan gestor

        // 4. Comprovam si es té permís sobre procediments per òrgan gestor


        return hasProcedimentsAmbPermis;
    }

    private List<ProcSerEntity> getProcedimentsAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius) {

        List<ProcSerEntity> procs = new ArrayList<>();
        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcedimentEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
        if (procedimentsAmbPermisIds.isEmpty()){
            return new ArrayList<>();
        }

        // if (procedimentsAmbPermisIds.size() > 1000) ...
        int maxInSize = 1000;
        int numElements = procedimentsAmbPermisIds.size();
        int nParts = (numElements / maxInSize) + 1;
        int inici = 0;
        int fi = numElements - maxInSize > 0 ? maxInSize : numElements;
        List<Long>  subList;
        for (int foo= 0; foo < nParts; foo++) {
            subList = procedimentsAmbPermisIds.subList(inici, fi);
            if (!subList.isEmpty()) {
                if (removeInactius)
                    procs.addAll(procSerRepository.findProcedimentsActiusByEntitatAndGrupAndIds(entitat, grups, subList));
                else
                    procs.addAll(procSerRepository.findProcedimentsByEntitatAndGrupAndIds(entitat, grups, subList));
            }
            inici = fi + 1 ;
            fi = numElements - inici > maxInSize ? maxInSize : numElements;
        }
        return procs;
    }

    private Boolean hasProcedimentsAmbPermisDirecte(EntitatEntity entitat, Permission[] permisos, List<String> grups, boolean removeInactius) {

        Boolean hasProcedimentsAmbPermis = false;
        List<Long> procedimentsAmbPermisIds = permisosHelper.getObjectsIdsWithPermission(ProcedimentEntity.class, permisos);

        // Filtre els procediments amb permisos per procediment de l'entitat i dels grups
        if (procedimentsAmbPermisIds.isEmpty()){
            return false;
        }

        // if (procedimentsAmbPermisIds.size() > 1000) ...
        int maxInSize = 1000;
        int numElements = procedimentsAmbPermisIds.size();
        int nParts = (numElements / maxInSize) + 1;
        int inici = 0;
        int fi = numElements - maxInSize > 0 ? maxInSize : numElements;
        List<Long>  subList;
        for (int foo= 0; foo < nParts && !hasProcedimentsAmbPermis; foo++) {
            subList = procedimentsAmbPermisIds.subList(inici, fi);
            if (!subList.isEmpty()) {
                if (removeInactius)
                    hasProcedimentsAmbPermis = procSerRepository.countProcedimentsActiusByEntitatAndGrupAndIds(entitat, grups, subList) > 0;
                else
                    hasProcedimentsAmbPermis = procSerRepository.countProcedimentsByEntitatAndGrupAndIds(entitat, grups, subList) > 0;
            }
            inici = fi + 1 ;
            fi = numElements - inici > maxInSize ? maxInSize : numElements;
        }
        return hasProcedimentsAmbPermis;
    }


}
