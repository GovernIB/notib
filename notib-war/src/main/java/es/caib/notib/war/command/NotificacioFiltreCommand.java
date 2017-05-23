/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioFiltreCommand {
	
	
	private String concepte;
	private Date dataInici;
	private Date dataFi;
	private String destinatari;
	
	private Long entitatId;
	
	
	
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	
	public String getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}
	
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	
	
	
	public static NotificacioFiltreCommand asCommand(NotificacioFiltreDto dto) {
		
		if(dto == null) return null;
		
		NotificacioFiltreCommand command = ConversioTipusHelper.convertir( dto, NotificacioFiltreCommand.class );
		
		return command;
		
	}
	
	public static NotificacioFiltreDto asDto(NotificacioFiltreCommand command) {
		
		if(command == null) return null;
		
		NotificacioFiltreDto dto = ConversioTipusHelper.convertir( command, NotificacioFiltreDto.class );
		if( command.getDataInici() == null ) dto.setDataInici( new Date(0) );
		if( command.getDataFi() == null ) dto.setDataFi( new Date(Long.MAX_VALUE) );
		
		return dto;
		
	}
	
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
