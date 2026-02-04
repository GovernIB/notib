package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "pagador_cie")
@Getter
@Setter
@NoArgsConstructor
public class PagadorCieResourceEntity extends BaseAuditableResourceEntity<PagadorCieResource> {
}
