/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto;
import es.caib.notib.core.api.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * Informació d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Enviament {

	private Long id;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String referencia;
	private Persona titular;
	private List<Persona> destinataris;
	private boolean entregaPostalActiva;
	private EntregaPostal entregaPostal;
	private boolean entregaDehActiva;
	private EntregaDeh entregaDeh;
	private NotificaServeiTipusEnumDto serveiTipus;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
