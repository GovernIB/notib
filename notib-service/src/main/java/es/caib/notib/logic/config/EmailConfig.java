package es.caib.notib.logic.config;

import ch.qos.logback.core.util.JNDIUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//@Configuration
public class EmailConfig {

    @Value("${es.caib.notib.email.jndi:mail/NotibSession}")
    private String emailJndiName;

    @Bean
    public JavaMailSender getMailSender() throws NamingException {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        ((JavaMailSenderImpl)mailSender).setSession(getMailSession());
        return mailSender;
    }

    private Session getMailSession() throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        return (Session) envCtx.lookup(emailJndiName);
    }
}
