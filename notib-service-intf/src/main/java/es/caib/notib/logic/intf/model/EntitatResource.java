package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.Registre;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.EntitatTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'una entitat.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = EntitatResource.Fields.nom,
		quickFilterFields = { EntitatResource.Fields.codi, EntitatResource.Fields.nom },
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
				roles = { BaseConfig.ROLE_SUPER },
				grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
		)
)
public class EntitatResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 64)
	@EqualsAndHashCode.Include
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@NotNull
	private EntitatTipusEnumDto tipus;
	@NotNull
	@Size(max = 9)
	@EqualsAndHashCode.Include
	private String dir3Codi;
	@Size(max = 9)
	private String dir3CodiReg;
	@NotNull
	@Size(max = 64)
	private String apiKey;
	private boolean ambEntregaDeh;
	@Size(max = 1024)
	private String descripcio;
	private boolean activa;
	@Size(max = 1024)
	private String colorFons;
	@Size(max = 1024)
	private String colorLletra;
	private TipusDocumentEnumDto tipusDocDefault;
	private boolean llibreEntitat;
	private boolean oficinaEntitat;

	@Size(max = 255)
	private String nomOficinaVirtual;
	@Size(max = 255)
	protected String llibre;
	@Size(max = 255)
	protected String llibreNom;
	@Size(max = 255)
	private String oficina;

	//private byte[] logoCapBytes;
	//private boolean eliminarLogoCap;
	//private byte[] logoPeuBytes;
	//private boolean eliminarLogoPeu;

	private boolean entregaCieActiva;
	private Long operadorPostalId;
	private Long cieId;

}
