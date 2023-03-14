package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name="not_callback")
@EntityListeners(AuditingEntityListener.class)
public class CallbackEntity extends NotibAuditable<Long> {

    private static final int ERROR_DESC_MAX_LENGTH = 2048;

    @Column(name = "notificacio_id", nullable = false)
    private Long notificacioId;
    @Column(name = "enviament_id", nullable = false)
    private Long enviamentId;
    @Column(name = "data", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @Column(name = "error", nullable = false)
    private boolean error;
    @Column(name = "error_desc", length = ERROR_DESC_MAX_LENGTH)
    private String errorDesc;
    @Column(name = "estat", length = 10, nullable = true)
    @Enumerated(EnumType.STRING)
    private CallbackEstatEnumDto estat;
    @Column(name = "intents")
    private int intents;

    public CallbackEntity() {
        data = new Date();
        error = false;
    }
}
