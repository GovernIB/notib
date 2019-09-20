package es.caib.notib.war.command;

import java.util.Date;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PagadorPostalCommand {
	
	private Long id;
	@NotEmpty @Size(max=64)
	private String dir3codi;
	@NotEmpty @Size(max=8)
	private String contracteNum;
	private Date contracteDataVig;
	@NotEmpty @Size(max=64)
	private String facturacioClientCodi;
	
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
	public String getContracteNum() {
		return contracteNum;
	}
	public void setContracteNum(String contracteNum) {
		this.contracteNum = contracteNum;
	}
	public Date getContracteDataVig() {
		return contracteDataVig;
	}
	public void setContracteDataVig(Date contracteDataVig) {
		this.contracteDataVig = contracteDataVig;
	}
	public String getFacturacioClientCodi() {
		return facturacioClientCodi;
	}
	public void setFacturacioClientCodi(String facturacioClientCodi) {
		this.facturacioClientCodi = facturacioClientCodi;
	}
	
	public static PagadorPostalCommand asCommand(PagadorPostalDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorPostalCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorPostalCommand.class );
		return command;
	}
	public static PagadorPostalDto asDto(PagadorPostalCommand command) {
		if (command == null) {
			return null;
		}
		PagadorPostalDto dto = ConversioTipusHelper.convertir(
				command,
				PagadorPostalDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
