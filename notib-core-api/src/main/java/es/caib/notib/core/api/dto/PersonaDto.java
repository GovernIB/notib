package es.caib.notib.core.api.dto;

import es.caib.notib.client.domini.DocumentTipusEnumDto;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class PersonaDto implements Serializable{

	private Long id;
	boolean incapacitat;
	private InteressatTipusEnumDto interessatTipus;
	private String nom;
	private String nomInput;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String raoSocialInput;
	private DocumentTipusEnumDto documentTipus;
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
		this.nom = raoSocialInput;
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
		StringBuilder sb = new StringBuilder();
		String llinatges = concatenarLlinatges();
		if (nif != null) {
			sb.append(nif);
			sb.append(" - ");
		}
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append("[");
			sb.append(llinatges);
		}

		if (nom != null && !nom.isEmpty()) {
			sb.append(", ");
			sb.append(nom);

			if (raoSocial == null) {
				sb.append("]");
			}
		}
		if (raoSocial != null && !raoSocial.isEmpty()) {
			sb.append(" | ");
			sb.append(raoSocial);
			sb.append("]");
		}
		return sb.toString();
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
