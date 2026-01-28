package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ConfigGroupResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "parent_id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "CONFIG_GROUP_PARENT_FK"))
    private ConfigGroupResourceEntity parent;

    @Column(name = "position")
    private int position;
    @Column(name = "description", length = 512, nullable = true)
    private String description;

}

