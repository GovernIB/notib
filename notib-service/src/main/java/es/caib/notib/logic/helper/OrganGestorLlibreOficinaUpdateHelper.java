package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Helper per a actualitzar llibres i oficines d'un òrgan gestor.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrganGestorLlibreOficinaUpdateHelper {

	private final PluginHelper pluginHelper;
	private final CacheHelper cacheHelper;

	/**
	 * Actualitza el llibre d'un òrgan gestor.
	 *
	 * @param organGestor
	 *            l'òrgan gestor del qual es vol actualitzar l'oficina.
	 */
	public void updateLlibre(
		OrganGestorResourceEntity organGestor) {
		LlibreDto llibre = pluginHelper.llistarLlibreOrganisme(
			organGestor.getEntitat().getDir3Codi(),
			organGestor.getCodi());
		if (llibre != null) {
			organGestor.setLlibre(llibre.getCodi());
			organGestor.setLlibreNom(llibre.getNomLlarg());
		}
	}

	/**
	 * Actualitza el llibre d'un òrgan gestor.
	 *
	 * @param organGestor
	 *            l'òrgan gestor del qual es vol actualitzar l'oficina.
	 * @param integracioInfo
	 *            informació de la integració (pot ser null).
	 */
	public void updateOficina(
		OrganGestorResourceEntity organGestor,
		IntegracioInfo integracioInfo) {
		Map<String, OrganismeDto> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(
			organGestor.getEntitat().getDir3Codi());
		try {
			log.debug("OFISYNC - Obtenint oficines de l'òrgan {}}", organGestor.getCodi());
			List<OficinaDto> oficines = cacheHelper.getOficinesSIRUnitat(arbreUnitats, organGestor.getCodi());
			log.debug("OFISYNC - Obtingudes {} oficines", oficines == null ? 0 : oficines.size());
			if (oficines != null && !oficines.isEmpty()) {
				OficinaDto oficina = oficines.get(0);
				if (StringUtils.isEmpty(organGestor.getOficina()) || !StringUtils.isEmpty(organGestor.getOficina()) && !oficines.toString().contains(organGestor.getOficina())) {
					log.debug(
						"OFISYNC - Actualitzant oficina: {} -> {}",
						organGestor.getOficina(),
						oficina.getCodi());
					if (integracioInfo != null) {
						integracioInfo.addParam(
							organGestor.getCodi(),
							"Actualitzant la oficina: " + organGestor.getOficina() + " -> " + oficina.getCodi());
					}
					organGestor.setOficina(oficina.getCodi());
					organGestor.setOficinaNom(oficina.getNom());
					log.debug("OFISYNC - Oficina actualitzada");
				} else {
					log.debug("OFISYNC - L'oficina no s'ha d'actualitzar");
				}
			} else {
				if (integracioInfo != null) {
					integracioInfo.addParam(organGestor.getCodi(), "No s'han obtingut oficines de l'òrgan " + organGestor.getCodi());
				}
			}
		} catch (Exception ex) {
			String msg = "S'ha produit un error obtenint les oficines de l'òrgan " + organGestor.getCodi();
			if (integracioInfo != null) {
				integracioInfo.addParam(organGestor.getCodi(), msg);
			}
			log.error(msg);
		}
	}

}
