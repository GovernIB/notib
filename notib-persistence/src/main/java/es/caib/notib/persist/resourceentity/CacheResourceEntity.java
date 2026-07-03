package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.CacheResource;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CacheResourceEntity implements ResourceEntity<CacheResource, String> {

	private String id;
	private String codi;
	private String descripcio;
	private long localHeapSize;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return id == null;
	}
}
