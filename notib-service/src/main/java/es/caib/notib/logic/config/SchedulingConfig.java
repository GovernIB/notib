package es.caib.notib.logic.config;

import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.logic.intf.service.SchedulledService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.PropertiesConstants;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Autowired
    SchedulledService schedulledService;
    @Autowired
    CallbackService callbackService;
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
	private ConfigHelper configHelper;

    private Boolean[] primeraVez = {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
    private ScheduledTaskRegistrar taskRegistrar;

    public void restartSchedulledTasks() {
        if (taskRegistrar != null) {
            taskRegistrar.destroy();
            taskRegistrar.afterPropertiesSet();
            registerSchedulledTasks();
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	taskRegistrar.setScheduler(taskScheduler);
        this.taskRegistrar = taskRegistrar;

//        registerSchedulledTasks();
    }

    private void registerSchedulledTasks() {
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.REGISTRAR_ENVIAMENTS_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long registrarEnviamentsPendentsInitialDelayLong = 0L;
                        if (primeraVez[0]) {
                        	registrarEnviamentsPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.REGISTRAR_ENVIAMENTS_PENDENTS_INITIAL_DELAY, 300000L);
                        	primeraVez[0] = false;
                        }
                        trigger.setInitialDelay(registrarEnviamentsPendentsInitialDelayLong);
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.NOTIFICA_ENVIAMENTS_REGISTRATS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long notificaEnviamentsRegistratsInitialDelayLong = 0L;
                        if (primeraVez[1]) {
                        	notificaEnviamentsRegistratsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.NOTIFICA_ENVIAMENTS_REGISTRATS_INITIAL_DELAY, 330000L);
                        	primeraVez[1] = false;
                        }
                        trigger.setInitialDelay(notificaEnviamentsRegistratsInitialDelayLong);
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[2]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_INITIAL_DELAY, 360000L);
                        	primeraVez[2] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelayLong);
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarEstatEnviatSirInitialDelayLong = 0L;
                        if (primeraVez[3]) {
                        	enviamentRefrescarEstatEnviatSirInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_INITIAL_DELAY, 390000L);
                        	primeraVez[3] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatEnviatSirInitialDelayLong);
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
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ACTUALITZAR_PROCEDIMENTS_CRON, "0 0 1 * * *"));
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
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.REFRESCAR_NOTIFICACIONS_EXPIRADES_CRON, "0 30 1 * * *"));
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.PROCESSAR_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long processarPendentsInitialDelayLong = 0L;
                        if (primeraVez[4]) {
                        	processarPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.PROCESSAR_PENDENTS_INITIAL_DELAY, 420000L);
                        	primeraVez[4] = false;
                        }
                        trigger.setInitialDelay(processarPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 8. Consulta certificació notificacions DEH finalitzades
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.enviamentRefrescarEstatDEH();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                        if (primeraVez[5]) {
                        	enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY, 450000L);
                        	primeraVez[5] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 9. Consulta certificació notificacions CIE finalitzades
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.enviamentRefrescarEstatCIE();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                        if (primeraVez[6]) {
                        	enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY, 480000L);
                        	primeraVez[6] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 10. Eliminiar arxius temporals
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.eliminarDocumentsTemporals();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(24, TimeUnit.HOURS);
                        trigger.setFixedRate(true);
                        trigger.setInitialDelay(calcularDelay());
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 11. Actualització dels serveis a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.actualitzarServeis();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ACTUALITZAR_SERVEIS_CRON, "0 0 2 * * *"));
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 12. Consulta de canvis en l'organigrama
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulledService.consultaCanvisOrganigrama();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        String cron = configHelper.getConfig(PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA, "0 30 2 * * *");
                        CronTrigger trigger = new CronTrigger(cron);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
    }

    private long calcularDelay() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cal.setTimeInMillis(cal.getTimeInMillis() + 2*60*60*1000l);
        logger.info("EL TIMER S'EXECUTARÀ EL " + new Date(cal.getTimeInMillis()));
        return cal.getTimeInMillis() - now.getTime();
    }

    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
}
