package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
		grantedPermissions = { PermissionEnum.READ }
	)
)
public class UsuariResource extends BaseResource<String> {

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
	@Size(max = 2)
	private String idioma;
	@Size(max = 3)
	private String numElementsPaginaDefecte;
	protected Long entitatDefecte;
	protected Long organDefecte;
	protected Long procedimentDefecte;

	public String getId() {
		return codi;
	}

	@Override
	public void setId(String id) {
		this.id = id;
		this.codi = id;
	}

}
