package es.caib.notib.core.entity.acl;

import lombok.Getter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa un SID d'una ACL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_acl_class")
public class AclClassEntity extends AbstractPersistable<Long> {

	@Column(name = "class", length = 100, nullable = false)
	private String classname;

	private static final long serialVersionUID = -2299453443943600172L;
}