/**
 * 
 */
package es.caib.notib.war.command;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;

/**
 * Command per al manteniment d'enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnviamentCommand {

	private ServeiTipusEnumDto serveiTipus;
	private PersonaCommand titular;
	private List<PersonaCommand> destinataris;

	private boolean entregaPostalActiva;
	private EntregapostalCommand entregaPostal;
	private EntregaDehCommand entregaDeh;
	
	
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
