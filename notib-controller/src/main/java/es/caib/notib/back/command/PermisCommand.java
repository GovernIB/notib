/**
 * 
 */
package es.caib.notib.back.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Getter @Setter
public class PermisCommand {

	private Long id;
	@NotEmpty
	@Size(max=100)
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

	public static List<PermisCommand> toPermisCommands(List<PermisDto> dtos) {

		List<PermisCommand> commands = new ArrayList<PermisCommand>();
		for (var dto: dtos) {
			commands.add(ConversioTipusHelper.convertir(dto, PermisCommand.class));
		}
		return commands;
	}

	public static PermisCommand asCommand(PermisDto dto) {
		return ConversioTipusHelper.convertir(dto, PermisCommand.class);
	}

	public static PermisDto asDto(PermisCommand command) {
		return ConversioTipusHelper.convertir(command, PermisDto.class);
	}

	public int getPrincipalDefaultSize() {

		int principalSize = 0;
		try {
			var principal = this.getClass().getDeclaredField("principal");
			principalSize = principal.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			log.error("No s'ha pogut recuperar la longitud de principal: " + ex.getMessage());
		}
		return principalSize;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
