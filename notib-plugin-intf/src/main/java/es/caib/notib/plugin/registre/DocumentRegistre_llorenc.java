package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DocumentRegistre_llorenc {
	
	private String titol;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private int arxiuMida;
	private String tipusMIMEFitxerAnexat;
	private String tipusDocumental;
	private int origenCiutadaAdmin;
	private Date dataCaptura;
}
