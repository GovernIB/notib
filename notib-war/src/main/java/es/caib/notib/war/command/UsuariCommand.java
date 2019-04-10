/**
 * 
 */
package es.caib.notib.war.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.war.helper.ConversioTipusHelper;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String[] rols;
	private Boolean rebreEmailsNotificacio;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String[] getRols() {
		return rols;
	}
	public void setRols(String[] rols) {
		this.rols = rols;
	}
	public Boolean getRebreEmailsNotificacio() {
		return rebreEmailsNotificacio;
	}
	public void setRebreEmailsNotificacio(Boolean rebreEmailsNotificacio) {
		this.rebreEmailsNotificacio = rebreEmailsNotificacio;
	}
	
	public static UsuariCommand asCommand(UsuariDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				UsuariCommand.class);
	}
	public static UsuariDto asDto(UsuariCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				UsuariDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	private static final long serialVersionUID = -139254994389509932L;

}
