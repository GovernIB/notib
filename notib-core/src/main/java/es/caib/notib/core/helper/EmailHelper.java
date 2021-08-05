package es.caib.notib.core.helper;

import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.UsuariRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Slf4j
@Component
public abstract class EmailHelper<T> {
    private static final String PREFIX_NOTIB = "[NOTIB]";
    @Resource
    protected ProcedimentHelper procedimentHelper;
    @Resource
    protected CacheHelper cacheHelper;
    @Resource
    protected UsuariRepository usuariRepository;
    @Resource
    protected GrupRepository grupRepository;
    @Resource
    protected GrupProcedimentRepository grupProcedimentRepository;
    @Autowired
    protected ConfigHelper configHelper;
    @Resource
    private JavaMailSender mailSender;

    protected abstract String getMailHtmlBody(T item);
    protected abstract String getMailPlainTextBody(T item);
    protected abstract String getMailSubject();

    public String sendMail(T item, String email) throws Exception {
        String resposta = null;
        try {
            email = email.replaceAll("\\s+","");
            sendEmailNotificacio(
                    email,
                    item);
        } catch (Exception ex) {
            String errorDescripció = "No s'ha pogut avisar per correu electrònic: " + ex;
            log.error(errorDescripció);
            resposta = errorDescripció;
        }
        return resposta;
    }
    protected void sendEmailNotificacio(
            String emailDestinatari, T item) throws MessagingException {
        sendEmailNotificacio(emailDestinatari, item ,null);
    }
    protected void sendEmailNotificacio(
            String emailDestinatari, T item, List<Attachment> files) throws MessagingException {
        log.debug("Enviament emails notificació");

        MimeMessage missatge = mailSender.createMimeMessage();
        missatge.setHeader("Content-Type", "text/html charset=UTF-8");
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(missatge, true);
        helper.setTo(emailDestinatari);
        helper.setFrom(getRemitent());
        helper.setSubject(PREFIX_NOTIB + " " + getMailSubject());

        //Html text
        helper.setText(getMailPlainTextBody(item), getMailHtmlBody(item));

        if (files != null) {
            for (Attachment attach: files) {
                helper.addAttachment(attach.filename, new ByteArrayResource(attach.content));
            }
        }
        mailSender.send(missatge);
    }

    public String getRemitent() {
        return configHelper.getConfig("es.caib.notib.email.remitent");
    }

    public String getEmailFooter() {
        return configHelper.getConfig("es.caib.notib.email.footer");
    }

    @AllArgsConstructor
    protected class Attachment{
        @NonNull String filename;
        @NonNull byte[] content;
    }
}
