/**
 * 
 */
package es.caib.notib.persist.dialect;

import es.caib.notib.persist.audit.AbstractAuditableEntity;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

/**
 * Dialecte de Hibernate per a la base de dades Oracle per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OracleCaibDialect extends Oracle10gDialect {

	private static final String HIBERNATE_SEQ = "hibernate_seq";
	/** Funció per ordenar text alfabèticament amb independència de l'idioma de la sessió (vegeu ORDRE_TEXT_SQL). */
	public static final String FUNCIO_ORDRE_TEXT = "ordre_text";
	/**
	 * Ordenació alfabètica multilingüe (ISO 14651), igual sigui quin sigui l'NLS_SORT de la sessió (que el driver
	 * JDBC pren de l'idioma de la JVM). Ha de coincidir exactament amb l'expressió dels índexs d'ordenació de
	 * not_notificacio_table perquè Oracle els pugui fer servir.
	 */
	public static final String ORDRE_TEXT_SQL = "NLSSORT(?1, 'NLS_SORT=GENERIC_M')";

	public OracleCaibDialect() {
		super();
		registerFunction("bitand", new OracleBitwiseAndSQLFunction("bitand", IntegerType.INSTANCE));
		registerFunction(FUNCIO_ORDRE_TEXT, new SQLFunctionTemplate(StringType.INSTANCE, ORDRE_TEXT_SQL));
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
