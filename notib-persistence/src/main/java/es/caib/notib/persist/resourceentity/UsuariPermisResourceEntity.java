package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.UsuariPermisResource;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuariPermisResourceEntity implements ResourceEntity<UsuariPermisResource, String>  {

	private String id;
	private String codi;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return id == null;
	}
}
