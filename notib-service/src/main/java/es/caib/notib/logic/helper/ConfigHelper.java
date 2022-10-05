package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.exception.NotDefinedConfigException;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.persist.entity.config.ConfigEntity;
import es.caib.notib.persist.entity.config.ConfigGroupEntity;
import es.caib.notib.persist.repository.config.ConfigGroupRepository;
import es.caib.notib.persist.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static es.caib.notib.logic.config.ReadDbPropertiesPostProcessor.DBAPP_PROPERTIES;

@Component
@Slf4j
@PropertySource(ignoreResourceNotFound = true, value = {
        "file://${" + ConfigService.APP_PROPERTIES + "}",
        "file://${" + ConfigService.APP_SYSTEM_PROPERTIES + "}"})
public class ConfigHelper {

    @Autowired
    private ConfigurableEnvironment environment;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigGroupRepository configGroupRepository;


    private static ThreadLocal<EntitatDto> entitat = new ThreadLocal<>();

    public static ThreadLocal<EntitatDto> getEntitat() {
        return entitat;
    }

    public static void setEntitat(EntitatDto entitat) {
        ConfigHelper.entitat.set(entitat);
    }

    public String getEntitatActualCodi() {
        return entitat != null && entitat.get() != null ? entitat.get().getCodi() : null;
    }

