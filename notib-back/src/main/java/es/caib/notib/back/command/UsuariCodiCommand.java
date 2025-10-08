package es.caib.notib.back.command;

import es.caib.notib.back.validation.UsuariExists;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@UsuariExists
@Getter
@Setter
public class UsuariCodiCommand implements Serializable {

    @NotNull
    private String codiAntic;
    @NotNull
    private String codiNou;
}