package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.AccioMassivaElementResource;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades dels elements d'una acció massives
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Entity
@Table(name = BaseConfig.DB_PREFIX + "accio_massiva_element")
@Getter
@Setter
@NoArgsConstructor
public class AccioMassivaElementResourceEntity extends BaseResourceEntity<AccioMassivaElementResource> implements ResourceEntity<AccioMassivaElementResource, Long> {

	private static int ERROR_DESC_MAX_LENGTH = 1024;
	private static int STACKTRACE_MAX_LENGTH = 2048;

	@Column(name = "element_id", nullable = false)
	private Long elementId;

	@Column(name = "data_execucio")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataExecucio;

	@Column(name = "error_descripcio", length = 1024)
	private String errorDescripcio;

	@Column(name = "excepcio_stacktrace", length = 2048)
	private String excepcioStackTrace;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "accio_massiva_id",
		referencedColumnName = "id",
		foreignKey = @javax.persistence.ForeignKey(name = "FK_ACCIOMASSIVA_ELEMENT"))
	private AccioMassivaResourceEntity accioMassiva;

	public void actualitzar() {

		dataExecucio = new Date();
		actualitzarDataFi();
	}

	public void actualitzar(String errorDesc, String errorStackTrace) {

		dataExecucio = new Date();
		if (!StringUtils.isEmpty(errorDesc)) {
			errorDescripcio = formatErrorDescripcio(errorDesc);
			accioMassiva.setNumErrors(accioMassiva.getNumErrors()+1);
		}
		if (!StringUtils.isEmpty(errorStackTrace)) {
			excepcioStackTrace = formatExcepcioStacktrace(errorStackTrace);
		}
		actualitzarDataFi();
	}

	public void actualitzarDataFi() {
//
//		var elementsNoExecutats = accioMassiva.getElements().stream().filter(x -> dataExecucio == null).collect(Collectors.toList());
//		if (elementsNoExecutats.isEmpty()) {
//			accioMassiva.setDataFi(new Date());
//		}
	}


	public String formatErrorDescripcio(String errorDescripcio) {
		return StringUtils.abbreviate(errorDescripcio, ERROR_DESC_MAX_LENGTH);
	}

	public String formatExcepcioStacktrace(String excepcioStacktrace) {
		return StringUtils.abbreviate(excepcioStacktrace, STACKTRACE_MAX_LENGTH);
	}


	@Builder
	public AccioMassivaElementResourceEntity(AccioMassivaElementResource resource, AccioMassivaResourceEntity accioMassiva) {

		elementId = resource.getElementId();
		dataExecucio = resource.getDataExecucio();
		errorDescripcio = resource.getErrorDescripcio();
		excepcioStackTrace = resource.getExcepcioStackTrace();
		this.accioMassiva = accioMassiva;
	}

}
