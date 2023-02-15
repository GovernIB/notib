package es.caib.notib.logic.config;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.intf.service.MonitorTasquesService;
import es.caib.notib.logic.intf.service.SchedulledService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.PropertiesConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
    }

    private void registerSchedulledTasks() {
        // 1. Enviament de notificacions pendents al registre y notific@
        ////////////////////////////////////////////////////////////////
        final String registrarEnviamentsPendents = "registrarEnviamentsPendents";
        monitorTasquesService.addTasca(registrarEnviamentsPendents);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(registrarEnviamentsPendents);
                            schedulledService.registrarEnviamentsPendents();
                            monitorTasquesService.fi(registrarEnviamentsPendents);
                        } catch(Exception e) {
                            monitorTasquesService.error(registrarEnviamentsPendents);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.REGISTRAR_ENVIAMENTS_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        var registrarEnviamentsPendentsInitialDelayLong = 0L;
                        if (primeraVez[0]) {
                        	registrarEnviamentsPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.REGISTRAR_ENVIAMENTS_PENDENTS_INITIAL_DELAY, 300000L);
                        	primeraVez[0] = false;
                        }
                        trigger.setInitialDelay(registrarEnviamentsPendentsInitialDelayLong);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(registrarEnviamentsPendents, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(registrarEnviamentsPendents);

        // 2. Enviament de notificacions registrades a Notific@
        ///////////////////////////////////////////////////////
        final String notificaEnviamentsRegistrats = "notificaEnviamentsRegistrats";
        monitorTasquesService.addTasca(notificaEnviamentsRegistrats);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(notificaEnviamentsRegistrats);
                            schedulledService.notificaEnviamentsRegistrats();
                            monitorTasquesService.fi(notificaEnviamentsRegistrats);
                        } catch(Exception e) {
                            monitorTasquesService.error(notificaEnviamentsRegistrats);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.NOTIFICA_ENVIAMENTS_REGISTRATS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        var notificaEnviamentsRegistratsInitialDelayLong = 0L;
                        if (primeraVez[1]) {
                        	notificaEnviamentsRegistratsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.NOTIFICA_ENVIAMENTS_REGISTRATS_INITIAL_DELAY, 330000L);
                        	primeraVez[1] = false;
                        }
                        trigger.setInitialDelay(notificaEnviamentsRegistratsInitialDelayLong);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(notificaEnviamentsRegistrats, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(notificaEnviamentsRegistrats);

        // 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
        //////////////////////////////////////////////////////////////////
        final String enviamentRefrescarEstatPendents = "enviamentRefrescarEstatPendents";
        monitorTasquesService.addTasca(enviamentRefrescarEstatPendents);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(enviamentRefrescarEstatPendents);
                            schedulledService.enviamentRefrescarEstatPendents();
                            monitorTasquesService.fi(enviamentRefrescarEstatPendents);
                        } catch(Exception e) {
                            monitorTasquesService.error(enviamentRefrescarEstatPendents);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        var enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[2]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_INITIAL_DELAY, 360000L);
                        	primeraVez[2] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelayLong);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(enviamentRefrescarEstatPendents, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(enviamentRefrescarEstatPendents);

        // 4. Actualització de l'estat dels enviaments amb l'estat de enviat_sir
        //////////////////////////////////////////////////////////////////
        final String enviamentRefrescarEstatEnviatSir = "enviamentRefrescarEstatEnviatSir";
        monitorTasquesService.addTasca(enviamentRefrescarEstatEnviatSir);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(enviamentRefrescarEstatEnviatSir);
                            schedulledService.enviamentRefrescarEstatEnviatSir();
                            monitorTasquesService.fi(enviamentRefrescarEstatEnviatSir);
                        } catch(Exception e) {
                            monitorTasquesService.error(enviamentRefrescarEstatEnviatSir);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        var enviamentRefrescarEstatEnviatSirInitialDelayLong = 0L;
                        if (primeraVez[3]) {
                        	enviamentRefrescarEstatEnviatSirInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_INITIAL_DELAY, 390000L);
                        	primeraVez[3] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatEnviatSirInitialDelayLong);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(enviamentRefrescarEstatEnviatSir, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(enviamentRefrescarEstatEnviatSir);

        // 5. Actualització dels procediments a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        final String actualitzarProcediments = "actualitzarProcediments";
        monitorTasquesService.addTasca(actualitzarProcediments);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(actualitzarProcediments);
                            schedulledService.actualitzarProcediments();
                            monitorTasquesService.fi(actualitzarProcediments);
                        } catch(Exception e) {
                            monitorTasquesService.error(actualitzarProcediments);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ACTUALITZAR_PROCEDIMENTS_CRON, "0 0 1 * * *"));
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(actualitzarProcediments, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(actualitzarProcediments);

        // 6. Refrescar notificacions expirades
        /////////////////////////////////////////////////////////////////////////
        final String refrescarNotificacionsExpirades = "refrescarNotificacionsExpirades";
        monitorTasquesService.addTasca(refrescarNotificacionsExpirades);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(refrescarNotificacionsExpirades);
                            schedulledService.refrescarNotificacionsExpirades();
                            monitorTasquesService.fi(refrescarNotificacionsExpirades);
                        } catch(Exception e) {
                            monitorTasquesService.error(refrescarNotificacionsExpirades);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.REFRESCAR_NOTIFICACIONS_EXPIRADES_CRON, "0 30 1 * * *"));
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(refrescarNotificacionsExpirades, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(refrescarNotificacionsExpirades);

        // 7. Callback de client
        /////////////////////////////////////////////////////////////////////////
        final String processarPendents = "processarPendents";
        monitorTasquesService.addTasca(processarPendents);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(processarPendents);
                            callbackService.processarPendents();
                            monitorTasquesService.fi(processarPendents);
                        } catch(Exception e) {
                            monitorTasquesService.error(processarPendents);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.PROCESSAR_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        var processarPendentsInitialDelayLong = 0L;
                        if (primeraVez[4]) {
                        	processarPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.PROCESSAR_PENDENTS_INITIAL_DELAY, 420000L);
                        	primeraVez[4] = false;
                        }
                        trigger.setInitialDelay(processarPendentsInitialDelayLong);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(processarPendents, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(processarPendents);

        // 8. Consulta certificació notificacions DEH finalitzades
        /////////////////////////////////////////////////////////////////////////
        final String enviamentRefrescarEstatDEH = "enviamentRefrescarEstatDEH";
        monitorTasquesService.addTasca(enviamentRefrescarEstatDEH);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(enviamentRefrescarEstatDEH);
                            schedulledService.enviamentRefrescarEstatDEH();
                            monitorTasquesService.fi(enviamentRefrescarEstatDEH);
                        } catch(Exception e) {
                            monitorTasquesService.error(enviamentRefrescarEstatDEH);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        var enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                        if (primeraVez[5]) {
                        	enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY, 450000L);
                        	primeraVez[5] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(enviamentRefrescarEstatDEH, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(enviamentRefrescarEstatDEH);

        // 9. Consulta certificació notificacions CIE finalitzades
        /////////////////////////////////////////////////////////////////////////
        final String enviamentRefrescarEstatCIE = "enviamentRefrescarEstatCIE";
        monitorTasquesService.addTasca(enviamentRefrescarEstatCIE);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(enviamentRefrescarEstatCIE);
                            schedulledService.enviamentRefrescarEstatCIE();
                            monitorTasquesService.fi(enviamentRefrescarEstatCIE);
                        } catch(Exception e) {
                            monitorTasquesService.error(enviamentRefrescarEstatCIE);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_RATE, 300000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        var enviamentRefrescarCertPendentsInitialDelayLong = 0L;
                        if (primeraVez[6]) {
                        	enviamentRefrescarCertPendentsInitialDelayLong = configHelper.getConfigAsLong(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_INITIAL_DELAY, 480000L);
                        	primeraVez[6] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarCertPendentsInitialDelayLong);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(enviamentRefrescarEstatCIE, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(enviamentRefrescarEstatCIE);

        // 10. Eliminiar arxius temporals
        /////////////////////////////////////////////////////////////////////////
        final String eliminarDocumentsTemporals = "eliminarDocumentsTemporals";
        monitorTasquesService.addTasca(eliminarDocumentsTemporals);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(eliminarDocumentsTemporals);
                            schedulledService.eliminarDocumentsTemporals();
                            monitorTasquesService.fi(eliminarDocumentsTemporals);
                        } catch(Exception e) {
                            monitorTasquesService.error(eliminarDocumentsTemporals);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new PeriodicTrigger(86400000l, TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        trigger.setInitialDelay(calcularDelay());
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(eliminarDocumentsTemporals, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(eliminarDocumentsTemporals);

        // 11. Actualització dels serveis a partir de la informació de Rolsac
        /////////////////////////////////////////////////////////////////////////
        final String actualitzarServeis = "actualitzarServeis";
        monitorTasquesService.addTasca(actualitzarServeis);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(actualitzarServeis);
                            schedulledService.actualitzarServeis();
                            monitorTasquesService.fi(actualitzarServeis);
                        } catch(Exception e) {
                            monitorTasquesService.error(actualitzarServeis);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ACTUALITZAR_SERVEIS_CRON, "0 0 2 * * *"));
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(actualitzarServeis, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(actualitzarServeis);

        // 12. Consulta de canvis en l'organigrama
        /////////////////////////////////////////////////////////////////////////
        final String consultaCanvisOrganigrama = "consultaCanvisOrganigrama";
        monitorTasquesService.addTasca(consultaCanvisOrganigrama);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(consultaCanvisOrganigrama);
                            schedulledService.consultaCanvisOrganigrama();
                            monitorTasquesService.fi(consultaCanvisOrganigrama);
                        } catch(Exception e) {
                            monitorTasquesService.error(consultaCanvisOrganigrama);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var cron = configHelper.getConfig(PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA, "0 30 2 * * *");
                        var trigger = new CronTrigger(cron);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(consultaCanvisOrganigrama, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(consultaCanvisOrganigrama);

        // 13. Eliminar entrades al monitor integracions antigues
        /////////////////////////////////////////////////////////////////////////
        final String monitorIntegracionsEliminarAntics = "monitorIntegracionsEliminarAntics";
        monitorTasquesService.addTasca(monitorIntegracionsEliminarAntics);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(monitorIntegracionsEliminarAntics);
                            schedulledService.monitorIntegracionsEliminarAntics();
                            monitorTasquesService.fi(monitorIntegracionsEliminarAntics);
                        } catch(Exception e) {
                            monitorTasquesService.error(monitorIntegracionsEliminarAntics);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {

                        var dies = configHelper.getConfig(PropertiesConstants.MONITOR_INTEGRACIONS_ELIMINAR_PERIODE_EXECUCIO);
                        if (Strings.isNullOrEmpty(dies)) {
                            dies = "0 30 1 * * *";
                        }
                        var trigger = new CronTrigger(dies);
                        var nextExecution = trigger.nextExecutionTime(triggerContext);
                        var millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(monitorIntegracionsEliminarAntics, millis);
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(monitorIntegracionsEliminarAntics);


        // 12. Actualitzar l'estat dels òrgans a not_notificacio_env_table
        /////////////////////////////////////////////////////////////////////////
        final String actualitzarEstatOrgansEnviamentTable = "actualitzarEstatOrgansEnviamentTable";
        monitorTasquesService.addTasca(consultaCanvisOrganigrama);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        try {
                            monitorTasquesService.inici(actualitzarEstatOrgansEnviamentTable);
                            schedulledService.actualitzarEstatOrgansEnviamentTable();
                            monitorTasquesService.fi(actualitzarEstatOrgansEnviamentTable);
                        } catch(Exception e) {
                            monitorTasquesService.error(actualitzarEstatOrgansEnviamentTable);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
//                        String cron = configHelper.getConfig(PropertiesConstants.actualitzarEstatOrgansEnviamentTable);
//                        if (cron == null) {
                        String cron = "0 30 3 * * *";
//                            String cron = "* * * * * *";
//                        }
                        CronTrigger trigger = new CronTrigger(cron);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long millis = nextExecution.getTime() - System.currentTimeMillis();
                        monitorTasquesService.updateProperaExecucio(actualitzarEstatOrgansEnviamentTable, millis);
                        return nextExecution;
                    }
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
