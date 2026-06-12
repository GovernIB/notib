package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.annotation.ResourceField;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.validation.CustomValidation;
import es.caib.notib.logic.intf.dto.AmpliacionPlazoDto;
import es.caib.notib.logic.intf.dto.MarcarProcessat;
import es.caib.notib.logic.intf.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.anular.AnularDto;
import es.caib.notib.logic.intf.dto.explotacio.EnviamentOrigen;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.validator.NotificacioProcedimentNotNull;
import es.caib.notib.logic.intf.model.validator.PrimerEnviamentCodiDir3ObligatoriEnviamentTipusSir;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificació.
 * <p>
 * Per a poder consultar / gestionar una notificació s'ha de complir:
 * - En el cas de procediments no comuns, s'ha de tenir el permís corresponent per a l'òrgan gestor o pel procediment.
 * - En el cas de procediments comuns sense "requereix permisos directes" s'ha de tenir el permís corresponent per a la
 *   combinació òrgan gestor - procediment.
 * - En el cas de procediments comuns amb "requereix permisos directes" s'han de complir tots aquests punts:
 *     · S'ha de tenir el permís de procediments comuns a l'òrgan gestor.
 *     · S'ha de tenir el permís corresponent per a la combinació òrgan gestor - procediment.
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
	},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = NotificacioResource.FILTER_CODE,
			formClass = NotificacioResource.NotificacioResourceFilter.class
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = NotificacioResource.PERSPECTIVE_ENVIAMENTS_NOTIFICACIO
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = NotificacioResource.PERSPECTIVE_DOCUMENTS_NOTIFICACIO
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = NotificacioResource.PERSPECTIVE_OPERADORS_CIE_POSTAL
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = NotificacioResource.PERSPECTIVE_GRUP
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioResource.REPORT_DESCARREGAR_JUSTIFICANT_NOTIFICACIO,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioResource.REPORT_DESCARREGAR_DOCUMENT_ENVIAT,
			requiresId = true,
			formClass = NotificacioResource.DocumentParams.class
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioResource.REPORT_DESCARREGAR_CERTIFICACIO,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_ANULAR_REMESA,
			requiresId = true,
			formClass = AnularDto.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN})
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_AMPLIAR_TERMINI,
			requiresId = true,
			formClass = AmpliacionPlazoDto.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN})
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_MARCAR_PROCESSAT,
			requiresId = true,
			formClass = MarcarProcessat.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN})
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_ESBORRAR_REMESA,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN})
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_ENVIAR_CALLBACK,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_ENVIAR_ENTREGA_POSTAL,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_REGISTRAR_REMESA,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_ENVIAR_NOTIFICA,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_REACTIVAR_ESTAT_NOTIFICA,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_REACTIVAR_CONSULTA_SIR,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_REACTIVAR_AMB_ERRORS,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioResource.ACTION_REENVIAR_AMB_ERRORS,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER, BaseConfig.ROLE_ORGAN }
				)
			}
		)
	}
)
@CustomValidation.List({
	@CustomValidation(
		customValidatorType = PrimerEnviamentCodiDir3ObligatoriEnviamentTipusSir.class),
	@CustomValidation(
		customValidatorType = NotificacioProcedimentNotNull.class,
		targetFields = NotificacioResource.Fields.procediment,
		springBean = true),
})
public class NotificacioResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER_NOTIFICACIO";
	public static final String PERSPECTIVE_ENVIAMENTS_NOTIFICACIO = "ENVIAMENTS_NOTIFICACIO";
	public static final String PERSPECTIVE_DOCUMENTS_NOTIFICACIO = "DOCUMENTS_NOTIFICACIO";
	public static final String PERSPECTIVE_OPERADORS_CIE_POSTAL = "OPERADORS_CIE_POSTAL";
	public static final String PERSPECTIVE_GRUP = "OPERADORS_GRUP";
	public static final String REPORT_DESCARREGAR_JUSTIFICANT_NOTIFICACIO = "DESCARREGAR_JUSTIFICANT_ENVIAMENT_NOTIFICACIO";
	public static final String REPORT_DESCARREGAR_DOCUMENT_ENVIAT = "DESCARREGAR_DOCUMENT_ENVIAT";
	public static final String REPORT_DESCARREGAR_CERTIFICACIO = "DESCARREGAR_CERTIFICACIO";
	public static final String ACTION_ANULAR_REMESA = "ANULAR_REMESA";
	public static final String ACTION_AMPLIAR_TERMINI = "AMPLIAR_TERMINI";
	public static final String ACTION_MARCAR_PROCESSAT = "MARCAR_PROCESSAT";
	public static final String ACTION_ESBORRAR_REMESA = "ESBORRAR_REMESA";
	public static final String ACTION_ENVIAR_CALLBACK = "ENVIAR_CALLBACK";
	public static final String ACTION_ENVIAR_ENTREGA_POSTAL = "ENVIAR_ENTREGA_POSTAL";
	public static final String ACTION_REGISTRAR_REMESA = "REGISTRAR_REMESA";
	public static final String ACTION_ENVIAR_NOTIFICA = "ENVIAR_NOTIFICA";
	public static final String ACTION_REACTIVAR_ESTAT_NOTIFICA = "REACTIVAR_ESTAT_NOTIFICA";
	public static final String ACTION_REACTIVAR_CONSULTA_SIR = "REACTIVAR_CONSULTA_SIR";
	public static final String ACTION_REACTIVAR_AMB_ERRORS = "REACTIVAR_AMB_ERRORS";
	public static final String ACTION_REENVIAR_AMB_ERRORS = "REENVIAR_AMB_ERRORS";

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
	@ResourceField(onChangeActive = true)
	private ResourceReference<OrganGestorResource, Long> organGestor;
	private ResourceReference<ProcedimentResource, Long> procediment;
