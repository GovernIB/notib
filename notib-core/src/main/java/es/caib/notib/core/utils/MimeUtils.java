package es.caib.notib.core.utils;

import com.google.common.io.Files;
import es.caib.notib.client.domini.DocumentV2;
import es.caib.notib.core.api.dto.mime.MimesSIR;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

@Slf4j
public class MimeUtils {

    private static final String ZIP_SIGNED = "application/pkcs7-signature";
    private static final String[] formatsValidsNotCom = {"JVBERi0","UEsDB"}; //PDF / ZIP

    public static String getMimeTypeFromContingut(String arxiuNom, byte[] contingut) {
        return getMimeTypeFromContingut(arxiuNom, Base64.encodeBase64String(contingut));
    }

    public static String getMimeTypeFromContingut(String arxiuNom, String base64) {

        try {
            int lastIndex = arxiuNom.lastIndexOf(".");
            if (lastIndex == 0 || lastIndex == -1) {
                throw new RuntimeException("Nom de l'arxiu invàlid: " + arxiuNom);
            }
            String nom = arxiuNom.substring(0, lastIndex);
            String ext = arxiuNom.substring(lastIndex, arxiuNom.length());
            return getMimeTypeFromBase64(base64, nom, ext);
        } catch (IOException ex) {
            String err = "Error obtenint el tipus MIME del document " + arxiuNom;
            log.error(err, ex);
            throw new RuntimeException(err);
        }
    }

    public static String getMimeTypeFromBase64(String base64, String nom, String extensio) throws IOException {

        byte[] contingut = Base64.decodeBase64(base64);
        File tmp = File.createTempFile(nom, "." + extensio);
        Files.write(contingut, tmp);
        Tika tika = new Tika();
        String mimeType = tika.detect(tmp);
        tmp.delete();
        return mimeType;
    }

    public static boolean isMimeValidSIR(String mime) throws Exception {
        return MimesSIR.formats.contains(mime);
    }

    public static boolean isFormatValid(DocumentV2 doc) {

        String mime = getMimeTypeFromContingut(doc.getArxiuNom(), doc.getContingutBase64());
        return isFormatValid(mime, doc.getContingutBase64());
    }

    public static boolean isFormatValid(String mime, String docBase64) {

        return docBase64.startsWith(formatsValidsNotCom[0]) || docBase64.startsWith(formatsValidsNotCom[1]); //|| isZipSigned(mime, docBase64);
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
            tmp.delete();
            return true;
        } catch (IOException ex) {
            log.error("Error comprovant si es un fitxer zipSigned", ex);
            return false;
        }
    }
}
