/**
 * 
 */
package es.caib.notib.war.command;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisCommand {

	private Long id;
	@NotEmpty
	private String principal;
	private TipusEnumDto tipus;
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
	private boolean notificacio;
	
	private boolean selectAll;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public TipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(TipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isWrite() {
		return write;
	}
	public void setWrite(boolean write) {
		this.write = write;
	}
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isAdministration() {
		return administration;
	}
	public void setAdministration(boolean administration) {
		this.administration = administration;
	}
	
	public boolean isUsuari() {
		return usuari;
	}
	public boolean isAdministrador() {
		return administrador;
	}
	public boolean isAdministradorEntitat() {
		return administradorEntitat;
	}
	public void setUsuari(boolean usuari) {
		this.usuari = usuari;
	}
	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}
	public void setAdministradorEntitat(boolean administradorEntitat) {
		this.administradorEntitat = administradorEntitat;
	}
	public boolean isAplicacio() {
		return aplicacio;
	}
	public void setAplicacio(boolean aplicacio) {
		this.aplicacio = aplicacio;
	}
	
	public boolean isProcessar() {
		return processar;
	}
	public boolean isNotificacio() {
		return notificacio;
	}
	public void setProcessar(boolean processar) {
		this.processar = processar;
	}
	public void setNotificacio(boolean notificacio) {
		this.notificacio = notificacio;
	}
	
	public boolean isSelectAll() {
		return selectAll;
	}
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
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
	public static PermisDto asDto(PermisCommand command) {
		PermisDto dto = ConversioTipusHelper.convertir(
				command,
				PermisDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
