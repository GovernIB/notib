package es.caib.notib.core.entity.historic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "not_hist_notif")
@EntityListeners(AuditingEntityListener.class)
public class HistoricNotificacioEntity extends HistoricEntity {

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_id")
	@ForeignKey(name = "not_procediment_not_fk")
	private ProcedimentEntity procediment;

	// Attribut de procediment
	@Column(name = "comu")
	protected boolean comu;

	// Attribut de notificacio
	@Column(name = "usuari_codi", length = 64, nullable = false)
	protected String usuariCodi;

	// Attribut de notificacio
	@Column(name = "grup_codi", length = 64)
	protected String grupCodi;

	// Attribut de notificacio
	@Column(name = "estat", nullable = false)
	protected NotificacioEstatEnumDto estat;

	/**
	 * Mètriques comunes
	 */

	@Column(name = "n_not_total", nullable = false)
	private Long numNotTotal;

	@Column(name = "n_not_correctes", nullable = false)
	private Long numNotCorrectes;

	@Column(name = "n_not_amb_error", nullable = false)
	private Long numNotAmbError;

	@Column(name = "n_not_origen_api", nullable = false)
	private Long numNotOrigenApi;

	@Column(name = "n_not_origen_web", nullable = false)
	private Long numNotOrigenWeb;

	@Column(name = "n_not_desti_adm", nullable = false)
	private Long numNotDestiAdm;

	@Column(name = "n_not_desti_ciutada", nullable = false)
	private Long numNotDestiCiutada;

	@Column(name = "n_com_total", nullable = false)
	private Long numComTotal;

	@Column(name = "n_com_correctes", nullable = false)
	private Long numComCorrectes;

	@Column(name = "n_com_amb_error", nullable = false)
	private Long numComAmbError;

	@Column(name = "n_com_origen_api", nullable = false)
	private Long numComOrigenApi;

	@Column(name = "n_com_origen_web", nullable = false)
	private Long numComOrigenWeb;

	@Column(name = "n_com_desti_adm", nullable = false)
	private Long numComDestiAdm;

	@Column(name = "n_com_desti_ciutada", nullable = false)
	private Long numComDestiCiutada;

	public HistoricNotificacioEntity(
			Date data,
			HistoricTipusEnumDto tipus,
			NotificacioEntity not) {
		super(not.getOrganGestor(), data, tipus);
		procediment = not.getProcediment();
		if (procediment != null)
			comu = procediment.isComu();
		
		usuariCodi = not.getUsuariCodi();
		grupCodi = not.getGrupCodi();
		estat = not.getEstat();

		numNotTotal = 0L;
		numNotCorrectes = 0L;
		numNotAmbError = 0L;
		numNotOrigenApi = 0L;
		numNotOrigenWeb = 0L;
		numNotDestiAdm = 0L;
		numNotDestiCiutada = 0L;
		numComTotal = 0L;
		numComCorrectes = 0L;
		numComAmbError = 0L;
		numComOrigenApi = 0L;
		numComOrigenWeb = 0L;
		numComDestiAdm = 0L;
		numComDestiCiutada = 0L;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4478326381810146935L;

}
