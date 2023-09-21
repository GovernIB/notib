package es.caib.notib.logic.config;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

//@Profile("!testNoSm")
@Component
public class SmTaskScheduler extends ThreadPoolTaskScheduler {

    public SmTaskScheduler() {
        super();
        setPoolSize(10);
        setThreadNamePrefix("notib-sm-task-pool");
    }

}
