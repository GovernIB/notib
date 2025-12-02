package es.caib.notib.logic.intf.base.config;

/**
 * Propietats de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
public class BaseConfig {

	public static final String APP_NAME = "notib";
	public static final String DB_PREFIX = "not_";
	public static final String DEFAULT_LOCALE = "ca";

	public static final String BASE_PACKAGE = "es.caib." + APP_NAME;
	public static final String PROPERTY_PREFIX = BASE_PACKAGE + ".";

	public static final String APP_PROPERTIES = BASE_PACKAGE + ".properties";
	public static final String APP_SYSTEM_PROPERTIES = BASE_PACKAGE + ".system.properties";

	public static final String ROLE_SUPER = "NOT_SUPER";
	public static final String ROLE_ADMIN = "NOT_ADMIN";
	public static final String ROLE_CARPETA = "NOT_CARPETA";
	public static final String ROLE_APL = "NOT_APL";
	public static final String ROLE_TOTHOM = "tothom";

	public static final String REACT_APP_PATH="/reactapp";
	public static final String API_PATH = "/apinew";
	public static final String PING_PATH = "/ping";
	public static final String AUTH_TOKEN_PATH = "/authToken";
	public static final String SYSENV_PATH = "/sysenv";
	public static final String MANIFEST_PATH = "/manifest";

	public static final String PROP_FILES = PROPERTY_PREFIX + "fitxers";
	public static final String PROP_SECURITY_MAPPABLE_ROLES = PROPERTY_PREFIX + "security.mappableRoles";
	public static final String PROP_SECURITY_ROLE_HTTP_HEADER = PROPERTY_PREFIX + "security.selected.role.http.header";
	public static final String PROP_SECURITY_NAME_ATTRIBUTE_KEY = PROPERTY_PREFIX + "security.nameAttributeKey";


}
