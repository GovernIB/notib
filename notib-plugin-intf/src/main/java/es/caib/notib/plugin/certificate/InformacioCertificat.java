/**
 * 
 */
package es.caib.notib.plugin.certificate;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Informació sobre un certificat
 * 
 * @author anadal
 */
@XmlRootElement
public class InformacioCertificat {

  // certClassification:
  public static final String EIDAS_ESEAL = "ESEAL"; // Certificado de sello electrónico.
                                                    // Pertenece a una persona jurídica pero NO
                                                    // incluye un custodio.
  public static final String EIDAS_ESIG = "ESIG"; // Certificado para firma electrónica
                                                  // (persona física).
  public static final String EIDAS_WSA = "WSA"; // Certificado para autenticación de servidor
                                                // web.
  public static final String EIDAS_UNKNOWN = "UNKNOWN"; // Se desconoce el tipo del certificado
                                                        // (Se considera no cualificado a menos
                                                        // que se indique lo contrario en
                                                        // certQualified).

  /*
   * 
   * Estos campos también se devolverán para los certificados españoles, para facilitar el
   * tratamiento a aquellas aplicaciones que necesiten admitir tanto a certificados españoles
   * como europeos, ya que las siguientes clasificaciones son equivalentes: ESEAL =>
   * Clasificación = 8. ESIG => Clasificación = 0, 5, 7, 11, 12. WSA => Clasificación = 9.
   * UNKNOWN => Clasificación = 2, 10.
   */

  public static final int CLASSIFICACIO_DESCONEGUDA = -1;

  // 0 –Persona física-
  public static final int CLASSIFICACIO_PERSONA_FISICA = 0;
  // 1 –Persona jurídica (no cualificado)-
  public static final int CLASSIFICACIO_PERSONA_JURIDICA = 1;
  // 2 –No cualificados-
  public static final int CLASSIFICACIO_NO_QUALIFICATS = 2;

  // 3 –Sede según la ley 40/2015(no cualificado)-
  public static final int CLASSIFICACIO_SEU_ELECTRONICA = 3;
  // 4 –Sello según la ley 40/2015(no cualificado)-
  public static final int CLASSIFICACIO_SEGELL = 4;
  // 5 –Empleado Público según la ley 40/2015-
  public static final int CLASSIFICACIO_EMPLEAT_PUBLIC = 5;
  // 6 –Entidad sin personalidad jurídica (no cualificado)-
  public static final int CLASSIFICACIO_ENTITAT_SENSE_PERSONALITAT_JURIDICA = 6;
  // 7 –Empleado público con seudónimo según el RD 1671/2009-
  public static final int CLASSIFICACIO_EMPLEAT_PUBLIC_AMB_PSEUDONIM = 7;
  // 8 –Cualificado de sello, según el reglamente UE 910/2014-
  public static final int CLASSIFICACIO_SEGELL_QUALIFICAT = 8;
  // 9 –Cualificado de autenticación, según el reglamente UE 910/2014-
  public static final int CLASSIFICACIO_AUTENTIFICACIO_QUALIFICAT = 9;
  // 10 –Cualificado de servicio cualificado de sello de tiempo-
  public static final int CLASSIFICACIO_SEGELL_DE_TEMPS = 10;
  // 11 –Persona física representante ante las Administraciones Públicas de persona jurídica-
  public static final int CLASSIFICACIO_AUTENTIFICACIO_REPRESENTANT_ADMINISTRACIO_PERSONA_JURIDICA = 11;
  // 12 –Persona física representante ante las Administraciones Públicas de entidad sin persona
  // jurídica
  public static final int CLASSIFICACIO_AUTENTIFICACIO_REPRESENTANT_ADMINISTRACIO_ENTITAT = 12;

  private String tipusCertificat;
  private String subject;
  private String nomResponsable;
  private String primerLlinatgeResponsable;
  private String segonLlinatgeResponsable;
  private String llinatgesResponsable;
  private String nomCompletResponsable;

