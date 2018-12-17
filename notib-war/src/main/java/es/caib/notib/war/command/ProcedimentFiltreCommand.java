package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment del filtre de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentFiltreCommand {
	
	private String codi;
	private String nom;
	private String codisia;
	private Long entitatId;
	
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodisia() {
		return codisia;
	}
	public void setCodisia(String codisia) {
		this.codisia = codisia;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	
	public static ProcedimentFiltreCommand asCommand(ProcedimentFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		ProcedimentFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcedimentFiltreCommand.class );
		return command;
	}
	public static ProcedimentFiltreDto asDto(ProcedimentFiltreCommand command) {
		if (command == null) {
			return null;
		}
		ProcedimentFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				ProcedimentFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
