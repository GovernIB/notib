package es.caib.notib.core.helper;

import es.caib.notib.core.api.exception.NotDefinedConfigException;
import es.caib.notib.core.entity.config.ConfigEntity;
import es.caib.notib.core.entity.config.ConfigGroupEntity;
import es.caib.notib.core.repository.config.ConfigGroupRepository;
import es.caib.notib.core.repository.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class ConfigHelper {

    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigGroupRepository configGroupRepository;

    public String getConfig(String key) throws NotDefinedConfigException {
        ConfigEntity configEntity = configRepository.findOne(key);
        if (configEntity == null) {
            throw new NotDefinedConfigException(key);
        }

        if (configEntity.isJbossProperty()) {
            // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
            return JBossPropertiesHelper.getProperties().getProperty(configEntity.getKey(), configEntity.getValue());
        }

        return configEntity.getValue();
    }

    public Map<String, String> getGroupProperties(String codeGroup) {
        Map<String, String> properties = new HashMap<>();
        ConfigGroupEntity configGroup = configGroupRepository.findOne(codeGroup);
        fillGroupProperties(configGroup, properties);
        return properties;
    }

    public void fillGroupProperties(ConfigGroupEntity configGroup, Map<String, String> outProperties) {
        for (ConfigEntity config : configGroup.getConfigs()) {
            outProperties.put(config.getKey(), config.getValue());
        }

        for (ConfigGroupEntity child : configGroup.getInnerConfigs()) {
            fillGroupProperties(child, outProperties);
        }
    }

    public boolean getAsBoolean(String key) {
        return new Boolean(getConfig(key));
    }
    public int getAsInt(String key) {
        return new Integer(getConfig(key));
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
            if (instance == null) {
                instance = new JBossPropertiesHelper();
                if (propertiesPath != null) {
                    instance.llegirSystem = false;
                    log.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
                    try {
                        if (propertiesPath.startsWith("classpath:")) {
                            instance.load(
                                    JBossPropertiesHelper.class.getClassLoader().getResourceAsStream(
                                            propertiesPath.substring("classpath:".length())));
                        } else if (propertiesPath.startsWith("file://")) {
                            FileInputStream fis = new FileInputStream(
                                    propertiesPath.substring("file://".length()));
                            instance.load(fis);
                        } else {
                            FileInputStream fis = new FileInputStream(propertiesPath);
                            instance.load(fis);
                        }
                    } catch (Exception ex) {
                        log.error("No s'han pogut llegir els properties", ex);
                    }
                }
            }
            return instance;
        }
//
//	public String getProperty(String key) {
//		if (llegirSystem)
//			return System.getProperty(key);
//		else
//			return super.getProperty(key);
//	}
//	public String getProperty(String key, String defaultValue) {
//		String val = getProperty(key);
//        return (val == null) ? defaultValue : val;
//	}
//
//	public boolean getAsBoolean(String key) {
//		return new Boolean(getProperty(key)).booleanValue();
//	}
//	public boolean getAsBoolean(String key, boolean defaultValue) {
//		return new Boolean(getProperty(key, Boolean.toString(defaultValue))).booleanValue();
//	}
//	public int getAsInt(String key) {
//		return new Integer(getProperty(key)).intValue();
//	}
//	public int getAsInt(String key, int defaultValue) {
//		return new Integer(getProperty(key, Integer.toString(defaultValue))).intValue();
//	}
//	public long getAsLong(String key) {
//		return new Long(getProperty(key)).longValue();
//	}
//	public long getAsLong(String key, long defaultValue) {
//		return new Long(getProperty(key, Long.toString(defaultValue))).longValue();
//	}
//	public float getAsFloat(String key) {
//		return new Float(getProperty(key)).floatValue();
//	}
//	public float getAsFloat(String key, float defaultValue) {
//		return new Float(getProperty(key, Float.toString(defaultValue))).floatValue();
//	}
//	public double getAsDouble(String key, double defaultValue) {
//		return new Double(getProperty(key, Double.toString(defaultValue))).doubleValue();
//	}

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
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }
    }
}
