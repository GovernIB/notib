package es.caib.notib.client;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.ws.notificacio.InformacioEnviament;

@JsonAutoDetect
@XmlRootElement
public class InformacioResposta {

	String codiResposta;
	String descripcioResposta;
	InformacioEnviament informacioEnviament;
	
	public String getCodiResposta() {
		return codiResposta;
	}
	public void setCodiResposta(String codiResposta) {
		this.codiResposta = codiResposta;
	}
	public String getDescripcioResposta() {
		return descripcioResposta;
	}
	public void setDescripcioResposta(String descripcioResposta) {
		this.descripcioResposta = descripcioResposta;
	}
	public InformacioEnviament getInformacioEnviament() {
		return informacioEnviament;
	}
	public void setInformacioEnviament(InformacioEnviament informacioEnviament) {
		this.informacioEnviament = informacioEnviament;
	}
	
}
