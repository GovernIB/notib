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
	private boolean eliminarLogoCap;
	private MultipartFile logoPeu;
	private boolean eliminarLogoPeu;
	private String colorFons;
	private String colorLletra;
	@NotEmpty
	private String[] tipusDocName;
	private String tipusDocDefault;
	private String tipusDocDefaultSelected;
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
	public boolean isEliminarLogoCap() {
		return eliminarLogoCap;
	}
	public void setEliminarLogoCap(boolean eliminarLogoCap) {
		this.eliminarLogoCap = eliminarLogoCap;
	}
	public boolean isEliminarLogoPeu() {
		return eliminarLogoPeu;
	}
	public void setEliminarLogoPeu(boolean eliminarLogoPeu) {
		this.eliminarLogoPeu = eliminarLogoPeu;
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
	public String getTipusDocDefault() {
		return tipusDocDefault;
	}
	public void setTipusDocDefault(String tipusDocDefault) {
		this.tipusDocDefault = tipusDocDefault;
	}
	public String getTipusDocDefaultSelected() {
		return tipusDocDefaultSelected;
	}
	public void setTipusDocDefaultSelected(String tipusDocDefaultSelected) {
		this.tipusDocDefaultSelected = tipusDocDefaultSelected;
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
		EntitatCommand entitat = ConversioTipusHelper.convertir(
				dto,
				EntitatCommand.class);
		if (dto.getTipusDocDefault().getTipusDocEnum() != null)
			entitat.setTipusDocDefault(dto.getTipusDocDefault().getTipusDocEnum().name());
		return entitat;
	}
	public static EntitatDto asDto(EntitatCommand command) throws IOException {
		EntitatDto entitat = ConversioTipusHelper.convertir(
				command,
				EntitatDto.class);
		List<TipusDocumentDto> tipusDocuments = new ArrayList<TipusDocumentDto>();
		TipusDocumentDto tipusDocumentDefault = new TipusDocumentDto();
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
		if (command.getTipusDocDefault() != null && !command.getTipusDocDefault().isEmpty()) {
			TipusDocumentEnumDto tipusDocumentDefaultEnum = TipusDocumentEnumDto.valueOf(command.getTipusDocDefault());
			tipusDocumentDefault.setTipusDocEnum(tipusDocumentDefaultEnum);
		}
		entitat.setTipusDocDefault(tipusDocumentDefault);
		entitat.setTipusDoc(tipusDocuments);
		return entitat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
