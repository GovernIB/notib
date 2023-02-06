package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre la notificació en una anotació de sortida
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DadesNotificacio extends DadesAnotacio {

	private boolean justificantRecepcio;
	private String avisTitol;
	private String avisText;
	private String avisTextSms;
	private String oficiTitol;
	private String oficiText;
	private TramitSubsanacio oficiTramitSubsanacio;

}
