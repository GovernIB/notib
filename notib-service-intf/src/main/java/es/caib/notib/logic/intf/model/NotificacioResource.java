package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.base.validation.CustomValidation;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.explotacio.EnviamentOrigen;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.annotation.ResourceField;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.validator.PrimerEnviamentCodiDir3ObligatoriEnviamentTipusSir;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	//descriptionField = NotificacioResource.Fields.codi,
	//quickFilterFields = { NotificacioResource.Fields.codi, NotificacioResource.Fields.nom },
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN },
			grantedPermissions = { PermissionEnum.READ }
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_USER },
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.CREATE }
		),
	}
)
@CustomValidation.List({
	@CustomValidation(
		customValidatorType = PrimerEnviamentCodiDir3ObligatoriEnviamentTipusSir.class)
})
public class NotificacioResource extends BaseResource<Long> {

	@NotNull
	private EnviamentTipus enviamentTipus;
	private Date enviamentDataProgramada;
	@NotNull
	@Size(max = 240)
	private String concepte;
	@Size(max = 1000)
	private String descripcio;
	private Integer retard;
	@ResourceField(onChangeActive = true)
	private Date caducitat;
	private Date caducitatOriginal;
	@Size(max = 9)
	private String procedimentCodiNotib;
	@Size(max = 64)
	private String grupCodi;
	private NotificacioEstatEnumDto estat;
	private Date estatDate;
	private TipusUsuariEnumDto tipusUsuari;
	@Size(max = 255)
	private String motiu;
	private Date notificaEnviamentData;
	private Date notificaEnviamentNotificaData;
	private int notificaEnviamentIntent;
	private int registreEnviamentIntent;
	private Integer registreNumero;
	@Size(max = 200)
	private String registreNumeroFormatat;
	private Date registreData;
	@Size(max = 80)
	private String numExpedient;
	@Size(max = 255)
	private String registreOficinaNom;
	@Size(max = 255)
	private String registreLlibreNom;
	private boolean errorLastCallback;
	private Idioma idioma = Idioma.CA;
	protected Date estatProcessatDate;
	@Size(max = 255)
	protected String referencia;
	@Size(max = 36)
	protected String seguentRemesa;
	@Size(max = 50)
	protected String numRegistrePrevi;
	private boolean justificantCreat;
	private EnviamentOrigen origen;
	private boolean deleted;

	private ResourceReference<EntitatResource, Long> entitat;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestor;
	@NotNull
	private ResourceReference<ProcedimentResource, Long> procediment;
	private ResourceReference<DocumentResource, Long> document;
	private ResourceReference<DocumentResource, Long> document2;
	private ResourceReference<DocumentResource, Long> document3;
	private ResourceReference<DocumentResource, Long> document4;
	private ResourceReference<DocumentResource, Long> document5;
	/*private ResourceReference<ProcedimentOrganResource, Long> procedimentOrgan;*/

	@NotNull
	@Size(min = 1)
	@Valid
	private List<NotificacioEnviamentResource> enviamentsInfo;
	@NotNull
	@Size(min = 1)
	@Valid
	private List<DocumentResource> documentsInfo;

	// El següent camp s'utilitza per a fer el càlcul de la data de caducitat especificant els dies naturals
	@ResourceField(onChangeActive = true)
	private Integer caducitatDiesNaturals = 10;

	// Camps calculats
	private LocalDateTime createdDate;
	private String createdBy;
	private ProcSerTipusEnum procedimentTipus;

	// Camps provinents de NotificacioTable
	private Date enviadaDate;
	private String estatString;

}
