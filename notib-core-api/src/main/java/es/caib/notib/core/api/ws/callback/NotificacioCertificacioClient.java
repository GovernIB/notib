package es.caib.notib.core.api.ws.callback;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Informació sobre l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect
public class NotificacioCertificacioClient {

	private CertificacioTipusEnum tipus;
	private CertificacioArxiuTipusEnum arxiuTipus;
	private String arxiuContingut;
	private String numSeguiment;
	private Date dataActualitzacio;

	public NotificacioCertificacioClient() {}

	public NotificacioCertificacioClient(
			CertificacioTipusEnum tipus, 
			CertificacioArxiuTipusEnum arxiuTipus,
			String arxiuContingut, 
			String numSeguiment, 
			Date dataActualitzacio) {
		
		super();
		this.tipus = tipus;
		this.arxiuTipus = arxiuTipus;
		this.arxiuContingut = arxiuContingut;
		this.numSeguiment = numSeguiment;
		this.dataActualitzacio = dataActualitzacio;
	}

	public CertificacioTipusEnum getTipus() {
		return tipus;
	}
	public void setTipus(CertificacioTipusEnum tipus) {
		this.tipus = tipus;
	}
	public CertificacioArxiuTipusEnum getArxiuTipus() {
		return arxiuTipus;
	}
	public void setArxiuTipus(CertificacioArxiuTipusEnum arxiuTipus) {
		this.arxiuTipus = arxiuTipus;
	}
	public String getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(String arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}
	public String getNumSeguiment() {
		return numSeguiment;
	}
	public void setNumSeguiment(String numSeguiment) {
		this.numSeguiment = numSeguiment;
	}
	public Date getDataActualitzacio() {
		return dataActualitzacio;
	}
	public void setDataActualitzacio(Date dataActualitzacio) {
		this.dataActualitzacio = dataActualitzacio;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}