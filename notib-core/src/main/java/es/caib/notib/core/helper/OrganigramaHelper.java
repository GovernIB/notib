package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.repository.OrganGestorRepository;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class OrganigramaHelper {
	
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	
	public List<OrganismeDto> getOrganismesFillsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiDir3Entitat);
		
		String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
		
		List<OrganismeDto> organismes= new ArrayList<OrganismeDto>();
		organismes.addAll(getOrgansGestorsFills(organigramaEntitat, codiDir3));
		return organismes;
	}
	
	public List<String> getCodisOrgansGestorsFillsByEntitat(String codiDir3Entitat) {
		return getCodisOrgansGestorsFillsByOrgan(codiDir3Entitat, null);
	}
	
	public List<String> getCodisOrgansGestorsFillsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiDir3Entitat);
		
		String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
		
		List<String> unitatsEntitat = new ArrayList<String>();
		
		unitatsEntitat.addAll(getCodisOrgansGestorsFills(organigramaEntitat, codiDir3));
		return unitatsEntitat;
	}
	
	public List<String> getCodisOrgansGestorsFillsExistentsByEntitat(String codiDir3Entitat) {
		return getCodisOrgansGestorsFillsExistentsByOrgan(codiDir3Entitat, null);
	}
	
	public List<String> getCodisOrgansGestorsFillsExistentsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiDir3Entitat);
		
		String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
		
		List<String> unitatsEntitat = new ArrayList<String>();
		unitatsEntitat.addAll(getCodisOrgansGestorsFills(organigramaEntitat, codiDir3));
		
		List<String> unitatsExistents = organGestorRepository.findCodisByEntitatDir3(codiDir3Entitat);
		unitatsEntitat.retainAll(unitatsExistents);
		return unitatsEntitat;
	}

	private List<String> getCodisOrgansGestorsFills(
			Map<String, OrganismeDto> organigrama,
			String codiDir3) {
		List<String> unitats = new ArrayList<String>();
		unitats.add(codiDir3);
		OrganismeDto organisme = organigrama.get(codiDir3);
		if (organisme != null && organisme.getFills() != null && !organisme.getFills().isEmpty()) {
			for (String fill: organisme.getFills()) {
				unitats.addAll(getCodisOrgansGestorsFills(organigrama, fill));
			}
		}
		return unitats;
	}
	
	private List<OrganismeDto> getOrgansGestorsFills(
			Map<String, OrganismeDto> organigrama,
			String codiDir3) {
		List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
		OrganismeDto organisme = organigrama.get(codiDir3);
		organismes.add(organisme);
		if (organisme != null && organisme.getFills() != null && !organisme.getFills().isEmpty()) {
			for (String fill: organisme.getFills()) {
				organismes.addAll(getOrgansGestorsFills(organigrama, fill));
			}
		}
		return organismes;
	}

	public List<String> getCodisOrgansGestorsParesExistentsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiDir3Entitat);
		
		String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
		
		List<String> unitatsEntitat = new ArrayList<String>();
		unitatsEntitat.addAll(getCodisOrgansGestorsPare(organigramaEntitat, codiDir3, codiDir3Entitat));
		
		List<String> unitatsExistents = organGestorRepository.findCodisByEntitatDir3(codiDir3Entitat);
		unitatsEntitat.retainAll(unitatsExistents);
		return unitatsEntitat;
	}
	
	public List<OrganGestorEntity> getOrgansGestorsParesExistentsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiDir3Entitat);
		
		String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
		
		List<String> unitatsEntitat = new ArrayList<String>();
		unitatsEntitat.addAll(getCodisOrgansGestorsPare(organigramaEntitat, codiDir3, codiDir3Entitat));
		
		List<String> unitatsExistents = organGestorRepository.findCodisByEntitatDir3(codiDir3Entitat);
		unitatsEntitat.retainAll(unitatsExistents);
		
		return organGestorRepository.findByCodiIn(unitatsEntitat);
	}

	private List<String> getCodisOrgansGestorsPare(
			Map<String, OrganismeDto> organigrama,
			String codiDir3,
			String codiDir3Entitat) {
		List<String> unitats = new ArrayList<String>();
		unitats.add(codiDir3);
		OrganismeDto organisme = organigrama.get(codiDir3);
		if (organisme != null && organisme.getPare() != null && !organisme.getPare().equals(codiDir3Entitat)) {
			unitats.addAll(getCodisOrgansGestorsPare(organigrama, organisme.getPare(), codiDir3Entitat));
		}
		return unitats;
	}
	
//	private static final Logger logger = LoggerFactory.getLogger(OrganigramaHelper.class);

}
