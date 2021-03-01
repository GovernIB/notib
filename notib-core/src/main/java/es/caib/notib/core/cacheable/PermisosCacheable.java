package es.caib.notib.core.cacheable;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.security.ExtendedPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

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
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;

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

        if (entityComprovarHelper.getGenerarLogsPermisosOrgan()) {
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
        log.debug("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ")");

        if (rolActual != null && rolActual.equals("NOT_ADMIN_ORGAN")) {
            Permission[] permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};
            List<Long> organGestorsIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class,
                    permisos);
            // Consulta totes les entitats dels organs gestors amb permisos
            List<EntitatEntity> entitats = entitatRepository.findByOrganGestorsIds(organGestorsIds);
            List<EntitatDto> resposta = conversioTipusHelper.convertirList(
                    entitats,
                    EntitatDto.class);

            for(EntitatDto dto : resposta) {
                dto.setUsuariActualAdministradorOrgan(true);
                dto.setUsuariActualAdministradorEntitat(true);
            }

            return resposta;
        } else {
            Permission[] permisos = new Permission[] {ExtendedPermission.ADMINISTRADORENTITAT};
            if (rolActual != null && rolActual.equals("tothom")) {
                permisos = new Permission[] {ExtendedPermission.USUARI};
            }

            List<Long> entitatsIds = permisosHelper.getObjectsIdsWithPermission(EntitatEntity.class,
                    permisos);
            List<EntitatDto> resposta;
            if (!entitatsIds.isEmpty()){
                resposta = conversioTipusHelper.convertirList(
                        entitatRepository.findByIdsAndActiva(entitatsIds, true),
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

    @CacheEvict(value = "entitatsUsuari", allEntries = true)
    public void evictFindEntitatsAccessiblesUsuari() {
    }

    @CacheEvict(value = "getPermisosEntitatsUsuariActual", key="#auth.name")
    public void evictGetPermisosEntitatsUsuariActual(Authentication auth) {
    }
}
