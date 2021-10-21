package es.caib.notib.core.entity.auditoria;

import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.audit.NotibAuditoria;
import es.caib.notib.core.entity.ProcSerEntity;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa la informació de auditoria d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_procediment_audit")
@EntityListeners(AuditingEntityListener.class)
public class ProcedimentAudit extends NotibAuditoria<Long> {
	
	@Column(name = "procediment_id")
	private Long procedimentId;
	@Column(name = "entitat_id")
	private Long entitatId;
	@Column(name = "organ", length = 64)
	private String organ;

	@Column(name = "codi", length = 64)
	protected String codi;
	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	
	@Column(name = "retard")
	protected Integer retard;
	@Column(name = "caducitat")
	protected Integer caducitat;
	@Column(name = "agrupar")
	protected boolean agrupar;
	@Column(name = "comu")
	protected boolean comu;

	@Column(name = "pagadorpostal_id")
	protected Long pagadorpostalId;
	@Column(name = "pagadorcie_id")
	protected Long pagadorcieId;
	
	public static Builder getBuilder(
			ProcSerEntity objecteAuditar,
			TipusOperacio tipusOperacio, 
			String joinPoint) {
		return new Builder(
				objecteAuditar,
				tipusOperacio,
				joinPoint);
	}
	
	public static Builder getBuilder(
			ProcSerDto procSerDto,
			TipusOperacio tipusOperacio,
			String joinPoint) {
		return new Builder(
				procSerDto,
				tipusOperacio,
				joinPoint);
	}

	public static class Builder {
		ProcedimentAudit built;
		Builder(
				ProcSerEntity procSerEntity,
				TipusOperacio tipusOperacio,
				String joinPoint) {
			built = new ProcedimentAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.procedimentId = procSerEntity.getId();
			built.entitatId = procSerEntity.getEntitat() != null ? procSerEntity.getEntitat().getId() : null;
			built.organ = procSerEntity.getOrganGestor() != null ? procSerEntity.getOrganGestor().getCodi() : null;
			built.codi = procSerEntity.getCodi();
			built.nom = procSerEntity.getNom();
			built.retard = procSerEntity.getRetard();
			built.caducitat = procSerEntity.getCaducitat();
			built.agrupar = procSerEntity.isAgrupar();
			built.comu = procSerEntity.isComu();
		}
		Builder(
				ProcSerDto procSerDto,
				TipusOperacio tipusOperacio, 
				String joinPoint) {
			built = new ProcedimentAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.procedimentId = procSerDto.getId();
			built.entitatId = procSerDto.getEntitat() != null ? procSerDto.getEntitat().getId() : null;
			built.organ = procSerDto.getOrganGestor();
			built.codi = procSerDto.getCodi();
			built.nom = procSerDto.getNom();
			built.retard = procSerDto.getRetard();
			built.caducitat = procSerDto.getCaducitat();
			built.agrupar = procSerDto.isAgrupar();
			built.comu = procSerDto.isComu();
//			built.pagadorpostalId = procedimentDto.getPagadorpostal() != null ? procedimentDto.getPagadorpostal().getId() : null;
//			built.pagadorcieId = procedimentDto.getPagadorcie() != null ? procedimentDto.getPagadorcie().getId() : null;
		}
		public ProcedimentAudit build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 458331024861203562L;

}
