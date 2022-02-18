package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.OrganNoRepetit;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

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
	@NotEmpty @Size(max=9)
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

	public static OrganGestorCommand asCommand(OrganGestorDto dto) {
		if (dto == null) {
			return null;
		}
		OrganGestorCommand command = ConversioTipusHelper.convertir(
				dto,
				OrganGestorCommand.class);
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
		OrganGestorDto organGestorDto = ConversioTipusHelper.convertir(
				command,
				OrganGestorDto.class);
		OficinaDto oficina = new OficinaDto();
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
