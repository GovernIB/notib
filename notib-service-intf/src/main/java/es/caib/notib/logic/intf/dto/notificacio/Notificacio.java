
package es.caib.notib.logic.intf.dto.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.caib.notib.client.domini.NotificacioV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Informació d'una notificació per al seu enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Notificacio extends NotificacioV2 implements Serializable {

    private Long id;
    private Long procedimentId;
    private Long grupId;

}
