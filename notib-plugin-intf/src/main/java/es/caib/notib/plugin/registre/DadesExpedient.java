package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre l'expedient de l'aplicació de tramitació
 * per a fer notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DadesExpedient {

	private String identificador;
	private String clau;
	private String unitatAdministrativa;

}
