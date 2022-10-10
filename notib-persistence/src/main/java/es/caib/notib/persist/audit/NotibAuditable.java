package es.caib.notib.persist.audit;


import es.caib.notib.persist.entity.UsuariEntity;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.io.Serializable;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@EntityListeners(NotibAuditingEntityListener.class)
public class NotibAuditable<PK extends Serializable> extends AbstractAuditable<UsuariEntity, PK> implements Serializable {

	private static final long serialVersionUID = 5373083799666869320L;

}
