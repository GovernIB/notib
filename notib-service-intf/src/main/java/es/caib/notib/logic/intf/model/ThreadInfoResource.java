package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.lang.management.LockInfo;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	quickFilterFields = { "threadName" },
	descriptionField = "threadName",
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = ThreadInfoResource.ACTION_SYSTEM_INFO),
	})
public class ThreadInfoResource extends BaseResource<Long> {

	public static final String ACTION_SYSTEM_INFO	= "SYSTEM_INFO";

	private Long threadId;
	private String threadName;
	private String threadState;
	private String tiempoCPU;
	private int prioritat;
	private boolean suspended;
	private LockInfo lock;
	private String waitedTime;
	private String blockedTime;

	@Override
	public Long getId() {
		return this.threadId;
	}

	@Override
	public void setId(Long id) {
		this.threadId = id;
	}
}
