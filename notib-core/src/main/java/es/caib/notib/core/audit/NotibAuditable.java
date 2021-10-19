package es.caib.notib.core.audit;

import es.caib.notib.core.entity.UsuariEntity;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.io.Serializable;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@EntityListeners(NotibAuditingEntityListener.class)
public class NotibAuditable<PK extends Serializable> extends AbstractAuditable<UsuariEntity, PK> {

	private static final long serialVersionUID = 5373083799666869320L;

}
