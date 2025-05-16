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

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="dimensio_id")
	protected ExplotDimensioEntity dimensio;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="temps_id")
	protected ExplotTempsEntity temps;

	// Total en estat
	@Column(name = "tot_pendent")	private long pendent; // Abans: pendent

	@Column(name = "tot_reg_err")	private long regEnviamentError; // Abans: reg_env_error
	@Column(name = "tot_registr")	private long registrada; // Abans: registrada
	@Column(name = "tot_sir_acc")	private long regAcceptada; // Abans: reg_acceptada
	@Column(name = "tot_sir_reb")	private long regRebutjada; // Abans: reg_rebutjada

	@Column(name = "tot_not_err")	private long notEnviamentError; // Abans: not_env_error
	@Column(name = "tot_not_env")	private long notEnviada; // Abans: not_enviada
	@Column(name = "tot_not_not")	private long notNotificada; // Abans: not_notificada
	@Column(name = "tot_not_reb")	private long notRebutjada; // Abans: not_rebutjada
	@Column(name = "tot_not_exp")	private long notExpirada; // Abans: not_expirada

	@Column(name = "tot_cie_err")	private long cieEnviamentError; // Abans: cie_env_error
	@Column(name = "tot_cie_env")	private long cieEnviada; // Abans: cie_enviada
	@Column(name = "tot_cie_not")	private long cieNotificada; // Abans: cie_notificada
	@Column(name = "tot_cie_reb")	private long cieRebutjada; // Abans: cie_rebutjada
	@Column(name = "tot_cie_fal")	private long cieError; // Abans: cie_error

	@Column(name = "tot_process")	private long processada; // Abans: processada

	// Transicions
	@Column(name = "tr_creades")	private Long trCreades;
	@Column(name = "tr_reg_err") 	private Long trRegEnviadesError;
	@Column(name = "tr_registr") 	private Long trRegistrades;
	@Column(name = "tr_sir_acc") 	private Long trSirAcceptades;
	@Column(name = "tr_sir_reb") 	private Long trSirRebutjades;
	@Column(name = "tr_not_err") 	private Long trNotEnviadesError;
	@Column(name = "tr_not_env") 	private Long trNotEnviades;
	@Column(name = "tr_not_not") 	private Long trNotNotificades;
	@Column(name = "tr_not_reb") 	private Long trNotRebujtades;
	@Column(name = "tr_not_exp") 	private Long trNotExpirades;
	@Column(name = "tr_not_fal") 	private Long trNotFallades;
	@Column(name = "tr_cie_err") 	private Long trCieEnviadesError;
	@Column(name = "tr_cie_env") 	private Long trCieEnviades;
	@Column(name = "tr_cie_not") 	private Long trCieNotificades;
	@Column(name = "tr_cie_reb") 	private Long trCieRebutjades;
	@Column(name = "tr_cie_can") 	private Long trCieCancelades;
	@Column(name = "tr_cie_fal") 	private Long trCieFallades;
	@Column(name = "tr_eml_err") 	private Long trEmailEnviadesError;
	@Column(name = "tr_eml_env") 	private Long trEmailEnviades;

	// Temps mig en estat
	@Column(name = "tmp_pnd") 	private Long temsMigPendent;
	@Column(name = "tmp_reg") 	private Long temsMigRegistrada;
	@Column(name = "tmp_not") 	private Long temsMigNotEnviada;
	@Column(name = "tmp_cie") 	private Long temsMigCieEnviada;
	@Column(name = "tmp_tot") 	private Long temsMigTotal;

	// Nombre mig d'intents
	@Column(name = "int_reg") 	private Long intentsRegistre;
	@Column(name = "int_sir") 	private Long intentsSir;
	@Column(name = "int_not") 	private Long intentsNotEnviament;
	@Column(name = "int_cie") 	private Long intentsCieEnviament;
	@Column(name = "int_eml") 	private Long intentsEmailEnviament;
}
