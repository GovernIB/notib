/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * Acció realitzada sobre una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class IntegracioAccioDto implements Serializable {

	private Long index;
	private Date data;
	private String descripcio;
	private String aplicacio;
	private List<AccioParam> parametres;
	private IntegracioDto integracio;
	private IntegracioAccioTipusEnumDto tipus;
	private long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;
	private String codiEntitat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;

	public int getParametresCount() {
		if (parametres == null) {
			return 0;
		} else {
			return parametres.size();
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
