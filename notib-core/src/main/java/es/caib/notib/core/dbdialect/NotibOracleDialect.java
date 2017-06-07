package es.caib.notib.core.dbdialect;

import org.hibernate.dialect.Oracle9iDialect;

public class NotibOracleDialect extends Oracle9iDialect {
    public Class getNativeIdentifierGeneratorClass() {
        return TableNameSequenceGenerator.class;
    }
}
