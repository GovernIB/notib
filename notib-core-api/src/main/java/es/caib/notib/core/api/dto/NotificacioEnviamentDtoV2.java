/**
 * 
 */
package es.caib.notib.core.api.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioEnviamentDtoV2 extends AuditoriaDto {

	private Long id;
	//private NotificacioDtoV2 notificacio;
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
//	private Date caducitat;
	
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
	private Date sirRecepcioData;
	private Date sirRegDestiData;
	private int registreNumero;
	private Date registreData;
	
	//Notific@
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private String notificaCertificacioArxiuNom;
	private Date notificaCertificacioData;
	private NotificacioEnviamentEstatEnumDto notificaEstat;
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
//	public String getDestinatarisNomLlinatges() {
//		destinatarisNomLlinatges = "";
//		for(PersonaDto destinatari: destinataris) {
//			destinatarisNomLlinatges += concatenarNomLlinatges(llinatgesDestinatari(destinatari), destinatari.getNom(), destinatari.getRaoSocial(), null)+"</br>";
//		}
//		return destinatarisNomLlinatges;
//	}
	public NotificacioEstatEnumDto getEstat() {
		if (isEnviant){
			return NotificacioEstatEnumDto.ENVIANT;
		}
		return this.estat;
	}
//
//	public String getTitularNomLlinatge() {
//		if(this.titularNomLlinatge != null) {
//			return this.titularNomLlinatge;
//		}else {
//			if(titular != null) {
//				titularNomLlinatge = concatenarNomLlinatges(
//						getTitularLlinatges(),
//						titular.getNom(),
//						titular.getRaoSocial(),
//						null);
//			}
//			return titularNomLlinatge;
//		}
//	}
//
//	public String getTitularLlinatges() {
//		return concatenarLlinatges(
//				titular.getLlinatge1(),
//				titular.getLlinatge2());
//	}
//
//	public String llinatgesDestinatari(PersonaDto destinatari) {
//		return concatenarLlinatges(
//				destinatari.getLlinatge1(),
//				destinatari.getLlinatge2());
//	}
//
//	private String concatenarLlinatges(
//			String llinatge1,
//			String llinatge2) {
//		if (llinatge1 == null && llinatge2 == null) {
//			return null;
//		}
//		StringBuilder sb = new StringBuilder();
//		sb.append(llinatge1);
//		if (llinatge2 != null && !llinatge2.isEmpty()) {
//			sb.append(" ");
//			sb.append(llinatge2);
//		}
//		return sb.toString();
//	}
//
//	private String concatenarNomLlinatges(
//			String llinatges,
//			String nom,
//			String raoSocial,
//			String destinatariNif) {
//		StringBuilder sb = new StringBuilder();
//
//		if (destinatariNif != null) {
//			sb.append(destinatariNif);
//			sb.append(" - ");
//		}
//		if (llinatges != null && !llinatges.isEmpty()) {
//			sb.append("[");
//			sb.append(llinatges);
//		}
//
//		if (nom != null && !nom.isEmpty()) {
//			sb.append(", ");
//			sb.append(nom);
//
//			if (raoSocial == null) {
//				sb.append("]");
//			}
//		}
//		if (raoSocial != null && !raoSocial.isEmpty()) {
//			sb.append(" | ");
//			sb.append(raoSocial);
//			sb.append("]");
//		}
//		return sb.toString();
//	}

	private static final long serialVersionUID = -139254994389509932L;

}
