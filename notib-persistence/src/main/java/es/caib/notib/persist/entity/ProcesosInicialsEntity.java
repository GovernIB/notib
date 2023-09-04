package es.caib.notib.persist.entity;

import es.caib.notib.logic.intf.dto.ProcessosInicialsEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "codi", nullable = false)
    private ProcessosInicialsEnum codi;

    @Setter
    @Column(name = "init", nullable = false)
    private boolean init;

    public void setId(Long id) {
        this.id = id;
    }

}