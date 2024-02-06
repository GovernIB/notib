package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Command per al manteniment del filtre de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcSerFiltreCommand {
	
	private String codi;
	private String nom;
	private String organGestor;
	private Long entitatId;
	private boolean comu;
	private boolean entregaCieActiva;
	private boolean actiu;
	private boolean manual;
	private ProcedimentEstat estat;
	
	
	public static ProcSerFiltreCommand asCommand(ProcSerFiltreDto dto) {

		return dto != null ? ConversioTipusHelper.convertir(dto, ProcSerFiltreCommand.class ) : null;
	}

	public ProcSerFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, ProcSerFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
