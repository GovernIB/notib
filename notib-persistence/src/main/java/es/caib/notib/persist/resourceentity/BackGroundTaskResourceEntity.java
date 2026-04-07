package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.BackGroundTaskResource;
import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BackGroundTaskResourceEntity implements ResourceEntity<BackGroundTaskResource, String> {

	private String id;
	private String codi;
	private MonitorTascaEstat estat;
	private Date dataInici;
	private Date dataFi;
	private Date properaExecucio;
	private String observacions;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return getId()==null;
	}
}
