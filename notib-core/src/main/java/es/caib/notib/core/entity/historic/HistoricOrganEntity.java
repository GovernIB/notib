package es.caib.notib.core.entity.historic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import lombok.Getter;

@Getter
@Entity
@Table(name = "not_hist_organ")
@EntityListeners(AuditingEntityListener.class)
public class HistoricOrganEntity extends HistoricEntity {

	@Column(name = "codi_dir3", length = 64)
	private String codiDir3;

	@Column(name = "nom", length = 64)
	private String nom;
	
	@Column(name = "n_procediments", nullable = false)
	private Long numProcediments;
	
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

	public HistoricOrganEntity(OrganGestorEntity organGestor, Date data, HistoricTipusEnumDto tipus) {
		super(organGestor, data, tipus);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6721540388968377929L;

}
