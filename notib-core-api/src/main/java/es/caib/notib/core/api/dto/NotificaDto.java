package es.caib.notib.core.api.dto;

import es.caib.notib.client.domini.EnviamentEstat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NotificaDto {
	
	private String notificaReferencia;
	private String notificaIdentificador;
	private Date notificaDataCreacio;
	private Date notificaDataDisposicio;
	private Date notificaDataCaducitat;
	private String notificaEmisorDir3;
	private String notificaEmisorDescripcio;
	private String notificaEmisorNif;
	private String notificaArrelDir3;
	private String notificaArrelDescripcio;
	private String notificaArrelNif;
	private EnviamentEstat notificaEstat;
	private Date notificaEstatData;
	private String notificaEstatDescripcio;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatReceptorNom;
	private String notificaDatatNumSeguiment;
	private String notificaDatatErrorDescripcio;
	private Date notificaCertificacioData;
	private String notificaCertificacioArxiuId;
	private String notificaCertificacioArxiuNom;
	private String notificaCertificacioHash;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioCsv;
	private String notificaCertificacioMime;
	private Integer notificaCertificacioTamany;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	private boolean notificaError;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
}
