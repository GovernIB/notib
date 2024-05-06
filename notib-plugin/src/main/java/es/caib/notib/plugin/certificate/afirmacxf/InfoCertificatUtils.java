package es.caib.notib.plugin.certificate.afirmacxf;

import org.apache.log4j.Logger;
import es.caib.notib.plugin.certificate.InformacioCertificat;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * @author anadal(u80067)
 *
 */
public class InfoCertificatUtils {
  
  private static final Logger log = Logger.getLogger(InfoCertificatUtils.class);
  private static final DateFormatSymbols DATE_FORMAT_SYMBOLS = new DateFormatSymbols(new Locale("es"));

  static {
    DATE_FORMAT_SYMBOLS.setShortWeekdays(new String[]{"", "dom", "lun", "mar", "mié", "jue", "vie", "sáb"});
  }

  public static InformacioCertificat processInfoCertificate(Map<String, Object> camps) {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd EEE HH:mm:ss Z", DATE_FORMAT_SYMBOLS);

    InformacioCertificat dades = new InformacioCertificat();

    for (String key : camps.keySet()) {

      //String key = camp.getIdCampo();
      String value = (String)camps.get(key);
      if ("tipoCertificado".equalsIgnoreCase(key)) {
        dades.setTipusCertificat(cleanValue(value));
        continue;
      }
      if ("subject".equalsIgnoreCase(key)) {
        dades.setSubject(cleanValue(value));
        continue;
      }
      if ("NombreApellidosResponsable".equalsIgnoreCase(key)) {
        dades.setNomCompletResponsable(cleanValue(value));
        continue;
      }
      if ("nombreResponsable".equalsIgnoreCase(key)) {
        dades.setNomResponsable(cleanValue(value));
        continue;
      }
      if ("primerApellidoResponsable".equalsIgnoreCase(key)) {
        dades.setPrimerLlinatgeResponsable(cleanValue(value));
        continue;
      }
      if ("segundoApellidoResponsable".equalsIgnoreCase(key)) {
        dades.setSegonLlinatgeResponsable(cleanValue(value));
        continue;
      }
      if ("NIFResponsable".equalsIgnoreCase(key)) {
        dades.setNifResponsable(cleanValue(value));
        continue;
      }
      if ("idEmisor".equalsIgnoreCase(key)) {
        dades.setEmissorID(cleanValue(value));
        continue;
      }
      if ("NIF-CIF".equalsIgnoreCase(key)) {
        dades.setUnitatOrganitzativaNifCif(cleanValue(value));
        continue;
      }
      if ("email".equalsIgnoreCase(key)) {
        dades.setEmail(cleanValue(value));
        continue;
      }
      if ("fechaNacimiento".equalsIgnoreCase(key)) {
        dades.setDataNaixement(cleanValue(value));
        continue;
      }
      if ("razonSocial".equalsIgnoreCase(key)) {
        dades.setRaoSocial(cleanValue(value));
        continue;
      }
      if ("clasificacion".equalsIgnoreCase(key)) {
        try {
          dades.setClassificacio(Integer.parseInt(value));
        } catch(NumberFormatException nfe) {
          log.error("Error parsejant el valor de 'clasificacion' ]" + value + "[:" + nfe.getMessage(), nfe);
        }
        continue;
      }
      if ("numeroSerie".equalsIgnoreCase(key)) {
        if (cleanValue(value) != null) {
          try {
            dades.setNumeroSerie(new BigInteger(value));
          } catch(NumberFormatException nfe) {
            log.error("Error parsejant el valor de 'numeroSerie' ]" + value + "[:" + nfe.getMessage(), nfe);
          }
        }
        continue;
      }
      if ("usoCertificado".equalsIgnoreCase(key)) {
        dades.setUsCertificat(cleanValue(value));
        continue;
      }
      if ("extensionUsoCertificado".equalsIgnoreCase(key)) {
        dades.setUsCertificatExtensio(cleanValue(value));
        continue;
      }
      if ("validoHasta".equalsIgnoreCase(key)) {
        try {
          dades.setValidFins(dateFormat.parse(value));
        } catch (ParseException e) {
          log.error("Error desconegut parsejant la data de final ]" + value + "[: " + e.getMessage(), e);
        }
        continue;
      }
      if ("validoDesde".equalsIgnoreCase(key)) {
        try {
          dades.setValidDesDe(dateFormat.parse(value));
        } catch (ParseException e) {
          log.error("Error desconegut parsejant la data d'inici  ]" + value + "[: " + e.getMessage(), e);
        }
        continue;
      }

      if ("politica".equalsIgnoreCase(key)) {
        dades.setPolitica(cleanValue(value));
        continue;
      }

      if ("versionPolitica".equalsIgnoreCase(key)) {
        dades.setPoliticaVersio(cleanValue(value));
        continue;
      }

      if ("idPolitica".equalsIgnoreCase(key)) {
        dades.setPoliticaID(cleanValue(value));
        continue;
      }

      if ("OrganizacionEmisora".equalsIgnoreCase(key)) {
        dades.setEmissorOrganitzacio(cleanValue(value));
        continue;
      }

      if ("pais".equalsIgnoreCase(key)) {
        dades.setPais(cleanValue(value));
        continue;
      }

      if ("unidadOrganizativa".equalsIgnoreCase(key)) {
        dades.setUnitatOrganitzativa(cleanValue(value));
        continue;
      }
      
      
      
      if ("ApellidosResponsable".equalsIgnoreCase(key)) {
        dades.setLlinatgesResponsable(cleanValue(value));
        continue;
      }
      
      
      if ("ID_europeo".equalsIgnoreCase(key)) {
        dades.setIdEuropeu(cleanValue(value));
        continue;
      }
      
      if ("OI_Europeo".equalsIgnoreCase(key)) {
        dades.setOiEuropeu(cleanValue(value));
        continue;
      }
      
      
      if ("entidadSuscriptora".equalsIgnoreCase(key)) {
        dades.setEntitatSubscriptoraNom(cleanValue(value));
        continue;
      }
      
      
      if ("NIFEntidadSuscriptora".equalsIgnoreCase(key)) {
        dades.setEntitatSubscriptoraNif(cleanValue(value));
        continue;
      }
      
      if ("certClassification".equalsIgnoreCase(key)) {
        dades.setClassificacioEidas(cleanValue(value));
        continue;
      }
      
      if ("certQualified".equalsIgnoreCase(key)) {
        if ("YES".equals(value)) {
          dades.setCertificatQualificat(true);
        } else if ("NO".equals(value)) {
          dades.setCertificatQualificat(false);
        } else {
          dades.setCertificatQualificat(null);
        }
        continue;
      }
      
      if ("puesto".equalsIgnoreCase(key)) {
        dades.setLlocDeFeina(cleanValue(value));
        continue;
      }
      
      
      if ("cargo".equalsIgnoreCase(key)) {
        dades.setCarrec(cleanValue(value));
        continue;
      }
      
      if ("qscd".equalsIgnoreCase(key)) {
        if ("YES".equals(value)) {
          dades.setCreatAmbUnDispositiuSegur(true);
        } else if ("NO".equals(value)) {
          dades.setCreatAmbUnDispositiuSegur(false);
        } else {
          dades.setCreatAmbUnDispositiuSegur(null);
        }
        continue;
      }

      if ("organizacion".equalsIgnoreCase(key)) {
        dades.setOrganitzacio(cleanValue(value));
        continue;
      }
      
      if ("NombreDominioIP".equalsIgnoreCase(key)) {
        dades.setNomDomini(cleanValue(value));
        continue;
      }
      
      if ("DenominaciónSistemaComponente".equalsIgnoreCase(key) || "DenominacionSistemaComponente".equalsIgnoreCase(key)) {
        dades.setDenominacioSistemaComponent(cleanValue(value));
        continue;
      }
      
      if ("seudonimo".equalsIgnoreCase(key)) {
        dades.setPseudonim(cleanValue(value));
        continue;
      }
      
      if ("Documento representación".equalsIgnoreCase(key)) {
        dades.setDocumentRepresentacio(cleanValue(value));
        continue;
      }
      
      
      if ("numeroIdentificacionPersonal".equalsIgnoreCase(key)) {
        dades.setNumeroIdentificacionPersonal(cleanValue(value));
        continue;
      }

      if ("QcCompliance".equalsIgnoreCase(key)) {
        dades.setQcCompliance(cleanValue(value));
        continue;
      }

      if ("QcSSCD".equalsIgnoreCase(key)) {
        dades.setQcSSCD(cleanValue(value));
        continue;
      }

      if ("IDlogOn".equalsIgnoreCase(key)) {
        dades.setIdlogOn(cleanValue(value));
        continue;
      }

      if (cleanValue(value) != null) {
        dades.getAltresValors().put(key, value);
      }

    }
    return dades;
  }
  
  
  protected static String cleanValue(String value) {
    if (value == null) {
      return null;
    }
    
    if (value.trim().length() == 0) {
      return null;
    }
    
    return value;
    
  }
  
}
