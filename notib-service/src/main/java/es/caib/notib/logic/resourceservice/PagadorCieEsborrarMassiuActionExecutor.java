package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.persist.resourceentity.PagadorCieResourceEntity;
import es.caib.notib.persist.resourcerepository.PagadorCieResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class PagadorCieEsborrarMassiuActionExecutor implements BaseMutableResourceService.ActionExecutor<PagadorCieResourceEntity, Serializable, RespostaActionExecutor> {

	private final PagadorCieResourceRepository pagadorCieResourceRepository;

	@Override
	public RespostaActionExecutor exec(String code, PagadorCieResourceEntity entity, Serializable params) throws ActionExecutionException {
		try {
			if (!(params instanceof PagadorCieResource.EsborrarMassiuParams)) {
				throw new ActionExecutionException(PagadorCieResource.class, null, "-1",
					"Els paràmetres de l'acció no són vàlids");
			}
			PagadorCieResource.EsborrarMassiuParams actionParams = (PagadorCieResource.EsborrarMassiuParams) params;
			List<Long> ids = actionParams.getIds();
			if (ids == null || ids.isEmpty()) {
				throw new ActionExecutionException(PagadorCieResource.class, null, "-1",
					"La llista d'IDs no pot ser buida");
			}
			pagadorCieResourceRepository.deleteAllById(ids);
			return RespostaActionExecutor.builder().ok(true).build();
		} catch (DataIntegrityViolationException ex) {
			log.error("[PagadorCieEsborrarMassiu] Error d'integritat referencial en esborrar els elements", ex);
			throw new ActionExecutionException(PagadorCieResource.class, null, "-1",
				"No s'han pogut eliminar els elements seleccionats perquè estan associats a altres registres del sistema.");
		} catch (ActionExecutionException ex) {
			throw ex;
		} catch (Exception ex) {
			var msg = "Error inesperat esborrant els Pagadors CIE massivament";
			log.error("[PagadorCieEsborrarMassiuActionExecutor] " + msg, ex);
			throw new ActionExecutionException(PagadorCieResource.class, null, "-1",
				"Ha ocorregut un error inesperat en eliminar els elements. Si el problema persisteix, contacteu amb l'administrador.");
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {
	}
}
