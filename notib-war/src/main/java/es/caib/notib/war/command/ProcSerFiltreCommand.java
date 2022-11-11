package es.caib.notib.war.command;

import es.caib.notib.war.model.SelectorHtml;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
	private Boolean actiu;

	private List<SelectorHtml> estats = new ArrayList<>();
	
	public static ProcSerFiltreCommand asCommand(ProcSerFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		ProcSerFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcSerFiltreCommand.class );
		return command;
	}

	public ProcSerFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				ProcSerFiltreDto.class);
	}

	public ProcSerFiltreCommand() {
		SelectorHtml elem = SelectorHtml.builder().text("aplicacio.list.filtre.text.activa").value("true").build();
		estats.add(elem);
		elem = SelectorHtml.builder().text("aplicacio.list.filtre.text.inactiva").value("false").build();
		estats.add(elem);
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
