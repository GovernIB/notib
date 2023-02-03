package es.caib.notib.persist.entity.cie;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.persist.audit.NotibAuditable;

/**
 * Classe de model de dades que conté la informació dels pagadors CIE.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_formats_sobre")
@EntityListeners(AuditingEntityListener.class)
public class PagadorCieFormatSobreEntity extends NotibAuditable<Long> {

	@Column(name = "codi", length = 64)
	private String codi;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pagador_cie_id")
	@ForeignKey(name = "not_formats_sobre_fk")
	private PagadorCieEntity pagadorCie;

	
	public String getCodi() {
		return codi;
	}
	public PagadorCieEntity getPagadorCie() {
		return pagadorCie;
	}

	public void update(String codi) {
		this.codi = codi;
	}
	
	public static Builder getBuilder(String codi, PagadorCieEntity pagadorCie) {
		return new Builder(codi, pagadorCie);
	}
	
	public static class Builder {
		PagadorCieFormatSobreEntity built;
		Builder(String codi, PagadorCieEntity pagadorCie) {
			built = new PagadorCieFormatSobreEntity();
			built.codi = codi;
			built.pagadorCie = pagadorCie;
		}
		public PagadorCieFormatSobreEntity build() {
			return built;
		}
	}
	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PagadorCieFormatSobreEntity other = (PagadorCieFormatSobreEntity) obj;
		if (codi == null) {
			if (other.codi != null) {
				return false;
			}
		} else if (!codi.equals(other.codi)) {
			return false;
		}
		return true;
	}
	
	
	
	public void setDir3codi(String codi) {
		this.codi = codi;
	}

	public void setContracteDataVig(PagadorCieEntity pagadorCie) {
		this.pagadorCie = pagadorCie;
	}



	private static final long serialVersionUID = 8596990469127710436L;
	
}
