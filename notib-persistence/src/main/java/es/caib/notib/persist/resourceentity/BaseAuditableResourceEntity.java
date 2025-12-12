package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.Resource;
import es.caib.notib.persist.base.entity.AuditableEntity;
import es.caib.notib.persist.config.AuditingConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entitat genèrica de base de dades amb informació d'auditoria.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingConfig.CustomAuditingEntityListener.class)
public abstract class BaseAuditableResourceEntity<R extends Resource<Long>>
		extends es.caib.notib.persist.base.entity.BaseResourceEntity<R, Long>
		implements AuditableEntity {

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

	@CreatedBy
	@Column(name = "createdby_codi", length = 64, nullable = false)
	private String createdBy;
	@CreatedDate
	@Column(name = "createddate", nullable = false)
	private LocalDateTime createdDate;
	@LastModifiedBy
	@Column(name = "lastmodifiedby_codi", length = 64)
	private String lastModifiedBy;
	@LastModifiedDate
	@Column(name = "lastmodifieddate")
	private LocalDateTime lastModifiedDate;

	@Override
	public void updateCreated(
			String createdBy,
			LocalDateTime createdDate) {
		this.createdBy = createdBy;
		this.createdDate = (createdDate != null) ? createdDate : LocalDateTime.now();
	}

	@Override
	public void updateLastModified(
			String lastModifiedBy,
			LocalDateTime lastModifiedDate) {
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = (lastModifiedDate != null) ? lastModifiedDate : LocalDateTime.now();
	}

}