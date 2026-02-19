package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Size;

/**
 * Informació d'un document adjunt d'una notificació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = DocumentResource.Fields.arxiuNom,
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
		grantedPermissions = {PermissionEnum.READ, PermissionEnum.CREATE}
	)
)
public class DocumentResource extends BaseResource<Long> {

	@Size(max = 256)
	private String arxiuGestdocId;
	@Size(max = 256)
	private String arxiuNom;
	@Size(max = 256)
	private String hash;
	@Size(max = 256)
	private String uuid;
	@Size(max = 256)
	private String csv;
	@Size(max = 256)
	private String mediaType;
	private Long mida;
	@Size(max = 256)
	private OrigenEnum origen = OrigenEnum.ADMINISTRACIO;
	private ValidesaEnum validesa = ValidesaEnum.ORIGINAL;
	private TipusDocumentalEnum tipoDocumental = TipusDocumentalEnum.ALTRES;
	private Boolean modoFirma;
	private Boolean normalitzat;

	/*
	 * Camp per a que el front pugui seleccionar d'on s'ha d'obtenir el document.
	 */
	private DocumentSource source = DocumentSource.ATTACHED;
	private FileReference attachment;

	public enum DocumentSource {
		CSV,
		UUID,
		ATTACHED
	}

}
