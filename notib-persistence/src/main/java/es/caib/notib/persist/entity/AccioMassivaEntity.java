package es.caib.notib.persist.entity;


import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="not_accio_massiva")
@EntityListeners(AuditingEntityListener.class)
public class AccioMassivaEntity  extends NotibAuditable<Long> {

    @Column(name = "tipus")
    @Enumerated(EnumType.STRING)
    private AccioMassivaTipus tipus;
    @Column(name = "data_inici")
    @Temporal(TemporalType.DATE)
    private Date dataInici;
    @Column(name = "data_fi")
    @Temporal(TemporalType.DATE)
    private Date dataFi;

}
