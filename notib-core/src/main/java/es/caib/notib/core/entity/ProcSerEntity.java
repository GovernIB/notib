package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.ProcSerTipusEnum;
import es.caib.notib.core.audit.NotibAuditable;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "not_procediment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tipus", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(AuditingEntityListener.class)
public abstract class ProcSerEntity extends NotibAuditable<Long> {

    @Column(name = "tipus", length = 32, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ProcSerTipusEnum tipus;

    @Column(name = "codi", length = 64, nullable = false, unique = true)
    protected String codi;

    @Column(name = "nom", length = 256, nullable = false)
    protected String nom;

    @Column(name = "retard")
    protected Integer retard;

    @Column(name = "caducitat")
    protected Integer caducitat;

    @Column(name = "tipusassumpte", length = 255)
    protected String tipusAssumpte;

    @Column(name = "tipusassumpte_nom", length = 255)
    protected String tipusAssumpteNom;

    @Column(name = "codiassumpte", length = 255)
    protected String codiAssumpte;

    @Column(name = "codiassumpte_nom", length = 255)
    protected String codiAssumpteNom;

    @Column(name = "agrupar")
    protected boolean agrupar;

    @Column(name = "comu")
    protected boolean comu;

    @Column(name = "DIRECT_PERMISSION_REQUIRED")
    protected boolean requireDirectPermission;

    @Column(name = "actiu")
    protected boolean actiu;

    @Column(name = "ultima_act")
    @Temporal(TemporalType.DATE)
    protected Date ultimaActualitzacio;

    @Setter
    @Column(name = "organ_no_sinc", nullable = false)
    private boolean organNoSincronitzat;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "entitat")
    @ForeignKey(name = "not_entitat_fk")
    protected EntitatEntity entitat;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTREGA_CIE_ID")
    @ForeignKey(name = "NOT_PROCEDIMENT_ENTREGA_CIE_FK")
    private EntregaCieEntity entregaCie;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_gestor", referencedColumnName = "codi")
    @ForeignKey(name = "not_proc_organ_fk")
    protected OrganGestorEntity organGestor;

    public boolean isEntregaCieVigent() {

        return false;
    }

    public boolean isEntregaCieActivaAlgunNivell() {
        if (entregaCie != null) {
            return true;
        }

        if (organGestor != null && organGestor.getEntregaCie() != null) {
            return true;
        }

        if (entitat != null && entitat.getEntregaCie() != null) {
            return true;
        }

        return false;
    }

    public EntregaCieEntity getEntregaCieEfectiva() {
        if (entregaCie != null) {
            return entregaCie;
        }

        if (organGestor != null && organGestor.getEntregaCie() != null) {
            return organGestor.getEntregaCie();
        }

        if (entitat != null && entitat.getEntregaCie() != null) {
            return entitat.getEntregaCie();
        }

        return null;
    }

    public void update(
            String codi,
            String nom,
            EntitatEntity entitat,
            EntregaCieEntity entregaCie,
            int retard,
            int caducitat,
            boolean agrupar,
            OrganGestorEntity organGestor,
            String tipusAssumpte,
            String tipusAssumpteNom,
            String codiAssumpte,
            String codiAssumpteNom,
            boolean comu,
            boolean requireDirectPermission) {
        this.codi = codi;
        this.nom = nom;
        this.entitat = entitat;
        this.entregaCie = entregaCie;
        this.agrupar = agrupar;
        this.organGestor = organGestor;
        this.retard = retard;
        this.caducitat = caducitat;
        this.tipusAssumpte = tipusAssumpte;
        this.tipusAssumpteNom = tipusAssumpteNom;
        this.codiAssumpte = codiAssumpte;
        this.codiAssumpteNom = codiAssumpteNom;
        this.comu=comu;
        this.requireDirectPermission = requireDirectPermission;
    }

    public void update(
            String nom,
            OrganGestorEntity organGestor,
            boolean comu) {
        this.nom = nom;
        this.organGestor = organGestor;
        this.comu= comu;
    }

    public void updateDataActualitzacio(Date dataActualitzacio) {
        this.ultimaActualitzacio = dataActualitzacio;
    }

    public void updateActiu(
            boolean actiu) {
        this.actiu = actiu;
    }

