package es.caib.notib.core.api.dto.notificacio;

import es.caib.notib.client.domini.IdiomaEnumDto;
import es.caib.notib.core.api.dto.AuditoriaDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificacio que s'ha d'utilitzar per a
 * crear/modificar un registre a la base de daes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class NotificacioDatabaseDto extends AuditoriaDto {

	private Long id;
	private ProcSerDto procediment;
	private String organGestorCodi;
	private GrupDto grup;
	private String usuariCodi;
	private String emisorDir3Codi;

	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	private String numExpedient;
	private IdiomaEnumDto idioma;

	private List<NotEnviamentDatabaseDto> enviaments = new ArrayList<>();

	private DocumentDto document;
	private DocumentDto document2;
	private DocumentDto document3;
	private DocumentDto document4;
	private DocumentDto document5;

	// Llista d'errors detectats al crear una notificació en enviaments massius
	private List<String> errors = new ArrayList<>();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
