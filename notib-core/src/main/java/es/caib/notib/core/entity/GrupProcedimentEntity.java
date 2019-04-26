package es.caib.notib.core.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe de model de dades que conté la informació dels grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_pro_grup")
@EntityListeners(AuditingEntityListener.class)
public class GrupProcedimentEntity extends NotibAuditable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "procediment")
	@ForeignKey(name = "not_pro_grup_fk")
	protected ProcedimentEntity procediment;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "grup")
	@ForeignKey(name = "not_grup_pro_fk")
	protected GrupEntity grup;
	
	
	public ProcedimentEntity getProcediment() {
		return procediment;
	}
	public GrupEntity getGrup() {
		return grup;
	}
	public void setProcediment(ProcedimentEntity procediment) {
		this.procediment = procediment;
	}
	public void setGrup(GrupEntity grup) {
		this.grup = grup;
	}
	
	public void update(
			ProcedimentEntity procediment,
			GrupEntity grup) {
		this.procediment = procediment;
		this.grup = grup;
	}
	
	public static Builder getBuilder(
			ProcedimentEntity procediment,
			GrupEntity grup) {
		return new Builder(
				procediment,
				grup);
	}
	
	public static class Builder {
		GrupProcedimentEntity built;
		Builder(
				ProcedimentEntity procediment,
				GrupEntity grup) {
			built = new GrupProcedimentEntity();
			built.procediment = procediment;
			built.grup = grup;
		}
		public GrupProcedimentEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = -4924926921877674490L;

}
