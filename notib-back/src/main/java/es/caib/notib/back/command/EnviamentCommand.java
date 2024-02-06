/**
 * 
 */
package es.caib.notib.back.command;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import es.caib.notib.client.domini.ServeiTipus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class EnviamentCommand {

	private Long id;
	private ServeiTipus serveiTipus;
	private Date caducitat;
	@Valid @NotNull
	private PersonaCommand titular;
	@Valid
	private List<PersonaCommand> destinataris;
	@Valid
	private EntregapostalCommand entregaPostal;
	@Valid
	private EntregaDehCommand entregaDeh;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
