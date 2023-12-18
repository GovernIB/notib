package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.exception.NotDefinedConfigException;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.persist.entity.config.ConfigEntity;
import es.caib.notib.persist.entity.config.ConfigGroupEntity;
import es.caib.notib.persist.repository.config.ConfigGroupRepository;
import es.caib.notib.persist.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static es.caib.notib.logic.config.ReadDbPropertiesPostProcessor.DBAPP_PROPERTIES;

@Component
@Slf4j
@PropertySource(ignoreResourceNotFound = true, value = {
        "file://${" + ConfigService.APP_PROPERTIES + "}",
        "file://${" + ConfigService.APP_SYSTEM_PROPERTIES + "}"})
public class ConfigHelper {

    // TODO REPASSAR QUE NO FALTI CAP @Transactional(readOnly = true) EN ALGUN DELS MÈTODES PER OBTENIR PROPIETATS

    @Autowired
    private ConfigurableEnvironment environment;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigGroupRepository configGroupRepository;


    private static ThreadLocal<String> entitatCodi = new ThreadLocal<>();

    public static ThreadLocal<String> getEntitatCodi() {
        return entitatCodi;
    }

    public static void setEntitatCodi(String entitatCodi) {
        ConfigHelper.entitatCodi.set(entitatCodi);
    }

    public String getEntitatActualCodi() {
        return entitatCodi.get();
    }

