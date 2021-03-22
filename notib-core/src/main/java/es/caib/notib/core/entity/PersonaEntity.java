package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.audit.NotibAuditable;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe del model de dades que representa una persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_persona")
@EntityListeners(AuditingEntityListener.class)
public class PersonaEntity extends NotibAuditable<Long> {
	
	@Column(name = "interessattipus", nullable = false)
	@Enumerated(EnumType.STRING)
	private InteressatTipusEnumDto interessatTipus;
	@Column(name = "incapacitat")
	private boolean incapacitat;
	@Column(name = "email", length = 255)
	private String email;
	@Column(name = "llinatge1", length = 40)
	private String llinatge1;
	@Column(name = "llinatge2", length = 40)
	private String llinatge2;
	@Column(name = "nif", length = 9)
	private String nif;
	@Column(name = "nom", length = 255)
	private String nom;
	@Column(name = "telefon", length = 16)
	private String telefon;
	@Column(name = "rao_social", length = 100)
	private String raoSocial;
	@Column(name = "cod_entitat_desti", length = 9)
	private String dir3Codi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notificacio_env_id")
	@ForeignKey(name = "NOT_PERSONA_NOT_FK")
	private NotificacioEnviamentEntity enviament;
	
	public InteressatTipusEnumDto getInteressatTipus() {
		return interessatTipus;
	}
	public void setInteressatTipus(InteressatTipusEnumDto interessatTipus) {
		this.interessatTipus = interessatTipus;
	}
	public boolean isIncapacitat() {
		return incapacitat;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public NotificacioEnviamentEntity getEnviament() {
		return enviament;
	}
	public void setEnviament(NotificacioEnviamentEntity enviament) {
		this.enviament = enviament;
	}
	
	public void update (
			InteressatTipusEnumDto interessatTipus,
			String email,
			String llinatge1,
			String llinatge2,
			String nif,
			String nom,
			String telefon,
			String raoSocial, 
			String dir3Codi, 
			boolean incapacitat) {
		this.interessatTipus = interessatTipus;
		this.email = email;
		this.llinatge1 = llinatge1;
		this.llinatge2 = llinatge2;
		this.nif = nif;
		this.nom = nom;
		this.telefon = telefon;
		if (interessatTipus != null && interessatTipus.equals(InteressatTipusEnumDto.JURIDICA) && (raoSocial == null || raoSocial.isEmpty()))
			this.raoSocial = nom;
		else
			this.raoSocial = raoSocial;
		this.dir3Codi = dir3Codi;
		this.incapacitat = incapacitat;
	}
	
	public static Builder getBuilder(
			String email,
			String llinatge1,
			String llinatge2,
			String nif,
			String nom,
			String telefon) {
		return new Builder(
				email,
				llinatge1,
				llinatge2,
				nif,
				nom,
				telefon);
	}
	
	public static class Builder {
		PersonaEntity built;
		Builder(
				String email,
				String llinatge1,
				String llinatge2,
				String nif,
				String nom,
				String telefon
				) {
			built = new PersonaEntity();
			built.email = email;
			built.llinatge1 = llinatge1;
			built.llinatge2 = llinatge2;
			built.nif = nif;
			built.nom = nom;
			built.telefon = telefon;
		}
		public PersonaEntity build() {
			return built;
		}
	}
	
	public static BuilderV2 getBuilderV2(
			InteressatTipusEnumDto interessatTipus,
			String email,
			String llinatge1,
			String llinatge2,
			String nif,
			String nom,
			String telefon,
			String raoSocial,
			String codiEntitatDesti) {
		return new BuilderV2(
				interessatTipus,
				email,
				llinatge1,
				llinatge2,
				nif,
				nom,
				telefon,
				raoSocial,
				codiEntitatDesti);
	}
	
	public static class BuilderV2 {
		PersonaEntity built;
		BuilderV2(
				InteressatTipusEnumDto interessatTipus,
				String email,
				String llinatge1,
				String llinatge2,
				String nif,
				String nom,
				String telefon,
				String raoSocial,
				String codiEntitatDesti
				) {
			built = new PersonaEntity();
			built.incapacitat = false;
			built.interessatTipus = interessatTipus;
			built.email = email;
			built.dir3Codi = codiEntitatDesti;
			built.llinatge1 = llinatge1;
			built.llinatge2 = llinatge2;
			built.nif = nif;
			built.nom = nom;
			if (interessatTipus != null && interessatTipus.equals(InteressatTipusEnumDto.JURIDICA) && (raoSocial == null || raoSocial.isEmpty()))
				built.raoSocial = nom;
			else
				built.raoSocial = raoSocial;
			built.telefon = telefon;
		}
		public BuilderV2 incapacitat(boolean incapacitat) {
			built.incapacitat = incapacitat;
			return this;
		}
		public PersonaEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 4569697366006085907L;
}
