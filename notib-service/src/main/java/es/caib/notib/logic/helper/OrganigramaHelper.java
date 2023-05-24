package es.caib.notib.logic.helper;

import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class OrganigramaHelper {

	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private ProcSerOrganRepository procSerOrganRepository;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	
	public List<OrganismeDto> getOrganismesFillsByOrgan(String codiDir3Entitat, String codiDir3Organ) {

		Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(codiDir3Entitat);
		String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
		List<OrganismeDto> organismes= new ArrayList<OrganismeDto>();
		organismes.addAll(getOrgansGestorsFills(organigramaEntitat, codiDir3));
		return organismes;
	}
	
	public List<String> getCodisOrgansGestorsFillsByEntitat(String codiDir3Entitat) {
		return getCodisOrgansGestorsFillsByOrgan(codiDir3Entitat, null);
	}
	
	public List<String> getCodisOrgansGestorsFillsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		return organGestorCachable.getCodisOrgansGestorsFillsByOrgan(codiDir3Entitat, codiDir3Organ);
	}

	public List<String> getCodisOrgansGestorsFillsExistentsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		List<String> unitatsEntitat = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(codiDir3Entitat, codiDir3Organ);

		List<String> unitatsExistents = organGestorRepository.findCodisByEntitatDir3(codiDir3Entitat);
		unitatsEntitat.retainAll(unitatsExistents);
		return unitatsEntitat;
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
		Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(codiDir3Entitat);
		
		String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
		
		List<String> unitatsEntitat = new ArrayList<String>();
		unitatsEntitat.addAll(getCodisOrgansGestorsPare(organigramaEntitat, codiDir3, codiDir3Entitat));
		
		List<String> unitatsExistents = organGestorRepository.findCodisByEntitatDir3(codiDir3Entitat);
		unitatsEntitat.retainAll(unitatsExistents);
		return unitatsEntitat;
	}

	/**
	 *
	 * @param codiDir3Entitat Codi dir3 de l'entitat actual.
	 * @param codiDir3Organ Codi dir3 de l'òrgan consultat.
	 * @return Conjunt dels codis dels OrganGestorEntity fills del òrgan indicat.
	 * 		   Inclou el OrganGestorEntity del codi indicat per paràmetre
	 */
	public List<OrganGestorEntity> getOrgansGestorsParesExistentsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
		List<String> unitatsEntitat = getCodisOrgansGestorsParesExistentsByOrgan(codiDir3Entitat, codiDir3Organ);
		if (!unitatsEntitat.isEmpty())
			return organGestorRepository.findByCodiIn(unitatsEntitat);
		else
			return new ArrayList<>();
	}

	public List<ProcSerOrganEntity> getProcSerOrgansGestorsParesExistentsByOrgan(Long procedimentId, String codiDir3Entitat, String codiDir3Organ) {
		List<String> unitatsEntitat = getCodisOrgansGestorsParesExistentsByOrgan(codiDir3Entitat, codiDir3Organ);
		if (!unitatsEntitat.isEmpty())
			return procSerOrganRepository.findByProcSerIdAndOrganGestorCodiIn(procedimentId, unitatsEntitat);
		else
			return new ArrayList<>();
	}

	/**
	 * Consulta el codi de tots els òrgans gestor d'un òrgan concret.
	 * Inclou el codi de l'òrgan indicat per paràmetre.
	 *
	 * @param organigrama Mapa de l'organigrama amb tots els òrgans de l'aplicació
	 * @param codiDir3 Codi dir3 de l'òrgan consultat.
	 * @param codiDir3Entitat Codi dir3 de l'entitat actual.
	 *
	 * @return Conjunt dels codis dels òrganismes fills del òrgan indicat.
	 * 		   Inclou el codi de l'òrgan indicat per paràmetre
	 */
	private List<String> getCodisOrgansGestorsPare(
			Map<String, OrganismeDto> organigrama,
			String codiDir3,
			String codiDir3Entitat) {
		List<String> unitats = new ArrayList<String>();
		unitats.add(codiDir3);
		if (!codiDir3.equals(codiDir3Entitat)) {
			OrganismeDto organisme = organigrama.get(codiDir3);
			if (organisme != null && organisme.getPare() != null && !organisme.getPare().equals(codiDir3Entitat)) {
				unitats.addAll(getCodisOrgansGestorsPare(organigrama, organisme.getPare(), codiDir3Entitat));
			}
		}
		return unitats;
	}
	
//	private static final Logger logger = LoggerFactory.getLogger(OrganigramaHelper.class);

}
