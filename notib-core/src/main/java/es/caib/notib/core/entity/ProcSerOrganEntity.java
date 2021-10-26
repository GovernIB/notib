package es.caib.notib.core.entity;

import es.caib.notib.core.audit.NotibAuditable;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe de model de dades que conté la relació procediment-òrgan per a assignar permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_pro_organ", uniqueConstraints = {@UniqueConstraint(columnNames = {"procediment_id", "organgestor_id"})})
@EntityListeners(AuditingEntityListener.class)
public class ProcSerOrganEntity extends NotibAuditable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "procediment_id")
	@ForeignKey(name = "not_organ_pro_fk")
	protected ProcSerEntity procSer;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "organgestor_id")
	@ForeignKey(name = "not_pro_organ_fk")
	protected OrganGestorEntity organGestor;
	
	public void update(
			ProcSerEntity procser,
			OrganGestorEntity organGestor) {
		this.procSer = procser;
		this.organGestor = organGestor;
	}
	
	public static Builder getBuilder(
			ProcSerEntity procser,
			OrganGestorEntity organGestor) {
		return new Builder(
				procser,
				organGestor);
	}
	
	public static class Builder {
		ProcSerOrganEntity built;
		Builder(
				ProcSerEntity procser,
				OrganGestorEntity organGestor) {
			built = new ProcSerOrganEntity();
			built.procSer = procser;
			built.organGestor = organGestor;
		}
		public ProcSerOrganEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 7972079974219014546L;
}