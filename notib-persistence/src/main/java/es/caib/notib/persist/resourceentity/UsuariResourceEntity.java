package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.UsuariResource;
import lombok.*;

import javax.persistence.*;

/**
 * Entitat de base de dades pels recursos de tipus usuari de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "usuari")
@Getter
@Setter
@NoArgsConstructor
public class UsuariResourceEntity extends es.caib.notib.persist.base.entity.BaseResourceEntity<UsuariResource, String> {

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
	private boolean rebreEmailsNotificacio = true;
	@Column(name = "rebre_emails_creats")
	private boolean rebreEmailsNotificacioCreats = true;
	@Column(name = "ultim_rol", length = 40)
	private String ultimRol;
	@Column(name = "ultima_entitat")
	private Long ultimaEntitat;
	@Column(name = "idioma", length = 2)
	private String idioma;
	@Column(name = "num_elements_pagina_defecte", length = 3)
	private String numElementsPaginaDefecte;
	@Column(name = "entitat_defecte")
	protected Long entitatDefecte;
	@Column(name = "organ_defecte")
	protected Long organDefecte;
	@Column(name = "procediment_defecte")
	protected Long procedimentDefecte;

	@Version
	private long version = 0;

	@Builder
	public UsuariResourceEntity(UsuariResource resource) {
		this.codi = resource.getCodi();
		this.nom = resource.getNom();
		this.nif = resource.getNif();
		this.llinatges = resource.getLlinatges();
		this.nomSencer = resource.getNomSencer();
		this.email = resource.getEmail();
		this.emailAlt = resource.getEmailAlt();
		this.rebreEmailsNotificacio = resource.isRebreEmailsNotificacio();
		this.rebreEmailsNotificacioCreats = resource.isRebreEmailsNotificacioCreats();
		this.ultimRol = resource.getUltimRol();
		this.ultimaEntitat = resource.getUltimaEntitat();
		this.numElementsPaginaDefecte = resource.getNumElementsPaginaDefecte();
		this.entitatDefecte = resource.getEntitatDefecte();
		this.organDefecte = resource.getOrganDefecte();
		this.procedimentDefecte = resource.getProcedimentDefecte();
	}

	@Override
	public String getId() {
		return codi;
	}

	@Override
	public void setId(String id) {
		this.codi = id;
	}

}
