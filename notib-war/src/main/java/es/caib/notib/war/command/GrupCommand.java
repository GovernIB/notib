package es.caib.notib.war.command;


import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GrupCommand {
	
	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=64)
	private String nom;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public static GrupCommand asCommand(GrupDto dto) {
		if (dto == null) {
			return null;
		}
		GrupCommand command = ConversioTipusHelper.convertir(
				dto,
				GrupCommand.class );
		return command;
	}
	public static GrupDto asDto(GrupCommand command) {
		if (command == null) {
			return null;
		}
		GrupDto dto = ConversioTipusHelper.convertir(
				command,
				GrupDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
