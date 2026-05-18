package es.caib.notib.persist.entity;


import es.caib.notib.persist.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Slf4j
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="not_correus_agrupats")
public class CorreusAgrupatsEntity extends NotibAuditable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENVIAMENT_ID")
    @ForeignKey(name = "CORREUS_AGRUPATS_ENV_ID_FK")
    private NotificacioEnviamentEntity enviament;

}
