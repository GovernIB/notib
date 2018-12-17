package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import es.caib.notib.core.api.dto.PagadorPostalFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment del filtre de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PagadorPostalFiltreCommand {
	
	private String dir3codi;
	private String contracteNum;
	
	public String getDir3codi() {
		return dir3codi;
	}
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}
	public String getContracteNum() {
		return contracteNum;
	}
	public void setContracteNum(String contracteNum) {
		this.contracteNum = contracteNum;
	}
	
	public static PagadorPostalFiltreCommand asCommand(PagadorPostalFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorPostalFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorPostalFiltreCommand.class );
		return command;
	}
	public static PagadorPostalFiltreDto asDto(PagadorPostalFiltreCommand command) {
		if (command == null) {
			return null;
		}
		PagadorPostalFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				PagadorPostalFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
