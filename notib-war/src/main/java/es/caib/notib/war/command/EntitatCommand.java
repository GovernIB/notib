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
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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
	private String dir3Codi;
	@NotEmpty
	private String apiKey;
	private String dir3CodiReg;
	private boolean ambEntregaDeh;
	private boolean ambEntregaCie;
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
	@NotEmpty
	private String oficina;
	private String nomOficinaVirtual;
	private String llibre;
	private String llibreNom;
	
	public String getLlibreCodiNom() {
		if (llibre != null)
			return llibre + " - " + (llibreNom != null ? llibreNom : "");
		return "";
	}
	public void setLlibreCodiNom(String llibreCodiNom) {
		if (llibreCodiNom != null) {
			int div = llibreCodiNom.indexOf(" - ");
			if (div > 0) {
				this.llibre = llibreCodiNom.substring(0, div);
				this.llibreNom = llibreCodiNom.substring(div + 3);
			}
		}
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
		if (dto.getTipusDocDefault() != null && dto.getTipusDocDefault().getTipusDocEnum() != null) {
			entitat.setTipusDocDefault(dto.getTipusDocDefault().getTipusDocEnum().name());
		}
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
