package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.config.SchedulingConfig;
import es.caib.notib.logic.helper.ExcepcioLogHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.notib.logic.intf.model.BackGroundTaskResource;
import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.logic.intf.monitor.MonitorTascaInfo;
import es.caib.notib.logic.intf.resourceservice.BackGroundTaskResourceService;
import es.caib.notib.logic.intf.service.MonitorTasquesService;
import es.caib.notib.logic.intf.util.StringUtils;
import es.caib.notib.persist.resourceentity.BackGroundTaskResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackGroundTaskResourceServiceImpl extends BaseMutableResourceService<BackGroundTaskResource, String, BackGroundTaskResourceEntity> implements BackGroundTaskResourceService {

	private final MonitorTasquesService monitorTasquesService;
	private final SchedulingConfig schedulingConfig;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final MessageHelper messageHelper;

    @PostConstruct
    public void init() {
    	register(BackGroundTaskResource.ACTION_RESTART_TASK,	new RestartTaskActionExecutor());
    }

	@Override
	public Page<BackGroundTaskResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives, Pageable pageable) {

		List<BackGroundTaskResource> resultat = new ArrayList<>();

		List<MonitorTascaInfo> monitorTasques = monitorTasquesService.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		for(MonitorTascaInfo monitorTasca : monitorTasques) {

			String nomTasca = messageHelper.getMessage("monitor.tasques.tasca.codi." + monitorTasca.getCodi());

			if (!StringUtils.hasValue(quickFilter) || nomTasca.contains(quickFilter)) {

				BackGroundTaskResource btr = new BackGroundTaskResource();
				btr.setId(monitorTasca.getCodi());
				btr.setNom(nomTasca);
				btr.setEstat(messageHelper.getMessage("monitor.tasques.estat." + monitorTasca.getEstat()));

				String strDataInici = "-";
				if (monitorTasca.getDataInici() != null) {
					strDataInici = sdf.format(monitorTasca.getDataInici());
				}
				btr.setDataInici(strDataInici);

				btr.setTempsExecucio(monitorTasca.getTempsExecucio());

				String strProperaExecucio = "-";
				if ( ! MonitorTascaEstat.EN_EXECUCIO.equals(monitorTasca.getEstat())
						&& monitorTasca.getProperaExecucio() != null) {
					strProperaExecucio = sdf.format(monitorTasca.getProperaExecucio());
				}
				btr.setProperaExecucio(strProperaExecucio);

				btr.setObservacions(monitorTasca.getObservacions());

				resultat.add(btr);
			}
		}

		Page<BackGroundTaskResource> page = new PageImpl<>(resultat, pageable, resultat.size());
		return page;
	}

	@Override
	public BackGroundTaskResource getOne(String id, String[] perspectives) throws ResourceNotFoundException {
		return null;
	}

	private class RestartTaskActionExecutor implements ActionExecutor<BackGroundTaskResourceEntity, BackGroundTaskResource.MassiveRestartTaskForm, Serializable> {

		@Override
		public Serializable exec(String code, BackGroundTaskResourceEntity entity, BackGroundTaskResource.MassiveRestartTaskForm params) throws ActionExecutionException {
			try {
				if (params.getIds()!=null) {
					for (String id: params.getIds()) {
						monitorTasquesService.reiniciarTasquesEnSegonPla(id);
//						schedulingConfig.restartSchedulledTasks(id);
					}
					schedulingConfig.restartSchedulledTasks(); //TODO s'ha comentant el restart per id. Revisar si cal afegir el restart by id
				}
				return "{\"resultat\": \"OK\"}";
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/backGroundTask/"+entity.getId()+"/RestartTaskActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}

		@Override
		public void onChange(Serializable id, BackGroundTaskResource.MassiveRestartTaskForm previous, String fieldName, Object fieldValue,
							 Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, BackGroundTaskResource.MassiveRestartTaskForm target) {}
	}

	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}
}
