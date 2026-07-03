package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
    quickFilterFields = { "nom", "observacions" },
    descriptionField = "nom",
    artifacts = {
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = BackGroundTaskResource.ACTION_RESTART_TASK,
                    formClass = SeleccioStringForm.class),
        })
public class BackGroundTaskResource extends BaseResource<String> {

	public static final String ACTION_RESTART_TASK	= "RESTART_TASK";

	private String nom;
	private String estat;
	private String tempsExecucio;
	private String dataInici;
	private String properaExecucio;
	private String observacions;

}
