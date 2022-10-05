package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.config.ConfigGroupDto;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.config.ConfigEntity;
import es.caib.notib.persist.entity.config.ConfigGroupEntity;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.config.ConfigGroupRepository;
import es.caib.notib.persist.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    private EntitatRepository entitatRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private ConfigHelper configHelper;

    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {

        log.info(String.format("Actualització valor propietat %s a %s ", property.getKey(), property.getValue()));
        ConfigEntity configEntity = configRepository.findById(property.getKey()).orElseThrow();
        if (!configEntity.isConfigurable()) {
            log.error("ATENCIÓ S'ESTÀ INTENTANT GUARDAR UNA PROPIETAT QUE NO ÉS CONFIGURABLE");
            return null;
        }
        configEntity.update(!"null".equals(property.getValue()) ? property.getValue() : null);
        configHelper.reloadDbProperties();
        pluginHelper.resetPlugins(configEntity.getGroupCode());
        cacheHelper.clearAllCaches();
        return conversioTipusHelper.convertir(configEntity, ConfigDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigGroupDto> findAll() {

        log.info("Consulta totes les propietats");
        List<ConfigGroupEntity> groups = configGroupRepository.findByParentCodeIsNull(Sort.by(Sort.Direction.ASC, "position"));
        List<ConfigGroupDto> configGroupDtoList =  conversioTipusHelper.convertirList(groups, ConfigGroupDto.class);
        for (ConfigGroupDto cGroup: configGroupDtoList) {
            processPropertyValues(cGroup);
        }
        return configGroupDtoList;
    }

    @Override
    @Transactional
    public List<String> syncFromJBossProperties() {

        log.info("Sincronitzant les propietats amb JBoss");
        Properties properties = configHelper.getEnvironmentProperties();
        List<String> editedProperties = new ArrayList<>();
        List<String> propertiesList = new ArrayList<>(properties.stringPropertyNames());
        Collections.sort(propertiesList);
        for (String key : propertiesList) {
            String value = properties.getProperty(key);
            log.info(key + " : " + value);
            ConfigEntity configEntity = configRepository.findById(key).orElse(null);
            if (configEntity != null) {
                configEntity.update(value);
                editedProperties.add(configEntity.getKey());
            }
        }
        configHelper.reloadDbProperties();
        pluginHelper.resetAllPlugins();
        return editedProperties;
    }

    @Override
    @Transactional
    public List<ConfigDto>  findEntitatsConfigByKey(String key) {

        if (Strings.isNullOrEmpty(key) || !key.contains(ConfigDto.prefix)) {
            log.error("Entitat config key buida o no conté el prefix. Key: " + key);
            return new ArrayList<>();
        }
        String [] split = key.split(ConfigDto.prefix);
        if (split == null || split.length != 2) {
            log.error("Entitat config key no trobada. Key: " + key);
            return new ArrayList<>();
        }
        return conversioTipusHelper.convertirList(configRepository.findLikeKeyEntitatNotNullAndConfigurable(split[1]), ConfigDto.class);
    }

    @Override
    @Transactional
    public void crearPropietatsConfigPerEntitats() {

        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNullAndConfigurableIsTrue();
        List<EntitatEntity> entitats = entitatRepository.findAll();
        ConfigEntity nova;
        for (ConfigEntity config : configs) {
            for (EntitatEntity entitat : entitats) {
                String key = configHelper.crearEntitatKey(entitat.getCodi(), config.getKey());
                if (configRepository.findByKey(key) != null ) {
                    continue;
                }
                nova = new ConfigEntity();
                nova.crearConfigNova(key, entitat.getCodi(), config);
                configRepository.save(nova);
            }
        }
        configHelper.reloadDbProperties();
    }

    @Override
    public void actualitzarPropietatsJBossBdd() {

        List<ConfigEntity> configs = configRepository.findJBossConfigurables();
        for(ConfigEntity config : configs) {
            String property = configHelper.getConfigGlobal(config.getKey());
            config.setValue(property);
            configRepository.save(config);
        }
    }

    @Override
    public String getPropertyValue(String key) {
        return configHelper.getConfig(key);
    }

    private void processPropertyValues(ConfigGroupDto cGroup) {
        for (ConfigDto config: cGroup.getConfigs()) {
            if ("PASSWORD".equals(config.getTypeCode())){
                config.setValue("*****");
            } else if (config.isJbossProperty()) {
                // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat a la base de dades.
                config.setValue(configHelper.getConfig(config.getKey(), config.getValue()));
            }
        }

        if (cGroup.getInnerConfigs() != null && !cGroup.getInnerConfigs().isEmpty()) {
            for (ConfigGroupDto child : cGroup.getInnerConfigs()) {
                processPropertyValues(child);
            }
        }
    }
}
