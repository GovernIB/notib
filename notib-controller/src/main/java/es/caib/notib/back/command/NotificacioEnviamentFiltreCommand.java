package es.caib.notib.back.command;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioTipusEnviamentEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;

/**
 * Command per al manteniment del filtre d'enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class NotificacioEnviamentFiltreCommand {
	
	private Long id;
	private String dataEnviamentInici;
	private String dataEnviamentFi;
	private String dataProgramadaDisposicioInici;
	private String dataProgramadaDisposicioFi;
	private String codiNotifica;
	private String codiProcediment;
	private String grup;
	private String usuari;
	private NotificacioTipusEnviamentEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private String nifTitular;
	private String titularNomLlinatge;
	private String emailTitular;
	private String dir3Codi;
	private String destinataris;
	private String registreLlibre;
	private String registreNumero;
	private String dataRegistreInici;
	private String dataRegistreFi;
	private String dataCaducitatInici;
	private String dataCaducitatFi;
	private String codiNotibEnviament;
	private String numeroCertCorreus;
	private String csvUuid;
	private NotificacioEstatEnumDto estat;
	private String referenciaNotificacio;

	
	public static NotificacioEnviamentFiltreCommand asCommand(NotificacioEnviamentFiltreDto dto) {
		return ConversioTipusHelper.convertir(dto, NotificacioEnviamentFiltreCommand.class);
	}
	public static NotificacioEnviamentFiltreDto asDto(NotificacioEnviamentFiltreCommand command) {
		return ConversioTipusHelper.convertir(command, NotificacioEnviamentFiltreDto.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
