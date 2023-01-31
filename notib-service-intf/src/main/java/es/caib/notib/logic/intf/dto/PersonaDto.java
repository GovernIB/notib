package es.caib.notib.logic.intf.dto;

import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class PersonaDto implements Serializable{

	private Long id;
	boolean incapacitat;
	private InteressatTipus interessatTipus;
	private String nom;
	private String nomInput;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String raoSocialInput;
	private DocumentTipus documentTipus;
	private String nif;
	private String telefon;
	private String email;
	private String dir3Codi;

	public void setNomInput(String nomInput) {
		this.nomInput = nomInput;
		this.nom = nomInput;
	}

	public void setRaoSocialInput(String raoSocialInput) {
		this.raoSocialInput = raoSocialInput;
		if (InteressatTipus.JURIDICA.equals(interessatTipus) || InteressatTipus.ADMINISTRACIO.equals(interessatTipus)) {
			this.nom = raoSocialInput;
		}
	}
	
	public String getLlinatges() {
		return concatenarLlinatges() != null ? concatenarLlinatges() : "";
	}

	public String getNif() {
		return nif != null && Character.isDigit(nif.charAt(0)) && nif.length() < 9 ? afegirZerosNif() : nif;
	}

	private String afegirZerosNif() {

		int length = 9 - nif.length();
		for (int foo = 0; foo < length; foo++) {
			nif = 0 + nif;
		}
		return nif;
	}

	public String getNomFormatted() {

		var llinatges = concatenarLlinatges();
		var formatted = "";
		if (raoSocial == null || raoSocial.isEmpty()) {
			formatted = nom != null ? nom : "";
			formatted += llinatges != null && !llinatges.isEmpty() ? " " + llinatges : "";
		} else {
			formatted += raoSocial != null && !raoSocial.isEmpty() ? raoSocial : "";
		}
		formatted += nif != null && !nif.isEmpty() ? " (" + nif + ")" : "";
		return formatted;
	}


	public String concatenarLlinatges() {
		if (llinatge1 == null && llinatge2 == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(llinatge1);
		if (llinatge2 != null && !llinatge2.isEmpty()) {
			sb.append(" ");
			sb.append(llinatge2);
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -8074332473505468212L;

}
