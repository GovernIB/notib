package es.caib.notib.core.helper;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.repository.OrganGestorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(OrganGestorHelper.class);

}
