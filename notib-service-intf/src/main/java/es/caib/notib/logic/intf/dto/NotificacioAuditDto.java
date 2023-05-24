package es.caib.notib.logic.intf.dto;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import lombok.Data;

import java.util.Date;

@Data
public class NotificacioAuditDto {

    private TipusOperacio tipusOperacio;
    private String joinPoint;
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private Long notificacioId;
    private NotificacioComunicacioTipusEnumDto comunicacioTipus;
    private TipusUsuariEnumDto tipusUsuari;
    private String usuari;
    private String emisor;
    private NotificaEnviamentTipusEnumDto tipus;
    private Long entitatId;
    private String organ;
    private String procediment;
    private String grup;
    private String concepte;
    private String descripcio;
    private String numExpedient;
    private Date enviamentDataProgramada;
    private Integer retard;
    private Date caducitat;
    private Long documentId;
    private NotificacioEstatEnumDto estat;
    private Date estatDate;
    private String motiu;
    private Long pagadorPostalId;
    private Long pagadorCieId;
    private int registreEnviamentIntent;
    private Integer registreNumero;
    private String registreNumeroFormatat;
    private Date registreData;
    private Date notificaEnviamentData;
    private int notificaEnviamentIntent;
//    private NotificacioErrorTipusEnumDto notificaErrorTipus;
    private boolean errorLastCallback;
    private Long errorEventId;
}
