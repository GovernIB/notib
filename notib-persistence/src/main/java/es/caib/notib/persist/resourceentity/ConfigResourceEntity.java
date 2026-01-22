package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ConfigResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "config")
@Getter
@Setter
@NoArgsConstructor
public class ConfigResourceEntity extends BaseAuditableResourceEntity<ConfigResource> {

    @Column(name = "key", length = 256, nullable = false)
    private String key;

    @Column(name = "value", length = 2048, nullable = true)
    private String value;

    @Column(name = "description", length = 2048, nullable = true)
    private String description;

    @Column(name = "jbossProperty", nullable = false)
    private boolean jbossProperty;

    @Column(name = "groupCode", length = 2048, nullable = true)
    private String groupCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "TYPE_CODE", insertable = false, updatable = false)
    @JoinColumn(name = "type_code", updatable = false)
    @ForeignKey(name = "NOT_CONFIG_TYPE_FK")
    private ConfigTypeResourceEntity type;

    @Column(name = "entitat_codi", length = 64)
    private String entitatCodi;

    @Column(name = "configurable")
    private boolean configurable;

    @Column(name = "position")
    private int position;
}
