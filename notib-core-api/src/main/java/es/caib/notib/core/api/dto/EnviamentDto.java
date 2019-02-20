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
	private boolean entregaPostalActiva;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	
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
	public boolean isEntregaPostalActiva() {
		return entregaPostalActiva;
	}
	public void setEntregaPostalActiva(boolean entregaPostalActiva) {
		this.entregaPostalActiva = entregaPostalActiva;
	}
	public EntregaPostalDto getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(EntregaPostalDto entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaDehDto getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(EntregaDehDto entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = 8694906912117301403L;
}
