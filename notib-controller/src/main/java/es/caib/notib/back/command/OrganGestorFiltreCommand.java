package es.caib.notib.back.command;

import com.google.common.base.Strings;
import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
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
	private String isFiltre;

	public static OrganGestorFiltreCommand asCommand(OrganGestorFiltreDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto,OrganGestorFiltreCommand.class ) : null;
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
