package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.validation.CustomValidation;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.model.validator.TitularIncapacitatObligatoriRepresentant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Informació d'un enviament d'una notificació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
	descriptionField = "id",
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.CREATE }
	)
)
@CustomValidation.List({
	@CustomValidation(
		customValidatorType = TitularIncapacitatObligatoriRepresentant.class,
		message="{es.caib.notib.validation.TitularIncapacitatObligatoriRepresentant.message}")
})
public class NotificacioEnviamentResource extends BaseResource<Long> {

	@NotNull
	private ServeiTipus serveiTipus = ServeiTipus.NORMAL;
	@Size(max = 36)
	private String notificaReferencia;
	@Size(max = 20)
	private String notificaIdentificador;
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

	// Camps calculats
	private String notificacioConcepte;
	private ResourceReference<OrganGestorResource, Long> notificacioOrganGestor;
	private ResourceReference<ProcedimentResource, Long> notificacioProcediment;
	private LocalDateTime enviatDate;

}
