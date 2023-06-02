package es.caib.notib.logic.intf.dto.callback;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Informació sobre l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect
public class NotificacioCanviClient {

	private String identificador;
	private String referenciaEnviament;
	private Date data;
	
	public NotificacioCanviClient() {
		super();
	}
	
	public NotificacioCanviClient(
			String identificador,
			String referenciaEnviament) {
		super();
		this.identificador = identificador;
		this.referenciaEnviament = referenciaEnviament;
		this.data = new Date();
	}
	
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getReferenciaEnviament() {
		return referenciaEnviament;
	}
	public void setReferenciaEnviament(String referenciaEnviament) {
		this.referenciaEnviament = referenciaEnviament;
	}

	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
