package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.model.EntitatTipusDocumentResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades pels recursos de tipus de documents associats a una entitat.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "entitat_tipus_doc")
@Getter
@Setter
@NoArgsConstructor
public class EntitatTipusDocumentResourceEntity extends BaseAuditableResourceEntity<EntitatTipusDocumentResource> {

	@Column(name = "tipus_doc", nullable = false)
	protected TipusDocumentEnumDto tipusDocument;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "not_entitat_tipus_doc_fk"),
			nullable = false)
	private EntitatResourceEntity entitat;

	@Builder
	public EntitatTipusDocumentResourceEntity(
			EntitatTipusDocumentResource resource,
			EntitatResourceEntity entitat) {
		this.tipusDocument = resource.getTipusDocument();
		this.entitat = entitat;
	}

}
