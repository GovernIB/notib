/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioDto extends AuditoriaDto {

	private Long id;
	private String cifEntitat;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private Date enviamentDataProgramada;
	private String concepte;
	private String pagadorCorreusCodiDir3;
	private String pagadorCorreusContracteNum;
	private String pagadorCorreusCodiClientFacturacio;
	private Date pagadorCorreusDataVigencia;
	private String pagadorCieCodiDir3;
	private Date pagadorCieDataVigencia;
	private String procedimentDescripcioSia;
	private String documentArxiuNom;
	private String documentArxiuId;
	private String csv_uuid;
	private String documentContingutBase64;
	private String documentSha1;
	private boolean documentNormalitzat;
	private boolean documentGenerarCsv;
	private String seuExpedientSerieDocumental;
	private String seuExpedientUnitatOrganitzativa;
	private String seuExpedientIdentificadorEni;
	private String seuExpedientTitol;
	private String seuRegistreOficina;
	private String seuRegistreLlibre;
	private String seuIdioma;
	private String seuAvisTitol;
	private String seuAvisText;
	private String seuAvisTextMobil;
	private String seuOficiTitol;
	private String seuOficiText;
	private String seuRegistreNumero;
	private NotificacioEstatEnumDto estat;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private EntitatDto entitat;
	private ProcedimentDto procediment;
	private String referenciaExterna;
	private String notificacio;
	private boolean permisProcessar;
	private String registreOrgan;
	private String registreOficina;
	private String registreLlibre;
	private String registreExtracte;
	private String registreTipusAssumpte;
	private String registreRefExterna;
	private String registreNumExpedient;
	private PagadorPostalDto pagadorPostal;
	private String usuariCodi;
	private String registreObservacions;
	private Date registreData;
	private Integer registreNumero;
	private DocumentDto document;
	private String descripcio;
	private List<EnviamentDto> enviaments;
	
	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualProcessar;
	private boolean usuariActualNotificacio;
	private boolean usuariActualAdministration;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCifEntitat() {
		return cifEntitat;
	}
	public void setCifEntitat(String cifEntitat) {
		this.cifEntitat = cifEntitat;
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
	public String getPagadorCorreusCodiDir3() {
		return pagadorCorreusCodiDir3;
	}
	public void setPagadorCorreusCodiDir3(String pagadorCorreusCodiDir3) {
		this.pagadorCorreusCodiDir3 = pagadorCorreusCodiDir3;
	}
	public String getPagadorCorreusContracteNum() {
		return pagadorCorreusContracteNum;
	}
	public void setPagadorCorreusContracteNum(String pagadorCorreusContracteNum) {
		this.pagadorCorreusContracteNum = pagadorCorreusContracteNum;
	}
	public String getPagadorCorreusCodiClientFacturacio() {
		return pagadorCorreusCodiClientFacturacio;
	}
	public void setPagadorCorreusCodiClientFacturacio(String pagadorCorreusCodiClientFacturacio) {
		this.pagadorCorreusCodiClientFacturacio = pagadorCorreusCodiClientFacturacio;
	}
	public Date getPagadorCorreusDataVigencia() {
		return pagadorCorreusDataVigencia;
	}
	public void setPagadorCorreusDataVigencia(Date pagadorCorreusDataVigencia) {
		this.pagadorCorreusDataVigencia = pagadorCorreusDataVigencia;
	}
	public String getPagadorCieCodiDir3() {
		return pagadorCieCodiDir3;
	}
	public void setPagadorCieCodiDir3(String pagadorCieCodiDir3) {
		this.pagadorCieCodiDir3 = pagadorCieCodiDir3;
	}
	public Date getPagadorCieDataVigencia() {
		return pagadorCieDataVigencia;
	}
	public void setPagadorCieDataVigencia(Date pagadorCieDataVigencia) {
		this.pagadorCieDataVigencia = pagadorCieDataVigencia;
	}
	public String getProcedimentDescripcioSia() {
		return procedimentDescripcioSia;
	}
	public void setProcedimentDescripcioSia(String procedimentDescripcioSia) {
		this.procedimentDescripcioSia = procedimentDescripcioSia;
	}
	public String getDocumentArxiuNom() {
		return documentArxiuNom;
	}
	public void setDocumentArxiuNom(String documentArxiuNom) {
		this.documentArxiuNom = documentArxiuNom;
	}
	public String getDocumentArxiuId() {
		return documentArxiuId;
	}
	public void setDocumentArxiuId(String documentArxiuId) {
		this.documentArxiuId = documentArxiuId;
	}
	public String getCsv_uuid() {
		return csv_uuid;
	}
	public void setCsv_uuid(String csv_uuid) {
		this.csv_uuid = csv_uuid;
	}
	public String getDocumentContingutBase64() {
		return documentContingutBase64;
	}
	public void setDocumentContingutBase64(String documentContingutBase64) {
		this.documentContingutBase64 = documentContingutBase64;
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
	public String getSeuExpedientSerieDocumental() {
		return seuExpedientSerieDocumental;
	}
	public void setSeuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
		this.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
	}
	public String getSeuExpedientUnitatOrganitzativa() {
		return seuExpedientUnitatOrganitzativa;
	}
	public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
		this.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
	}
	public String getSeuExpedientIdentificadorEni() {
		return seuExpedientIdentificadorEni;
	}
	public void setSeuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
		this.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
	}
	public String getSeuExpedientTitol() {
		return seuExpedientTitol;
	}
	public void setSeuExpedientTitol(String seuExpedientTitol) {
		this.seuExpedientTitol = seuExpedientTitol;
	}
	public String getSeuRegistreOficina() {
		return seuRegistreOficina;
	}
	public void setSeuRegistreOficina(String seuRegistreOficina) {
		this.seuRegistreOficina = seuRegistreOficina;
	}
	public String getSeuRegistreLlibre() {
		return seuRegistreLlibre;
	}
	public void setSeuRegistreLlibre(String seuRegistreLlibre) {
		this.seuRegistreLlibre = seuRegistreLlibre;
	}
	public String getSeuIdioma() {
		return seuIdioma;
	}
	public void setSeuIdioma(String seuIdioma) {
		this.seuIdioma = seuIdioma;
	}
	public String getSeuAvisTitol() {
		return seuAvisTitol;
	}
	public void setSeuAvisTitol(String seuAvisTitol) {
		this.seuAvisTitol = seuAvisTitol;
	}
	public String getSeuAvisText() {
		return seuAvisText;
	}
	public void setSeuAvisText(String seuAvisText) {
		this.seuAvisText = seuAvisText;
	}
	public String getSeuAvisTextMobil() {
		return seuAvisTextMobil;
	}
	public void setSeuAvisTextMobil(String seuAvisTextMobil) {
		this.seuAvisTextMobil = seuAvisTextMobil;
	}
	public String getSeuOficiTitol() {
		return seuOficiTitol;
	}
	public void setSeuOficiTitol(String seuOficiTitol) {
		this.seuOficiTitol = seuOficiTitol;
	}
	public String getSeuOficiText() {
		return seuOficiText;
	}
	public void setSeuOficiText(String seuOficiText) {
		this.seuOficiText = seuOficiText;
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
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public boolean isNotificaError() {
		return notificaErrorData != null;
	}
	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public boolean isUsuariActualProcessar() {
		return usuariActualProcessar;
	}
	public boolean isUsuariActualNotificacio() {
		return usuariActualNotificacio;
	}
	public boolean isUsuariActualAdministration() {
		return usuariActualAdministration;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
	}
	public void setUsuariActualProcessar(boolean usuariActualProcessar) {
		this.usuariActualProcessar = usuariActualProcessar;
	}
	public void setUsuariActualNotificacio(boolean usuariActualNotificacio) {
		this.usuariActualNotificacio = usuariActualNotificacio;
	}
	public void setUsuariActualAdministration(boolean usuariActualAdministration) {
		this.usuariActualAdministration = usuariActualAdministration;
	}
	public String getReferenciaExterna() {
		return referenciaExterna;
	}
	public void setReferenciaExterna(String referenciaExterna) {
		this.referenciaExterna = referenciaExterna;
	}
	public String getSeuRegistreNumero() {
		return seuRegistreNumero;
	}
	public void setSeuRegistreNumero(String seuRegistreNumero) {
		this.seuRegistreNumero = seuRegistreNumero;
	}
	public String getNotificacio() {
		return notificacio;
	}
	public void setNotificacio(String notificacio) {
		this.notificacio = notificacio;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public boolean isPermisProcessar() {
		return permisProcessar;
	}
	public void setPermisProcessar(boolean permisProcessar) {
		this.permisProcessar = permisProcessar;
	}
	public ProcedimentDto getProcediment() {
		return procediment;
	}
	public void setProcediment(ProcedimentDto procediment) {
		this.procediment = procediment;
	}
	public String getRegistreOrgan() {
		return registreOrgan;
	}
	public void setRegistreOrgan(String registreOrgan) {
		this.registreOrgan = registreOrgan;
	}
	public String getRegistreExtracte() {
		return registreExtracte;
	}
	public void setRegistreExtracte(String registreExtracte) {
		this.registreExtracte = registreExtracte;
	}
	public String getRegistreTipusAssumpte() {
		return registreTipusAssumpte;
	}
	public void setRegistreTipusAssumpte(String registreTipusAssumpte) {
		this.registreTipusAssumpte = registreTipusAssumpte;
	}
	public String getRegistreRefExterna() {
		return registreRefExterna;
	}
	public void setRegistreRefExterna(String registreRefExterna) {
		this.registreRefExterna = registreRefExterna;
	}
	public String getRegistreNumExpedient() {
		return registreNumExpedient;
	}
	public void setRegistreNumExpedient(String registreNumExpedient) {
		this.registreNumExpedient = registreNumExpedient;
	}
	public PagadorPostalDto getPagadorPostal() {
		return pagadorPostal;
	}
	public void setPagadorPostal(PagadorPostalDto pagadorPostal) {
		this.pagadorPostal = pagadorPostal;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getRegistreObservacions() {
		return registreObservacions;
	}
	public void setRegistreObservacions(String registreObservacions) {
		this.registreObservacions = registreObservacions;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public Integer getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(Integer registreNumero) {
		this.registreNumero = registreNumero;
	}
	public DocumentDto getDocument() {
		return document;
	}
	public void setDocument(DocumentDto document) {
		this.document = document;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public List<EnviamentDto> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<EnviamentDto> enviaments) {
		this.enviaments = enviaments;
	}
	public String getRegistreOficina() {
		return registreOficina;
	}
	public void setRegistreOficina(String registreOficina) {
		this.registreOficina = registreOficina;
	}
	public String getRegistreLlibre() {
		return registreLlibre;
	}
	public void setRegistreLlibre(String registreLlibre) {
		this.registreLlibre = registreLlibre;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
