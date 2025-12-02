package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.AclEntryResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.Id;

/**
 * Mapping lleuger per mantenir un identificador estable d'API per a AclEntry.
 * Les dades reals d'autorització es desen en les taules Spring ACL (com_acl_*).
 */
@Getter
@Setter
@NoArgsConstructor
public class AclEntryResourceEntity implements es.caib.notib.persist.base.entity.ResourceEntity<AclEntryResource, String> {

	@Id
	private @Nullable String id;
	private AclEntryResource resource;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return null == getId();
	}

	public Boolean getGrantedAuthority() {
		if (getResource() != null) {
			return getResource().isGrantedAuthority();
		} else {
			return null;
		}
	}
	public String getSidName() {
		if (getResource() != null) {
			return getResource().getSidName();
		} else {
			return null;
		}
	}

	@Builder
	public AclEntryResourceEntity(
			String id,
			AclEntryResource resource) {
		this.id = id;
		this.resource = resource;
	}

}
