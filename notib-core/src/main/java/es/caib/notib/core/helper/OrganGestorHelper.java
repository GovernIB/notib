package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentOrganRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.usuari.DadesUsuari;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	private CacheHelper cacheHelper;
	@Autowired
	private OrganigramaHelper organigramaHelper;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;

	@Cacheable(value = "organsEntitiesPermis", key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
	public List<OrganGestorEntity> getProcedimentsWithPermis(
			String usuariCodi,
			Authentication auth,
			EntitatEntity entitat,
			Permission[] permisos) {

		// 1. Obtenim els òrgans gestors amb permisos
		List<OrganGestorEntity> organsDisponibles = organGestorRepository.findByEntitat(entitat);

		permisosHelper.filterGrantedAny(
				organsDisponibles,
				new PermisosHelper.ObjectIdentifierExtractor<OrganGestorEntity>() {
					public Long getObjectIdentifier(OrganGestorEntity organGestor) {
						return organGestor.getId();
					}
				},
				OrganGestorEntity.class,
				permisos,
				auth);

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

	private static final Logger logger = LoggerFactory.getLogger(OrganGestorHelper.class);

}