//	@NotNull
	private ResourceReference<DocumentResource, Long> document;
	private ResourceReference<DocumentResource, Long> document2;
	private ResourceReference<DocumentResource, Long> document3;
	private ResourceReference<DocumentResource, Long> document4;
	private ResourceReference<DocumentResource, Long> document5;
	private ResourceReference<NotificacioMassivaResource, Long> notificacioMassiva;
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
	private boolean procedimentRequired = true;
	private boolean entregaPostal;

	// Camps provinents de NotificacioTable
	private Date enviadaDate;
	private String estatString;
	private String registreNums;
	private String titular;
	private String notificaIds;
	private boolean permisProcessar;

	// Camps pel detall de remeses
	private PagadorPostalResource operadorPostalInfo;
	private PagadorCieResource operadorCieInfo;
	private GrupResource grupInfo;
	private boolean notificacioAntiga;
	private boolean hasEnviamentsPendents;
	private boolean eventsCallbackPendent;
	private boolean plazoAmpliado;
	private boolean anulat;
	private boolean comunicacioSir;
	private String motiuAnulacio;
	private Date dataCallbackPendent;
	private NotificacioRegistreEstatEnumDto registreEstat;
	private List<String> notificacionsMovilErrorDesc = new ArrayList<>();
	private boolean fiReintents;
	private String fiReintentsDesc;
	private boolean callbackFiReintents;
	private String callbackFiReintentsDesc;
	private boolean errorEntregaPostal;
	private String notificaErrorDescripcio;
	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	private NotificacioEventTipusEnumDto noticaErrorEventTipus;
	private Date notificaErrorData;


	@Getter
	@Setter
	@NoArgsConstructor
	public static class NotificacioResourceFilter implements Serializable {

		private EnviamentTipus enviamentTipus;
		private String concepte;
		private NotificacioEstatEnumDto estat;
		private Date dataIniciInici;
		private Date dataIniciFi;
		private String interessat;
		private String numExpedient;
		private String identificadorNotifica;
		private ResourceReference<OrganGestorResource, Long> organGestor;
		private ResourceReference<ProcedimentResource, Long> procediment;
		private ResourceReference<ProcedimentResource, Long> servei;
		private TipusUsuariEnumDto tipusUsuari;
		private String createdBy;
		protected String referencia;
		private String registreNumeroSortida;
		private Date dataCaducitatInici;
		private Date dataCaducitatFi;
		private boolean nomesLesMeves;
		private boolean errorLastCallback;
		private boolean entregaPostal;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class DocumentParams implements Serializable {

		private Long docId;
	}

}
