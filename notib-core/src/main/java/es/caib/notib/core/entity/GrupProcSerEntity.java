package es.caib.notib.core.entity;

import es.caib.notib.core.audit.NotibAuditable;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe de model de dades que conté la informació dels grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_pro_grup")
@EntityListeners(AuditingEntityListener.class)
public class GrupProcSerEntity extends NotibAuditable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "procediment")
	@ForeignKey(name = "not_pro_grup_fk")
	protected ProcSerEntity procser;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "grup")
	@ForeignKey(name = "not_grup_pro_fk")
	protected GrupEntity grup;
	
	
	public ProcSerEntity getProcser() {
		return procser;
	}
	public void setProcser(ProcSerEntity procediment) {
		this.procser = procediment;
	}
	public GrupEntity getGrup() {
		return grup;
	}
	public void setGrup(GrupEntity grup) {
		this.grup = grup;
	}
	
	public void update(
			ProcSerEntity procser,
			GrupEntity grup) {
		this.procser = procser;
		this.grup = grup;
	}
	
	public static Builder getBuilder(
			ProcSerEntity procser,
			GrupEntity grup) {
		return new Builder(
				procser,
				grup);
	}
	
	public static class Builder {
		GrupProcSerEntity built;
		Builder(
				ProcSerEntity procser,
				GrupEntity grup) {
			built = new GrupProcSerEntity();
			built.procser = procser;
			built.grup = grup;
		}
		public GrupProcSerEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = -4924926921877674490L;

}
