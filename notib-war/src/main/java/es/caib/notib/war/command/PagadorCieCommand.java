package es.caib.notib.war.command;

import java.util.Date;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import com.sun.istack.NotNull;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PagadorCieCommand {
	
	private Long id;
	@NotEmpty @Size(max=64)
	private String dir3codi;
	@NotNull
	private Date contracteDataVig;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDir3codi() {
		return dir3codi;
	}
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}
	public Date getContracteDataVig() {
		return contracteDataVig;
	}
	public void setContracteDataVig(Date contracteDataVig) {
		this.contracteDataVig = contracteDataVig;
	}
	
	public static PagadorCieCommand asCommand(PagadorCieDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorCieCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorCieCommand.class );
		return command;
	}
	public static PagadorCieDto asDto(PagadorCieCommand command) {
		if (command == null) {
			return null;
		}
		PagadorCieDto dto = ConversioTipusHelper.convertir(
				command,
				PagadorCieDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
