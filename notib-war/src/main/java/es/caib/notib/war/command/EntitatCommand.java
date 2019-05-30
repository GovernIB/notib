/**
 * 
 */
package es.caib.notib.war.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.EntitatValorsNoRepetits;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntitatValorsNoRepetits
public class EntitatCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotNull 
	private EntitatTipusEnumDto tipus;
	@NotEmpty
	@Size(max=9)
//	@DocumentIdentitat
	private String dir3Codi;
	@NotEmpty
	private String apiKey;
	private String descripcio;
	private MultipartFile logoCap;
	private MultipartFile logoPeu;
	private String colorFons;
	private String colorLletra;
	private String[] tipusDocName;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public EntitatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(EntitatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public MultipartFile getLogoCap() {
		return logoCap;
	}
	public void setLogoCap(MultipartFile logoCap) {
		this.logoCap = logoCap;
	}
	public MultipartFile getLogoPeu() {
		return logoPeu;
	}
	public void setLogoPeu(MultipartFile logoPeu) {
		this.logoPeu = logoPeu;
	}
	public String getColorFons() {
		return colorFons;
	}
	public void setColorFons(String colorFons) {
		this.colorFons = colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public void setColorLletra(String colorLletra) {
		this.colorLletra = colorLletra;
	}
	public String[] getTipusDocName() {
		return tipusDocName;
	}
	public void setTipusDocName(String[] tipusDocName) {
		this.tipusDocName = tipusDocName;
	}
	public static List<EntitatCommand> toEntitatCommands(
			List<EntitatDto> dtos) {
		List<EntitatCommand> commands = new ArrayList<EntitatCommand>();
		for (EntitatDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							EntitatCommand.class));
		}
		return commands;
	}

	public static EntitatCommand asCommand(EntitatDto dto) {	
		return ConversioTipusHelper.convertir(
				dto,
				EntitatCommand.class);
	}
	public static EntitatDto asDto(EntitatCommand command) throws IOException {
		EntitatDto entitat = ConversioTipusHelper.convertir(
				command,
				EntitatDto.class);
		List<TipusDocumentDto> tipusDocuments = new ArrayList<TipusDocumentDto>();
		entitat.setLogoCapBytes(command.getLogoCap().getBytes());
		entitat.setLogoPeuBytes(command.getLogoPeu().getBytes());
		
		if (command.getTipusDocName() != null) {
			for (String tipusDocumentStr : command.getTipusDocName()) {
				TipusDocumentEnumDto tipusDocumentEnum = TipusDocumentEnumDto.valueOf(tipusDocumentStr);
				TipusDocumentDto tipusDocument = new TipusDocumentDto();
				tipusDocument.setTipusDocEnum(tipusDocumentEnum);
				tipusDocuments.add(tipusDocument);
			}
		}
		entitat.setTipusDoc(tipusDocuments);
		return entitat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
