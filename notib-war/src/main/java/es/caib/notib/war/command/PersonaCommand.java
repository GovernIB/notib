package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.ValidDir3CodiIfAdm;
import es.caib.notib.war.validation.ValidIfVisible;
import es.caib.notib.war.validation.ValidLlinatgeIfFisic;
import es.caib.notib.war.validation.ValidNifIfFisic;

/**
 * Command per al manteniment de persones (Titulars | Destinataris).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@ValidIfVisible.List({
	@ValidIfVisible(
        fieldName = "visible",
        fieldValue = "true",
        dependFieldName = "nom")
})
@ValidLlinatgeIfFisic(
        fieldName = "interessatTipus",
        fieldName2 = "visible",
        fieldValue = "FISICA",
        fieldValue2 = "true",
        dependFieldName = "llinatge1"
)
@ValidNifIfFisic(
        fieldName = "interessatTipus",
        fieldName2 = "visible",
        fieldValue = "FISICA",
        fieldValue2 = "true",
        dependFieldName = "nif"
)
@ValidDir3CodiIfAdm(
        fieldName = "interessatTipus",
        fieldName2 = "visible",
        fieldValue = "ADMINISTRACIO",
        fieldValue2 = "true",
        dependFieldName = "dir3codi"
)
public class PersonaCommand {

	private boolean incapacitat;
	private InteressatTipusEnumDto interessatTipus;
	@Size(max=255)
	private String nom;
	@Size(max=40)	
	private String llinatge1;
	@Size(max=40)
	private String llinatge2;
	private String nif;
	private String telefon;
	@Size(max=255)
	private String email;
	private String dir3codi;
	private boolean visible = true;
	
	
	public boolean isIncapacitat() {
		return incapacitat;
	}
	public void setIncapacitat(boolean incapacitat) {
		this.incapacitat = incapacitat;
	}
	public InteressatTipusEnumDto getInteressatTipus() {
		return interessatTipus;
	}
	public void setInteressatTipus(InteressatTipusEnumDto interessatTipus) {
		this.interessatTipus = interessatTipus;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDir3codi() {
		return dir3codi;
	}
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}

	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public static PersonaCommand asCommand(PersonaDto dto) {
		if (dto == null) {
			return null;
		}
		PersonaCommand command = ConversioTipusHelper.convertir(
				dto,
				PersonaCommand.class );
		return command;
	}
	public static PersonaDto asDto(PersonaCommand command) {
		if (command == null) {
			return null;
		}
		PersonaDto dto = ConversioTipusHelper.convertir(
				command,
				PersonaDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
