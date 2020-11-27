package es.caib.notib.core.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;
import lombok.Getter;

/**
 * Classe de model de dades que conté la relació procediment-òrgan per a assignar permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_pro_organ", uniqueConstraints = {@UniqueConstraint(columnNames = {"procediment_id", "organgestor_id"})})
@EntityListeners(AuditingEntityListener.class)
public class ProcedimentOrganEntity extends NotibAuditable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_id")
	@ForeignKey(name = "not_organ_pro_fk")
	protected ProcedimentEntity procediment;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organgestor_id")
	@ForeignKey(name = "not_pro_organ_fk")
	protected OrganGestorEntity organGestor;
	
	public void update(
			ProcedimentEntity procediment,
			OrganGestorEntity organGestor) {
		this.procediment = procediment;
		this.organGestor = organGestor;
	}
	
	public static Builder getBuilder(
			ProcedimentEntity procediment,
			OrganGestorEntity organGestor) {
		return new Builder(
				procediment,
				organGestor);
	}
	
	public static class Builder {
		ProcedimentOrganEntity built;
		Builder(
				ProcedimentEntity procediment,
				OrganGestorEntity organGestor) {
			built = new ProcedimentOrganEntity();
			built.procediment = procediment;
			built.organGestor = organGestor;
		}
		public ProcedimentOrganEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 7972079974219014546L;
}