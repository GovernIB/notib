package es.caib.notib.plugin.certificate;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author anadal
 * 
 */
@XmlRootElement
public class ResultatValidacio {
  
  public static final int RESULTAT_VALIDACIO_OK = 0;

  private int resultatValidacioCodi;
  private String resultatValidacioDescripcio;

  private InformacioCertificat informacioCertificat = null;

  public int getResultatValidacioCodi() {
    return resultatValidacioCodi;
  }

  public void setResultatValidacioCodi(int resultatValidacioCodi) {
    this.resultatValidacioCodi = resultatValidacioCodi;
  }

  public String getResultatValidacioDescripcio() {
    return resultatValidacioDescripcio;
  }

  public void setResultatValidacioDescripcio(String resultatValidacioDescripcio) {
    this.resultatValidacioDescripcio = resultatValidacioDescripcio;
  }

  public InformacioCertificat getInformacioCertificat() {
    return informacioCertificat;
  }

  public void setInformacioCertificat(InformacioCertificat informacioCertificat) {
    this.informacioCertificat = informacioCertificat;
  }

  public String toString() {
    StringBuffer st = new StringBuffer();
    st.append("Resultat Validacio Codi: ").append(getResultatValidacioCodi()).append("\n");
    st.append("Resultat Validacio Desc.: ").append(getResultatValidacioDescripcio()).append("\n");
    if (getInformacioCertificat() != null) {
      st.append("\n").append(getInformacioCertificat());
    }
    return st.toString();
  }

}
