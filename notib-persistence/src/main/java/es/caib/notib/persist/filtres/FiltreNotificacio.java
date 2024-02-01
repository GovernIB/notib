package es.caib.notib.persist.filtres;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class FiltreNotificacio {

    private boolean entitatIdNull;
    private Long entitatId;
    private EntitatEntity entitat;
    private boolean enviamentTipusNull;
    private EnviamentTipus enviamentTipus;
    private boolean concepteNull;
    private String concepte;
    private boolean estatNull;
    private Integer estatMask;
    private boolean dataIniciNull;
    private Date dataInici;
    private boolean dataFiNull;
    private Date dataFi;
    private boolean titularNull;
    private String titular;
    private boolean organCodiNull;
    private String organCodi;
    private boolean procedimentNull;
    private String procedimentCodi;
    private boolean tipusUsuariNull;
    private TipusUsuariEnumDto tipusUsuari;
    private boolean numExpedientNull;
    private String numExpedient;
    private boolean creadaPerNull;
    private String creadaPer;
    private boolean identificadorNull;
    private String identificador;
    private boolean registreNumNull;
    private String registreNum;
    private boolean nomesAmbErrors;
    private boolean nomesSenseErrors;
    private boolean referenciaNull;
    private String referencia;
    private boolean isUsuari;
    private boolean procedimentsCodisNotibNull;
    private List<? extends String> procedimentsCodisNotib;
    private List<? extends String> grupsProcedimentCodisNotib;
    private boolean organsGestorsCodisNotibNull;
    private List<? extends String> organsGestorsCodisNotib;
    private boolean procedimentOrgansIdsNotibNull;
    private List<String> procedimentOrgansIdsNotib;
    private String usuariCodi;
    private boolean isUsuariEntitat;
    private boolean isSuperAdmin;
    private List<EntitatEntity> entitatsActives;
    private boolean isAdminOrgan;
    private List<String> organs;
    private boolean notMassivaIdNull;
    private Long notMassivaId;

    private NotificacioMassivaEntity notificacioMassiva;

    private boolean esOrgansGestorsComunsCodisNotibNull;
    private List<? extends String> organsGestorsComunsCodisNotib;
}
