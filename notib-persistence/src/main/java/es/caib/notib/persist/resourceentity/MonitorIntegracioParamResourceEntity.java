package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.MonitorIntegracioParamResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entitat de base de dades de monitor integracio param.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "mon_int_param")
@Getter
@Setter
@NoArgsConstructor
public class MonitorIntegracioParamResourceEntity extends BaseResourceEntity<MonitorIntegracioParamResource> {


	@Column(name = "codi", length = 256, nullable = false)
	private String codi;

	@Column(name = "valor", length = 1024)
	private String valor;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "mon_int_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "monintparam_monint_fk"))
	private MonitorIntegracioResourceEntity monitorIntegracio;

	public MonitorIntegracioParamResourceEntity(MonitorIntegracioParamResource resource, MonitorIntegracioResourceEntity monitorIntegracio) {

		codi = resource.getCodi();
		valor = resource.getValor();
		this.monitorIntegracio = monitorIntegracio;
	}
}
