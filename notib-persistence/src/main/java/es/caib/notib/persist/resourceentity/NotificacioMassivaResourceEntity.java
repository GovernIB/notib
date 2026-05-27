package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.model.NotificacioMassivaResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ForeignKey;

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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entitat de base de dades de les notificacions massives.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Entity
@Table(name = BaseConfig.DB_PREFIX + "notificacio_massiva")
@Getter
@Setter
@NoArgsConstructor
public class NotificacioMassivaResourceEntity
	extends BaseAuditableResourceEntity<NotificacioMassivaResource>
	implements AdminEntitatResourceEntity<NotificacioMassivaResource> {

//	@Transient
//	private final Object procesLock = new Object();
//	@Builder.Default
	@Column(name = "PROGRESS", length = 20, nullable = false)
	private Integer progress = 0;
	@Column(name = "CSV_FILENAME", length = 200, nullable = false)
	private String csvFilename;
	@Column(name = "ZIP_FILENAME", length = 200, nullable = false)
	private String zipFilename;
	@Column(name = "CSV_GESDOC_ID", length = 64, nullable = false)
	private String csvGesdocId;
	@Column(name = "ZIP_GESDOC_ID", length = 64, nullable = false)
	private String zipGesdocId;
	@Setter
	@Column(name = "RESUM_GESDOC_ID", length = 64)
	private String resumGesdocId;
	@Setter
	@Column(name = "ERRORS_GESDOC_ID", length = 64)
	private String errorsGesdocId;
	@Column(name = "CADUCITAT", nullable = false)
	@Temporal(TemporalType.DATE)
	protected Date caducitat;
	@Column(name = "EMAIL", nullable = true)
	private String email;
	@Column(name = "estat_validacio", length = 32)
	@Enumerated(EnumType.STRING)
	private NotificacioMassivaEstatDto estatValidacio;
	@Column(name = "estat_proces", length = 32)
	@Enumerated(EnumType.STRING)
	private NotificacioMassivaEstatDto estatProces;
	@Column(name = "num_notificacions")
	private Integer totalNotificacions;
	@Column(name = "num_validades")
	private Integer notificacionsValidades;
	@Column(name = "num_processades")
	private Integer notificacionsProcessades;
	@Column(name = "num_error")
	private Integer notificacionsProcessadesAmbError;
	@Column(name = "num_cancelades")
	private Integer notificacionsCancelades;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat_id",
		referencedColumnName = "id",
		foreignKey = @javax.persistence.ForeignKey(name = BaseConfig.DB_PREFIX + "_MASSIVA_ENTITAT_FK"),
		nullable = false)
	private EntitatResourceEntity entitat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAGADOR_POSTAL_ID")
	@ForeignKey(name = "NOT_MASSIVA_PAGADOR_POSTAL_FK")
	private PagadorPostalResourceEntity pagadorPostal;

	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
	@ForeignKey(name = "NOT_NOTIF_NOTIF_MASSIVA_FK")
	@JoinColumn(name = "NOTIFICACIO_MASSIVA_ID") // we need to duplicate the physical information
	protected List<NotificacioResourceEntity> notificacions;



	public void updateEstatValidacio(Integer notificacionsValidades) {
		this.notificacionsValidades = notificacionsValidades;
		if (notificacionsValidades == 0) {
			this.estatValidacio = NotificacioMassivaEstatDto.ERRONIA;
		} else if (notificacionsValidades < totalNotificacions) {
			this.estatValidacio = NotificacioMassivaEstatDto.FINALITZAT_AMB_ERRORS;
		} else {
			this.estatValidacio = NotificacioMassivaEstatDto.FINALITZAT;
		}
	}

	public void updateProcessadaToError() {
		log.info("[PROCES MASSIU] updateProcessadaToError");
		this.notificacionsProcessadesAmbError++;
		this.notificacionsProcessades--;
		updateProgres();
	}

	public void updateErrorToProcessada() {
		log.info("[PROCES MASSIU] updateErrorToProcessada");
		this.notificacionsProcessades++;
		this.notificacionsProcessadesAmbError--;
		updateProgres();
	}

	public void updateToProcessada() {
		log.info("[PROCES MASSIU] updateToProcessada");
		this.notificacionsProcessades++;
		updateProgres();
	}

	public void updateCancelades() {
		log.info("[PROCES MASSIU] updateCancelades");
		if (notificacionsCancelades == null) {
			notificacionsCancelades = 0;
		}
		notificacionsCancelades++;
		updateProgres();
	}

	public void updateToError() {
		log.info("[PROCES MASSIU] updateToError");
		this.notificacionsProcessadesAmbError++;
		updateProgres();
	}

	private void updateProgres() {

		if (notificacionsValidades == null || notificacionsValidades == 0) {
			return;
		}
		this.progress = ((notificacionsProcessades + notificacionsProcessadesAmbError) * 100) / notificacionsValidades;
		log.info("[PROCES MASSIU] updateProgres (" + this.progress + ") - validades: " + notificacionsValidades + ", processades: " + notificacionsProcessades + ", error: " + notificacionsProcessadesAmbError);

		if (notificacionsCancelades != null && notificacionsCancelades == notificacions.size()) {
			this.estatProces = NotificacioMassivaEstatDto.CANCELADA;
			return;
		}

		if (notificacionsCancelades != null && notificacionsCancelades > 0) {
			this.estatProces = NotificacioMassivaEstatDto.FINALITZAT_PARCIAL;
			return;
		}

		if ((notificacionsProcessades + notificacionsProcessadesAmbError) == 0) {
			this.estatProces = NotificacioMassivaEstatDto.PENDENT;
		} else if ((notificacionsProcessades + notificacionsProcessadesAmbError) == notificacionsValidades) {
			if (notificacionsProcessadesAmbError > 0) {
				this.estatProces = NotificacioMassivaEstatDto.FINALITZAT_AMB_ERRORS;
			} else {
				this.estatProces = NotificacioMassivaEstatDto.FINALITZAT;
			}
		} else {
			if (notificacionsProcessadesAmbError > 0) {
				this.estatProces = NotificacioMassivaEstatDto.EN_PROCES_AMB_ERRORS;
			} else {
				this.estatProces = NotificacioMassivaEstatDto.EN_PROCES;
			}
		}
	}

	public void joinNotificacio(NotificacioResourceEntity notificacioResourceEntity) {

		if (notificacions == null){
			notificacions = new ArrayList<>();
		}
		notificacions.add(notificacioResourceEntity);
	}

	@Builder
	public NotificacioMassivaResourceEntity(NotificacioMassivaResource resource,
	                                        EntitatResourceEntity entitat,
	                                        PagadorPostalResourceEntity pagadorPostal,
	                                        List<NotificacioResourceEntity> notificacions) {

		this.csvFilename = resource.getCsvFilename();
		this.zipFilename = resource.getZipFilename();
		this.csvGesdocId = resource.getCsvGesdocId();
		this.zipGesdocId = resource.getZipGesdocId();
		this.resumGesdocId = resource.getResumGesdocId();
		this.errorsGesdocId = resource.getErrorsGesdocId();
		this.caducitat = resource.getCaducitat();
		this.email = resource.getEmail();
		this.estatValidacio = resource.getEstatValidacio();
		this.estatProces = resource.getEstatProces();
		this.totalNotificacions = resource.getTotalNotificacions();
		this.notificacionsValidades = resource.getNotificacionsValidades();
		this.notificacionsProcessades = resource.getNotificacionsProcessades();
		this.notificacionsProcessadesAmbError = resource.getNotificacionsProcessadesAmbError();
		this.notificacionsCancelades = resource.getNotificacionsCancelades();

		this.entitat = entitat;
		this.pagadorPostal = pagadorPostal;
		this.notificacions = notificacions;
	}

}
