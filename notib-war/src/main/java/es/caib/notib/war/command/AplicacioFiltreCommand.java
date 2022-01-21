package es.caib.notib.war.command;

import es.caib.notib.war.model.SelectorHtml;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AplicacioFiltreCommand {

    private String codiUsuari;
    private String callbackUrl;
    private String activa;

    private List<SelectorHtml> aplicacioEstats = new ArrayList<>();

    public AplicacioFiltreCommand() {
        SelectorHtml elem = SelectorHtml.builder().text("aplicacio.list.filtre.text.activa").value("1").build();
        aplicacioEstats.add(elem);
        elem = SelectorHtml.builder().text("aplicacio.list.filtre.text.inactiva").value("0").build();
        aplicacioEstats.add(elem);
    }
}
