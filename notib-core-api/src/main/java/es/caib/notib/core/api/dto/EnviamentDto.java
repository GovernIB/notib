/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnviamentDto extends AuditoriaDto {

	private ServeiTipusEnumDto serveiTipus;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	
	
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public PersonaDto getTitular() {
		return titular;
	}
	public void setTitular(PersonaDto titular) {
		this.titular = titular;
	}
	public List<PersonaDto> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaDto> destinataris) {
		this.destinataris = destinataris;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = 8694906912117301403L;
}
