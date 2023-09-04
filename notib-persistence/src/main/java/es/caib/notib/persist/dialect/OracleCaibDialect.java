/**
 * 
 */
package es.caib.notib.persist.dialect;

import es.caib.notib.persist.audit.AbstractAuditableEntity;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.IntegerType;

/**
 * Dialecte de Hibernate per a la base de dades Oracle per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OracleCaibDialect extends Oracle10gDialect {

	private static final String HIBERNATE_SEQ = "hibernate_seq";

	public OracleCaibDialect() {
		super();
		registerFunction("bitand", new OracleBitwiseAndSQLFunction("bitand", IntegerType.INSTANCE));
	}

	@Override
	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName.equalsIgnoreCase("hibernate_sequence")
				? AbstractAuditableEntity.TABLE_PREFIX + "_" + HIBERNATE_SEQ + ".nextval"
				:sequenceName + ".nextval";
	}

	@Override
	public String getCreateSequenceString(String sequenceName) {
		//starts with 1, implicitly
		return sequenceName.equalsIgnoreCase("hibernate_sequence")
				? "create sequence " + AbstractAuditableEntity.TABLE_PREFIX + "_" + HIBERNATE_SEQ
				: "create sequence " + sequenceName;
	}

	@Override
	public String getDropSequenceString(String sequenceName) {
		return sequenceName.equalsIgnoreCase("hibernate_sequence")
			 	? "drop sequence " + AbstractAuditableEntity.TABLE_PREFIX + "_" + HIBERNATE_SEQ
				: "drop sequence " + sequenceName;
	}

}