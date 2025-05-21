package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExplotEnvStats {

    private ExplotFetsKey key;

    private long totalEnviaments;     // Total d'enviaments per la data
    private Double tempsMigEstat;     // Temps mig en estat (pot ser `null`)
    private Double intentsMig;        // Intents mig (pot ser `null`)
    private Double tempsTotal;        // Temps total (pot ser `null`)


    public ExplotEnvStats(
            Long entitatId,
            Long procedimentId,
            String organGestorCodi,
            String usuariCodi,
            EnviamentTipus enviamentTipus,
            EnviamentOrigen origen,
            long totalEnviaments,
            Double tempsMigEstat,
            Double intentsMig,
            Double tempsTotal) {
        this.key = new ExplotFetsKey(entitatId, procedimentId, organGestorCodi, usuariCodi, enviamentTipus, origen);
        this.totalEnviaments = totalEnviaments;
        this.tempsMigEstat = tempsMigEstat;
        this.intentsMig = intentsMig;
        this.tempsTotal = tempsTotal;
    }
}

