package es.caib.notib.persist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "not_oficina")
@EntityListeners(AuditingEntityListener.class)
public class OficinaEntity implements Serializable {

    @Id
    @Column(name = "codi", length = 64, nullable = false)
    private String codi;

    @Column(name = "nom", length = 1024, nullable = false)
    private String nom;

    @Column(name = "sir")
    private boolean sir;

    @Column(name = "actiu")
    private boolean actiu;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "entitat_id")
    @ForeignKey(name = "not_entitat_fk")
    private EntitatEntity entitat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_codi", referencedColumnName = "codi")
    @ForeignKey(name = "not_proc_organ_fk")
    private OrganGestorEntity organGestor;

}
