package es.caib.notib.persist.objectes;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.persist.entity.EntitatEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class FiltreEnviament {

    private boolean entitatIdNull;
    private Long entitatId;
    private boolean dataEnviamentIniciNull;
    private Date dataEnviamentInici;
    private boolean dataEnviamentFiNull;
    private Date dataEnviamentFi;
    private boolean dataProgramadaDisposicioIniciNull;
    private Date dataProgramadaDisposicioInici;
    private boolean dataProgramadaDisposicioFiNull;
    private Date dataProgramadaDisposicioFi;
    private boolean codiNotificaNull;
    private String codiNotifica;
    private boolean codiProcedimentNull;
    private String codiProcediment;
    private boolean grupNull;
    private String grup;
    private boolean usuariNull;
    private String usuari;
    private boolean enviamentTipusNull;
    private EnviamentTipus enviamentTipus;
    private boolean concepteNull;
    private String concepte;
    private boolean descripcioNull;
    private String descripcio;
    private boolean nifTitularNull;
    private String nifTitular;
    private boolean nomTitularNull;
    private String nomTitular;
    private boolean emailTitularNull;
    private String emailTitular;
    private boolean registreNumeroNull;
    private String registreNumero;
    private boolean dataRegistreIniciNull;
    private Date dataRegistreInici;
    private boolean dataRegistreFiNull;
    private Date dataRegistreFi;
    private boolean dataCaducitatIniciNull;
    private Date dataCaducitatInici;
    private boolean dataCaducitatFiNull;
    private Date dataCaducitatFi;
    private boolean codiNotibEnviamentNull;
    private String codiNotibEnviament;
    private boolean numeroCertCorreusNull;
    private String numeroCertCorreus;
    private boolean csvUuidNull;
    private String csvUuid;
    private boolean estatNull;
    private NotificacioEstatEnumDto estat;
    private EnviamentEstat notificaEstat;
    private boolean dir3CodiNull;
    private String dir3Codi;
    private boolean creadaPerNull;
    private String creadaPerCodi;

    private boolean nomesSenseErrors;
    private boolean nomesAmbErrors;
    private boolean hasZeronotificaEnviamentIntentNull;
    private Boolean hasZeronotificaEnviamentIntent;
    private boolean referenciaNotificacioNull;
    private String referenciaNotificacio;

    private boolean procedimentsCodisNotibNull;
    private List<String> procedimentsCodisNotib;
    private boolean organsGestorsCodisNotibNull;
    private boolean organsGestorsComunsCodisNotibNull;
    private List<? extends String> organsGestorsComunsCodisNotib;
    private List<String> organsGestorsCodisNotib;
    private boolean procedimentOrgansAmbPermisNull;
    private List<String> procedimentOrgansAmbPermis;
    private List<String> organs;
    private EntitatEntity entitat;
    private List<String> rols;
    private String usuariCodi;
    private boolean isUsuari;
    private boolean isAdminOrgan;
    private boolean isSuperAdmin;
    private List<EntitatEntity> entitatsActives;

    public boolean isUsuari() {
        return isUsuari;
    }
}
