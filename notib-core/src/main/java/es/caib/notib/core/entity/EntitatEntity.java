/**
 * 
 */
package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_entitat")
@EntityListeners(AuditingEntityListener.class)
public class EntitatEntity extends NotibAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "tipus", length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private EntitatTipusEnumDto tipus;
	@Column(name = "dir3_codi", length = 9, nullable = false)
	private String dir3Codi;
	@Column(name = "dir3_codi_reg", length = 9)
	private String dir3CodiReg;
	@Column(name = "api_key", length = 64, nullable = false)
	private String apiKey;
	@Column(name = "amb_entrega_deh", nullable = false)
	private boolean ambEntregaDeh;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "activa", nullable = false)
	private boolean activa;
	@Column(name = "logo_cap", length = 1024)
	private byte[] logoCapBytes;
	@Column(name = "logo_peu", length = 1024)
	private byte[] logoPeuBytes;
	@Column(name = "color_fons", length = 1024)
	private String colorFons;
	@Column(name = "color_lletra", length = 1024)
	private String colorLletra;
	@Column(name = "tipus_doc_default")
	private TipusDocumentEnumDto tipusDocDefault;
	
	@Version
	private long version = 0;

	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public EntitatTipusEnumDto getTipus() {
		return tipus;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public String getApiKey() {
		return apiKey;
	}
	public boolean isAmbEntregaDeh() {
		return ambEntregaDeh;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public boolean isActiva() {
		return activa;
	}
	public byte[] getLogoCapBytes() {
		return logoCapBytes;
	}
	public byte[] getLogoPeuBytes() {
		return logoPeuBytes;
	}
	public String getColorFons() {
		return colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public long getVersion() {
		return version;
	}
	public TipusDocumentEnumDto getTipusDocDefault() {
		return tipusDocDefault;
	}
	public String getDir3CodiReg() {
		return dir3CodiReg;
	}
	public void update(
			String codi,
			String nom,
			EntitatTipusEnumDto tipus,
			String dir3Codi,
			String dir3CodiReg,
			String apiKey,
			boolean ambEntregaDeh,
			String descripcio,
			byte[] logoCapBytes,
			byte[] logoPeuBytes,
			String colorFons,
			String colorLletra,
			TipusDocumentEnumDto tipusDocDefault) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.dir3Codi = dir3Codi;
		this.dir3CodiReg = dir3CodiReg;
		this.apiKey = apiKey;
		this.ambEntregaDeh = ambEntregaDeh;
		this.logoCapBytes = logoCapBytes;
		this.logoPeuBytes = logoPeuBytes;
		this.colorFons = colorFons;
		this.colorLletra = colorLletra;
		this.tipusDocDefault = tipusDocDefault;
	}

	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}
	
	public static Builder getBuilder(
			String codi,
			String nom,
			EntitatTipusEnumDto tipus,
			String dir3Codi,
			String dir3CodiReg,
			String apiKey,
			boolean ambEntregaDeh,
			byte[] logoCapBytes,
			byte[] logoPeuBytes,
			String colorFons,
			String colorLletra,
			TipusDocumentEnumDto tipusDocDefault) {
		return new Builder(
				codi,
				nom,
				tipus,
				dir3Codi,
				dir3CodiReg,
				apiKey,
				ambEntregaDeh,
				logoCapBytes,
				logoPeuBytes,
				colorFons,
				colorLletra,
				tipusDocDefault);
	}

	public static class Builder {
		EntitatEntity built;
		Builder(
				String codi,
				String nom,
				EntitatTipusEnumDto tipus,
				String dir3Codi,
				String dir3CodiReg,
				String apiKey,
				boolean ambEntregaDeh,
				byte[] logoCapBytes,
				byte[] logoPeuBytes,
				String colorFons,
				String colorLletra,
				TipusDocumentEnumDto tipusDocDefault) {
			built = new EntitatEntity();
			built.codi = codi;
			built.nom = nom;
			built.tipus = tipus;
			built.dir3Codi = dir3Codi;
			built.dir3CodiReg = dir3CodiReg;
			built.activa = true;
			built.apiKey = apiKey;
			built.ambEntregaDeh = ambEntregaDeh;
			built.logoCapBytes = logoCapBytes;
			built.logoPeuBytes = logoPeuBytes;
			built.colorFons = colorFons;
			built.colorLletra = colorLletra;
			built.tipusDocDefault = tipusDocDefault;
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public EntitatEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntitatEntity other = (EntitatEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
