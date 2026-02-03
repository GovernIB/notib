package es.caib.notib.persist.resourceentity;


import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "pagador_postal")
@Getter
@Setter
@NoArgsConstructor
public class PagadorPostalResourceEntity extends BaseAuditableResourceEntity<PagadorPostalResource> {
}
