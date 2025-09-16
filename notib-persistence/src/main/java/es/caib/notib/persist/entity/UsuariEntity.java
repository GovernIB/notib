/**
 * 
 */
package es.caib.notib.persist.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Classe de model de dades que conté la informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "not_usuari")
public class UsuariEntity implements Serializable {

	@Id
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 100)
	private String nom;
	@Column(name = "nif", length = 40)
	private String nif;
	@Column(name = "llinatges", length = 100)
	private String llinatges;
	@Column(name = "nom_sencer", length = 200)
	private String nomSencer;
	@Column(name = "email", length = 200)
	private String email;
	@Column(name = "email_alt", length = 200)
	private String emailAlt;
	@Column(name = "rebre_emails")
	@Builder.Default
	private boolean rebreEmailsNotificacio = true;
	@Column(name = "rebre_emails_creats")
	@Builder.Default
	private boolean rebreEmailsNotificacioCreats = true;
	@Column(name = "ultim_rol", length = 40)
	private String ultimRol;
	@Column(name = "ultima_entitat")
	private Long ultimaEntitat;
	@Column(name="idioma", length = 2)
	private String idioma;
	@Column(name="NUM_ELEMENTS_PAGINA_DEFECTE", length=3)
	private String numElementsPaginaDefecte;
    @Column(name = "entitat_defecte")
    protected Long entitatDefecte;
    @Column(name = "organ_defecte", length = 64)
    protected String organDefecte;
    @Column(name = "procediment_defecte")
    protected Long procedimentDefecte;


    @Version
	private long version = 0;

	public void update(String nom, String llinatges, String nif, String email) {
		this.nom = nom;
		this.llinatges = llinatges;
		this.nif = nif;
		this.email = email;
	}
	public void update(String nomSencer, String nif, String email) {
		this.nomSencer = nomSencer;
		this.nif = nif;
		this.email = email;
	}
	
	public void update(UsuariEntity usuari) {

		rebreEmailsNotificacio = usuari.isRebreEmailsNotificacio();
		rebreEmailsNotificacioCreats = usuari.isRebreEmailsNotificacioCreats();
		idioma = usuari.getIdioma();
		emailAlt = usuari.getEmailAlt();
		numElementsPaginaDefecte = usuari.numElementsPaginaDefecte;
        entitatDefecte = usuari.getEntitatDefecte();
        organDefecte = usuari.getOrganDefecte();
        procedimentDefecte = usuari.getProcedimentDefecte();
	}
	
	public void updateUltimRol(String ultimRol) {
		this.ultimRol = ultimRol;
	}

	public void updateUltimaEntitat(Long ultimaEntitat) {
		this.ultimaEntitat = ultimaEntitat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuariEntity other = (UsuariEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	public String getNomSencer() {

		if (nomSencer != null) {
			return nomSencer;
		}
		if (nom == null) {
			return null;
		}
		return llinatges != null ? nom + " " + llinatges :nom;
	}

	private static final long serialVersionUID = -6657066865382086237L;

}
