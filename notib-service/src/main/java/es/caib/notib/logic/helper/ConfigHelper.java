package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.exception.NotDefinedConfigException;
import es.caib.notib.persist.entity.config.ConfigEntity;
import es.caib.notib.persist.entity.config.ConfigGroupEntity;
import es.caib.notib.persist.repository.config.ConfigGroupRepository;
import es.caib.notib.persist.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
public class ConfigHelper {

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

    @Transactional(readOnly = true)
    public String getEntitatActualCodi() {

        return entitat != null && entitat.get() != null ? entitat.get().getCodi() : null;
    }

    @Transactional(readOnly = true)
    public String getConfigKeyByEntitat(String entitatCodi, String property) {

        String key = crearEntitatKey(entitatCodi, property);
        ConfigEntity configEntity = configRepository.findById(key).orElse(null);
        if (configEntity != null && (configEntity.isJbossProperty() && configEntity.getValue() == null || configEntity.getValue() != null)) {
            String config = getConfig(configEntity);
            if (!Strings.isNullOrEmpty(config)) {
                return config;
            }
        }
        configEntity = configRepository.findById(property).orElse(null);
        if (configEntity != null) {
            return getConfig(configEntity);
        }
        log.error("No s'ha trobat la propietat -> key global: " + property + " key entitat: " + key);
        throw new NotDefinedConfigException(property);
    }

    @Transactional(readOnly = true)
    public String getConfig(String keyGeneral)  {

        String entitatCodi  = getEntitatActualCodi();
        String value = null;
        ConfigEntity configEntity = configRepository.findById(keyGeneral).orElse(null);
        if (configEntity == null) {
            return getJBossProperty(keyGeneral);
        }
        // Propietat trobada en db
        if (configEntity.isConfigurable() && !Strings.isNullOrEmpty(entitatCodi)) {
            // Propietat a nivell d'entitat
            String keyEntitat = crearEntitatKey(entitatCodi, keyGeneral);
            ConfigEntity configEntitatEntity = configRepository.findById(keyEntitat).orElse(null);
            if (configEntitatEntity != null) {
                value = getConfig(configEntitatEntity);
            }
        }
        if (value == null) {
            // Propietat global
            value = getConfig(configEntity);
        }
        return value;
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
            outProperties.put(config.getKey(), getConfig(config));
        }

        if (configGroup.getInnerConfigs() != null) {
            for (ConfigGroupEntity child : configGroup.getInnerConfigs()) {
                fillGroupProperties(child, outProperties);
            }
        }
    }

    public boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }

    public int getAsInt(String key) {
        return new Integer(getConfig(key));
    }

    public long getAsLongByEntitat(String key) {
        return new Long(getConfig(key));
    }

    public long getAsLong(String key) {
        return new Long(getConfig(key));
    }
    public float getAsFloat(String key) {
        return new Float(getConfig(key));
    }

    public String getJBossProperty(String key) {
        return JBossPropertiesHelper.getProperties().getProperty(key);
    }
    public String getJBossProperty(String key, String defaultValue) {
        return JBossPropertiesHelper.getProperties().getProperty(key, defaultValue);
    }

    @Transactional(readOnly = true)
    public Properties getAllEntityProperties(String entitatCodi) {
        Properties properties = new Properties();
//        List<ConfigEntity> configs = !Strings.isNullOrEmpty(entitatCodi) ? configRepository.findConfigEntitaCodiAndGlobals(entitatCodi) : configRepository.findByEntitatCodiIsNull();
        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNull();
        for (ConfigEntity config: configs) {
             String value = !Strings.isNullOrEmpty(entitatCodi) ? getConfigKeyByEntitat(entitatCodi, config.getKey()) : getConfig(config);
            if (value != null) {
                properties.put(config.getKey(), value);
            }
        }
        return properties;
    }

    private String getConfig(ConfigEntity configEntity) throws NotDefinedConfigException {
        if (configEntity.isJbossProperty()) {
            // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
            return getJBossProperty(configEntity.getKey(), configEntity.getValue());
        }
        return configEntity.getValue();
    }

    @Slf4j
    public static class JBossPropertiesHelper extends Properties {

        private static final String APPSERV_PROPS_PATH = "es.caib.notib.properties.path";

        private static JBossPropertiesHelper instance = null;

        private boolean llegirSystem = true;

        public static JBossPropertiesHelper getProperties() {
            return getProperties(null);
        }
        public static JBossPropertiesHelper getProperties(String path) {

            String propertiesPath = path;
            if (propertiesPath == null) {
                propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
            }
            if (instance != null) {
                return instance;
            }
            instance = new JBossPropertiesHelper();
            if (propertiesPath == null) {
                return instance;
            }
            instance.llegirSystem = false;
            log.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
            try {
                if (propertiesPath.startsWith("classpath:")) {
                    instance.load(JBossPropertiesHelper.class.getClassLoader().getResourceAsStream(propertiesPath.substring("classpath:".length())));
                } else if (propertiesPath.startsWith("file://")) {
                    FileInputStream fis = new FileInputStream(propertiesPath.substring("file://".length()));
                    instance.load(fis);
                } else {
                    FileInputStream fis = new FileInputStream(propertiesPath);
                    instance.load(fis);
                }
            } catch (Exception ex) {
                log.error("No s'han pogut llegir els properties", ex);
            }
            return instance;
        }

        public String getProperty(String key) {
            return llegirSystem ? System.getProperty(key) : super.getProperty(key);
        }
        public String getProperty(String key, String defaultValue) {
            String val = getProperty(key);
            return (val == null) ? defaultValue : val;
        }

        public boolean isLlegirSystem() {
            return llegirSystem;
        }
        public void setLlegirSystem(boolean llegirSystem) {
            this.llegirSystem = llegirSystem;
        }


        public Properties findAll() {
            return findByPrefixProperties(null);
        }

        public Map<String, String> findByPrefix(String prefix) {
            Map<String, String> properties = new HashMap<String, String>();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }

        public Properties findByPrefixProperties(String prefix) {
            Properties properties = new Properties();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }
    }

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
    }

    public void deleteConfigEntitat(String codiEntitat) {
        configRepository.deleteByEntitatCodi(codiEntitat);
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
}
