/**
 * 
 */
package es.caib.notib.back.command;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatOrdreFiltre;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Calendar;
import java.util.Date;

/**
 * Command per al manteniment del filtre de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Getter
@Setter
public class NotificacioFiltreCommand extends FiltreCommand {

	private boolean filtreSimpleActiu = true;
	private Long entitatId;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private EnviamentTipus enviamentTipus;
	private NotificacioEstatOrdreFiltre estat;
	private String concepte;
	private Date dataInici;
	private Date dataFi;
	private Date dataCaducitatInici;
	private Date dataCaducitatFi;
	private String titular;
	private String organGestor;
	private Long procedimentId;
	private Long serveiId;
	private TipusUsuariEnumDto tipusUsuari;
	private String numExpedient;
	private String creadaPer;
	private String identificador;
	private String registreNum;
	private String referencia;
	private boolean nomesAmbErrors;
	private boolean nomesAmbEntregaPostal;
	private boolean nomesFiReintents;
	private boolean hasErrors;
	private boolean deleted;
	private boolean massiu;

	public void setDataInici(Date dataInici) {

		validarData(dataInici, "notificacio.list.filtre.camp.datainici");
		this.dataInici = dataInici;
	}

	public void setDataFi(Date dataFi) {

		validarData(dataFi, "notificacio.list.filtre.camp.datafi");
		this.dataFi = dataFi;
	}

	public static NotificacioFiltreCommand asCommand(NotificacioFiltreDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, NotificacioFiltreCommand.class ) : null;
	}

	public static NotificacioFiltreDto asDto(NotificacioFiltreCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, NotificacioFiltreDto.class) : null;
	}

	public NotificacioFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, NotificacioFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public void setDefaultFiltreData() {

		if (massiu) {
			return;
		}

		try {

			if (dataFi == null) {
				var c = Calendar.getInstance();
				c.setTime(new Date());
				c.set(Calendar.SECOND, 59);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.HOUR_OF_DAY, 23);
				dataFi = c.getTime();
			}
			if (dataInici != null) {
				return;
			}
			var c = Calendar.getInstance();
			c.setTime(dataFi);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.add(Calendar.MONTH, -3);
            dataInici = c.getTime();
		} catch (Exception ex) {
			log.error("Error parsejant la data", ex);
		}
	}

}
