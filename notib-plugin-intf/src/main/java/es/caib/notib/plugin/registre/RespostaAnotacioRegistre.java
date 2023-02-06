package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RespostaAnotacioRegistre {
	
	private String numero;
	private String numeroRegistroFormateado;
	private Date data;
    private String errorCodi;
    private String errorDescripcio;
	
}
