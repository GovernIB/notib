package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExplotFets {

    private final Long entitatId;
    private final Long procedimentId;
    private final String organCodi;
    @Builder.Default
    private final String usuariCodi = "DESCONEGUT";
    private final EnviamentTipus tipus;
    private final EnviamentOrigen origen;
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

    public ExplotFets(Long entitatId, Long procedimentId, String organCodi, String usuariCodi, EnviamentTipus tipus, EnviamentOrigen origen, long pendent, long regEnviamentError, long registrada, long regAcceptada, long regRebutjada, long notEnviamentError, long notEnviada, long notNotificada, long notRebutjada, long notExpirada, long cieEnviamentError, long cieEnviada, long cieNotificada, long cieRebutjada, long cieError, long processada) {
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
    }

//    public ExplotFets minus(ExplotFets fetAnterior) {
//        if (!this.entitatId.equals(fetAnterior.entitatId)) {
//            throw new RuntimeException("Error al obtenir les dades estadístiques. Entitat incorrecte");
//        }
//        if (!this.procedimentId.equals(fetAnterior.procedimentId)) {
//            throw new RuntimeException("Error al obtenir les dades estadístiques. Procediment incorrecte");
//        }
//        if (!this.organCodi.equals(fetAnterior.organCodi)) {
//            throw new RuntimeException("Error al obtenir les dades estadístiques. Organ incorrecte");
//        }
//        if (!this.tipus.equals(fetAnterior.tipus)) {
//            throw new RuntimeException("Error al obtenir les dades estadístiques. Tipus incorrecte");
//        }
//        if (!this.origen.equals(fetAnterior.origen)) {
//            throw new RuntimeException("Error al obtenir les dades estadístiques. Origen incorrecte");
//        }
//        return ExplotFets.builder()
//                .entitatId(this.entitatId)
//                .procedimentId(this.procedimentId)
//                .organCodi(this.organCodi)
////                .usuariCodi(this.usuariCodi)
//                .tipus(this.tipus)
//                .origen(this.origen)
//                .pendent(this.pendent - fetAnterior.getPendent())
//                .regEnviamentError(this.regEnviamentError - fetAnterior.getRegEnviamentError())
//                .registrada(this.registrada - fetAnterior.getRegistrada())
//                .regAcceptada(this.regAcceptada - fetAnterior.getRegAcceptada())
//                .regRebutjada(this.regRebutjada - fetAnterior.getRegRebutjada())
//                .notEnviamentError(this.notEnviamentError - fetAnterior.getNotEnviamentError())
//                .notEnviada(this.notEnviada - fetAnterior.getNotEnviada())
//                .notNotificada(this.notNotificada - fetAnterior.getNotNotificada())
//                .notRebutjada(this.notRebutjada - fetAnterior.getNotRebutjada())
//                .notExpirada(this.notExpirada - fetAnterior.getNotExpirada())
//                .cieEnviamentError(this.cieEnviamentError - fetAnterior.getCieEnviamentError())
//                .cieEnviada(this.cieEnviada - fetAnterior.getCieEnviada())
//                .cieNotificada(this.cieNotificada - fetAnterior.getCieNotificada())
//                .cieRebutjada(this.cieRebutjada - fetAnterior.getCieRebutjada())
//                .cieError(this.cieError - fetAnterior.getCieError())
//                .processada(this.processada - fetAnterior.getProcessada())
//                .build();
//    }
}
