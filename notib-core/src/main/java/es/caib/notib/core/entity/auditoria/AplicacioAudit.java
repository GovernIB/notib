/**
 * 
 */
package es.caib.notib.core.entity.auditoria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.audit.NotibAuditoria;
import lombok.Getter;

/**
 * Classe del model de dades que representa la informació de auditoria d'una aplicació amb accés a NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name="not_aplicacio_audit")
@EntityListeners(AuditingEntityListener.class)
public class AplicacioAudit extends NotibAuditoria<Long> {

	@Column(name = "aplicacio_id")
	private Long aplicacioId;
	@Column(name = "entitat_id")
	private Long entitatId;
	@Column(name = "usuari_codi", length = 64)
	private String usuariCodi;
	@Column(name = "callback_url", length = 256)
	private String callbackUrl;
	@Column(name = "activa")
	private boolean activa;
	
	public static Builder getBuilder(
			AplicacioDto aplicacioDto,
			TipusOperacio tipusOperacio, 
			String joinPoint) {
		return new Builder(
				aplicacioDto,
				tipusOperacio,
				joinPoint);
	}

	public static class Builder {
		AplicacioAudit built;
		Builder(
				AplicacioDto aplicacioDto,
				TipusOperacio tipusOperacio,
				String joinPoint) {
			built = new AplicacioAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.aplicacioId = aplicacioDto.getId();
			built.entitatId = aplicacioDto.getEntitatId();
			built.usuariCodi = aplicacioDto.getUsuariCodi();
			built.callbackUrl = aplicacioDto.getCallbackUrl();
			built.activa = aplicacioDto.isActiva();
		}
		public AplicacioAudit build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
