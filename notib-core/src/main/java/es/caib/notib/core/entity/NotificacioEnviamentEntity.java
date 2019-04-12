/**
 * 
 */
package es.caib.notib.core.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa els enviaments d'una
 * notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_notificacio_env")
@SecondaryTable(name="not_notificacio")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEnviamentEntity extends NotibAuditable<Long> {


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "notificacio_id")
	@ForeignKey(name = "NOT_NOTIFICACIO_NOTENV_FK")
	@NotFound(action = NotFoundAction.IGNORE)
	protected NotificacioEntity notificacio;
	
	@Column(name="notificacio_id", insertable=false, updatable=false)
	protected Long notificacioId;
	
	/* Titular */
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "titular_id")
	@ForeignKey(name = "not_persona_notificacio_env_fk")
	protected PersonaEntity titular;
	
	/* Destinataris */
	@OneToMany(fetch = FetchType.EAGER)
	@ForeignKey(name = "not_persona_not_fk")
    @JoinColumn(name = "notificacio_env_id") // we need to duplicate the physical information
	@NotFound(action = NotFoundAction.IGNORE)
	protected List<PersonaEntity> destinataris;
	
	/* Domicili */
	@Column(name = "dom_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliTipusEnumDto domiciliTipus;
	
	@Column(name = "dom_con_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus;
	
	@Column(name = "dom_via_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliViaTipusEnumDto domiciliViaTipus;
	
	@Column(name = "dom_via_nom", length = 100)
	protected String domiciliViaNom;
	@Column(name = "dom_num_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;
	
	@Column(name = "dom_num_num", length = 10)
	protected String domiciliNumeracioNumero;
	
	@Column(name = "dom_num_qualif", length = 3)
	protected String domiciliNumeracioQualificador;
	
	@Column(name = "dom_num_puntkm", length = 10)
	protected String domiciliNumeracioPuntKm;
	
	@Column(name = "dom_apartat", length = 10)
	protected String domiciliApartatCorreus;
	
	@Column(name = "dom_bloc", length = 50)
	protected String domiciliBloc;
	
	@Column(name = "dom_portal", length = 50)
	protected String domiciliPortal;
	
	@Column(name = "dom_escala", length = 50)
	protected String domiciliEscala;
	
	@Column(name = "dom_planta", length = 50)
	protected String domiciliPlanta;
	
	@Column(name = "dom_porta", length = 50)
	protected String domiciliPorta;
	
	@Column(name = "dom_complem", length = 250)
	protected String domiciliComplement;
	
	@Column(name = "dom_poblacio", length = 30)
	protected String domiciliPoblacio;
	
	@Column(name = "dom_mun_codine", length = 6)
	protected String domiciliMunicipiCodiIne;
	
	@Column(name = "dom_mun_nom", length = 64)
	protected String domiciliMunicipiNom;
	
	@Column(name = "dom_codi_postal", length = 10)
	protected String domiciliCodiPostal;
	
	@Column(name = "dom_prv_codi", length = 2)
	protected String domiciliProvinciaCodi;
	
	@Column(name = "dom_prv_nom", length = 64)
	protected String domiciliProvinciaNom;
	
	@Column(name = "dom_pai_codiso", length = 3)
	protected String domiciliPaisCodiIso; // ISO-3166
	
	@Column(name = "dom_pai_nom", length = 64)
	protected String domiciliPaisNom;
	
	@Column(name = "dom_linea1", length = 50)
	protected String domiciliLinea1;
	
	@Column(name = "dom_linea2", length = 50)
	protected String domiciliLinea2;
	
	@Column(name = "dom_cie")
	protected Integer domiciliCie;
	
	/* DEH */
	@Column(name = "deh_obligat")
	
	protected Boolean dehObligat;
	@Column(name = "deh_nif", length = 9)
	
	protected String dehNif;
	@Column(name = "deh_proc_codi", length = 6)
	
	protected String dehProcedimentCodi;
	/* Altres */
	
	@Column(name = "servei_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected ServeiTipusEnumDto serveiTipus;
	
	@Column(name = "format_sobre", length = 10)
	protected String formatSobre;
	
	@Column(name = "format_fulla", length = 10)
	protected String formatFulla;
	
	/* Notifica informació */
	@Column(name = "notifica_ref", length = 20)
	protected String notificaReferencia;
	
	@Column(name = "notifica_id", length = 20)
	protected String notificaIdentificador;
	
	@Column(name = "notifica_datcre")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaDataCreacio;
	
	@Column(name = "notifica_datdisp")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaDataDisposicio;
	
	@Column(name = "notifica_datcad")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaDataCaducitat;
	
	@Column(name = "notifica_emi_dir3codi", length = 9)
	protected String notificaEmisorDir3;
	
	@Column(name = "notifica_emi_dir3desc", length = 100)
	protected String notificaEmisorDescripcio;
	
	@Column(name = "notifica_emi_dir3nif", length = 9)
	protected String notificaEmisorNif;
	
	@Column(name = "notifica_arr_dir3codi", length = 9)
	protected String notificaArrelDir3;
	
	@Column(name = "notifica_arr_dir3desc", length = 100)
	protected String notificaArrelDescripcio;
	
	@Column(name = "notifica_arr_dir3nif", length = 9)
	protected String notificaArrelNif;
	
	/* Notifica estat i datat */
	@Column(name = "notifica_estat", nullable = false)
	protected NotificacioEnviamentEstatEnumDto notificaEstat;
	
	@Column(name = "notifica_estat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaEstatData;
	
	@Column(name = "notifica_estat_dataact")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaEstatDataActualitzacio;
	
	@Column(name = "notifica_estat_final")
	protected boolean notificaEstatFinal;
	
	@Column(name = "notifica_estat_desc", length = 255)
	protected String notificaEstatDescripcio;
	
	@Column(name = "notifica_datat_origen", length = 20)
	protected String notificaDatatOrigen;
	
	@Column(name = "notifica_datat_recnif", length = 9)
	protected String notificaDatatReceptorNif;
	
	@Column(name = "notifica_datat_recnom", length = 100)
	protected String notificaDatatReceptorNom;
	
	@Column(name = "notifica_datat_numseg", length = 50)
	protected String notificaDatatNumSeguiment;
	
	@Column(name = "notifica_datat_errdes", length = 255)
	protected String notificaDatatErrorDescripcio;
	
	/* Notifica certificació */
	@Column(name = "notifica_cer_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaCertificacioData;
	
	@Column(name = "notifica_cer_arxiuid", length = 50)
	protected String notificaCertificacioArxiuId;
	
	@Column(name = "notifica_cer_hash", length = 50)
	protected String notificaCertificacioHash;
	
	@Column(name = "notifica_cer_origen", length = 20)
	protected String notificaCertificacioOrigen;
	
	@Column(name = "notifica_cer_metas", length = 255)
	protected String notificaCertificacioMetadades;
	
	@Column(name = "notifica_cer_csv", length = 50)
	protected String notificaCertificacioCsv;
	
	@Column(name = "notifica_cer_mime", length = 20)
	protected String notificaCertificacioMime;
	
	@Column(name = "notifica_cer_tamany", length = 20)
	protected Integer notificaCertificacioTamany;
	
	@Column(name = "notifica_cer_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	
	@Column(name = "notifica_cer_arxtip")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	
	@Column(name = "notifica_cer_numseg", length = 50)
	protected String notificaCertificacioNumSeguiment;
	
	/* Notifica error */
	@Column(name = "notifica_error", nullable = false)
	protected boolean notificaError;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "notifica_error_event_id")
	@ForeignKey(name = "not_noteve_noterr_notdest_fk")
	protected NotificacioEventEntity notificaErrorEvent;
	
	@Column(name = "notifica_intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaIntentData;
	@Column(name = "intent_num")
	protected int intentNum;


	
	@Column(name="proc_codi_notib", table = "not_notificacio")
	protected String procedimentCodiNotib;
	@Column(name="env_data_prog", table = "not_notificacio")
	private Date enviamentDataProgramada;
	@Column(name="grup_codi", table = "not_notificacio")
	private String grupCodi;
	@Column(name="emisor_dir3codi", table = "not_notificacio")
	private String emisorDir3Codi;
	@Column(name="usuari_codi", table = "not_notificacio")
	private String usuariCodi;
	@Column(name="env_tipus", table = "not_notificacio")
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	@Column(name="concepte", table = "not_notificacio")
	private String concepte;
	@Column(name="descripcio", table = "not_notificacio")
	private String descripcio;
	@Column(name="registre_llibre", table = "not_notificacio")
	private String llibre;
	@Column(name="registre_numero", table = "not_notificacio")
	private Integer registreNumero;
	@Column(name="registre_data", table = "not_notificacio")
	private Date registreData;
	@Column(name="estat", table = "not_notificacio")
	private NotificacioEstatEnumDto estat;
	@Column(name = "com_tipus", table = "not_notificacio")
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	
	@Transient
	private String csvUuid;
	
	public String getProcedimentCodiNotib() {
		return procedimentCodiNotib;
	}
	public void setProcedimentCodiNotib(String procedimentCodiNotib) {
		this.procedimentCodiNotib = procedimentCodiNotib;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
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
	public Integer getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(Integer registreNumero) {
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
	public NotificaDomiciliTipusEnumDto getDomiciliTipus() {
		return domiciliTipus;
	}
	public List<PersonaEntity> getDestinataris() {
		return destinataris;
	}
	public int getIntentNum() {
		return intentNum;
	}
	public PersonaEntity getTitular() {
		return titular;
	}
	public void setTitular(PersonaEntity titular) {
		this.titular = titular;
	}
	public NotificaDomiciliConcretTipusEnumDto getDomiciliConcretTipus() {
		return domiciliConcretTipus;
	}
	public NotificaDomiciliViaTipusEnumDto getDomiciliViaTipus() {
		return domiciliViaTipus;
	}
	public String getDomiciliViaNom() {
		return domiciliViaNom;
	}
	public NotificaDomiciliNumeracioTipusEnumDto getDomiciliNumeracioTipus() {
		return domiciliNumeracioTipus;
	}
	public String getDomiciliNumeracioNumero() {
		return domiciliNumeracioNumero;
	}
	public String getDomiciliNumeracioQualificador() {
		return domiciliNumeracioQualificador;
	}
	public String getDomiciliNumeracioPuntKm() {
		return domiciliNumeracioPuntKm;
	}
	public String getDomiciliApartatCorreus() {
		return domiciliApartatCorreus;
	}
	public String getDomiciliBloc() {
		return domiciliBloc;
	}
	public String getDomiciliPortal() {
		return domiciliPortal;
	}
	public String getDomiciliEscala() {
		return domiciliEscala;
	}
	public String getDomiciliPlanta() {
		return domiciliPlanta;
	}
	public String getDomiciliPorta() {
		return domiciliPorta;
	}
	public String getDomiciliComplement() {
		return domiciliComplement;
	}
	public String getDomiciliPoblacio() {
		return domiciliPoblacio;
	}
	public String getDomiciliMunicipiCodiIne() {
		return domiciliMunicipiCodiIne;
	}
	public String getDomiciliMunicipiNom() {
		return domiciliMunicipiNom;
	}
	public String getDomiciliCodiPostal() {
		return domiciliCodiPostal;
	}
	public String getDomiciliProvinciaCodi() {
		return domiciliProvinciaCodi;
	}
	public String getDomiciliProvinciaNom() {
		return domiciliProvinciaNom;
	}
	public String getDomiciliPaisCodiIso() {
		return domiciliPaisCodiIso;
	}
	public String getDomiciliPaisNom() {
		return domiciliPaisNom;
	}
	public String getDomiciliLinea1() {
		return domiciliLinea1;
	}
	public String getDomiciliLinea2() {
		return domiciliLinea2;
	}
	public Integer getDomiciliCie() {
		return domiciliCie;
	}
	public Boolean getDehObligat() {
		return dehObligat;
	}
	public String getDehNif() {
		return dehNif;
	}
	public String getDehProcedimentCodi() {
		return dehProcedimentCodi;
	}
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public String getFormatSobre() {
		return formatSobre;
	}
	public String getFormatFulla() {
		return formatFulla;
	}
	public String getNotificaReferencia() {
		return notificaReferencia;
	}
	public String getNotificaIdentificador() {
		return notificaIdentificador;
	}
	public Date getNotificaDataCreacio() {
		return notificaDataCreacio;
	}
	public Date getNotificaDataDisposicio() {
		return notificaDataDisposicio;
	}
	public Date getNotificaDataCaducitat() {
		return notificaDataCaducitat;
	}
	public String getNotificaEmisorDir3() {
		return notificaEmisorDir3;
	}
	public String getNotificaEmisorDescripcio() {
		return notificaEmisorDescripcio;
	}
	public String getNotificaEmisorNif() {
		return notificaEmisorNif;
	}
	public String getNotificaArrelDir3() {
		return notificaArrelDir3;
	}
	public String getNotificaArrelDescripcio() {
		return notificaArrelDescripcio;
	}
	public String getNotificaArrelNif() {
		return notificaArrelNif;
	}
	public NotificacioEnviamentEstatEnumDto getNotificaEstat() {
		return notificaEstat;
	}
	public Date getNotificaEstatData() {
		return notificaEstatData;
	}
	public Date getNotificaEstatDataActualitzacio() {
		return notificaEstatDataActualitzacio;
	}
	public boolean isNotificaEstatFinal() {
		return notificaEstatFinal;
	}
	public String getNotificaEstatDescripcio() {
		return notificaEstatDescripcio;
	}
	public String getNotificaDatatOrigen() {
		return notificaDatatOrigen;
	}
	public String getNotificaDatatReceptorNif() {
		return notificaDatatReceptorNif;
	}
	public String getNotificaDatatReceptorNom() {
		return notificaDatatReceptorNom;
	}
	public String getNotificaDatatNumSeguiment() {
		return notificaDatatNumSeguiment;
	}
	public String getNotificaDatatErrorDescripcio() {
		return notificaDatatErrorDescripcio;
	}
	public Date getNotificaCertificacioData() {
		return notificaCertificacioData;
	}
	public String getNotificaCertificacioArxiuId() {
		return notificaCertificacioArxiuId;
	}
	public String getNotificaCertificacioHash() {
		return notificaCertificacioHash;
	}
	public String getNotificaCertificacioOrigen() {
		return notificaCertificacioOrigen;
	}
	public String getNotificaCertificacioMetadades() {
		return notificaCertificacioMetadades;
	}
	public String getNotificaCertificacioCsv() {
		return notificaCertificacioCsv;
	}
	public String getNotificaCertificacioMime() {
		return notificaCertificacioMime;
	}
	public Integer getNotificaCertificacioTamany() {
		return notificaCertificacioTamany;
	}
	public NotificaCertificacioTipusEnumDto getNotificaCertificacioTipus() {
		return notificaCertificacioTipus;
	}
	public NotificaCertificacioArxiuTipusEnumDto getNotificaCertificacioArxiuTipus() {
		return notificaCertificacioArxiuTipus;
	}
	public String getNotificaCertificacioNumSeguiment() {
		return notificaCertificacioNumSeguiment;
	}
	public boolean isNotificaError() {
		return notificaError;
	}
	public NotificacioEventEntity getNotificaErrorEvent() {
		return notificaErrorEvent;
	}
	public Date getNotificaIntentData() {
		return notificaIntentData;
	}
	public NotificacioEntity getNotificacio() {
		return notificacio;
	}
	public void setNotificacio(NotificacioEntity notificacio) {
		this.notificacio = notificacio;
	}
	public Long getNotificacioId() {
		return notificacioId;
	}
	public void updateNotificaReferencia(
			String notificaReferencia) {
		this.notificaReferencia = notificaReferencia;
	}
	public void updateNotificaIdentificador(
			String notificaidentificador) {
		this.notificaIdentificador = notificaidentificador;
	}
	public void setDehObligat(Boolean dehObligat) {
		this.dehObligat = dehObligat;
	}
	public void updateNotificaEnviada(
			String notificaIdentificador) {
		this.notificaIdentificador = notificaIdentificador;
		this.notificaEstatData = new Date();
		this.notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIB_ENVIADA;
		this.notificaError = false;
		this.notificaErrorEvent = null;
		this.notificaIntentData = new Date();
		this.notificaEstatDataActualitzacio = new Date();
	}
	public void updateNotificaInformacio(
			Date notificaDataCreacio,
			Date notificaDataDisposicio,
			Date notificaDataCaducitat,
			String notificaEmisorDir3,
			String notificaEmisorDescripcio,
			String notificaEmisorNif,
			String notificaArrelDir3,
			String notificaArrelDescripcio,
			String notificaArrelNif) {
		this.notificaDataCreacio = notificaDataCreacio;
		this.notificaDataDisposicio = notificaDataDisposicio;
		this.notificaDataCaducitat = notificaDataCaducitat;
		this.notificaEmisorDir3 = notificaEmisorDir3;
		this.notificaEmisorDescripcio = notificaEmisorDescripcio;
		this.notificaEmisorNif = notificaEmisorNif;
		this.notificaArrelDir3 = notificaArrelDir3;
		this.notificaArrelDescripcio = notificaArrelDescripcio;
		this.notificaArrelNif = notificaArrelNif;
	}
	public void updateNotificaDatat(
			NotificacioEnviamentEstatEnumDto notificaEstat,
			Date notificaEstatData,
			boolean notificaEstatFinal,
			String notificaEstatDescripcio,
			String notificaDatatOrigen,
			String notificaDatatReceptorNif,
			String notificaDatatReceptorNom,
			String notificaDatatNumSeguiment,
			String notificaDatatErrorDescripcio) {
		this.notificaEstat = notificaEstat;
		this.notificaEstatData = notificaEstatData;
		this.notificaEstatFinal = notificaEstatFinal;
		this.notificaEstatDescripcio = notificaEstatDescripcio;
		this.notificaDatatOrigen = notificaDatatOrigen;
		this.notificaDatatReceptorNif = notificaDatatReceptorNif;
		this.notificaDatatReceptorNom = notificaDatatReceptorNom;
		this.notificaDatatNumSeguiment = notificaDatatNumSeguiment;
		this.notificaDatatErrorDescripcio = notificaDatatErrorDescripcio;
		this.notificaEstatDataActualitzacio = new Date();
	}
	public void updateNotificaCertificacio(
			Date notificaCertificacioData,
			String notificaCertificacioArxiuId,
			String notificaCertificacioHash,
			String notificaCertificacioOrigen,
			String notificaCertificacioMetadades,
			String notificaCertificacioCsv,
			String notificaCertificacioMime,
			Integer notificaCertificacioTamany,
			NotificaCertificacioTipusEnumDto notificaCertificacioTipus, // acuse o sobre
			NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus,
			String notificaCertificacioNumSeguiment) {
		this.notificaCertificacioData = notificaCertificacioData;
		this.notificaCertificacioArxiuId = notificaCertificacioArxiuId;
		this.notificaCertificacioHash = notificaCertificacioHash;
		this.notificaCertificacioOrigen = notificaCertificacioOrigen;
		this.notificaCertificacioMetadades = notificaCertificacioMetadades;
		this.notificaCertificacioCsv = notificaCertificacioCsv;
		this.notificaCertificacioMime = notificaCertificacioMime;
		this.notificaCertificacioTamany = notificaCertificacioTamany;
		this.notificaCertificacioTipus = notificaCertificacioTipus;
		this.notificaCertificacioArxiuTipus = notificaCertificacioArxiuTipus;
		this.notificaCertificacioNumSeguiment = notificaCertificacioNumSeguiment;
	}
	public void updateNotificaError(
			boolean notificaError,
			NotificacioEventEntity notificaErrorEvent) {
		this.notificaError = notificaError;
		this.notificaErrorEvent = notificaErrorEvent;
	}
	/*
	public void updateSeuEnviament(
			String seuRegistreNumero,
			Date seuRegistreData,
			SeuEstatEnumDto seuEstat) {
		this.seuRegistreNumero = seuRegistreNumero;
		this.seuRegistreData = seuRegistreData;
		this.seuEstat = seuEstat;
		
		if (this.seuRegistreNumero != null && SeuEstatEnumDto.ENVIADA.equals(this.seuEstat)) {
			this.seuIntentData = new Date();
			this.intentNum = 0;
		}
	}
	public void updateSeuEstat(
			Date seuDataFi,
			SeuEstatEnumDto seuEstat) {
		this.seuDataEstat = new Date();
		this.seuDataFi = seuDataFi;
		this.seuEstat = seuEstat;
	}
	public void updateSeuNotificaInformat() {
		this.seuDataNotificaInformat = new Date();
		this.seuDataNotificaDarreraPeticio = new Date();
	}
	public void updateSeuError(
			boolean seuError,
			NotificacioEventEntity seuErrorEvent,
			boolean esNotificaPeticio) {
		this.seuError = seuError;
		this.seuErrorEvent = seuErrorEvent;
		if (esNotificaPeticio) {
			this.seuDataNotificaDarreraPeticio = new Date();
		}
	}
	*/
//	public void updateSeuNouEnviament(int reintentsPeriodeSeu) {
//		this.intentNum++;
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.add(Calendar.MILLISECOND, reintentsPeriodeSeu*(2^intentNum));
//		this.seuIntentData = cal.getTime();
//	}
//	public void updateSeuNovaConsulta() {
//		this.seuIntentData = new Date();
//	}
//	public void updateSeuConsultaError(int reintentsPeriodeSeu) {
//		this.intentNum++;
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.add(Calendar.MILLISECOND, reintentsPeriodeSeu*(2^intentNum));
//		this.seuIntentData = cal.getTime();
//	}
	/*
	public void updateSeuFitxer(
			Long codi,
			String clau) {
		this.seuFitxerCodi = codi;
		this.seuFitxerClau = clau;
	}
	public void updateSeuFiOperacio() {
		this.seuIntentData = new Date();
	}
	public void updateSeuFiOperacio(boolean isError, Integer reintentPeriode) {
		if (isError) {
			this.intentNum++;
			Calendar cal = GregorianCalendar.getInstance();
			cal.add(Calendar.MILLISECOND, reintentPeriode*(2^intentNum));
			this.seuIntentData = cal.getTime();
		} else {
			this.intentNum = 0;
			this.seuIntentData = new Date();
		}
	}
	*/
	public void updateNotificaFiOperacio() {
		this.notificaIntentData = new Date();
	}
	public void updateNotificaFiOperacio(boolean isError, Integer reintentPeriode) {
		if (isError) {
			this.intentNum++;
			Calendar cal = GregorianCalendar.getInstance();
			cal.add(Calendar.MILLISECOND, reintentPeriode*(2^intentNum));
			this.notificaIntentData = cal.getTime();
		} else {
			this.intentNum = 0;
			this.notificaIntentData = new Date();
		}
	}
	public void updateNotificaDataRefrescEstat() {
		this.notificaEstatDataActualitzacio = new Date();
	}
	
	public static Builder getBuilder(
			String titularNif,
			ServeiTipusEnumDto serveiTipus,
			NotificacioEntity notificacio) {
		return new Builder(
				titularNif,
				serveiTipus,
				notificacio);
	}
	
	public static BuilderV1 getBuilderV1(
			Enviament enviament, Notificacio notificacio, 
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
			NotificaDomiciliConcretTipusEnumDto tipusConcret,
			ServeiTipusEnumDto tipusServei,
			NotificacioEntity notificacioGuardada,
			PersonaEntity titular,
			List<PersonaEntity> destinataris) {
		return new BuilderV1(
				enviament,
				notificacio,
				numeracioTipus,
				tipusConcret,
				tipusServei,
				notificacioGuardada,
				titular,
				destinataris
				);
	}

	public static BuilderV2 getBuilderV2(
			Enviament enviament, NotificacioDtoV2 notificacio, 
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
			NotificaDomiciliConcretTipusEnumDto tipusConcret,
			ServeiTipusEnumDto tipusServei,
			NotificacioEntity notificacioGuardada,
			PersonaEntity titular,
			List<PersonaEntity> destinataris) {
		return new BuilderV2(
				enviament,
				notificacio,
				numeracioTipus,
				tipusConcret,
				tipusServei,
				notificacioGuardada,
				titular,
				destinataris
				);
	}
	
	public static class Builder {
		NotificacioEnviamentEntity built;
		Builder(
				String titularNif,
				ServeiTipusEnumDto serveiTipus,
				NotificacioEntity notificacio) {
			built = new NotificacioEnviamentEntity();
//			built.titularNif = titularNif;
			built.serveiTipus = serveiTipus;
			built.notificacio = notificacio;
			built.notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT;
			//built.seuEstat = SeuEstatEnumDto.PENDENT;
			built.intentNum = 0;
			// Definim la data en que s'ha d'enviar cap a la SEU
			/* 
			if (notificacio.getEnviamentDataProgramada() != null) {
			 
				built.seuIntentData = notificacio.getEnviamentDataProgramada();
			} else {
				built.seuIntentData = new Date();
			}
			*/
		}
//		public Builder titularNom(String titularNom) {
//			built.titularNom = titularNom;
//			return this;
//		}
//		public Builder titularLlinatge1(String titularLlinatge1) {
//			built.titularLlinatge1 = titularLlinatge1;
//			return this;
//		}
//		public Builder titularLlinatge2(String titularLlinatge2) {
//			built.titularLlinatge2 = titularLlinatge2;
//			return this;
//		}
//		public Builder titularRaoSocial(String titularRaoSocial) {
//			built.titularRaoSocial = titularRaoSocial;
//			return this;
//		}
//		public Builder titularCodiDesti(String titularCodiDesti) {
//			built.titularCodiDesti = titularCodiDesti;
//			return this;
//		}
//		public Builder titularTelefon(String titularTelefon) {
//			built.titularTelefon = titularTelefon;
//			return this;
//		}
//		public Builder titularEmail(String titularEmail) {
//			built.titularEmail = titularEmail;
//			return this;
//		}
//		public Builder destinatariNif(String destinatariNif) {
//			built.destinatariNif = destinatariNif;
//			return this;
//		}
//		public Builder destinatariNom(String destinatariNom) {
//			built.destinatariNom = destinatariNom;
//			return this;
//		}
//		public Builder destinatariLlinatge1(String destinatariLlinatge1) {
//			built.destinatariLlinatge1 = destinatariLlinatge1;
//			return this;
//		}
//		public Builder destinatariLlinatge2(String destinatariLlinatge2) {
//			built.destinatariLlinatge2 = destinatariLlinatge2;
//			return this;
//		}
//		public Builder destinatariRaoSocial(String destinatariRaoSocial) {
//			built.destinatariRaoSocial = destinatariRaoSocial;
//			return this;
//		}
//		public Builder destinatariCodiDesti(String destinatariCodiDesti) {
//			built.destinatariCodiDesti = destinatariCodiDesti;
//			return this;
//		}
//		public Builder destinatariTelefon(String destinatariTelefon) {
//			built.destinatariTelefon = destinatariTelefon;
//			return this;
//		}
//		public Builder destinatariEmail(String destinatariEmail) {
//			built.destinatariEmail = destinatariEmail;
//			return this;
//		}
		public Builder domiciliTipus(NotificaDomiciliTipusEnumDto domiciliTipus) {
			built.domiciliTipus = domiciliTipus;
			return this;
		}
		public Builder domiciliConcretTipus(NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus) {
			built.domiciliConcretTipus = domiciliConcretTipus;
			return this;
		}
		public Builder domiciliViaTipus(NotificaDomiciliViaTipusEnumDto domiciliViaTipus) {
			built.domiciliViaTipus = domiciliViaTipus;
			return this;
		}
		public Builder domiciliViaNom(String domiciliViaNom) {
			built.domiciliViaNom = domiciliViaNom;
			return this;
		}
		public Builder domiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus) {
			built.domiciliNumeracioTipus = domiciliNumeracioTipus;
			return this;
		}
		public Builder domiciliNumeracioNumero(String domiciliNumeracioNumero) {
			built.domiciliNumeracioNumero = domiciliNumeracioNumero;
			return this;
		}
		public Builder domiciliNumeracioQualificador(String domiciliNumeracioQualificador) {
			built.domiciliNumeracioQualificador = domiciliNumeracioQualificador;
			return this;
		}
		public Builder domiciliNumeracioPuntKm(String domiciliNumeracioPuntKm) {
			built.domiciliNumeracioPuntKm = domiciliNumeracioPuntKm;
			return this;
		}
		public Builder domiciliApartatCorreus(String domiciliApartatCorreus) {
			built.domiciliApartatCorreus = domiciliApartatCorreus;
			return this;
		}
		public Builder domiciliBloc(String domiciliBloc) {
			built.domiciliBloc = domiciliBloc;
			return this;
		}
		public Builder domiciliPortal(String domiciliPortal) {
			built.domiciliPortal = domiciliPortal;
			return this;
		}
		public Builder domiciliEscala(String domiciliEscala) {
			built.domiciliEscala = domiciliEscala;
			return this;
		}
		public Builder domiciliPlanta(String domiciliPlanta) {
			built.domiciliPlanta = domiciliPlanta;
			return this;
		}
		public Builder domiciliPorta(String domiciliPorta) {
			built.domiciliPorta = domiciliPorta;
			return this;
		}
		public Builder domiciliComplement(String domiciliComplement) {
			built.domiciliComplement = domiciliComplement;
			return this;
		}
		public Builder domiciliPoblacio(String domiciliPoblacio) {
			built.domiciliPoblacio = domiciliPoblacio;
			return this;
		}
		public Builder domiciliMunicipiCodiIne(String domiciliMunicipiCodiIne) {
			built.domiciliMunicipiCodiIne = domiciliMunicipiCodiIne;
			return this;
		}
		public Builder domiciliMunicipiNom(String domiciliMunicipiNom) {
			built.domiciliMunicipiNom = domiciliMunicipiNom;
			return this;
		}
		public Builder domiciliCodiPostal(String domiciliCodiPostal) {
			built.domiciliCodiPostal = domiciliCodiPostal;
			return this;
		}
		public Builder domiciliProvinciaCodi(String domiciliProvinciaCodi) {
			built.domiciliProvinciaCodi = domiciliProvinciaCodi;
			return this;
		}
		public Builder domiciliProvinciaNom(String domiciliProvinciaNom) {
			built.domiciliProvinciaNom = domiciliProvinciaNom;
			return this;
		}
		public Builder domiciliPaisCodiIso(String domiciliPaisCodiIso) {
			built.domiciliPaisCodiIso = domiciliPaisCodiIso;
			return this;
		}
		public Builder domiciliPaisNom(String domiciliPaisNom) {
			built.domiciliPaisNom = domiciliPaisNom;
			return this;
		}
		public Builder domiciliLinea1(String domiciliLinea1) {
			built.domiciliLinea1 = domiciliLinea1;
			return this;
		}
		public Builder domiciliLinea2(String domiciliLinea2) {
			built.domiciliLinea2 = domiciliLinea2;
			return this;
		}
		public Builder domiciliCie(Integer domiciliCie) {
			built.domiciliCie = domiciliCie;
			return this;
		}
		public Builder dehObligat(Boolean dehObligat) {
			built.dehObligat = dehObligat;
			return this;
		}
		public Builder dehNif(String dehNif) {
			built.dehNif = dehNif;
			return this;
		}
		public Builder dehProcedimentCodi(String dehProcedimentCodi) {
			built.dehProcedimentCodi = dehProcedimentCodi;
			return this;
		}
		public Builder serveiTipus(ServeiTipusEnumDto serveiTipus) {
			built.serveiTipus = serveiTipus;
			return this;
		}
		public Builder formatSobre(String formatSobre) {
			built.formatSobre = formatSobre;
			return this;
		}
		public Builder formatFulla(String formatFulla) {
			built.formatFulla = formatFulla;
			return this;
		}
		public NotificacioEnviamentEntity build() {
			return built;
		}
	}
	
	public static class BuilderV1 {
		NotificacioEnviamentEntity built;
		BuilderV1(
				Enviament enviament, Notificacio notificacio, 
				NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
				NotificaDomiciliConcretTipusEnumDto tipusConcret,
				ServeiTipusEnumDto tipusServei,
				NotificacioEntity notificacioGuardada,
				PersonaEntity titular,
				List<PersonaEntity> destinataris) {
			built = new NotificacioEnviamentEntity();
			built.serveiTipus = tipusServei;
			built.notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT;
			built.intentNum = 0;
			built.notificacio = notificacioGuardada;
			
			built.domiciliTipus = NotificaDomiciliTipusEnumDto.CONCRETO;
			built.domiciliViaNom = enviament.getEntregaPostal().getViaNom();
			built.domiciliNumeracioNumero = enviament.getEntregaPostal().getNumeroCasa();
			built.domiciliNumeracioQualificador = enviament.getEntregaPostal().getNumeroQualificador();
			built.domiciliNumeracioPuntKm = enviament.getEntregaPostal().getPuntKm();
			built.domiciliApartatCorreus = enviament.getEntregaPostal().getApartatCorreus();
			built.domiciliPortal = enviament.getEntregaPostal().getPortal();
			built.domiciliEscala = enviament.getEntregaPostal().getEscala();
			built.domiciliPlanta = enviament.getEntregaPostal().getPlanta();
			built.domiciliPorta = enviament.getEntregaPostal().getPorta();
			built.domiciliBloc = enviament.getEntregaPostal().getBloc();
			built.domiciliComplement = enviament.getEntregaPostal().getComplement();
			built.domiciliCodiPostal = enviament.getEntregaPostal().getCodiPostal();
			built.domiciliPoblacio = enviament.getEntregaPostal().getPoblacio();
			built.domiciliMunicipiCodiIne = enviament.getEntregaPostal().getMunicipiCodi();
			built.domiciliProvinciaCodi = enviament.getEntregaPostal().getProvinciaCodi();
			built.domiciliPaisCodiIso = enviament.getEntregaPostal().getPaisCodi();
			built.domiciliLinea1 = enviament.getEntregaPostal().getLinea1();
			built.domiciliLinea2 = enviament.getEntregaPostal().getLinea2();
			built.domiciliCie = enviament.getEntregaPostal().getCie();
			built.formatSobre = enviament.getEntregaPostal().getFormatSobre();
			built.formatFulla = enviament.getEntregaPostal().getFormatFulla();

			built.dehProcedimentCodi = enviament.getEntregaDeh().getProcedimentCodi();
			built.dehObligat = enviament.getEntregaDeh().isObligat();			
			
			built.titular = titular;
			built.destinataris = destinataris;
		}
		public BuilderV1 domiciliViaTipus(NotificaDomiciliViaTipusEnumDto domiciliViaTipus) {
			built.domiciliViaTipus = domiciliViaTipus;
			return this;
		}
		
		public BuilderV1 titular(PersonaEntity titular) {
			built.titular = titular;
			return this;
		}
		
		public BuilderV1 destinataris(List<PersonaEntity> destinataris) {
			built.destinataris = destinataris;
			return this;
		}
		
		public NotificacioEnviamentEntity build() {
			return built;
		}
	}
	
	public static class BuilderV2 {
		NotificacioEnviamentEntity built;
		BuilderV2(
				Enviament enviament, NotificacioDtoV2 notificacio, 
				NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
				NotificaDomiciliConcretTipusEnumDto tipusConcret,
				ServeiTipusEnumDto tipusServei,
				NotificacioEntity notificacioGuardada,
				PersonaEntity titular,
				List<PersonaEntity> destinataris) {	
			built = new NotificacioEnviamentEntity();
			built.serveiTipus = tipusServei;
			built.notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT;
			built.intentNum = 0;
			built.notificacio = notificacioGuardada;
			built.domiciliTipus = NotificaDomiciliTipusEnumDto.CONCRETO;
			built.domiciliNumeracioTipus = numeracioTipus;
			built.domiciliConcretTipus = tipusConcret;

			if(enviament.getEntregaPostal() != null) {
				built.domiciliViaNom = enviament.getEntregaPostal().getViaNom();
				built.domiciliNumeracioNumero = enviament.getEntregaPostal().getNumeroCasa();
				built.domiciliNumeracioQualificador = enviament.getEntregaPostal().getNumeroQualificador();
				built.domiciliNumeracioPuntKm = enviament.getEntregaPostal().getPuntKm();
				built.domiciliApartatCorreus = enviament.getEntregaPostal().getApartatCorreus();
				built.domiciliPortal = enviament.getEntregaPostal().getPortal();
				built.domiciliEscala = enviament.getEntregaPostal().getEscala();
				built.domiciliPlanta = enviament.getEntregaPostal().getPlanta();
				built.domiciliPorta = enviament.getEntregaPostal().getPorta();
				built.domiciliBloc = enviament.getEntregaPostal().getBloc();
				built.domiciliComplement = enviament.getEntregaPostal().getComplement();
				built.domiciliCodiPostal = enviament.getEntregaPostal().getCodiPostal();
				built.domiciliPoblacio = enviament.getEntregaPostal().getPoblacio();
				built.domiciliMunicipiCodiIne = enviament.getEntregaPostal().getMunicipiCodi();
				built.domiciliProvinciaCodi = enviament.getEntregaPostal().getProvinciaCodi();
				built.domiciliPaisCodiIso = enviament.getEntregaPostal().getPaisCodi();
				built.domiciliLinea1 = enviament.getEntregaPostal().getLinea1();
				built.domiciliLinea2 = enviament.getEntregaPostal().getLinea2();
				built.domiciliCie = enviament.getEntregaPostal().getCie();
				built.formatSobre = enviament.getEntregaPostal().getFormatSobre();
				built.formatFulla = enviament.getEntregaPostal().getFormatFulla();
				built.dehObligat = enviament.getEntregaDeh().isObligat();
			}
			
			built.titular = titular;
			built.destinataris = destinataris;
		}
		
		public BuilderV2 domiciliViaTipus(NotificaDomiciliViaTipusEnumDto domiciliViaTipus) {
			built.domiciliViaTipus = domiciliViaTipus;
			return this;
		}
		
//		public BuilderV2 titular(PersonaEntity titular) {
//			built.titular = titular;
//			return this;
//		}
		
		public BuilderV2 destinataris(List<PersonaEntity> destinataris) {
			built.destinataris = destinataris;
			return this;
		}
		
		public NotificacioEnviamentEntity build() {
			return built;
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((notificacio == null) ? 0 : notificacio.hashCode());
		result = prime * result + ((titular.getNif() == null) ? 0 : titular.getNif().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificacioEnviamentEntity other = (NotificacioEnviamentEntity) obj;
		if (notificacio == null) {
			if (other.notificacio != null)
				return false;
		} else if (!notificacio.equals(other.notificacio))
			return false;
		if (titular.getNif() == null) {
			if (other.titular.getNif() != null)
				return false;
		} else if (!titular.getNif().equals(other.titular.getNif()))
			return false;
		return true;
	}
	

	private static final long serialVersionUID = 6993171107561077019L;
}
