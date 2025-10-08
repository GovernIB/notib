/**
 * 
 */
package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
	private boolean normalitzat;
	private boolean generarCsv;
	private String uuid;
	private String csv;
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private boolean modoFirma;
	
	public static DocumentCommand asCommand(Document dto) {

		return dto != null ? ConversioTipusHelper.convertir(dto, DocumentCommand.class ) : new DocumentCommand();
	}

	public static DocumentCommand asCommand(DocumentDto dto) {

		return dto != null ? ConversioTipusHelper.convertir(dto, DocumentCommand.class ) : new DocumentCommand();
	}

	public static Document asDto(DocumentCommand command) {

		return command == null || (
				(command.getArxiuGestdocId() == null || command.getArxiuGestdocId().isEmpty()) &&
				(command.getContingutBase64() == null || command.getContingutBase64().isEmpty()) &&
				(command.getUuid() == null || command.getUuid().isEmpty()) &&
				(command.getCsv() == null || command.getCsv().isEmpty()))
				? null : ConversioTipusHelper.convertir(command, Document.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
