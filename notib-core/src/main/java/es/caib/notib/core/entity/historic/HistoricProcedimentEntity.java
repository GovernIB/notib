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

import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import lombok.Getter;


@Getter
@Entity
@Table(name = "not_hist_procediment")
@EntityListeners(AuditingEntityListener.class)
public class HistoricProcedimentEntity extends HistoricEntity {

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_id")
	@ForeignKey(name = "not_procediment_not_fk")
	private ProcedimentEntity procediment;

	// Attribut de procediment
	@Column(name = "codi_sia", length = 64)
	private String codiSia;
	
	// Attribut de procediment
	@Column(name = "comu")
	protected boolean comu;

	// Attribut de procediment
	@Column(name = "nom", length = 64)
	private String nom;
	
	@Column(name = "n_grups", nullable = false)
	private Long numGrups;
	
	@Column(name = "n_perm_consulta", nullable = false)
	private Long numPermConsulta;
	
	@Column(name = "n_perm_notificacio", nullable = false)
	private Long numPermNotificacio;
	
	@Column(name = "n_perm_gestio", nullable = false)
	private Long numPermGestio;
	
	@Column(name = "n_perm_processar", nullable = false)
	private Long numPermProcessar;
	
	@Column(name = "n_perm_administrar", nullable = false)
	private Long numPermAdministrar;
	

	public HistoricProcedimentEntity(OrganGestorEntity organGestor, Date data, HistoricTipusEnumDto tipus) {
		super(organGestor, data, tipus);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4678638288953750091L;

}
