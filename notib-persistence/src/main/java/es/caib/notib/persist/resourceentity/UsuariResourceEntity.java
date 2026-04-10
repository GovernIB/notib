package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.NumElementsPaginaDefecte;
import es.caib.notib.client.domini.Tema;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.UsuariResource;
import lombok.*;

import javax.persistence.*;

/**
 * Entitat de base de dades d'usuari de l'aplicació.
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
	private String id;
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
	@Convert(converter = IdiomaConverter.class)
	@Column(name = "idioma", length = 2)
	private Idioma idioma;
	@Column(name = "tema", length = 10)
	private Tema tema;
	@Convert(converter = NumElementsPaginaConverter.class)
	@Column(name = "num_elements_pagina_defecte", length = 3)
	private NumElementsPaginaDefecte numElementsPaginaDefecte;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat_defecte",
		referencedColumnName = "id")
	protected EntitatResourceEntity entitatDefecte;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organ_defecte",
		referencedColumnName = "id")
	protected OrganGestorResourceEntity organDefecte;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "procediment_defecte",
		referencedColumnName = "id")
	protected ProcedimentResourceEntity procedimentDefecte;

	@Version
	private long version = 0;

	@Builder
	public UsuariResourceEntity(
		UsuariResource resource,
		EntitatResourceEntity entitatDefecte,
		OrganGestorResourceEntity organDefecte,
		ProcedimentResourceEntity procedimentDefecte) {
		this.id = resource.getCodi();
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
		this.entitatDefecte = entitatDefecte;
		this.organDefecte = organDefecte;
		this.procedimentDefecte = procedimentDefecte;
	}

	public String getCodi() {
		return id;
	}

	@Converter
	public static class IdiomaConverter implements AttributeConverter<Idioma, String> {
		@Override
		public String convertToDatabaseColumn(Idioma idioma) {
			if (idioma == null) return null;
			return idioma.name().toLowerCase();
		}
		@Override
		public Idioma convertToEntityAttribute(String dbData) {
			if (dbData == null) return null;
			return Idioma.valueOf(dbData.toUpperCase());
		}
	}

	@Converter
	public static class NumElementsPaginaConverter implements AttributeConverter<NumElementsPaginaDefecte, String> {
		@Override
		public String convertToDatabaseColumn(NumElementsPaginaDefecte attribute) {
			return attribute != null ? String.valueOf(attribute.getElements()) : null;
		}
		@Override
		public NumElementsPaginaDefecte convertToEntityAttribute(String dbData) {
			if (dbData == null) return null;
			int value = Integer.parseInt(dbData);
			for (NumElementsPaginaDefecte e: NumElementsPaginaDefecte.values()) {
				if (e.getElements() == value) {
					return e;
				}
			}
			throw new IllegalArgumentException("Unknown enum value: " + dbData);
		}
	}

}
