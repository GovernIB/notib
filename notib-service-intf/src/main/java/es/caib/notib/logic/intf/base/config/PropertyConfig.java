package es.caib.notib.logic.intf.base.config;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Propietats de configuració de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
public class PropertyConfig {

	public static final String PROPERTY_PREFIX = BaseConfig.BASE_PACKAGE + ".";
    public static final String PROPERTY_PREFIX_FRONT = PROPERTY_PREFIX + "front.";

	public static final String PROP_BACKEND_HTTP_HEADER_ANSWERS = PROPERTY_PREFIX + "http.header.answers";
	public static final String PROP_PERSIST_DEFAULT_AUDITOR = PROPERTY_PREFIX + "persist.default.auditor";
	public static final String PROP_PERSIST_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PROP_PERSIST_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PERSISTENCE_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

    public static final String PROP_FRONT_API_URL = PROPERTY_PREFIX_FRONT + "api.url";

    public static final Map<String, String> REACT_APP_PROPS_MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(PROP_FRONT_API_URL, "REACT_APP_API_URL"));

    public static final Map<String, String> VITE_PROPS_MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(PROP_FRONT_API_URL, "VITE_API_URL"));
}
