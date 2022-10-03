package es.caib.notib.back.command;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class AvisCommand {

	private Long id;
	@NotEmpty
	private String assumpte;
	@NotEmpty
	private String missatge;
	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFinal;
	private Boolean actiu;
	@NotNull
	private AvisNivellEnumDto avisNivell;
	
	public static AvisCommand asCommand(AvisDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AvisCommand.class);
	}
	public static AvisDto asDto(AvisCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AvisDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
