package es.caib.notib.logic.intf.dto.organisme;

import es.caib.notib.logic.intf.model.OrganGestorResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@Setter
@Getter
public class OrgansAmbPermis implements Serializable {

	private List<OrganGestorResource> organs;
}
