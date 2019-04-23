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
	private Date enviamentDataProgramada;
	private Integer retard;
	protected int notificaEnviamentIntent;
	private Date caducitat;
	private DocumentDto document;
	private String csv_uuid;
	private ProcedimentDto procediment;
	private String procedimentCodiNotib;
	private GrupDto grup;
	private String grupCodi;
	private ParametresRegistreDto parametresRegistre;
	private NotificacioEstatEnumDto estat;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private String serveiTipus;
	private List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<NotificacioEnviamentDtoV2>();
	private String usuariCodi;
	private String motiu;
	// Paràmetres registre
	private String oficina;
	private String organ;
	private String llibre;
	private String registreNumero;
	private Date registreData;
	private String extracte;
	private RegistreDocumentacioFisicaEnumDto docFisica;
	private IdiomaEnumDto idioma;
	private String tipusAssumpte;
	private String numExpedient;
	private String refExterna;
	private String codiAssumpte;
	private String observacions;
	
	
	
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
		return grup;
	}
	public void setGrup(GrupDto grup) {
		this.grup = grup;
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
	public String getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(String serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getLlibre() {
		return llibre;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public String getOficina() {
		return oficina;
	}
	public String getExtracte() {
		return extracte;
	}
	public RegistreDocumentacioFisicaEnumDto getDocFisica() {
		return docFisica;
	}
	public IdiomaEnumDto getIdioma() {
		return idioma;
	}
	public String getTipusAssumpte() {
		return tipusAssumpte;
	}
	public String getRefExterna() {
		return refExterna;
	}
	public String getCodiAssumpte() {
		return codiAssumpte;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public void setDocFisica(RegistreDocumentacioFisicaEnumDto docFisica) {
		this.docFisica = docFisica;
	}
	public void setIdioma(IdiomaEnumDto idioma) {
		this.idioma = idioma;
	}
	public void setTipusAssumpte(String tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
	public String getNumExpedient() {
		return numExpedient;
	}
	public void setNumExpedient(String numExpedient) {
		this.numExpedient = numExpedient;
	}
	public void setRefExterna(String refExterna) {
		this.refExterna = refExterna;
	}
	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public boolean isNotificaError() {
		return notificaErrorData != null;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public List<NotificacioEnviamentDtoV2> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<NotificacioEnviamentDtoV2> enviaments) {
		this.enviaments = enviaments;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public int getNotificaEnviamentIntent() {
		return notificaEnviamentIntent;
	}
	public void setNotificaEnviamentIntent(int notificaEnviamentIntent) {
		this.notificaEnviamentIntent = notificaEnviamentIntent;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
