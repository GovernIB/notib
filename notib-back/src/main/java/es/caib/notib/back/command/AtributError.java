package es.caib.notib.back.command;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtributError {

    private String atribut;
    private String error;
}
