package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.persist.repository.UsuariRepository;
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
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public abstract class EmailHelper<T> {

    @Resource
    protected CacheHelper cacheHelper;
    @Resource
    protected UsuariRepository usuariRepository;
    @Autowired
    protected ConfigHelper configHelper;
    @Autowired
    protected MessageHelper messageHelper;
    @Autowired
    protected JavaMailSender mailSender;

    protected abstract String getMailHtmlBody(T item);
    protected abstract String getMailPlainTextBody(T item);
    protected abstract String getMailSubject();

//    public String sendMail(T item, String email) throws Exception {
//        String resposta = null;
//        try {
//            email = email.replaceAll("\\s+","");
//            sendEmailNotificacio(
//                    email,
//                    item);
//        } catch (Exception ex) {
//            String errorDescripció = "No s'ha pogut avisar per correu electrònic: " + ex;
//            log.error(errorDescripció);
//            resposta = errorDescripció;
//        }
//        return resposta;
//    }
    protected void sendEmailNotificacio(String emailDestinatari, T item) throws MessagingException {
        sendEmailNotificacio(emailDestinatari, item ,null);
    }
    protected void sendEmailNotificacio(String emailDestinatari, T item, List<Attachment> files) throws MessagingException {

        log.debug("Enviament correu notificació");
        var missatge = mailSender.createMimeMessage();
        missatge.setHeader("Content-Type", "text/html charset=UTF-8");
        var helper = new MimeMessageHelper(missatge, true);
        helper.setTo(emailDestinatari);
        helper.setFrom(getRemitent());
        helper.setSubject(configHelper.getPrefix() + " " + getMailSubject());
        //Html text
        helper.setText(getMailPlainTextBody(item), getMailHtmlBody(item));
        if (files != null) {
            for (var attach: files) {
                helper.addAttachment(attach.filename, new ByteArrayResource(attach.content));
            }
        }
        mailSender.send(missatge);
    }

    public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
    public static boolean isEmailValid(String email) {

        if (Strings.isNullOrEmpty(email)) {
            return false;
        }
        try {
            return EMAIL_REGEX.matcher(email).find();
        } catch (Exception e) {
            return false;
        }
    }


    public String getRemitent() {
        return configHelper.getConfig("es.caib.notib.email.remitent");
    }

    public String getEmailFooter() {
        return configHelper.getConfig("es.caib.notib.email.footer");
    }

    @AllArgsConstructor
    protected static class Attachment{
        @NonNull String filename;
        @NonNull byte[] content;
    }
}
