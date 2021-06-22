package es.caib.notib.core.api.dto.notificacio;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un enviament massiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class NotificacioMassivaDto implements Serializable {
	private String ficheroCsvNom;
	private String ficheroZipNom;
	private byte[] ficheroCsvBytes;
	private byte[] ficheroZipBytes;
	private Date caducitat;
	private String email;
	private Long pagadorPostalId;
}
