/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.core.api.dto.TipusEnumDto;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@JsonAutoDetect
@XmlRootElement
public class PermisConsulta {

	private String codiDir3Entitat;
	private String usuariCodi;
	private String procedimentCodi;
	private boolean permisConsulta;

	public String getCodiDir3Entitat() {
		return codiDir3Entitat;
	}

	public void setCodiDir3Entitat(String codiDir3Entitat) {
		this.codiDir3Entitat = codiDir3Entitat;
	}

	public String getUsuariCodi() {
		return usuariCodi;
	}

	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}

	public String getProcedimentCodi() {
		return procedimentCodi;
	}

	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}

	public boolean isPermisConsulta() {
		return permisConsulta;
	}

	public void setPermisConsulta(boolean permisConsulta) {
		this.permisConsulta = permisConsulta;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
