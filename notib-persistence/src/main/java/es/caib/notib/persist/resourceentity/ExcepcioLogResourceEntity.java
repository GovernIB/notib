package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ExcepcioLogResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "excepcio_log")
@Getter
@Setter
@NoArgsConstructor
public class ExcepcioLogResourceEntity extends BaseAuditableResourceEntity<ExcepcioLogResource> {

	@Column(name = "data", nullable = false)
	private Date data = new Date();

	@Column(name = "tipus", length = 512)
	private String tipus;

	@Column(name = "object_id", length = 512)
	private String objectId;

	@Column(name = "object_class", length = 512)
	private String objectClass;

	@Column(name = "uri", length = 1024)
	private String uri;

	@Column(name = "param1", length = 512)
	private String param1;

	@Column(name = "param2", length = 512)
	private String param2;

	@Column(name = "message", length = 4000)
	private String message;

	@Lob
	@Column(name = "stacktrace")
	private String stacktrace;

	@Column(name = "entitat_id")
	private Long entitatId;

	/** Hash que identifica una excepció "del mateix tipus" per detectar repeticions. */
	@Column(name = "signatura", length = 64)
	private String signatura;

	/** Vegades que s'ha produït aquesta excepció dins la finestra de deduplicació. */
	@Column(name = "num_ocurrencies", nullable = false)
	private int numOcurrencies = 1;

	/** Data de la darrera ocurrència (a "data" hi ha la primera). */
	@Column(name = "data_darrera")
	private Date dataDarrera;
}
