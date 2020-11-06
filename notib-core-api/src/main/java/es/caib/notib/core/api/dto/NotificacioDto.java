/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;


/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioDto extends AuditoriaDto {

	private Long id;
	private String cifEntitat;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private Date enviamentDataProgramada;
	private String concepte;
	private String pagadorCorreusCodiDir3;
	private String pagadorCorreusContracteNum;
	private String pagadorCorreusCodiClientFacturacio;
	private Date pagadorCorreusDataVigencia;
	private String pagadorCieCodiDir3;
	private Date pagadorCieDataVigencia;
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
	private Date estatDate;
	private Date notificaErrorData;
	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	private String notificaErrorDescripcio;
	private EntitatDto entitat;
	private ProcedimentDto procediment;
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
	private PagadorPostalDto pagadorPostal;
	private String usuariCodi;
	private String registreObservacions;
	private Date registreData;
	private Integer registreNumero;
	private DocumentDto document;
	private String descripcio;
	private String organGestor;
	private String organGestorNom;
	private List<EnviamentDto> enviaments;
	
	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualProcessar;
	private boolean usuariActualNotificacio;
	private boolean usuariActualAdministration;
	private boolean errorLastCallback;
	private boolean errorLastEvent;
	private boolean hasEnviamentsPendents;
	
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
