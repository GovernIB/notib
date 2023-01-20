package es.caib.notib.persist.dialect;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

import java.util.List;

public class PostgresBitwiseAndSQLFunction extends StandardSQLFunction {

    public PostgresBitwiseAndSQLFunction(String name) {
        super(name);
    }

    public PostgresBitwiseAndSQLFunction(String name, Type registeredType) {
        super(name, registeredType);
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
        if (arguments.size() != 2){
            throw new IllegalArgumentException("the function must be passed 2 arguments");
        }

        StringBuffer buf = new StringBuffer(arguments.get(0).toString());
        buf.append(" & ");
        buf.append(arguments.get(1).toString());

        return buf.toString();
    }
}