  private String nifResponsable;

  private String pseudonim;

  private String documentRepresentacio;

  private String carrec;
  private String llocDeFeina;

  private String nomDomini;
  private String denominacioSistemaComponent;

  private String idEuropeu;
  private String oiEuropeu;

  private String numeroIdentificacionPersonal;

  private String entitatSubscriptoraNom;
  private String entitatSubscriptoraNif;

  // EIDAS Classification (certClassification)
  private String classificacioEidas;

  /**
   * certQualified: + NO: El certificado no es cualificado (reconocido). + YES: El certificado
   * es cualificado. + UNKNOWN: Se desconoce (Se considera no cualificado a menos que se
   * indique lo contrario en certClasification).
   */
  private Boolean certificatQualificat;

  // qualified signature creation device (QSCD).
  private Boolean creatAmbUnDispositiuSegur;

  private String organitzacio;

  private String emissorID;
  private String unitatOrganitzativaNifCif;
  private String email;
  private String dataNaixement;
  private String raoSocial;
  private int classificacio = CLASSIFICACIO_DESCONEGUDA;
  private BigInteger numeroSerie;

  private String usCertificat;
  private String usCertificatExtensio;
  private Date validFins;
  private Date validDesDe;
  private String politica;
  private String politicaVersio;
  private String politicaID;
  private String emissorOrganitzacio;
  private String pais;
  private String unitatOrganitzativa;

  /** Indica si el certificado es cualificado EU según ETSi 319 412-5 */
  private String qcCompliance;

  /**
   * Indica si la clave privada reside en un dispositivo hardware cualificado según ETSi 319
   * 412-5
   */
  private String qcSSCD;

  /** Identificador de log-on del Sistema Operativo */
  private String idlogOn;

  private Map<String, String> altresValors = new HashMap<String, String>();

  public InformacioCertificat() {
    super();
  }

  public String getNomCompletResponsable() {
    return nomCompletResponsable;
  }

  public void setNomCompletResponsable(String nomCompletResponsable) {
    this.nomCompletResponsable = nomCompletResponsable;
  }

  public String getTipusCertificat() {
    return tipusCertificat;
  }

