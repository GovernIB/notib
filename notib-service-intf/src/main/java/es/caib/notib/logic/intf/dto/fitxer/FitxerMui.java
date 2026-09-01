package es.caib.notib.logic.intf.dto.fitxer;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class FitxerMui implements Serializable {

	private String content;
	private Long contentLength;
	private String contentType;
	private String name;
}
