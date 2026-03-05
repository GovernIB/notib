package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PersonaResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

/**
 * Entitat de base de dades de persona destinatària d'una notificació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "persona")
@Getter
@Setter
@NoArgsConstructor
public class PersonaResourceEntity
	extends BaseAuditableResourceEntity<PersonaResource> {

	@Column(name = "interessattipus", nullable = false)
	@Enumerated(EnumType.STRING)
	private InteressatTipus interessatTipus;
	@Column(name = "document_tipus")
	@Enumerated(EnumType.STRING)
	private DocumentTipus documentTipus;
	@Column(name = "nif", length = 9)
	private String nif;
	@Column(name = "nom", length = 255)
	private String nom;
	@Column(name = "llinatge1", length = 30)
	private String llinatge1;
	@Column(name = "llinatge2", length = 30)
	private String llinatge2;
	@Column(name = "telefon", length = 16)
	private String telefon;
	@Column(name = "email", length = 255)
	private String email;
	@Column(name = "rao_social", length = 100)
	private String raoSocial;
	@Column(name = "cod_entitat_desti", length = 9)
	private String dir3Codi;
	@Column(name = "incapacitat")
	private boolean incapacitat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "notificacio_env_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "persona_not_fk"),
		nullable = false)
	private NotificacioEnviamentResourceEntity enviament;

	@Formula("(nom || ' ' || COALESCE(llinatge1 || ' ', '') || COALESCE(llinatge2 || ' ', '') || '(' || nif || ')')")
	private String nomSencerNif;

	@Builder
	public PersonaResourceEntity(
		PersonaResource resource,
		NotificacioEnviamentResourceEntity enviament) {
		this.interessatTipus = resource.getInteressatTipus();
		this.incapacitat = resource.isIncapacitat();
		this.email = resource.getEmail();
		this.llinatge1 = resource.getLlinatge1();
		this.llinatge2 = resource.getLlinatge1();
		this.documentTipus = resource.getDocumentTipus();
		this.nif = resource.getNif();
		this.nom = resource.getNom();
		this.telefon = resource.getTelefon();
		this.raoSocial = resource.getRaoSocial();
		this.dir3Codi = resource.getDir3Codi();
		this.enviament = enviament;
	}

}
