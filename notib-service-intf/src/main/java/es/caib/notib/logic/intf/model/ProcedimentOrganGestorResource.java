package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * Informació d'una relació procediment - òrgan gestor.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class ProcedimentOrganGestorResource extends BaseResource<Long> {

	private ResourceReference<ProcedimentResource, Long> procediment;
	private ResourceReference<OrganGestorResource, Long> organGestor;

}
