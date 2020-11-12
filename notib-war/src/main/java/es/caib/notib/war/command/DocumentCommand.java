/**
 * 
 */
package es.caib.notib.war.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DocumentCommand {

	private String id;
	private String arxiuNom;
	private String contingutBase64;
	private String mediaType;
	private Long mida;
	private String hash;
	private String url;
	private List<String> metadadesKeys = new ArrayList<String>();
	private List<String> metadadesValues = new ArrayList<String>();
	private boolean normalitzat;
	private boolean generarCsv;
	private String csv;
	private String uuid;
	private String arxiuGestdocId;
	
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
