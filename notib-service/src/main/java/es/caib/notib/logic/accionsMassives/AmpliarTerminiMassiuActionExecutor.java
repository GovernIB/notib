package es.caib.notib.logic.accionsMassives;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.AmpliacionPlazoDto;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AmpliarTerminiMassiuActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, AmpliacionPlazoDto, RespostaActionExecutor> {

	private final AccioMassivaService accioMassivaService;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;

	@Override
	public RespostaActionExecutor exec(String code, NotificacioResourceEntity entity, AmpliacionPlazoDto params) throws ActionExecutionException {

		if (params == null || params.idsEmpty()) {
			throw new ActionExecutionException(NotificacioResource.class, null, "-1", "La selecció no pot ser buida");
		}
		try {
			var entitatActual = userSessionHelper.getCurrentEntitatId();
			var isAdminEntitat = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
			var accio = AccioMassivaExecucio.builder()
							.isAdminEntitat(isAdminEntitat)
							.tipus(AccioMassivaTipus.AMPLIAR_TERMINI)
							.tipusElementSeleccionat(params.getSeleccioTipus())
							.entitatId(entitatActual)
							.seleccio(params.getIds())
							.motiu(params.getMotiu())
							.dies(params.getDies())
							.build();
			var accioId = accioMassivaService.altaAccioMassiva(accio);
			accio.setAccioId(accioId);
			accioMassivaService.executarAccio(accio);
			return RespostaActionExecutor.builder().ok(true).build();
		} catch (Exception ex) {
			var msg = "Error inesperat ampliant termini massiu";
			log.error("[AmpliarTerminiMassiuActionExecutor] " + msg);
			throw new ActionExecutionException(NotificacioResource.class, null, "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, AmpliacionPlazoDto previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, AmpliacionPlazoDto target) {

	}

}
