package es.caib.notib.persist.entity;

import es.caib.notib.persist.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name="not_columnes")
@EntityListeners(AuditingEntityListener.class)
public class ColumnesRemesesEntity extends NotibAuditable<Long> {


    @Column(name="data_creacio_remesa")
    private boolean dataCreacio;
    @Column(name="data_enviament_remesa")
    private boolean dataEnviament;
    @Column(name="num_registre_remesa")
    private boolean numRegistre;
    @Column(name="organ_emisor_remesa")
    private boolean organEmisor;
    @Column(name="proc_ser_codi_remesa")
    private boolean procSerCodi;
    @Column(name="num_expedient_remesa")
    private boolean numExpedient;
    @Column(name="concepte_remesa")
    private boolean concepte;
    @Column(name="creada_per_remesa")
    private boolean creadaPer;
    @Column(name="interessats_remesa")
    private boolean interessats;
    @Column(name="estat_remesa")
    private boolean estat;

}
