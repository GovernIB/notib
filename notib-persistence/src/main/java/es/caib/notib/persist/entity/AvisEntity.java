package es.caib.notib.persist.entity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(	name = "not_avis")
@EntityListeners(AuditingEntityListener.class)
public class AvisEntity extends NotibAuditable<Long> {

	@Column(name = "assumpte", length = 256, nullable = false)
	private String assumpte;
	@Column(name = "missatge", length = 2048, nullable = false)
	private String missatge;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_final", nullable = false)
	private Date dataFinal;
	@Column(name = "actiu", nullable = false)
	private Boolean actiu;
	@Column(name = "avis_nivell", length = 2048, nullable = false)
	@Enumerated(EnumType.STRING)
	private AvisNivellEnumDto avisNivell;
	@Column(name = "avis_admin", nullable = false)
	private Boolean avisAdministrador;

	@ManyToOne()
	@JoinColumn(
		name = "entitat_id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "servei_entitat_fk"),
		insertable = false, updatable = false)
	private EntitatEntity entitat;
	@Column(name = "entitat_id", length = 2048)
	private Long entitatId;


	public void update(String assumpte, String missatge, Date dataInici, Date dataFinal, AvisNivellEnumDto avisNivell, Long entitatId) {

		this.assumpte = assumpte;
		this.missatge = missatge;
		this.dataInici = dataInici;
		this.dataFinal = dataFinal;
		this.avisNivell = avisNivell;
		this.entitatId = entitatId;
	}

	public void updateActiva(Boolean actiu) {
		this.actiu = actiu;
	}


	public static Builder getBuilder(String assumpte, String missatge, Date dataInici, Date dataFinal, AvisNivellEnumDto avisNivell, Boolean avisAdministrador, Long entitatId) {
		return new Builder(assumpte, missatge, dataInici, dataFinal, avisNivell, avisAdministrador, entitatId);
	}


	public static class Builder {
		AvisEntity built;
		Builder(String assumpte, String missatge, Date dataInici, Date dataFinal, AvisNivellEnumDto avisNivell, Boolean avisAdministrador, Long entitatId) {
			built = new AvisEntity();
			built.assumpte = assumpte;
			built.missatge = missatge;
			built.dataInici = dataInici;
			built.dataFinal = dataFinal;
			built.actiu = true;
			built.avisNivell = avisNivell;
			built.avisAdministrador = avisAdministrador != null && avisAdministrador;
			built.entitatId = entitatId;
		}
		public AvisEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = 6125475298050864472L;

}
