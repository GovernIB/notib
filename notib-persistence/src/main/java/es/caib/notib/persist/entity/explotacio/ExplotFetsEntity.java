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
	@Column(name = "tot_pendent")	private Long pendent; // Abans: pendent

	@Column(name = "tot_reg_err")	private Long regEnviamentError; // Abans: reg_env_error
	@Column(name = "tot_registr")	private Long registrada; // Abans: registrada
	@Column(name = "tot_sir_acc")	private Long regAcceptada; // Abans: reg_acceptada
	@Column(name = "tot_sir_reb")	private Long regRebutjada; // Abans: reg_rebutjada

	@Column(name = "tot_not_err")	private Long notEnviamentError; // Abans: not_env_error
	@Column(name = "tot_not_env")	private Long notEnviada; // Abans: not_enviada
	@Column(name = "tot_not_not")	private Long notNotificada; // Abans: not_notificada
	@Column(name = "tot_not_reb")	private Long notRebutjada; // Abans: not_rebutjada
	@Column(name = "tot_not_exp")	private Long notExpirada; // Abans: not_expirada

	@Column(name = "tot_cie_err")	private Long cieEnviamentError; // Abans: cie_env_error
	@Column(name = "tot_cie_env")	private Long cieEnviada; // Abans: cie_enviada
	@Column(name = "tot_cie_not")	private Long cieNotificada; // Abans: cie_notificada
	@Column(name = "tot_cie_reb")	private Long cieRebutjada; // Abans: cie_rebutjada
	@Column(name = "tot_cie_fal")	private Long cieError; // Abans: cie_error

	@Column(name = "tot_process")	private Long processada; // Abans: processada

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

	@Column(name = "tmp_reg_sac") 	private Long temsMigRegistradaPerSirAcceptada;
	@Column(name = "tmp_reg_srb") 	private Long temsMigRegistradaPerSirRebutjada;
	@Column(name = "tmp_reg_not") 	private Long temsMigRegistradaPerNotificada;
	@Column(name = "tmp_reg_eml") 	private Long temsMigRegistradaPerEmail;
	@Column(name = "tmp_not_not") 	private Long temsMigNotEnviadaPerNotificada;
	@Column(name = "tmp_not_reb") 	private Long temsMigNotEnviadaPerRebubjada;
	@Column(name = "tmp_not_exp") 	private Long temsMigNotEnviadaPerExpirada;
	@Column(name = "tmp_not_fal") 	private Long temsMigNotEnviadaPerFallada;
	@Column(name = "tmp_cie_not") 	private Long temsMigCieEnviadaPerNotificada;
	@Column(name = "tmp_cie_reb") 	private Long temsMigCieEnviadaPerRebubjada;
	@Column(name = "tmp_cie_can") 	private Long temsMigCieEnviadaPerCancelada;
	@Column(name = "tmp_cie_fal") 	private Long temsMigCieEnviadaPerFallada;
	@Column(name = "tmp_tot_nac") 	private Long temsMigTotalPerNotAcceptada;
	@Column(name = "tmp_tot_nrb") 	private Long temsMigTotalPerNotRebutjada;
	@Column(name = "tmp_tot_nex") 	private Long temsMigTotalPerNotExpirada;
	@Column(name = "tmp_tot_nfl") 	private Long temsMigTotalPerNotFallada;
	@Column(name = "tmp_tot_cac") 	private Long temsMigTotalPerCieAcceptada;

	// Nombre mig d'intents
	@Column(name = "int_reg") 	private Long intentsRegistre;
	@Column(name = "int_sir") 	private Long intentsSir;
	@Column(name = "int_not") 	private Long intentsNotEnviament;
	@Column(name = "int_cie") 	private Long intentsCieEnviament;
	@Column(name = "int_eml") 	private Long intentsEmailEnviament;


	public ExplotFetsEntity(ExplotDimensioEntity dimension, ExplotTempsEntity ete, ExplotFets fets) {
		this.dimensio = dimension;
		this.temps = ete;
		this.pendent = fets.getPendent();
		this.regEnviamentError = fets.getRegEnviamentError();
		this.registrada = fets.getRegistrada();
		this.regAcceptada = fets.getRegAcceptada();
		this.regRebutjada = fets.getRegRebutjada();
		this.notEnviamentError = fets.getNotEnviamentError();
		this.notEnviada = fets.getNotEnviada();
		this.notNotificada = fets.getNotNotificada();
		this.notRebutjada = fets.getNotRebutjada();
		this.notExpirada = fets.getNotExpirada();
		this.cieEnviamentError = fets.getCieEnviamentError();
		this.cieEnviada = fets.getCieEnviada();
		this.cieNotificada = fets.getCieNotificada();
		this.cieRebutjada = fets.getCieRebutjada();
		this.cieError = fets.getCieError();
		this.processada = fets.getProcessada();
		this.trCreades = fets.getTrCreades();
		this.trRegEnviadesError = fets.getTrRegEnviadesError();
		this.trRegistrades = fets.getTrRegistrades();
		this.trSirAcceptades = fets.getTrSirAcceptades();
		this.trSirRebutjades = fets.getTrSirRebutjades();
		this.trNotEnviadesError = fets.getTrNotEnviadesError();
		this.trNotEnviades = fets.getTrNotEnviades();
		this.trNotNotificades = fets.getTrNotNotificades();
		this.trNotRebujtades = fets.getTrNotRebujtades();
		this.trNotExpirades = fets.getTrNotExpirades();
		this.trNotFallades = fets.getTrNotFallades();
		this.trCieEnviadesError = fets.getTrCieEnviadesError();
		this.trCieEnviades = fets.getTrCieEnviades();
		this.trCieNotificades = fets.getTrCieNotificades();
		this.trCieRebutjades = fets.getTrCieRebutjades();
		this.trCieCancelades = fets.getTrCieCancelades();
		this.trCieFallades = fets.getTrCieFallades();
		this.trEmailEnviadesError = fets.getTrEmailEnviadesError();
		this.trEmailEnviades = fets.getTrEmailEnviades();
		this.temsMigPendent = fets.getTemsMigPendent();
		this.temsMigRegistrada = fets.getTemsMigRegistrada();
		this.temsMigNotEnviada = fets.getTemsMigNotEnviada();
		this.temsMigCieEnviada = fets.getTemsMigCieEnviada();
		this.temsMigTotal = fets.getTemsMigTotal();
		this.temsMigRegistradaPerSirAcceptada = fets.getTemsMigRegistradaPerSirAcceptada();
		this.temsMigRegistradaPerSirRebutjada = fets.getTemsMigRegistradaPerSirRebutjada();
		this.temsMigRegistradaPerNotificada = fets.getTemsMigRegistradaPerNotificada();
		this.temsMigRegistradaPerEmail = fets.getTemsMigRegistradaPerEmail();
		this.temsMigNotEnviadaPerNotificada = fets.getTemsMigNotEnviadaPerNotificada();
		this.temsMigNotEnviadaPerRebubjada = fets.getTemsMigNotEnviadaPerRebubjada();
		this.temsMigNotEnviadaPerExpirada = fets.getTemsMigNotEnviadaPerExpirada();
		this.temsMigNotEnviadaPerFallada = fets.getTemsMigNotEnviadaPerFallada();
		this.temsMigCieEnviadaPerNotificada = fets.getTemsMigCieEnviadaPerNotificada();
		this.temsMigCieEnviadaPerRebubjada = fets.getTemsMigCieEnviadaPerRebubjada();
		this.temsMigCieEnviadaPerCancelada = fets.getTemsMigCieEnviadaPerCancelada();
		this.temsMigCieEnviadaPerFallada = fets.getTemsMigCieEnviadaPerFallada();
		this.temsMigTotalPerNotAcceptada = fets.getTemsMigTotalPerNotAcceptada();
		this.temsMigTotalPerNotRebutjada = fets.getTemsMigTotalPerNotRebutjada();
		this.temsMigTotalPerNotExpirada = fets.getTemsMigTotalPerNotExpirada();
		this.temsMigTotalPerNotFallada = fets.getTemsMigTotalPerNotFallada();
		this.temsMigTotalPerCieAcceptada = fets.getTemsMigTotalPerCieAcceptada();
		this.intentsRegistre = fets.getIntentsRegistre();
		this.intentsSir = fets.getIntentsSir();
		this.intentsNotEnviament = fets.getIntentsNotEnviament();
		this.intentsCieEnviament = fets.getIntentsCieEnviament();
		this.intentsEmailEnviament = fets.getIntentsEmailEnviament();
	}
	
}
