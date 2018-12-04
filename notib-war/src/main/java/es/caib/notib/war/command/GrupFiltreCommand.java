package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.GrupFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GrupFiltreCommand {
	
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=64)
	private String nom;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public static GrupFiltreCommand asCommand(GrupDto dto) {
		if (dto == null) {
			return null;
		}
		GrupFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				GrupFiltreCommand.class );
		return command;
	}
	public static GrupFiltreDto asDto(GrupFiltreCommand command) {
		if (command == null) {
			return null;
		}
		GrupFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				GrupFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
