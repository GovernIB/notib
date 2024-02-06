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
public class NotificacioEnviamentFiltreCommand extends FiltreCommand {
	
	private Long id;
	private String dataCreacioInici;
	private String dataCreacioFi;
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

	public void setDataEnviamentInici(String dataEnviamentInici) {

		validarData(dataEnviamentInici, "enviament.list.dataenviament.inici");
		this.dataEnviamentInici = dataEnviamentInici;
	}

	public void setDataEnviamentFi(String dataEnviamentFi) {

		validarData(dataEnviamentFi, "enviament.list.dataenviament.fi");
		this.dataEnviamentFi = dataEnviamentFi;
	}

	public void setDataProgramadaDisposicioInici(String dataProgramadaDisposicioInici) {

		validarData(dataProgramadaDisposicioInici, "enviament.list.dataProgramadaDisposicio.inici");
		this.dataProgramadaDisposicioInici = dataProgramadaDisposicioInici;
	}

	public void setDataProgramadaDisposicioFi(String dataProgramadaDisposicioFi) {

		validarData(dataProgramadaDisposicioFi, "enviament.list.dataProgramadaDisposicio.fi");
		this.dataProgramadaDisposicioFi = dataProgramadaDisposicioFi;
	}

	public void setDataRegistreInici(String dataRegistreInici) {

		validarData(dataRegistreInici, "enviament.list.dataRegistre.inici");
		this.dataRegistreInici = dataRegistreInici;
	}

	public void setDataRegistreFi(String dataRegistreFi) {

		validarData(dataRegistreFi, "enviament.list.dataRegistre.fi");
		this.dataRegistreFi = dataRegistreFi;
	}

	public void setDataCaducitatInici(String dataCaducitatInici) {

		validarData(dataCaducitatInici, "enviament.list.dataCaducitat.inici");
		this.dataCaducitatInici = dataCaducitatInici;
	}

	public void setDataCaducitatFi(String dataCaducitatFi) {

		validarData(dataCaducitatFi, "enviament.list.dataCaducitat.fi");
		this.dataCaducitatFi = dataCaducitatFi;
	}

	
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
