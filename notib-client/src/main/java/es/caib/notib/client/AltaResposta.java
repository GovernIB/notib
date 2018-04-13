package es.caib.notib.client;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
@XmlRootElement
public class AltaResposta {

	String codiResposta;
	String descripcioResposta;
	List<String> referencies;
	
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
	public List<String> getReferencies() {
		return referencies;
	}
	public void setReferencies(List<String> referencies) {
		this.referencies = referencies;
	}
	
}
