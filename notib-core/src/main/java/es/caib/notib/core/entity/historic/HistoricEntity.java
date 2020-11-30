package es.caib.notib.core.entity.historic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.core.audit.NotibAuditable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import lombok.Getter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditingEntityListener.class)
public abstract class HistoricEntity extends NotibAuditable<Long> {
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "not_historic_entitat_fk")
	protected EntitatEntity entitat;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organ_id")
	@ForeignKey(name = "not_historic_entitat_fk")
	protected OrganGestorEntity organGestor;
	
	@Column(name = "data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date data;
	
	@Column(name = "tipus", nullable = false)
	protected HistoricTipusEnumDto tipus;
	
	@Version
	private long version = 0;
	
	public HistoricEntity(OrganGestorEntity organGestor, Date data, HistoricTipusEnumDto tipus) {
		super();
		this.organGestor = organGestor;
		this.entitat = organGestor.getEntitat();
		this.data = data;
		this.tipus = tipus;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = -2762985010335092420L;
}
