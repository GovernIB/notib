package es.caib.notib.logic.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

//@Profile("!testNoSm")
@Component
public class SmTaskScheduler extends ThreadPoolTaskScheduler {

    public SmTaskScheduler() {
        super();
        setPoolSize(50);
        setThreadNamePrefix("notib-sm-task-pool");
    }

}
