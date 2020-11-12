/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
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
	private ServeiTipusEnumDto serveiTipus;
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
