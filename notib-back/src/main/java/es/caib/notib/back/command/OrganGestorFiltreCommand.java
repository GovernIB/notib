package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.logic.intf.dto.organisme.NumeroPermisos;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorFiltreDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
	private boolean permetreSir;
	private NumeroPermisos numeroPermisos;

	private String isFiltre;

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
		return StringUtils.isEmpty(codi) && StringUtils.isEmpty(codiPare) && StringUtils.isEmpty(nom) && StringUtils.isEmpty(oficina) && estat == null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
