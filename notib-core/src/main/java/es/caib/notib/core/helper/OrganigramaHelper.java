package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.OrganismeDto;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class OrganigramaHelper {
	
	@Autowired
	private CacheHelper cacheHelper;
	
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
	
	private static final Logger logger = LoggerFactory.getLogger(OrganigramaHelper.class);

}
