/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import es.caib.notib.core.api.dto.IdiomaEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@JsonAutoDetect
@XmlRootElement
@Getter @Setter
public class NotificacioV2 {

	private String emisorDir3Codi;
	private String organGestor;
	private EnviamentTipusEnum enviamentTipus;
	private String concepte;
	private String descripcio; //Observacions
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	private DocumentV2 document;
	private String usuariCodi;
	private String procedimentCodi;
	private String grupCodi;
	private String numExpedient;
	private List<Enviament> enviaments;
	private IdiomaEnumDto idioma;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
