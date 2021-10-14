package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.procediment.ProcedimentFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcedimentFiltreCommand {
	
	private String codi;
	private String nom;
	private String organGestor;
	private Long entitatId;
	private boolean comu;
	private boolean entregaCieActiva;
	
	
	public static ProcedimentFiltreCommand asCommand(ProcedimentFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		ProcedimentFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcedimentFiltreCommand.class );
		return command;
	}

	public ProcedimentFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				ProcedimentFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
