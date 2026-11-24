/**
 *
 */
package es.caib.notib.logic.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalTime;

/**
 * Informació d'una aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class AplicacioDto extends AuditoriaDto {

	private Long id;
	private String usuariCodi;
	private String callbackUrl;
	private boolean activa;
	private boolean headerCsrf;
	private Long entitatId;
	private boolean aplicarLimitEnviaments;
    private LocalTime horariLaboralInici;
    private LocalTime horariLaboralFi;
    private Integer maxEnviamentsMinutLaboral;
    private Integer maxEnviamentsMinutNoLaboral;
    private Integer maxEnviamentsDiaLaboral;
    private Integer maxEnviamentsDiaNoLaboral;
	private AplicacioVersio darreraVersioApi;


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
