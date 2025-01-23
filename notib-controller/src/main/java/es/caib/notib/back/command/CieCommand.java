package es.caib.notib.back.command;

import es.caib.notib.back.validation.ValidCie;
import es.caib.notib.back.validation.ValidNotificacio;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per al manteniment de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@ValidCie
public class CieCommand {
	
	private Long id;

	@NotEmpty
	@Size(max=64)
	private String organismePagadorCodi;
	@NotEmpty
	@Size(max=64)
	private String organismeEmisorCodi;
	private String nom;
	private Date contracteDataVig;
	private String apiKey;
	private boolean cieExtern = true;

	public void setCieNotifica(boolean cieNotifica) {
		cieExtern = !cieNotifica;
	}

	public boolean isCieNotifica() {
		return !cieExtern;
	}

	public static CieCommand asCommand(CieDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, CieCommand.class ) : null;
	}

	public CieDataDto asDto() {
		return ConversioTipusHelper.convertir(this, CieDataDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
