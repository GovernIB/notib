/**
 * 
 */
package es.caib.notib.persist.dialect;

import es.caib.notib.persist.audit.AbstractAuditableEntity;
import org.hibernate.Hibernate;
import org.hibernate.dialect.PostgreSQL9Dialect;

/**
 * Dialecte de Hibernate per a la base de dades Postgres per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PostgreSqlCaibDialect extends PostgreSQL9Dialect {

	public PostgreSqlCaibDialect() {
		super();
		registerFunction("bitand", new PostgresBitwiseAndSQLFunction("bitand", Hibernate.INTEGER));
	}

	@Override
	public String getSelectSequenceNextValString(String sequenceName) {
		if (sequenceName.equalsIgnoreCase("hibernate_sequence")) {
			return "nextval ('" + AbstractAuditableEntity.TABLE_PREFIX + "_" + sequenceName + "')";
		} else {
			return "nextval ('" + sequenceName + "')";
		}
	}

	@Override
	public String getCreateSequenceString(String sequenceName) {
		//starts with 1, implicitly
		if (sequenceName.equalsIgnoreCase("hibernate_sequence")) {
			return "create sequence " + AbstractAuditableEntity.TABLE_PREFIX + "_" + sequenceName;
		} else {
			return "create sequence " + sequenceName;
		}
	}

	@Override
	public String getDropSequenceString(String sequenceName) {
		if (sequenceName.equalsIgnoreCase("hibernate_sequence")) {
			return "drop sequence " + AbstractAuditableEntity.TABLE_PREFIX + "_" + sequenceName;
		} else {
			return "drop sequence " + sequenceName;
		}
	}

}