    protected ProcSerEntity(ProcSerEntityBuilder<?, ?> b) {
        this.setId(b.id);
        this.setCreatedBy(b.createdBy);
        this.setCreatedDate(b.createdDate);
        this.setLastModifiedBy(b.lastModifiedBy);
        this.setLastModifiedDate(b.lastModifiedDate);
        this.codi = b.codi;
        this.nom = b.nom;
        this.retard = b.retard;
        this.caducitat = b.caducitat;
        this.tipusAssumpte = b.tipusAssumpte;
        this.tipusAssumpteNom = b.tipusAssumpteNom;
        this.codiAssumpte = b.codiAssumpte;
        this.codiAssumpteNom = b.codiAssumpteNom;
        this.agrupar = b.agrupar;
        this.comu = b.comu;
        this.requireDirectPermission = b.requireDirectPermission;
        this.ultimaActualitzacio = b.ultimaActualitzacio;
        this.entitat = b.entitat;
        this.entregaCie = b.entregaCie;
        this.organGestor = b.organGestor;
        this.actiu = true;
    }

    public static abstract class ProcSerEntityBuilder<C extends ProcSerEntity, B extends ProcSerEntityBuilder<C, B>> {
        private String codi;
        private String nom;
        private Integer retard;
        private Integer caducitat;
        private String tipusAssumpte;
        private String tipusAssumpteNom;
        private String codiAssumpte;
        private String codiAssumpteNom;
        private boolean agrupar;
        private boolean comu;
        private boolean requireDirectPermission;
        private boolean actiu;
        private Date ultimaActualitzacio;
        private EntitatEntity entitat;
        private EntregaCieEntity entregaCie;
        private OrganGestorEntity organGestor;

        private UsuariEntity createdBy;
        private DateTime createdDate;
        private UsuariEntity lastModifiedBy;
        private DateTime lastModifiedDate;
        private Long id;

        public B id(Long id) {
            this.id = id;
            return self();
        }

        public B createdBy(UsuariEntity createdBy) {
            this.createdBy = createdBy;
            return self();
        }

        public B createdDate(DateTime createdDate) {
            this.createdDate = createdDate;
            return self();
        }

        public B lastModifiedBy(UsuariEntity lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return self();
        }

        public B lastModifiedDate(DateTime lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return self();
        }

        public B codi(String codi) {
            this.codi = codi;
            return self();
        }

        public B nom(String nom) {
            this.nom = nom;
            return self();
        }

        public B retard(Integer retard) {
            this.retard = retard;
            return self();
        }

        public B caducitat(Integer caducitat) {
            this.caducitat = caducitat;
            return self();
        }

        public B tipusAssumpte(String tipusAssumpte) {
            this.tipusAssumpte = tipusAssumpte;
            return self();
        }

        public B tipusAssumpteNom(String tipusAssumpteNom) {
            this.tipusAssumpteNom = tipusAssumpteNom;
            return self();
        }

        public B codiAssumpte(String codiAssumpte) {
            this.codiAssumpte = codiAssumpte;
            return self();
        }

        public B codiAssumpteNom(String codiAssumpteNom) {
            this.codiAssumpteNom = codiAssumpteNom;
            return self();
        }

        public B agrupar(boolean agrupar) {
            this.agrupar = agrupar;
            return self();
        }

        public B comu(boolean comu) {
            this.comu = comu;
            return self();
        }

        public B requireDirectPermission(boolean requireDirectPermission) {
            this.requireDirectPermission = requireDirectPermission;
            return self();
        }

        public B actiu(boolean actiu) {
            this.actiu = actiu;
            return self();
        }

        public B ultimaActualitzacio(Date ultimaActualitzacio) {
            this.ultimaActualitzacio = ultimaActualitzacio;
            return self();
        }

        public B entitat(EntitatEntity entitat) {
            this.entitat = entitat;
            return self();
        }

        public B entregaCie(EntregaCieEntity entregaCie) {
            this.entregaCie = entregaCie;
            return self();
        }

        public B organGestor(OrganGestorEntity organGestor) {
            this.organGestor = organGestor;
            return self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "ProcSerEntity.ProcSerEntityBuilder(super=" + super.toString() + ", codi=" + this.codi + ", nom=" + this.nom + ", retard=" + this.retard + ", caducitat=" + this.caducitat + ", tipusAssumpte=" + this.tipusAssumpte + ", tipusAssumpteNom=" + this.tipusAssumpteNom + ", codiAssumpte=" + this.codiAssumpte + ", codiAssumpteNom=" + this.codiAssumpteNom + ", agrupar=" + this.agrupar + ", comu=" + this.comu + ", requireDirectPermission=" + this.requireDirectPermission + ", ultimaActualitzacio=" + this.ultimaActualitzacio + ", entitat=" + this.entitat + ", entregaCie=" + this.entregaCie + ", organGestor=" + this.organGestor + ")";
        }
    }
}
