package es.caib.notib.logic.config;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.PropertiesConstants;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.intf.service.MonitorTasquesService;
import es.caib.notib.logic.intf.service.SchedulledService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
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
@Slf4j
public class SchedulingConfig implements SchedulingConfigurer {

    @Autowired
    SchedulledService schedulledService;
    @Autowired
    CallbackService callbackService;
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
	private ConfigHelper configHelper;
    @Autowired
    private MonitorTasquesService monitorTasquesService;

    private static Integer CALLBACK_CLIENT = 0;
    private static Integer CERT_DEH = 1;
    private static Integer CERT_CIE = 2;

    private static Boolean[] primeraVez = {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
    private ScheduledTaskRegistrar taskRegistrar;

    public void restartSchedulledTasks() {

        if (taskRegistrar != null) {
            taskRegistrar.destroy();
            taskRegistrar.afterPropertiesSet();
            registerSchedulledTasks();
        }
    }

    public void restartSchedulledTasksWithDelay() {

        if (taskRegistrar != null) {
            taskRegistrar.destroy();
            taskRegistrar.afterPropertiesSet();
            primeraVez = new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
            registerSchedulledTasks();
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	taskRegistrar.setScheduler(taskScheduler);
        this.taskRegistrar = taskRegistrar;
        registerSchedulledTasks();
    }


    private void registerSchedulledTasks() {

        // 1. Actualització dels procediments a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        final String actualitzarProcediments = "actualitzarProcediments";
        monitorTasquesService.addTasca(actualitzarProcediments);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(actualitzarProcediments);
                        schedulledService.actualitzarProcediments();
                        monitorTasquesService.fi(actualitzarProcediments);
                    } catch(Exception e) {
                        monitorTasquesService.error(actualitzarProcediments);
                    }
                },
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ACTUALITZAR_PROCEDIMENTS_CRON, "0 00 3 * * *"));
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(actualitzarProcediments, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(actualitzarProcediments);

        // 2. Refrescar notificacions expirades
        /////////////////////////////////////////////////////////////////////////
        final String refrescarNotificacionsExpirades = "refrescarNotificacionsExpirades";
        monitorTasquesService.addTasca(refrescarNotificacionsExpirades);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(refrescarNotificacionsExpirades);
                        schedulledService.refrescarNotificacionsExpirades();
                        monitorTasquesService.fi(refrescarNotificacionsExpirades);
                    } catch(Exception e) {
                        monitorTasquesService.error(refrescarNotificacionsExpirades);
                    }
                },
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.REFRESCAR_NOTIFICACIONS_EXPIRADES_CRON, "0 15 3 * * *"));
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(refrescarNotificacionsExpirades, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(refrescarNotificacionsExpirades);

        // 3. Callback de client
        /////////////////////////////////////////////////////////////////////////
        final String processarPendents = "processarPendents";
        monitorTasquesService.addTasca(processarPendents);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(processarPendents);
                        callbackService.processarPendents();
                        monitorTasquesService.fi(processarPendents);
                    } catch(Exception e) {
                        monitorTasquesService.error(processarPendents);
                    }
                },
                triggerContext -> {
                    PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.PROCESSAR_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                    trigger.setFixedRate(true);
                    // Només la primera vegada que s'executa
                    Long processarPendentsInitialDelayLong = 0L;
                    if (primeraVez[CALLBACK_CLIENT]) {
                        processarPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.PROCESSAR_PENDENTS_INITIAL_DELAY, 300000L);
                        primeraVez[CALLBACK_CLIENT] = false;
                    }
                    trigger.setInitialDelay(processarPendentsInitialDelayLong);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(processarPendents, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(processarPendents);

        // 4. Consulta certificació notificacions DEH finalitzades
        /////////////////////////////////////////////////////////////////////////
        final String enviamentRefrescarEstatDEH = "enviamentRefrescarEstatDEH";
        monitorTasquesService.addTasca(enviamentRefrescarEstatDEH);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(enviamentRefrescarEstatDEH);
                        schedulledService.enviamentRefrescarEstatDEH();
                        monitorTasquesService.fi(enviamentRefrescarEstatDEH);
                    } catch(Exception e) {
                        monitorTasquesService.error(enviamentRefrescarEstatDEH);
                    }
                },
                triggerContext -> {
                    PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                    trigger.setFixedRate(true);
                    // Només la primera vegada que s'executa
                    Long enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                    if (primeraVez[CERT_DEH]) {
                        enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY, 360000L);
                        primeraVez[CERT_DEH] = false;
                    }
                    trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(enviamentRefrescarEstatDEH, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(enviamentRefrescarEstatDEH);

        // 5. Consulta certificació notificacions CIE finalitzades
        /////////////////////////////////////////////////////////////////////////
        final String enviamentRefrescarEstatCIE = "enviamentRefrescarEstatCIE";
        monitorTasquesService.addTasca(enviamentRefrescarEstatCIE);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(enviamentRefrescarEstatCIE);
                        schedulledService.enviamentRefrescarEstatCIE();
                        monitorTasquesService.fi(enviamentRefrescarEstatCIE);
                    } catch(Exception e) {
                        monitorTasquesService.error(enviamentRefrescarEstatCIE);
                    }
                },
                triggerContext -> {
                    PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                    trigger.setFixedRate(true);
                    // Només la primera vegada que s'executa
                    Long enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                    if (primeraVez[CERT_CIE]) {
                        enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY, 420000L);
                        primeraVez[CERT_CIE] = false;
                    }
                    trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(enviamentRefrescarEstatCIE, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(enviamentRefrescarEstatCIE);

        // 6. Eliminiar arxius temporals
        /////////////////////////////////////////////////////////////////////////
        final String eliminarDocumentsTemporals = "eliminarDocumentsTemporals";
        monitorTasquesService.addTasca(eliminarDocumentsTemporals);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(eliminarDocumentsTemporals);
                        schedulledService.eliminarDocumentsTemporals();
                        monitorTasquesService.fi(eliminarDocumentsTemporals);
                    } catch(Exception e) {
                        monitorTasquesService.error(eliminarDocumentsTemporals);
                    }
                },
                triggerContext -> {
                    PeriodicTrigger trigger = new PeriodicTrigger(86400000L, TimeUnit.MILLISECONDS);
                    trigger.setFixedRate(true);
                    trigger.setInitialDelay(calcularDelay());
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(eliminarDocumentsTemporals, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(eliminarDocumentsTemporals);

        // 7. Actualització dels serveis a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        final String actualitzarServeis = "actualitzarServeis";
        monitorTasquesService.addTasca(actualitzarServeis);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(actualitzarServeis);
                        schedulledService.actualitzarServeis();
                        monitorTasquesService.fi(actualitzarServeis);
                    } catch(Exception e) {
                        monitorTasquesService.error(actualitzarServeis);
                    }
                },
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ACTUALITZAR_SERVEIS_CRON, "0 30 3 * * *"));
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(actualitzarServeis, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(actualitzarServeis);

        // 8. Consulta de canvis en l'organigrama
        /////////////////////////////////////////////////////////////////////////
        final String consultaCanvisOrganigrama = "consultaCanvisOrganigrama";
        monitorTasquesService.addTasca(consultaCanvisOrganigrama);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(consultaCanvisOrganigrama);
                        schedulledService.consultaCanvisOrganigrama();
                        monitorTasquesService.fi(consultaCanvisOrganigrama);
                    } catch(Exception e) {
                        monitorTasquesService.error(consultaCanvisOrganigrama);
                    }
                },
                triggerContext -> {
                    String cron = configHelper.getConfig(PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA);
                    if (cron == null)
                        cron = "0 45 2 * * *";
                    CronTrigger trigger = new CronTrigger(cron);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(consultaCanvisOrganigrama, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(consultaCanvisOrganigrama);

        // 9. Eliminar entrades al monitor integracions antigues
        /////////////////////////////////////////////////////////////////////////
        final String monitorIntegracionsEliminarAntics = "monitorIntegracionsEliminarAntics";
        monitorTasquesService.addTasca(monitorIntegracionsEliminarAntics);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(monitorIntegracionsEliminarAntics);
                        schedulledService.monitorIntegracionsEliminarAntics();
                        monitorTasquesService.fi(monitorIntegracionsEliminarAntics);
                    } catch(Exception e) {
                        monitorTasquesService.error(monitorIntegracionsEliminarAntics);
                    }
                },
                triggerContext -> {

                    String dies = configHelper.getConfig(PropertiesConstants.MONITOR_INTEGRACIONS_ELIMINAR_PERIODE_EXECUCIO, "0 30  1 * * *");
                    CronTrigger trigger = new CronTrigger(dies);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(monitorIntegracionsEliminarAntics, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(monitorIntegracionsEliminarAntics);


        // 10. Actualitzar estat organs enviament table
        /////////////////////////////////////////////////////////////////////////
        final String actualitzarEstatOrgansEnviamentTable = "actualitzarEstatOrgansEnviamentTable";
        monitorTasquesService.addTasca(actualitzarEstatOrgansEnviamentTable);
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        monitorTasquesService.inici(actualitzarEstatOrgansEnviamentTable);
                        schedulledService.actualitzarEstatOrgansEnviamentTable();
                        monitorTasquesService.fi(actualitzarEstatOrgansEnviamentTable);
                    } catch(Exception e) {
                        monitorTasquesService.error(actualitzarEstatOrgansEnviamentTable);
                    }
                },
                triggerContext -> {
                    String cron = "0 30 3 * * *";
                    CronTrigger trigger = new CronTrigger(cron);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(actualitzarEstatOrgansEnviamentTable, millis);
                    return nextExecution;
                }
        );
        monitorTasquesService.addTasca(actualitzarEstatOrgansEnviamentTable);

    }

    private long calcularDelay() {

        var cal = Calendar.getInstance();
        var now = new Date();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        log.info("EL TIMER S'EXECUTARÀ EL " + cal.getTime());
        return cal.getTimeInMillis() - now.getTime();
    }
}
