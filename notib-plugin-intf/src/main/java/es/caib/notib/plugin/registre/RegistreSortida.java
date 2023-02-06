package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Registre de sortida o notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreSortida {

	private String codiEntitat;
	private String codiUsuari;
	private DadesOficina dadesOficina;
	private List<DadesInteressat> dadesInteressat = new ArrayList<DadesInteressat>();
	private DadesAnotacio dadesAnotacio;
	private List<DocumentRegistre> documents;
	private String versioNotib;
	private String aplicacio;

}
