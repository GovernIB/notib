package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "not_explot_env_info",
		uniqueConstraints = {@UniqueConstraint(name = "not_explot_env_uk", columnNames = {"enviament_id"})}
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ExplotEnvInfoEntity extends AbstractPersistable<Long> implements Serializable {

	private static final long serialVersionUID = 3034708072746624642L;

	@Column(name = "data_creacio", nullable = false)
	private LocalDateTime dataCreacio;
	@Column(name = "temps_pendent")
	private Long tempsPendent;
	@Column(name = "data_reg_env_error")
	private LocalDateTime dataRegEnviamentError;
	@Column(name = "intents_reg_enviament")
	private int intentsRegEnviament;
	@Column(name = "data_registrada")
	private LocalDateTime dataRegistrada;
	@Column(name = "temps_registrada")
	private Long tempsRegistrada;
	@Column(name = "intents_sir_consulta")
	private int intentsSirConsulta;
	@Column(name = "data_reg_acceptada")
	private LocalDateTime dataRegAcceptada;
	@Column(name = "data_reg_rebutjada")
	private LocalDateTime dataRegRebutjada;
	@Column(name = "data_not_enviament_error")
	private LocalDateTime dataNotEnviamentError;
	@Column(name = "intents_not_enviament")
	private int intentsNotEnviament;
	@Column(name = "data_not_enviada")
	private LocalDateTime dataNotEnviada;
	@Column(name = "temps_not_enviada")
	private Long tempsNotEnviada;
	@Column(name = "data_not_notificada")
	private LocalDateTime dataNotNotificada;
	@Column(name = "data_not_rebutjada")
	private LocalDateTime dataNotRebutjada;
	@Column(name = "data_not_expirada")
	private LocalDateTime dataNotExpirada;
	@Column(name = "data_not_error")
	private LocalDateTime dataNotError;
	@Column(name = "data_cie_enviament_error")
	private LocalDateTime dataCieEnviamentError;
	@Column(name = "intents_cie_enviament")
	private int intentsCieEnviament;
	@Column(name = "data_cie_enviada")
	private LocalDateTime dataCieEnviada;
	@Column(name = "temps_cie_enviada")
	private Long tempsCieEnviada;
	@Column(name = "data_cie_notificada")
	private LocalDateTime dataCieNotificada;
	@Column(name = "data_cie_rebutjada")
	private LocalDateTime dataCieRebutjada;
	@Column(name = "data_cie_cancelada")
	private LocalDateTime dataCieCancelada;
	@Column(name = "data_cie_error")
	private LocalDateTime dataCieError;
	@Column(name = "data_email_enviament_error")
	private LocalDateTime dataEmailEnviamentError;
	@Column(name = "intents_email_enviament")
	private int intentsEmailEnviament;
	@Column(name = "data_email_enviada")
	private LocalDateTime dataEmailEnviada;
	@Column(name = "temps_total")
	private Long tempsTotal;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="enviament_id")
	protected NotificacioEnviamentEntity enviament;

}
