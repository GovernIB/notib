/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioEnviamentFiltreDto extends AuditoriaDto {

//	private Long id;
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
	private String nomTitular;
	private String emailTitular;
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
	private String dir3Codi;
	private String titularNomLlinatge;
	private String uuid;
	private String referenciaNotificacio;
	private Boolean entregaPostal;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
