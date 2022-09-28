/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Informació de la resposta de Notifica a una consulta de certificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificaRespostaCertificacioDto extends NotificaRespostaDto {

	private String identificador;
	private String referenciaEmisor;
	private String titularNif;
	private NotificaCertificacioArxiuTipusEnumDto certificatTipus;
	private byte[] certificatContingut;
	private NotificaCertificacioTipusEnumDto certificacioTipus;
	private Date dataActualitzacio;
	private String numSeguiment;

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getReferenciaEmisor() {
		return referenciaEmisor;
	}
	public void setReferenciaEmisor(String referenciaEmisor) {
		this.referenciaEmisor = referenciaEmisor;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public NotificaCertificacioArxiuTipusEnumDto getCertificatTipus() {
		return certificatTipus;
	}
	public void setCertificatTipus(NotificaCertificacioArxiuTipusEnumDto certificatTipus) {
		this.certificatTipus = certificatTipus;
	}
	public byte[] getCertificatContingut() {
		return certificatContingut;
	}
	public void setCertificatContingut(byte[] certificatContingut) {
		this.certificatContingut = certificatContingut;
	}
	public NotificaCertificacioTipusEnumDto getCertificacioTipus() {
		return certificacioTipus;
	}
	public void setCertificacioTipus(NotificaCertificacioTipusEnumDto certificacioTipus) {
		this.certificacioTipus = certificacioTipus;
	}
	public Date getDataActualitzacio() {
		return dataActualitzacio;
	}
	public void setDataActualitzacio(Date dataActualitzacio) {
		this.dataActualitzacio = dataActualitzacio;
	}
	public String getNumSeguiment() {
		return numSeguiment;
	}
	public void setNumSeguiment(String numSeguiment) {
		this.numSeguiment = numSeguiment;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
