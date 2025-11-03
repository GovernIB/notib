/**
 * 
 */
package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.validation.EntitatValorsNoRepetits;
import es.caib.notib.logic.intf.util.MimeUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.converters.FileConverter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Getter @Setter
@EntitatValorsNoRepetits
public class EntitatCommand {

	private Long id;
	@NotEmpty
	@Size(max=64)
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
	private String logoCapNom;
	private boolean eliminarLogoCap;
	private MultipartFile logoPeu;
	private String logoPeuNom;
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

		return llibre != null ? llibre + " - " + (llibreNom != null ? llibreNom : "") : "";
	}
	public void setLlibreCodiNom(String llibreCodiNom) {

		if (llibreCodiNom == null) {
			return;
		}
		var div = llibreCodiNom.indexOf(" - ");
		if (div > 0) {
			this.llibre = llibreCodiNom.substring(0, div);
			this.llibreNom = llibreCodiNom.substring(div + 3);
		}
	}
	
	public static List<EntitatCommand> toEntitatCommands(List<EntitatDto> dtos) {

		List<EntitatCommand> commands = new ArrayList<>();
		for (var dto: dtos) {
			commands.add(ConversioTipusHelper.convertir(dto, EntitatCommand.class));
		}
		return commands;
	}

	public static EntitatCommand asCommand(EntitatDto dto) {

		var entitat = ConversioTipusHelper.convertir(dto, EntitatCommand.class);
		if (dto.getTipusDocDefault() != null && dto.getTipusDocDefault().getTipusDocEnum() != null) {
			entitat.setTipusDocDefault(dto.getTipusDocDefault().getTipusDocEnum().name());
		}

        if (dto.getLogoCapBytes() != null) {
            var base64 = Base64.getEncoder().encodeToString(dto.getLogoCapBytes());
            try {
                var nom = "logo_cap";
                var mime = MimeUtils.getMimeTypeFromBase64(base64, nom);
                var multipartFile = new MockMultipartFile(nom, nom, mime, dto.getLogoCapBytes());
                entitat.setLogoCap(multipartFile);
                entitat.setLogoCapNom(nom);
            } catch (Exception ex) {
                log.error("Error obtenint el mime type per el logo_cap");
            }
        }
        if (dto.getLogoPeuBytes() != null) {
            var base64 = Base64.getEncoder().encodeToString(dto.getLogoPeuBytes());
            try {
                var nom = "logo_peu";
                var mime = MimeUtils.getMimeTypeFromBase64(base64, nom);
                var multipartFile = new MockMultipartFile(nom, nom, mime, dto.getLogoPeuBytes());
                entitat.setLogoPeu(multipartFile);
                entitat.setLogoPeuNom(nom);
            } catch (Exception ex) {
                log.error("Error obtenint el mime type per el logo_cap");
            }
        }
		return entitat;
	}
	public EntitatDataDto asDto() throws IOException {

		var entitat = ConversioTipusHelper.convertir(this, EntitatDataDto.class);
		List<TipusDocumentDto> tipusDocuments = new ArrayList<>();
		var tipusDocumentDefault = new TipusDocumentDto();
		entitat.setLogoCapBytes(this.getLogoCap().getBytes());
		entitat.setLogoPeuBytes(this.getLogoPeu().getBytes());
		if (this.getTipusDocName() != null) {
			TipusDocumentEnumDto tipusDocumentEnum;
			TipusDocumentDto tipusDocument;
			for (var tipusDocumentStr : this.getTipusDocName()) {
				tipusDocumentEnum = TipusDocumentEnumDto.valueOf(tipusDocumentStr);
				tipusDocument = new TipusDocumentDto();
				tipusDocument.setTipusDocEnum(tipusDocumentEnum);
				tipusDocuments.add(tipusDocument);
			}
		}
		if (this.getTipusDocDefault() != null && !this.getTipusDocDefault().isEmpty()) {
			var tipusDocumentDefaultEnum = TipusDocumentEnumDto.valueOf(this.getTipusDocDefault());
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
