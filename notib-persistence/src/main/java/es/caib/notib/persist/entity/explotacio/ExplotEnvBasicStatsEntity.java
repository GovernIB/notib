package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable // Evita persistència/updates
@Table(name = "NOT_STATS_VIEW")
public class ExplotEnvBasicStatsEntity {

    @Id
    Long id;
    @Column(name = "entitat_id")
    private Long entitatId;
    @Column(name = "procediment_id")
    private Long procedimentId;
    @Column(name = "organ_codi")
    private String organCodi;
    @Column(name = "usuari_codi")
    private String usuariCodi;
    @Column(name = "env_tipus")
    @Enumerated(EnumType.ORDINAL)
    private EnviamentTipus enviamentTipus;
    @Column(name = "origen")
    @Enumerated(EnumType.STRING)
    private EnviamentOrigen origen;
    @Column(name = "tipus")
    private String tipus;
    @Column(name = "dia")
    private LocalDate dia;
    @Column(name = "total")
    private long totalEnviaments;     // Total d'enviaments per la data

    public ExplotFetsKey getKey() {
        return new ExplotFetsKey(entitatId, procedimentId, organCodi, usuariCodi, enviamentTipus, origen);
    }

}

