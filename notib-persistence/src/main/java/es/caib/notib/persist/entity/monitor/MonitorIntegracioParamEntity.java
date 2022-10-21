package es.caib.notib.persist.entity.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="not_mon_int_param")
@EntityListeners(AuditingEntityListener.class)
public class MonitorIntegracioParamEntity extends AbstractPersistable<Long> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mon_int_id")
    @ForeignKey(name = "NOT_MONINTPARAM_MONINT_FK")
    private MonitorIntegracioEntity monitorIntegracio;

    @Column(name = "codi", length = 256, nullable = false)
    private String codi;

    @Column(name = "valor", length = 1024)
    private String valor;

}
