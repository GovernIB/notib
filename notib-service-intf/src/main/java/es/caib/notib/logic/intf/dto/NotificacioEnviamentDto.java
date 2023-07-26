/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.dto.cie.EntregaPostalDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioEnviamentDto extends AuditoriaDto {

	private Long id;
	private String idXifrat;
	private NotificacioDto notificacio;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipus serveiTipus;
	private String titularNomLlinatge;
	private String referencia;
	private String usuari;
	private boolean notificaError;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private String notificaCertificacioArxiuNom;
	private Date notificaCertificacioData;
	private EnviamentEstat notificaEstat;
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
	private boolean notificaEstatFinal;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	
	private Date registreData;
	private String registreNumeroFormatat;
	private NotificacioRegistreEstatEnumDto registreEstat;
	private boolean isCallbackPendent;
	private String callbackData;
	private boolean fiReintents;
	private String fiReintentsDesc;
	private boolean callbackFiReintents;
	private String callbackFiReintentsDesc;
	private boolean perEmail;
	private Date sirRecepcioData;
	private Date sirRegDestiData;
	private String notificacioMovilErrorDesc;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getTitularLlinatges() {
		return titular.concatenarLlinatges();
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

	public boolean isEnviamentEnviat() {
		return !EnviamentEstat.NOTIB_PENDENT.equals(notificaEstat) && !EnviamentEstat.REGISTRADA.equals(notificaEstat);
	}
	public boolean isEnviamentFinalitzat() {
		return notificaEstatFinal || EnviamentEstat.FINALITZADA.equals(notificaEstat);
	}
	public boolean isEnviamentProcessat() {
		return EnviamentEstat.PROCESSADA.equals(notificaEstat);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
