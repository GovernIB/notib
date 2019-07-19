/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.ServeiTipusEnumDto;

/**
 * Command per al manteniment d'enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnviamentCommand {

	@Valid
	private ServeiTipusEnumDto serveiTipus;
	private Date caducitat;
	@Valid
	private PersonaCommand titular;
	@Valid
	private List<PersonaCommand> destinataris;
	@Valid
	private boolean entregaPostalActiva;
	@Valid
	private EntregapostalCommand entregaPostal;
	@Valid
	private boolean entregaDehActiva;
	private EntregaDehCommand entregaDeh;
	
	
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
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
	public boolean isEntregaPostalActiva() {
		return entregaPostalActiva;
	}
	public void setEntregaPostalActiva(boolean entregaPostalActiva) {
		this.entregaPostalActiva = entregaPostalActiva;
	}
	public EntregapostalCommand getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(EntregapostalCommand entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public boolean isEntregaDehActiva() {
		return entregaDehActiva;
	}
	public void setEntregaDehActiva(boolean entregaDehActiva) {
		this.entregaDehActiva = entregaDehActiva;
	}
	public EntregaDehCommand getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(EntregaDehCommand entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
