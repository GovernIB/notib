package es.caib.notib.core.helper;

import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.repository.GrupProcSerRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public abstract class EmailHelper<T> {
    private static final String PREFIX_NOTIB = "[NOTIB]";

    @Resource
    protected CacheHelper cacheHelper;
    @Resource
    protected UsuariRepository usuariRepository;
    @Autowired
    protected ConfigHelper configHelper;
    @Resource
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
    protected void sendEmailNotificacio(
            String emailDestinatari, T item) throws MessagingException {
        sendEmailNotificacio(emailDestinatari, item ,null);
    }
    protected void sendEmailNotificacio(
            String emailDestinatari, T item, List<Attachment> files) throws MessagingException {
        log.debug("Enviament correu notificació");

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

    public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
    public static boolean isEmailValid(String email) {

        if (Strings.isNullOrEmpty(email)) {
            return false;
        }
        try {
            Matcher matcher = EMAIL_REGEX.matcher(email);
            return matcher.find();
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
