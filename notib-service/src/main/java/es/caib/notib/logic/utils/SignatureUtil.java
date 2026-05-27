package es.caib.notib.logic.utils;

import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.util.PdfUtils;
import es.caib.notib.logic.objectes.LoggingTipus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.pluginsib.utils.signature.SignatureCommonUtils;

import java.util.Arrays;

@AllArgsConstructor
@Slf4j
public class SignatureUtil {

    private final PluginHelper pluginHelper;

    public static boolean checkIfSignedAttached(byte[] contingut, String contentType) throws Exception{

        try {
            try {
                SignatureCommonUtils.getXAdESMode(contingut, false);
            } catch (Exception ex) {
                var error = "No es pot determinar el mode de signatura";
                if (ex.getMessage().contains(error) || Arrays.toString(ex.getStackTrace()).contains(error)) {
                    NotibLogger.getInstance().info("XADES error: " + ex.getMessage(), log, LoggingTipus.VALIDATE_SIGNATURE);
                    return false;
                }
            }
            try {
                SignatureCommonUtils.getCAdESMode(contingut);
            } catch (Exception ex) {
                var error = "Malformed content";
                if (ex.getMessage().contains(error) || Arrays.toString(ex.getStackTrace()).contains(error)) {
                    NotibLogger.getInstance().info("CADES error: " + ex.getMessage(), log, LoggingTipus.VALIDATE_SIGNATURE);
                    return false;
                }
            }
            var numSignatures = PdfUtils.getNumberOfSignaturesInPDF(contingut);
            if ("application/pdf".equals(contentType) && numSignatures <= 0) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            log.error("Error detectant la signatura", ex);
            throw ex;
        }
    }
}
