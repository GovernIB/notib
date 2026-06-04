package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.model.AccioMassivaResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Entitat de base de dades de les accions massives
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Entity
@Table(name = BaseConfig.DB_PREFIX + "accio_massiva")
@Getter
@Setter
@NoArgsConstructor
public class AccioMassivaResourceEntity extends BaseAuditableResourceEntity<AccioMassivaResource> implements AdminEntitatResourceEntity<AccioMassivaResource> {


	private static int ERROR_DESC_MAX_LENGTH = 1024;
	private static int STACKTRACE_MAX_LENGTH = 2048;

	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccioMassivaTipus tipus;
	@Column(name = "data_inici")
	@Temporal(TemporalType.DATE)
	private Date dataInici;
	@Column(name = "data_fi")
	@Temporal(TemporalType.DATE)
	private Date dataFi;
	@Column(name = "error")
	private Boolean error;
	@Column(name = "num_errors")
	private int numErrors;
	@Column(name = "error_descripcio", length = 1024)
	private String errorDescripcio;
	@Column(name = "excepcio_stacktrace", length = 2048)
	private String excepcioStacktrace;

	@Column(name = "tipus_seleccionat", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private SeleccioTipus tipusElementSeleccionat;

	// Paràmetres de la acció massiva
	@Column(name = "motiu", length = 250)
	private String motiu;
	@Column(name = "admin_entitat")
	private boolean adminEntitat;
	@Column(name = "dies")
	private int dies;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat_id",
		referencedColumnName = "id",
		foreignKey = @javax.persistence.ForeignKey(name = BaseConfig.DB_PREFIX + "_ACCIO_MASSIVA_ENTITAT_FK"),
		nullable = false)
	private EntitatResourceEntity entitat;

	@OneToMany(mappedBy = "accioMassiva", fetch = FetchType.LAZY, orphanRemoval = true, cascade={CascadeType.ALL})
	private List<AccioMassivaElementResourceEntity> elements;

	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_MAX_LENGTH);
	}

	public void setExcepcioStacktrace(String excepcioStacktrace) {
		this.excepcioStacktrace = StringUtils.abbreviate(excepcioStacktrace, STACKTRACE_MAX_LENGTH);
	}

	public AccioMassivaElementResourceEntity getElement(Long elementId) {
		return elements.stream().filter(x -> x.getElementId().equals(elementId)).findFirst().orElse(null);
	}

	@Builder
	public AccioMassivaResourceEntity(AccioMassivaResource resource, List<AccioMassivaElementResourceEntity> elements, EntitatResourceEntity entitat) {

		tipus = resource.getTipus();
		dataInici = resource.getDataInici();
		dataFi = resource.getDataFi();
		error = resource.getError();
		numErrors = resource.getNumErrors();
		errorDescripcio = resource.getErrorDescripcio();
		excepcioStacktrace = resource.getExcepcioStacktrace();
		tipusElementSeleccionat = resource.getTipusElementSeleccionat();
		motiu = resource.getMotiu();
		adminEntitat = resource.isAdminEntitat();
		dies = resource.getDies();
		this.entitat = entitat;
		this.elements = elements;

	}

}
