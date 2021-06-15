package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
			NotificacioDatabaseDto notificacio,
			EntitatEntity entitat
	) {
		String codiOrgan = notificacio.getOrganGestorCodi();
		OrganGestorEntity organGestor = organGestorRepository.findByCodi(codiOrgan);
		if (organGestor == null) {
			Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
			if (!organigramaEntitat.containsKey(codiOrgan)) {
				throw new NotFoundException(
						codiOrgan,
						OrganGestorEntity.class,
						"L'òrgan gestor especificat no es correspon a cap Òrgan Gestor de l'entitat especificada");
			}
			crearOrganGestor(entitat, codiOrgan);
		}

		return organGestor;
	}

	/**
	 * Registra un nou òrgan gestor a la base de dades amb les dades del òrgan amb aquest codi
	 * proporcionades per la API de DIR3.
	 *
	 * @param entitat L'entitat actual
	 * @param codiOrgan Codi dir3 de l'òrgan que es vol agregar a la base de dades
	 *
	 * @return L'òrgan gestor creat
	 */
	public OrganGestorEntity crearOrganGestor(EntitatEntity entitat, String codiOrgan) {
		LlibreDto llibreOrgan = pluginHelper.llistarLlibreOrganisme(
				entitat.getCodi(),
				codiOrgan);
		Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
		List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
				arbreUnitats,
				codiOrgan);
		NodeDir3 nodeOrgan = arbreUnitats.get(codiOrgan);
		OrganGestorEntity organGestor = OrganGestorEntity.builder(
				codiOrgan,
				findDenominacioOrganisme(nodeOrgan, codiOrgan),
				entitat,
				llibreOrgan.getCodi(),
				llibreOrgan.getNomLlarg(),
				(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getCodi() : null),
				(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getNom() : null),
				getEstatOrgan(nodeOrgan)
		).build();
		organGestorRepository.save(organGestor);
		return organGestor;
	}

	/**
	 * Obté l'estat de l'organ gestor d'un node de dir3 en el format utilitzat per NOTIB
	 *
	 * @param nodeOrgan Node d'un òrgan gestor obtingut de l'API de DIR3
	 *
	 * @return L'estat de l'òrgan
	 */
	public OrganGestorEstatEnum getEstatOrgan(NodeDir3 nodeOrgan) {
		if (nodeOrgan == null){
			return OrganGestorEstatEnum.ALTRES;
		}

		if ("Vigente".equals(nodeOrgan.getEstat())) {
			return OrganGestorEstatEnum.VIGENT;

		} else {
			return OrganGestorEstatEnum.ALTRES;
		}
	}

	private String findDenominacioOrganisme(NodeDir3 nodeOrgan, String codiDir3) {
		if (nodeOrgan != null){
			return nodeOrgan.getDenominacio();
		}
		String denominacio = null;
		try {
			denominacio = cacheHelper.findDenominacioOrganisme(codiDir3);
		} catch (Exception e) {
			String errorMessage = "No s'ha pogut recuperar la denominació de l'organismes: " + codiDir3;
			log.error(
					errorMessage,
					e.getMessage());
		}
		return denominacio;
	}
}