    public String getConfigGlobal(String propietatGlobal) {
        return environment.getProperty(propietatGlobal);
    }
    public String getConfigByEntitat(String entitatCodi, String propietatGlobal) {
        return getPropietat(entitatCodi, propietatGlobal).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    public String getConfig(String propietatGlobal)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).orElse(null);
    }

    public String getConfig(String propietatGlobal, String defaultValue)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).orElse(defaultValue);
    }

    public Long getConfigAsLong(String propietatGlobal)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).map(Long::parseLong).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    public Long getConfigAsLong(String propietatGlobal, Long defaultValue)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).map(Long::parseLong).orElse(defaultValue);
    }

    public Integer getConfigAsInteger(String propietatGlobal)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).map(Integer::parseInt).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    public Integer getConfigAsInteger(String propietatGlobal, Integer defaultValue)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).map(Integer::parseInt).orElse(defaultValue);
    }

    public Boolean getConfigAsBoolean(String propietatGlobal)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).map(Boolean::parseBoolean).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    public Boolean getConfigAsBoolean(String propietatGlobal, Boolean defaultValue)  {
        var entitatCodi  = getEntitatActualCodi();
        return getPropietat(entitatCodi, propietatGlobal).map(Boolean::parseBoolean).orElse(defaultValue);
    }

    private Optional<String> getPropietat(String entitatCodi, String globalKey) {
        String propertyValue = null;
        String entitatKey = null;

        if (!Strings.isNullOrEmpty(entitatCodi)) {
            entitatKey = crearEntitatKey(entitatCodi, globalKey);
            propertyValue = environment.getProperty(entitatKey);
            if (!Strings.isNullOrEmpty(propertyValue))
                return Optional.of(propertyValue);
        }

        propertyValue = environment.getProperty(globalKey);
        if (propertyValue != null)
            return Optional.of(propertyValue);

        log.error("No s'ha trobat la propietat -> key global: " + globalKey + " key entitat: " + entitatKey);
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Map<String, String> getGroupProperties(String codeGroup) {
        Map<String, String> properties = new HashMap<>();
        ConfigGroupEntity configGroup = configGroupRepository.findById(codeGroup).orElse(null);
        fillGroupProperties(configGroup, properties);
        return properties;
    }

    private void fillGroupProperties(ConfigGroupEntity configGroup, Map<String, String> outProperties) {
        if (configGroup == null) {
            return;
        }
        for (ConfigEntity config : configGroup.getConfigs()) {
            outProperties.put(config.getKey(), getConfig(config.getKey()));
        }

        if (configGroup.getInnerConfigs() != null) {
            for (ConfigGroupEntity child : configGroup.getInnerConfigs()) {
                fillGroupProperties(child, outProperties);
            }
        }
    }

    @Transactional(readOnly = true)
    public Properties getAllEntityProperties(String entitatCodi) {
        Properties properties = new Properties();
        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNull();
        for (ConfigEntity config: configs) {
             String value = !Strings.isNullOrEmpty(entitatCodi) ? getConfigByEntitat(entitatCodi, config.getKey()) : getConfig(config.getKey());
            if (value != null) {
                properties.put(config.getKey(), value);
            }
        }
        return properties;
    }

//    private String getConfig(ConfigEntity configEntity) throws NotDefinedConfigException {
//        if (configEntity.isJbossProperty()) {
//            // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
//            return getJBossProperty(configEntity.getKey(), configEntity.getValue());
//        }
//        return configEntity.getValue();
//    }

    public void crearConfigsEntitat(String codiEntitat) {

        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNullAndConfigurableIsTrue();
        ConfigDto dto = new ConfigDto();
        dto.setEntitatCodi(codiEntitat);
        ConfigEntity nova;
        List<ConfigEntity> confs = new ArrayList<>();
        for (ConfigEntity config : configs) {
            dto.setKey(config.getKey());
            String key = dto.crearEntitatKey();
            nova = new ConfigEntity();
            nova.crearConfigNova(key, codiEntitat, config);
            confs.add(nova);
        }
        configRepository.saveAll(confs);
        reloadDbProperties();
    }

    public void deleteConfigEntitat(String codiEntitat) {
        configRepository.deleteByEntitatCodi(codiEntitat);
        reloadDbProperties();
    }

    public String crearEntitatKey(String entitatCodi, String key) {

        if (entitatCodi == null || entitatCodi == "" || key == null || key == "") {
            String msg = "Codi entitat " + entitatCodi + " i/o key " + key + " no contenen valor";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        String [] split = key.split(ConfigDto.prefix);
        if (split == null) {
            String msg = "Format no reconegut per la key: " + key;
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return split.length < 2 ? split.length == 0 ? null : split[0] : (ConfigDto.prefix + "." + entitatCodi + split[1]);
    }

    public void reloadDbProperties() {
        Map<String, Object> propertySource = new HashMap<>();

        List<ConfigEntity> dbProperties = configRepository.findDbProperties();
        dbProperties.forEach(p -> propertySource.put(p.getKey(), p.getValue()));
        if (environment.getPropertySources().contains(DBAPP_PROPERTIES)) {
            environment.getPropertySources().replace(DBAPP_PROPERTIES, new MapPropertySource(DBAPP_PROPERTIES, propertySource));
        } else {
            environment.getPropertySources().addFirst(new MapPropertySource(DBAPP_PROPERTIES, propertySource));
        }
    }

    public Map<String, Object> getEnvironmentPropertiesMap() {

        Map<String, Object> propertiesMap = new HashMap();
        environment.getPropertySources().stream().forEach(ps -> {
            if (ps instanceof MapPropertySource) {
                propertiesMap.putAll(((MapPropertySource) ps).getSource());
            }
            if (ps instanceof CompositePropertySource) {
                ((CompositePropertySource) ps).getPropertySources().stream().forEach( cps -> {
                    if (cps instanceof MapPropertySource) {
                        propertiesMap.putAll(((MapPropertySource) cps).getSource());
                    }
                });
            }
        });
        return propertiesMap;
    }

    public Properties getEnvironmentProperties() {

        var properties = new Properties();
        environment.getPropertySources().stream().forEach(ps -> {
            if (ps instanceof MapPropertySource) {
                properties.putAll(((MapPropertySource) ps).getSource());
            }
            if (ps instanceof CompositePropertySource) {
                ((CompositePropertySource) ps).getPropertySources().stream().forEach( cps -> {
                    if (cps instanceof MapPropertySource) {
                        properties.putAll(((MapPropertySource) cps).getSource());
                    }
                });
            }
        });
        return properties;
    }
}
