package es.caib.notib.back.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SelectorHtml {

    private String value;
    private String text;
}
