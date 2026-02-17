package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.InteressatTipus;
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

/**
 * Informació d'una persona destinatària d'una notificació. Una persona pot ser tant l'interessat de la notificació com
 * un dels representants (anomentats destinataris a Notifica).
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = PersonaResource.Fields.nom,
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.CREATE }
		),
	}
)
public class PersonaResource extends BaseResource<Long> {

	@NotNull
	private InteressatTipus interessatTipus;
	private DocumentTipus documentTipus;
	@Size(max = 9)
	private String nif;
	@Size(max = 255)
	private String nom;
	@Size(max = 30)
	private String llinatge1;
	@Size(max = 30)
	private String llinatge2;
	@Size(max = 16)
	private String telefon;
	@Size(max = 255)
	private String email;
	@Size(max = 100)
	private String raoSocial;
	@Size(max = 9)
	private String dir3Codi;
	private boolean incapacitat;

	private ResourceReference<NotificacioEnviamentResource, Long> enviament;

}
