/**
 * 
 */
package es.caib.notib.persist.entity;

import es.caib.notib.logic.intf.dto.EntitatTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.persist.audit.NotibAuditable;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe del model de dades que representa una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor @AllArgsConstructor
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
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "ENTREGA_CIE_ID")
	@ForeignKey(name = "NOT_ENTITAT_ENTREGA_CIE_FK")
	private EntregaCieEntity entregaCie;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "activa", nullable = false)
	private boolean activa;
//	@Lob
	@Column(name = "logo_cap")
	private byte[] logoCapBytes;
//	@Lob
	@Column(name = "logo_peu")
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
	@Column(name = "oficina_entitat")
	private boolean oficinaEntitat;
	@Setter
	@Column(name = "data_sincronitzacio")
	@Temporal(TemporalType.TIMESTAMP)
	Date dataSincronitzacio;
	@Setter
	@Column(name = "data_actualitzacio")
	@Temporal(TemporalType.TIMESTAMP)
	Date dataActualitzacio;

	@Version
	private long version = 0;

	public void update(
			String nom,
			EntitatTipusEnumDto tipus,
			String dir3Codi,
			String dir3CodiReg,
			String apiKey,
			boolean ambEntregaDeh,
			EntregaCieEntity entregaCie,
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
			String llibreNom,
			boolean oficinaEntitat) {

		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.dir3Codi = dir3Codi;
		this.dir3CodiReg = dir3CodiReg;
		this.apiKey = apiKey;
		this.ambEntregaDeh = ambEntregaDeh;
		this.entregaCie = entregaCie;
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
		this.oficinaEntitat = oficinaEntitat;
	}

	public void updateActiva(boolean activa) {
		this.activa = activa;
	}
	
	public static EntitatEntityBuilder getBuilder(
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
			TipusDocumentEnumDto tipusDocDefault,
			String oficina,
			String nomOficinaVirtual,
			boolean llibreEntitat,
			String llibre,
			String llibreNom,
			boolean oficinaEntitat) {
		return hiddenBuilder()
				.codi(codi)
				.nom(nom)
				.tipus(tipus)
				.dir3Codi(dir3Codi)
				.dir3CodiReg(dir3CodiReg)
				.apiKey(apiKey)
				.ambEntregaDeh(ambEntregaDeh)
				.logoCapBytes(logoCapBytes)
				.logoPeuBytes(logoPeuBytes)
				.colorFons(colorFons)
				.colorLletra(colorLletra)
				.tipusDocDefault(tipusDocDefault)
				.oficina(oficina)
				.nomOficinaVirtual(nomOficinaVirtual)
				.llibreEntitat(llibreEntitat)
				.llibre(llibre)
				.llibreNom(llibreNom)
				.oficinaEntitat(oficinaEntitat)
				.activa(true);
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
			return other.dir3Codi == null;
		} else
			return dir3Codi.equals(other.dir3Codi);
	}


	private static final long serialVersionUID = -2299453443943600172L;

}
