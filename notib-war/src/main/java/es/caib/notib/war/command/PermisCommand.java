/**
 * 
 */
package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Command per al manteniment de permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PermisCommand {

	public enum EntitatPermis {
		ORGAN,
		PROCEDIMENT,
		SERVEI
	}


	private Long id;
	@NotEmpty @Size(max=100)
	private String principal;
	private TipusEnumDto tipus;
	private String organ;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;
	
	private boolean usuari;
	private boolean administrador;
	private boolean administradorEntitat;
	private boolean aplicacio;
	
	private boolean processar;

	private boolean comuns;

	private boolean selectAll;

	private boolean notificacio;
	private boolean comunicacio;
	private boolean comunicacioSir;
	private boolean comunicacioSenseProcediment;

	public static List<PermisCommand> toPermisCommands(
			List<PermisDto> dtos) {
		List<PermisCommand> commands = new ArrayList<PermisCommand>();
		for (PermisDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							PermisCommand.class));
		}
		return commands;
	}

	public static PermisCommand asCommand(PermisDto dto) {
		PermisCommand command = ConversioTipusHelper.convertir(
				dto,
				PermisCommand.class);
		return command;		
	}
	public static PermisCommand asCommand(PermisDto dto, EntitatPermis entitatPermis) {
		PermisCommand command = ConversioTipusHelper.convertir(
				dto,
				PermisCommand.class);
		switch (entitatPermis) {
			case ORGAN:
				command.setSelectAll(dto.isRead() && dto.isProcessar() && dto.isAdministration() && dto.isComuns() && dto.isNotificacio() && dto.isComunicacio() && dto.isComunicacioSir() && dto.isComunicacioSenseProcediment());
				break;
			case PROCEDIMENT:
			case SERVEI:
				command.setSelectAll(dto.isRead() && dto.isProcessar() && dto.isAdministration() && dto.isNotificacio() && dto.isComunicacio() && dto.isComunicacioSir());
				break;
		}
		return command;
	}
	public static PermisDto asDto(PermisCommand command) {
		PermisDto dto = ConversioTipusHelper.convertir(
				command,
				PermisDto.class);
		return dto;
	}

	public int getPrincipalDefaultSize() {
		int principalSize = 0;
		try {
			Field principal = this.getClass().getDeclaredField("principal");
			principalSize = principal.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud de principal: " + ex.getMessage());
		}
		return principalSize;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final Logger logger = LoggerFactory.getLogger(PermisCommand.class);
}
