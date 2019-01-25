/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

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
	private Date caducitat;
	private DocumentDto document;
	private String csv_uuid;
	private ProcedimentDto procediment;
	private String procedimentCodiNotib;
	private GrupDto Grup;
	private String grupCodi;
	private ParametresRegistreDto parametresRegistre;
	private NotificacioEstatEnumDto estat;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private ServeiTipusEnumDto serveiTipus;
	private PersonaDto titular;
	private PersonaDto destinatari = new PersonaDto();
	private List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
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
	public String getCsv_uuid() {
		return csv_uuid;
	}
	public void setCsv_uuid(String csv_uuid) {
		this.csv_uuid = csv_uuid;
	}
	public ProcedimentDto getProcediment() {
		return procediment;
	}
	public void setProcediment(ProcedimentDto procediment) {
		this.procediment = procediment;
	}
	public String getProcedimentCodiNotib() {
		return procedimentCodiNotib;
	}
	public void setProcedimentCodiNotib(String procedimentCodiNotib) {
		this.procedimentCodiNotib = procedimentCodiNotib;
	}
	public GrupDto getGrup() {
		return Grup;
	}
	public void setGrup(GrupDto grup) {
		Grup = grup;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public ParametresRegistreDto getParametresRegistre() {
		return parametresRegistre;
	}
	public void setParametresRegistre(ParametresRegistreDto parametresRegistre) {
		this.parametresRegistre = parametresRegistre;
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
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public PersonaDto getTitular() {
		return titular;
	}
	public void setTitular(PersonaDto titular) {
		this.titular = titular;
	}
	public PersonaDto getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(PersonaDto destinatari) {
		this.destinatari = destinatari;
	}
	public List<PersonaDto> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaDto> destinataris) {
		this.destinataris = destinataris;
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
