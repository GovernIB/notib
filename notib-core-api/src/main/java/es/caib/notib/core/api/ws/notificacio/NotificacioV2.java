/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.core.api.dto.IdiomaEnumDto;
import es.caib.notib.core.api.util.TrimStringDeserializer;
import lombok.*;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacioV2 {

	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String emisorDir3Codi;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String organGestor;
	private EnviamentTipusEnum enviamentTipus;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String concepte;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String descripcio; //Observacions
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String usuariCodi;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String procedimentCodi;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String grupCodi;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String numExpedient;
	private List<Enviament> enviaments;
	private IdiomaEnumDto idioma;
	private DocumentV2 document;
	private DocumentV2 document2;
	private DocumentV2 document3;
	private DocumentV2 document4;
	private DocumentV2 document5;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
