package es.caib.notib.core.entity.auditoria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.audit.NotibAuditoria;
import lombok.Getter;

/**
 * Classe del model de dades que representa la informació de auditoria d'un Grup.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_grup_audit")
@EntityListeners(AuditingEntityListener.class)
public class GrupAudit extends NotibAuditoria<Long> {

	@Column(name = "grup_id")
	private Long grupId;
	@Column(name = "codi", length = 64)
	private String codi;
	@Column(name = "nom", length = 100)
	private String nom;
	@Column(name = "entitat_id")
	private Long entitatId;
	@Column(name = "organ", length = 64)
	private String organ;
	
	public static Builder getBuilder(
			GrupDto grupDto,
			TipusOperacio tipusOperacio, 
			String joinPoint) {
		return new Builder(
				grupDto,
				tipusOperacio,
				joinPoint);
	}
	
	public static class Builder {
		GrupAudit built;
		Builder(
				GrupDto grupDto,
				TipusOperacio tipusOperacio, 
				String joinPoint) {
			built = new GrupAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.grupId = grupDto.getId();
			built.entitatId = grupDto.getEntitatId();
			built.organ = grupDto.getOrganGestorCodi();
			built.codi = grupDto.getCodi();
			built.nom = grupDto.getNom();
		}
		public GrupAudit build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = -4924926921877674490L;

}
