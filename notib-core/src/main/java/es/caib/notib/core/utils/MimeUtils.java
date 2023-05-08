package es.caib.notib.core.utils;

import com.google.common.io.Files;
import es.caib.notib.core.api.dto.mime.MimesSIR;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

@Slf4j
public class MimeUtils {


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
            return getMimeTypeFromBase64(base64, ext);
        } catch (IOException ex) {
            String err = "Error obtenint el tipus MIME del document " + arxiuNom;
            log.error(err, ex);
            throw new RuntimeException(err);
        }
    }

    public static String getMimeTypeFromBase64(String base64, String extensio) throws IOException {

        byte[] contingut = Base64.decodeBase64(base64);
        File tmp = File.createTempFile("foo", "." + extensio);
        Files.write(contingut, tmp);
        Tika tika = new Tika();
        String mimeType = tika.detect(tmp);
        tmp.delete();
        return mimeType;
    }

    public static boolean isMimeValidSIR(String mime) {

        return MimesSIR.formats.contains(mime);
    }
}
