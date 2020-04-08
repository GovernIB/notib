/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment del filtre de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioFiltreCommand {
	
	private Long entitatId;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private NotificacioEstatEnumDto estat;
	private String concepte;
	private Date dataInici;
	private Date dataFi;
	private String titular;
	private Long procedimentId;
	private TipusUsuariEnumDto tipusUsuari;
	private String numExpedient;
	
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public NotificacioComunicacioTipusEnumDto getComunicacioTipus() {
		return comunicacioTipus;
	}
	public void setComunicacioTipus(NotificacioComunicacioTipusEnumDto comunicacioTipus) {
		this.comunicacioTipus = comunicacioTipus;
	}
	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
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
	public String getTitular() {
		return titular;
	}
	public void setTitular(String titular) {
		this.titular = titular;
	}
	public Long getProcedimentId() {
		return procedimentId;
	}
	public void setProcedimentId(Long procedimentId) {
		this.procedimentId = procedimentId;
	}
	public TipusUsuariEnumDto getTipusUsuari() {
		return tipusUsuari;
	}
	public void setTipusUsuari(TipusUsuariEnumDto tipusUsuari) {
		this.tipusUsuari = tipusUsuari;
	}
	public String getNumExpedient() {
		return numExpedient;
	}
	public void setNumExpedient(String numExpedient) {
		this.numExpedient = numExpedient;
	}
	public static NotificacioFiltreCommand asCommand(NotificacioFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		NotificacioFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				NotificacioFiltreCommand.class );
		return command;
	}
	public static NotificacioFiltreDto asDto(NotificacioFiltreCommand command) {
		if (command == null) {
			return null;
		}
		NotificacioFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				NotificacioFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
