package es.caib.notib.logic.intf.util;

import com.google.common.io.Files;
import es.caib.notib.logic.intf.dto.mime.Mimes;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

@Slf4j
public class MimeUtils {

    private MimeUtils() {
        throw new IllegalStateException("MimeUtils no pot ser instanciat");
    }

    private static final String ZIP_SIGNED = "application/pkcs7-signature";
    private static final String[] formatsValidsNotCom = {"JVBERi0","UEsDB"}; //PDF / ZIP

    public static boolean isPDF(String docBase64) {
        return docBase64.startsWith(formatsValidsNotCom[0]);
    }

    public static boolean isZIP(String docBase64) {
        return docBase64.startsWith(formatsValidsNotCom[1]);
    }

    public static String getMimeTypeFromContingut(String arxiuNom, byte[] contingut) {
        try {
            var suffix = "";
            return getMimeType(arxiuNom, contingut, suffix);
        } catch (IOException ex) {
            String err = "Error obtenint el tipus MIME del document " + arxiuNom + ex.getMessage();
            log.error(err, ex);
            return null;
        }
    }

    public static String getMimeTypeFromContingut(String arxiuNom, String base64) {

        try {
            return getMimeTypeFromBase64(base64, arxiuNom);
        } catch (IOException ex) {
            String err = "Error obtenint el tipus MIME del document " + arxiuNom;
            log.error(err, ex);
            throw new RuntimeException(err);
        }
    }

    public static String getMimeTypeFromBase64(String base64, String nom) throws IOException {

        var suffix = "";
        byte[] contingut = Base64.decodeBase64(base64);
        return getMimeType(nom, contingut, suffix);
    }

    public static String getMimeTypeFromBase64(String base64, String nom, String extensio) throws IOException {

        var suffix = "." + extensio;
        byte[] contingut = Base64.decodeBase64(base64);
        return getMimeType(nom, contingut, suffix);
    }

    private static String getMimeType(String arxiuNom, byte[] contingut, String suffix) throws IOException {

//        if (Strings.isNullOrEmpty(arxiuNom)) {
        arxiuNom = Long.toString(System.nanoTime());
//        }
        File tmp = File.createTempFile(arxiuNom, suffix);
        Files.write(contingut, tmp);
        Tika tika = new Tika();
        String mimeType = tika.detect(tmp);
        FitxerUtils.esborrar(tmp);
        return mimeType;
    }

    public static String getExtension(String mimeType) throws MimeTypeException {

        return MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
    }


    public static boolean isMimeValidSIR(String mime) {
        return Mimes.getFormatsSIR().contains(mime);
    }
    public static boolean isMimeValidNoSIR(String mime) {
        return Mimes.getFormatsNoSIR().contains(mime);
    }

    public static boolean isFormatValid(Document doc) {

        String mime = getMimeTypeFromContingut(doc.getArxiuNom(), doc.getContingutBase64());
        return isFormatValid(mime, doc.getContingutBase64());
    }

    public static boolean isFormatValid(String mime, String docBase64) {

        return docBase64.startsWith(formatsValidsNotCom[0]) || docBase64.startsWith(formatsValidsNotCom[1]); //|| isZipSigned(mime, docBase64);
    }

    public static boolean isZipFileByMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }

        var mime = mimeType.trim().toLowerCase();
        return "application/zip".equals(mime)
                || "application/x-zip-compressed".equals(mime)
                || "application/x-zip".equals(mime)
                || "application/zip-compressed".equals(mime)
                || "application/x-zip-compressed".equals(mime);
    }

    public static boolean isZipSigned(String mime, String base64) {

        if (!ZIP_SIGNED.equals(mime)) {
            return false;
        }
        try {
            byte[] contingut = Base64.decodeBase64(base64);
            File tmp = File.createTempFile("zipSigned", ".zip" );
            Files.write(contingut, tmp);
            ZipFile zip = new ZipFile(tmp);
            zip.close();
            FitxerUtils.esborrar(tmp);
            return true;
        } catch (IOException ex) {
            log.error("Error comprovant si es un fitxer zipSigned", ex);
            return false;
        }
    }
}
