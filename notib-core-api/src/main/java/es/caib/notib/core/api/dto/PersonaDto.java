package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

@Getter @Setter
public class PersonaDto implements Serializable{

	private Long id;
	boolean incapacitat;
	private InteressatTipusEnumDto interessatTipus;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String nif;
	private String telefon;
	private String email;
	private String dir3Codi;
	
	public String getLlinatges() {
		return llinatge1 + " " + llinatge2; 
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -8074332473505468212L;

}
