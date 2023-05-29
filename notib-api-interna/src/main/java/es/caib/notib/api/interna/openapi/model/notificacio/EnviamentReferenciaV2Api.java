/**
 * 
 */
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Informació de referència d'un enviament retornada per Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "EnviamentReferenciaV2")
public class EnviamentReferenciaV2Api {

	@Schema(name = "titularNom", implementation = String.class, example = "Pep Riera",
			description = "Nom del titular a qui va adreçat l'enviament")
	private String titularNom;
	@Schema(name = "titularNif", implementation = String.class, example = "00000000T",
			description = "Nif del titular a qui va adreçat l'enviament")
	private String titularNif;
	@Schema(name = "titularEmail", implementation = String.class, example = "titular@correu.es",
			description = "Correu electrònic del titular a qui va adreçat l'enviament")
	private String titularEmail;
	@Schema(name = "referencia", implementation = String.class, example = "00000000-0000-0000-0000-000000000000",
			description = "Referència (identificador) única de l'enviament a Notib")
	private String referencia;

}
