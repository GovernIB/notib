package es.caib.notib.core.entity;

import es.caib.notib.core.api.ws.notificacio.ProcessosInicialsEnum;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="not_processos_inicials")
public class ProcesosInicialsEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "codi", nullable = false)
    private ProcessosInicialsEnum codi;

    @Setter
    @Column(name = "init", nullable = false)
    private boolean init;

    public void setId(Long id) {
        this.id = id;
    }

}