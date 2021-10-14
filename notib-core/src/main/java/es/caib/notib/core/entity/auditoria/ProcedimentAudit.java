package es.caib.notib.core.entity.auditoria;

import es.caib.notib.core.api.dto.procediment.ProcedimentDto;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.audit.NotibAuditoria;
import es.caib.notib.core.entity.ProcedimentEntity;
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
			ProcedimentEntity objecteAuditar,
			TipusOperacio tipusOperacio, 
			String joinPoint) {
		return new Builder(
				objecteAuditar,
				tipusOperacio,
				joinPoint);
	}
	
	public static Builder getBuilder(
			ProcedimentDto procedimentDto,
			TipusOperacio tipusOperacio,
			String joinPoint) {
		return new Builder(
				procedimentDto,
				tipusOperacio,
				joinPoint);
	}
	
	public static class Builder {
		ProcedimentAudit built;
		Builder(
				ProcedimentEntity procedimentEntity,
				TipusOperacio tipusOperacio,
				String joinPoint) {
			built = new ProcedimentAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.procedimentId = procedimentEntity.getId();
			built.entitatId = procedimentEntity.getEntitat() != null ? procedimentEntity.getEntitat().getId() : null;
			built.organ = procedimentEntity.getOrganGestor() != null ? procedimentEntity.getOrganGestor().getCodi() : null;
			built.codi = procedimentEntity.getCodi();
			built.nom = procedimentEntity.getNom();
			built.retard = procedimentEntity.getRetard();
			built.caducitat = procedimentEntity.getCaducitat();
			built.agrupar = procedimentEntity.isAgrupar();
			built.comu = procedimentEntity.isComu();
		}
		Builder(
				ProcedimentDto procedimentDto,
				TipusOperacio tipusOperacio, 
				String joinPoint) {
			built = new ProcedimentAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.procedimentId = procedimentDto.getId();
			built.entitatId = procedimentDto.getEntitat() != null ? procedimentDto.getEntitat().getId() : null;
			built.organ = procedimentDto.getOrganGestor();
			built.codi = procedimentDto.getCodi();
			built.nom = procedimentDto.getNom();
			built.retard = procedimentDto.getRetard();
			built.caducitat = procedimentDto.getCaducitat();
			built.agrupar = procedimentDto.isAgrupar();
			built.comu = procedimentDto.isComu();
//			built.pagadorpostalId = procedimentDto.getPagadorpostal() != null ? procedimentDto.getPagadorpostal().getId() : null;
//			built.pagadorcieId = procedimentDto.getPagadorcie() != null ? procedimentDto.getPagadorcie().getId() : null;
		}
		public ProcedimentAudit build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 458331024861203562L;

}
