/**
 * 
 */
package es.caib.notib.back.helper;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utilitat per accedir a les entrades del fitxer de properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class PropertiesHelper extends Properties {

	private static final String APPSERV_PROPS_PATH = "es.caib.notib.properties.path";

	private static PropertiesHelper instance = null;

	private boolean llegirSystem = true;



	public static PropertiesHelper getProperties() {
		return getProperties(null);
	}
	public static PropertiesHelper getProperties(String path) {

		String propertiesPath = path;
		if (propertiesPath == null) {
			propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
		}
		if (instance != null) {
			return instance;
		}
		if (propertiesPath == null) {
			return new PropertiesHelper();
		}
		instance = new PropertiesHelper();
		instance.llegirSystem = false;
		log.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
		try {
			if (propertiesPath.startsWith("classpath:")) {
				instance.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(propertiesPath.substring("classpath:".length())));
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
		var val = getProperty(key);
        return (val == null) ? defaultValue : val;
	}

	public boolean getAsBoolean(String key) {
		return new Boolean(getProperty(key)).booleanValue();
	}
	public boolean getAsBoolean(String key, boolean defaultValue) {
		return new Boolean(getProperty(key, Boolean.toString(defaultValue))).booleanValue();
	}
	public int getAsInt(String key) {
		return new Integer(getProperty(key)).intValue();
	}
	public int getAsInt(String key, int defaultValue) {
		return new Integer(getProperty(key, Integer.toString(defaultValue))).intValue();
	}
	public long getAsLong(String key) {
		return new Long(getProperty(key)).longValue();
	}
	public long getAsLong(String key, long defaultValue) {
		return new Long(getProperty(key, Long.toString(defaultValue))).longValue();
	}
	public float getAsFloat(String key) {
		return new Float(getProperty(key)).floatValue();
	}
	public float getAsFloat(String key, float defaultValue) {
		return new Float(getProperty(key, Float.toString(defaultValue))).floatValue();
	}
	public double getAsDouble(String key, double defaultValue) {
		return new Double(getProperty(key, Double.toString(defaultValue))).doubleValue();
	}

	public Map<String, String> findByPrefix(String prefix) {

		Map<String, String> properties = new HashMap<String, String>();
		if (llegirSystem) {
			for (Object key: System.getProperties().keySet()) {
				if (key instanceof String) {
					String keystr = (String)key;
					if (keystr.startsWith(prefix)) {
						properties.put(keystr, System.getProperty(keystr));
					}
				}
			}
			return properties;
		}
		for (Object key: this.keySet()) {
			if (key instanceof String) {
				String keystr = (String)key;
				if (keystr.startsWith(prefix)) {
					properties.put(keystr, getProperty(keystr));
				}
			}
		}
		return properties;
	}

	private static final long serialVersionUID = 1L;

}
