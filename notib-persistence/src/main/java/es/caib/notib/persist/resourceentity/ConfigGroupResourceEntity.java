package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ConfigGroupResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "config_group")
@Getter
@Setter
@NoArgsConstructor
public class ConfigGroupResourceEntity extends BaseResourceEntity<ConfigGroupResource> {

    @Column(name = "CODE", length = 128, nullable = false)
    private String key;

    @Column(name = "description", length = 512, nullable = true)
    private String description;

    @Column(name = "position")
    private int position;
//
//    @Column(name = "parentCode")
//    private String parentCode;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code")
    @OrderBy("position ASC")
    private Set<ConfigResourceEntity> configs;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    @OrderBy("position ASC")
    private Set<ConfigGroupResourceEntity> innerConfigs;
}
