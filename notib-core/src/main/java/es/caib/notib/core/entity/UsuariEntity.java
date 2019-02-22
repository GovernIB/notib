/**
 * 
 */
package es.caib.notib.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Classe de model de dades que conté la informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_usuari")
public class UsuariEntity implements Serializable {

	@Id
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 100)
	private String nom;
	@Column(name = "llinatges", length = 100)
	private String llinatges;
	@Column(name = "nom_sencer", length = 200)
	private String nomSencer;
	@Column(name = "email", length = 200)
	private String email;
	@Version
	private long version = 0;

	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public String getLlinatges() {
		return llinatges;
	}
	public String getNomSencer() {
		return nomSencer;
	}
	public String getEmail() {
		return email;
	}

	public void update(
			String nom,
			String llinatges,
			String email) {
		this.nom = nom;
		this.llinatges = llinatges;
		this.email = email;
	}
	public void update(
			String nomSencer,
			String email) {
		this.nomSencer = nomSencer;
		this.email = email;
	}

	public static Builder getBuilder(
			String codi,
			String email) {
		return new Builder(
				codi,
				email);
	}

	public static class Builder {
		UsuariEntity built;
		Builder(String codi,
				String email) {
			built = new UsuariEntity();
			built.codi = codi;
			built.email = email;
		}
		public Builder nom(String nom) {
			built.nom = nom;
			return this;
		}
		public Builder llinatges(String llinatges) {
			built.llinatges = llinatges;
			return this;
		}
		public Builder nomSencer(String nomSencer) {
			built.nomSencer = nomSencer;
			return this;
		}
		public UsuariEntity build() {
			return built;
		}
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -6657066865382086237L;

}
