package es.caib.notib.plugin.unitat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Informació d'un pais
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodiValorPais {

	private String alfa2Pais;
	private String alfa3Pais;
	private Long codiPais;
	private String descripcioPais;
	
}
