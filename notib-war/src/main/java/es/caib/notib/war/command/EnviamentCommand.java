package es.caib.notib.war.command;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

public class EnviamentCommand {
	
	@NotEmpty
	private ServeiTipusEnumDto serveiTipus;
	private PersonaCommand titular;
	private PersonaCommand destinatari;
	
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public PersonaCommand getTitular() {
		return titular;
	}
	public void setTitular(PersonaCommand titular) {
		this.titular = titular;
	}
	public PersonaCommand getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(PersonaCommand destinatari) {
		this.destinatari = destinatari;
	}
	
	public static EnviamentCommand asCommand(NotificacioDto dto) {
		if (dto == null) {
			return null;
		}
		EnviamentCommand command = ConversioTipusHelper.convertir(
				dto,
				EnviamentCommand.class );
		return command;
	}
	public static NotificacioDto asDto(NotificacioCommand command) {
		if (command == null) {
			return null;
		}
		NotificacioDto dto = ConversioTipusHelper.convertir(
				command,
				NotificacioDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
