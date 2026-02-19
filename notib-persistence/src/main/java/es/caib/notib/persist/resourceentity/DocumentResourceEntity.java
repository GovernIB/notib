package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.*;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.DocumentResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades d'un document adjunt d'una notificació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "document")
@Getter
@Setter
@NoArgsConstructor
public class DocumentResourceEntity extends BaseAuditableResourceEntity<DocumentResource> {

	@Column(name = "arxiu_gest_doc_id", length = 64)
	protected String arxiuGestdocId;
	@Column(name = "arxiu_nom", length = 256)
	protected String arxiuNom;
	@Column(name = "hash", length = 256)
	protected String hash;
	@Column(name = "normalitzat")
	protected Boolean normalitzat;
	@Column(name = "uuid", length = 256)
	protected String uuid;
	@Column(name = "csv", length = 256)
	protected String csv;
	@Column(name = "media", length = 256)
	private String mediaType;
	@Column(name = "mida")
	private Long mida;
	@Column(name = "origen", length = 20)
	@Enumerated(EnumType.STRING)
	private OrigenEnum origen;
	@Column(name = "validesa", length = 20)
	@Enumerated(EnumType.STRING)
	private ValidesaEnum validesa;
	@Column(name = "tipus_documental", length = 30)
	@Enumerated(EnumType.STRING)
	private TipusDocumentalEnum tipoDocumental;
	@Column(name = "firmat")
	private Boolean modoFirma;

	@Builder
	public DocumentResourceEntity(DocumentResource resource) {
		this.arxiuGestdocId = resource.getArxiuGestdocId();
		this.arxiuNom = resource.getArxiuNom();
		this.hash = resource.getHash();
		this.normalitzat = resource.getNormalitzat();
		this.uuid = resource.getUuid();
		this.csv = resource.getCsv();
		this.mediaType = resource.getMediaType();
		this.mida = resource.getMida();
		this.origen = resource.getOrigen();
		this.validesa = resource.getValidesa();
		this.tipoDocumental = resource.getTipoDocumental();
		this.modoFirma = resource.getModoFirma();
	}

}
