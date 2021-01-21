package es.caib.notib.war.helper;

import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.security.SimplePrincipal;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.security.Principal;

public class FlushAuthCacheHelper {

    public static void flushAuthenticationCache(String username) throws Exception {
        MBeanServer server = MBeanServerLocator.locateJBoss();
        String jaasMgrName = "jboss.security:service=JaasSecurityManager";
        ObjectName jaasMgr = new ObjectName(jaasMgrName);
        String domainName = "seycon";
        Principal user = new SimplePrincipal(username);
        Object[] params = { domainName, user };
        String[] signature = { String.class.getName(), Principal.class.getName() };
        server.invoke(jaasMgr, "flushAuthenticationCache", params, signature);
    }
}
