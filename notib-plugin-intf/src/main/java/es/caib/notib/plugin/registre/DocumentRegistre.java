package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació sobre un document que es registra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DocumentRegistre {

	private String nom;
	private Date data;
	private String idiomaCodi;
	private String arxiuNom;
	private byte[] arxiuContingut;
	
	private String tipusDocument;
	private String tipusDocumental;
	private Integer origen;
	private Integer modeFirma;
	private String observacions;
	private String csv;

}
