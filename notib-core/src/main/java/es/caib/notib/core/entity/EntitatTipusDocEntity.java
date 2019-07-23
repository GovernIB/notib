/**
 * 
 */
package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_entitat_tipus_doc")
@EntityListeners(AuditingEntityListener.class)
public class EntitatTipusDocEntity extends NotibAuditable<Long> {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "not_entitat_tipus_doc_fk")
	protected EntitatEntity entitat;
	
	@Column(name = "tipus_doc")
	protected TipusDocumentEnumDto tipusDocEnum;
	
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public TipusDocumentEnumDto getTipusDocEnum() {
		return tipusDocEnum;
	}
	public String getEnumValue() {
		return tipusDocEnum.name();
	}
	public void update(
			EntitatEntity entitat,
			TipusDocumentEnumDto tipusDocEnum) {
		this.entitat = entitat;
		this.tipusDocEnum = tipusDocEnum;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			TipusDocumentEnumDto tipusDocEnum) {
		return new Builder(
				entitat,
				tipusDocEnum);
	}

	public static class Builder {
		EntitatTipusDocEntity built;
		Builder(
				EntitatEntity entitat,
				TipusDocumentEnumDto tipusDocEnum) {
			built = new EntitatTipusDocEntity();
			built.entitat = entitat;
			built.tipusDocEnum = tipusDocEnum;
		}
		public EntitatTipusDocEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
