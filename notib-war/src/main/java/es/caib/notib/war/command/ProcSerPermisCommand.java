package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class ProcSerPermisCommand {

    public enum EntitatPermis {
        ORGAN,
        PROCEDIMENT,
        SERVEI
    }

    private Long id;
    @NotEmpty @Size(max=100)
    private String principal;
    private TipusEnumDto tipus;
    @NotEmpty
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

    public static List<ProcSerPermisCommand> toPermisCommands(List<PermisDto> dtos) {

        List<ProcSerPermisCommand> commands = new ArrayList<>();
        for (PermisDto dto: dtos) {
            commands.add(ConversioTipusHelper.convertir(dto, ProcSerPermisCommand.class));
        }
        return commands;
    }

    public static ProcSerPermisCommand asCommand(PermisDto dto) {
        return ConversioTipusHelper.convertir(dto, ProcSerPermisCommand.class);
    }

    public static ProcSerPermisCommand asCommand(PermisDto dto, ProcSerPermisCommand.EntitatPermis entitatPermis) {

        ProcSerPermisCommand command = ConversioTipusHelper.convertir(dto, ProcSerPermisCommand.class);
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

    public static PermisDto asDto(ProcSerPermisCommand command) {
        return ConversioTipusHelper.convertir(command, PermisDto.class);
    }

    public int getPrincipalDefaultSize() {

        int principalSize = 0;
        try {
            Field principal = this.getClass().getDeclaredField("principal");
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
