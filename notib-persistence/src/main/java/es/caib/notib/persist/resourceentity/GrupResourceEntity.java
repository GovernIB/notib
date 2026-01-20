package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.model.GrupResource;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "grup")
@Getter
@Setter
@NoArgsConstructor
public class GrupResourceEntity extends BaseAuditableResourceEntity<GrupResource> {

    @EqualsAndHashCode.Include
    @Column(name = "codi", length = 64, nullable = false)
    private String codi;

    @Column(name = "nom", length = 100)
    private String nom;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entitat",
            referencedColumnName = "id",
            foreignKey = @javax.persistence.ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_grup_fk"),
            nullable = false)
    protected EntitatResourceEntity entitat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organ_gestor",
            referencedColumnName = "id",
            foreignKey = @javax.persistence.ForeignKey(name = BaseConfig.DB_PREFIX + "grup_organ_fk"),
            nullable = false)
    protected OrganGestorResourceEntity organGestor;


    @Builder
    public GrupResourceEntity(GrupResource resource, EntitatResourceEntity entitat) {

        this.codi = resource.getCodi();
        this.nom = resource.getNom();
        this.entitat = entitat;
    }
}
