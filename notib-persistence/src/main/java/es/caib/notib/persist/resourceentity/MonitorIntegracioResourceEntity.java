package es.caib.notib.persist.resourceentity;


import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.model.MonitorIntegracioParamResource;
import es.caib.notib.logic.intf.model.MonitorIntegracioResource;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioParamEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entitat de base de dades de monitor integracio.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "mon_int")
@Getter
@Setter
@NoArgsConstructor
public class MonitorIntegracioResourceEntity extends BaseResourceEntity<MonitorIntegracioResource> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	@Enumerated(EnumType.STRING)
	private IntegracioCodi codi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;

	@Column(name = "descripcio", length = 1024)
	private String descripcio;

	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	protected IntegracioAccioTipusEnumDto tipus;

	@Column(name = "temps_resposta")
	private Long tempsResposta;

	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private IntegracioAccioEstatEnumDto estat;

	@Column(name = "error_descripcio", length = 1024)
	private String errorDescripcio;

	@Column(name = "excepcio_msg", length = 1024)
	private String excepcioMessage;

	@Column(name = "excepcio_stacktrace", length = 2048)
	private String excepcioStacktrace;

	@Column(name = "aplicacio", length = 64)
	private String aplicacio;

	@Column(name = "codi_entitat", length = 64)
	private String codiEntitat;

	@Column(name = "notificacio_id")
	private Long notificacioId;

	@Column(name = "codi_usuari", length = 64)
	private String codiUsuari;


	@OneToMany(mappedBy = "monitorIntegracio", fetch = FetchType.LAZY, orphanRemoval = true, cascade={CascadeType.ALL})
	private List<MonitorIntegracioParamResourceEntity> parametres = new ArrayList<>();


	public MonitorIntegracioResourceEntity(MonitorIntegracioResource resource, UsuariResourceEntity usuari) {

		codi = resource.getCodi();
		data = resource.getData();
		descripcio = resource.getDescripcio();
		tipus = resource.getTipus();
		tempsResposta = resource.getTempsResposta();
		estat = resource.getEstat();
		errorDescripcio = resource.getErrorDescripcio();
		excepcioMessage = resource.getExcepcioMessage();
		excepcioStacktrace = resource.getExcepcioStacktrace();
		aplicacio = resource.getAplicacio();
		codiEntitat = resource.getCodiEntitat();
		codiUsuari = resource.getCodiUsuari();
	}
}
