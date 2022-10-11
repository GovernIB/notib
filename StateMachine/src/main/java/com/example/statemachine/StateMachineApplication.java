package com.example.statemachine;

import com.example.statemachine.enums.EstatsComSIR;
import com.example.statemachine.enums.EventsComSIR;
import com.example.statemachine.repositoris.MongoStateMachineRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.statemachine.data.mongodb.MongoDbRepositoryStateMachine;
import org.springframework.statemachine.service.StateMachineService;

@Slf4j
@SpringBootApplication
public class StateMachineApplication implements CommandLineRunner {

//    @Autowired
//    private StateMachine<EstatsComSIR, EventsComSIR> stateMachine;

    @Autowired
    private MongoTemplate mt;

    @Autowired
    MongoStateMachineRepository repo;

    @Autowired
    private StateMachineService<EstatsComSIR, EventsComSIR> stateMachineService;

    public static void main(String[] args) {
        SpringApplication.run(StateMachineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        var foo = stateMachineService.acquireStateMachine("foo");
        var bar = "bar";
        foo.sendEvent(EventsComSIR.ALTA);
        stateMachineService.acquireStateMachine(foo.getId());
//        repo.save(new MongoDbRepositoryStateMachine());
//        var ping = new BsonString("ping");
//        try {
//            var answer = mt.getDb();
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }

//        var foos = foo.findAll();
//        for (var f : foos) {
//            System.out.println(f.getId());
//        }
//        stateMachine.start();
//        stateMachine.sendEvent(EventsComSIR.ALTA);
//        log.info(stateMachine.getState().toString());
//        stateMachine.sendEvent(EventsComSIR.REGISTRAR);
//        do  {
//            stateMachine.sendEvent(EventsComSIR.REINTENTAR_REGISTRE);
//        } while (EstatsComSIR.ERROR_REGISTRE.equals(stateMachine.getState().getId()));
//        log.info(stateMachine.getState().toString());
//        stateMachine.sendEvent(EventsComSIR.CONSULTA_ESTAT_SIR);
//        log.info(stateMachine.getState().toString());
//        do  {
//            stateMachine.sendEvent(EventsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR);
//            log.info(stateMachine.getState().toString());
//        } while (EstatsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR.equals(stateMachine.getState().getId()));
//        stateMachine.sendEvent(EventsComSIR.CHECK_ESTAT_SIR);
//        log.info("************************** CONSULTA ESTAT SIR ********************");
//        if (!EstatsComSIR.REENVIAR_CONSULTA_ESTAT_SIR.equals(stateMachine.getState().getId())) {
//            log.info("Resultat -> " + stateMachine.getState().getId().toString());
//        }
//        stateMachine.sendEvent(EventsComSIR.REENVIAR_CONSULTA_ESTAT_SIR);
//        do  {
//            stateMachine.sendEvent(EventsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR);
//            log.info(stateMachine.getState().toString());
//        } while (EstatsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR.equals(stateMachine.getState().getId()));
//        stateMachine.sendEvent(EventsComSIR.CHECK_ESTAT_SIR);
    }
}
