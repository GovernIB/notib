package es.caib.notib.logic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.naming.NamingException;

@Profile("!boot")
@Configuration
public class EmailConfig {

    @Value("${es.caib.notib.email.jndi:java:jboss/mail/NotibSession}")
    private String emailJndiName;

    @Bean
    public JavaMailSender mailSender(Session session) throws NamingException {

        var mailSender = new JavaMailSenderImpl();
        mailSender.setSession(session);
        return mailSender;
    }

    @Bean
    public Session session() {

//        var initCtx = new InitialContext();
//        var envCtx = (Context) initCtx.lookup("java:comp/env");
//        return (Session) envCtx.lookup(emailJndiName);
        try {
            return JndiLocatorDelegate.createDefaultResourceRefLocator().lookup(emailJndiName, Session.class);
        } catch (NamingException ex) {
            throw new IllegalStateException(String.format("Unable to find Session in JNDI location %s", emailJndiName), ex);
        }
    }
}
