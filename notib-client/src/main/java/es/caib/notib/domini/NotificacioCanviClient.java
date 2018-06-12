package es.caib.notib.domini;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació sobre l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement
@JsonAutoDetect
public class NotificacioCanviClient {

	private String identificador;
	private String referenciaEnviament;
	
	public NotificacioCanviClient() {
		super();
	}
	
	public NotificacioCanviClient(
			String identificador,
			String referenciaEnviament) {
		super();
		this.identificador = identificador;
		this.referenciaEnviament = referenciaEnviament;
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

}
