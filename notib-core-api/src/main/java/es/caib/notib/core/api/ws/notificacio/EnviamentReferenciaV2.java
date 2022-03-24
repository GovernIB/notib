/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Informació de referència d'un enviament retornada per Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@JsonAutoDetect
@XmlRootElement
public class EnviamentReferenciaV2 {

	private String titularNom;
	private String titularNif;
	private String titularEmail;
	private String referencia;


}
