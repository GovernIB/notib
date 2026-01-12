package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.AplicacioResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

/**
 * Entitat de base de dades pels recursos de tipus aplicació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "aplicacio")
@Getter
@Setter
@NoArgsConstructor
public class AplicacioResourceEntity extends BaseAuditableResourceEntity<AplicacioResource> {

	@Column(name = "usuari_codi", length = 64, nullable = false)
	protected String usuariCodi;
	@Column(name = "callback_url", length = 256, nullable = false)
	private String callbackUrl;
	@Column(name = "activa", nullable = false)
	private boolean activa;
	@Column(name = "header_csrf", nullable = false)
	private boolean headerCsrf;
	@Column(name = "horari_laboral_inici", nullable = false)
	private LocalTime horariLaboralInici;
	@Column(name = "horari_laboral_fi", nullable = false)
	private LocalTime horariLaboralFi;
	@Column(name = "max_env_min_laboral", nullable = false)
	private Integer maxEnviamentsMinutLaboral;
	@Column(name = "max_env_min_no_laboral", nullable = false)
	private Integer maxEnviamentsMinutNoLaboral;
	@Column(name = "max_env_dia_laboral", nullable = false)
	private Integer maxEnviamentsDiaLaboral;
	@Column(name = "max_env_dia_no_laboral", nullable = false)
	private Integer maxEnviamentsDiaNoLaboral;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_entrega_cie_fk"),
			nullable = false)
	private EntitatResourceEntity entitat;

	@Builder
	public AplicacioResourceEntity(
			AplicacioResource resource,
			EntitatResourceEntity entitat) {
		this.usuariCodi = resource.getUsuariCodi();
		this.callbackUrl = resource.getCallbackUrl();
		this.activa = resource.getActiva() != null && resource.getActiva();
		this.headerCsrf = resource.getHeaderCsrf() != null && resource.getHeaderCsrf();
		this.horariLaboralInici = resource.getHorariLaboralInici();
		this.horariLaboralFi = resource.getHorariLaboralFi();
		this.maxEnviamentsMinutLaboral = resource.getMaxEnviamentsMinutLaboral();
		this.maxEnviamentsMinutNoLaboral = resource.getMaxEnviamentsMinutNoLaboral();
		this.maxEnviamentsDiaLaboral = resource.getMaxEnviamentsDiaLaboral();
		this.maxEnviamentsDiaNoLaboral = resource.getMaxEnviamentsDiaNoLaboral();
		this.entitat = entitat;
	}

}
