package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.model.CallbackResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades dels callback d'una remesa.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "callback")
@Getter
@Setter
@NoArgsConstructor
public class CallbackResourceEntity extends BaseResourceEntity<CallbackResource> {

	private static final int ERROR_DESC_MAX_LENGTH = 2048;

	@Column(name = "usuari_codi", length = 64, nullable = false)
	private String usuariCodi;
	@Column(name = "notificacio_id", nullable = false)
	private Long notificacioId;
	@Column(name = "enviament_id", nullable = false)
	private Long enviamentId;
	@Column(name = "data_creacio", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCreacio;
	@Column(name = "ultim_intent", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date ultimIntent;
	@Column(name = "data", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	@Column(name = "error", nullable = false)
	private boolean error;
	@Column(name = "error_desc", length = ERROR_DESC_MAX_LENGTH)
	private String errorDesc;
	@Column(name = "estat", length = 10, nullable = true)
	@Enumerated(EnumType.STRING)
	private CallbackEstatEnumDto estat;
	@Column(name = "intents")
	private int intents;
	@Column(name = "pausat", nullable = false)
	private boolean pausat;

	@Builder
	public CallbackResourceEntity(CallbackResource resource)  {

		usuariCodi = resource.getUsuariCodi();
		notificacioId = resource.getNotificacioId();
		enviamentId = resource.getEnviamentId();
		dataCreacio = resource.getDataCreacio();
		ultimIntent = resource.getUltimIntent();
		data = resource.getData();
		error = resource.isError();
		errorDesc = resource.getErrorDesc();
		estat = resource.getEstat();
		intents = resource.getIntents();
		pausat = resource.isPausat();
	}

}
