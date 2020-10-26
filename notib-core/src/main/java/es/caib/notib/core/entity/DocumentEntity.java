package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;
import lombok.Getter;

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
	
//	@Column(name = "metadades")
//	protected String metadades;
	
	@Column(name = "normalitzat")
	protected Boolean normalitzat;
	
//	@Column(name = "generar_csv")
//	protected Boolean generarCsv;
	
	@Column(name = "uuid", length = 256)
	protected String uuid;
	
	@Column(name = "csv", length = 256)
	protected String csv;
	
	@Column(name = "media", length = 256)
	private String mediaType;
	
	@Column(name = "mida")
	private Long mida;
	
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
			Long mida
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
				mida
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
				Long mida
				) {
			built = new DocumentEntity();
			built.arxiuGestdocId = arxiuGestdocId;
			built.arxiuNom = arxiuNom;
			built.hash = hash;
			built.normalitzat = normalitzat;
			built.url = url;
			built.mediaType = media;
			built.mida = mida;
			
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
			Long mida
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
				mida);
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
				Long mida
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
		}
		public DocumentEntity build() {
			return built;
		}
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
