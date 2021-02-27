package es.caib.notib.core.entity.acl;

import lombok.Getter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

/**
 * Classe del model de dades que representa un SID d'una ACL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_acl_entry")
public class AclEntryEntity extends AbstractPersistable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "acl_object_identity", nullable = false)
	private AclObjectIdentityEntity aclObjectIdentity;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "sid", nullable = false)
	private AclSidEntity sid;
	@Column(name = "mask", nullable = false)
	private Integer mask;
	@Column(name = "granting", nullable = false)
	private Boolean granting;
	
	private static final long serialVersionUID = -2299453443943600172L;

}
