package es.caib.notib.logic.accionsMassives;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.AccioMassivaParams;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EsborrarMassiuActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, AccioMassivaParams, RespostaAccio> {

	private final AccioMassivaService accioMassivaService;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final EnviamentService enviamentService;

	@Override
	public RespostaAccio exec(String code, NotificacioResourceEntity entity, AccioMassivaParams params) throws ActionExecutionException {

		if (params == null || params.idsEmpty()) {
			throw new ActionExecutionException(NotificacioResource.class, null, "-1", "La selecció no pot ser buida");
		}
		try {
			var entitatActual = userSessionHelper.getCurrentEntitatId();
			boolean isAdminEntitat = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
			var accio = AccioMassivaExecucio.builder()
							.isAdminEntitat(isAdminEntitat)
							.tipus(AccioMassivaTipus.ESBORRAR)
							.tipusElementSeleccionat(SeleccioTipus.NOTIFICACIO)
							.entitatId(entitatActual)
							.seleccio(params.getIds())
							.build();
			var accioId = accioMassivaService.altaAccioMassiva(accio);
			accio.setAccioId(accioId);
			var noEsborrades = accioMassivaService.esborrarNotificacions(accio);
			return RespostaAccio.builder().noExecutables(noEsborrades).build();
		} catch (Exception ex) {
			var msg = "Error inesperat esborrant les remeses";
			log.error("[EsborrarMassiuActionExecutor] " + msg);
			throw new ActionExecutionException(NotificacioResource.class, null, "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, AccioMassivaParams previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, AccioMassivaParams target) {

	}

}
