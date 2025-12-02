package es.caib.notib.logic.config;

import es.caib.notib.logic.base.config.BaseAclConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.model.AclCache;

/**
 * Configuració de les ACLs de Spring Security.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class AclConfig extends BaseAclConfig {

	@Override
	public String getDbTablePrefix() {
		return BaseConfig.DB_PREFIX;
	}

	@Override
	protected String getTableSequenceSuffix() {
		return "_seq";
	}

	@Override
	protected boolean isOracleSequenceLegacy() {
		return true;
	}

}
