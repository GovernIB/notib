/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;

import es.caib.notib.client.domini.EnviamentEstat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació sobre l'estat d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class NotificacioEnviamenEstatDto {

	private EnviamentEstat notificaEstat;
	private Date notificaEstatData;
	private String notificaEstatDescripcio;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatReceptorNom;
	private String notificaDatatNumSeguiment;
	private String notificaDatatErrorDescripcio;
	private Date notificaCertificacioData;
	private String notificaCertificacioArxiuId;
	private String notificaCertificacioArxiuNom;
	private String notificaCertificacioHash;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioCsv;
	private String notificaCertificacioMime;
	private Integer notificaCertificacioTamany;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	private boolean notificaError;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;

}
