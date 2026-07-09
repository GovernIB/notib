package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.ActiveMqDetailResource;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ActiveMqDetailResourceEntity implements ResourceEntity<ActiveMqDetailResource, String> {

	private String id;
	private String uuid;
	private String notificacioUuId;
	private Date data;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return id == null;
	}
}
