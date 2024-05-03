package es.caib.notib.plugin.validatesignature.api;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import es.caib.notib.plugin.certificate.InformacioCertificat;
import org.fundaciobit.pluginsib.core.utils.AbstractPluginPropertiesTranslations;

/**
 * 
 * @author anadal
 *
 */
public abstract class AbstractValidateSignaturePlugin extends AbstractPluginPropertiesTranslations
    implements IValidateSignaturePlugin, ValidateSignatureConstants {

  protected Logger log = Logger.getLogger(this.getClass());
  
  
  private static final String VALIDATE_SIGNATURE_API_BUNDLE = "validatesignature-api";

  /**
   * 
   */
  public AbstractValidateSignaturePlugin() {
    super();
  }

  /**
   * @param propertyKeyBase
   * @param properties
   */
  public AbstractValidateSignaturePlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }

  /**
   * @param propertyKeyBase
   */
  public AbstractValidateSignaturePlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }

  @Override
  public String filter(ValidateSignatureRequest vsr) {

    SignatureRequestedInformation required = vsr.getSignatureRequestedInformation();
    SignatureRequestedInformation supported = getSupportedSignatureRequestedInformation();

    if (!checkRequiredSupported(required.getReturnSignatureTypeFormatProfile(),
        supported.getReturnSignatureTypeFormatProfile())) {
      String msg = getTraduccio(VALIDATE_SIGNATURE_API_BUNDLE, "error.filter.typeformatprofile" , new Locale(vsr.getLanguage())) ;
      return msg;
    }
    if (!checkRequiredSupported(required.getValidateCertificateRevocation(),
        supported.getValidateCertificateRevocation())) {
      String msg = getTraduccio(VALIDATE_SIGNATURE_API_BUNDLE, "error.filter.certificaterevocation" , new Locale(vsr.getLanguage())) ;
      return msg;
    }
    if (!checkRequiredSupported(required.getReturnCertificateInfo(),
        supported.getReturnCertificateInfo())) {
      String msg = getTraduccio(VALIDATE_SIGNATURE_API_BUNDLE, "error.filter.certificateinfo" , new Locale(vsr.getLanguage())) ;
      return msg;
    }
    if (!checkRequiredSupported(required.getReturnValidationChecks(),
        supported.getReturnValidationChecks())) {
      String msg = getTraduccio(VALIDATE_SIGNATURE_API_BUNDLE, "error.filter.validationchecks" , new Locale(vsr.getLanguage())) ;
      return msg;
    }
    if (!checkRequiredSupported(required.getReturnCertificates(),
        supported.getReturnCertificates())) {
      String msg = getTraduccio(VALIDATE_SIGNATURE_API_BUNDLE, "error.filter.returncertificates" , new Locale(vsr.getLanguage())) ;
      return msg;
    }
    if (!checkRequiredSupported(required.getReturnTimeStampInfo(),
        supported.getReturnTimeStampInfo())) {
      String msg = getTraduccio(VALIDATE_SIGNATURE_API_BUNDLE, "error.filter.timestampinfo" , new Locale(vsr.getLanguage())) ;
      return msg;
    }

    // null Significa tot OK
    return null;
  }

  protected boolean checkRequiredSupported(Boolean required, Boolean supported) {
    if (required == null || required == false) {
      return true;
    } else {
      // required == true
      if (supported == null) {
        // Suposarem que si que es retornarà
        return true;
      } else {
        return supported.booleanValue();
      }
    }
  }

  /**
   * 
   * @param vs
   */
  public static void printSignatureInfo(ValidateSignatureResponse vs) {
    System.out.println(" **************************************** ");
    System.out.println();
    System.out.println();

    ValidationStatus status = vs.getValidationStatus();
    if (status.getStatus() == ValidationStatus.SIGNATURE_VALID) {
      System.out.println(" status = VALID");
    } else {
      System.out
          .println(" status = "
              + ((status.getStatus() == ValidationStatus.SIGNATURE_INVALID) ? "INVALID"
                  : "ERROR"));
      System.out.println(" statusMsg = " + status.getErrorMsg());
    }

    System.out.println(" vs.getSignType() = " + vs.getSignType());
    System.out.println(" vs.getSignFormat() = " + vs.getSignFormat());
    System.out.println(" vs.getSignProfile() = " + vs.getSignProfile());

    SignatureDetailInfo[] diList = vs.getSignatureDetailInfo();
    if (diList != null) {
      for (int i = 0; i < diList.length; i++) {

        SignatureDetailInfo di = diList[i];

        System.out.println(" ================ SIGN[" + i + "] ===============");

        System.out.println(" - Algortime: " + di.getAlgorithm());
        System.out.println(" - DigestValue: " + di.getDigestValue());
        if (di.getSignDate() != null) {
          System.out
              .println(" - SignDate: " + new SimpleDateFormat().format(di.getSignDate()));
        }

        System.out.print(printChecks(di.getValidChecks(), " +++ VALID CHECKS +++"));
        System.out.print(printChecks(di.getInvalidChecks(), " +++ INVALID CHECKS +++"));
        System.out.print(printChecks(di.getIndeterminateChecks(),
            " +++ INDETERMINATE CHECKS +++"));

        {
          TimeStampInfo tsi = di.getTimeStampInfo();
          if (tsi != null) {

            System.out.println(" - SEGELL DE TEMPS ");
            System.out.println("      + Data: " + tsi.getCreationTime());
            System.out.println("      + Algo: " + tsi.getAlgorithm());
            System.out.println("      + CertificateIssuer: " + tsi.getCertificateIssuer());
            System.out.println("      + CertificateSubject: " + tsi.getCertificateSubject());
            System.out.println("      + Certificate: " + tsi.getCertificate());

          }
        }

        {
          byte[][] certificate = di.getCertificateChain();

          if (certificate != null) {
            System.out.println(" - Certificates[" + certificate.length + "]: ");

            for (int j = 0; j < certificate.length; j++) {
              System.out.println("    + Cert[" + j + "] = "
                  + org.fundaciobit.pluginsib.core.utils.Base64.encode(certificate[j]));
            }

          }
        }

        InformacioCertificat ci = di.getCertificateInfo();
        if (ci != null) {
          System.out.println(" - Certificate Info:");
          System.out.println(ci.toString());

        }
      }
    }
  }

  public static String printChecks(List<SignatureCheck> details, String title) {

    if (details == null || details.size() == 0) {
      return "";
    }

    StringBuffer str = new StringBuffer(title + "\n");
    int d = 0;
    for (SignatureCheck detail : details) {

      str.append(d + ".- Check " + detail.getName() + "");
      if (detail.getType() != null) {
        str.append(" (" + detail.getType() + ")");
      }
      str.append("\n");
      d++;
    }
    str.append("\n");
    return str.toString();
  }

}
