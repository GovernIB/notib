package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_document")
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
	
	@Column(name = "metadades")
	protected String metadades;
	
	@Column(name = "normalitzat")
	protected Boolean normalitzat;
	
	@Column(name = "generar_csv")
	protected Boolean generarCsv;
	
	@Column(name = "uuid", length = 256)
	protected String uuid;
	
	@Column(name = "csv", length = 256)
	protected String csv;
	
	public static Builder getBuilder(
			String arxiuId,
			String arxiuGestdocId,
			String arxiuNom,
			String hash,
			String url,
			String metadades,
			Boolean normalitzat,
			Boolean generarCsv
			) {
		return new Builder(
				arxiuId,
				arxiuGestdocId,
				arxiuNom,
				hash,
				url,
				metadades,
				normalitzat,
				generarCsv
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
				Boolean generarCsv
				) {
			built = new DocumentEntity();
			built.arxiuGestdocId = arxiuGestdocId;
			built.arxiuNom = arxiuNom;
			built.generarCsv = generarCsv;
			built.hash = hash;
			built.metadades = metadades;
			built.normalitzat = normalitzat;
			built.url = url;
			
		}
		public DocumentEntity build() {
			return built;
		}
	}
	
	
	public static BuilderV2 getBuilderV2(
			String arxiuId,
			String arxiuGestdocId,
			String arxiuNom,
			String hash,
			String url,
			String metadades,
			Boolean normalitzat,
			Boolean generarCsv,
			String uuid,
			String csv
			) {
		return new BuilderV2(
				arxiuId,
				arxiuGestdocId,
				arxiuNom,
				hash,
				url,
				metadades,
				normalitzat,
				generarCsv,
				uuid,
				csv);
	}
	
	public static class BuilderV2{
		DocumentEntity built;
		BuilderV2(
				String arxiuId,
				String arxiuGestdocId,
				String arxiuNom,
				String hash,
				String url,
				String metadades,
				Boolean normalitzat,
				Boolean generarCsv,
				String uuid,
				String csv
				) {
			built = new DocumentEntity();
			built.arxiuGestdocId = arxiuGestdocId;
			built.arxiuNom = arxiuNom;
			built.csv = csv;
			built.generarCsv = generarCsv;
			built.hash = hash;
			built.metadades = metadades;
			built.normalitzat = normalitzat;
			built.url = url;
			built.uuid = uuid;
		}
		public DocumentEntity build() {
			return built;
		}
	}
	

	public String getArxiuNom() {
		return arxiuNom;
	}

	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

	public String getContingutBase64() {
		return contingutBase64;
	}

	public void setContingutBase64(String contingutBase64) {
		this.contingutBase64 = contingutBase64;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMetadades() {
		return metadades;
	}

	public void setMetadades(String metadades) {
		this.metadades = metadades;
	}

	public Boolean getNormalitzat() {
		return normalitzat;
	}

	public void setNormalitzat(Boolean normalitzat) {
		this.normalitzat = normalitzat;
	}

	public Boolean getGenerarCsv() {
		return generarCsv;
	}

	public void setGenerarCsv(Boolean generarCsv) {
		this.generarCsv = generarCsv;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCsv() {
		return csv;
	}

	public void setCsv(String csv) {
		this.csv = csv;
	}

	public String getArxiuGestdocId() {
		return arxiuGestdocId;
	}

	public void setArxiuGestdocId(String arxiuGestdocId) {
		this.arxiuGestdocId = arxiuGestdocId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
