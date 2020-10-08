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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.Getter;

/**
 * Classe del model de dades que representa una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name="not_entitat", 
	uniqueConstraints = {
			@UniqueConstraint(columnNames = "codi"),
			@UniqueConstraint(columnNames = "dir3_codi")
	}
)
@EntityListeners(AuditingEntityListener.class)
public class EntitatEntity extends NotibAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "tipus", length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private EntitatTipusEnumDto tipus;
	@Column(name = "dir3_codi", length = 9, nullable = false, unique = true)
	private String dir3Codi;
	@Column(name = "dir3_codi_reg", length = 9)
	private String dir3CodiReg;
	@Column(name = "api_key", length = 64, nullable = false)
	private String apiKey;
	@Column(name = "amb_entrega_deh", nullable = false)
	private boolean ambEntregaDeh;
	@Column(name = "amb_entrega_cie", nullable = false)
	private boolean ambEntregaCie;
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
	@Column(name = "nom_oficina_virtual", length = 255)
	private String nomOficinaVirtual;
	@Column(name = "oficina", length = 255)
	private String oficina;
	@Column(name = "llibre_entitat")
	private boolean llibreEntitat;
	@Column(name = "llibre")
	protected String llibre;
	@Column(name = "llibre_nom")
	protected String llibreNom;

	@Version
	private long version = 0;

	public void update(
			String codi,
			String nom,
			EntitatTipusEnumDto tipus,
			String dir3Codi,
			String dir3CodiReg,
			String apiKey,
			boolean ambEntregaDeh,
			boolean ambEntregaCie,
			String descripcio,
			byte[] logoCapBytes,
			byte[] logoPeuBytes,
			String colorFons,
			String colorLletra,
			TipusDocumentEnumDto tipusDocDefault,
			String oficina,
			String nomOficinaVirtual,
			boolean llibreEntitat,
			String llibre,
			String llibreNom) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.dir3Codi = dir3Codi;
		this.dir3CodiReg = dir3CodiReg;
		this.apiKey = apiKey;
		this.ambEntregaDeh = ambEntregaDeh;
		this.ambEntregaCie = ambEntregaCie;
		this.logoCapBytes = logoCapBytes;
		this.logoPeuBytes = logoPeuBytes;
		this.colorFons = colorFons;
		this.colorLletra = colorLletra;
		this.tipusDocDefault = tipusDocDefault;
		this.oficina = oficina;
		this.nomOficinaVirtual = nomOficinaVirtual;
		this.llibreEntitat = llibreEntitat;
		this.llibre = llibre;
		this.llibreNom = llibreNom;
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
			boolean ambEntregaCie,
			byte[] logoCapBytes,
			byte[] logoPeuBytes,
			String colorFons,
			String colorLletra,
			TipusDocumentEnumDto tipusDocDefault,
			String oficina,
			String nomOficinaVirtual,
			boolean llibreEntitat,
			String llibre,
			String llibreNom) {
		return new Builder(
				codi,
				nom,
				tipus,
				dir3Codi,
				dir3CodiReg,
				apiKey,
				ambEntregaDeh,
				ambEntregaCie,
				logoCapBytes,
				logoPeuBytes,
				colorFons,
				colorLletra,
				tipusDocDefault,
				oficina,
				nomOficinaVirtual,
				llibreEntitat,
				llibre,
				llibreNom);
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
				boolean ambEntregaCie,
				byte[] logoCapBytes,
				byte[] logoPeuBytes,
				String colorFons,
				String colorLletra,
				TipusDocumentEnumDto tipusDocDefault,
				String oficina,
				String nomOficinaVirtual,
				boolean llibreEntitat,
				String llibre,
				String llibreNom) {
			built = new EntitatEntity();
			built.codi = codi;
			built.nom = nom;
			built.tipus = tipus;
			built.dir3Codi = dir3Codi;
			built.dir3CodiReg = dir3CodiReg;
			built.activa = true;
			built.apiKey = apiKey;
			built.ambEntregaDeh = ambEntregaDeh;
			built.ambEntregaCie = ambEntregaCie;
			built.logoCapBytes = logoCapBytes;
			built.logoPeuBytes = logoPeuBytes;
			built.colorFons = colorFons;
			built.colorLletra = colorLletra;
			built.tipusDocDefault = tipusDocDefault;
			built.oficina = oficina;
			built.nomOficinaVirtual = nomOficinaVirtual;
			built.llibreEntitat = llibreEntitat;
			built.llibre = llibre;
			built.llibreNom = llibreNom;
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
		result = prime * result + ((dir3Codi == null) ? 0 : dir3Codi.hashCode());
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
		if (dir3Codi == null) {
			if (other.dir3Codi != null)
				return false;
		} else if (!dir3Codi.equals(other.dir3Codi))
			return false;
		return true;
	}


	private static final long serialVersionUID = -2299453443943600172L;

}
