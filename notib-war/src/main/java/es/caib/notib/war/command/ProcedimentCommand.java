package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.ValidProcediment;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Getter @Setter
@ValidProcediment
public class ProcedimentCommand {
	
	private Long id;
	@NotEmpty @Size(max=9)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	private Long entitatId;
	private String entitatNom;
	private Long pagadorPostalId;
	private Long pagadorCieId;	
	private boolean agrupar;
	private boolean consulta;
	private boolean processar;
	private boolean notificacio;
	private boolean gestio;
	private int retard;
	@NotEmpty
	private String organGestor;
	@NotEmpty
	private String organGestorNom;
	private String tipusAssumpte;
	private String tipusAssumpteNom;
	private String codiAssumpte;
	private String codiAssumpteNom;
	private int caducitat;
	private boolean comu;
	protected boolean requireDirectPermission;

	public static ProcedimentCommand asCommand(ProcedimentDto dto) {
		if (dto == null) {
			return null;
		}
		ProcedimentCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcedimentCommand.class );
		return command;
	}
	public static ProcedimentDto asDto(ProcedimentCommand command) {
		if (command == null) {
			return null;
		}
		ProcedimentDto dto = ConversioTipusHelper.convertir(
				command,
				ProcedimentDto.class);
		
		EntitatDto entitatDto = new EntitatDto();
		entitatDto.setId(command.getEntitatId());
		dto.setEntitat(entitatDto);

		PagadorPostalDto pagadoPostalDto = null;
		if (command.getPagadorPostalId() != null) {
			pagadoPostalDto = new PagadorPostalDto();
			pagadoPostalDto.setId(command.getPagadorPostalId());
		}
		dto.setPagadorpostal(pagadoPostalDto);

		PagadorCieDto pagadorCieDto = null;
		if (command.getPagadorCieId() != null) {
			pagadorCieDto = new PagadorCieDto();
			pagadorCieDto.setId(command.getPagadorCieId());
		}
		dto.setPagadorcie(pagadorCieDto);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
