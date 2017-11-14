/**
 * 
 */
package es.caib.notib.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa un destinatari de la
 * notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_notificacio_env")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEnviamentEntity extends NotibAuditable<Long> {

	@Column(name = "titular_nom", length = 100)
	private String titularNom;
	@Column(name = "titular_llinatge1", length = 100)
	private String titularLlinatge1;
	@Column(name = "titular_llinatge2", length = 100)
	private String titularLlinatge2;
	@Column(name = "titular_nif", length = 9, nullable = false)
	private String titularNif;
	@Column(name = "titular_telefon", length = 16)
	private String titularTelefon;
	@Column(name = "titular_email", length = 100)
	private String titularEmail;
	@Column(name = "destinatari_nom", length = 100)
	private String destinatariNom;
	@Column(name = "destinatari_llinatge1", length = 100)
	private String destinatariLlinatge1;
	@Column(name = "destinatari_llinatge2", length = 100)
	private String destinatariLlinatge2;
	@Column(name = "destinatari_nif", length = 9)
	private String destinatariNif;
	@Column(name = "destinatari_telefon", length = 16)
	private String destinatariTelefon;
	@Column(name = "destinatari_email", length = 100)
	private String destinatariEmail;
	@Column(name = "dom_tipus")
	private NotificaDomiciliTipusEnumDto domiciliTipus;
	@Column(name = "dom_con_tipus")
	private NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus;
	@Column(name = "dom_via_tipus")
	private NotificaDomiciliViaTipusEnumDto domiciliViaTipus;
	@Column(name = "dom_via_nom", length = 100)
	private String domiciliViaNom;
	@Column(name = "dom_num_tipus")
	private NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;
	@Column(name = "dom_num_num", length = 10)
	private String domiciliNumeracioNumero;
	@Column(name = "dom_num_puntkm", length = 10)
	private String domiciliNumeracioPuntKm;
	@Column(name = "dom_apartat", length = 10)
	private String domiciliApartatCorreus;
	@Column(name = "dom_bloc", length = 50)
	private String domiciliBloc;
	@Column(name = "dom_portal", length = 50)
	private String domiciliPortal;
	@Column(name = "dom_escala", length = 50)
	private String domiciliEscala;
	@Column(name = "dom_planta", length = 50)
	private String domiciliPlanta;
	@Column(name = "dom_porta", length = 50)
	private String domiciliPorta;
	@Column(name = "dom_complem", length = 250)
	private String domiciliComplement;
	@Column(name = "dom_poblacio", length = 30)
	private String domiciliPoblacio;
	@Column(name = "dom_mun_codine", length = 5)
	private String domiciliMunicipiCodiIne;
	@Column(name = "dom_mun_nom", length = 64)
	private String domiciliMunicipiNom;
	@Column(name = "dom_codi_postal", length = 10)
	private String domiciliCodiPostal;
	@Column(name = "dom_prv_codi", length = 2)
	private String domiciliProvinciaCodi;
	@Column(name = "dom_prv_nom", length = 64)
	private String domiciliProvinciaNom;
	@Column(name = "dom_pai_codiso", length = 3)
	private String domiciliPaisCodiIso; // ISO-3166
	@Column(name = "dom_pai_nom", length = 64)
	private String domiciliPaisNom;
	@Column(name = "dom_linea1", length = 50)
	private String domiciliLinea1;
	@Column(name = "dom_linea2", length = 50)
	private String domiciliLinea2;
	@Column(name = "dom_cie")
	private Integer domiciliCie;
	@Column(name = "deh_obligat")
	private Boolean dehObligat;
	@Column(name = "deh_nif", length = 9)
	private String dehNif;
	@Column(name = "deh_proc_codi", length = 6)
	private String dehProcedimentCodi;
	@Column(name = "servei_tipus")
	private NotificaServeiTipusEnumDto serveiTipus;
	@Column(name = "retard_postal")
	private Integer retardPostal;
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	private Date caducitat;
	@Column(name = "notifica_id", length = 20)
	private String notificaIdentificador;
	@Column(name = "notifica_ref", length = 20)
	private String notificaReferencia;
	@Column(name = "notifica_datcre")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaDataCreacio;
	@Column(name = "notifica_datdisp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaDataDisposicio;
	@Column(name = "notifica_emi_dir3codi", length = 9)
	private String notificaEmisorDir3;
	@Column(name = "notifica_emi_dir3desc", length = 100)
	private String notificaEmisorDescripcio;
	@Column(name = "notifica_arr_dir3codi", length = 9)
	private String notificaArrelDir3;
	@Column(name = "notifica_arr_dir3desc", length = 100)
	private String notificaArrelDescripcio;
	@Column(name = "notifica_des_dir3codi", length = 9)
	private String notificaDestiDir3;
	@Column(name = "notifica_des_dir3desc", length = 100)
	private String notificaDestiDescripcio;
	@Column(name = "notifica_estat", nullable = false)
	private NotificacioDestinatariEstatEnumDto notificaEstat;
	@Column(name = "notifica_estat_desc", length = 255)
	private String notificaEstatDescripcio;
	@Column(name = "notifica_estat_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEstatData;
	@Column(name = "notifica_estat_origen", length = 20)
	private String notificaEstatOrigen;
	@Column(name = "notifica_estat_recnif", length = 9)
	private String notificaEstatReceptorNif;
	@Column(name = "notifica_estat_recnom", length = 100)
	private String notificaEstatReceptorNom;
	@Column(name = "notifica_estat_numseg", length = 50)
	private String notificaEstatNumSeguiment;
	@Column(name = "notifica_cer_arxiuid", length = 50)
	private String notificaCertificacioArxiuId;
	@Column(name = "notifica_cer_hash", length = 50)
	private String notificaCertificacioHash;
	@Column(name = "notifica_cer_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaCertificacioData;
	@Column(name = "notifica_cer_origen", length = 20)
	private String notificaCertificacioOrigen;
	@Column(name = "notifica_cer_metas", length = 255)
	private String notificaCertificacioMetadades;
	@Column(name = "notifica_cer_csv", length = 50)
	private String notificaCertificacioCsv;
	@Column(name = "notifica_cer_mime", length = 20)
	private String notificaCertificacioTipusMime;
	@Column(name = "notifica_cer_tamany", length = 20)
	private Integer notificaCertificacioTamany;
	@Column(name = "notifica_cer_tipus")
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	@Column(name = "notifica_cer_arxtip")
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	@Column(name = "notifica_cer_numseg", length = 50)
	private String notificaCertificacioNumSeguiment;
	@Column(name = "notifica_error", nullable = false)
	private boolean notificaError;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "notifica_error_event_id")
	@ForeignKey(name = "not_noteve_noterr_notdest_fk")
	private NotificacioEventEntity notificaErrorEvent;
	@Column(name = "seu_reg_numero", length = 50)
	private String seuRegistreNumero;
	@Column(name = "seu_reg_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date seuRegistreData;
	@Column(name = "seu_data_fi")
	@Temporal(TemporalType.TIMESTAMP)
	private Date seuDataFi;
	@Column(name = "seu_estat", nullable = false)
	private NotificacioDestinatariEstatEnumDto seuEstat;
	@Column(name = "seu_error", nullable = false)
	private boolean seuError;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "seu_error_event_id")
	@ForeignKey(name = "not_noteve_seuerr_notdest_fk")
	private NotificacioEventEntity seuErrorEvent;
	@Column(name = "seu_data_enviam")
	@Temporal(TemporalType.TIMESTAMP)
	private Date seuDataEnviament;
	@Column(name = "seu_data_estat")
	@Temporal(TemporalType.TIMESTAMP)
	private Date seuDataEstat;
	@Column(name = "seu_data_notinf")
	@Temporal(TemporalType.TIMESTAMP)
	private Date seuDataNotificaInformat;
	@Column(name = "seu_data_notidp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date seuDataNotificaDarreraPeticio;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "notificacio_id")
	@ForeignKey(name = "not_notificacio_notdest_fk")
	private NotificacioEntity notificacio;

	public String getTitularNom() {
		return titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public String getTitularTelefon() {
		return titularTelefon;
	}
	public String getTitularEmail() {
		return titularEmail;
	}
	public String getDestinatariNom() {
		return destinatariNom;
	}
	public String getDestinatariLlinatge1() {
		return destinatariLlinatge1;
	}
	public String getDestinatariLlinatge2() {
		return destinatariLlinatge2;
	}
	public String getDestinatariNif() {
		return destinatariNif;
	}
	public String getDestinatariTelefon() {
		return destinatariTelefon;
	}
	public String getDestinatariEmail() {
		return destinatariEmail;
	}
	public NotificaDomiciliTipusEnumDto getDomiciliTipus() {
		return domiciliTipus;
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
	public NotificaServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public Integer getRetardPostal() {
		return retardPostal;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public String getNotificaIdentificador() {
		return notificaIdentificador;
	}
	public String getNotificaReferencia() {
		return notificaReferencia;
	}
	public Date getNotificaDataCreacio() {
		return notificaDataCreacio;
	}
	public Date getNotificaDataDisposicio() {
		return notificaDataDisposicio;
	}
	public String getNotificaEmisorDir3() {
		return notificaEmisorDir3;
	}
	public String getNotificaEmisorDescripcio() {
		return notificaEmisorDescripcio;
	}
	public String getNotificaArrelDir3() {
		return notificaArrelDir3;
	}
	public String getNotificaArrelDescripcio() {
		return notificaArrelDescripcio;
	}
	public String getNotificaDestiDir3() {
		return notificaDestiDir3;
	}
	public String getNotificaDestiDescripcio() {
		return notificaDestiDescripcio;
	}
	public NotificacioDestinatariEstatEnumDto getNotificaEstat() {
		return notificaEstat;
	}
	public String getNotificaEstatDescripcio() {
		return notificaEstatDescripcio;
	}
	public Date getNotificaEstatData() {
		return notificaEstatData;
	}
	public String getNotificaEstatOrigen() {
		return notificaEstatOrigen;
	}
	public String getNotificaEstatReceptorNif() {
		return notificaEstatReceptorNif;
	}
	public String getNotificaEstatReceptorNom() {
		return notificaEstatReceptorNom;
	}
	public String getNotificaEstatNumSeguiment() {
		return notificaEstatNumSeguiment;
	}
	public String getNotificaCertificacioArxiuId() {
		return notificaCertificacioArxiuId;
	}
	public String getNotificaCertificacioHash() {
		return notificaCertificacioHash;
	}
	public Date getNotificaCertificacioData() {
		return notificaCertificacioData;
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
	public String getNotificaCertificacioTipusMime() {
		return notificaCertificacioTipusMime;
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
	public String getSeuRegistreNumero() {
		return seuRegistreNumero;
	}
	public Date getSeuRegistreData() {
		return seuRegistreData;
	}
	public Date getSeuDataFi() {
		return seuDataFi;
	}
	public NotificacioDestinatariEstatEnumDto getSeuEstat() {
		return seuEstat;
	}
	public boolean isSeuError() {
		return seuError;
	}
	public NotificacioEventEntity getSeuErrorEvent() {
		return seuErrorEvent;
	}
	public Date getSeuDataEnviament() {
		return seuDataEnviament;
	}
	public Date getSeuDataEstat() {
		return seuDataEstat;
	}
	public Date getSeuDataNotificaInformat() {
		return seuDataNotificaInformat;
	}
	public Date getSeuDataNotificaDarreraPeticio() {
		return seuDataNotificaDarreraPeticio;
	}
	public NotificacioEntity getNotificacio() {
		return notificacio;
	}

	public void updateNotificaIdentificador(
			String notificaIdentificador) {
		this.notificaIdentificador = notificaIdentificador;
	}

	public void updateNotificaEstat(
			Date notificaEstatData,
			NotificacioDestinatariEstatEnumDto notificaEstat,
			boolean netejarError) {
		this.notificaEstatData = notificaEstatData;
		this.notificaEstat = notificaEstat;
		if (netejarError) {
			this.notificaError = false;
			this.notificaErrorEvent = null;
		}
	}
	public void updateNotificaEstat(
			Date notificaEstatData,
			NotificacioDestinatariEstatEnumDto notificaEstat,
			String notificaEstatDescripcio,
			String notificaEstatNumSeguiment,
			boolean netejarError) {
		this.notificaEstatData = notificaEstatData;
		this.notificaEstat = notificaEstat;
		this.notificaEstatDescripcio = notificaEstatDescripcio;
		this.notificaEstatNumSeguiment = notificaEstatNumSeguiment;
		if (netejarError) {
			this.notificaError = false;
			this.notificaErrorEvent = null;
		}
	}
	public void updateNotificaDatat(
			Date notificaEstatData,
			NotificacioDestinatariEstatEnumDto notificaEstat,
			String notificaEstatDescripcio,
			String notificaEstatOrigen,
			String notificaEstatReceptorNif,
			String notificaEstatReceptorNom,
			String notificaEstatNumSeguiment) {
		this.notificaEstatData = notificaEstatData;
		this.notificaEstat = notificaEstat;
		this.notificaEstatDescripcio = notificaEstatDescripcio;
		this.notificaEstatOrigen = notificaEstatOrigen;
		this.notificaEstatReceptorNif = notificaEstatReceptorNif;
		this.notificaEstatReceptorNom = notificaEstatReceptorNom;
		this.notificaEstatNumSeguiment = notificaEstatNumSeguiment;
	}
	public void updateNotificaCertificacio(
			Date notificaCertificacioData,
			String notificaCertificacioArxiuId,
			String notificaCertificacioHash,
			String notificaCertificacioOrigen,
			String notificaCertificacioMetadades,
			String notificaCertificacioCsv,
			String notificaCertificacioTipusMime,
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
		this.notificaCertificacioTipusMime = notificaCertificacioTipusMime;
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
	public void updateNotificaReferencia(
			String notificaReferencia) {
		this.notificaReferencia = notificaReferencia;
	}

	public void updateSeuEnviament(
			String seuRegistreNumero,
			Date seuRegistreData,
			NotificacioDestinatariEstatEnumDto seuEstat) {
		this.seuDataEnviament = new Date();
		this.seuRegistreNumero = seuRegistreNumero;
		this.seuRegistreData = seuRegistreData;
		this.seuEstat = seuEstat;
	}
	public void updateSeuEstat(
			Date seuDataFi,
			NotificacioDestinatariEstatEnumDto seuEstat) {
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

	public static Builder getBuilder(
			String titularNif,
			NotificaServeiTipusEnumDto serveiTipus,
			NotificacioEntity notificacio) {
		return new Builder(
				titularNif,
				serveiTipus,
				notificacio);
	}

	public static class Builder {
		NotificacioEnviamentEntity built;
		Builder(
				String titularNif,
				NotificaServeiTipusEnumDto serveiTipus,
				NotificacioEntity notificacio) {
			built = new NotificacioEnviamentEntity();
			built.titularNif = titularNif;
			built.serveiTipus = serveiTipus;
			built.notificacio = notificacio;
			built.notificaEstat = NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT;
			built.seuEstat = NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT;
		}
		public Builder titularNom(String titularNom) {
			built.titularNom = titularNom;
			return this;
		}
		public Builder titularLlinatge1(String titularLlinatge1) {
			built.titularLlinatge1 = titularLlinatge1;
			return this;
		}
		public Builder titularLlinatge2(String titularLlinatge2) {
			built.titularLlinatge2 = titularLlinatge2;
			return this;
		}
		public Builder titularTelefon(String titularTelefon) {
			built.titularTelefon = titularTelefon;
			return this;
		}
		public Builder titularEmail(String titularEmail) {
			built.titularEmail = titularEmail;
			return this;
		}
		public Builder destinatariNif(String destinatariNif) {
			built.destinatariNif = destinatariNif;
			return this;
		}
		public Builder destinatariNom(String destinatariNom) {
			built.destinatariNom = destinatariNom;
			return this;
		}
		public Builder destinatariLlinatge1(String destinatariLlinatge1) {
			built.destinatariLlinatge1 = destinatariLlinatge1;
			return this;
		}
		public Builder destinatariLlinatge2(String destinatariLlinatge2) {
			built.destinatariLlinatge2 = destinatariLlinatge2;
			return this;
		}
		public Builder destinatariTelefon(String destinatariTelefon) {
			built.destinatariTelefon = destinatariTelefon;
			return this;
		}
		public Builder destinatariEmail(String destinatariEmail) {
			built.destinatariEmail = destinatariEmail;
			return this;
		}
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
		public Builder serveiTipus(NotificaServeiTipusEnumDto serveiTipus) {
			built.serveiTipus = serveiTipus;
			return this;
		}
		public Builder retardPostal(Integer retardPostal) {
			built.retardPostal = retardPostal;
			return this;
		}
		public Builder caducitat(Date caducitat) {
			built.caducitat = caducitat;
			return this;
		}
		public NotificacioEnviamentEntity build() {
			return built;
		}
	}

	public static NotificacioDestinatariEstatEnumDto calcularEstatCombinatNotificaSeu(
			NotificacioEnviamentEntity enviament) {
		NotificacioDestinatariEstatEnumDto estatNotifica = enviament.getNotificaEstat();
		NotificacioDestinatariEstatEnumDto estatSeu = enviament.getSeuEstat();
		NotificacioDestinatariEstatEnumDto estatBo;
		if (!NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT.equals(estatSeu) &&
			!NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA.equals(estatSeu)) {
			estatBo = estatSeu;
		} else if (	!NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT.equals(estatNotifica) &&
					!NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA.equals(estatNotifica)) {
			estatBo = estatNotifica;
		} else {
			if (NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA.equals(estatSeu) ||
				NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA.equals(estatNotifica)) {
				return NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA;
			} else {
				return NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT;
			}
		}
		return estatBo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((notificacio == null) ? 0 : notificacio.hashCode());
		result = prime * result + ((titularNif == null) ? 0 : titularNif.hashCode());
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
		if (titularNif == null) {
			if (other.titularNif != null)
				return false;
		} else if (!titularNif.equals(other.titularNif))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
