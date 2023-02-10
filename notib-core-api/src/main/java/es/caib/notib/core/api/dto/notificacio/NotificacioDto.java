/**
 * 
 */
package es.caib.notib.core.api.dto.notificacio;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notenviament.EnviamentDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;


/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@Getter @Setter
public class NotificacioDto extends AuditoriaDto {

	private Long id;
	private String referencia;
	private String cifEntitat;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private Date enviamentDataProgramada;
	private Date createdDate;
	private String concepte;
//	private String pagadorCorreusCodiDir3;
//	private String pagadorCorreusContracteNum;
//	private String pagadorCorreusCodiClientFacturacio;
//	private Date pagadorCorreusDataVigencia;
//	private String pagadorCieCodiDir3;
//	private Date pagadorCieDataVigencia;
	private String procedimentDescripcioSia;
	private String documentArxiuNom;
	private String documentArxiuId;
	private String csv_uuid;
	private String documentContingutBase64;
	private String documentSha1;
	private String grupCodi;
	private String grupNom;
	private boolean documentNormalitzat;
	private boolean documentGenerarCsv;
	private NotificacioEstatEnumDto estat;
//	private EnviamentEstat notificaEstat;
	private Date estatDate;
	private Date notificaErrorData;
	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	private String notificaErrorDescripcio;
	private EntitatDto entitat;
	private ProcSerDto procediment;
	private String referenciaExterna;
	private String notificacio;
	private boolean permisProcessar;
	private String registreOrgan;
	private String registreOficina;
	private String registreLlibre;
	private String registreExtracte;
	private String registreTipusAssumpte;
	private String registreRefExterna;
	private String numExpedient;
//	private OperadorPostalDto pagadorPostal;
	private String usuariCodi;
	private String registreObservacions;
	private Date registreData;
	private Integer registreNumero;
	private DocumentDto document;
	private String descripcio;
	private String organGestor;
	private String organGestorNom;
	private List<EnviamentDto> enviaments;
	private Date notificaEnviamentData;
	
	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualProcessar;
	private boolean usuariActualNotificacio;
	private boolean usuariActualAdministration;
	private boolean errorLastCallback;
	private boolean hasEnviamentsPendents;
	private boolean hasEnviamentsPendentsRegistre;
	
	private TipusUsuariEnumDto tipusUsuari;
	
	public boolean isNotificaError() {
		return notificaErrorData != null;
	}
	
	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}
	
	public String getCreatedByComplet() {
		String nomComplet = "";
		if (getCreatedBy() != null) {
			String nomUsuari = getCreatedBy().getNom();
			String codiUsuari = getCreatedBy().getCodi();
			if (nomUsuari != null && !nomUsuari.isEmpty())
				nomComplet += nomUsuari + " ";
			if (codiUsuari != null && !codiUsuari.isEmpty())
				nomComplet += "(" + codiUsuari + ")";
		}
		return nomComplet;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
