package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
public class IntegracioFiltreCommand {

    private String entitatCodi;
    private String aplicacio;

    public static IntegracioFiltreCommand asCommand(IntegracioFiltreDto dto) {
        return dto != null ? ConversioTipusHelper.convertir(dto, IntegracioFiltreCommand.class ) : null;
    }

    public IntegracioFiltreDto asDto() {
        return ConversioTipusHelper.convertir(this, IntegracioFiltreDto.class);
    }

    public static IntegracioFiltreCommand getFiltreCommand(HttpServletRequest request, String filtre) {

        IntegracioFiltreCommand command = (IntegracioFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, filtre);
        if (command == null) {
            command = new IntegracioFiltreCommand();
            RequestSessionHelper.actualitzarObjecteSessio(request, filtre, command);
        }
        return command;
    }
}
