package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.validation.OrganNoRepetit;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Command per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */

@Getter @Setter
@OrganNoRepetit
public class OrganGestorCommand {
	
	private Long id;
	@NotEmpty
	@Size(max=9)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	private Long entitatId;
	private String entitatNom;
	private String llibre;
	private String llibreNom;
	private String oficina;
	private String oficinaNom;

	private String oficinaEntitat;
	private String estat;
	private boolean cie;

	private boolean entregaCieActiva;
	private Long operadorPostalId;
	private Long cieId;
	private boolean permetreSir;

	private String estatTraduccio;

	public static OrganGestorCommand asCommand(OrganGestorDto dto) {

		if (dto == null) {
			return null;
		}
		var command = ConversioTipusHelper.convertir(dto, OrganGestorCommand.class);
		if (dto.getOficina() != null) {
			command.setOficina(dto.getOficina().getCodi());
			command.setOficinaNom(dto.getOficina().getNom());
		}
		return command;
	}
	public static OrganGestorDto asDto(OrganGestorCommand command) {

		if (command == null) {
			return null;
		}
		var organGestorDto = ConversioTipusHelper.convertir(command, OrganGestorDto.class);
		var oficina = new OficinaDto();
		oficina.setCodi(command.getOficina());
		oficina.setNom(command.getOficinaNom());
		organGestorDto.setOficina(oficina);
		return organGestorDto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
