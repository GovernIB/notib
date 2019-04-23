/**
 * 
 */
package es.caib.notib.core.api.dto;
import java.util.Date;
import java.util.List;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamentDtoV2 extends AuditoriaDto {

	private Long id;
	//private NotificacioDtoV2 notificacio;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipusEnumDto serveiTipus;
	private String titularNomLlinatge;
	private String destinatarisNomLlinatges;
	private String titularEmail;
	private String titularNif;
	private String usuari;
	private String referencia;
	private String notificaIdentificador;
	private String numeroCertCorreus;
	private String csv;
	private String uuid;
	private String notificaCertificacioNumSeguiment;
	private String detalls;
	private String codiNotibEnviament;
	private String notificaDataCaducitat;
	private Date caducitat;
	
	//Info notificació
	private Date enviamentDataProgramada;
	private String procedimentCodiNotib;
	private String grupCodi;
	private String emisorDir3Codi;
	private String usuariCodi;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private String llibre;
	private int registreNumero;
	private Date registreData;
	private NotificacioEstatEnumDto estat;
	private Long notificacioId;
	private String csvUuid;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private String registreNumeroFormatat;
	private NotificacioRegistreEstatEnumDto registreEstat;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	//public NotificacioDtoV2 getNotificacio() {
	//	return notificacio;
	//}
	//public void setNotificacio(NotificacioDtoV2 notificacio) {
	//	this.notificacio = notificacio;
	//}
	public List<PersonaDto> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaDto> destinataris) {
		this.destinataris = destinataris;
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
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setTitular(PersonaDto titular) {
		this.titular = titular;
	}
	public void setTitularNomLlinatge(String titularNomLlinatge) {
		this.titularNomLlinatge = titularNomLlinatge;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getReferencia() {
		return referencia;
	}
	public String getNotificaIdentificador() {
		return notificaIdentificador;
	}
	public void setNotificaIdentificador(String notificaCodi) {
		this.notificaIdentificador = notificaCodi;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getTitularEmail() {
		return titularEmail;
	}
	public void setTitularEmail(String titularEmail) {
		this.titularEmail = titularEmail;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public String getDestinatarisNomLlinatges() {
		destinatarisNomLlinatges = "";
		for(PersonaDto destinatari: destinataris) {
			destinatarisNomLlinatges += concatenarNomLlinatges(llinatgesDestinatari(destinatari), destinatari.getNom(), destinatari.getRaoSocial(), null)+"</br>";
		}
		return destinatarisNomLlinatges;
	}
	public void setDestinatarisNomLlinatges(String destinatarisNomLlinatge) {
		this.destinatarisNomLlinatges = destinatarisNomLlinatge;
	}
	public String getNumeroCertCorreus() {
		return numeroCertCorreus;
	}
	public void setNumeroCertCorreus(String numeroCertCorreus) {
		this.numeroCertCorreus = numeroCertCorreus;
	}
	public String getCodiNotibEnviament() {
		return codiNotibEnviament;
	}
	public void setCodiNotibEnviament(String codiNotibEnviament) {
		this.codiNotibEnviament = codiNotibEnviament;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public String getProcedimentCodiNotib() {
		return procedimentCodiNotib;
	}
	public void setProcedimentCodiNotib(String procedimentCodiNotib) {
		this.procedimentCodiNotib = procedimentCodiNotib;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
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
	public String getLlibre() {
		return llibre;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	public int getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(int registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getCsvUuid() {
		return csvUuid;
	}
	public void setCsvUuid(String csvUuid) {
		this.csvUuid = csvUuid;
	}
	public NotificacioComunicacioTipusEnumDto getComunicacioTipus() {
		return comunicacioTipus;
	}
	public void setComunicacioTipus(NotificacioComunicacioTipusEnumDto comunicacioTipus) {
		this.comunicacioTipus = comunicacioTipus;
	}
	//public String getCsvUuid() {
	//	if(notificacio.getDocument().getUuid() != null) {
	//		this.setCsvUuid(notificacio.getDocument().getUuid());
	//	}
	//	if(notificacio.getDocument().getCsv() != null) {
	//		this.setCsvUuid(notificacio.getDocument().getCsv());
	//	}
	//	return this.csvUuid;
	//}
	//public void setCsvUuid(String csvUuid) {
	//	this.csvUuid = csvUuid;
	//}
	public String getTitularNomLlinatge() {
		if(this.titularNomLlinatge != null) {
			return this.titularNomLlinatge;
		}else {
			if(titular != null) {
				titularNomLlinatge = concatenarNomLlinatges(
						getTitularLlinatges(),
						titular.getNom(),
						titular.getRaoSocial(),
						null);
			}
			return titularNomLlinatge;
		}
	}
	
	public String getTitularLlinatges() {
		return concatenarLlinatges(
				titular.getLlinatge1(),
				titular.getLlinatge2());
	}
	
	public String llinatgesDestinatari(PersonaDto destinatari) {
		return concatenarLlinatges(
				destinatari.getLlinatge1(),
				destinatari.getLlinatge2());
	}
	
//	public String getTitular() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(titular.getNom());
//		String llinatges = getTitularLlinatges();
//		if (llinatges != null && !llinatges.isEmpty()) {
//			sb.append(" ");
//			sb.append(llinatges);
//		}
//		sb.append(" (");
//		sb.append(titular.getNif());
//		sb.append(")");
//		return sb.toString();
//	}
	
	private String concatenarLlinatges(
			String llinatge1,
			String llinatge2) {
		if (llinatge1 == null && llinatge2 == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(llinatge1);
		if (llinatge2 != null && !llinatge2.isEmpty()) {
			sb.append(" ");
			sb.append(llinatge2);
		}
		return sb.toString();
	}
	
	public PersonaDto getTitular() {
		return titular;
	}
	private String concatenarNomLlinatges(
			String llinatges,
			String nom,
			String raoSocial,
			String destinatariNif) {
		StringBuilder sb = new StringBuilder();
		
		if (destinatariNif != null) {
			sb.append(destinatariNif);
			sb.append(" - ");
		}
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append("[");
			sb.append(llinatges);
		}
		
		if (nom != null && !nom.isEmpty()) {
			sb.append(", ");
			sb.append(nom);
			
			if (raoSocial == null) {
				sb.append("]");
			}
		}
		if (raoSocial != null && !raoSocial.isEmpty()) {
			sb.append(" | ");
			sb.append(raoSocial);
			sb.append("]");
		}
		return sb.toString();
	}

	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getNotificaCertificacioNumSeguiment() {
		return notificaCertificacioNumSeguiment;
	}
	public void setNotificaCertificacioNumSeguiment(String notificaCertificacioNumSeguiment) {
		this.notificaCertificacioNumSeguiment = notificaCertificacioNumSeguiment;
	}
	public String getDetalls() {
		return detalls;
	}
	public void setDetalls(String detalls) {
		this.detalls = detalls;
	}

	public Long getNotificacioId() {
		return notificacioId;
	}
	public void setNotificacioId(Long notificacioId) {
		this.notificacioId = notificacioId;
	}
	public String getNotificaDataCaducitat() {
		return notificaDataCaducitat;
	}
	public void setNotificaDataCaducitat(String notificaDataCaducitat) {
		this.notificaDataCaducitat = notificaDataCaducitat;
	}
	public String getRegistreNumeroFormatat() {
		return registreNumeroFormatat;
	}
	public void setRegistreNumeroFormatat(String registreNumeroFormatat) {
		this.registreNumeroFormatat = registreNumeroFormatat;
	}
	public NotificacioRegistreEstatEnumDto getRegistreEstat() {
		return registreEstat;
	}
	public void setRegistreEstat(NotificacioRegistreEstatEnumDto registreEstat) {
		this.registreEstat = registreEstat;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
