package es.caib.notib.logic.avisos;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.AccioMassivaParams;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import es.caib.notib.persist.resourcerepository.AvisResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Accions massives sobre avisos: activar, desactivar i esborrar.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@AllArgsConstructor
public class AvisMassiuActionExecutor implements BaseMutableResourceService.ActionExecutor<AvisResourceEntity, AccioMassivaParams, Serializable> {

	private final AvisResourceRepository avisRepository;

	@Override
	public Serializable exec(String code, AvisResourceEntity entity, AccioMassivaParams params) throws ActionExecutionException {

		if (params == null || params.idsEmpty()) {
			throw new ActionExecutionException(AvisResource.class, null, code, "La selecció no pot ser buida");
		}
		try {
			var avisos = avisRepository.findAllById(params.getIds());
			switch (code) {
				case AvisResource.ACTION_ACTIVAR_MASSIU:
					avisos.forEach(a -> a.setActiu(true));
					break;
				case AvisResource.ACTION_DESACTIVAR_MASSIU:
					avisos.forEach(a -> a.setActiu(false));
					break;
				case AvisResource.ACTION_ESBORRAR_MASSIU:
					avisRepository.deleteAll(avisos);
					break;
				default:
					throw new ActionExecutionException(AvisResource.class, null, code, "Acció desconeguda");
			}
			return null;
		} catch (ActionExecutionException ex) {
			throw ex;
		} catch (Exception ex) {
			var msg = "Error inesperat executant l'acció massiva " + code + " sobre avisos: ";
			log.error("[AvisMassiuActionExecutor] " + msg, ex);
			throw new ActionExecutionException(AvisResource.class, null, code, msg + ex.getMessage(), ex);
		}
	}

	@Override
	public void onChange(Serializable id, AccioMassivaParams previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AccioMassivaParams target) {
	}
}
