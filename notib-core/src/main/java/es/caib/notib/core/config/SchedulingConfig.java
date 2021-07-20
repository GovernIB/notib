package es.caib.notib.core.config;

import es.caib.notib.core.api.service.CallbackService;
import es.caib.notib.core.api.service.SchedulledService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Autowired
    SchedulledService schedulledService;
    @Autowired
    CallbackService callbackService;

    // 1. Enviament de notificacions pendents al registre y notific@
    public static Long registrarEnviamentsPendentsRate = 120L;
    public static Long registrarEnviamentsPendentsInitialDelay = 10L;

    // 2. Enviament de notificacions registrades a Notific@
    public static Long notificaEnviamentsRegistratsRate = 120L;
    public static Long notificaEnviamentsRegistratsInitialDelay = 20L;

    // 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
    public static Long enviamentRefrescarEstatPendentsRate = 120L;
    public static Long enviamentRefrescarEstatPendentsInitialDelay = 30L;

    // 4. Actualització de l'estat dels enviaments amb l'estat de enviat_sir
    public static Long enviamentRefrescarEstatEnviatSirRate = 120L;
    public static Long enviamentRefrescarEstatEnviatSirInitialDelay = 40L;

    // 5. Actualització dels procediments a partir de la informació de Rolsac
    public static String actualitzarProcedimentsCron = "0 52 11 * * *";

    // 6. Refrescar notificacions expirades
    public static String refrescarNotificacionsExpiradesCron = "0 0 0 * * ?";

    // 7. Callback de client
    public static Long processarPendentsRate = 120L;
    public static Long processarPendentsInitialDelay = 50L;

    @Bean
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(20);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());

        // 1. Enviament de notificacions pendents al registre y notific@
        ////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.registrarEnviamentsPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(registrarEnviamentsPendentsRate, TimeUnit.SECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        trigger.setInitialDelay(registrarEnviamentsPendentsInitialDelay);
                        registrarEnviamentsPendentsInitialDelay = 0L;
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 2. Enviament de notificacions registrades a Notific@
        ///////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.notificaEnviamentsRegistrats();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(notificaEnviamentsRegistratsRate, TimeUnit.SECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        trigger.setInitialDelay(notificaEnviamentsRegistratsInitialDelay);
                        notificaEnviamentsRegistratsInitialDelay = 0L;
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
        //////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.enviamentRefrescarEstatPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(enviamentRefrescarEstatPendentsRate, TimeUnit.SECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelay);
                        enviamentRefrescarEstatPendentsInitialDelay = 0L;
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 4. Actualització de l'estat dels enviaments amb l'estat de enviat_sir
        //////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.enviamentRefrescarEstatEnviatSir();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(enviamentRefrescarEstatEnviatSirRate, TimeUnit.SECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        trigger.setInitialDelay(enviamentRefrescarEstatEnviatSirInitialDelay);
                        enviamentRefrescarEstatEnviatSirInitialDelay = 0L;
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 5. Actualització dels procediments a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.actualitzarProcediments();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = new CronTrigger(actualitzarProcedimentsCron);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 6. Refrescar notificacions expirades
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.refrescarNotificacionsExpirades();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = new CronTrigger(refrescarNotificacionsExpiradesCron);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 7. Callback de client
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        callbackService.processarPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(processarPendentsRate, TimeUnit.SECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        trigger.setInitialDelay(processarPendentsInitialDelay);
                        processarPendentsInitialDelay = 0L;
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
    }
}
