package es.caib.notib.logic.intf.dto.notenviament;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.cie.EntregaPostalDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * DTO per a crear o editar un enviament d'una notificació a la base de dades
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotEnviamentDatabaseDto extends AuditoriaDto {

	private Long id;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipusEnumDto serveiTipus;
	private String titularNomLlinatge;
	private String destinatarisNomLlinatges;
	private String titularEmail;
	private String titularNif;
	private String usuari;
	private String referencia;
	private String numeroCertCorreus;
	private String csv;
	private String uuid;
	private String detalls;
	private String codiNotibEnviament;
	private String notificaDataCaducitat;
	private boolean perEmail;

	//Info notificació
	private boolean entregaPostalActiva;
	private boolean entregaDehActiva;
	private Date enviamentDataProgramada;
	private String procedimentCodiNotib;
	private String grupCodi;
	private String emisorDir3Codi;
	private String usuariCodi;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private String llibre;
	private NotificacioEstatEnumDto estat;
	private Long notificacioId;
	private String csvUuid;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;

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
	private String notificaCertificacioArxiuNom;
	private Date notificaCertificacioData;
	private EnviamentEstat notificaEstat;
	private Date notificaEstatData;
	private String notificaDatatErrorDescripcio;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatReceptorNom;
	private String notificaDatatNumSeguiment;
	private String notificaCertificacioMime;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioArxiuId;
	private String notificaCertificacioCsv;
	private String notificaReferencia;
	private String notificaIdentificador;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	private boolean notificaError;
	private int notificaIntentNum;

	private boolean isEnviant;

	public NotificacioEstatEnumDto getEstat() {
		if (isEnviant){
			return NotificacioEstatEnumDto.ENVIANT;
		}
		return this.estat;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