    public String getConfigGlobal(String propietatGlobal) {
        return environment.getProperty(propietatGlobal);
    }
    public String getConfigGlobal(String propietatGlobal, String defaultValue) {
        return getPropietatGlobal(propietatGlobal).orElse(defaultValue);
    }
    public String getConfigByEntitat(String entitatCodi, String propietatGlobal) {
        return getPropietat(entitatCodi, propietatGlobal).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    public String getConfig(String propietatGlobal)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).orElse(null);
    }

    public String getConfig(String propietatGlobal, String defaultValue)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public Long getConfigAsLong(String propietatGlobal)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).map(Long::parseLong).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    @Transactional(readOnly = true)
    public Long getConfigAsLong(String propietatGlobal, Long defaultValue)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).map(Long::parseLong).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public Integer getConfigAsInteger(String propietatGlobal)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).map(Integer::parseInt).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    @Transactional(readOnly = true)
    public Integer getConfigAsInteger(String propietatGlobal, Integer defaultValue)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).map(Integer::parseInt).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public Boolean getConfigAsBoolean(String propietatGlobal)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).map(Boolean::parseBoolean).orElseThrow(() -> new NotDefinedConfigException(propietatGlobal));
    }

    @Transactional(readOnly = true)
    public Boolean getConfigAsBoolean(String propietatGlobal, Boolean defaultValue)  {
        return getPropietat(getEntitatActualCodi(), propietatGlobal).map(Boolean::parseBoolean).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public String getPrefix() {
        var prefix = getConfig(PropertiesConstants.CODI_ENTORN);
        return "[" + (!Strings.isNullOrEmpty(prefix) ? prefix : "NOTIB") + "]";
    }

    @Transactional(readOnly = true)
    public Integer getMaxReintentsRegistre() {
        return getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.reintents.maxim");
    }

    @Transactional(readOnly = true)
    public Integer getMaxReintentsNotifca() {
        return getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.reintents.maxim");
    }
    @Transactional(readOnly = true)
    public Integer getMaxReintentsConsultaNotifica() {
        return getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim");
    }

    @Transactional(readOnly = true)
    public Integer getMaxReintentsConsultaSir() {
        return getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim");
    }

    private Optional<String> getPropietatGlobal(String globalKey) {
        var propertyValue = environment.getProperty(globalKey);
        return propertyValue != null ? Optional.of(propertyValue) : Optional.empty();
    }

    private Optional<String> getPropietat(String entitatCodi, String globalKey) {

        String propertyValue = null;
        String entitatKey = null;
        if (!Strings.isNullOrEmpty(entitatCodi)) {
            entitatKey = crearEntitatKey(entitatCodi, globalKey);
            propertyValue = environment.getProperty(entitatKey);
            if (!Strings.isNullOrEmpty(propertyValue)) {
                return Optional.of(propertyValue);
            }
        }
        propertyValue = environment.getProperty(globalKey);
        if (propertyValue != null) {
            return Optional.of(propertyValue);
        }
        log.error("No s'ha trobat la propietat -> key global: " + globalKey + " key entitat: " + entitatKey);
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Map<String, String> getGroupProperties(String codeGroup) {

        Map<String, String> properties = new HashMap<>();
        var configGroup = configGroupRepository.findById(codeGroup).orElse(null);
        fillGroupProperties(configGroup, properties);
        return properties;
    }

    private void fillGroupProperties(ConfigGroupEntity configGroup, Map<String, String> outProperties) {

        if (configGroup == null) {
            return;
        }
        for (var config : configGroup.getConfigs()) {
            outProperties.put(config.getKey(), getConfig(config.getKey()));
        }

        if (configGroup.getInnerConfigs() == null) {
            return;
        }
        for (var child : configGroup.getInnerConfigs()) {
            fillGroupProperties(child, outProperties);
        }
    }

    @Transactional(readOnly = true)
    public Properties getAllEntityProperties(String entitatCodi) {

        var properties = new Properties();
        var configs = configRepository.findByEntitatCodiIsNull();
        for (var config: configs) {
            try {
                var value = !Strings.isNullOrEmpty(entitatCodi) ? getConfigByEntitat(entitatCodi, config.getKey()) : getConfig(config.getKey());
                if (value == null) {
                    continue;
                }
                properties.put(config.getKey(), value);
            } catch (NotDefinedConfigException dcex) {
                log.warn("Propietat '" + config.getKey() + "' no definida.");
            }
        }
        return properties;
    }

    public void crearConfigsEntitat(String codiEntitat) {

        var dto = new ConfigDto();
        dto.setEntitatCodi(codiEntitat);
        ConfigEntity nova;
        List<ConfigEntity> confs = new ArrayList<>();
        var configs = configRepository.findByEntitatCodiIsNullAndConfigurableIsTrue();
        String key;
        for (var config : configs) {
            dto.setKey(config.getKey());
            key = dto.crearEntitatKey();
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

        if (Strings.isNullOrEmpty(entitatCodi) || Strings.isNullOrEmpty(key)) {
            var msg = "Codi entitat " + entitatCodi + " i/o key " + key + " no contenen valor";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        var split = key.split(ConfigDto.prefix);
        if (split == null) {
            var msg = "Format no reconegut per la key: " + key;
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return split.length < 2 ? split.length == 0 ? null : split[0] : (ConfigDto.prefix + "." + entitatCodi + split[1]);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void schedullingLoadDbProperties() {
        reloadDbProperties();
    }

    public void reloadDbProperties() {

        Map<String, Object> propertySource = new HashMap<>();
        var dbProperties = configRepository.findDbProperties();
        dbProperties.forEach(p -> propertySource.put(p.getKey(), p.getValue()));
        if (environment.getPropertySources().contains(DBAPP_PROPERTIES)) {
            environment.getPropertySources().replace(DBAPP_PROPERTIES, new MapPropertySource(DBAPP_PROPERTIES, propertySource));
            return;
        }
        environment.getPropertySources().addFirst(new MapPropertySource(DBAPP_PROPERTIES, propertySource));
    }

    public Map<String, Object> getEnvironmentPropertiesMap() {

        Map<String, Object> propertiesMap = new HashMap<>();
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
                properties.putAll(((MapPropertySource) ps).getSource().entrySet().stream().filter(x -> x.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
            if (ps instanceof CompositePropertySource) {
                ((CompositePropertySource) ps).getPropertySources().stream().forEach( cps -> {
                    if (cps instanceof MapPropertySource) {
                        properties.putAll(((MapPropertySource) cps).getSource().entrySet().stream().filter(x -> x.getValue() != null)
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    }
                });
            }
        });
        return properties;
    }
}
