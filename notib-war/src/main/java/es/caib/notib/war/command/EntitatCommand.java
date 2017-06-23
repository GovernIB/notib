/**
 * 
 */
package es.caib.notib.war.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.DocumentIdentitat;
import es.caib.notib.war.validation.EntitatValorsNoRepetits;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@CodiEntitatNoRepetit(campId = "id", campCodi = "codi")
@EntitatValorsNoRepetits
public class EntitatCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom; 
	@NotEmpty @Size(max=9) @DocumentIdentitat
	private String cif;
	@NotNull 
	private EntitatTipusEnumDto tipus;
	@Size(max=9)
	private String dir3Codi;

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
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public EntitatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(EntitatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	
	public static List<EntitatCommand> toEntitatCommands(
			List<EntitatDto> dtos) {
		List<EntitatCommand> commands = new ArrayList<EntitatCommand>();
		for (EntitatDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							EntitatCommand.class));
		}
		return commands;
	}

	public static EntitatCommand asCommand(EntitatDto dto) {	
		
		return ConversioTipusHelper.convertir( dto, EntitatCommand.class );
	}
	public static EntitatDto asDto(EntitatCommand command) {
		
		return ConversioTipusHelper.convertir( command, EntitatDto.class );
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
