package es.caib.notib.persist.filtres;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@Slf4j
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
    private List<String> procedimentsCodisNotib;
    private List<List<String>> procedimentsCodisNotibSplit;
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

    private boolean deleted;

    public void crearProcedimentsCodisNotibSplit() {

        procedimentsCodisNotibSplit = new ArrayList<>(4);
        if (procedimentsCodisNotib == null || procedimentsCodisNotib.isEmpty()) {
            for (var foo = 0; foo<4; foo++) {
                procedimentsCodisNotibSplit.add(new ArrayList<>());
            }
            return;
        }
        var temp = ListUtils.partition(procedimentsCodisNotib, 999);
        if (temp.size() > 4) {
            procedimentsCodisNotibSplit = new ArrayList<>(temp.size());
        }
        procedimentsCodisNotibSplit.addAll(temp);
        while (procedimentsCodisNotibSplit.size() < 4) {
            procedimentsCodisNotibSplit.add(new ArrayList<>());
        }
    }

}
