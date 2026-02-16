package es.caib.notib.persist.entity.resourceentity;

import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Table(name = "NOT_NOTIFICACIO_ENV_TABLE")
@Getter
@Setter
@NoArgsConstructor
public class EnviamentResourceEntity extends BaseAuditableEntity<NotificacioEnviamentResource, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    @Column(name = "entrega_postal")
    private Boolean entregaPostalActiva;
}
