package es.caib.notib.persist.acl;

import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;


public interface NotibMutableAclService extends MutableAclService {

	public void deleteEntries(ObjectIdentity oid, Sid sid);
	
}
