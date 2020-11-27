package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProcedimentOrganDto extends AuditoriaDto implements Serializable{

	private Long id;
	private ProcedimentDto procediment;
	private OrganGestorDto organGestor;

	private static final long serialVersionUID = -696557578055790854L;
}
