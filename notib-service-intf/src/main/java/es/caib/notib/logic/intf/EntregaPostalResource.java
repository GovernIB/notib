package es.caib.notib.logic.intf;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EntregaPostalVia;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.logic.intf.dto.cie.CieCertificacioArxiuTipus;
import es.caib.notib.logic.intf.dto.cie.CieCertificacioTipus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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
public class EntregaPostalResource extends BaseResource<Long> {

	private NotificaDomiciliTipusEnumDto domiciliTipus;
	private NotificaDomiciliConcretTipus domiciliConcretTipus;
	private EntregaPostalVia domiciliViaTipus;
	private String domiciliViaNom;
	private NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;
	private String domiciliNumeracioNumero;
	private String domiciliNumeracioQualificador;
	private String domiciliNumeracioPuntKm;
	private String domiciliApartatCorreus;
	private String domiciliBloc;
	private String domiciliPortal;
	private String domiciliEscala;
	private String domiciliPlanta;
	private String domiciliPorta;
	private String domiciliComplement;
	private String domiciliPoblacio;
	private String domiciliMunicipiCodiIne;
	private String domiciliMunicipiNom;
	private String domiciliCodiPostal;
	private String domiciliProvinciaCodi;
	private String domiciliProvinciaNom;
	private String domiciliPaisCodiIso; // ISO-3166
	private String domiciliPaisNom;
	private String domiciliLinea1;
	private String domiciliLinea2;
	private Integer domiciliCie;
	private String formatSobre;
	private String formatFulla;
	private String cieId;
	private boolean cieCancelat;
	private CieEstat cieEstat;
	private String cieErrorDesc;
	private String cieDatatReceptorNif;
	private String cieDatatReceptorNom;
	private Date cieCertificacioData;
	private String cieCertificacioArxiuId;
	private String cieCertificacioHash;
	private String cieCertificacioOrigen;
	private String cieCertificacioMetadades;
	private String cieCertificacioCsv;
	private String cieCertificacioMime;
	private Integer cieCertificacioTamany;
	private CieCertificacioTipus cieCertificacioTipus;
	private CieCertificacioArxiuTipus cieCertificacioArxiuTipus;
	private String cieCertificacioNumSeguiment;
	private Date cieEstatData;
	private String cieEstatDescripcio;
	private String cieDatatOrigen;
	private String cieDatatNumSeguiment;
	private String cieDatatErrorDescripcio;
	private Date cieEstatDataActualitzacio;
	private String cieCertificacioArxiuNom;
}


