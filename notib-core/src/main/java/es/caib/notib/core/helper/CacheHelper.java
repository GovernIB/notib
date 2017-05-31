/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;

/**
 * Utilitat per a accedir a les caches. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CacheHelper { 

	@Resource
	private EntitatRepository entitatRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private UsuariHelper usuariHelper;



	@Cacheable(value = "entitatsUsuari", key="#usuariCodi")
	public List<EntitatDto> findEntitatsAccessiblesUsuari(
			String usuariCodi) {
		logger.debug("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ")");
		List<EntitatEntity> entitats = entitatRepository.findByActiva(true);
		return conversioTipusHelper.convertirList(
				entitats,
				EntitatDto.class);
	}
	@CacheEvict(value = "entitatsUsuari", key="#usuariCodi")
	public void evictEntitatsAccessiblesUsuari(String usuariCodi) {
	}

	@Cacheable(value = "usuariAmbCodi", key="#usuariCodi")
	public DadesUsuari findUsuariAmbCodi(
			String usuariCodi) {
		return pluginHelper.dadesUsuariConsultarAmbCodi(
				usuariCodi);
	}

//	@Cacheable(value = "unitatsOrganitzatives", key="#entitatCodi")
//	public ArbreDto<UnitatOrganitzativaDto> findUnitatsOrganitzativesPerEntitat(
//			String entitatCodi) {
//		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
//		return pluginHelper.unitatsOrganitzativesFindArbreByPare(
//				entitat.getUnitatArrel());
//	}
//	@CacheEvict(value = "unitatsOrganitzatives", key="#entitatCodi")
//	public void evictUnitatsOrganitzativesPerEntitat(
//			String entitatCodi) {
//	}
//
//	@Cacheable(value = "unitatOrganitzativa", key="#organCodi")
//	public UnitatOrganitzativaDto findUnitatOrganitzativaPerCodi(
//			String organCodi) {
//		UnitatOrganitzativaDto unitat = pluginHelper.unitatsOrganitzativesFindByCodi(organCodi);
//		if (unitat != null) {
//			unitat.setAdressa(
//					getAdressa(
//							unitat.getTipusVia(), 
//							unitat.getNomVia(), 
//							unitat.getNumVia()));
//			if (unitat.getCodiPais() != null && !"".equals(unitat.getCodiPais()))
//				unitat.setCodiPais(("000" + unitat.getCodiPais()).substring(unitat.getCodiPais().length()));
//			if(unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat()))
//				unitat.setCodiComunitat(("00" + unitat.getCodiComunitat()).substring(unitat.getCodiComunitat().length()));
//			
//			if ((unitat.getCodiProvincia() == null || "".equals(unitat.getCodiProvincia())) && 
//					unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
//				List<ProvinciaDto> provincies = findProvinciesPerComunitat(unitat.getCodiComunitat());
//				if (provincies != null && provincies.size() == 1) {
//					unitat.setCodiProvincia(provincies.get(0).getCodi());
//				}		
//			}
//			if (unitat.getCodiProvincia() != null && !"".equals(unitat.getCodiProvincia())) {
//				unitat.setCodiProvincia(("00" + unitat.getCodiProvincia()).substring(unitat.getCodiProvincia().length()));
//				
//				if (unitat.getLocalitat() == null && unitat.getNomLocalitat() != null) {
//					MunicipiDto municipi = findMunicipiAmbNom(
//							unitat.getCodiProvincia(), 
//							unitat.getNomLocalitat());
//					if (municipi != null)
//						unitat.setLocalitat(municipi.getCodi());
//					else
//						logger.error("UNITAT ORGANITZATIVA. No s'ha trobat la localitat amb el nom: '" + unitat.getNomLocalitat() + "'");
//				}
//			}
//		}
//		return unitat;
//	}

	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);

}
