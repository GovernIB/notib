package es.caib.notib.core.entity;

import es.caib.notib.client.domini.DocumentTipusEnumDto;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa una persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
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
	@Column(name = "llinatge1", length = 30)
	private String llinatge1;
	@Column(name = "llinatge2", length = 30)
	private String llinatge2;
	@Column(name = "document_tipus", nullable = true)
	@Enumerated(EnumType.STRING)
	private DocumentTipusEnumDto documentTipus;
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

	public PersonaDto asDto(){
		return PersonaDto.builder()
				.id(getId())
				.incapacitat(incapacitat)
				.interessatTipus(interessatTipus)
				.nom(nom)
				.llinatge1(llinatge1)
				.llinatge2(llinatge2)
				.raoSocial(raoSocial)
				.nif(nif)
				.telefon(telefon)
				.email(email)
				.dir3Codi(dir3Codi)
				.build();
	}

	public String getNomSencer() {
		String nomSencer = nom;
		if (llinatge1 != null && !llinatge1.isEmpty()) {
			nomSencer += " " + llinatge1;
		}
		if (llinatge2 != null && !llinatge2.isEmpty()) {
			nomSencer += " " + llinatge2;
		}
		return nomSencer;
	}
	private static final long serialVersionUID = 4569697366006085907L;
}
