/**
 * 
 */
package es.caib.notib.plugin;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Utilitat per accedir a les entrades del fitxer de properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class PropertiesHelper extends Properties {

	private static final String APPSERV_PROPS_PATH = "es.caib.notib.properties.path";

	private static PropertiesHelper instance = null;

	private PropertiesHelper(Properties defaults) {
		super(defaults);
	}

	public static PropertiesHelper getProperties() {
		return getProperties(null);
	}
	public static PropertiesHelper getProperties(String path) {
		String propertiesPath = path;
		if (propertiesPath == null) {
			propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
		}
		if (instance == null) {
			instance = new PropertiesHelper(System.getProperties());
			if (propertiesPath != null) {
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
			}
		}
		return instance;
	}

	public String getProperty(String key) {
		return super.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
        return (val == null) ? defaultValue : val;
	}

	public boolean getAsBoolean(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}
	public int getAsInt(String key) {
		return new Integer(getProperty(key));
	}
	public long getAsLong(String key) {
		return new Long(getProperty(key));
	}
	public float getAsFloat(String key) {
		return new Float(getProperty(key));
	}
	public double getAsDouble(String key) {
		return new Double(getProperty(key));
	}

	public Properties findAll() {
		return findByPrefix(null);
	}
	public Properties findByPrefix(String prefix) {
		Properties properties = new Properties();
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
		return properties;
	}

	private static final long serialVersionUID = 1L;

}
