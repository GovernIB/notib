package es.caib.notib.persist.entity;


import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="not_accio_massiva")
@EntityListeners(AuditingEntityListener.class)
public class AccioMassivaEntity  extends NotibAuditable<Long> {

    private static int ERROR_DESC_MAX_LENGTH = 1024;
    private static int STACKTRACE_MAX_LENGTH = 2048;

    @Column(name = "tipus", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccioMassivaTipus tipus;
    @Column(name = "data_inici")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInici;
    @Column(name = "data_fi")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFi;
    @Column(name = "entitat_id")
    private Long entitatId;
    @Column(name = "error")
    private Boolean error;
    @Column(name = "error_descripcio", length = 1024)
    private String errorDescripcio;
    @Column(name = "excepcio_stacktrace", length = 2048)
    private String excepcioStacktrace;

    public void setErrorDescripcio(String errorDescripcio) {
        this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_MAX_LENGTH);
    }

    public void setExcepcioStacktrace(String excepcioStacktrace) {
        this.excepcioStacktrace = StringUtils.abbreviate(excepcioStacktrace, STACKTRACE_MAX_LENGTH);
    }


}
