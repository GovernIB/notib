package es.caib.notib.logic.intf.dto.notenviament;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.dto.EntregaDehDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.cie.EntregaPostalDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'un destinatari d'una anotació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class EnviamentInfo extends NotificacioEnviamentDatatableDto{

	private Long id;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipus serveiTipus;

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
	protected int sirConsultaIntent;
	private Date sirRecepcioData;
	private Date sirRegDestiData;
	private boolean sirFiPooling;
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

	private NotificacioEventDto ultimEvent;
	private boolean ultimEventError;
	private boolean errorLastCallback;
	private boolean callbackFiReintents;
	private String callbackFiReintentsDesc;
	private String notificacioMovilErrorDesc;


	public boolean isNotificat() {

		return EnviamentEstat.NOTIFICADA.equals(notificaEstat) || entregaPostal != null && CieEstat.NOTIFICADA.name().equals(entregaPostal.getCieEstat());
	}

	public boolean isUltimEventError() {
		return ultimEvent != null && ultimEvent.isError();
	}

}
