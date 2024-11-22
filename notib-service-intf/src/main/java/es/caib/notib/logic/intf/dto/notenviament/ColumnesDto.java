package es.caib.notib.logic.intf.dto.notenviament;

import lombok.Data;
import java.io.Serializable;

@Data
public class ColumnesDto implements Serializable {

	private Long id;
	private boolean dataCreacio;
	private boolean dataEnviament;
	private boolean dataProgramada;
	private boolean notIdentificador;
	private boolean proCodi; 
	private boolean grupCodi; 
	private boolean dir3Codi; 
	private boolean usuari; 
	private boolean enviamentTipus; 
	private boolean concepte; 
	private boolean descripcio; 
	private boolean titularNomLlinatge;
	private boolean titularEmail;
	private boolean destinataris; 
	private boolean llibreRegistre; 
	private boolean numeroRegistre; 
	private boolean dataRegistre; 
	private boolean dataCaducitat; 
	private boolean codiNotibEnviament; 
	private boolean numCertificacio; 
	private boolean csvUuid; 
	private boolean estat;
	private boolean referenciaNotificacio;
	private boolean entregaPostal;

	private static final long serialVersionUID = 3836618098904965344L;
}
