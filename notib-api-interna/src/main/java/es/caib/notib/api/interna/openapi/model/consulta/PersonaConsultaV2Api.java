package es.caib.notib.api.interna.openapi.model.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "PersonaConsultaV2")
public class PersonaConsultaV2Api {

	@Schema(name = "tipus", implementation = GenericInfoApi.class, description = "Tipus d'interessat")
	private GenericInfoApi tipus;
	@Schema(name = "nom", implementation = String.class, example = "codi", description = "Nom de la persona")
	private String nom;
	@Schema(name = "llinatge1", implementation = String.class, example = "codi", description = "Primer llinatge de la persona")
	private String llinatge1;
	@Schema(name = "llinatge2", implementation = String.class, example = "codi", description = "Segon llinatge de la persona")
	private String llinatge2;
	@Schema(name = "nif", implementation = String.class, example = "codi", description = "NIF de la persona")
	private String nif;
	@Schema(name = "email", implementation = String.class, example = "codi", description = "Correu electrònic de la persona")
	private String email;
	
}
