package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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
                    formClass = BackGroundTaskResource.MassiveRestartTaskForm.class),
        })
public class BackGroundTaskResource extends BaseResource<String> {

	public static final String ACTION_RESTART_TASK	= "RESTART_TASK";

	private String nom;
	private String estat;
	private String tempsExecucio;
	private String dataInici;
	private String properaExecucio;
	private String observacions;

    @Getter
    @Setter
    public static class MassiveRestartTaskForm implements Serializable {
		@NotNull
        @NotEmpty
        private List<String> ids;
        private boolean massivo = false;
    }
}
