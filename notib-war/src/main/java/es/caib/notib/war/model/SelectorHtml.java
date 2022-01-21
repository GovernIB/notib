package es.caib.notib.war.model;

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
