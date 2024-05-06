package es.caib.notib.plugin.validatesignature.afirmacxf;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import es.caib.notib.plugin.validatesignature.api.IValidateSignaturePlugin;
import es.caib.notib.plugin.validatesignature.api.SignatureDetailInfo;
import es.caib.notib.plugin.validatesignature.api.SignatureRequestedInformation;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureRequest;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureResponse;
import es.caib.notib.plugin.validatesignature.api.ValidationStatus;
import org.fundaciobit.pluginsib.core.utils.FileUtils;
import es.caib.notib.plugin.certificate.InformacioCertificat;

/**
 * 
 * @author anadal
 *
 */
public class ValidateSignatureMain {

  public static void main(String[] args) {
    
    if (args.length != 3) {
      
      System.err.println("Usage:   validatesignature.bat [filePropertiesPath] [packageBase] [signaturePath]");
      System.err.println("Example: validatesignature.bat plugin.properties org.fundaciobit.exemple.base. firma.pdf");
      
      System.exit(-1);
    }
    
    String filePropertiesPathStr = args[0];
    File filePropertiesPath = new File(filePropertiesPathStr);
    
    if (!filePropertiesPath.exists()) {
      System.err.println("El fitxer de propietats '" + filePropertiesPathStr + "' no existeix" );
      System.exit(-1);
    }
    
    Properties pluginProperties = new Properties();
    try {
      pluginProperties.load(new FileInputStream(filePropertiesPath));
    } catch(Exception e) {
      System.err.println("No s'ha pogut llegir el fitxer de propietats '" 
        + filePropertiesPathStr + "': " + e.getMessage());
      e.printStackTrace();
      System.exit(-1);
    }
        
    String packageBase = args[1];
    
    String signaturePathStr = args[2];
    File signaturePath = new File(signaturePathStr);
    
    if (!signaturePath.exists()) {
      System.err.println("El fitxer de firma '" + signaturePathStr + "' no existeix" );
      System.exit(-1);
    }
    
    byte[] signatureData;
    try {
      signatureData = FileUtils.readFromFile(signaturePath);      
    } catch(Exception e) {
      System.err.println("El fitxer de firma '" + signaturePathStr + "' no s'ha pogut llegir: " + e.getMessage() );
      System.exit(-1);
      return;
    }
    
    IValidateSignaturePlugin plugin;
    plugin = new AfirmaCxfValidateSignaturePlugin(packageBase, pluginProperties);

    ValidateSignatureRequest vsr = new ValidateSignatureRequest();
    vsr.setLanguage("ca");
    vsr.setSignatureData(signatureData);
    SignatureRequestedInformation signatureRequestedInformation;
    signatureRequestedInformation = new SignatureRequestedInformation();
    signatureRequestedInformation.setReturnCertificateInfo(true);
    
    vsr.setSignatureRequestedInformation(signatureRequestedInformation);
    
    System.out.println("\n\n\n");

    ValidateSignatureResponse response;
    try {
      response =  plugin.validateSignature(vsr);
      
    } catch(Exception e) {
      e.printStackTrace();
      return;
    }
    
      
      
      if (response == null) {
        System.out.println("ValidateSignatureResponse es null");
      }
      
      
      
      ValidationStatus statusInfo = response.getValidationStatus();
      
      final int status = statusInfo.getStatus();
      
          
      if(status == ValidationStatus.SIGNATURE_VALID) {
        System.out.println("Execucio: OK.");
      } else {
        System.err.println("Execucio: ERROR\nMissatge: " + statusInfo.getErrorMsg());
        System.out.println("\n\n\n");
        return;
      }
        
      
      System.out.println("response.getSignType() = " + response.getSignType());
      System.out.println("response.getSignFormat() = " + response.getSignFormat());
      System.out.println("response.getSignProfile() = " + response.getSignProfile());

      SignatureDetailInfo[] certificatesInfo = response.getSignatureDetailInfo();
      
      if (certificatesInfo != null) {
        int count = 1;
        for (SignatureDetailInfo info :certificatesInfo) {
          System.out.println(" =========== CERTIFICAT " + (count++) + " ============");
          InformacioCertificat certInfo = info.getCertificateInfo();
          
          if (certInfo == null) {
            System.out.println("       --- NO HI HA INFORMACIO DISPONIBLE ---");
          } else {
            System.out.println("    -  getNombreApellidosResponsable: " + certInfo.getNomCompletResponsable());
            System.out.println("    -  getNombreResponsable: " + certInfo.getNomResponsable());
            System.out.println("    -  getApellidosResponsable: " + certInfo.getLlinatgesResponsable());
            System.out.println("    -  getNumeroSerie: " + certInfo.getNumeroSerie());
            System.out.println("    -  getValidoDesde: " + certInfo.getValidDesDe());
            System.out.println("    -  getValidoHasta: " + certInfo.getValidFins());
            System.out.println("    -  getNifResponsable: " + certInfo.getNifResponsable());
            System.out.println("    -  getEmissorID: " + certInfo.getEmissorID());
          }
        }
        
      }
      
      
      
      System.out.println("\n\n\n");


  }
}
