package es.caib.notib.persist.repository.statemachine;

import es.caib.notib.persist.entity.statemachine.StateMachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StateMachineRepository extends JpaRepository<StateMachineEntity, Long> {

    @Query("from StateMachineEntity sm where sm.machine_id = :machineId")
    Optional<StateMachineEntity> findByMachineId(@Param("machineId") String machineId);


    @Query("select sm.state from StateMachineEntity sm where sm.machine_id = :machineId")
    String findEstatByMachineId(@Param("machineId") String machineId);

}
