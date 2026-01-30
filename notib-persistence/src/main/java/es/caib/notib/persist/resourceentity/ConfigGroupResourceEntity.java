package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ConfigGroupResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Entitat de base de dades pels recursos de tipus grup de propietats de configuració.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "config_group")
@Getter
@Setter
@NoArgsConstructor
public class ConfigGroupResourceEntity extends BaseResourceEntity<ConfigGroupResource> {

	@Column(name = "code", length = 128, nullable = false)
	private String key;
	@Column(name = "position")
	private int position;
	@Column(name = "description", length = 512, nullable = true)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "parent_id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "config_group_parent_fk"))
	private ConfigGroupResourceEntity parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<ConfigGroupResourceEntity> children;

	@OneToMany(mappedBy = "configGroup", cascade = CascadeType.ALL)
	private List<ConfigResourceEntity> configs;

}

