/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.ws.notificacio.Document;


/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioDtoV2 extends AuditoriaDto {

	private Long id;
	private String emisorDir3Codi;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private int retard;
	private Date caducitat;
	private DocumentDto document;
	private ProcedimentDto procediment;
	private GrupDto Grup;
	private NotificacioEstatEnumDto estat;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private EnviamentDto enviament;
	private boolean entregaPostalActiva;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public NotificacioComunicacioTipusEnumDto getComunicacioTipus() {
		return comunicacioTipus;
	}
	public void setComunicacioTipus(NotificacioComunicacioTipusEnumDto comunicacioTipus) {
		this.comunicacioTipus = comunicacioTipus;
	}
	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public int getRetard() {
		return retard;
	}
	public void setRetard(int retard) {
		this.retard = retard;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public DocumentDto getDocument() {
		return document;
	}
	public void setDocument(DocumentDto document) {
		this.document = document;
	}
	public ProcedimentDto getProcediment() {
		return procediment;
	}
	public void setProcediment(ProcedimentDto procediment) {
		this.procediment = procediment;
	}
	public GrupDto getGrup() {
		return Grup;
	}
	public void setGrup(GrupDto grup) {
		Grup = grup;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getNotificaErrorData() {
		return notificaErrorData;
	}
	public void setNotificaErrorData(Date notificaErrorData) {
		this.notificaErrorData = notificaErrorData;
	}
	public String getNotificaErrorDescripcio() {
		return notificaErrorDescripcio;
	}
	public void setNotificaErrorDescripcio(String notificaErrorDescripcio) {
		this.notificaErrorDescripcio = notificaErrorDescripcio;
	}
	public EnviamentDto getEnviament() {
		return enviament;
	}
	public void setEnviament(EnviamentDto enviament) {
		this.enviament = enviament;
	}
	public boolean isEntregaPostalActiva() {
		return entregaPostalActiva;
	}
	public void setEntregaPostalActiva(boolean entregaPostalActiva) {
		this.entregaPostalActiva = entregaPostalActiva;
	}
	public EntregaPostalDto getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(EntregaPostalDto entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaDehDto getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(EntregaDehDto entregaDeh) {
		this.entregaDeh = entregaDeh;
	}

	public boolean isNotificaError() {
		return notificaErrorData != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
