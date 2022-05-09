/**
 * 
 */
package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.EntitatValorsNoRepetits;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private String dir3CodiReg;
	@NotEmpty
	private String apiKey;
	private boolean ambEntregaDeh;
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

	private boolean entregaCieActiva;
	private Long operadorPostalId;
	private Long cieId;

	private boolean llibreEntitat;
	private String llibre;
	private String llibreNom;
	
	private boolean oficinaEntitat;
	private String oficina;
	private String nomOficinaVirtual;

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
	public EntitatDataDto asDto() throws IOException {
		EntitatDataDto entitat = ConversioTipusHelper.convertir(
				this,
				EntitatDataDto.class);
		List<TipusDocumentDto> tipusDocuments = new ArrayList<TipusDocumentDto>();
		TipusDocumentDto tipusDocumentDefault = new TipusDocumentDto();
		entitat.setLogoCapBytes(this.getLogoCap().getBytes());
		entitat.setLogoPeuBytes(this.getLogoPeu().getBytes());
		
		if (this.getTipusDocName() != null) {
			for (String tipusDocumentStr : this.getTipusDocName()) {
				TipusDocumentEnumDto tipusDocumentEnum = TipusDocumentEnumDto.valueOf(tipusDocumentStr);
				TipusDocumentDto tipusDocument = new TipusDocumentDto();
				tipusDocument.setTipusDocEnum(tipusDocumentEnum);
				tipusDocuments.add(tipusDocument);
			}
		}
		if (this.getTipusDocDefault() != null && !this.getTipusDocDefault().isEmpty()) {
			TipusDocumentEnumDto tipusDocumentDefaultEnum = TipusDocumentEnumDto.valueOf(this.getTipusDocDefault());
			tipusDocumentDefault.setTipusDocEnum(tipusDocumentDefaultEnum);
		}
		entitat.setTipusDocDefault(tipusDocumentDefault);
		entitat.setTipusDoc(tipusDocuments);
		
		if (!this.isLlibreEntitat()) {
			entitat.setLlibre(null);
			entitat.setLlibreNom(null);
		}
		return entitat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
