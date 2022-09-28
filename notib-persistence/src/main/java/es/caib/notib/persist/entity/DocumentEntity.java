package es.caib.notib.persist.entity;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Classe del model de dades que representa un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_document")
@EntityListeners(AuditingEntityListener.class)
public class DocumentEntity  extends NotibAuditable<Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "arxiu_gest_doc_id", length = 64)
	protected String arxiuGestdocId;
	
	@Column(name = "arxiu_nom", length = 256)
	protected String arxiuNom;
	
	@Transient
	protected String contingutBase64;
	
	@Column(name = "hash", length = 256)
	protected String hash;
	
	@Column(name = "url", length = 256)
	protected String url;
	
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

	// Metadades
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
	
	public void update (
			String arxiuGestdocId,
			String arxiuNom,
			String url,
			Boolean normalitzat,
			String uuid,
			String csv,
			String media,
			Long mida,
			OrigenEnum origen,
			ValidesaEnum validesa,
			TipusDocumentalEnum tipoDocumental,
			Boolean modoFirma) {
		this.arxiuGestdocId = arxiuGestdocId;
		this.arxiuNom = arxiuNom;
		this.url = url;
		this.normalitzat = normalitzat;
		this.uuid = uuid;
		this.csv = csv;
		this.mediaType = media;
		this.mida = mida;
		this.origen = origen;
		this.validesa = validesa;
		this.tipoDocumental = tipoDocumental;
		this.modoFirma = modoFirma;
	}
	
	public static Builder getBuilder(
			String arxiuId,
			String arxiuGestdocId,
			String arxiuNom,
			String hash,
			String url,
			String metadades,
			Boolean normalitzat,
			Boolean generarCsv,
			String media,
			Long mida,
			OrigenEnum origen,
			ValidesaEnum validesa,
			TipusDocumentalEnum tipoDocumental,
			Boolean modoFirma
			) {
		return new Builder(
				arxiuId,
				arxiuGestdocId,
				arxiuNom,
				hash,
				url,
				metadades,
				normalitzat,
				generarCsv,
				media,
				mida,
				origen,
				validesa,
				tipoDocumental,
				modoFirma
				);
	}
	
	public static class Builder{
		DocumentEntity built;
		Builder(
				String arxiuId,
				String arxiuGestdocId,
				String arxiuNom,
				String hash,
				String url,
				String metadades,
				Boolean normalitzat,
				Boolean generarCsv,
				String media,
				Long mida,
				OrigenEnum origen,
				ValidesaEnum validesa,
				TipusDocumentalEnum tipoDocumental,
				Boolean modoFirma
				) {
			built = new DocumentEntity();
			built.arxiuGestdocId = arxiuGestdocId;
			built.arxiuNom = arxiuNom;
			built.hash = hash;
			built.normalitzat = normalitzat;
			built.url = url;
			built.mediaType = media;
			built.mida = mida;
			built.origen = origen;
			built.validesa = validesa;
			built.tipoDocumental = tipoDocumental;
			built.modoFirma = modoFirma;
			
		}
		public DocumentEntity build() {
			return built;
		}
	}
	
	
	public static BuilderV2 getBuilderV2(
			String arxiuId,
			String arxiuGestdocId,
			String arxiuNom,
			String url,
			Boolean normalitzat,
			String uuid,
			String csv,
			String media,
			Long mida,
			OrigenEnum origen,
			ValidesaEnum validesa,
			TipusDocumentalEnum tipoDocumental,
			Boolean modoFirma
			) {
		return new BuilderV2(
				arxiuId,
				arxiuGestdocId,
				arxiuNom,
				url,
				normalitzat,
				uuid,
				csv,
				media,
				mida,
				origen,
				validesa,
				tipoDocumental,
				modoFirma);
	}
	
	public static class BuilderV2{
		DocumentEntity built;
		BuilderV2(
				String arxiuId,
				String arxiuGestdocId,
				String arxiuNom,
				String url,
				Boolean normalitzat,
				String uuid,
				String csv,
				String media,
				Long mida,
				OrigenEnum origen,
				ValidesaEnum validesa,
				TipusDocumentalEnum tipoDocumental,
				Boolean modoFirma
				) {
			built = new DocumentEntity();
			built.arxiuGestdocId = arxiuGestdocId;
			built.arxiuNom = arxiuNom;
			built.csv = csv;
//			String metadadesStr = "";
//			Iterator it = metadades.entrySet().iterator();
//		    while (it.hasNext()) {
//		        Map.Entry pair = (Map.Entry)it.next();
//		        metadadesStr += "<" + pair.getKey()+ ">" + pair.getValue() + "</" + pair.getKey() + ">";
//		    }
//			built.metadades = metadadesStr;
			built.normalitzat = normalitzat;
			built.url = url;
			built.uuid = uuid;
			built.mediaType = media;
			built.mida = mida;
			built.origen = origen;
			built.validesa = validesa;
			built.tipoDocumental = tipoDocumental;
			built.modoFirma = modoFirma;
		}
		public DocumentEntity build() {
			return built;
		}
	}

	public void updateId(Long id) {
		setId(id);
	}

	public String getArxiuNom() {
		if (arxiuNom != null && !arxiuNom.isEmpty()) {
			return arxiuNom;
		}

		if (csv != null && !csv.isEmpty()) {
			return this.csv + ".pdf";
		}

		if (uuid != null && !uuid.isEmpty()) {
			return this.uuid + ".pdf";
		}

		return this.getId().toString() + ".pdf";
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
