package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
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
public class ProcSerCommand {
	
	private Long id;
	@NotEmpty @Size(max=9)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	private Long entitatId;
	private String entitatNom;
//	private Long pagadorPostalId;
//	private Long pagadorCieId;
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

	private boolean entregaCieActiva;
	private Long operadorPostalId;
	private Long cieId;

	public static ProcSerCommand asCommand(ProcSerDto dto) {
		if (dto == null) {
			return null;
		}
		ProcSerCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcSerCommand.class );
		if (dto.getEntitat() != null)
			command.setEntitatId(dto.getEntitat().getId());
		return command;
	}
	public static ProcSerDto asDto(ProcSerCommand command) {
		if (command == null) {
			return null;
		}
		ProcSerDto dto = ConversioTipusHelper.convertir(
				command,
				ProcSerDto.class);
		
		EntitatDto entitatDto = new EntitatDto();
		entitatDto.setId(command.getEntitatId());
		dto.setEntitat(entitatDto);

//		OperadorPostalDto pagadoPostalDto = null;
//		if (command.getPagadorPostalId() != null) {
//			pagadoPostalDto = new OperadorPostalDto();
//			pagadoPostalDto.setId(command.getPagadorPostalId());
//		}
//		dto.setPagadorpostal(pagadoPostalDto);
//
//		CieDto cieDto = null;
//		if (command.getPagadorCieId() != null) {
//			cieDto = new CieDto();
//			cieDto.setId(command.getPagadorCieId());
//		}
//		dto.setPagadorcie(cieDto);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
