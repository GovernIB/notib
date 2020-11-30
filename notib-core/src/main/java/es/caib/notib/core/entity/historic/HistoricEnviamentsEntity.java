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

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

//import lombok.Getter;

//@Getter
@Entity
@Table(name = "not_hist_enviaments")
@EntityListeners(AuditingEntityListener.class)
public class HistoricEnviamentsEntity extends HistoricEntity {

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
	
	@Column(name = "estat_not", nullable = false)
	protected NotificacioEnviamentEstatEnumDto estatNot;
	

	/**
	 * Mètriques 	
	 */
	
	@Column(name = "n_total", nullable = false)
	private Long numTotal;
	
	@Column(name = "n_correctes", nullable = false)
	private Long numCorrectes;
	
	@Column(name = "n_amb_error", nullable = false)
	private Long numAmbError;
	
	@Column(name = "n_origen_api", nullable = false)
	private Long numOrigenApi;
	
	@Column(name = "n_origen_web", nullable = false)
	private Long numOrigenWeb;
	
	@Column(name = "n_desti_adm", nullable = false)
	private Long numDestiAdm;
	
	@Column(name = "n_desti_ciutada", nullable = false)
	private Long numDestiCiutada;
	

	public HistoricEnviamentsEntity(OrganGestorEntity organGestor, Date data, HistoricTipusEnumDto tipus) {
		super(organGestor, data, tipus);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4478326381810146935L;

}
