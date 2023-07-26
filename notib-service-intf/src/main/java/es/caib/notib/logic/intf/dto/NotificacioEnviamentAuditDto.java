package es.caib.notib.logic.intf.dto;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import lombok.Data;

import java.util.Date;

@Data
public class NotificacioEnviamentAuditDto {

    private TipusOperacio tipusOperacio;
    private String joinPoint;
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private Long enviamentId;
    private Long notificacioId;
    private Long titularId;
    private String destinataris;
    private NotificaDomiciliConcretTipus domiciliTipus;
    private String domicili;
    private ServeiTipus serveiTipus;
    private Integer cie;
    private String formatSobre;
    private String formatFulla;
    private Boolean dehObligat;
    private String dehNif;
    private String notificaReferencia;
    private String notificaIdentificador;
    private Date notificaDataCreacio;
    private Date notificaDataDisposicio;
    private Date notificaDataCaducitat;
    private String notificaEmisorDir3;
    private String notificaArrelDir3;
    private EnviamentEstat notificaEstat;
    private Date notificaEstatData;
    private boolean notificaEstatFinal;
    private String notificaDatatOrigen;
    private String notificaDatatReceptorNif;
    private String notificaDatatNumSeguiment;
    private Date notificaCertificacioData;
    private String notificaCertificacioArxiuId;
    private String notificaCertificacioOrigen;
    private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
    private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
    private String notificaCertificacioNumSeguiment;
    private String registreNumeroFormatat;
    private Date registreData;
    private NotificacioRegistreEstatEnumDto registreEstat;
    private boolean registreEstatFinal;
    private Date sirConsultaData;
    private Date sirRecepcioData;
    private Date sirRegDestiData;
    private Long notificacioErrorEvent;
    private boolean notificaError;
    private String notificaDatatErrorDescripcio;
}
