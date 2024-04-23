package es.caib.notib.persist.dialect;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

import java.util.List;

public class OracleBitwiseAndSQLFunction extends StandardSQLFunction {

    public OracleBitwiseAndSQLFunction(String name) {
        super(name);
    }

    public OracleBitwiseAndSQLFunction(String name, Type registeredType) {
        super(name, registeredType);
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {

        if (arguments.size() != 2){
            throw new IllegalArgumentException("the function must be passed 2 arguments");
        }
        var buf = new StringBuffer("BITAND(");
        buf.append(arguments.get(0).toString());
        buf.append(", ");
        buf.append(arguments.get(1).toString());
        buf.append(")");

        return buf.toString();
    }
}