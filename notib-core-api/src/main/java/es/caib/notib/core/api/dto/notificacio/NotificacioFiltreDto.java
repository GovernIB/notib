/**
 * 
 */
package es.caib.notib.core.api.dto.notificacio;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Filtre per a la consulta de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioFiltreDto implements Serializable {
	
	private Long entitatId;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private NotificacioEstatEnumDto estat;
	private String concepte;
	private Date dataInici;
	private Date dataFi;
	private String titular;
	private String organGestor;
	private Long procedimentId;
	private Long serveiId;
	private TipusUsuariEnumDto tipusUsuari;
	private String numExpedient;
	private String creadaPer;
	private String identificador;
	private String referencia;
	private boolean nomesAmbErrors;

	public boolean isEmpty() {
		if (entitatId != null) {
			return false;
		}
		if (comunicacioTipus != null) {
			return false;
		}
		if (enviamentTipus != null) {
			return false;
		}
		if (estat != null) {
			return false;
		}
		if (concepte != null && !concepte.isEmpty()) {
			return false;
		}
		if (dataInici != null) {
			return false;
		}
		if (dataFi != null) {
			return false;
		}
		if (titular != null && !titular.isEmpty()) {
			return false;
		}
		if (organGestor != null && !organGestor.isEmpty()) {
			return false;
		}
		if (procedimentId != null) {
			return false;
		}
		if (serveiId != null) {
			return false;
		}
		if (tipusUsuari != null) {
			return false;
		}
		if (numExpedient != null && !numExpedient.isEmpty()) {
			return false;
		}
		if (creadaPer != null && !creadaPer.isEmpty()) {
			return false;
		}
		if (identificador != null && !identificador.isEmpty()) {
			return false;
		}
		if (nomesAmbErrors) {
			return false;
		}
		if (referencia != null && !referencia.isEmpty()) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 4118407692540857237L;

}
