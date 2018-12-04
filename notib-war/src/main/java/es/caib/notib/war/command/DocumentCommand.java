/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.NotificaComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.ws.notificacio.Document;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentCommand {

	private String arxiuNom;
	private byte[] contingutBase64;
	private String hash;
	private String url;
	private List<String> metadades;
	private boolean normalitzat;
	private boolean generarCsv;
	private String uUID;
	private String cSV;
	
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public byte[] getContingutBase64() {
		return contingutBase64;
	}
	public void setContingutBase64(byte[] contingutBase64) {
		this.contingutBase64 = contingutBase64;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getMetadades() {
		return metadades;
	}
	public void setMetadades(List<String> metadades) {
		this.metadades = metadades;
	}
	public boolean isNormalitzat() {
		return normalitzat;
	}
	public void setNormalitzat(boolean normalitzat) {
		this.normalitzat = normalitzat;
	}
	public boolean isGenerarCsv() {
		return generarCsv;
	}
	public void setGenerarCsv(boolean generarCsv) {
		this.generarCsv = generarCsv;
	}
	public String getUUID() {
		return uUID;
	}
	public void setUUID(String uUID) {
		this.uUID = uUID;
	}
	public String getCSV() {
		return cSV;
	}
	public void setCSV(String cSV) {
		this.cSV = cSV;
	}
	
	public static DocumentCommand asCommand(DocumentDto dto) {
		if (dto == null) {
			return null;
		}
		DocumentCommand command = ConversioTipusHelper.convertir(
				dto,
				DocumentCommand.class );
		return command;
	}
	public static DocumentDto asDto(DocumentCommand command) {
		if (command == null) {
			return null;
		}
		DocumentDto dto = ConversioTipusHelper.convertir(
				command,
				DocumentDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
