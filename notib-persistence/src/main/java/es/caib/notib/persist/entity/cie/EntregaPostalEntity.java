package es.caib.notib.persist.entity.cie;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EntregaPostalVia;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.logic.intf.dto.cie.CieCertificacioArxiuTipus;
import es.caib.notib.logic.intf.dto.cie.CieCertificacioTipus;
import es.caib.notib.logic.intf.dto.notificacio.EntregaPostal;
import es.caib.notib.logic.intf.rest.consulta.Estat;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Classe del model de dades que representa els enviaments d'una
 * notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="NOT_ENTREGA_POSTAl")
@EntityListeners(AuditingEntityListener.class)
public class EntregaPostalEntity extends NotibAuditable<Long> {

	/* Domicili */
	@Column(name = "dom_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliTipusEnumDto domiciliTipus;
	
	@Column(name = "dom_con_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliConcretTipus domiciliConcretTipus;
	
	@Column(name = "dom_via_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected EntregaPostalVia domiciliViaTipus;
	
	@Column(name = "dom_via_nom", length = 50)
	protected String domiciliViaNom;

	@Column(name = "dom_num_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;
	
	@Column(name = "dom_num_num", length = 5)
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
	
	@Column(name = "dom_poblacio", length = 255)
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

	@Column(name = "format_sobre", length = 10)
	protected String formatSobre;

	@Column(name = "format_fulla", length = 10)
	protected String formatFulla;

	@Setter
	@Column(name = "cie_id")
	protected String cieId;

	@Setter
	@Column(name = "cie_cancelat")
	protected boolean cieCancelat;

	@Setter
	@Column(name = "cie_estat")
	protected CieEstat cieEstat;

	@Setter
	@Column(name = "cie_error_desc", length = 250)
	protected String cieErrorDesc;
	// TODO crear tots els camps seguents a 930.yaml
	@Column(name = "cie_datat_recnif", length = 9)
	protected String cieDatatReceptorNif;

	@Column(name = "cie_datat_recnom", length = 400)
	protected String cieDatatReceptorNom;

	@Column(name = "cie_cer_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date cieCertificacioData;

	@Column(name = "cie_cer_arxiuid", length = 50)
	protected String cieCertificacioArxiuId;

	@Column(name = "cie_cer_hash", length = 50)
	protected String cieCertificacioHash;

	@Column(name = "cie_cer_origen", length = 20)
	protected String cieCertificacioOrigen;

	@Column(name = "cie_cer_metas", length = 255)
	protected String cieCertificacioMetadades;

	@Column(name = "cie_cer_csv", length = 50)
	protected String cieCertificacioCsv;

	@Column(name = "cie_cer_mime", length = 20)
	protected String cieCertificacioMime;

	@Column(name = "cie_cer_tamany", length = 20)
	protected Integer cieCertificacioTamany;

	@Column(name = "cie_cer_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected CieCertificacioTipus cieCertificacioTipus;

	@Column(name = "cie_cer_arxtip")
	@Enumerated(EnumType.ORDINAL)
	protected CieCertificacioArxiuTipus cieCertificacioArxiuTipus;

	@Column(name = "cie_cer_numseg", length = 50)
	protected String cieCertificacioNumSeguiment;

	@Column(name = "cie_estat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date cieEstatData;

	@Column(name = "cie_estat_desc", length = 255)
	protected String cieEstatDescripcio;

	@Column(name = "cie_datat_origen", length = 20)
	protected String cieDatatOrigen;

	@Column(name = "cie_datat_numseg", length = 50)
	protected String cieDatatNumSeguiment;

	@Column(name = "cie_datat_errdes", length = 255)
	protected String cieDatatErrorDescripcio;

	@Column(name = "cie_estat_dataact")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date cieEstatDataActualitzacio;

	public String getCieCertificacioArxiuNom() {
		return "certificacio_postal_" + cieId + ".pdf";
	}


	public void update(EntregaPostal entregaPostal) {

		domiciliViaTipus = entregaPostal.getViaTipus();
		domiciliConcretTipus = NotificaDomiciliConcretTipus.NACIONAL;
		if (entregaPostal.getTipus() != null) {
			switch (entregaPostal.getTipus()) {
				case APARTAT_CORREUS:
					domiciliConcretTipus = NotificaDomiciliConcretTipus.APARTAT_CORREUS;
					break;
				case ESTRANGER:
					domiciliConcretTipus = NotificaDomiciliConcretTipus.ESTRANGER;
					break;
				case SENSE_NORMALITZAR:
					domiciliConcretTipus = NotificaDomiciliConcretTipus.SENSE_NORMALITZAR;
					break;
			}
		}
		if (entregaPostal.getNumeroCasa() != null) {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
		} else if (entregaPostal.getApartatCorreus() != null) {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
		} else if (entregaPostal.getPuntKm() != null) {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
		} else {
			domiciliNumeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
		}
		this.domiciliTipus = NotificaDomiciliTipusEnumDto.CONCRETO;
		if(! NotificaDomiciliConcretTipus.SENSE_NORMALITZAR.equals(entregaPostal.getTipus())) {
			this.domiciliViaNom = entregaPostal.getViaNom();
			this.domiciliNumeracioNumero = entregaPostal.getNumeroCasa();
			this.domiciliNumeracioQualificador = entregaPostal.getNumeroQualificador();
			this.domiciliNumeracioPuntKm = entregaPostal.getPuntKm();
			this.domiciliApartatCorreus = entregaPostal.getApartatCorreus();
			this.domiciliPortal = entregaPostal.getPortal();
			this.domiciliEscala = entregaPostal.getEscala();
			this.domiciliPlanta = entregaPostal.getPlanta();
			this.domiciliPorta = entregaPostal.getPorta();
			this.domiciliBloc = entregaPostal.getBloc();
			this.domiciliComplement = entregaPostal.getComplement();
			this.domiciliPoblacio = entregaPostal.getPoblacio();
			this.domiciliMunicipiCodiIne = entregaPostal.getMunicipiCodi();
			this.domiciliProvinciaCodi = entregaPostal.getProvincia();
			this.domiciliCie = entregaPostal.getCie();
			this.formatSobre = entregaPostal.getFormatSobre();
			this.formatFulla = entregaPostal.getFormatFulla();
		}
		this.domiciliPaisCodiIso = entregaPostal.getPaisCodi();
		this.domiciliCodiPostal = entregaPostal.getCodiPostal();
		this.domiciliLinea1 = entregaPostal.getLinea1();
		this.domiciliLinea2 = entregaPostal.getLinea2();
	}

	public String provinciaCodiFormatted(){
		return domiciliProvinciaCodi != null && !domiciliProvinciaCodi.isEmpty() ?String.format("%02d", Integer.parseInt(domiciliProvinciaCodi)) : "";
	}

	public String municipiCodiFormatted(){
		return domiciliMunicipiCodiIne != null && !domiciliMunicipiCodiIne.isEmpty() ?
				provinciaCodiFormatted() + String.format("%04d", Integer.parseInt(domiciliMunicipiCodiIne)) : "";
	}

	@Override
	public String toString() {

		String domicili = "";
		switch (domiciliConcretTipus) {
			case ESTRANGER:
				domicili = domiciliPaisCodiIso + "-";
				domicili += getDomiciliPoblacioString();
				domicili += getAdressa();
				break;
			case NACIONAL:
				domicili = provinciaCodiFormatted() + "-";
				domicili += municipiCodiFormatted() + "-";
				domicili += domiciliCodiPostal + "-";
				domicili += getDomiciliPoblacioString();

				domicili += getAdressa();
				break;
			case APARTAT_CORREUS:
				domicili = provinciaCodiFormatted() + "-";
				domicili += municipiCodiFormatted() + "-";
				domicili += domiciliCodiPostal + "-";
				domicili += domiciliApartatCorreus;
				domicili += getDomiciliPoblacioString();
				break;
			case SENSE_NORMALITZAR:
				domicili = domiciliLinea1 + " " + domiciliLinea2;
				break;
			default:
				return "";
		}
		return domicili;
	}

	private String getDomiciliPoblacioString() {

		if (domiciliPoblacio == null) {
			return "";
		}
		return domiciliPoblacio.length() > 30 ? " (" + domiciliPoblacio.substring(0, 30) + ") - " : " (" + domiciliPoblacio + ") - ";
	}

	private String getAdressa() {

		var adressa = "";
		if (domiciliViaTipus != null) {
			adressa += domiciliViaTipus + " ";
		}
		if (domiciliViaNom != null && !domiciliViaNom.isEmpty()) {
			adressa += domiciliViaNom + " ";
		}
		if (NotificaDomiciliNumeracioTipusEnumDto.NUMERO.equals(domiciliNumeracioTipus)) {
			adressa += domiciliNumeracioNumero + " ";
		} else if(NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC.equals(domiciliNumeracioTipus)) {
			adressa += domiciliNumeracioPuntKm + " ";
		}
		if (domiciliNumeracioQualificador != null && !domiciliNumeracioQualificador.isEmpty()) {
			adressa += domiciliNumeracioQualificador + " ";
		}
		if (domiciliBloc != null && !domiciliBloc.isEmpty()) {
			adressa += "b. " + domiciliBloc + " ";
		}
		if (domiciliPortal != null && !domiciliPortal.isEmpty()) {
			adressa += "pt. " + domiciliPortal + " ";
		}
		if (domiciliEscala != null && !domiciliEscala.isEmpty()) {
			adressa += "e. " + domiciliEscala + " ";
		}
		if (domiciliPlanta != null && !domiciliPlanta.isEmpty()) {
			adressa += "pl. " + domiciliPlanta + " ";
		}
		if (domiciliPorta != null && !domiciliPorta.isEmpty()) {
			adressa += "p. " + domiciliPorta + " ";
		}
		if (domiciliComplement != null && !domiciliComplement.isEmpty()) {
			adressa += "c. " + domiciliComplement + " ";
		}
		return adressa;
	}

	public boolean errorEntregaPostal() {
		return CieEstat.ERROR.equals(cieEstat);
	}

	public boolean isCieEstatFinal() {

		return cieEstat != null && (CieEstat.NOTIFICADA.equals(cieEstat)
				|| CieEstat.CANCELADO.equals(cieEstat)
				|| CieEstat.EXTRAVIADA.equals(cieEstat)
				|| CieEstat.SIN_INFORMACION.equals(cieEstat)
				|| CieEstat.REHUSADA.equals(cieEstat)
				|| CieEstat.ERROR.equals(cieEstat)
				|| CieEstat.DEVUELTO.equals(cieEstat));
	}

	public void updateReceptorDatat(String cieDatatReceptorNif, String cieDatatReceptorNom) {

		if (!isBlank(cieDatatReceptorNif)) {
			this.cieDatatReceptorNif = cieDatatReceptorNif;
		}
		if (!isBlank(cieDatatReceptorNom)) {
			this.cieDatatReceptorNom = cieDatatReceptorNom;
		}
	}

	public void updateCieCertificacio(
			Date cieCertificacioData,
			String cieCertificacioArxiuId,
			String cieCertificacioHash,
			String cieCertificacioOrigen,
			String cieCertificacioMetadades,
			String cieCertificacioCsv,
			String cieCertificacioMime,
			Integer cieCertificacioTamany,
			CieCertificacioTipus cieCertificacioTipus, // acuse o sobre
			CieCertificacioArxiuTipus cieCertificacioArxiuTipus,
			String cieCertificacioNumSeguiment) {

		this.cieCertificacioData = cieCertificacioData;
		this.cieCertificacioArxiuId = cieCertificacioArxiuId;
		this.cieCertificacioHash = cieCertificacioHash;
		this.cieCertificacioOrigen = cieCertificacioOrigen;
		this.cieCertificacioMetadades = cieCertificacioMetadades;
		this.cieCertificacioCsv = cieCertificacioCsv;
		this.cieCertificacioMime = cieCertificacioMime;
		this.cieCertificacioTamany = cieCertificacioTamany;
		this.cieCertificacioTipus = cieCertificacioTipus;
		this.cieCertificacioArxiuTipus = cieCertificacioArxiuTipus;
		this.cieCertificacioNumSeguiment = cieCertificacioNumSeguiment;
	}

	public void updateCieDatat(
			CieEstat cieEstat,
			Date cieEstatData,
			boolean cieEstatFinal,
			String cieEstatDescripcio,
			String cieDatatOrigen,
			String cieDatatReceptorNif,
			String cieDatatReceptorNom,
			String cieDatatNumSeguiment,
			String cieDatatErrorDescripcio) {

		this.cieEstat = cieEstat;
		this.cieEstatData = cieEstatData;
//		this.cieEstatFinal = cieEstatFinal;
		this.cieEstatDescripcio = cieEstatDescripcio;

		if (!isBlank(cieDatatOrigen)) {
			this.cieDatatOrigen = cieDatatOrigen;
		}
		if (!isBlank(cieDatatReceptorNif)) {
			this.cieDatatReceptorNif = cieDatatReceptorNif;
		}
		if (!isBlank(cieDatatReceptorNom)) {
			this.cieDatatReceptorNom = cieDatatReceptorNom;
		}
		if (!isBlank(cieDatatNumSeguiment)) {
			this.cieDatatNumSeguiment = cieDatatNumSeguiment;
		}
		this.cieDatatErrorDescripcio = cieDatatErrorDescripcio;
		this.cieEstatDataActualitzacio = new Date();
	}

}
