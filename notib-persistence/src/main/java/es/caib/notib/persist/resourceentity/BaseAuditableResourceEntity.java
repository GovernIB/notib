package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;

/**
 * Entitat genèrica de base de dades amb informació d'auditoria.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseAuditableResourceEntity<R extends Resource<Long>>
		extends es.caib.notib.persist.base.entity.BaseAuditableEntity<R, Long> {

	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "hibernate_seq")
	@SequenceGenerator(
			name = "hibernate_seq",
			sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq",
			allocationSize = 1)
	private @Nullable Long id;

	@Nullable
	@Override
	@Column(updatable = false, nullable = false)
	public Long getId() {
		return id;
	}

}