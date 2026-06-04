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
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.ArrayList;
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

	// TODO REVISAR EL TEMA DE L'ACTUALITZACIO DE L'ESTAT

	@Column(name = "estat")
	private String estat = "";

	public List<AccioMassivaElementResourceEntity> getElements() { return elements; }

	public void setElements(List<AccioMassivaElementResourceEntity> elements) {
		this.elements = elements;
		this.estat = calcularEstat();
	}

	public void addElement(AccioMassivaElementResourceEntity element) {
		elements.add(element);
		element.setAccioMassiva(this);
		this.estat = calcularEstat();
	}

	public void removeElement(AccioMassivaElementResourceEntity element) {
		elements.remove(element);
		element.setAccioMassiva(null);
		this.estat = calcularEstat();
	}

	@PrePersist
	@PreUpdate
	void updateEstatBeforeSave() {
		this.estat = calcularEstat();
	}

	@PostLoad
	void updateEstatAfterLoad() {
		this.estat = calcularEstat();
	}

	private String calcularEstat() {
		if (elements == null || elements.isEmpty()) return "";
		boolean pendent = false, error = false, finalitzat = false;
		for (var element : elements) {
			if (element.getDataExecucio() == null && StringUtils.isBlank(element.getErrorDescripcio())) pendent = true;
			else if (element.getDataExecucio() != null && !StringUtils.isBlank(element.getErrorDescripcio())) error = true;
			else if (element.getDataExecucio() != null && StringUtils.isBlank(element.getErrorDescripcio())) finalitzat = true;
		}
		var parts = new ArrayList<String>();
		if (pendent) parts.add("PENDENT");
		if (error) parts.add("ERROR");
		if (finalitzat) parts.add("FINALITZAT");
		return String.join(",", parts);
	}

	public String getEstat() { return estat; }
	public void setEstat(String estat) { this.estat = estat; }
//
//	private String calcularEstat() {
//
//		if (elements == null) {
//			return "";
//		}
//		var estat = "";
//		for (var element : elements) {
//			if (element.getDataExecucio() == null && StringUtils.isBlank(element.getErrorDescripcio())) {
//				estat += !estat.contains("PENDENT") ? "PENDENT" : "";
//			}
//			if (element.getDataExecucio() != null && !StringUtils.isBlank(element.getErrorDescripcio())) {
//				estat += !estat.contains("ERROR") ? "ERROR" : "";
//				// ERROR
//			}
//			if (element.getDataExecucio() != null && StringUtils.isBlank(element.getErrorDescripcio())) {
//				estat += !estat.contains("FINALITZAT") ? "FINALITZAT" : "";
//				// FINALITZAT
//			}
//		}
//		return estat;
//	}

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
