package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.MetriquesResource;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetriquesResourceEntity implements ResourceEntity<MetriquesResource, String> {

	private String id;
	private String metriques;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return id == null;
	}
}
