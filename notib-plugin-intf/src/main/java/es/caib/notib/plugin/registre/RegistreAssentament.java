package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistreAssentament {

	private String organisme;
	private String oficina;
	private String llibre;
	private String extracte;
	private String assumpteTipus;
	private String assumpteCodi;
	private String idioma;
	private List<RegistreAssentamentInteressat> interessats;
	private DocumentRegistre_llorenc document;
	private String documentacioFisicaCodi;

	
}

