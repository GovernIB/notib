package es.caib.notib.core.api.dto.notenviament;

import es.caib.notib.core.api.dto.AuditoriaDto;
import es.caib.notib.core.api.dto.EntregaDehDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.cie.EntregaPostalDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * Informació d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EnviamentDto extends AuditoriaDto {
	private ServeiTipusEnumDto serveiTipus;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	private boolean entregaPostalActiva;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private Date caducitat;
	private String raoSocial;
	private String codiEntitatDesti;
}
