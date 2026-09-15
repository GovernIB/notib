package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.model.FieldOption;
import es.caib.notib.logic.intf.model.EntregaPostalResource;
import es.caib.notib.logic.intf.resourceservice.EntregaPostalResourceService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.EntregaPostalResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei d'entrega postal
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntregaPostalResourceServiceImpl extends BaseMutableResourceService<EntregaPostalResource, Long, EntregaPostalResourceEntity> implements EntregaPostalResourceService {

	private final NotificacioService notificacioService;

	@PostConstruct
	public void init() {
		register(EntregaPostalResource.Fields.domiciliPaisCodiIso, new PaisosFieldOptionsProvider());
		register(EntregaPostalResource.Fields.domiciliProvinciaCodi, new ProvinciesFieldOptionsProvider());
	}

	class PaisosFieldOptionsProvider implements FieldOptionsProvider {
		@Override
		public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
			return notificacioService.llistarPaisos().stream().
					map(p -> new FieldOption(p.getAlfa2Pais(), p.getDescripcioPais())).
					collect(Collectors.toList());
		}
	}

	class ProvinciesFieldOptionsProvider implements FieldOptionsProvider {
		@Override
		public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
			return notificacioService.llistarProvincies().stream().
					map(p -> new FieldOption(p.getId(), p.getDescripcio())).
					collect(Collectors.toList());
		}
	}

}
