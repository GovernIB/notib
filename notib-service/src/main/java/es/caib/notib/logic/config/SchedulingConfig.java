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
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    private static String ACTUALIZAR_PROCEDIM_DEFCRON = "0 30 1 * * *";
    private static String ACTUALITZAR_SERVEIS_DEFCRON = "0 00 2 * * *";
    private static String CONS_CANVIS_ORGANIG_DEFCRON = "0 30 2 * * *";
    private static String ACTUAL_ESTAT_ORGANS_DEFCRON = "0 00 3 * * *";
    private static String REFRESCAR_NOT_EXPIR_DEFCRON = "0 30 3 * * *";
    private static String MONITOR_BUIDA_DADES_DEFCRON = "0 30 4 * * *";

    private static Integer CALLBACK_CLIENT = 0;
    private static Integer CERT_DEH = 1;
    private static Integer CERT_CIE = 2;
    private static Integer ARXIUS_TMP = 3;

    private static Boolean[] primeraVez = {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
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
        configHelper.schedullingLoadDbProperties();
        registerSchedulledTasks();
    }


    private void registerSchedulledTasks() {

        Supplier<SchedulledService> schedulledServiceSupplier = () -> schedulledService;
        Supplier<CallbackService> callbackServiceSupplier = () -> callbackService;

        // 1. Actualització dels procediments a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "actualitzarProcediments",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().actualitzarProcediments(),
                PropertiesConstants.ACTUALITZAR_PROCEDIMENTS_CRON,
                ACTUALIZAR_PROCEDIM_DEFCRON);

        // 2. Refrescar notificacions expirades
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "refrescarNotificacionsExpirades",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().refrescarNotificacionsExpirades(),
                PropertiesConstants.REFRESCAR_NOTIFICACIONS_EXPIRADES_CRON,
                REFRESCAR_NOT_EXPIR_DEFCRON);

        // 3. Callback de client
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "processarPendents",
                callbackServiceSupplier,
                (Supplier<CallbackService> s) -> s.get().processarPendents(),
                PropertiesConstants.PROCESSAR_PENDENTS_RATE,
                300000L,
                CALLBACK_CLIENT,
                PropertiesConstants.PROCESSAR_PENDENTS_INITIAL_DELAY,
                300000L);

        // 4. Consulta certificació notificacions DEH finalitzades
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "enviamentRefrescarEstatDEH",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().enviamentRefrescarEstatDEH(),
                PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_RATE,
                300000L,
                CERT_DEH,
                PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY,
                360000L);

        // 5. Consulta certificació notificacions CIE finalitzades
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "enviamentRefrescarEstatCIE",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().enviamentRefrescarEstatCIE(),
                PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_RATE,
                300000L,
                CERT_CIE,
                PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY,
                420000L);

        // 6. Eliminiar arxius temporals
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "eliminarDocumentsTemporals",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().eliminarDocumentsTemporals(),
                86400000L,
                ARXIUS_TMP,
                calcularDelay());

        // 7. Actualització dels serveis a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "actualitzarServeis",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().actualitzarServeis(),
                PropertiesConstants.ACTUALITZAR_SERVEIS_CRON,
                ACTUALITZAR_SERVEIS_DEFCRON);

        // 8. Consulta de canvis en l'organigrama
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "consultaCanvisOrganigrama",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().consultaCanvisOrganigrama(),
                PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA,
                CONS_CANVIS_ORGANIG_DEFCRON);

        // 9. Eliminar entrades al monitor integracions antigues
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "monitorIntegracionsEliminarAntics",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().monitorIntegracionsEliminarAntics(),
                PropertiesConstants.MONITOR_INTEGRACIONS_ELIMINAR_PERIODE_EXECUCIO,
                MONITOR_BUIDA_DADES_DEFCRON);

        // 10. Actualitzar estat organs enviament table
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "actualitzarEstatOrgansEnviamentTable",
                schedulledServiceSupplier,
                (Supplier<SchedulledService> s) -> s.get().actualitzarEstatOrgansEnviamentTable(),
                PropertiesConstants.ACTUALITZAR_ESTAT_ORGANS,
                ACTUAL_ESTAT_ORGANS_DEFCRON);

    }

    private <T> void registerCronTask(String taskName,
                                      Supplier<T> supplier,
                                      Consumer<Supplier<T>> method,
                                      String cronConfig,
                                      String defualtCron) {
        monitorTasquesService.addTasca(taskName);
        taskRegistrar.addTriggerTask(
                () -> executeSchedulledMethod(supplier, method, taskName),
                triggerContext -> {
                    // Creating a trigger here
                    CronTrigger trigger = new CronTrigger(configHelper.getConfig(cronConfig, defualtCron));
                    // Compute the next execution time
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(taskName, millis);
                    return nextExecution;
                });
        monitorTasquesService.addTasca(taskName);
    }

    private <T> void registerPeriodicTask(String taskName,
                                          Supplier<T> supplier,
                                          Consumer<Supplier<T>> method,
                                          String periodeConfig,
                                          Long defualtPeriode,
                                          Integer operation,
                                          String delayConfig,
                                          Long defaultDelay) {
        monitorTasquesService.addTasca(taskName);
        taskRegistrar.addTriggerTask(
                () -> executeSchedulledMethod(supplier, method, taskName),
                triggerContext -> {
                    // Creating a trigger here
                    PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(periodeConfig, defualtPeriode), TimeUnit.MILLISECONDS);
                    trigger.setFixedRate(true);
                    // Compute initial delay (only first time)
                    Long processarPendentsInitialDelayLong = 0L;
                    if (primeraVez[operation]) {
                        processarPendentsInitialDelayLong = configHelper.getConfigAsLong(delayConfig, defaultDelay);
                        primeraVez[operation] = false;
                    }
                    trigger.setInitialDelay(processarPendentsInitialDelayLong);
                    // Compute the next execution time
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(taskName, millis);
                    return nextExecution;
                });
        monitorTasquesService.addTasca(taskName);
    }

    private <T> void registerPeriodicTask(String taskName,
                                          Supplier<T> supplier,
                                          Consumer<Supplier<T>> method,
                                          Long periode,
                                          Integer opeation,
                                          Long delay) {
        monitorTasquesService.addTasca(taskName);
        taskRegistrar.addTriggerTask(
                () -> executeSchedulledMethod(supplier, method, taskName),
                triggerContext -> {
                    // Creating a trigger here
                    PeriodicTrigger trigger = new PeriodicTrigger(periode, TimeUnit.MILLISECONDS);
                    trigger.setFixedRate(true);
                    // Compute initial delay (only first time)
                    if (primeraVez[opeation]) {
                        trigger.setInitialDelay(delay);
                        primeraVez[opeation] = false;
                    }
                    // Compute the next execution time
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long millis = nextExecution.getTime() - System.currentTimeMillis();
                    monitorTasquesService.updateProperaExecucio(taskName, millis);
                    return nextExecution;
                });
        monitorTasquesService.addTasca(taskName);
    }

    private <T> void executeSchedulledMethod(
            Supplier<T> supplier,
            Consumer<Supplier<T>> method,
            String methodToExecute){
        try {
//            Supplier<SchedulledService> schedulledServiceSupplier = () -> schedulledService;
            log.info("[SCH] Iniciant tansca en segon pla: " + methodToExecute);
            monitorTasquesService.inici(methodToExecute);
            method.accept(supplier);
            monitorTasquesService.fi(methodToExecute);
        } catch(Exception e) {
            monitorTasquesService.error(methodToExecute);
        }
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
