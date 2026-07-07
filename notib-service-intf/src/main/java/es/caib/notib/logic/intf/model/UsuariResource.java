package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.NumElementsPaginaDefecte;
import es.caib.notib.client.domini.Tema;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un usuari de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = UsuariResource.Fields.codi,
	quickFilterFields = { UsuariResource.Fields.codi, UsuariResource.Fields.nom },
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
	),
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = UsuariResource.FILTER_CODE,
			formClass = UsuariResource.UsuariResourceFilter.class)
	}
)
public class UsuariResource extends BaseResource<String> {

	public static final String FILTER_CODE = "FILTER_USUARI_PERMIS";

	@NotNull
	@Size(max = 64)
	private String codi;
	@Size(max = 100)
	private String nom;
	@Size(max = 40)
	private String nif;
	@Size(max = 100)
	private String llinatges;
	@Size(max = 200)
	private String nomSencer;
	@Size(max = 200)
	private String email;
	@Size(max = 200)
	private String emailAlt;
	private boolean rebreEmailsNotificacio = true;
	private boolean rebreEmailsNotificacioCreats = true;
	@Size(max = 40)
	private String ultimRol;
	private Long ultimaEntitat;
	private Idioma idioma;
	private Tema tema;
	private NumElementsPaginaDefecte numElementsPaginaDefecte;
	protected ResourceReference<EntitatResource, Long> entitatDefecte;
	protected ResourceReference<OrganGestorResource, Long> organDefecte;
	protected ResourceReference<ProcedimentResource, Long> procedimentDefecte;

	public String getId() {
		return codi;
	}

	public Integer getNumElementsPaginaDefecteAsInt() {
		return numElementsPaginaDefecte != null ? numElementsPaginaDefecte.getElements(): null;
	}

	@Override
	public void setId(String id) {
		this.id = id;
		this.codi = id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class UsuariResourceFilter implements Serializable {

		private String codi;
	}



}
