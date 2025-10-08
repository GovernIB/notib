package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
public class IntegracioFiltreCommand extends FiltreCommand {

    private String entitatCodi;
    private String aplicacio;
    private String dataInici;
    private String dataFi;
    private String descripcio;
    private IntegracioAccioTipusEnumDto tipus;
    private IntegracioAccioEstatEnumDto estat;

    public static IntegracioFiltreCommand asCommand(IntegracioFiltreDto dto) {
        return dto != null ? ConversioTipusHelper.convertir(dto, IntegracioFiltreCommand.class ) : null;
    }

    public IntegracioFiltreDto asDto() {
        return ConversioTipusHelper.convertir(this, IntegracioFiltreDto.class);
    }

    public static IntegracioFiltreCommand getFiltreCommand(HttpServletRequest request, String filtre) {

        var command = (IntegracioFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, filtre);
        if (command == null) {
            command = new IntegracioFiltreCommand();
            RequestSessionHelper.actualitzarObjecteSessio(request, filtre, command);
        }
        return command;
    }
}
