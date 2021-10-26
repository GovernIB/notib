package es.caib.notib.core.api.dto.procediment;

import es.caib.notib.core.api.dto.AuditoriaDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ProcSerOrganDto extends AuditoriaDto implements Serializable{

	private Long id;
	private ProcSerSimpleDto procSer;
	private OrganGestorDto organGestor;

	private static final long serialVersionUID = -696557578055790854L;
}