  public void setTipusCertificat(String tipusCertificat) {
    this.tipusCertificat = tipusCertificat;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getDenominacioSistemaComponent() {
    return denominacioSistemaComponent;
  }

  public void setDenominacioSistemaComponent(String denominacioSistemaComponent) {
    this.denominacioSistemaComponent = denominacioSistemaComponent;
  }

  public String getNomResponsable() {
    return nomResponsable;
  }

  public String getNomDomini() {
    return nomDomini;
  }

  public void setNomDomini(String nomDomini) {
    this.nomDomini = nomDomini;
  }

  public void setNomResponsable(String nomResponsable) {
    this.nomResponsable = nomResponsable;
  }

  public String getPrimerLlinatgeResponsable() {
    return primerLlinatgeResponsable;
  }

  public void setPrimerLlinatgeResponsable(String primerLlinatgeResponsable) {
    this.primerLlinatgeResponsable = primerLlinatgeResponsable;
  }

  public String getSegonLlinatgeResponsable() {
    return segonLlinatgeResponsable;
  }

  public void setSegonLlinatgeResponsable(String segonLlinatgeResponsable) {
    this.segonLlinatgeResponsable = segonLlinatgeResponsable;
  }

  public String getNifResponsable() {
    return nifResponsable;
  }

  public void setNifResponsable(String nifResponsable) {
    this.nifResponsable = nifResponsable;
  }

  public String getDocumentRepresentacio() {
    return documentRepresentacio;
  }

  public void setDocumentRepresentacio(String documentRepresentacio) {
    this.documentRepresentacio = documentRepresentacio;
  }

  public String getEmissorID() {
    return emissorID;
  }

  public void setEmissorID(String emissorID) {
    this.emissorID = emissorID;
  }

  public String getUnitatOrganitzativaNifCif() {
    return unitatOrganitzativaNifCif;
  }

  public void setUnitatOrganitzativaNifCif(String unitatOrganitzativaNifCif) {
    this.unitatOrganitzativaNifCif = unitatOrganitzativaNifCif;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDataNaixement() {
    return dataNaixement;
  }

  public void setDataNaixement(String dataNaixement) {
    this.dataNaixement = dataNaixement;
  }

  public String getRaoSocial() {
    return raoSocial;
  }

  public void setRaoSocial(String raoSocial) {
    this.raoSocial = raoSocial;
  }

  public int getClassificacio() {
    return classificacio;
  }

  public void setClassificacio(int classificacio) {
    this.classificacio = classificacio;
  }

  public BigInteger getNumeroSerie() {
    return numeroSerie;
  }

  public void setNumeroSerie(BigInteger numeroSerie) {
    this.numeroSerie = numeroSerie;
  }

  public String getUsCertificat() {
    return usCertificat;
  }

  public void setUsCertificat(String usCertificat) {
    this.usCertificat = usCertificat;
  }

  public String getUsCertificatExtensio() {
    return usCertificatExtensio;
  }

  public void setUsCertificatExtensio(String usCertificatExtensio) {
    this.usCertificatExtensio = usCertificatExtensio;
  }

  public Date getValidFins() {
    return validFins;
  }

  public void setValidFins(Date validFins) {
    this.validFins = validFins;
  }

  public Date getValidDesDe() {
    return validDesDe;
  }

  public void setValidDesDe(Date validDesDe) {
    this.validDesDe = validDesDe;
  }

  public String getPolitica() {
    return politica;
  }

  public void setPolitica(String politica) {
    this.politica = politica;
  }

  public String getPoliticaVersio() {
    return politicaVersio;
  }

  public void setPoliticaVersio(String politicaVersio) {
    this.politicaVersio = politicaVersio;
  }

  public String getPoliticaID() {
    return politicaID;
  }

  public void setPoliticaID(String politicaID) {
    this.politicaID = politicaID;
  }

  public String getEmissorOrganitzacio() {
    return emissorOrganitzacio;
  }

  public void setEmissorOrganitzacio(String emissorOrganitzacio) {
    this.emissorOrganitzacio = emissorOrganitzacio;
  }

  public String getPais() {
    return pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
  }

  public String getUnitatOrganitzativa() {
    return unitatOrganitzativa;
  }

  public void setUnitatOrganitzativa(String unitatOrganitzativa) {
    this.unitatOrganitzativa = unitatOrganitzativa;
  }

  public String getLlinatgesResponsable() {
    return llinatgesResponsable;
  }

  public void setLlinatgesResponsable(String llinatgesResponsable) {
    this.llinatgesResponsable = llinatgesResponsable;
  }

  public String getIdEuropeu() {
    return idEuropeu;
  }

  public void setIdEuropeu(String idEuropeu) {
    this.idEuropeu = idEuropeu;
  }

  public String getEntitatSubscriptoraNom() {
    return entitatSubscriptoraNom;
  }

  public void setEntitatSubscriptoraNom(String entitatSubscriptoraNom) {
    this.entitatSubscriptoraNom = entitatSubscriptoraNom;
  }

  public String getEntitatSubscriptoraNif() {
    return entitatSubscriptoraNif;
  }

  public void setEntitatSubscriptoraNif(String entitatSubscriptoraNif) {
    this.entitatSubscriptoraNif = entitatSubscriptoraNif;
  }

  public String getClassificacioEidas() {
    return classificacioEidas;
  }

  public void setClassificacioEidas(String classificacioEidas) {
    this.classificacioEidas = classificacioEidas;
  }

  public Boolean getCertificatQualificat() {
    return certificatQualificat;
  }

  public void setCertificatQualificat(Boolean certificatQualificat) {
    this.certificatQualificat = certificatQualificat;
  }

  public String getLlocDeFeina() {
    return llocDeFeina;
  }

  public void setLlocDeFeina(String llocDeFeina) {
    this.llocDeFeina = llocDeFeina;
  }

  public Boolean getCreatAmbUnDispositiuSegur() {
    return creatAmbUnDispositiuSegur;
  }

  public void setCreatAmbUnDispositiuSegur(Boolean creatAmbUnDispositiuSegur) {
    this.creatAmbUnDispositiuSegur = creatAmbUnDispositiuSegur;
  }

  public String getOrganitzacio() {
    return organitzacio;
  }

  public void setOrganitzacio(String organitzacio) {
    this.organitzacio = organitzacio;
  }

  public String getCarrec() {
    return carrec;
  }

  public void setCarrec(String carrec) {
    this.carrec = carrec;
  }

  public String getOiEuropeu() {
    return oiEuropeu;
  }

  public void setOiEuropeu(String oiEuropeu) {
    this.oiEuropeu = oiEuropeu;
  }

  public Map<String, String> getAltresValors() {
    return altresValors;
  }

  public void setAltresValors(Map<String, String> altresValors) {
    this.altresValors = altresValors;
  }

  public String getQcCompliance() {
    return qcCompliance;
  }

  public void setQcCompliance(String qcCompliance) {
    this.qcCompliance = qcCompliance;
  }

  public String getQcSSCD() {
    return qcSSCD;
  }

  public void setQcSSCD(String qcSSCD) {
    this.qcSSCD = qcSSCD;
  }

  public String getIdlogOn() {
    return idlogOn;
  }

  public void setIdlogOn(String idlogOn) {
    this.idlogOn = idlogOn;
  }

  public String getPseudonim() {
    return pseudonim;
  }

  public void setPseudonim(String pseudonim) {
    this.pseudonim = pseudonim;
  }

  public String getNumeroIdentificacionPersonal() {
    return numeroIdentificacionPersonal;
  }

  public void setNumeroIdentificacionPersonal(String numeroIdentificacionPersonal) {
    this.numeroIdentificacionPersonal = numeroIdentificacionPersonal;
  }

  @Override
  public String toString() {
    StringBuffer st = new StringBuffer();

    st.append("Subject: ").append(getSubject()).append("\n");

    st.append("Nom Complet Responsable: ").append(getNomCompletResponsable()).append("\n");

    st.append("Nom Responsable: ").append(getNomResponsable()).append("\n");
    st.append("Primer Llinatge Responsable: ").append(getPrimerLlinatgeResponsable())
        .append("\n");
    st.append("Segon Llinatge Responsable: ").append(getSegonLlinatgeResponsable())
        .append("\n");
    st.append("Llinatges Responsable: ").append(getLlinatgesResponsable()).append("\n");
    st.append("NIF Responsable: ").append(getNifResponsable()).append("\n");

    st.append("Numero Identificacion Personal: ").append(getNumeroIdentificacionPersonal())
        .append("\n");

    st.append("ID Europeu: ").append(getIdEuropeu()).append("\n");
    st.append("OI Europeu: ").append(getOiEuropeu()).append("\n");

    st.append("Email: ").append(getEmail()).append("\n");
    st.append("Data Naixement: ").append(getDataNaixement()).append("\n");
    st.append("Rao Social: ").append(getRaoSocial()).append("\n");

    st.append("Document Representació: ").append(getDocumentRepresentacio()).append("\n");

    st.append("Pseudonim: ").append(getPseudonim()).append("\n");

    st.append("Lloc de Feina: ").append(getLlocDeFeina()).append("\n");
    st.append("Càrrec: ").append(getCarrec()).append("\n");

    st.append("Nom Domini: ").append(getNomDomini()).append("\n");
    st.append("Denominació Sistema Component: ").append(getDenominacioSistemaComponent())
        .append("\n");

    st.append("Tipus Certificat: ").append(getTipusCertificat()).append("\n");
    st.append("Classificació Espanya: ");
    switch (getClassificacio()) {
      case CLASSIFICACIO_NO_QUALIFICATS:
        st.append("COMPONENTS");
      break;
      case CLASSIFICACIO_PERSONA_FISICA:
        st.append("PERSONA FISICA");
      break;
      case CLASSIFICACIO_PERSONA_JURIDICA:
        st.append("PERSONA JURIDICA");
      break;
      case CLASSIFICACIO_SEU_ELECTRONICA:
        st.append("Sede según la ley 40/2015(no cualificado)");
      break;

      case CLASSIFICACIO_SEGELL:
        st.append("Sello según la ley 40/2015(no cualificado)");
      break;

      case CLASSIFICACIO_EMPLEAT_PUBLIC:
        st.append("Empleado Público según la ley 40/2015");
      break;

      case CLASSIFICACIO_ENTITAT_SENSE_PERSONALITAT_JURIDICA:
        st.append("Entidad sin personalidad jurídica (no cualificado)");
      break;

      case CLASSIFICACIO_EMPLEAT_PUBLIC_AMB_PSEUDONIM:
        st.append("Empleado público con seudónimo según el RD 1671/2009");
      break;

      case CLASSIFICACIO_SEGELL_QUALIFICAT:
        st.append("Cualificado de sello, según el reglamente UE 910/2014");
      break;

      case CLASSIFICACIO_AUTENTIFICACIO_QUALIFICAT:
        st.append("Cualificado de autenticación, según el reglamente UE 910/2014");
      break;

      case CLASSIFICACIO_SEGELL_DE_TEMPS:
        st.append("Cualificado de servicio cualificado de sello de tiempo");
      break;

      case CLASSIFICACIO_AUTENTIFICACIO_REPRESENTANT_ADMINISTRACIO_PERSONA_JURIDICA:
        st.append("Persona  física  representante  ante  las  Administraciones  Públicas de persona jurídica");
      break;

      case CLASSIFICACIO_AUTENTIFICACIO_REPRESENTANT_ADMINISTRACIO_ENTITAT:
        st.append("Persona  física  representante  ante  las  Administraciones Públicas de entidad sin persona jurídica");
      break;

      default:
        st.append("Desconegut");
    }
    st.append(" (").append(getClassificacio()).append(")").append("\n");

    {
      String eidasClassificacio = getClassificacioEidas();

      if (eidasClassificacio != null) {
        st.append("Classificacio EIDAS: ");
        if (EIDAS_ESEAL.equals(eidasClassificacio)) {

          st.append(" Certificado de sello electrónico. Pertenece a una persona jurídica pero NO incluye un custodio. (ESEAL)\n");
        } else if (EIDAS_ESIG.equals(eidasClassificacio)) {
          st.append("Certificado para firma electrónica (persona física, ESIG).");
        } else if (EIDAS_WSA.equals(eidasClassificacio)) {
          st.append("Certificado para autenticación de servidor web (WSA)");
        } else if (EIDAS_UNKNOWN.equals(eidasClassificacio)) {
          st.append("Se desconoce el tipo del certificado (Se considera no  cualificado a menos que se indique lo contrario en certQualified)(UNKNOWN)");
        } else {
          st.append(" Classificació desconeguda => " + eidasClassificacio);
        }

        st.append("\n");
      }
    }

    st.append("Entitat Subscriptora Nom: ").append(getEntitatSubscriptoraNom()).append("\n");
    st.append("Entitat Subscriptora Nif/Cif: ").append(getEntitatSubscriptoraNif())
        .append("\n");

    st.append("Organització Nom: ").append(getOrganitzacio()).append("\n");
    st.append("Organització Nif/Cif: ").append(getUnitatOrganitzativaNifCif()).append("\n");

    st.append("Unitat Organitzativa: ").append(getUnitatOrganitzativa()).append("\n");

    st.append("Pais: ").append(getPais()).append("\n");

    st.append("Emissor ID: ").append(getEmissorID()).append("\n");
    st.append("Emissor Organització: ").append(getEmissorOrganitzacio()).append("\n");
    st.append("Numero Serie: ").append(getNumeroSerie()).append("\n");
    st.append("Us Certificat: ").append(getUsCertificat()).append("\n");
    st.append("Us Certificat Extes: ").append(getUsCertificatExtensio()).append("\n");

    {
      Boolean cq = getCertificatQualificat();
      st.append("Certificat Qualificat: ")
          .append(cq == null ? "Desconegut" : (cq ? "Si" : "No")).append("\n");
    }

    {
      Boolean cq = getCreatAmbUnDispositiuSegur();
      st.append("Creat amb un Dispositiu Qualificat de Firma: ")
          .append(cq == null ? "Desconegut" : (cq ? "Si" : "No")).append("\n");
    }

    st.append("Valid des de: ").append(getValidDesDe()).append("\n");
    st.append("Valid fins: ").append(getValidFins()).append("\n");
    st.append("Politica: ").append(getPolitica()).append("\n");
    st.append("Politica Versio: ").append(getPoliticaVersio()).append("\n");
    st.append("Politica ID: ").append(getPoliticaID()).append("\n");

    if (getQcCompliance() != null) {
      st.append("QcCompliance: ").append(getQcCompliance()).append("\n");
    }

    if (getQcSSCD() != null) {
      st.append("QcSSCD: ").append(getQcSSCD()).append("\n");
    }

    if (getIdlogOn() != null) {
      st.append("IDlogOn: ").append(getIdlogOn()).append("\n");
    }

    if (altresValors != null && altresValors.size() != 0) {

      st.append("\n").append(" -- Altres Valors --").append("\n");
      for (String key : altresValors.keySet()) {
        st.append("- ").append(key).append(" =>").append(altresValors.get(key)).append("\n");
      }

    }

    return st.toString();

  }

  public String[] compareTo(InformacioCertificat obj) {

    if (this == obj) {
      return null; // és la mateixa instancia
    }

    if (obj == null) {
      return new String[] { "La Informacio de Certificat Generada val null" };
    }

    ArrayList<String> errors = new ArrayList<String>();

    comparaCamp(errors, carrec, obj.carrec, "Càrrec");

    comparaCamp(errors, certificatQualificat, obj.certificatQualificat, "CertificatQualificat");

    comparaCamp(errors, classificacio, obj.classificacio, "classificacio");

    comparaCamp(errors, classificacioEidas, obj.classificacioEidas, "classificacioEidas");

    comparaCamp(errors, creatAmbUnDispositiuSegur, obj.creatAmbUnDispositiuSegur,
        "creatAmbUnDispositiuSegur");

    comparaCamp(errors, dataNaixement, obj.dataNaixement, "dataNaixement");
    comparaCamp(errors, denominacioSistemaComponent, obj.denominacioSistemaComponent,
        "denominacioSistemaComponent");

    comparaCamp(errors, documentRepresentacio, obj.documentRepresentacio,
        "documentRepresentacio");
    comparaCamp(errors, email, obj.email, "email");

    comparaCamp(errors, emissorID, obj.emissorID, "emissorID");

    comparaCamp(errors, emissorOrganitzacio, obj.emissorOrganitzacio, "emissorOrganitzacio");

    comparaCamp(errors, entitatSubscriptoraNif, obj.entitatSubscriptoraNif,
        "entitatSubscriptoraNif");

    comparaCamp(errors, entitatSubscriptoraNom, obj.entitatSubscriptoraNom,
        "entitatSubscriptoraNom");

    comparaCamp(errors, idEuropeu, obj.idEuropeu, "idEuropeu");

    comparaCamp(errors, llinatgesResponsable, obj.llinatgesResponsable, "llinatgesResponsable");

    comparaCamp(errors, llocDeFeina, obj.llocDeFeina, "llocDeFeina");
    comparaCamp(errors, nifResponsable, obj.nifResponsable, "nifResponsable");
    comparaCamp(errors, nomCompletResponsable, obj.nomCompletResponsable,
        "nomCompletResponsable");

    comparaCamp(errors, nomDomini, obj.nomDomini, "nomDomini");

    comparaCamp(errors, nomResponsable, obj.nomResponsable, "nomResponsable");

    comparaCamp(errors, numeroIdentificacionPersonal, obj.numeroIdentificacionPersonal,
        "numeroIdentificacionPersonal");

    comparaCamp(errors, numeroSerie, obj.numeroSerie, "numeroSerie");

    comparaCamp(errors, oiEuropeu, obj.oiEuropeu, "oiEuropeu");

    comparaCamp(errors, organitzacio, obj.organitzacio, "organitzacio");

    comparaCamp(errors, pais, obj.pais, "pais");

    comparaCamp(errors, politica, obj.politica, "politica");

    comparaCamp(errors, politicaID, obj.politicaID, "politicaID");

    comparaCamp(errors, politicaVersio, obj.politicaVersio, "politicaVersio");

    comparaCamp(errors, primerLlinatgeResponsable, obj.primerLlinatgeResponsable,
        "primerLlinatgeResponsable");

    comparaCamp(errors, pseudonim, obj.pseudonim, "pseudonim");

    comparaCamp(errors, raoSocial, obj.raoSocial, "raoSocial");

    comparaCamp(errors, segonLlinatgeResponsable, obj.segonLlinatgeResponsable,
        "segonLlinatgeResponsable");

    comparaCamp(errors, subject, obj.subject, "subject");

    comparaCamp(errors, tipusCertificat, obj.tipusCertificat, "tipusCertificat");

    comparaCamp(errors, unitatOrganitzativa, obj.unitatOrganitzativa, "unitatOrganitzativa");

    comparaCamp(errors, unitatOrganitzativaNifCif, obj.unitatOrganitzativaNifCif,
        "unitatOrganitzativaNifCif");

    comparaCamp(errors, usCertificat, obj.usCertificat, "usCertificat");

    comparaCamp(errors, usCertificatExtensio, obj.usCertificatExtensio, "usCertificatExtensio");

    comparaCamp(errors, validDesDe, obj.validDesDe, "validDesDe");

    comparaCamp(errors, validFins, obj.validFins, "validFins");

    // comparaCamp(errors, idlogOn, obj.idlogOn, "idlogOn")

    // comparaCamp(errors, qcCompliance,obj.qcCompliance,"qcCompliance");

    // comparaCamp(errors, qcSSCD,obj.qcSSCD,"qcSSCD");

    if (errors.size() == 0) {
      return null;
    } else {
      return errors.toArray(new String[errors.size()]);
    }

  }

  private String comparaCamp(ArrayList<String> errors, Object expected, Object generated,
      String fieldName) {
    if (expected == null) {
      if (generated != null) {
        errors.add("El valor del camp " + fieldName + " és diferent EG [" + expected + " | "
            + generated + "]");
      }
    } else if (!expected.equals(generated)) {
      errors.add("El valor del camp " + fieldName + " és diferent EG [" + expected + " | "
          + generated + "]");
    }
    return null;
  }
  
  
  private String comparaCamp(ArrayList<String> errors, String expected, String generated,
      String fieldName) {
    if (expected == null) {
      if (generated != null) {
        errors.add("El valor del camp " + fieldName + " és diferent EG [" + expected + " | "
            + generated + "]");
      }
    } else if (!expected.equalsIgnoreCase(generated)) {
      errors.add("El valor del camp " + fieldName + " és diferent EG [" + expected + " | "
          + generated + "]");
    }
    return null;
  }

}
