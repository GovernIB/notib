/**
 * 
 */
package es.caib.notib.core.dialect;

import org.hibernate.dialect.Oracle9iDialect;

/**
 * Dialecte de Hibernate per a la base de dades Oracle per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la DGDT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotibOracleDialect extends Oracle9iDialect {

	@SuppressWarnings("rawtypes")
	public Class getNativeIdentifierGeneratorClass() {
		return TableNameSequenceGenerator.class;
	}

}
