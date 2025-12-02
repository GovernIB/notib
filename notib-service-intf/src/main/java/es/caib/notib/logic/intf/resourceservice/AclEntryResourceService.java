package es.caib.notib.logic.intf.resourceservice;

import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.service.MutableResourceService;
import es.caib.notib.logic.intf.model.AclEntryResource;
import es.caib.notib.logic.intf.model.AclEntryResourceType;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface AclEntryResourceService extends MutableResourceService<AclEntryResource, String> {

	boolean anyPermissionGranted(
			AclEntryResourceType resourceType,
			Serializable resourceId,
			List<PermissionEnum> permissions);

	Set<Serializable> findIdsWithAnyPermission(
			AclEntryResourceType resourceType,
			List<PermissionEnum> permissions);

}
