package es.caib.notib.core.entity.cie;

import es.caib.notib.core.audit.NotibAuditable;
import lombok.*;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe de model de dades que conté la informació dels pagadors CIE.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "NOT_ENTREGA_CIE")
@EntityListeners(AuditingEntityListener.class)
public class EntregaCieEntity extends NotibAuditable<Long> {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CIE_ID", insertable = false, updatable = false)
    @ForeignKey(name = "NOT_ENTREGA_CIE_CIE_FK")
    private PagadorCieEntity cie;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_POSTAL_ID", insertable = false, updatable = false)
    @ForeignKey(name = "NOT_ENTREGA_CIE_OPERADOR_FK")
    private PagadorPostalEntity operadorPostal;

    @Column(name = "CIE_ID")
    private Long cieId;
    @Column(name = "OPERADOR_POSTAL_ID")
    private Long operadorPostalId;

    public EntregaCieEntity(Long cieId, Long operadorPostalId) {
        this.cieId = cieId;
        this.operadorPostalId = operadorPostalId;
    }

    public EntregaCieEntity() {

    }
    public void update(Long cieId, Long operadorPostalId) {
        this.cieId = cieId;
        this.operadorPostalId = operadorPostalId;
    }
}
