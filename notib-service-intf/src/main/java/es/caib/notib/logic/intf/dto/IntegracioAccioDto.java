/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * Acció realitzada sobre una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class IntegracioAccioDto implements Serializable {

	private Long id;
	private Date data;
	private String descripcio;
	private IntegracioAccioTipusEnumDto tipus;
	private long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private String codiUsuari;
	private String codiEntitat;
//	private String errorDescripcio;
//	private String excepcioMessage;
//	private String excepcioStacktrace;
//	private List<AccioParam> parametres;
	private String aplicacio;
//	private IntegracioDto integracio;
//	private EntitatDto entitat;

//	public int getParametresCount() {
//		if (parametres == null) {
//			return 0;
//		} else {
//			return parametres.size();
//		}
//	}

	private static final long serialVersionUID = -139254994389509932L;

}
