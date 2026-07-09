package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.ActiveMqResource;
import es.caib.notib.logic.intf.model.MetriquesResource;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActiveMqResourceEntity implements ResourceEntity<ActiveMqResource, String> {

	private String id;
	private String nom;
	private String descripcio;
	private long mida;
	private long consumersCount;
	private long enqueueCount;
	private long dequeueCount;
	private long forwardCount;
	private long inFlightCount;
	private long expiredCount;
	private long storeMessageSize;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return id == null;
	}
}
