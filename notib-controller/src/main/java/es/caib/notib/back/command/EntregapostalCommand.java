package es.caib.notib.back.command;

import es.caib.notib.client.domini.EntregaPostalVia;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.back.validation.ValidEntregaPostal;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

/**
 * Command per al manteniment de entregues postals
 * 	
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@ValidEntregaPostal
@Getter @Setter
public class EntregapostalCommand {

	private NotificaDomiciliConcretTipus domiciliConcretTipus = NotificaDomiciliConcretTipus.NACIONAL;
	private EntregaPostalVia viaTipus;
	@Size(max=50)
	private String viaNom;
	@Size(max=5)
	private String numeroCasa;
	@Size(max=3)
	private String numeroQualificador;
	@Size(max=20)
	private String puntKm;
	@Size(max=10)
	private String apartatCorreus;
	@Size(max=3)
	private String portal;
	@Size(max=3)
	private String escala;
	@Size(max=3)
	private String planta;
	@Size(max=3)
	private String porta;
	@Size(max=3)
	private String bloc;
	@Size(max=40)
	private String complement;
	@Size(max=10)
	private String codiPostal;
	@Size(max=10)
	private String codiPostalNorm;
	
	@Size(max=255)
	private String poblacio;
	@Size(max=6)
	private String municipiCodi;
	@Size(max=2)
	private String provincia;
	@Size(max=2)
	private String paisCodi;
	@Size(max=50)
	private String linea1;
	@Size(max=50)
	private String linea2;
	@Size(max=10)
	private String formatSobre;
	@Size(max=10)
	private String formatFulla;

	private boolean activa;
	
}
