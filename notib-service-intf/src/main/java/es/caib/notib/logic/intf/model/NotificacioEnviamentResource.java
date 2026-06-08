package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.EntregaPostalResource;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.validation.CustomValidation;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.model.validator.TitularIncapacitatObligatoriRepresentant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Informació d'un enviament d'una notificació.
 *
 * @author Límit Tecnologies
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
	descriptionField = "id",
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = {BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER},
		grantedPermissions = {PermissionEnum.READ}
	),
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = NotificacioEnviamentResource.FILTER_CODE,
			formClass = NotificacioEnviamentResource.NotificacioEnviamentResourceFilter.class
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = NotificacioEnviamentResource.PERSPECTIVE_TITULAR
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = NotificacioEnviamentResource.PERSPECTIVE_ENTREGA_POSTAL
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioEnviamentResource.REPORT_DESCARREGAR_DIAGRAMA_STATE_MACHINE
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioEnviamentResource.ACTION_REFRESCAR_ESTAT_NOTIFICA,
			requiresId = true,
			accessConstraints = {
					@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN }
					)
				}
			),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioEnviamentResource.REPORT_DESCARREGAR_CIE_CERTIFICACIO,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioEnviamentResource.REPORT_DESCARREGAR_CERTIFICACIO_ENVIAMENT,
			requiresId = true
		),
	}
)
@CustomValidation.List({
	@CustomValidation(
		customValidatorType = TitularIncapacitatObligatoriRepresentant.class,
		message = "{es.caib.notib.validation.TitularIncapacitatObligatoriRepresentant.message}")
})
public class NotificacioEnviamentResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER_ENVIAMENT";
	public static final String PERSPECTIVE_TITULAR = "TITULAR";
	public static final String PERSPECTIVE_ENTREGA_POSTAL = "ENTREGA_POSTAL";
	public static final String REPORT_DESCARREGAR_DIAGRAMA_STATE_MACHINE = "DESCARREGAR_DIAGRAMA_STATE_MACHINE";
	public static final String ACTION_REFRESCAR_ESTAT_NOTIFICA = "REFRESCAR_ESTAT_NOTIFICA";
	public static final String REPORT_DESCARREGAR_CIE_CERTIFICACIO = "DESCARREGAR_CIE_CERTIFICACIO";
	public static final String REPORT_DESCARREGAR_CERTIFICACIO_ENVIAMENT = "DESCARREGAR_CERTIFICACIO_ENVIAMENT";

	@NotNull
	private ServeiTipus serveiTipus = ServeiTipus.NORMAL;
	@Size(max = 36)
	private String referenciaEnviament;
	@Size(max = 20)
	private String notificaReferencia;
	private Date notificaDataCreacio;
	private Date notificaDataDisposicio;
	private Date notificaDataCaducitat;
	private boolean plazoAmpliado;
	private Boolean dehObligat;
	@Size(max = 9)
	private String dehNif;
	@Size(max = 64)
	private String dehProcedimentCodi;
	@Size(max = 9)
	private String notificaEmisorDir3;
	@Size(max = 100)
	private String notificaEmisorDescripcio;
	@Size(max = 9)
	private String notificaEmisorNif;
	@Size(max = 9)
	private String notificaArrelDir3;
	@Size(max = 100)
	private String notificaArrelDescripcio;
	@Size(max = 9)
	private String notificaArrelNif;
	private EnviamentEstat notificaEstat;
	private Date notificaEstatData;
	private Date notificaEstatDataActualitzacio;
	private boolean notificaEstatFinal;
	@Size(max = 255)
	private String notificaEstatDescripcio;
	@Size(max = 20)
	private String notificaDatatOrigen;
	@Size(max = 9)
	private String notificaDatatReceptorNif;
	@Size(max = 400)
	private String notificaDatatReceptorNom;
	@Size(max = 50)
	private String notificaDatatNumSeguiment;
	@Size(max = 255)
	private String notificaDatatErrorDescripcio;
	private Date notificaCertificacioData;
	@Size(max = 50)
	private String notificaCertificacioArxiuId;
	@Size(max = 50)
	private String notificaCertificacioHash;
	@Size(max = 20)
	private String notificaCertificacioOrigen;
	@Size(max = 255)
	private String notificaCertificacioMetadades;
	@Size(max = 50)
	private String notificaCertificacioCsv;
	@Size(max = 20)
	private String notificaCertificacioMime;
	private Integer notificaCertificacioTamany;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	@Size(max = 50)
	private String notificaCertificacioNumSeguiment;
	private boolean notificaError;
	private Date notificaIntentData;
	private int notificaIntentNum;
	@Size(max = 50)
	private String registreNumeroFormatat;
	@Size(max = 255)
	private String registreMotiu;
	private Date registreData;
	private NotificacioRegistreEstatEnumDto registreEstat;
	private boolean registreEstatFinal;
	private String sirTitularDir3Codi;
	private Date sirConsultaData;
	private int sirConsultaIntent;
	private boolean sirFiPooling;
	private Date sirRecepcioData;
	private Date sirRegDestiData;
	private int dehCertIntentNum;
	private Date dehCertIntentData;
	private int cieCertIntentNum;
	private Date cieCertIntentData;
	private boolean perEmail;
	private boolean errorLastCallback;
	private boolean anulat;
	@Size(max = 250)
	private String motiuAnulacio;
	private boolean entregaPostalActiva;

	private ResourceReference<NotificacioResource, Long> notificacio;
	private ResourceReference<PersonaResource, Long> titular;
	private ResourceReference<PersonaResource, Long> representant;
	//private ResourceReference<NotificacioEventResource, Long> ultimaEvent;
	//private ResourceReference<EntregaPostalResource, Long> entregaPostal;

	/*
	 * Camps per a que el front pugui enviar la informació necessària per a crear la notificació.
	 */
	@Valid
	@NotNull
	private PersonaResource titularInfo;
	@Valid
	private List<PersonaResource> representantsInfo;

	private EntregaPostalResource entregaPostalInfo;

	// Camps calculats
	private String notificacioConcepte;
	private String notificacioDescripcio;
	private ResourceReference<OrganGestorResource, Long> notificacioOrganGestor;
	private ResourceReference<ProcedimentResource, Long> notificacioProcediment;
	private LocalDateTime enviatDate;
	private LocalDateTime createdDate;
	private String createdBy;
	private LocalDateTime enviamentDataProgramada;
	private String codiCsvUuidDocument;
	private String representantsString;
	private EnviamentTipus tipusEnviament;
	private String referenciaNotificacio;
	private String grupCodi;
	private String procedimentCodi;
	private String titularNom;
	private String titularNif;
	private boolean notificat;

	public String getNotificaCertificacioArxiuNom() {
		return !StringUtils.isEmpty(notificaReferencia) ?  "certificacio_" + notificaReferencia + ".pdf" : null;
	}


	@Getter
	@Setter
	@NoArgsConstructor
	public static class NotificacioEnviamentResourceFilter implements Serializable {

		private EnviamentTipus tipusEnviament;
		private String notificacioConcepte;
		private EnviamentEstat notificaEstat;
		private Date dataEnviamentInici;
		private Date dataEnviamentFi;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private Date enviamentDataProgramadaInici;
		private Date enviamentDataProgramadaFi;
		private String notificaReferencia;
		private String grupCodi;
		private ResourceReference<OrganGestorResource, Long> organGestor;
		private ResourceReference<ProcedimentResource, Long> procedimentServei;
		private String createdBy;
		private String notificacioDescripcio;
		private String titularNomNif;
		private String representantsString;
		private String numRegistre;
		private Date dataCaducitatInici;
		private Date dataCaducitatFi;
		private String referenciaEnviament;
		private String referenciaNotificacio;
		private String codiCsvUuidDocument;
		private boolean entregaPostalActiva;

		private String procedimentCodi;
	}

}
