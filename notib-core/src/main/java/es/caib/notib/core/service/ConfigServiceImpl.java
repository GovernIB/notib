package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.config.ConfigDto;
import es.caib.notib.core.api.dto.config.ConfigGroupDto;
import es.caib.notib.core.api.service.ConfigService;
import es.caib.notib.core.entity.config.ConfigEntity;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.config.ConfigGroupRepository;
import es.caib.notib.core.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Classe que implementa els metodes per consultar i editar les configuracions de l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigGroupRepository configGroupRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PluginHelper pluginHelper;

    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {
        log.info(String.format("Actualització valor propietat %s a %s ", property.getKey(), property.getValue()));
        ConfigEntity configEntity = configRepository.findOne(property.getKey());
        configEntity.update(property.getValue());
        pluginHelper.reloadProperties(configEntity.getGroupCode());
        if (property.getKey().endsWith(".class")){
            pluginHelper.resetPlugins();
        }
        return conversioTipusHelper.convertir(configEntity, ConfigDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigGroupDto> findAll() {
        log.debug("Consulta totes les propietats");
        List<ConfigGroupDto> configGroupDtoList =  conversioTipusHelper.convertirList(
                configGroupRepository.findByParentCodeIsNull(new Sort(Sort.Direction.ASC, "position")),
                ConfigGroupDto.class);

        for (ConfigGroupDto cGroup: configGroupDtoList) {
            processPropertyValues(cGroup);
        }
        return configGroupDtoList;
    }

    @Override
    @Transactional
    public List<String> syncFromJBossProperties() {
        log.debug("Sincronitzant les propietats amb JBoss");
        Properties properties = ConfigHelper.JBossPropertiesHelper.getProperties().findAll();
        List<String> editedProperties = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            log.trace(key + " : " + value);
            ConfigEntity configEntity = configRepository.findOne(key);
            if (configEntity != null) {
                configEntity.update(value);
                pluginHelper.reloadProperties(configEntity.getGroupCode());
                if (configEntity.getKey().endsWith(".class")){
                    pluginHelper.resetPlugins();
                }
                editedProperties.add(configEntity.getKey());
            }
        }
        return editedProperties;
    }

    private void processPropertyValues(ConfigGroupDto cGroup) {
        for (ConfigDto config: cGroup.getConfigs()) {
            if ("PASSWORD".equals(config.getTypeCode())){
                config.setValue("*****");
            } else if (config.isJbossProperty()) {
                // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat a la base de dades.
                config.setValue(ConfigHelper.JBossPropertiesHelper.getProperties().getProperty(config.getKey(), config.getValue()));
            }
        }

        if (cGroup.getInnerConfigs() != null && !cGroup.getInnerConfigs().isEmpty()) {
            for (ConfigGroupDto child : cGroup.getInnerConfigs()) {
                processPropertyValues(child);
            }
        }
    }
}
