package es.caib.notib.core.config;

import es.caib.notib.core.api.service.CallbackService;
import es.caib.notib.core.api.service.SchedulledService;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.PropertiesConstants;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
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
	

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	taskRegistrar.setScheduler(taskScheduler);

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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.REGISTRAR_ENVIAMENTS_PENDENTS_RATE), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long registrarEnviamentsPendentsInitialDelayLong = 0L; 
                        if (primeraVez[0]) {
                        	registrarEnviamentsPendentsInitialDelayLong = configHelper.getAsLong(PropertiesConstants.REGISTRAR_ENVIAMENTS_PENDENTS_INITIAL_DELAY);
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.NOTIFICA_ENVIAMENTS_REGISTRATS_RATE), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long notificaEnviamentsRegistratsInitialDelayLong = 0L;
                        if (primeraVez[1]) {
                        	notificaEnviamentsRegistratsInitialDelayLong = configHelper.getAsLong(PropertiesConstants.NOTIFICA_ENVIAMENTS_REGISTRATS_INITIAL_DELAY);
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_RATE), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[2]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = configHelper.getAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_INITIAL_DELAY);
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_RATE), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarEstatEnviatSirInitialDelayLong = 0L;
                        if (primeraVez[3]) {
                        	enviamentRefrescarEstatEnviatSirInitialDelayLong = configHelper.getAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_INITIAL_DELAY);
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
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ACTUALITZAR_PROCEDIMENTS_CRON));
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
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.REFRESCAR_NOTIFICACIONS_EXPIRADES_CRON));
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.PROCESSAR_PENDENTS_RATE), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long processarPendentsInitialDelayLong = 0L;
                        if (primeraVez[4]) {
                        	processarPendentsInitialDelayLong = configHelper.getAsLong(PropertiesConstants.PROCESSAR_PENDENTS_INITIAL_DELAY);
                        	primeraVez[4] = false;
                        }
                        trigger.setInitialDelay(processarPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        // 6. Consulta certificació notificacions DEH finalitzades
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_RATE), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                        if (primeraVez[5]) {
                        	enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY);
                        	primeraVez[5] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        // 7. Consulta certificació notificacions CIE finalitzades
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
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_RATE), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        Long enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                        if (primeraVez[6]) {
                        	enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY);
                        	primeraVez[6] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
    }
}
