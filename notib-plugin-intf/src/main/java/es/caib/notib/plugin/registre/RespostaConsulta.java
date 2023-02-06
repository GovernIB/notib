package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Resposta a una consulta de registre d'entrada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RespostaConsulta extends RespostaBase {

	private String registreNumero;
	private Date registreData;
	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesAnotacio dadesAssumpte;
	private List<DocumentRegistre_llorenc> documents;

}
