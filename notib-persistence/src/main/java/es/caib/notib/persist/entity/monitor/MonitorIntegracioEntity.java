package es.caib.notib.persist.entity.monitor;


import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="not_mon_int")
@EntityListeners(AuditingEntityListener.class)
public class MonitorIntegracioEntity extends AbstractPersistable<Long> {

    @Column(name = "codi", length = 64, nullable = false, unique = true)
    private String codi;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data", nullable = false)
    private Date data;

    @Column(name = "descripcio", length = 1024)
    private String descripcio;

    @Column(name = "tipus", nullable = false)
    @Enumerated(EnumType.STRING)
    protected IntegracioAccioTipusEnumDto tipus;

    @Column(name = "aplicacio", length = 64)
    private String aplicacio;

    @Column(name = "temps_resposta")
    private Long tempsResposta;

    @Column(name = "estat")
    @Enumerated(EnumType.STRING)
    private IntegracioAccioEstatEnumDto estat = IntegracioAccioEstatEnumDto.OK;

//    @Column(name = "codi_usuari", length = 64, nullable = false)
    @Column(name = "codi_usuari", length = 64)
    private String codiUsuari;

    @Column(name = "codi_entitat", length = 64)
    private String codiEntitat;

    @Column(name = "error_descripcio", length = 1024)
    private String errorDescripcio;

    @Column(name = "excepcio_msg", length = 1024)
    private String excepcioMessage;

    @Column(name = "excepcio_stacktrace", length = 2048)
    private String excepcioStacktrace;

    @OneToMany(mappedBy = "monitorIntegracio", fetch = FetchType.LAZY, orphanRemoval = true, cascade={CascadeType.ALL})
    private List<MonitorIntegracioParamEntity> parametres = new ArrayList<>();

}
