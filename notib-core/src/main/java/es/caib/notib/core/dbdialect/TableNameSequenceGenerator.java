package es.caib.notib.core.dbdialect;

import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

public class TableNameSequenceGenerator extends SequenceGenerator {

    public static final String CUSTOM_SEQUENCE_NAME = "NOT_HIBERNATE_SEQ";

    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        if(params.getProperty(SEQUENCE) == null || params.getProperty(SEQUENCE).length() == 0) {
            String seqName = CUSTOM_SEQUENCE_NAME;
            params.setProperty(SEQUENCE, seqName);               
        }
        super.configure(type, params, dialect);
    }

}