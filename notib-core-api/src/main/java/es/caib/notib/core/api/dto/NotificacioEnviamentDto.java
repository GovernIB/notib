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
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioEnviamentDto extends AuditoriaDto {

	private Long id;
	private NotificacioDto notificacio;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipusEnumDto serveiTipus;
	private String titularNomLlinatge;
	private String referencia;
	private String usuari;
	private boolean notificaError;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private String notificaCertificacioArxiuNom;
	private Date notificaCertificacioData;
	private NotificacioEnviamentEstatEnumDto notificaEstat;
	private Date notificaEstatData;
	private String notificaDatatErrorDescripcio;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatReceptorNom;
	private String notificaDatatNumSeguiment;
	private String notificaCertificacioMime;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioCsv;
	private String notificaReferencia;
	private String notificaIdentificador;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	
	private Date registreData;
	private String registreNumeroFormatat;
	private NotificacioRegistreEstatEnumDto registreEstat;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getTitularLlinatges() {
		return concatenarLlinatges(
				titular.getLlinatge1(),
				titular.getLlinatge2());
	}
	public String getTitularNomLlinatges() {
		StringBuilder sb = new StringBuilder();
		sb.append(titular.getNom());
		String llinatges = getTitularLlinatges();
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append(" ");
			sb.append(llinatges);
		}
		sb.append(" (");
		sb.append(titular.getNif());
		sb.append(")");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private String concatenarLlinatges(
			String llinatge1,
			String llinatge2) {
		if (llinatge1 == null && llinatge2 == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(llinatge1);
		if (llinatge2 != null && !llinatge2.isEmpty()) {
			sb.append(" ");
			sb.append(llinatge2);
		}
		return sb.toString();
	}

	private static final long serialVersionUID = -139254994389509932L;

}
