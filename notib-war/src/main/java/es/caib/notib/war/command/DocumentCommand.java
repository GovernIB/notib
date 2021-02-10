/**
 * 
 */
package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DocumentCommand {

	private String id;
	private String arxiuGestdocId;
	private String arxiuNom;
	private String mediaType;
	private Long mida;
	private String contingutBase64;
	private String hash;
	private String url;
	//	private List<String> metadadesKeys = new ArrayList<String>();
	//	private List<String> metadadesValues = new ArrayList<String>();
	private boolean normalitzat;
	private boolean generarCsv;
	private String uuid;
	private String csv;
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private boolean modoFirma;
	
	public static DocumentCommand asCommand(DocumentDto dto) {
		if (dto == null) {
			return new DocumentCommand();
		}
		DocumentCommand command = ConversioTipusHelper.convertir(
				dto,
				DocumentCommand.class );
		return command;
	}
	public static DocumentDto asDto(DocumentCommand command) {
		if (command == null || (
				(command.getArxiuGestdocId() == null || command.getArxiuGestdocId().isEmpty()) &&
				(command.getContingutBase64() == null || command.getContingutBase64().isEmpty()) &&
				(command.getUuid() == null || command.getUuid().isEmpty()) &&
				(command.getCsv() == null || command.getCsv().isEmpty()) &&
				(command.getUrl() == null || command.getUrl().isEmpty()))
		) {
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
