package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExplotFets {

    private final Long entitatId;
    private final Long procedimentId;
    private final String organCodi;
    private final String usuariCodi;
    private final EnviamentTipus tipus;
    private final EnviamentOrigen origen;

    // Total en estat
    private final Long pendent;
    private final Long regEnviamentError;
    private final Long registrada;
    private final Long regAcceptada;
    private final Long regRebutjada;
    private final Long notEnviamentError;
    private final Long notEnviada;
    private final Long notNotificada;
    private final Long notRebutjada;
    private final Long notExpirada;
    private final Long cieEnviamentError;
    private final Long cieEnviada;
    private final Long cieNotificada;
    private final Long cieRebutjada;
    private final Long cieError;
    private final Long processada;

    // Transicions
    private Long trCreades;
    private Long trRegEnviadesError;
    private Long trRegistrades;
    private Long trSirAcceptades;
    private Long trSirRebutjades;
    private Long trNotEnviadesError;
    private Long trNotEnviades;
    private Long trNotNotificades;
    private Long trNotRebujtades;
    private Long trNotExpirades;
    private Long trNotFallades;
    private Long trCieEnviadesError;
    private Long trCieEnviades;
    private Long trCieNotificades;
    private Long trCieRebutjades;
    private Long trCieCancelades;
    private Long trCieFallades;
    private Long trEmailEnviadesError;
    private Long trEmailEnviades;

    // Temps mig en estat
    private Long temsMigPendent;
    private Long temsMigRegistrada;
    private Long temsMigNotEnviada;
    private Long temsMigCieEnviada;
    private Long temsMigTotal;

    private Long temsMigRegistradaPerSirAcceptada;
    private Long temsMigRegistradaPerSirRebutjada;
    private Long temsMigRegistradaPerNotificada;
    private Long temsMigRegistradaPerEmail;
    private Long temsMigNotEnviadaPerNotificada;
    private Long temsMigNotEnviadaPerRebubjada;
    private Long temsMigNotEnviadaPerExpirada;
    private Long temsMigNotEnviadaPerFallada;
    private Long temsMigCieEnviadaPerNotificada;
    private Long temsMigCieEnviadaPerRebubjada;
    private Long temsMigCieEnviadaPerCancelada;
    private Long temsMigCieEnviadaPerFallada;
    private Long temsMigTotalPerNotAcceptada;
    private Long temsMigTotalPerNotRebutjada;
    private Long temsMigTotalPerNotExpirada;
    private Long temsMigTotalPerNotFallada;
    private Long temsMigTotalPerCieAcceptada;

    // Nombre mig d'intents
    private Long intentsRegistre;
    private Long intentsSir;
    private Long intentsNotEnviament;
    private Long intentsCieEnviament;
    private Long intentsEmailEnviament;

    public ExplotFets(
            Long entitatId,
            Long procedimentId,
            String organCodi,
            String usuariCodi,
            EnviamentTipus tipus,
            EnviamentOrigen origen,
            Long pendent,
            Long regEnviamentError,
            Long registrada,
            Long regAcceptada,
            Long regRebutjada,
            Long notEnviamentError,
            Long notEnviada,
            Long notNotificada,
            Long notRebutjada,
            Long notExpirada,
            Long cieEnviamentError,
            Long cieEnviada,
            Long cieNotificada,
            Long cieRebutjada,
            Long cieError,
            Long processada) {
        this.entitatId = entitatId;
        this.procedimentId = procedimentId;
        this.organCodi = organCodi;
        this.usuariCodi = usuariCodi != null ? usuariCodi : "DESCONEGUT";
        this.tipus = tipus;
        this.origen = origen;
        this.pendent = pendent;
        this.regEnviamentError = regEnviamentError;
        this.registrada = registrada;
        this.regAcceptada = regAcceptada;
        this.regRebutjada = regRebutjada;
        this.notEnviamentError = notEnviamentError;
        this.notEnviada = notEnviada;
        this.notNotificada = notNotificada;
        this.notRebutjada = notRebutjada;
        this.notExpirada = notExpirada;
        this.cieEnviamentError = cieEnviamentError;
        this.cieEnviada = cieEnviada;
        this.cieNotificada = cieNotificada;
        this.cieRebutjada = cieRebutjada;
        this.cieError = cieError;
        this.processada = processada;

        this.trCreades = 0L;
        this.trRegEnviadesError = 0L;
        this.trRegistrades = 0L;
        this.trSirAcceptades = 0L;
        this.trSirRebutjades = 0L;
        this.trNotEnviadesError = 0L;
        this.trNotEnviades = 0L;
        this.trNotNotificades = 0L;
        this.trNotRebujtades = 0L;
        this.trNotExpirades = 0L;
        this.trNotFallades = 0L;
        this.trCieEnviadesError = 0L;
        this.trCieEnviades = 0L;
        this.trCieNotificades = 0L;
        this.trCieRebutjades = 0L;
        this.trCieCancelades = 0L;
        this.trCieFallades = 0L;
        this.trEmailEnviadesError = 0L;
        this.trEmailEnviades = 0L;

    }

}
