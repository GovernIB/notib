package es.caib.notib.back.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Command per al manteniment d'enviaments de l'interfície rest.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class NotificacioEnviamentCommand {
	
	private Long id;
	private Date dataEnviament;
	private NotificacioCommand notificacio;
	private String codiNotifica;
	private String codiProcediment;
	private String usuari;
	private String nifTitular;
	private String nomTitular;
	private String emailTitular;
	private String destinataris;
	private String llibreRegistre;
	private String numeroRegistre;
	private Date dataRegistre;
	private Date dataCaducitat;
	private String codiNotib;
	private String numeroCertCorreus;
	private String csv;
	private String estat;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
