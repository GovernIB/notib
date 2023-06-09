package es.caib.notib.back.config.scopedata;

import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Getter @Setter
@SessionScope
public class SessionScopedContext {

    // Usuari actual
    private UsuariDto usuariActual;
    private String rolActual;
    private List<String> rolsDisponibles;
    private EntitatDto entitatActual;
    private List<EntitatDto> entitatsAccessibles;
    private OrganGestorDto organActual;
    private List<OrganGestorDto> organsAccessibles;
    private List<AvisDto> avisos;
    // Permisos
    private Boolean usuariEntitat;
    private Boolean adminEntitat;
    private Boolean adminOrgan;
    private Boolean aplicacioEntitat;
    private Boolean menuNotificacions;
    private Boolean menuComunicacions;
    private Boolean menuSir;

    // Propietats globals
    private String capLogo;
    private String capBackColor;
    private String capColor;
    private String peuLogo;

//    // Missatges error/warning/success/info
//    private List<String> missatgesError;
//    private List<String> missatgesWarning;
//    private List<String> missatgesSuccess;
//    private List<String> missatgesInfo;
//
//    // Filtres
//    private NotificacioFiltreCommand filtreNotificacions;
//    private NotificacioEnviamentFiltreCommand filtreEnviaments;
//    private OrganGestorFiltreCommand filtreOrgans;
//    private ProcSerFiltreCommand filtreProcediments;
//    private ProcSerFiltreCommand filtreServeis;
//    private NotificacioMassivaFiltreCommand filtreMassives;
//    private CieFiltreCommand filtreCie;
//    private OperadorPostalFiltreCommand filtreOperadorsPostals;
//    private AplicacioFiltreCommand filtreAaplicacions;
//    private IntegracioFiltreCommand filtreIntegracions;
//    private GrupFiltreCommand filtreGrups;
//    private NotificacioRegistreErrorFiltreCommand filtreMassiuRegistre;
//    private NotificacioErrorCallbackFiltreCommand filtreMassiuCallbacks;
//
//    // Seleccio massiva
//    private String integracioCodi;
//    private Long enviamentId;
//    private List<Long> enviamentSeleccio;
//    private Set<Long> notificacionsSeleccio;
//    private Set<Long> massivaSeleccio;
//    private Set<Long> massivaRegistreSeleccio;
//
//    // Notificacio
//    private TipusEnviamentEnumDto tipusEnviament;
//    private String referer;

    // Sincronitzacio
    private Integer procedimentsAmbOrganNoSinc;
    private Integer serveisAmbOrganNoSinc;

    // Auxiliar
    private Boolean autenticacioProcessada;
    private Long instantEntitatsCarregades;
    private Long instantOrgansCarregades;



    public String getIdiomaUsuari() {
        return this.usuariActual != null ? this.usuariActual.getIdioma() : "ca";
    }
    public String getEntitatActualCodi() {
        return this.entitatActual != null ? this.entitatActual.getCodi() : null;
    }
    public Long getEntitatActualId() {
        return this.entitatActual != null ? this.entitatActual.getId() : null;
    }
    public String getUsuariActualCodi() {
        return this.usuariActual != null ? this.usuariActual.getCodi() : null;
    }
    public Integer getOrgansProcNoSincronitzats() {
        return procedimentsAmbOrganNoSinc != null ? procedimentsAmbOrganNoSinc : 0;
    }
    public Integer getOrgansServNoSincronitzats() {
        return serveisAmbOrganNoSinc != null ? serveisAmbOrganNoSinc : 0;
    }
    public void cleanPermisosUsuari() {
        this.usuariEntitat = null;
        this.adminEntitat = null;
        this.adminOrgan = null;
        this.aplicacioEntitat = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SessionScopedContext:\n");
        sb.append("\tusuariActual= " + (usuariActual != null ? usuariActual.toString() : "null") + "\n");
        sb.append("\trolActual= " + (rolActual != null ? rolActual : "null") + "\n");
        sb.append("\trolsDisponibles= " + (rolsDisponibles != null ? Arrays.deepToString(rolsDisponibles.toArray()) : "null") + "\n");
        sb.append("\tentitatActual= " + (entitatActual != null ? entitatActual.toString() : "null") + "\n");
        sb.append("\tentitatsAccessibles= " + (entitatsAccessibles != null ? Arrays.deepToString(entitatsAccessibles.toArray()) : "null") + "\n");
        sb.append("\torganActual= " + (organActual != null ? organActual.toString() : "null") + "\n");
        sb.append("\torgansAccessibles= " + (organsAccessibles != null ? Arrays.deepToString(organsAccessibles.toArray()) : "null") + "\n");
        sb.append("\tavisos= " + (avisos != null ? Arrays.deepToString(avisos.toArray()) : "null") + "\n");
        sb.append("\tusuariEntitat= " + (usuariEntitat != null ? usuariEntitat.toString() : "null") + "\n");
        sb.append("\tadminEntitat= " + (adminEntitat != null ? adminEntitat.toString() : "null") + "\n");
        sb.append("\tadminOrgan= " + (adminOrgan != null ? adminOrgan.toString() : "null") + "\n");
        sb.append("\taplicacioEntitat= " + (aplicacioEntitat != null ? aplicacioEntitat.toString() : "null") + "\n");
        sb.append("\tmenuNotificacions= " + (menuNotificacions != null ? menuNotificacions.toString() : "null") + "\n");
        sb.append("\tmenuComunicacions= " + (menuComunicacions != null ? menuComunicacions.toString() : "null") + "\n");
        sb.append("\tmenuSir= " + (menuSir != null ? menuSir.toString() : "null") + "\n");
        sb.append("\tcapLogo= " + (capLogo != null ? capLogo : "null") + "\n");
        sb.append("\tcapBackColor= " + (capBackColor != null ? capBackColor : "null") + "\n");
        sb.append("\tcapColor= " + (capColor != null ? capColor : "null") + "\n");
        sb.append("\tpeuLogo= " + (peuLogo != null ? peuLogo : "null") + "\n");
        sb.append("\tprocedimentsAmbOrganNoSinc= " + (procedimentsAmbOrganNoSinc != null ? procedimentsAmbOrganNoSinc : "null") + "\n");
        sb.append("\tserveisAmbOrganNoSinc= " + (serveisAmbOrganNoSinc != null ? serveisAmbOrganNoSinc : "null") + "\n");
        sb.append("\tautenticacioProcessada= " + (autenticacioProcessada != null ? autenticacioProcessada.toString() : "null") + "\n");
        sb.append("\tinstantEntitatsCarregades= " + (instantEntitatsCarregades != null ? instantEntitatsCarregades.toString() : "null") + "\n");
        sb.append("\tinstantOrgansCarregades= " + (instantOrgansCarregades != null ? instantOrgansCarregades.toString() : "null"));
        return sb.toString();
    }
}
