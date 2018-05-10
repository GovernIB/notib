/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioCommand {

	private Long id;
	@NotEmpty
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	@NotEmpty @Size(max=9)
	private Date enviamentDataProgramada;
	@NotEmpty @Size(max=50)
	private String concepte;
	@NotEmpty @Size(max=6)
	private String procedimentCodiSia;
	@NotEmpty @Size(max=256)
	private String procedimentDescripcioSia;
	@NotEmpty @Size(max=64)
	private String documentArxiuId;
	@NotEmpty @Size(max=20)
	private String documentSha1;
	@NotEmpty
	private boolean documentNormalitzat;
	@NotEmpty
	private boolean documentGenerarCsv;
	@NotEmpty @Size(max=64)
	private NotificacioEstatEnumDto estat;
	@NotEmpty @Size(max=64)
	private NotificacioEnviamentEstatEnumDto estatNotifica;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getProcedimentCodiSia() {
		return procedimentCodiSia;
	}
	public void setProcedimentCodiSia(String procedimentCodiSia) {
		this.procedimentCodiSia = procedimentCodiSia;
	}
	public String getProcedimentDescripcioSia() {
		return procedimentDescripcioSia;
	}
	public void setProcedimentDescripcioSia(String procedimentDescripcioSia) {
		this.procedimentDescripcioSia = procedimentDescripcioSia;
	}
	public String getDocumentArxiuId() {
		return documentArxiuId;
	}
	public void setDocumentArxiuId(String documentArxiuId) {
		this.documentArxiuId = documentArxiuId;
	}
	public String getDocumentSha1() {
		return documentSha1;
	}
	public void setDocumentSha1(String documentSha1) {
		this.documentSha1 = documentSha1;
	}
	public boolean isDocumentNormalitzat() {
		return documentNormalitzat;
	}
	public void setDocumentNormalitzat(boolean documentNormalitzat) {
		this.documentNormalitzat = documentNormalitzat;
	}
	public boolean isDocumentGenerarCsv() {
		return documentGenerarCsv;
	}
	public void setDocumentGenerarCsv(boolean documentGenerarCsv) {
		this.documentGenerarCsv = documentGenerarCsv;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public NotificacioEnviamentEstatEnumDto getEstatNotifica() {
		return estatNotifica;
	}
	public void setEstatNotifica(NotificacioEnviamentEstatEnumDto estatNotifica) {
		this.estatNotifica = estatNotifica;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
