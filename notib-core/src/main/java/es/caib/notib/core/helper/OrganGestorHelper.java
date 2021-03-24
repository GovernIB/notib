package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class OrganGestorHelper {
	
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private OrganigramaHelper organigramaHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private OrganGestorCachable organGestorCachable;
	@Autowired
	private PluginHelper pluginHelper;

	@Cacheable(value = "organsEntitiesPermis", key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
	public List<OrganGestorEntity> getOrgansGestorsWithPermis(
			String usuariCodi,
			Authentication auth,
			EntitatEntity entitat,
			Permission[] permisos) {

		// 1. Obtenim els òrgans gestors amb permisos
		List<OrganGestorEntity> organsDisponibles = findOrganismesEntitatAmbPermis(entitat,
				permisos);

		if (organsDisponibles != null && !organsDisponibles.isEmpty()) {
			Set<OrganGestorEntity> organsGestorsAmbPermis = new HashSet<>(organsDisponibles);

			// 2. Obtenim els òrgans gestors fills dels organs gestors amb permisos
			for (OrganGestorEntity organGestorEntity : organsDisponibles) {
				List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitat.getDir3Codi(),
						organGestorEntity.getCodi());
				if (organsFills != null)
					for(String organCodi: organsFills) {
						organsGestorsAmbPermis.add(organGestorRepository.findByCodi(organCodi));
					}

			}

			organsDisponibles = new ArrayList<>(organsGestorsAmbPermis);
		}

		return organsDisponibles;
	}

	public List<OrganGestorEntity> findOrganismesEntitatAmbPermis(EntitatEntity entitat, Permission[] permisos) {
		List<Long> objectsIds = permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				permisos);
		if (objectsIds.isEmpty()) {
			return new ArrayList<OrganGestorEntity>();
		}
		return organGestorRepository.findByEntitatAndIds(entitat, objectsIds);
	}

	public OrganGestorEntity createOrganGestorFromNotificacio(
			NotificacioDtoV2 notificacio,
			EntitatEntity entitat
	) {
		OrganGestorEntity organGestor = organGestorRepository.findByCodi(notificacio.getOrganGestor());
		if (organGestor == null) {
			Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
			if (!organigramaEntitat.containsKey(notificacio.getOrganGestor())) {
				throw new NotFoundException(
						notificacio.getOrganGestor(),
						OrganGestorEntity.class,
						"L'òrgan gestor especificat no es correspon a cap Òrgan Gestor de l'entitat especificada");
			}
			LlibreDto llibreOrgan = pluginHelper.llistarLlibreOrganisme(
					entitat.getCodi(),
					notificacio.getOrganGestor());
			Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
					arbreUnitats,
					notificacio.getOrganGestor());
//					### Crear òrgan gestor si no existeix, si existeix no fer res
			organGestor = OrganGestorEntity.getBuilder(
					notificacio.getOrganGestor(),
					organigramaEntitat.get(notificacio.getOrganGestor()).getNom(),
					entitat,
					llibreOrgan.getCodi(),
					llibreOrgan.getNomLlarg(),
					(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getCodi() : null),
					(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getNom() : null)).build();
			organGestorRepository.save(organGestor);
		}

		return organGestor;
	}

	private static final Logger logger = LoggerFactory.getLogger(OrganGestorHelper.class);

}
