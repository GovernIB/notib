package es.caib.notib.logic.intf.dto.procediment;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorCacheDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProcSerOrganCacheDto extends AuditoriaDto implements Serializable{

	private Long id;
	private ProcSerCacheDto procSer;
	private OrganGestorCacheDto organGestor;

	private static final long serialVersionUID = -696557578055790854L;
}
