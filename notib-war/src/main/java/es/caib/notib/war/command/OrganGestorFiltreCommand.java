package es.caib.notib.war.command;

import com.google.common.base.Strings;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorFiltreCommand {
	
	private String codi;
	private String codiPare;
	private String nom;
	private String oficina;
	private OrganGestorEstatEnum estat;
	private boolean entregaCie;

	public static OrganGestorFiltreCommand asCommand(OrganGestorFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		OrganGestorFiltreCommand command = ConversioTipusHelper.convertir(dto,OrganGestorFiltreCommand.class );
		return command;
	}
	public OrganGestorFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, OrganGestorFiltreDto.class);
	}

	public boolean isEmpty() {
		return Strings.isNullOrEmpty(codi) && Strings.isNullOrEmpty(codiPare) && Strings.isNullOrEmpty(nom) && Strings.isNullOrEmpty(oficina) && estat == null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
