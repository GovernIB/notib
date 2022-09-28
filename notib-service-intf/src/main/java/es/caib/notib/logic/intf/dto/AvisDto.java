package es.caib.notib.logic.intf.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class AvisDto implements Serializable {

	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;
	private Boolean avisAdministrador;
	private Long entitatId;
	
	private static final long serialVersionUID = -6004968939457076917L;
	
}
