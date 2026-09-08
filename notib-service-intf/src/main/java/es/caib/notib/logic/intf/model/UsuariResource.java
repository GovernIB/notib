package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.MenuEstil;
import es.caib.notib.client.domini.NumElementsPaginaDefecte;
import es.caib.notib.client.domini.Tema;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

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
	)
)
public class UsuariResource extends BaseResource<String> {

	public static final String FILTER_CODE = "FILTER_USUARI_PERMIS";

	@NotNull
	@Size(max = 64)
	protected String codi;
	@Size(max = 100)
	protected String nom;
	@Size(max = 40)
	protected String nif;
	@Size(max = 100)
	protected String llinatges;
	@Size(max = 200)
	protected String nomSencer;
	@Size(max = 200)
	protected String email;
	@Size(max = 200)
	protected String emailAlt;
	protected boolean rebreEmailsNotificacio = true;
	protected boolean rebreEmailsNotificacioCreats = true;
	@Size(max = 40)
	protected String ultimRol;
	protected Long ultimaEntitat;
	protected Idioma idioma;
	protected Tema tema;
	protected MenuEstil estilMenu;
	protected NumElementsPaginaDefecte numElementsPaginaDefecte;
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
