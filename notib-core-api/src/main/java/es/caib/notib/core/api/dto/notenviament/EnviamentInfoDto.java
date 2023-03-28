package es.caib.notib.core.api.dto.notenviament;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.cie.EntregaPostalDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class EnviamentInfoDto extends NotificacioEnviamentDatatableDto{

	private Long id;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipusEnumDto serveiTipus;

	private String usuari;
	private String referencia;
	private String numeroCertCorreus;
	private String csv;
	private String uuid;
	private String detalls;
	private String codiNotibEnviament;
	private String notificaDataCaducitat;

	//Registre
	private String registreNumeroFormatat;
	private NotificacioRegistreEstatEnumDto registreEstat;
	private boolean registreEstatFinal;
	private Date sirRecepcioData;
	private Date sirRegDestiData;
	private int registreNumero;
	private Date registreData;

	//Notific@
	private Date notificaErrorData;
	private String notificaErrorDescripcio;

	private EnviamentEstat notificaEstat;

	private String notificaCertificacioArxiuNom;
	private Date notificaCertificacioData;
	private String notificaCertificacioMime;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioArxiuId;
	private String notificaCertificacioCsv;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;

	private String notificaIdentificador;
	private boolean notificaError;

}
