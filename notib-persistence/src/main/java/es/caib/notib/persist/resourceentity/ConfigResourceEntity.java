package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ConfigResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "config")
@Getter
@Setter
@NoArgsConstructor
public class ConfigResourceEntity extends BaseResourceEntity<ConfigResource> {

	@Column(name = "key", length = 256, nullable = false)
	private String key;
	@Column(name = "value", length = 2048, nullable = true)
	private String value;
	@Column(name = "description", length = 2048, nullable = true)
	private String description;
	@Column(name = "jboss_property", nullable = false)
	private boolean jbossProperty;
	@Column(name = "configurable", nullable = false)
	private boolean configurable;
	@Column(name = "position", nullable = false)
	private int position;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "config_group_id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "CONFIG_GROUP_ID_FK"))
	private ConfigGroupResourceEntity configGroup;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "config_type_id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "CONFIG_TYPE_ID_FK"))
	private ConfigTypeResourceEntity configType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat_id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "CONFIG_ENTITAT_FK"))
	private EntitatResourceEntity entitat;

	@Builder
	public ConfigResourceEntity(
		ConfigResource resource,
		ConfigGroupResourceEntity configGroup,
		ConfigTypeResourceEntity configType,
		EntitatResourceEntity entitat) {
		this.key = resource.getKey();
		this.value = resource.getValue();
		this.description = resource.getDescription();
		this.jbossProperty = resource.isJbossProperty();
		this.configurable = resource.isConfigurable();
		this.position = resource.getPosition();
		this.configGroup = configGroup;
		this.configType = configType;
		this.entitat = entitat;
	}

}
