package es.caib.notib.logic.intf.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Arxiu per descarregar.
 *
 * @author Limit Tecnologies
 */
@Builder
@Getter @Setter
@AllArgsConstructor
public class DownloadableFile {

	private String name;
	private String contentType;
	private byte[] content;

	public Long getContentLength() {
		return content != null ? Long.valueOf(content.length) : null;
	}

}
