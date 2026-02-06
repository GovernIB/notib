package es.caib.notib.persist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "not_sincronizar_envio")
public class SincronizarEnvioEntity extends AbstractPersistable<Long> implements Serializable {

    @Column(name = "identificador")
    private String identificador;

    @Lob
    @Column(name = "json_contingut")
    private String jsonContingut;

    @Column(name = "data_creacio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCreacio;

}
