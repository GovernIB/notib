package es.caib.notib.persist.entity.explotacio;

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
import java.io.Serializable;

@Entity
@Table(name = "not_explot_fet")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ExplotFetsEntity extends AbstractPersistable<Long> implements Serializable {

	private static final long serialVersionUID = 2900135379128738307L;

	@Column(name = "pendent")
	private long pendent;

	@Column(name = "reg_env_error")
	private long regEnviamentError;

	@Column(name = "registrada")
	private long registrada;

	@Column(name = "reg_acceptada")
	private long regAcceptada;

	@Column(name = "reg_rebutjada")
	private long regRebutjada;

	@Column(name = "not_env_error")
	private long notEnviamentError;

	@Column(name = "not_enviada")
	private long notEnviada;

	@Column(name = "not_notificada")
	private long notNotificada;

	@Column(name = "not_rebutjada")
	private long notRebutjada;

	@Column(name = "not_expirada")
	private long notExpirada;

	@Column(name = "cie_env_error")
	private long cieEnviamentError;

	@Column(name = "cie_enviada")
	private long cieEnviada;

	@Column(name = "cie_notificada")
	private long cieNotificada;

	@Column(name = "cie_rebutjada")
	private long cieRebutjada;

	@Column(name = "cie_error")
	private long cieError;

	@Column(name = "processada")
	private long processada;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="dimensio_id")
	protected ExplotDimensioEntity dimensio;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="temps_id")
	protected ExplotTempsEntity temps;

}
