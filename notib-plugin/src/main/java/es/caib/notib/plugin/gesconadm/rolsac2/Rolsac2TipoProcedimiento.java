package es.caib.notib.plugin.gesconadm.rolsac2;

import es.caib.notib.plugin.gesconadm.Link;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rolsac2TipoProcedimiento {

	private String descripcion;
	private Integer codigo;
	private Link link_entidad;
	private Boolean hateoasEnabled;
	private Integer entidad;
	private String identificador;
}
