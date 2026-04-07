package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.model.ThreadInfoResource;
import es.caib.notib.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.lang.management.LockInfo;

@Getter
@Setter
@NoArgsConstructor
public class ThreadInfoResourceEntity implements ResourceEntity<ThreadInfoResource, Long> {

    @Id
	private Long threadId;
    private String threadName;
    private long blockedTime;
    private long blockedCount;
    private long waitedTime;
    private long waitedCount;
    private LockInfo lock;
    private String lockName;
    private long lockOwnerId;
    private String lockOwnerName;
    private boolean daemon;
    private boolean inNative;
    private boolean suspended;
    private Thread.State threadState;
    private int priority;

	@Override
	public Long getId() {
		return this.threadId;
	}

	@Override
	public boolean isNew() {
		return getId()==null;
	}

	@Override
	public void setId(Long id) {
		threadId = id;
	}
}
