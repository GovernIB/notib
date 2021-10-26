package es.caib.notib.core.entity.auditoria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.procediment.ProcSerGrupDto;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.audit.NotibAuditoria;
import lombok.Getter;

/**
 * Classe del model de dades que representa la informació de auditoria de la relació Procediment-Grup.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_pro_grup_audit")
@EntityListeners(AuditingEntityListener.class)
public class GrupProcedimentAudit extends NotibAuditoria<Long> {

	@Column(name = "progrup_id")
	private Long procedimentGrupId;
	@Column(name = "procediment", length = 64)
	private String procediment;
	@Column(name = "grup", length = 64)
	private String grup;
	
	public static Builder getBuilder(
			ProcSerGrupDto procedimentGrupDto,
			TipusOperacio tipusOperacio, 
			String joinPoint) {
		return new Builder(
				procedimentGrupDto,
				tipusOperacio,
				joinPoint);
	}
	
	public static class Builder {
		GrupProcedimentAudit built;
		Builder(
				ProcSerGrupDto procedimentGrupDto,
				TipusOperacio tipusOperacio, 
				String joinPoint) {
			built = new GrupProcedimentAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.procedimentGrupId = procedimentGrupDto.getId();
			built.procediment = procedimentGrupDto.getProcSer() != null ? procedimentGrupDto.getProcSer().getCodi() : null;
			built.grup = procedimentGrupDto.getGrup() != null ? procedimentGrupDto.getGrup().getCodi() : null;
		}
		public GrupProcedimentAudit build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = -4924926921877674490L;

}
