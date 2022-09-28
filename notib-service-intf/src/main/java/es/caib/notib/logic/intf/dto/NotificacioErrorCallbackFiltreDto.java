/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtre per a la consulta de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioErrorCallbackFiltreDto implements Serializable {
	
	private Date dataInici;
	private Date dataFi;
	private Long procedimentId;
	private String concepte;
//	private Long entitatId;
	private NotificacioEstatEnumDto estat;
	private String usuari;

	public boolean isEmpty() {
		return (dataInici == null && 
				dataFi == null && 
				procedimentId == null && 
				(concepte == null || concepte.trim().isEmpty()) &&
//				entitatId == null &&
				estat == null &&
				(usuari == null || usuari.trim().isEmpty()));
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 4118407692540857237L;

}
