/**
 * 
 */
package es.caib.notib.war.command;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;

/**
 * Command per al manteniment de notificacions manuals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnviamentCommand {

	private ServeiTipusEnumDto serveiTipus;
	private PersonaCommand titular;
	private List<PersonaCommand> destinataris;
	
	
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
	public List<PersonaCommand> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaCommand> destinataris) {
		this.destinataris = destinataris;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
