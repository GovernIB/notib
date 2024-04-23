package es.caib.notib.persist.entity.statemachine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="state_machine")
//@EntityListeners(AuditingEntityListener.class)
public class StateMachineEntity {

    @Id
    private String machine_id;

    @Setter
    @Column(name = "state", length = 255)
    private String state;

}