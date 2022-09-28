package es.caib.notib.persist.audit;

import java.io.Serializable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.Getter;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@MappedSuperclass
public class NotibAuditoria<PK extends Serializable> extends NotibAuditable<PK> {

	private static final long serialVersionUID = 5025928932008668460L;

	@Getter
	@Enumerated(EnumType.STRING)
	protected TipusOperacio tipusOperacio;
	@Getter
	protected String joinPoint;

}
