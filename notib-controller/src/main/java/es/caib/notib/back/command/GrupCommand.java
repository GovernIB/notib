package es.caib.notib.back.command;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.back.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GrupCommand {
	
	private Long id;
	@NotEmpty
	@Size(max=255)
	private String codi;
	@NotEmpty @Size(max=255)
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
		return dto != null ? ConversioTipusHelper.convertir(dto, GrupCommand.class ) : null;
	}

	public static GrupDto asDto(GrupCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, GrupDto.class) : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
