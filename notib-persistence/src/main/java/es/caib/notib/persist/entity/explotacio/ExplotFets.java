package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ExplotFets {

    private final Long entitatId;
    private final Long procedimentId;
    private final String organCodi;
    @Builder.Default
    private final String usuariCodi = "DESCONEGUT";
    private final EnviamentTipus tipus;
    private final EnviamentOrigen origen;

    // Total en estat
    private final long pendent;
    private final long regEnviamentError;
    private final long registrada;
    private final long regAcceptada;
    private final long regRebutjada;
    private final long notEnviamentError;
    private final long notEnviada;
    private final long notNotificada;
    private final long notRebutjada;
    private final long notExpirada;
    private final long cieEnviamentError;
    private final long cieEnviada;
    private final long cieNotificada;
    private final long cieRebutjada;
    private final long cieError;
    private final long processada;

    // Transicions
    private final Long trCreades;
    private final Long trRegEnviadesError;
    private final Long trRegistrades;
    private final Long trSirAcceptades;
    private final Long trSirRebutjades;
    private final Long trNotEnviadesError;
    private final Long trNotEnviades;
    private final Long trNotNotificades;
    private final Long trNotRebujtades;
    private final Long trNotExpirades;
    private final Long trNotFallades;
    private final Long trCieEnviadesError;
    private final Long trCieEnviades;
    private final Long trCieNotificades;
    private final Long trCieRebutjades;
    private final Long trCieCancelades;
    private final Long trCieFallades;
    private final Long trEmailEnviadesError;
    private final Long trEmailEnviades;

    // Temps mig en estat
    private final Long temsMigPendent;
    private final Long temsMigRegistrada;
    private final Long temsMigNotEnviada;
    private final Long temsMigCieEnviada;
    private final Long temsMigTotal;

    // Nombre mig d'intents
    private final Long intentsRegistre;
    private final Long intentsSir;
    private final Long intentsNotEnviament;
    private final Long intentsCieEnviament;
    private final Long intentsEmailEnviament;

    public ExplotFets(
            Long entitatId,
            Long procedimentId,
            String organCodi,
            String usuariCodi,
            EnviamentTipus tipus,
            EnviamentOrigen origen,
            long pendent,
            long regEnviamentError,
            long registrada,
            long regAcceptada,
            long regRebutjada,
            long notEnviamentError,
            long notEnviada,
            long notNotificada,
            long notRebutjada,
            long notExpirada,
            long cieEnviamentError,
            long cieEnviada,
            long cieNotificada,
            long cieRebutjada,
            long cieError,
            long processada) {
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

        this.trCreades = null;
        this.trRegEnviadesError = null;
        this.trRegistrades = null;
        this.trSirAcceptades = null;
        this.trSirRebutjades = null;
        this.trNotEnviadesError = null;
        this.trNotEnviades = null;
        this.trNotNotificades = null;
        this.trNotRebujtades = null;
        this.trNotExpirades = null;
        this.trNotFallades = null;
        this.trCieEnviadesError = null;
        this.trCieEnviades = null;
        this.trCieNotificades = null;
        this.trCieRebutjades = null;
        this.trCieCancelades = null;
        this.trCieFallades = null;
        this.trEmailEnviadesError = null;
        this.trEmailEnviades = null;
        this.temsMigPendent = null;
        this.temsMigRegistrada = null;
        this.temsMigNotEnviada = null;
        this.temsMigCieEnviada = null;
        this.temsMigTotal = null;
        this.intentsRegistre = null;
        this.intentsSir = null;
        this.intentsNotEnviament = null;
        this.intentsCieEnviament = null;
        this.intentsEmailEnviament = null;
    }

}
