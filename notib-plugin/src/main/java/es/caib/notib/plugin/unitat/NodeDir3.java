/**
 * 
 */
package es.caib.notib.plugin.unitat;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data 
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeDir3 implements Serializable, Comparable<NodeDir3> {

	@NonNull
	@JsonProperty("codigo")
	private String codi;
	@NonNull
	@JsonProperty("denominacion")
	private String denominacio;
	@JsonProperty("descripcionEstado")
	private String estat;
	@JsonProperty("raiz")
	private String arrel;
	@JsonProperty("superior")
	private String superior;
	@JsonProperty("localidad")
	private String localitat;
	@JsonProperty("idPadre")
	private String idPare;
	
	
	@Override
	public int compareTo(NodeDir3 o) {
		return denominacio.compareToIgnoreCase(o.getDenominacio());
	}
	
	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getDenominacio() {
		return denominacio;
	}

	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}

	public String getEstat() {
		return estat;
	}

	public void setEstat(String estat) {
		this.estat = estat;
	}

	public String getArrel() {
		return arrel;
	}

	public void setArrel(String arrel) {
		this.arrel = arrel;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getLocalitat() {
		return localitat;
	}

	public void setLocalitat(String localitat) {
		this.localitat = localitat;
	}

	public String getIdPare() {
		return idPare;
	}

	public void setIdPare(String idPare) {
		this.idPare = idPare;
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
