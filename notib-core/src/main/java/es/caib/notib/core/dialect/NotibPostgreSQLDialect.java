/**
 * 
 */
package es.caib.notib.core.dialect;

import org.hibernate.Hibernate;
import org.hibernate.dialect.PostgreSQLDialect;

/**
 * Dialecte de Hibernate per a la base de dades Postgres per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la DGDT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotibPostgreSQLDialect extends PostgreSQLDialect {

	@SuppressWarnings("rawtypes")
	public Class getNativeIdentifierGeneratorClass() {
		return TableNameSequenceGenerator.class;
	}

	public NotibPostgreSQLDialect() {
		super();
		registerFunction("bitand", new PostgresBitwiseAndSQLFunction("bitand", Hibernate.INTEGER));
	}
}
