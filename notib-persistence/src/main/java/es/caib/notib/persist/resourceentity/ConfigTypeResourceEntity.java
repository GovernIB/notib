package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ConfigTypeResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "config_type")
@Getter
@Setter
@NoArgsConstructor
public class ConfigTypeResourceEntity extends BaseAuditableResourceEntity<ConfigTypeResource> {

    @Column(name = "code", length = 128, nullable = false)
    private String code;

    @Column(name = "value", length = 2048)
    private String value;
}
