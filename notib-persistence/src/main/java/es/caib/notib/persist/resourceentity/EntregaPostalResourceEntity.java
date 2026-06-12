package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EntregaPostalVia;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.logic.intf.model.EntregaPostalResource;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.logic.intf.dto.cie.CieCertificacioArxiuTipus;
import es.caib.notib.logic.intf.dto.cie.CieCertificacioTipus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades de la entrega postal d'un enviament
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "entrega_postal")
@Getter
@Setter
@NoArgsConstructor
public class EntregaPostalResourceEntity extends BaseAuditableResourceEntity<EntregaPostalResource> {

	@Column(name = "dom_tipus")
	@Enumerated(EnumType.ORDINAL)
	private NotificaDomiciliTipusEnumDto domiciliTipus;

	@Column(name = "dom_con_tipus")
	@Enumerated(EnumType.ORDINAL)
	private NotificaDomiciliConcretTipus domiciliConcretTipus;

	@Column(name = "dom_via_tipus")
	@Enumerated(EnumType.ORDINAL)
	private EntregaPostalVia domiciliViaTipus;

	@Column(name = "dom_via_nom", length = 50)
	private String domiciliViaNom;

	@Column(name = "dom_num_tipus")
	@Enumerated(EnumType.ORDINAL)
	private NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;

	@Column(name = "dom_num_num", length = 5)
	private String domiciliNumeracioNumero;

	@Column(name = "dom_num_qualif", length = 3)
	private String domiciliNumeracioQualificador;

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

	@Column(name = "dom_poblacio", length = 255)
	private String domiciliPoblacio;

	@Column(name = "dom_mun_codine", length = 6)
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

	@Column(name = "format_sobre", length = 10)
	private String formatSobre;

	@Column(name = "format_fulla", length = 10)
	private String formatFulla;

	@Column(name = "cie_id")
	private String cieId;

	@Column(name = "cie_cancelat")
	private boolean cieCancelat;

	@Column(name = "cie_estat")
	private CieEstat cieEstat;

	@Column(name = "cie_error_desc", length = 250)
	private String cieErrorDesc;

	@Column(name = "cie_datat_recnif", length = 9)
	private String cieDatatReceptorNif;

	@Column(name = "cie_datat_recnom", length = 400)
	private String cieDatatReceptorNom;

	@Column(name = "cie_cer_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date cieCertificacioData;

	@Column(name = "cie_cer_arxiuid", length = 50)
	private String cieCertificacioArxiuId;

	@Column(name = "cie_cer_hash", length = 50)
	private String cieCertificacioHash;

	@Column(name = "cie_cer_origen", length = 20)
	private String cieCertificacioOrigen;

	@Column(name = "cie_cer_metas", length = 255)
	private String cieCertificacioMetadades;

	@Column(name = "cie_cer_csv", length = 50)
	private String cieCertificacioCsv;

	@Column(name = "cie_cer_mime", length = 20)
	private String cieCertificacioMime;

	@Column(name = "cie_cer_tamany", length = 20)
	private Integer cieCertificacioTamany;

	@Column(name = "cie_cer_tipus")
	@Enumerated(EnumType.ORDINAL)
	private CieCertificacioTipus cieCertificacioTipus;

	@Column(name = "cie_cer_arxtip")
	@Enumerated(EnumType.ORDINAL)
	private CieCertificacioArxiuTipus cieCertificacioArxiuTipus;

	@Column(name = "cie_cer_numseg", length = 50)
	private String cieCertificacioNumSeguiment;

	@Column(name = "cie_estat_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date cieEstatData;

	@Column(name = "cie_estat_desc", length = 255)
	private String cieEstatDescripcio;

	@Column(name = "cie_datat_origen", length = 20)
	private String cieDatatOrigen;

	@Column(name = "cie_datat_numseg", length = 50)
	private String cieDatatNumSeguiment;

	@Column(name = "cie_datat_errdes", length = 255)
	private String cieDatatErrorDescripcio;

	@Column(name = "cie_estat_dataact")
	@Temporal(TemporalType.TIMESTAMP)
	private Date cieEstatDataActualitzacio;

	public String getCieCertificacioArxiuNom() {
		return "certificacio_postal_" + cieId + ".pdf";
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
}
