package es.caib.notib.war.command;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

public class PersonaCommand {

	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String telefon;
	private String email;
	private String dir3codi;
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDir3codi() {
		return dir3codi;
	}
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}

	public List<PersonaCommand> toListCommand(PersonaCommand command){
		try {
			List<PersonaCommand> destinataris;
			
			command.getNom();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public static PersonaCommand asCommand(PersonaDto dto) {
		if (dto == null) {
			return null;
		}
		PersonaCommand command = ConversioTipusHelper.convertir(
				dto,
				PersonaCommand.class );
		return command;
	}
	public static PersonaDto asDto(PersonaCommand command) {
		if (command == null) {
			return null;
		}
		PersonaDto dto = ConversioTipusHelper.convertir(
				command,
				PersonaDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
