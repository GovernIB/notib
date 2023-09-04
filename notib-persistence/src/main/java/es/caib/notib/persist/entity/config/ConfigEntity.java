package es.caib.notib.persist.entity.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(	name = "NOT_CONFIG")
@NoArgsConstructor
public class ConfigEntity {
    @Id
    @Column(name = "KEY", length = 256, nullable = false)
    private String key;

    @Column(name = "VALUE", length = 2048, nullable = true)
    private String value;

    @Column(name = "DESCRIPTION", length = 2048, nullable = true)
    private String description;

    @Column(name = "JBOSS_PROPERTY", nullable = false)
    private boolean jbossProperty;

    @Column(name = "GROUP_CODE", length = 2048, nullable = true)
    private String groupCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "TYPE_CODE", insertable = false, updatable = false)
    @JoinColumn(name = "TYPE_CODE", updatable = false)
    @ForeignKey(name = "NOT_CONFIG_TYPE_FK")
    private ConfigTypeEntity type;

    @Column(name = "entitat_codi", length = 64)
    private String entitatCodi;

    @Column(name = "CONFIGURABLE")
    private boolean configurable;

    @Column(name = "POSITION")
    private int position;
//
//    @ManyToOne
//    private UsuariEntity lastModifiedBy;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;

    public ConfigEntity(String key, String value) {

        this.key = key;
        this.value = value;
    }
    /**
     * Per a mapejar el Dto de la vista.
     *
     * @return El llistat de possibles valors que pot prendre la propietat
     */
    public List<String> getValidValues() {
       return type == null ? Collections.<String>emptyList() : type.getValidValues();
    }
    public String getTypeCode() {
        return type == null ? "" : type.getCode();
    }

    public void update(String value) {
        this.value = value;
    }

    public void crearConfigNova(String key, String entitatCodi, ConfigEntity entitat) {

        this.key = key;
        this.entitatCodi = entitatCodi;
        this.value = null;
        this.description = entitat.getDescription();
        this.jbossProperty = entitat.isJbossProperty();
        this.groupCode = entitat.getGroupCode();
        this.type = entitat.getType();
        this.configurable = entitat.isConfigurable();
    }
}