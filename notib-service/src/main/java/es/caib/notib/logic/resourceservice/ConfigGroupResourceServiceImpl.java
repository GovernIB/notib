package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.ConfigGroupResource;
import es.caib.notib.logic.intf.resourceservice.ConfigGroupResourceService;
import es.caib.notib.persist.resourceentity.ConfigGroupResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGroupResourceServiceImpl extends BaseMutableResourceService<ConfigGroupResource, Long, ConfigGroupResourceEntity> implements ConfigGroupResourceService {

}