package es.caib.notib.core.api.dto.notificacio;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un enviament massiu que es motra a la taula amb el llistat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioMassivaTableItemDto implements Serializable {
	private Long id;
	private Date createdDate;
	private String csvFilename;
	private String zipFilename;
	private String createdByNom;
	private String createdByCodi;
//	private NotificacioMassivaEstatDto estat;
	private Integer progress;

	private NotificacioMassivaEstatDto estatValidacio;
	private NotificacioMassivaEstatDto estatProces;

	private Integer totalNotificacions;
	private Integer notificacionsValidades;
	private Integer notificacionsProcessades;
	private Integer notificacionsProcessadesAmbError;

	public String getCreatedByComplet() {
		String nomComplet = "";
		if (createdByNom != null && !createdByNom.isEmpty())
			nomComplet += createdByNom + " ";
		if (createdByCodi != null && !createdByCodi.isEmpty())
			nomComplet += "(" + createdByCodi + ")";
		return nomComplet;
	}
}
